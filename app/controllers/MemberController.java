package controllers;

import java.util.Iterator;
import java.util.List;



import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;

import com.fasterxml.jackson.core.JsonParser;
//import com.fasterxml.jackson.databind.JsonNode;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import models.Member;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Results;
import play.mvc.Controller;
import play.mvc.BodyParser;

public class MemberController extends Controller {
	
	public static Result getAllMembers() {
		
		List<Member> members = Member.find.all();
		return Results.ok(Json.toJson(members));
	}
	
	public static Result getMemberById(Integer memberId){
		Member member = Member.find.byId(memberId);
		return Results.ok(Json.toJson(member));
	}
	
	public static Result getMemberByEmail(String email){
		return Results.ok(Json.toJson(Member.find.where().eq("email",email).findUnique()));
	}
	
	
	@BodyParser.Of(BodyParser.Json.class)
	public static Result createMember(){
		JsonNode body = request().body().asJson();
		if(body != null){
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
		else{
			return badRequest("Received an empty or invalid Member structure");
		}
		
	}
	
	@BodyParser.Of(BodyParser.Json.class)
	public static Result updateMember(){
		JsonNode body = Controller.request().body().asJson();
		if(body != null){
			Integer id = body.findPath("id").asInt();
			Member member = Member.find.byId(id);
			if(member == null){
				return badRequest("Trying to update a member that doesn't exist");
			}
			String fname = body.findPath("fname").asText();
			if(fname != null || !"".equals(fname)){
				member.setFname(fname);
			}
			String lname = body.findPath("lname").asText();
			if(lname != null || !"".equals(lname)){
				member.setLname(lname);
			}
			String email = body.findPath("email").asText();
			if(fname != null || !"".equals(email)){
				if(!email.equals(member.getEmail()) && !checkEmailUnique(email)){
					return badRequest("Update failed. Trying to update with an email address that is already registered");
				}
				else{
					member.setEmail(email);
				}
			}
			String password = body.findPath("password").asText();
			if(password != null || !"".equals(password)){
				member.setPassword(password);
			}
			Integer type= body.findPath("type").asInt();
			if(type	!= null){
				member.setType(type);
			}
				
			member.update();
			return Results.ok();
		}
		else{
			return badRequest("Received an empty or invalid Member structure");
		}
		
	}
	
	public static Result deleteMember(Integer memberId){
		Member member = Member.find.byId(memberId);
		member.delete();
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
