package controllers;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import database.DatabaseApi;
import model.Message;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.ArrayList;

public class Application extends Controller {

    public static Result index() {
        ArrayNode arrayNode = Json.newArray();
        ArrayList<Message> newsFeed = DatabaseApi.getNewsFeed();
        if (newsFeed != null) {
            for (Message message : newsFeed) {
                ObjectNode objectNode = Json.newObject();
                objectNode.put("id", message.getId());
                objectNode.put("title", message.getTitle());
                objectNode.put("body", message.getBody());
                objectNode.put("likes", message.getLikes());
                arrayNode.add(objectNode);
            }
        } else {
            return internalServerError("Server Error");
        }
        return ok(arrayNode);
    }

    public static Result createPost() {
        String title = request().getQueryString("title");
        String message = request().getQueryString("message");
        if (title == null || message == null) {
            return badRequest("Invalid Parameters, expected title and message");
        }
        boolean result = DatabaseApi.createMessage(title, message);
        if (result) {
            return ok("Successfully created the new post!");
        } else {
            return badRequest("Bad Request");
        }
    }

    public static Result incrementLikes() {
        long id;
        try {
            id = Long.parseLong(request().getQueryString("id"));
        } catch (Exception e) {
            return badRequest("Invalid parameter, expected ID");
        }
        boolean result = DatabaseApi.incrementLikesForMessage(id);
        if (result) {
            return ok("Successful Increment");
        } else {
            return badRequest("Bad Request");
        }
    }

    public static Result decrementLikes() {
        long id;
        try {
            id = Long.parseLong(request().getQueryString("id"));
        } catch (Exception e) {
            return badRequest("Invalid parameter, expected ID");
        }
        boolean result = DatabaseApi.decrementLikesForMessage(id);
        if (result) {
            return ok("Successful Decrement!");
        } else {
            return badRequest("Bad Request");
        }
    }

}
