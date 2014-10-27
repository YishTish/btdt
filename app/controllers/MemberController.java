package controllers;

import java.util.List;








import javax.persistence.PersistenceException;

import org.apache.commons.lang3.RandomStringUtils;

import actions.CorsComposition;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import models.Member;
import play.Logger;
import play.Logger.ALogger;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Results;
import play.mvc.Controller;
import play.mvc.BodyParser;
import play.mvc.With;
import utilities.JsonValidator;

@With(CorsComposition.CorsAction.class)
public class MemberController extends Controller {
	
	private static final ALogger logger = Logger.of(MemberController.class);

	
	public static Result getAllMembers() {
		logger.debug("getting all members");
		List<Member> members = Member.find.all();
		return Results.ok(Json.toJson(members));
	}
	
	public static Result getMemberById(Integer memberId){
		logger.error("sdsd");
		Member member = Member.find.byId(memberId);
		if(member!= null){
			return Results.ok(Json.toJson(member));
		}
		else return Results.ok();
	}
	
	public static Result getMemberByEmail(String email){
		return Results.ok(Json.toJson(Member.find.where().eq("email",email).findUnique()));
	}
	
	public static Result Cors(){
		return Results.ok("cors enabled method");
	}
	
	
	@BodyParser.Of(BodyParser.Json.class)
	public static Result insertMember(){
		JsonNode body = request().body().asJson();
		if(body != null){
			
			String[] fields = {"fname","lname","email","type"};
			List<String> errors = JsonValidator.validateFieldsExist(body,fields);
			if(errors.size() > 0){
				return Results.badRequest(Json.toJson(errors));
			}
			
			String fname = body.findPath("fname").textValue();
			String lname = body.findPath("lname").asText();
			String email = body.findPath("email").asText();
			if(email == null || "".equals(email)){
				return badRequest("Email field is mandatory");
			}
			if(!checkEmailUnique(email))
			{
				return badRequest("Member with this email address already exists");
			}
			String password = body.findPath("password").asText();
			if(password == null || "".equals(password)){
				password = RandomStringUtils.randomAlphanumeric(10);
			}
			Integer type= body.findPath("type").asInt();
			if(type== null){
				type = 0;
			}
				
			Member member = new Member(fname, lname,email,password,type);
			try{
				member.save();
				if(member.getId() != null && member.getId() > 0){
					 ObjectNode response = Json.newObject();
					    response.put("id", member.getId());
					    response.put("password", password);
					return Results.ok(response);
				}
				else
					return Results.internalServerError("Failed to create user");
			}
			catch(PersistenceException pe){
				return Results.internalServerError("Failed to create user: "+pe.getMessage());
			}
		
		}
		else{
			return badRequest("Received an empty or invalid Member structure");
		}
		
	}
	
	@BodyParser.Of(BodyParser.Json.class)
	public static Result updateMember(){
		JsonNode body = Controller.request().body().asJson();
		
		if(body != null){
			
			String[] fields = {"id","fname","lname","email","type"};
			List<String> errors = JsonValidator.validateFieldsExist(body,fields);
			if(errors.size() > 0){
				return Results.badRequest(Json.toJson(errors));
			}
			
			Integer id = body.findPath("id").asInt();
			Member member = Member.find.byId(id);
			if(member == null){
				return badRequest("Trying to update a member that doesn't exist");
			}
			String fname = body.findPath("fname").asText();
			if(fname != null ){
				member.setFname(fname);
			}
			String lname = body.findPath("lname").asText();
			if(lname != null){
				member.setLname(lname);
			}
			String email = body.findPath("email").asText();
			if(!"".equals(email)){
				if(!email.equals(member.getEmail()) && !checkEmailUnique(email)){
					return badRequest("Update failed. Trying to update with an email address that is already registered");
				}
				else{
					member.setEmail(email);
				}
			}
			String password = body.findPath("password").asText();
			if(password != null){
				member.setPassword(password);
			}
			Integer type= body.findPath("type").asInt();
			if(type	!= null){
				member.setType(type);
			}
			try{	
				member.update();
				return Results.ok();
			}
			catch(PersistenceException pe){
				return internalServerError(pe.getMessage());
			}
		}
		else{
			return badRequest("Received an empty or invalid Member structure");
		}
		
	}
	
	public static Result deleteMember(Integer memberId){
		System.out.println("got to deleting member method");
		Member member = Member.find.byId(memberId);
		try{
			member.delete();
		}
		catch(PersistenceException pe){
			return Results.internalServerError("Failed to delete member: "+ pe.getMessage());
		}
		return Results.ok("Member deleted successfully");
	}
	
	private static boolean checkEmailUnique(String email){
		int numOfMembers = Member.find.where().eq("email",email).findRowCount();
		return !(numOfMembers > 0);
	}
	
	@BodyParser.Of(BodyParser.Json.class)
	public static Result login(){
		JsonNode body = Controller.request().body().asJson();
		Integer id = body.findPath("id").asInt();
		String password = body.findPath("password").asText();
		
		if(password==null || "".equals(password)){
			return Results.badRequest("Both Email and password fields are mandatory");
		}
		Member member;
		if(id == null || id.equals(0)){
			String email = body.findPath("email").asText();
			if(email==null || "".equals(email)){
				return Results.badRequest("Both Email and password fields are mandatory");
			}
			member = Member.find.where().eq("email",email).findUnique();
		}
		else{
			member = Member.find.byId(id);	
		}
		
		if(member != null && member.checkPassword(password)){
			return Results.ok(Json.toJson(member));
		}
		else
			return Results.badRequest("Could not find a member with the given credentials");
		
	}

}
