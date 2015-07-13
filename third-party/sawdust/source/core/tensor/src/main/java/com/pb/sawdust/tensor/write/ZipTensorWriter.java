package com.pb.sawdust.tensor.write;

import com.pb.sawdust.io.ZipFile;
import com.pb.sawdust.tensor.ArrayTensor;
import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.TensorUtil;
import com.pb.sawdust.tensor.decorators.primitive.*;
import com.pb.sawdust.tensor.group.TensorGroup;
import com.pb.sawdust.tensor.index.IdlessIndex;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.index.IndexUtil;
import com.pb.sawdust.tensor.index.ScalarIndex;
import com.pb.sawdust.tensor.read.ZipTensorReader;
import com.pb.sawdust.util.ContainsMetadata;
import com.pb.sawdust.util.JavaType;
import com.pb.sawdust.util.abacus.IterableAbacus;

import static com.pb.sawdust.util.Range.*;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

/**
 * The {@code ZipTensorWriter} class writes tensors, indices, and tensor groups to the zip tensor format.  The format is
 * described in detail below, but basically it encodes the data in a series of files which are compresed in the zip
 * file format.  All tensors, indices, and tensor groups in a given zip tensor must have the same dimensionality (index
 * dimensionality refers to the base dimension), but beyond that there are no restrictions on the data that can be stored.
 * <p>
 * The {@code write} methods specified in the implemented interfaces in this class only queue the data for writing, but
 * do not actually write it to disk. This is done to avoid the ineffiencies associated with constantly rewriting zip
 * file.  To write the queued data to disk, the {@link #writeZipTensor()} is called.
 * <p>
 * Currently, the zip tensor format does not support collapsing indices. //todo: this
 * <p>
 * This zip tensor writer is sensitive to use of overly broad index ids and tensor group tensor/index keys.  Specifically,
 * if any of these (in a given index dimension or tensor group) includes a mixture of primitive object equivalents
 * ({@code Character}, {@code Boolean}, {@code Integer},<i>etc.</i>), {@code String}s, and/or {@code Objects}, then these
 * will be serialized rather than written out as primitive data. 
 * <p>
 * As stated below, any {@code Object} data which is not a {@code String} and does not have a primitive equivalent is
 * serialized. This means that any data that does not extend {@code java.io.Serializable} will cause the writing of the
 * zip tensor to fail. For portability, avoiding serialized data is recommended. Because metadata is more likely to
 * contain unserializable objects, it is possible to set a flag to skip them when writing the zip tensor.
 * <p>
 * The zip tensor format is as follows:
 * <ul>
 *     <li>
 *        Data types are stored as the string equivalent of the data's {@code JavaType}. If the data is {@code String},
 *        then {@link #STRING_TYPE_IDENTIFIER} is used instead of {@code JavaType.OBJECT}.
 *     </li>
 *     <li>
 *        If data is primitive (or the object equivalent), then it is stored as the (byte level) output from the
 *        {@code java.io.DataOutput}. If the data is string, then it is stored as UTF data as encoded by the
 *        {@code java.io.DataOutut#.writeUTF(String)} method. Otherwise, the data is serialized (or attempted to be).
 *     </li>
 *     <li>
 *        Metadata is stored as follows:
 *        <ul>
 *            <li>
 *                 An integer indicating how many metadata elements there are.
 *            </li>
 *            <li>
 *                 An integer indicating how many metadata elements there are.
 *            </li>
 *            <li>
 *            For each metadata element:
 *            <ul>
 *                <li>
 *                    The string key.
 *                </li>
 *                <li>
 *                    The metadata value.
 *                </li>
 *            </ul>
 *            </li>
 *        </ul>
 *        By default, all metadata will be written (or attempted to be).  If it is known that unserializable metadata
 *        values exist, then calling {@code setSkipUnserializableMetadata(true)} will cause these key-values to be
 *        skipped, avoiding any possible exceptions which might be thrown.
 *     </li>
 *     <li>
 *        The dimensions of the zip tensor are held in a zip entry named {@link #DIMENSION_ENTRY}. The dimensions are
 *        stored as space delimted text numbers; each entry represents the size of the dimension.  So, a 5x6x2 tensor's
 *        dimensions would be stored as "5 6 2". The entry is stored as ascii text, <i>not as a utf string</i>; this is
 *        the only zip entry stored in this manner (this is done for ease of readability).
 *     </li>
 *     <li>
 *         Each tensor is stored in a zip entry whose name starts with {@link #TENSOR_ENTRY_PREFIX} and ends with a unique
 *         positive integer id. No guarantees about the sequence of ids is made.  For each tensor entry, the data is
 *         stored as:
 *         <ul>
 *             <li>
 *                The tensor metadata as described above.
 *             </li>
 *             <li>
 *                The data type held by the tensor.
 *             </li>
 *             <li>
 *                If the tensor has a non-standard index, the integer index id (see index entry description below); if
 *                the tensor has a standard index, then {@code -1}.
 *             </li>
 *             <li>
 *                The data for the tensor, in <i>reverse</i> abacus order (see {@link com.pb.sawdust.util.abacus.Abacus}).
 *             </li>
 *         </ul>
 *     </li>
 *     <li>
 *         Each index is stored in a zip entry whose name starts with {@link #INDEX_ENTRY_PREFIX} and ends with a unique
 *         positive integer id. No guarantees about the sequence of ids is made.  For each index entry, the data is
 *         stored as:
 *         <ul>
 *             <li>
 *                The index metadata as described above.
 *             </li>
 *             <li>
 *                The dimension of the index:
 *                <ul>
 *                    <li>
 *                        The number of dimensions in the index, or, if the index is just a pass through for the
 *                        dimensionality of the zip tensor, {@code -1}.
 *                    </li>
 *                    <li>
 *                        If the dimension count was not {@code -1}, the size of each dimension as a series of integers.
 *                    </li>
 *                    <li>
 *                        If the dimension count was not {@code -1}, a series of integers representing the reference
 *                        index for each dimension element (the references are listed in order).
 *                    </li>
 *                 </ul>
 *             </li>
 *             <li>
 *                A boolean that if {@code true} indicates this index has ids.
 *             </li>
 *             <li>
 *                If the index has ids (the previous data element was {@code true}, the index ids are recorded.  For each
 *                dimension:
 *                <ul>
 *                    <li>
 *                        The data type of the id.
 *                    </li>
 *                    <li>
 *                        The ids encoded in order.
 *                    </li>
 *                </ul>
 *             </li>
 *         </ul>
 *     </li>
 *     <li>
 *         Each tensor group is stored in a zip entry whose name starts with {@link #TENSOR_GROUP_ENTRY_PREFIX} and ends
 *         with a unique positive integer id. No guarantees about the sequence of ids is made.  For each tensor group
 *         entry, the data is stored as:
 *         <ul>
 *             <li>
 *                The tensor group metadata as described above.
 *             </li>
 *             <li>
 *                 The (integer) number of tensors in the group.
 *             </li>
 *             <li>
 *                 For each tensor in the group:
 *                 <ul>
 *                     <li>
 *                         The string tensor key.
 *                     </li>
 *                     <li>
 *                         The integer ids of the tensor (see tensor entry description above).
 *                     </li>
 *                 </ul>
 *             </li>
 *             <li>
 *                 The (integer) number of indices in the group.
 *             </li>
 *             <li>
 *                 For each index in the group:
 *                 <ul>
 *                     <li>
 *                         The string index key.
 *                     </li>
 *                     <li>
 *                         The integer ids of the index (see index entry description above).
 *                     </li>
 *                 </ul>
 *             </li>
 *         </ul>
 *     </li>
 * </ul>
 *
 * @author crf <br/>
 *         Started: Dec 13, 2009 10:45:43 PM
 */
public class ZipTensorWriter implements TensorGroupWriter<Serializable,Serializable>,TensorWriter<Serializable>,IndexWriter<Serializable> {
    /**
     * The extension for zip tensor files.
     */
    public static final String ZIP_TENSOR_EXTENSION = "zpt";

    /**
     * The entry name for dimensions of the zip tensor.
     */
    public static final String DIMENSION_ENTRY = "_dim";

    /**
     * The entry name prefix for tensors held by the zip tensor.
     */
    public static final String TENSOR_ENTRY_PREFIX = "_tensor_";

    /**
     * The entry name prefix for indices held by the zip tensor.
     */
    public static final String INDEX_ENTRY_PREFIX = "_index_";

    /**
     * The entry name prefix for tensor groups held by the zip tensor.
     */
    public static final String TENSOR_GROUP_ENTRY_PREFIX = "_tensor_group_";

    /**
     * The identifier for string types (as distinct from other {@code JavaType}s).
     */
    public static final String STRING_TYPE_IDENTIFIER = "STRING";

    private final File zipFile;
    private boolean overwrite;
    private int[] dimensions = null;
    private int tensorCount = 0;
    private int indexCount = 0;
    private int tensorGroupCount = 0;
    private Iterable<int[]> abacus;
    private boolean skipUnserializableMetadata = false;

    /**
     * Constructor specifying the file to write to, and whether to overwrite the file.
     *
     * @param zipFile
     *        The file which will hold the zip tensor.
     *
     * @param overwrite
     *        If {@code true}, the {@code zipFile} will be overwritten (if it exists), if {@code false}, it will be appended
     *        to. If the file doesn't exist, this parameter has no effect.
     */
    public ZipTensorWriter(File zipFile, boolean overwrite) {
        this.zipFile = zipFile;
        this.overwrite = overwrite;
    }

    /**
     * Constructor specifying the file to write to, and whether to overwrite the file.
     *
     * @param zipFile
     *        The file which will hold the zip tensor.
     *
     * @param overwrite
     *        If {@code true}, the {@code zipFile} will be overwritten (if it exists), if {@code false}, it will be appended
     *        to. If the file doesn't exist, this parameter has no effect.
     */
    public ZipTensorWriter(String zipFile, boolean overwrite) {
        this(new File(zipFile),overwrite);
    }

    /**
     * Constructor specifying the file to write to.  The file will be overwritten if it already exists.
     *
     * @param zipFile
     *        The file which will hold the zip tensor.
     */
    public ZipTensorWriter(File zipFile) {
        this(zipFile,true);
    }

    /**
     * Constructor specifying the file to write to.  The file will be overwritten if it already exists.
     *
     * @param zipFile
     *        The file which will hold the zip tensor.
     */
    public ZipTensorWriter(String zipFile) {
        this(new File(zipFile));
    }

    /**
     * Set whether unserializable metadata should be skipped when writing the zip tensor.  If unserializable metadata
     * is not skipped, an exception may be thrown when writing the zip tensor. By default, unserializable metadata
     * <i>will not</i> be skipped so as to alert the user.
     *
     * @param skip
     *        If {@code true}, unserializabl metadata will be skipped, if {@code false} it will not.
     */
    public void setSkipUnserializableMetadata(boolean skip) {
        skipUnserializableMetadata = skip;
    }

    /**
     * Set the dimensions for this zip tensor. The dimensions for a zip tensor can only be set once (even implicitly).
     *
     * @param dimensions
     *        The dimensons for this zip tensor.
     *
     * @throws IllegalStateException if the dimensions have already been set and this call would change the dimensionality
     *                               of the zip tensor.
     */
    public void setDimensions(int[] dimensions) {
        if (this.dimensions != null && !Arrays.equals(this.dimensions,dimensions))
            throw new IllegalStateException("Dimensions for zip tensor can only be set once.");
        this.dimensions = dimensions;
    }

    /**
     * Write the zip tensor to disk.  This method must be called to save the changes to the zip tensor.
     */
    public void writeZipTensor() {
        zf.write();
    }

    private com.pb.sawdust.io.ZipFile zf = null;

    private void initializeZipTensor(int[] dimensions) {
         //initializes the zip tensor file (if necessary) and returns an output stream handle to it
        if (zf == null)
            zf = new com.pb.sawdust.io.ZipFile(zipFile,overwrite);
        if (this.dimensions == null) {
            if (overwrite || !zipFile.exists()) {
                if (dimensions == null) //special index case
                    throw new IllegalArgumentException("Cannot write index to ZipTensor where dimensions have not been established");
                this.dimensions = dimensions;
                writeDimensions(zf);
//                overwrite = false;
            } else {   //zip file exists and don't overwrite
                int[] dims = loadZipTensorInfo();
                if (dims == null)
                    throw new IllegalStateException("Corrupt or invalid ZipTensor file - dimensions could not be read.");
                this.dimensions = dims;
                checkDimensions(dimensions);
            }
            //abacus = dimensions.length == 0 ? Arrays.asList(new int[0]) : IterableAbacus.getIterableAbacus(dimensions);
            //todo: note: I just changed the format to reverse abacus direction
            //      this was, in theory, to make reading in a ziptensor quicker;  we'll see
            abacus = dimensions.length == 0 ? Arrays.asList(new int[0]) : IterableAbacus.getIterableAbacus(true,dimensions);
        } else { //dimensions already set
            checkDimensions(dimensions);
        }
    }

    private void checkDimensions(int[] dimensions) {
        if (!Arrays.equals(dimensions,this.dimensions))
            throw new IllegalArgumentException("Dimensions invalid for this writer, expected\n\t" + Arrays.toString(this.dimensions) + "\nfound\n\t" + Arrays.toString(dimensions));
    }

    private int[] loadZipTensorInfo() {
        int[] dimensions;
        ZipFile zf = new ZipFile(zipFile);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        zf.extract(DIMENSION_ENTRY,baos);
        String[] sdim = new String(baos.toByteArray()).split("\\s");
        dimensions = new int[sdim.length];
        for (int i : range(dimensions.length))
            dimensions[i] = Integer.parseInt(sdim[i]);
        for (String name : zf)
            if (name.startsWith(TENSOR_ENTRY_PREFIX))
                tensorCount = (Math.max(tensorCount,Integer.parseInt(name.replace(TENSOR_ENTRY_PREFIX,""))+1));
            else if (name.startsWith(INDEX_ENTRY_PREFIX))
                indexCount = (Math.max(indexCount,Integer.parseInt(name.replace(INDEX_ENTRY_PREFIX,""))+1));
            else if (name.startsWith(TENSOR_GROUP_ENTRY_PREFIX))
                tensorGroupCount = (Math.max(tensorGroupCount,Integer.parseInt(name.replace(TENSOR_GROUP_ENTRY_PREFIX,""))+1));
        return dimensions;
    }

    /**
     * {@inheritDoc}
     *
     * This method will queue the tensor for writing, but will not actually write the tensor to disk. Call
     * {@link #writeZipTensor()} to write the zip tensor to disk.
     *
     * @throws IllegalArgumentException if the dimensions of {@code tensor} are different than those established for this
     *                                  zip tensor.
     */
    public void writeTensor(Tensor<? extends Serializable> tensor) {
        initializeZipTensor(tensor.getDimensions());
        writeTensorToZip(tensor);
    }

    /**
     * {@inheritDoc}
     *
     * This method will queue the index for writing, but will not actually write the index to disk. Call
     * {@link #writeZipTensor()} to write the zip tensor to disk.
     *
     * @throws IllegalArgumentException if the dimensions of {@code index} are different than those established for this
     *                                  zip tensor.
     */
    public void writeIndex(Index<? extends Serializable> index) {
        initializeZipTensor(index.getDimensions());
        writeIndexToZip(index,false);
    }

    /**
     * {@inheritDoc}
     *
     * This method will queue the tensor group for writing, but will not actually write the tensor group to disk. Call
     * {@link #writeZipTensor()} to write the zip tensor to disk.
     *
     * @throws IllegalArgumentException if the dimensions of {@code group} are different than those established for this
     *                                  zip tensor.
     */
    public void writeTensorGroup(TensorGroup<? extends Serializable,? extends Serializable> group) {
        initializeZipTensor(group.getDimensions());
        //linked, just in case order matters...
        Map<String,Integer> ts = new LinkedHashMap<String,Integer>();
        Map<String,Integer> is = new LinkedHashMap<String,Integer>();
        for (String tensorKey : group.tensorKeySet())
            ts.put(tensorKey,writeTensorToZip(group.getTensor(tensorKey)));
        for (String indexKey : group.indexKeySet())
            is.put(indexKey,writeIndexToZip(group.getIndex(indexKey),false));
        writeTensorGroupEntry(group,ts,is,zf);
    }

    private String getTypeIdentifierFromCollection(Collection<?> collectionToType) {
        if (collectionToType.size() == 0)
            return getTypeIdentifier(collectionToType); //shortcut to return object
        Iterator<?> i = collectionToType.iterator();
        String type = getTypeIdentifier(i.next());
        while (i.hasNext())
            if (!type.equals(getTypeIdentifier(i.next())))
                return getTypeIdentifier(collectionToType);
        return type;
    }

    private int writeTensorGroupEntry(final TensorGroup<?,?> group, final Map<String,Integer> ts, final Map<String,Integer> is, com.pb.sawdust.io.ZipFile zf) {
        int entryNumber = tensorGroupCount++;
        zf.addEntry(TENSOR_GROUP_ENTRY_PREFIX + entryNumber,new com.pb.sawdust.io.ZipFile.ZipEntrySource() {
            public void writeData(OutputStream os) throws IOException {
                DataOutputStream dos = new DataOutputStream(os);
                writeMetadata(group,dos);
                Set<String> tKeys = ts.keySet();
                int size = tKeys.size();
                dos.writeInt(size);
                for (String key : tKeys) {
                    dos.writeUTF(key);
                    dos.writeInt(ts.get(key));
                }
                Set<String> iKeys = is.keySet();
                size = iKeys.size();
                dos.writeInt(size);
                for (String key : iKeys) {
                    dos.writeUTF(key);
                    dos.writeInt(is.get(key));
                }
                dos.flush();
            }

            public void close() {}
        });
        return entryNumber;
    }

     private void writeDimensions(com.pb.sawdust.io.ZipFile zf) {
        final StringBuilder sb = new StringBuilder();
        int len = dimensions.length;
        if (len > 0)
            sb.append(dimensions[0]);
        for (int i = 1; i < len; i++)
            sb.append(" ").append(dimensions[i]);
        zf.addEntry(DIMENSION_ENTRY,sb.toString(),Charset.forName("US-ASCII")); 
    }

    private int writeTensorToZip(final Tensor<? extends Serializable> tensor) {
        @SuppressWarnings("unchecked") //this is understood and will throw an exception when serialization is attempted if not correct
        Index<? extends Serializable> index = (Index<? extends Serializable>) tensor.getIndex();
        final boolean hasIds = !(index instanceof IdlessIndex) && !(index instanceof ScalarIndex); //scalar can hold no ids, but is not necessarily Idless
        final int ic = hasIds ?  indexCount : -1;
        int entryNumber = tensorCount++;
        zf.addEntry(TENSOR_ENTRY_PREFIX + entryNumber,new com.pb.sawdust.io.ZipFile.ZipEntrySource() {
            public void writeData(OutputStream os) throws IOException {
                DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(os));
                writeMetadata(tensor,dos);
                JavaType type = tensor.getType();
                boolean isString = type == JavaType.OBJECT && TensorUtil.getFirstElement(tensor) instanceof String;
                dos.writeUTF(isString ? STRING_TYPE_IDENTIFIER : type.name());
                dos.writeInt(ic);
                switch (type) {
                    case BYTE : writeByteTensor((ByteTensor) tensor,dos); break;
                    case SHORT : writeShortTensor((ShortTensor) tensor,dos); break;
                    case INT : writeIntTensor((IntTensor) tensor,dos); break;
                    case LONG : writeLongTensor((LongTensor) tensor,dos); break;
                    case FLOAT : writeFloatTensor((FloatTensor) tensor,dos); break;
                    case DOUBLE : writeDoubleTensor((DoubleTensor) tensor,dos); break;
                    case CHAR : writeCharTensor((CharTensor) tensor,dos); break;
                    case BOOLEAN : writeBooleanTensor((BooleanTensor) tensor,dos); break;
                    case OBJECT : {
                        if (isString) {
                            @SuppressWarnings("unchecked") //should be a string
                            Tensor<String> t = (Tensor<String>) tensor;
                            writeStringTensor(t,dos);
                        } else {
                            writeObjectTensor(tensor,dos);
                        }
                        break;
                    }
                }
                dos.flush();
            }

            public void close() {}
        });
        if (hasIds) //only write index if ids are present, otherwise will just be standard tensor index - redundant (metadata may be lost?)
            writeIndexToZip(index,true);
        return entryNumber;
    }
    
    private void writeByteTensor(ByteTensor tensor, DataOutput os) throws IOException {
        for (int[] ind : abacus)
            os.writeByte(tensor.getCell(ind));
    }                                                                                       
    
    private void writeShortTensor(ShortTensor tensor, DataOutput os) throws IOException {
        for (int[] ind : abacus)
            os.writeShort(tensor.getCell(ind));
    }
    
    private void writeIntTensor(IntTensor tensor, DataOutput os) throws IOException {
        for (int[] ind : abacus)
            os.writeInt(tensor.getCell(ind));
    }
    
    private void writeLongTensor(LongTensor tensor, DataOutput os) throws IOException {
        for (int[] ind : abacus)
            os.writeLong(tensor.getCell(ind));
    }
    
    private void writeFloatTensor(FloatTensor tensor, DataOutput os) throws IOException {
        for (int[] ind : abacus)
            os.writeFloat(tensor.getCell(ind));
    }
    
    private void writeDoubleTensor(DoubleTensor tensor, DataOutput os) throws IOException {
        for (int[] ind : abacus)
            os.writeDouble(tensor.getCell(ind));
    }
    
    private void writeCharTensor(CharTensor tensor, DataOutput os) throws IOException {
        for (int[] ind : abacus)
            os.writeChar(tensor.getCell(ind));
    }
    
    private void writeBooleanTensor(BooleanTensor tensor, DataOutput os) throws IOException {
        for (int[] ind : abacus)
            os.writeBoolean(tensor.getCell(ind));
    }

    private void writeStringTensor(Tensor<String> tensor, DataOutput os) throws IOException {
        for (int[] ind : abacus)
            os.writeUTF(tensor.getValue(ind));
    }

    private void writeObjectTensor(Tensor<? extends Serializable> tensor, DataOutputStream os) throws IOException {
        //todo: make list of oo streams to close when finished writing
        ObjectOutputStream oo = new ObjectOutputStream(os);
        for (int[] ind : abacus)
            oo.writeObject(tensor.getValue(ind));
        oo.flush();
    }

    private void writeObject(Object o, String type, DataOutputStream os) throws IOException {
        if (type.equals(STRING_TYPE_IDENTIFIER)) {
            os.writeUTF((String) o);
        } else {
            switch (JavaType.valueOf(type)) {
                case BOOLEAN : os.writeBoolean((Boolean) o); break;
                case CHAR : os.writeChar((Character) o); break;
                case BYTE : os.writeByte((Byte) o); break;
                case SHORT : os.writeShort((Short) o); break;
                case INT : os.writeInt((Integer) o); break;
                case LONG : os.writeLong((Long) o); break;
                case FLOAT : os.writeFloat((Float) o); break;
                case DOUBLE : os.writeDouble((Double) o); break;
                case OBJECT : {
                        //todo: make list of oo streams to close when finished writing
                        ObjectOutputStream oo = new ObjectOutputStream(os);
                        oo.writeObject(o);
                        oo.flush();
                        break;
                }
            }
        }
    }

    private String getTypeIdentifier(Object value) {
        return value instanceof String ? STRING_TYPE_IDENTIFIER : JavaType.getPrimitiveJavaType(value.getClass()).name();
    }

    private void writeMetadata(ContainsMetadata<String> metadata, DataOutputStream os) throws IOException {
        List<Object[]> writableMetadata = new LinkedList<Object[]>();
        for (String key : metadata.getMetadataKeys()) {
            Object v = metadata.getMetadataValue(key);
            if (skipUnserializableMetadata && !(v instanceof Serializable))
                continue;
            writableMetadata.add(new Object[] {key,v});
        }
        os.writeInt(writableMetadata.size());
        for (Object[] md : writableMetadata) {
            os.writeUTF((String) md[0]);
            String type = getTypeIdentifier(md[1]);
            os.writeUTF(type);
            writeObject(md[1],type,os);
        }
    }

    private int writeIndexToZip(final Index<? extends Serializable> index, final boolean passThrough) {
        if (!IndexUtil.indexValidFor(index,dimensions))
            throw new IllegalArgumentException("Index invalid for ZipTensor with dimensionality " + Arrays.toString(dimensions));
        int entryNumber = indexCount++;
        zf.addEntry(INDEX_ENTRY_PREFIX + entryNumber,new com.pb.sawdust.io.ZipFile.ZipEntrySource() {
            public void writeData(OutputStream os) throws IOException {
                DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(os));
                writeMetadata(index,dos);
                int[] dim;
                if (passThrough) {
                    dos.writeInt(-1);
                    dim = dimensions;
                } else {
                    dim = index.getDimensions();
                    dos.writeInt(dim.length);
                    for (int d : dim)
                        dos.writeInt(d);
                    for (int d : range(dim.length))
                        for (int i : range(dim[d]))
                            dos.writeInt(index.getIndex(d,i));
                }
                boolean hasIds = !(index instanceof IdlessIndex);
                dos.writeBoolean(hasIds);
                if (hasIds) {
                    List<? extends List<? extends Serializable>> ids = index.getIndexIds();
                    for (int d : range(dim.length)) {
                        //String type = getTypeIdentifier(index.getIndexId(d,0));
                        String type = getTypeIdentifierFromCollection(ids.get(d));
                        dos.writeUTF(type);
                        if (type.equals(STRING_TYPE_IDENTIFIER)) {
                            for (int i : range(dim[d]))
                                dos.writeUTF((String) index.getIndexId(d,i));
                        } else {
                            switch (JavaType.valueOf(type)) {
                                case BOOLEAN : {
                                    for (int i : range(dim[d]))
                                        dos.writeBoolean((Boolean) index.getIndexId(d,i));
                                    break;
                                }
                                case CHAR : {
                                    for (int i : range(dim[d]))
                                        dos.writeChar((Character) index.getIndexId(d,i));
                                    break;
                                }
                                case BYTE : {
                                    for (int i : range(dim[d]))
                                        dos.writeByte((Byte) index.getIndexId(d,i));
                                    break;
                                }
                                case SHORT : {
                                    for (int i : range(dim[d]))
                                        dos.writeShort((Short) index.getIndexId(d,i));
                                    break;
                                }
                                case INT : {
                                    for (int i : range(dim[d]))
                                        dos.writeInt((Integer) index.getIndexId(d,i));
                                    break;
                                }
                                case LONG : {
                                    for (int i : range(dim[d]))
                                        dos.writeLong((Long) index.getIndexId(d,i));
                                    break;
                                }
                                case FLOAT : {
                                    for (int i : range(dim[d]))
                                        dos.writeFloat((Float) index.getIndexId(d,i));
                                    break;
                                }
                                case DOUBLE : {
                                    for (int i : range(dim[d]))
                                        dos.writeDouble((Double) index.getIndexId(d,i));
                                    break;
                                }
                                case OBJECT : {
                                    //todo: make list of oo streams to close when finished writing
                                    ObjectOutputStream oo = new ObjectOutputStream(dos);
                                    for (int i : range(dim[d]))
                                        oo.writeObject(index.getIndexId(d,i));
                                    oo.flush();
                                    break;
                                }
                            }
                        }
                    }
                }
                dos.flush();
            }

            public void close() {}
        });
        return entryNumber;
    }

    public static void writeTensor(File file, Tensor<? extends Serializable> tensor) {
        ZipTensorWriter writer = new ZipTensorWriter(file);
        writer.writeTensor(tensor);
        writer.writeZipTensor();
    }

    public static void writeTensorGroup(File file, TensorGroup<? extends Serializable,? extends Serializable> tensorGroup) {
        ZipTensorWriter writer = new ZipTensorWriter(file);
        writer.writeTensorGroup(tensorGroup);
        writer.writeZipTensor();
    }

    public static void main(String ... args) {
        //testing, testing
        List<List<String>> ids = new LinkedList<List<String>>();
        ids.add(Arrays.asList("a","b","c","d","e"));
        ids.add(Arrays.asList("a","b","c","d","e","z"));
        IntTensor t = ArrayTensor.getFactory().initializedIntTensor(4,ids,5,6);
        t.setCell(-4,0,0);
        t.setMetadataValue("name","yotumom");
        t.setMetadataValue("year",123);
        t.setMetadataValue("year2",123);
        t.setMetadataValue("year3",123);
        t.setMetadataValue("year4",123);
        t.setMetadataValue("date",new Date());
        System.out.println(TensorUtil.toString(t));

        String file = "d:\\dump\\test." + ZIP_TENSOR_EXTENSION;
        ZipTensorWriter ztw = new ZipTensorWriter(file,true);
        ztw.writeTensor(t);
//        ztw.writeTensor(t);
//        ztw.writeTensor(t);
        ztw.writeZipTensor(); //necessary to flush data

        ZipTensorReader<?,?> ztr = new ZipTensorReader(file);
        //ztr.setCurrentTensor(2);
        Tensor<?> t2 = ArrayTensor.getFactory().tensor(ztr);

        System.out.println(TensorUtil.toString(t2));
        System.out.println(t2.getMetadataValue("name"));
        System.out.println(t2.getMetadataValue("year"));
        System.out.println(t2.getMetadataValue("date"));
    }
}