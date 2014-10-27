package controllers;

import actions.CorsComposition;
import play.*;
import play.mvc.*;
import views.html.*;

@With(CorsComposition.CorsAction.class)
public class Application extends Controller {

    public static Result index() {
        return ok(index.render("Your new application is ready."));

    }
    
    public static Result newPage() {
        return ok(testMe.render("This is the title", "<h3>This is the body</h3>"));

    }
    
    public static Result preflight(String path){
	    return ok();
    }
}