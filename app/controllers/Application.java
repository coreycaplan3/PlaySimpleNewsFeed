package controllers;

import controllers.DatabaseController.OnNewsFeedLoadedListener;
import model.Message;
import play.api.mvc.*;
import play.api.mvc.Call;
import play.mvc.*;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

import java.util.ArrayList;

public class Application extends Controller {

    public static Result index() {
        DatabaseController.getNewsFeed(new OnNewsFeedLoadedListener() {
            @Override
            public void onNewsFeedLoaded(ArrayList<Message> newsFeed) {
                //TODO redirect, pass in list
            }
        });
        return ok(index.render("Welcome to The Buzz!"));
    }

    public static Result home() {
        return ok("Latest");
    }

//    @Override
//    public void onDatabaseResponse(DatabaseTask databaseTask, boolean response) {
//        if (databaseTask == DatabaseTask.INSERT_MESSAGE) {
//
//        } else if (databaseTask == DatabaseTask.INCREMENT_LIKES) {
//
//        } else if (databaseTask == DatabaseTask.DECREMENT_LIKES) {
//
//        } else {
//            System.err.println("Invalid database task, found " + String.valueOf(databaseTask));
//        }
//    }

    public static Result throw404() {
        return notFound();
    }

}
