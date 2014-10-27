package controllers;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;

import org.apache.commons.lang3.RandomStringUtils;

import models.Location;
import actions.CorsComposition;

import com.fasterxml.jackson.databind.JsonNode;

import play.mvc.Result;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Results;
import play.mvc.With;
import utilities.JsonValidator;

@With(CorsComposition.CorsAction.class)
public class LocationController extends Controller {
	
	public static Result insertLocation() {
		
		JsonNode locationNode = request().body().asJson();
		
		String[] fields = {"name","description","longitude","latitude"};
		List<String> errors = JsonValidator.validateFieldsExist(locationNode,fields);
		if(errors.size() > 0){
			return Results.badRequest(Json.toJson(errors));
		}
		Location location = populateObject(new Location(), locationNode);
		List<String> errorMessages = validateMandatory(location);
		if(errorMessages.size() > 0){
			return badRequest(Json.toJson(errorMessages));
		}

		try{
			location.save();
			return Results.ok(Json.toJson(location));
		}
		catch(PersistenceException pe){
			return Results.internalServerError(pe.getMessage());
		}
	}
	
	private static List<String> validateMandatory(Location location ){
		String name = location.name;
		Double longitude = location.longitude;
		Double latitude = location.latitude;
		
		List<String> errorMessages= new ArrayList<>();
		if(name == null || "".equals(name)){
			errorMessages.add("Location name is required.");
		}
		if(longitude==null || longitude.equals(0)){
			errorMessages.add("Longitude is required.");
		}
		if(latitude==null || latitude.equals(0)){
			errorMessages.add("Latitude is required.");
		}
		return errorMessages;
	}
	
	private static String generateCode() {
		String locationCode = RandomStringUtils.randomAlphanumeric(25);
		Location location = Location.find.where().eq("code",locationCode).findUnique();
		if(location != null){
			return generateCode();
		}
		return locationCode;
	}

	private static Location populateObject(Location location, JsonNode locationNode){
		String locationName;
		String locationDescription;
		Double locationLongitude;
		Double locationLatitude;
		String locationCode;
		
		if(location == null){
			location = new Location();
		}
		
		locationName = locationNode.findPath("name").asText();
		if(locationName != null){
			location.name = locationName;
		}
		
		locationDescription = locationNode.findPath("description").asText();
		if(locationDescription != null ){
			location.description = locationDescription;
		}
		locationLongitude = locationNode.findValue("longitude").asDouble();
		if(locationLongitude != null && locationLongitude.doubleValue() > 0){
			location.longitude = locationLongitude;
		}
		locationLatitude = locationNode.findValue("latitude").asDouble();
		if(locationLatitude != null && locationLatitude.doubleValue() > 0){
			location.latitude = locationLatitude;
		}
		JsonNode locationCodeJson = locationNode.findValue("code");
		if(locationCodeJson != null){
			location.code = locationCodeJson.textValue();
		}
		else{
			if(location.code==null || "".equals(location.code)){
				location.code = generateCode();
			}
		}
		return location;
	}
	
	
	public static Result updateLocation(){
		JsonNode locationNode = request().body().asJson();
		String[] fields = {"id", "name","description","longitude","latitude"};
		List<String> errors = JsonValidator.validateFieldsExist(locationNode,fields);
		if(errors.size() > 0){
			return Results.badRequest(Json.toJson(errors));
		}
		Integer locationId = locationNode.findPath("id").asInt();
		Location location = Location.find.byId(locationId);
		String noIdError = null;
		if(location == null){
			noIdError = "Could not find Locatoin with the Id provided";
			return Results.badRequest(Json.toJson(noIdError));
		}
		else{
			location = populateObject(location, locationNode);
		}
		List<String> errorMessages = validateMandatory(location);
		
		if(errorMessages!= null && errorMessages.size() > 0){
			return Results.badRequest(Json.toJson(errorMessages));
		}
		try{
			location.update();
		}
		catch(PersistenceException pe){
			Results.internalServerError(pe.getMessage());
		}
		return Results.ok(Json.toJson(location));
	}
	
	public static Result deleteLocation(Integer id){
		Location location = Location.find.byId(id);
		if(location == null){
			return Results.badRequest("No Location found with the Id provided");
		}
		try{
			location.delete();
			return Results.ok();
		}
		catch(PersistenceException pe){
			return Results.internalServerError("Failed to delete location: "+pe.getMessage());
		}
	}
	
	public static Result getAllLocations(){
		List<Location> locatoins = Location.find.all();
		return Results.ok(Json.toJson(locatoins));
	}
	
	public static Result generateLocationCode(Integer id){
		Location location = Location.find.byId(id);
		if(location == null){
			return Results.badRequest("No Location found with the Id provided");
		}
		String newCode = generateCode();
		location.code = newCode;
		try{
			location.update();
		}
		catch(PersistenceException pe){
			return Results.internalServerError(pe.getMessage());
		}
		return Results.ok(Json.toJson(location));
	}
	
	public static Result getLocation(Integer id){
		Location location = Location.find.byId(id);
		return Results.ok(Json.toJson(location));
	}
	
	public static Result getLocationByCode(String code){
		Location location = Location.find.where().eq("code",code).findUnique();
		return Results.ok(Json.toJson(location));
	}
	
	public static Result getLocationsByName(String name){
		List<Location> locations = Location.find.where().like("name","%"+name+"%").findList();
		return Results.ok(Json.toJson(locations));
	}
	
}


