package database;

import model.Message;
import play.db.DB;
import play.mvc.Controller;

import javax.annotation.Nullable;
import javax.validation.constraints.Null;
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
public class DatabaseApi extends Controller {

    private static final String NEWS_FEED = "newsfeed";
    private static final String ID = "_id";
    private static final String TITLE = "title";
    private static final String MESSAGE = "message";
    private static final String LIKES = "_likes";

    private static final String INSERT = "insert into ";
    private static final String LEFT_PARENTHESIS = "( ";
    private static final String RIGHT_PARENTHESIS = ") ";
    private static final String COMMA = ", ";
    private static final String SEMI_COLON = ";";
    private static final String VALUES = " values ";
    private static final String UPDATE = "update ";
    private static final String SET = " set ";
    private static final String SELECT = "select ";
    private static final String ASTERISK = "* ";
    private static final String FROM = " FROM ";
    private static final String WHERE = " WHERE ";
    private static final String EQUALS = " = ";
    private static final String QUESTION_MARK = "?";
    private static final String ORDER_BY = " order by ";
    private static final String DESC = " DESC";

    private DatabaseApi() {
    }

    /**
     * @return All of the messages in the news feed.
     */
    @Nullable
    public static ArrayList<Message> getNewsFeed() {
        ArrayList<Message> newsFeed = new ArrayList<>();
        ResultSet resultSet = null;
        try {
            resultSet = DB.getConnection()
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
            return null;
        } finally {
            try {
                if (resultSet != null) {
                    System.out.println("GetNewsFeed: Connection closed!");
                    resultSet.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return newsFeed;
    }

    /**
     * Increments the number of likes that a message currently has.
     *
     * @param messageId The ID of the message whose number of likes should be incremented.
     * @return True if the update was successful or false if it was not.
     */
    public static boolean incrementLikesForMessage(long messageId) {
        return updateLikes(messageId, getNumberOfCurrentLikes(messageId) + 1);
    }

    /**
     * Decrements the number of likes that a message currently has.
     *
     * @param messageId The ID of the message whose number of likes should be incremented.
     * @return True if the update was successful or false if it was not.
     */
    public static boolean decrementLikesForMessage(long messageId) {
        return updateLikes(messageId, getNumberOfCurrentLikes(messageId) - 1);
    }

    public static long getNumberOfCurrentLikes(long id) {
        PreparedStatement statement = null;
        try {
            statement = DB.getConnection().prepareStatement(SELECT + LIKES + FROM + NEWS_FEED + WHERE + ID + EQUALS +
                    QUESTION_MARK + SEMI_COLON);
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet != null) {
                if (resultSet.next()) {
                    return resultSet.getLong(LIKES);
                }
            }
            return -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        } finally {
            if (statement != null) {
                try {
                    System.out.println("GetNumberOfCurrentLikes: Connection closed!");
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Inserts a new message into the database.
     *
     * @return True if the insertion was successful or false if it was not.
     */
    public static boolean createMessage(String title, String body) {
        PreparedStatement statement = null;
        try {
            statement = DB.getConnection()
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
            statement.setString(1, title);
            // Bind the body of the message to the second parameter.
            statement.setString(2, body);

            //If the number returned is equal to 1, then we know that we successfully inserted the row.
            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (statement != null) {
                try {
                    System.out.println("Insert Message: Connection closed!");
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Private Helper Methods
    ///////////////////////////////////////////////////////////////////////////

    private static boolean updateLikes(long messageId, long numberOfLikesAfterUpdate) {
        PreparedStatement statement = null;
        try {
            statement = DB.getConnection()
                    .prepareStatement(UPDATE + NEWS_FEED +
                            SET + LIKES + EQUALS + numberOfLikesAfterUpdate +
                            WHERE + ID + EQUALS + QUESTION_MARK + SEMI_COLON);
            // Bind the ID of the message to the first parameter.
            // Keep in mind that JDBC does not use zero-based indices.
            statement.setLong(1, messageId);
            int numberOfChanges = statement.executeUpdate();

            //If the number of changes is equal to 1, then we know that we successfully updated the row.
            return numberOfChanges == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                    System.out.println("Update Likes: Connection closed!");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}