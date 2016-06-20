package model;

/**
 * Created by Corey on 6/19/2016.
 * Project: playsimplenewsfeed
 * <p></p>
 * Purpose of Class:
 */
public class Message {

    private final long id;
    private final String title;
    private final String body;
    private int likes;

    public Message(long id, String title, String body, int likes) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.likes = likes;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }
}
