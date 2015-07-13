package com.pb.sawdust.util.exceptions;

import com.pb.sawdust.util.Range;

import java.util.List;
import java.util.ArrayList;
import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * The {@code MultiThrowable} class provides a framework where multiple {@code Throwable} instance can be wrapped into
 * one. This is useful if a given operation (or series of operations) need to be executed as a block and throwables
 * captured, rather than thrown, during the block call. When the block operation finishes, the single multi-throwable,
 * which encapsulates all caught throwables, can be thrown (or dealt with).
 *
 * @author crf <br/>
 *         Started: Dec 23, 2008 11:11:45 AM
 */
public class MultiThrowable extends Throwable {
    private static final long serialVersionUID = 3240446504578001150L;

    private final List<Throwable> throwables;
    private final MultiThrowable cause;
    private String throwableName = "throwable";
    private String multiThrowableName = "MultiThrowable";

    /**
     * Constructor specifying the throwables contained in the multi-throwable.
     *
     * @param t1
     *        The first throwable to wrap.
     *
     * @param additionalThrowables
     *        Any additional throwables that are to be wrapped.
     */
    public MultiThrowable(Throwable t1, Throwable ... additionalThrowables) {
        throwables = new ArrayList<Throwable>();
        throwables.add(t1);
        Throwable[] additionalCauses = new Throwable[additionalThrowables.length];
        for (int i : Range.range(additionalThrowables.length)) {
            Throwable t = additionalThrowables[i];
            throwables.add(t);
            additionalCauses[i] = t == null ? null : t.getCause();
        }
        cause = new MultiThrowable(t1 == null ? null : t1.getCause(),additionalCauses);
    }

    /**
     * Set the name for the throwables held by this instance. This is used in the {@code getMessage()} method. The default
     * is "throwable." Another common name to use could be "exception."
     *
     * @param throwableName
     *        The throwable name to be used with this multi-throwable.
     */
    protected void setThrowableName(String throwableName) {
        this.throwableName = throwableName;
    }

    /**
     * Set the name for this multi-throwable. This is used in the {@code getMessage()} method. The default value is
     * "MultiThrowable."
     *
     * @param multiThrowableName
     *        The name to use for this multi-throwable.
     */
    protected void setMultiThrowableName(String multiThrowableName) {
        this.multiThrowableName = multiThrowableName;
    }

    public MultiThrowable getCause() {
        return cause;
    }

    private String getMessageHead() {
        return multiThrowableName + " with " + throwables.size() + " " + throwableName + "s:";
    }

    public String getMessage() {
        StringBuffer b = new StringBuffer();
        b.append(getMessageHead());
        for (Throwable t : throwables)
            b.append("\n\t").append(t.getMessage());
        return b.toString();
    }

    public String toString() {
        StringBuffer b = new StringBuffer();
        b.append(getMessageHead());
        for (Throwable t : throwables)
            b.append("\n\t").append(t.toString());
        return b.toString();
    }

    private String createStackTraceHeader(int throwableNumber) {
        return new StringBuilder().append("****Stack trace for ").append(throwableName).append(throwableNumber).append("****\n").toString();
    }

    public void printStackTrace(PrintStream s) {
        synchronized (s) {
            int counter = 1;
            for (Throwable t : throwables) {
                s.println(createStackTraceHeader(counter++));
                t.printStackTrace(s);
            }
        }
    }

    public void printStackTrace(PrintWriter s) {
        synchronized (s) {
            int counter = 1;
            for (Throwable t : throwables) {
                s.println(createStackTraceHeader(counter++));
                t.printStackTrace(s);
            }
        }
    }
}