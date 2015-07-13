package com.pb.sawdust.util.concurrent;

import com.pb.sawdust.util.exceptions.RuntimeInterruptedException;
import com.pb.sawdust.util.exceptions.RuntimeWrappingException;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * The {@code DnCRecursiveTask} provides a framework for "divide-and-conquer" {@code RecursiveTask}s. A divide-and-conquer
 * task is recursively split in half as seperate tasks (within a {@code ForkJoinPool}) until some criterion is
 * reached, at which point the sub-tasks are executed and their results combined. The criterion should generally use
 * the static {@code getSurplusQueuedTaskCount()} method inherited from the {@code RecursiveTask} class.
 * <p>
 * In order to work correctly, the two constructors in this class must be used in a specific manner.  The constructor
 * taking two {@code long} arguments is intended for use as the (super) constructor of the instance passed to the
 * {@code ForkJoinPool}. The other constructor should be invoked (through super calls) in the implementation of the
 * {@link #getNextTask(long, long, DnCRecursiveTask)} method.
 * <p>
 * To make this more concrete, the following presents an example implementation for an task which takes an array
 * of {@code double}s and returns the sum of its squares.
 *
 * <pre><code>
 *     public class ArraySquareSum extends DnCRecursiveTask&lt;Double&gt; {
 *         private final double[] array;
 *
 *         public ArraySquareSum(double[] array) {
 *             super(0,array.length);
 *             this.array = array;
 *         }
 *
 *         private ArraySquareSum(double[] array, long start, long length, DnCRecursiveTask&lt;Double&gt; next) {
 *             super(start,length,next);
 *             this.array = array;
 *         }
 *
 *         protected Double computeTask(long start, long length) {
 *             long end = start + length;
 *             double sum = 0.0;
 *             for (long i = start; i < end; i++)
 *                 sum += array[i]*array[i];
 *             return sum;
 *         }
 *
 *         protected DnCRecursiveTask&lt;Double&gt; getNextTask(long start, long length, DnCRecursiveTask&lt;Double&gt; next) {
 *             return new ArraySquareSum(array,start,length,next);
 *         }
 *
 *         protected boolean continueDividing(long newLength) {
 *             return getSurplusQueuedTaskCount() < 3;
 *         }
 *
 *         protected T joinResults(T currentResult, T nextResult) {
 *             return currentResult + nextResult;
 *         }
 *     }
 * </code></pre>
 *
 * @param <T>
 *        The type returned by this task.
 *
 * @author crf <br/>
 *         Started: Aug 11, 2009 8:57:02 AM
 */
public abstract class DnCRecursiveTask<T>  extends RecursiveTask<T> {
    private final long start;
    private long length;
    private final DnCRecursiveTask<T> next;

    /**
     * Constructor for sub-tasks. This should be called for constructing the object returned by
     * {@link #getNextTask(long, long, DnCRecursiveTask)}.
     *
     * @param start
     *        The starting point of the task.
     *
     * @param length
     *        The length of the task.
     *
     * @param next
     *        The next action.
     */
    protected DnCRecursiveTask(long start, long length, DnCRecursiveTask<T> next) {
        this.start = start;
        this.length = length;
        this.next = next;
    }

    /**
     * Constructor for the main task passed to the {@code ForkJoinPool}.
     *
     * @param start
     *        The starting point of the action.
     *
     * @param length
     *        The length of the action.
     */
    protected DnCRecursiveTask(long start, long length) {
        this(start,length,null);
    }

    /**
     * Compute the task. This method should perform whatever tasks are needed for the points on <code>[start,start+length)</code>.
     *
     * @param start
     *        The starting point of the action.
     *
     * @param length
     *        The length of the action.
     *
     * @return the result of the task.
     */
    abstract protected T computeTask(long start, long length);

    /**
     * Get the next (sub-)task resulting from a division. This method should return an instance of the subclass which
     * uses the {@link #DnCRecursiveTask(long, long, DnCRecursiveTask)} constructor as its {@code super}.
     *
     * @param start
     *        The starting point of the action.
     *
     * @param length
     *        The length of the action.
     *
     * @param next
     *        The next action.
     *
     * @return the next sub-task.
     */
    abstract protected DnCRecursiveTask<T> getNextTask(long start, long length, DnCRecursiveTask<T> next);

    /**
     * Determine whether the task should continue to divide or, instead, perform its computations. This method should,
     * at least, make use of the static {@code getSurplusQueuedTaskCount()} method inherited from the {@code RecursiveTask} class,
     * the result of which should generally be kept above 3 to maintain high concurrency.
     *
     * @param length
     *        The length of the action.
     *
     * @return {@code true} if it should continue dividing, {@code false} if it should perform its computations.
     */
    abstract protected boolean continueDividing(long length);

    /**
     * Join the results of two sub-tasks.
     *
     * @param result1
     *        The result of one task.
     *
     * @param result2
     *        The result of the other task.
     *
     * @return the result of joining {@code result1} and {@code result2}.
     */
    abstract protected T joinResults(T result1, T result2);

    protected T compute() {
        long newLength = length;
        DnCRecursiveTask<T> b = null;
        while (continueDividing(newLength)) {
            newLength = newLength >>> 1; //divide by two
            if (newLength == length) //newLength < 2
                break;
            b = getNextTask(start+newLength,length-newLength,b);
            b.fork();
            length = newLength;
        }
        T result = null;
        try {
            result = computeTask(start,length);
        } catch (Exception | Error e) {
            completeExceptionally(e);
        }
        while (b != null) {
            if (b.tryUnfork())
                result = joinResults(result,b.compute());
            else
                result = joinResults(result,b.join());
            b = b.next;
        }
        return result;
    }

    /**
     * This method is the same as {@code get()}, except it wraps the declared exceptions in runtime exceptions.
     *
     * @return the result of {@code get()}.
     *
     * @throws RuntimeInterruptedException in place of {@code InterruptedException}.
     * @throws RuntimeWrappingException wrapping a thrown {@code ExecutionException}.
     */
    public T getResult() {
        try {
            return get();
        } catch (InterruptedException e) {
            throw new RuntimeInterruptedException(e);
        } catch (ExecutionException e) {
            throw new RuntimeWrappingException(e);
        }
    }

    /**
     * This method is the same as {@code get(long,TimeUnit)}, except it wraps the declared exceptions in runtime exceptions.
     *
     * @param timeout
     *        The length of time to wait for the method to return.
     *
     * @param timeUnit
     *        The units for {@code timeout}.
     *
     * @return the result of {@code get(timeout,timeUnit)}.
     *
     * @throws RuntimeInterruptedException in place of {@code InterruptedException}.
     * @throws RuntimeWrappingException wrapping a thrown {@code ExecutionException} or {@code TimeoutException}}.
     */
    public T getResult(long timeout, TimeUnit timeUnit) {
        try {
            return get(timeout,timeUnit);
        } catch (InterruptedException e) {
            throw new RuntimeInterruptedException(e);
        } catch (ExecutionException | TimeoutException e) {
            throw new RuntimeWrappingException(e);
        }
    }
}