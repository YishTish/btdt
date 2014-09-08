package controllers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import models.Location;

import com.fasterxml.jackson.databind.JsonNode;
import play.mvc.Result;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Results;

public class LocationController extends Controller {
	
	public static Result createLocation() {
		JsonNode locationNode = request().body().asJson();
		String locationName = locationNode.findPath("name").asText();
		String locationDescription = locationNode.findPath("description").asText();
		Double locationLongitude = locationNode.findPath("longitude").asDouble();
		Double locationLatitude = locationNode.findPath("latitude").asDouble();
		String locationCode = locationNode.findPath("code").asText();
		
		List<String> errorMessages = validateMandatory(locationName, locationLongitude, locationLatitude);
		if(errorMessages.size() > 0){
			return badRequest(Json.toJson(errorMessages));
		}
		if(locationCode==null || "".equals(locationCode)){
			locationCode = generateCode();
		}
		
		Location location = new Location(locationName,locationDescription,locationLongitude, locationLatitude, locationCode);
		location.save();
		return Results.ok(Json.toJson(location));
	}
	
	private static List<String> validateMandatory(String name, Double longitude, Double latitude ){
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

	public static Result updateLocation(){
		JsonNode locationNode = request().body().asJson();
		Integer locationId = locationNode.findPath("id").asInt();
		Location location = Location.find.byId(locationId);
		if(location == null){
			return Results.badRequest("Could not find Locatoin with the Id provided");
		}
		
		String  locationName = locationNode.findPath("name").asText();
		if(locationName != null && !"".equals(locationName)){
			location.name = locationName;
		}
		
		String  locationDescription = locationNode.findPath("description").asText();
		if(locationDescription != null && !"".equals(locationDescription)){
			location.description = locationDescription;
		}
		Double  locationLongitude = locationNode.findPath("longitude").asDouble();
		if(locationLongitude != null && locationLongitude.intValue() > 0){
			location.longitude = locationLongitude;
		}
		Double  locationLatitude = locationNode.findPath("latitude").asDouble();
		if(locationLatitude != null && locationLatitude.intValue() > 0){
			location.latitude = locationLatitude;
		}
		String  locationCode = locationNode.findPath("code").asText();
		if(locationCode != null && !"".equals(locationCode)){
			location.code = locationCode;
		}
		location.update();
		return Results.ok(Json.toJson(location));
	}
	
	public static Result deleteLocation(Integer id){
		Location location = Location.find.byId(id);
		if(location == null){
			return Results.badRequest("No Location found with the Id provided");
		}
		location.delete();
		return Results.ok();
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
		location.update();
		return Results.ok(Json.toJson(location));
	}
	
	public static Result getLocation(Integer id){
		Location location = Location.find.byId(id);
		if(location == null){
			return Results.badRequest("No Location found with the Id provided");
		}
		return Results.ok(Json.toJson(location));
	}
	
	public static Result getLocationByCode(String code){
		Location location = Location.find.where().eq("code",code).findUnique();
		if(location == null){
			return Results.badRequest("No Location found with the code provided");
		}
		return Results.ok(Json.toJson(location));
	}
	
	public static Result getLocationsByName(String name){
		List<Location> locations = Location.find.where().like("code","%"+name+"%").findList();
		if(locations == null || locations.size()==0){
			return Results.badRequest("No Location found matching the name provided");
		}
		return Results.ok(Json.toJson(locations));
	}
	
}


