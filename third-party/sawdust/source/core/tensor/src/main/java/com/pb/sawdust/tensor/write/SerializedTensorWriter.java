package com.pb.sawdust.tensor.write;

import com.pb.sawdust.io.RuntimeCloseable;
import com.pb.sawdust.util.abacus.Abacus;
import com.pb.sawdust.util.exceptions.RuntimeIOException;
import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.TensorUtil;
import com.pb.sawdust.tensor.decorators.primitive.*;
import com.pb.sawdust.tensor.index.IdlessIndex;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.util.JavaType;
import com.pb.sawdust.util.exceptions.RuntimeInterruptedException;

import java.io.*;
import java.util.Iterator;
import java.util.List;

/**
 * The {@code SerializedTensorWriter} provides a very simple {@code TensorWriter} implementation which will write a tensor
 * as a serialized object.  The serialization methodology is custom and internally specified, avoiding the need to have
 * tensor implement {@code Serializable} directly.  Because the serialization scheme is generic, implementation details
 * of the serialized tensor (such as if it was a derivative tensor built from reference indices) will often be lost. It
 * will, however, include type and id structures, if they are available.
 * <p>
 * This class also provides a static method through which the serialization scheme may be accessed from outside of the
 * {@code TensorReader} framework ({@code SerializableTensor} uses this class' serialization specification in this manner).
 * <p>
 * To read the tensor(s) written with this class, use the {@code SerializedTensorReader} class.
 *
 * @author crf <br/>
 *         Started Feb 8, 2010 2:03:38 PM
 *
 * @see com.pb.sawdust.tensor.SerializableTensor
 * @see com.pb.sawdust.tensor.read.SerializedTensorReader
 */
public class SerializedTensorWriter<T> implements TensorWriter<T>, RuntimeCloseable {
    private final ObjectOutput os;
    private int delay;
    private int delayFrequency;

    /**
     * Constructor specifying an output stream.
     *
     * @param os
     *        The outputstream to use for this writer.
     */
    public SerializedTensorWriter(OutputStream os) {
        try {
            this.os = new ObjectOutputStream(os);
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    /**
     * Constructor specifying a file to write to. If the file already exists, it will be overwritten.
     *
     * @param file
     *        The file to.
     */
    public SerializedTensorWriter(File file) {
        this(getFileOutputStream(file));
    }

    /**
     * Constructor specifying a file to write to. If the file already exists, it will be overwritten.
     *
     * @param file
     *        The file to.
     */
    public SerializedTensorWriter(String file) {
        this(getFileOutputStream(new File(file)));
    }

    private static final OutputStream getFileOutputStream(File file) {
        try {
            return new BufferedOutputStream(new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            throw new RuntimeIOException(e);
        }
    }

    /**
     * Sets a data transmission delay for this writer. In some cases, the writer can flood a socket with data which the
     * reader at the other end cannot keep up with, leading to a hung program (unknown why this behavior is seen). To
     * remedy this, a delay can be set up in the data transmission; basically, after {@code delayFrequency} tensor elements
     * have been sent, the thread sending the data will pause (via {@code Thread.sleep}) for {@code delayInMillis} milliseconds.
     * This may need to be tuned to the specifics of the platform/machine that is using this class.
     *
     * @param delayInMillis
     *        The actual delay in milliseconds.
     *
     * @param delayFrequency
     *        The number of tensor elements that will be sent in between delays.
     */
    public void setWriteDelay(int delayInMillis, int delayFrequency) {
        this.delay = delayInMillis;
        this.delayFrequency = delayFrequency;
    }


    @Override
    public void writeTensor(Tensor<? extends T> tensor)  {
        serializeTensor(tensor,os);
    }

    /**
     * Close the output stream this writer writes to.  This method should generally be called when the writer is no longer
     * in use, to free up resources.
     */
    @Override
    public void close() {
        try {
            os.close();
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    //provided so that other classes can access method


    /**
     * Convenience method to serialize a tensor to an object output. No data sending delay will be used.
     *
     * @param tensor
     *        The tensor to serialize.
     *
     * @param oo
     *        The object output to serialize the tensor to.
     *
     * @param <T>
     *        The type held by the tensor.
     */
    public static <T> void serializeTensor(Tensor<T> tensor, ObjectOutput oo) {
        serializeTensor(tensor,oo,0,0);
    }

    /**
     * Convenience method to serialize a tensor to an object output, including a data delay. For information about the
     * data delay, see {@link #setWriteDelay(int,int)}.
     *
     * @param tensor
     *        The tensor to serialize.
     *
     * @param oo
     *        The object output to serialize the tensor to.
     *
     * @param delayInMillis
     *        The actual data delay in milliseconds.
     *
     * @param delayFrequency
     *        The number of tensor elements that will be sent in between delays.
     *
     * @param <T>
     *        The type held by the tensor.
     */
    public static <T> void serializeTensor(Tensor<T> tensor, ObjectOutput oo, int delayInMillis, int delayFrequency) {
        try {
            //write size
            oo.writeObject(tensor.getDimensions());
            //write type
            JavaType type = tensor.getType();
            oo.writeObject(type);
            Index<?> index = tensor.getIndex();
            boolean hasIds = !(index instanceof IdlessIndex);
            oo.writeBoolean(hasIds);
            if (hasIds)
                for (List<?> ids : index.getIndexIds())
                    for (Object id : ids) 
                        oo.writeObject(id);
            //write data
            switch (type) {
                case BOOLEAN : {
                    Iterator<Boolean> it = TensorUtil.getElementIterator((BooleanTensor) tensor);
                    if (delayInMillis > 0) {
                        int counter = 0;
                        while (it.hasNext()) {
                            try {
                                if (counter++ % delayFrequency == 0)
                                Thread.sleep(delayInMillis);
                            } catch (InterruptedException e) {
                                throw new RuntimeInterruptedException(e);
                            }
                            oo.writeBoolean(it.next());
                        }
                    } else {
                        while (it.hasNext())
                            oo.writeBoolean(it.next());
                    }
                    break;
                }                                 
                case CHAR : {
                    Iterator<Character> it = TensorUtil.getElementIterator((CharTensor) tensor);
                    if (delayInMillis > 0) {
                        int counter = 0;
                        while (it.hasNext()) {
                            try {
                                if (counter++ % delayFrequency == 0)
                                Thread.sleep(delayInMillis);
                            } catch (InterruptedException e) {
                                throw new RuntimeInterruptedException(e);
                            }
                            oo.writeChar(it.next());
                        }
                    } else {
                        while (it.hasNext())
                            oo.writeChar(it.next());
                    }
                    break;
                }
                case BYTE : {
                    Iterator<Byte> it = TensorUtil.getElementIterator((ByteTensor) tensor);
                    if (delayInMillis > 0) {
                        int counter = 0;
                        while (it.hasNext()) {
                            try {
                                if (counter++ % delayFrequency == 0)
                                Thread.sleep(delayInMillis);
                            } catch (InterruptedException e) {
                                throw new RuntimeInterruptedException(e);
                            }
                            oo.writeByte(it.next());
                        }
                    } else {
                        while (it.hasNext())
                            oo.writeByte(it.next());
                    }
                    break;
                }
                case SHORT : {
                    Iterator<Short> it = TensorUtil.getElementIterator((ShortTensor) tensor);
                    if (delayInMillis > 0) {
                        int counter = 0;
                        while (it.hasNext()) {
                            try {
                                if (counter++ % delayFrequency == 0)
                                Thread.sleep(delayInMillis);
                            } catch (InterruptedException e) {
                                throw new RuntimeInterruptedException(e);
                            }
                            oo.writeShort(it.next());
                        }
                    } else {
                        while (it.hasNext())
                            oo.writeShort(it.next());
                    }
                    break;
                }
                case INT : {
                    Iterator<Integer> it = TensorUtil.getElementIterator((IntTensor) tensor);
                    if (delayInMillis > 0) {
                        int counter = 0;
                        while (it.hasNext()) {
                            try {
                                if (counter++ % delayFrequency == 0)
                                Thread.sleep(delayInMillis);
                            } catch (InterruptedException e) {
                                throw new RuntimeInterruptedException(e);
                            }
                            oo.writeInt(it.next());
                        }
                    } else {
                        while (it.hasNext())
                            oo.writeInt(it.next());
                    }
                    break;
                }
                case LONG : {
                    Iterator<Long> it = TensorUtil.getElementIterator((LongTensor) tensor);
                    if (delayInMillis > 0) {
                        int counter = 0;
                        while (it.hasNext()) {
                            try {
                                if (counter++ % delayFrequency == 0)
                                Thread.sleep(delayInMillis);
                            } catch (InterruptedException e) {
                                throw new RuntimeInterruptedException(e);
                            }
                            oo.writeLong(it.next());
                        }
                    } else {
                        while (it.hasNext())
                            oo.writeLong(it.next());
                    }
                    break;
                }
                case FLOAT : {
                    Iterator<Float> it = TensorUtil.getElementIterator((FloatTensor) tensor);
                    if (delayInMillis > 0) {
                        int counter = 0;
                        while (it.hasNext()) {
                            try {
                                if (counter++ % delayFrequency == 0)
                                Thread.sleep(delayInMillis);
                            } catch (InterruptedException e) {
                                throw new RuntimeInterruptedException(e);
                            }
                            oo.writeFloat(it.next());
                        }
                    } else {
                        while (it.hasNext())
                            oo.writeFloat(it.next());
                    }
                    break;
                }
                case DOUBLE : {
                    Iterator<Double> it = TensorUtil.getElementIterator((DoubleTensor) tensor);
                    if (delayInMillis > 0) {
                        int counter = 0;
                        while (it.hasNext()) {
                            try {
                                if (counter++ % delayFrequency == 0)
                                Thread.sleep(delayInMillis);
                            } catch (InterruptedException e) {
                                throw new RuntimeInterruptedException(e);
                            }
                            oo.writeDouble(it.next());
                        }
                    } else {
                        while (it.hasNext())
                            oo.writeDouble(it.next());
                    }
                    break;
                }
                case OBJECT : {
                    Iterator<? extends T> it = TensorUtil.getElementIterator(tensor);
                    if (delayInMillis > 0) {
                        int counter = 0;
                        while (it.hasNext()) {
                            try {
                                if (counter++ % delayFrequency == 0)
                                Thread.sleep(delayInMillis);
                            } catch (InterruptedException e) {
                                throw new RuntimeInterruptedException(e);
                            }
                            oo.writeObject(it.next());
                        }
                    } else {
                        while (it.hasNext())
                            oo.writeObject(it.next());
                    }
                    break;
                }
            }
            oo.writeInt(tensor.metadataSize());
            for(String key : tensor.getMetadataKeys()) {
                oo.writeObject(key);
                oo.writeObject(tensor.getMetadataValue(key));
            }
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }
}