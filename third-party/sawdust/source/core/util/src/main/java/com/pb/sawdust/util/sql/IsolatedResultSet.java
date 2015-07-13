package com.pb.sawdust.util.sql;

import com.pb.sawdust.util.sql.wrappers.WrappedResultSet;
import com.pb.sawdust.util.exceptions.MultiException;

import java.sql.*;
import java.util.List;
import java.util.LinkedList;

/**
 * The {@code IsolatedResultSet} class provides a framework through which resources used to generate a {@code ResultSet}
 * can be freed up through a single method call. Specifically, the {@code java.sql.Connection} and (if used)
 * {@code java.sql.Statement} used to create a result set are closed automatically when the {@link #close()} method is
 * called. This means that these resources cannot be directly used again, however for one-off result sets this
 * class provides some convenience by wrapping the multiple close statement idiom into a singel method call.
 * <p>
 * The close method may be set to ignore {@code java.sql.SQLException}s thrown during the close call (the default), or
 * it may capture them and rethrow them after all attempts to close resources have finished.
 *
 * @author crf <br/>
 *         Started: Sep 30, 2008 6:31:28 PM
 */
public class IsolatedResultSet extends WrappedResultSet {
    private final Connection connection;
    private final Statement statement;
    private boolean rethrowCloseExceptions = false;

    /**
     * Constructor specifying the result set, and the connection and statement used to generate it.
     *
     * @param resultSet
     *        The result set to wrap.
     *
     * @param connection
     *        The connection through which the result set was generated.
     *
     * @param statement
     *        The statment used to generate the result set.
     */
    public IsolatedResultSet(ResultSet resultSet, Connection connection, Statement statement) {
        super(resultSet);
        this.connection = connection;
        this.statement = statement;
    }

    /**
     * Constructor specifying the result set, and the connection used to generate it.
     *
     * @param resultSet
     *        The result set to wrap.
     *
     * @param connection
     *        The connection through which the result set was generated.
     */
    public IsolatedResultSet(ResultSet resultSet, Connection connection) {
        this(resultSet,connection,null);
    }

    /**
     * Set whether result set should rethrow exceptions encountered during {@code close()} calls, or suppress them.
     *
     * @param rethrowCloseExceptions
     *        If {@code true}, exceptions caught during a {@code close()} call will be rethrown, if {@code false}, they
     *        will be supressed.
     */
    public void setRethrowCloseExceptions(boolean rethrowCloseExceptions) {
        this.rethrowCloseExceptions = rethrowCloseExceptions;
    }

    /**
     * {@inheritDoc}
     *
     * This method will close the resources used to generate the result set, along with the result set. If any exceptions
     * are encountered during the resource closings, they will be caught; if the class has been set to rethrow close
     * exceptions (via {@code setRethrowCloseExceptions(true)}), then they'll be rethrown, otherwise they are suppressed.
     * The rethrown exceptions will be wrapped in the form of a {@code MultiException}.
     */
    public void close() {
        List<SQLException> exceptions = new LinkedList<SQLException>();
        try {
            if (resultSet != null)
                resultSet.close();
        } catch (SQLException e) {
            exceptions.add(e);
        }
        try {
            if (statement != null)
                statement.close();
        } catch (SQLException e) {
            exceptions.add(e);
        }
        try {
            if (connection != null)
                connection.close();
        } catch (SQLException e) {
            exceptions.add(e);
        }
        if (rethrowCloseExceptions) {
            switch (exceptions.size()) {
                case 1 : throw new MultiException(exceptions.get(0));
                case 2 : throw new MultiException(exceptions.get(0),exceptions.get(1));
                case 3 : throw new MultiException(exceptions.get(0),exceptions.get(1),exceptions.get(2));
            }
        }
    }
}