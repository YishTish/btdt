package controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.persistence.PersistenceException;

import models.Activity;
import models.ActivityType;
import models.Location;
import models.Member;
import actions.CorsComposition;

import com.fasterxml.jackson.databind.JsonNode;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;
import play.mvc.With;
import utilities.JsonValidator;

@With(CorsComposition.CorsAction.class)
public class ActivityController extends Controller{
	
	static Result result;
	static String errorMesage;
	
	public static Result insertActivity(){
		JsonNode activityNode = request().body().asJson();
		
		String[] fields = {"activityType","comments","date","member"};
		List<String> errors = JsonValidator.validateFieldsExist(activityNode,fields);
		if(errors.size() > 0){
			return Results.badRequest(Json.toJson(errors));
		}
		
		Activity activity = generateActivityFromJson(activityNode, new Activity());
		if(activity == null){
			return result;
		}
		try{
			activity.save();
		}
		catch(PersistenceException pe){
			return Results.internalServerError(pe.getMessage());
		}
		
		return Results.ok(Json.toJson(activity));
		
	}
	
	public static Result updateActivity(){
		
		JsonNode activityNode = request().body().asJson();
		
		String[] fields = {"id", "activityType","comments","date","member"};
		List<String> errors = JsonValidator.validateFieldsExist(activityNode,fields);
		if(errors.size() > 0){
			return Results.badRequest(Json.toJson(errors));
		}
		
		int activityId = activityNode.findValue("id").intValue();
		if(activityId == 0){
			return Results.badRequest("Invalid activity id sent. Cannot update activity");
		}
		Activity activity = Activity.find.byId(activityId);
		
		if(activity == null){
			return Results.badRequest("Invalid activity id sent. Cannot update activity");
		}
		
		Activity updatedActivity = generateActivityFromJson(activityNode, activity);
		if(updatedActivity == null){
			return result;
		}
		
		try{
			updatedActivity.update();
		}
		catch(PersistenceException pe){
			return Results.internalServerError("Activty Update failure. "+pe.getMessage());
		}
		
		return Results.ok(Json.toJson(activity));
	}
	
	public static Result getAllActivities(){
		List<Activity> activities = Activity.find.all();
		return Results.ok(Json.toJson(activities));
	}
	
	public static Result getActivity(int id){
		Activity activity = Activity.find.byId(id);
		if(activity!= null){
			return Results.ok(Json.toJson(activity));
		}
		return Results.badRequest("Activity id "+id+" not found.");
	}
	
	public static Result deleteActivity(int id){
		Activity.find.byId(id).delete();
		return Results.ok();
	}
	
	private static Activity generateActivityFromJson(JsonNode activityJson, Activity activity){
		
		if(activity == null){
			activity = new Activity();
		}
		int memberId= activityJson.findValue("member").intValue();
		if(memberId== 0){
			errorMesage= "Could not generate activity object without a valid member";
			result = Results.badRequest(errorMesage);
			return null;
		}
		Member member = Member.find.byId(memberId);
		activity.setMember(member);

		int locationId = activityJson.findValue("location").intValue();
		if(locationId != 0){
			Location location = Location.find.byId(locationId);
			activity.setLocation(location);
		}
		
		int activityTypeId = activityJson.findValue("activityType").intValue();
		if(activityTypeId == 0){
			errorMesage = "Could not generate activity object without a valid type";
			result = Results.badRequest(errorMesage);
			return null;
		}
		ActivityType activityType = ActivityType.find.byId(activityTypeId);
		activity.setActivityType(activityType);
		
		String date = activityJson.findValue("date").textValue();
		if(date == null){
			errorMesage = "Could not generate activity object without a valid date";
			result = Results.badRequest(errorMesage);
			return null;
		}
		Date activityDate = null;
		try {
			activityDate = new SimpleDateFormat("yyyy-MM-dd H:mm:ss", Locale.ENGLISH).parse(date);
		} catch (ParseException e) {
			errorMesage = "Could not generate activity object without a valid date";
			result = Results.badRequest(errorMesage);
		} 
		activity.setDate(activityDate);
		
		String comments = activityJson.findValue("comments").textValue();
		if(comments != null){
			activity.setComments(comments);
		}
		
		return activity;
	}
	

}
