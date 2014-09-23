package controllers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.PersistenceException;

import models.ActivityType;

import com.fasterxml.jackson.databind.JsonNode;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;
import utilities.JsonValidator;

public class ActivityTypeController extends Controller {
	
	public static Result insertActivityType(){
		JsonNode activityTypeNode = request().body().asJson();
		String[] fields = {"name","description"};
		List<String> errors = JsonValidator.validateFieldsExist(activityTypeNode,fields);
		if(errors.size() > 0){
			return Results.badRequest(Json.toJson(errors));
		}
		String name = activityTypeNode.findValue("name").asText();
		if(name == null || "".equals(name)){
			return Results.badRequest("Activity Type must have a name.");
		}
		String description = activityTypeNode.findValue("description").asText();
		boolean active = true;

		JsonNode activeNode= activityTypeNode.findValue("active");
		if(activeNode!=null){
			active = activeNode.asBoolean();
		}
		ActivityType activityType = new ActivityType(name, description,active);
		try{
			activityType.save();
			return Results.ok(Json.toJson(activityType));
		}
		catch(PersistenceException pe){
			return Results.internalServerError(pe.getMessage());
		}
		
	}
	
	public static Result updateActivityType(){
		JsonNode activityTypeNode = request().body().asJson();
		
		String[] fields = {"id", "name","description"};
		List<String> errors = JsonValidator.validateFieldsExist(activityTypeNode,fields);
		if(errors.size() > 0){
			return Results.badRequest(Json.toJson(errors));
		}
		
		
		int id = activityTypeNode.findValue("id").asInt();
		
		ActivityType activityType = ActivityType.find.byId(id);
		if(activityType == null){
			return Results.badRequest("Could not find an Activity type with the id requested. Aborted upload");
		}
		
		String name = activityTypeNode.findValue("name").asText();
		if(name != null && !"".equals(name)){
			activityType.name = name;
		}
		
		String description = null;
		JsonNode descriptionNode = activityTypeNode.findValue("description");
		if(descriptionNode!= null){
			description = descriptionNode.asText();
		}
		
		if(description != null && !"".equals(description)){
			activityType.description = description;
		}
		
		JsonNode activeNode = activityTypeNode.findValue("active");
		if(activeNode!= null){
			activityType.active=activeNode.asBoolean();
		}
		try{
			activityType.update();
			return Results.ok(Json.toJson(activityType));
		}
		catch(PersistenceException pe){
			return Results.internalServerError(pe.getMessage());
		}
	}
	
	public static Result inactivateActivityType(int id){
		ActivityType activityType = ActivityType.find.byId(id);
		if(activityType == null){
			return Results.badRequest("Could not find an Activity type with the id requested.");
		}
		activityType.active=false;
		activityType.update();
		return Results.ok(Json.toJson(activityType));
	}
	
	public static Result activateActivityType(int id){
		ActivityType activityType = ActivityType.find.byId(id);
		if(activityType == null){
			return Results.badRequest("Could not find an Activity type with the id requested.");
		}
		activityType.active=true;
		activityType.update();
		return Results.ok(Json.toJson(activityType));
	}
	
	public static Result getActivityType(int id){
		ActivityType activityType = ActivityType.find.byId(id);
		if(activityType == null){
			return Results.badRequest("Could not find an Activity type with the id requested. Aborted upload");
		}
		return Results.ok(Json.toJson(activityType));
	}
	
	public static Result getAllActivityTypes(){
		List<ActivityType> activityType = ActivityType.find.all();
		return Results.ok(Json.toJson(activityType));
	}
	
}
