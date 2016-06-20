package controllers;

import model.Message;
import play.db.DB;
import play.db.Database;
import play.mvc.Controller;

import javax.annotation.Nonnull;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Corey on 6/19/2016.
 * Project: playsimplenewsfeed
 * <p></p>
 * Purpose of Class:
 */
public class DatabaseController extends Controller {

    /**
     * A couple of constants used to define standard database operations.
     */
    public enum DatabaseTask {
        INCREMENT_LIKES, DECREMENT_LIKES, INSERT_MESSAGE
    }

    /**
     * An interface used for transferring the result of a generic database operation back to the calling thread.
     */
    public interface OnDatabaseResponseListener {

        /**
         * Called whenever a {@link DatabaseTask} is completed.
         *
         * @param databaseTask The {@link DatabaseTask} that was completed.
         * @param response     True if the database operation was successful or false if it was not.
         */
        void onDatabaseResponse(DatabaseTask databaseTask, boolean response);
    }

    /**
     * An interface used for transferring the results of the {@link #getNewsFeed(OnNewsFeedLoadedListener)} ()} method
     * back to the calling thread.
     */
    public interface OnNewsFeedLoadedListener {

        /**
         * Called when {@link #getNewsFeed(OnNewsFeedLoadedListener)} has finished accessing the database.
         *
         * @param newsFeed The news feed that was retrieved.
         */
        void onNewsFeedLoaded(ArrayList<Message> newsFeed);
    }

    private static final String NEWS_FEED = "newsfeed";
    private static final String ID = "_id";
    private static final String TITLE = "title";
    private static final String MESSAGE = "message";
    private static final String LIKES = "likes";

    private static final String INSERT = "insert into ";
    private static final String LEFT_PARENTHESIS = "( ";
    private static final String RIGHT_PARENTHESIS = ") ";
    private static final String COMMA = ", ";
    private static final String VALUES = " values ";
    private static final String UPDATE = "update ";
    private static final String SET = " set ";
    private static final String SELECT = "select ";
    private static final String ASTERISK = "* ";
    private static final String FROM = "FROM ";
    private static final String WHERE = " WHERE ";
    private static final String EQUALS = " EQUALS ";
    private static final String QUESTION_MARK = "?";
    private static final String ORDER_BY = " order by ";
    private static final String DESC = " DESC";

    private DatabaseController() {
    }

    /**
     * This method is invoked on a separate thread, so the web page does not get blocked by the database operation.
     *
     * @param listener An instance of {@link OnNewsFeedLoadedListener} that will be used as a callback when the
     *                 resulting database operation is finished.
     */
    public static void getNewsFeed(final OnNewsFeedLoadedListener listener) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ArrayList<Message> newsFeed = new ArrayList<>();
                try {
                    ResultSet resultSet = DB.getConnection()
                            .prepareStatement(
                                    SELECT + ASTERISK +
                                            FROM + NEWS_FEED +
                                            ORDER_BY + ID + DESC)
                            .executeQuery();
                    while (resultSet.next()) {
                        long id = resultSet.getLong(resultSet.findColumn(ID));
                        String title = resultSet.getString(resultSet.findColumn(TITLE));
                        String message = resultSet.getString(resultSet.findColumn(MESSAGE));
                        int likes = resultSet.getInt(resultSet.findColumn(LIKES));
                        newsFeed.add(new Message(id, title, message, likes));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                listener.onNewsFeedLoaded(newsFeed);
            }
        });
        thread.start();
    }

    /**
     * Increments the number of likes that a message currently has.
     *
     * @param messageId              The ID of the message whose number of likes should be incremented.
     * @param numberOfLikesCurrently The number of likes that the message has currently (before the user pressed the
     *                               like button).
     * @return True if the update was successful or false if it was not.
     */
    public static boolean incrementLikesForMessage(long messageId, int numberOfLikesCurrently) {
        return updateLikes(messageId, numberOfLikesCurrently + 1);
    }

    /**
     * Decrements the number of likes that a message currently has.
     *
     * @param messageId              The ID of the message whose number of likes should be incremented.
     * @param numberOfLikesCurrently The number of likes that the message has currently (before the user un-liked the
     *                               message).
     * @return True if the update was successful or false if it was not.
     */
    public static boolean decrementLikesForMessage(long messageId, int numberOfLikesCurrently) {
        return updateLikes(messageId, numberOfLikesCurrently - 1);
    }

    /**
     * Inserts a new message into the database.
     *
     * @param message The {@link Message} object to be inserted.
     * @return True if the insertion was successful or false if it was not.
     */
    public static boolean insertNewMessage(Message message) {
        try {
            PreparedStatement statement = DB.getConnection()
                    .prepareStatement(INSERT + NEWS_FEED + LEFT_PARENTHESIS +
                            TITLE + COMMA +
                            MESSAGE +
                            RIGHT_PARENTHESIS + VALUES + LEFT_PARENTHESIS +
                            QUESTION_MARK + COMMA +
                            QUESTION_MARK +
                            RIGHT_PARENTHESIS
                    );
            // Bind the title of the message to the first parameter.
            // Keep in mind that JDBC does not use zero-based indices.
            statement.setString(1, message.getTitle());
            // Bind the body of the message to the second parameter.
            statement.setString(2, message.getBody());

            //If the number returned is equal to 1, then we know that we successfully inserted the row.
            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Private Helper Methods
    ///////////////////////////////////////////////////////////////////////////

    private static boolean updateLikes(long messageId, int numberOfLikesAfterUpdate) {
        try {
            PreparedStatement statement = DB.getConnection()
                    .prepareStatement(UPDATE + NEWS_FEED +
                            SET + LIKES + EQUALS + numberOfLikesAfterUpdate +
                            WHERE + ID + EQUALS + QUESTION_MARK);
            // Bind the ID of the message to the first parameter.
            // Keep in mind that JDBC does not use zero-based indices.
            statement.setLong(1, messageId);
            int numberOfChanges = statement.executeUpdate();

            //If the number of changes is equal to 1, then we know that we successfully updated the row.
            return numberOfChanges == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
