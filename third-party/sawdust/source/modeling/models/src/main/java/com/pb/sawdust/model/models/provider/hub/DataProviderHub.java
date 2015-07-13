package com.pb.sawdust.model.models.provider.hub;

import com.pb.sawdust.model.models.provider.DataProvider;
import com.pb.sawdust.model.models.provider.IdData;

import java.util.Set;

/**
 * The {@code DataProviderHub} interface provides access to {@code DataProvider}s. A provider hub has an associated
 * data length, which must match that of the providers it holds. Data providers are retrieved by a key, whose type is
 * specified as the generic parameter to this class. Like a {@code DataProvider}, the {@code DataProviderHub} can
 * be partitioned such that the {@code DataProvider}s are likewise partitioned. Also, a provider hub has a data identifier
 * which (probably) is distinct from its data providers' and can be used for calculation caching.
 * <p>
 * Data provider hubs are useful for situations where different data providers (which may be related or similar) are needed
 * based on the context.  For example, in choice utility models, different choices will require different data (often,
 * though, with the same variable names), and provider hubs create a straightforward way to collect the data providers.
 * <p>
 * Data provider hubs can also store data which is shared across all keys, so that a single {@code DataProvider} can be
 * used to obtain the data for the shared variables. This shared provider is available via {@link PolyDataProvider}.
 * This shared data will also be included in the keyed data providers, but is available in shared form for situations
 * where performance benefits may be obtained by using the data in a non-keyed manner.
 * <p>
 * Though the length of all of the data providers should be consistent, there is no requirement that they be immutable.
 * It is up to the implementing class to determine the rules through which internal data consistency is enforced.
 *
 * @param <K>
 *        The type of the data provider key.
 *
 * @author crf <br/>
 *         Started Sep 14, 2010 11:48:40 AM
 */
public interface DataProviderHub<K> extends IdData {

    /**
     * The data length to use if a provider hub has not been initialized with data providers. This value is negative and
     * distinct from {@link DataProvider#UNINITIALIZED_DATA_LENGTH}.
     */
    final int UNINITIALIZED_HUB_LENGTH = DataProvider.UNINITIALIZED_DATA_LENGTH-1;

    /**
     * Get the data provider for a key.
     *
     * @param key
     *        The data provider key.
     *
     * @return the data provider for {@code key}.
     *
     * @throws IllegalArgumentException if this provider hub does not have a provider associated with {@code key}.
     */
    DataProvider getProvider(K key);

    /**
     * Get a partition of this data provider hub. The partition will provide similarly partitioned data providers.
     *
     * @param start
     *        The starting (inclusive) observation for the sub-data provider hub.
     *
     * @param end
     *        The ending (exclusive) observation for the sub-data provider hub.
     *
     * @return a data provider hub for observations from {@code start} up to {@code end}.
     *
     * @throws IllegalArgumentException if <code>end &lt;= start</code> or if {@code start} and/or {@code end} are out of
     *                                  this provider hub's data bounds (<i>i.e.</i> if either are less than zero or greater
     *                                  than the data hub's length).
     */
    DataProviderHub<K> getSubDataHub(int start, int end);  

    /**
     * Get the index to the original source data provider hub's observation where this data provider hub starts. If this
     * data provider hub is the source (<i>i.e.</i> is not a sub-data providerhub ) then this method will return zero.
     * Otherwise, it returns the observation index in the original data provider hub that this sub-data's coverage starts
     * at. This method is useful for joining together work forked through sub-data constructions.
     *
     * @return the index in the source data provider hub where this provider's observations start.
     */
    int getAbsoluteStartIndex();

    /**
     * Get the data length for this provider hub.
     *
     * @return this provider hub's data length, or {@link #UNINITIALIZED_HUB_LENGTH} if this hub has not been initialized
     *         with data providers and has no length associated with it..
     */
    int getDataLength();

    /**
     * Get a set of all of the data keys available from this provider hub.
     *
     * @return the data keys available from this hub.
     */
    Set<K> getDataKeys();

    /**
     * Get a data provider for the data shared across all keys in this provider hub.
     *
     * @return a data provider for the variables in this provider hub which are constant across keys.
     */
    DataProvider getSharedProvider();
}