import static org.junit.Assert.*;
import static play.mvc.Http.Status.BAD_REQUEST;
import static play.test.Helpers.callAction;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.status;

import java.util.Date;

import models.Activity;
import models.ActivityType;
import models.Location;
import models.Member;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import play.libs.Json;
import play.mvc.Result;
import static play.test.Helpers.*;

public class ActivityTypeTest {
	
	int id;
	String name;
	String description;
	
	@Before
	public void setUp(){
		start(fakeApplication());
		name = "testActivityType";
		description = "testActivityType description";
		
		ActivityType existingAT = new ActivityType(name, description);
		
		ActivityType oldAT = ActivityType.find.where().exampleLike(existingAT).findUnique();
		if(oldAT != null)
			oldAT.delete();
	}
	
	@After
	public void close(){
		if(id > 0){
			ActivityType.find.byId(id).delete();
		}
		stop(fakeApplication());
	}
	
	@Test
	public void testInsertActivityType(){
		ActivityType activityType = new ActivityType(name, description);
		activityType.save();
		assertTrue("direct save should create new id which should be > 0", activityType.id > 0);
		activityType.delete();
		ObjectNode aTypeJson = Json.newObject();
		aTypeJson.put("name", name);
		aTypeJson.put("description", description);
		Result result = callAction(controllers.routes.ref.ActivityTypeController.insertActivityType(), 
				fakeRequest().withHeader("Conent-Type","application/json").withJsonBody(aTypeJson)
				);
		assertTrue("Inserting ActivityType through controller should return 200. response: "+contentAsString(result), status(result)==OK);
		JsonNode jsonResult = Json.parse(contentAsString(result));
		int newId = jsonResult.findValue("id").asInt();
		assertTrue("Inserting ActivityType through controller should create new id",newId > 0);
		ActivityType.find.byId(newId).delete();
	}
	
	@Test
	public void testUpdateActivityType(){
		ActivityType origAT = new ActivityType(name, description);
		origAT.save();
		
		String newName = name+" name test";
		String newDescription = description+ " description test";
		ObjectNode aTypeJson = Json.newObject();
		aTypeJson.put("id",origAT.id);
		aTypeJson.put("name",newName);
		aTypeJson.put("description", newDescription);
		Result result = callAction(controllers.routes.ref.ActivityTypeController.updateActivityType(), 
				fakeRequest().withHeader("Conent-Type","application/json").withJsonBody(aTypeJson)
				);
		assertTrue("Updating ActivityType through controller should return 200",status(result)==OK);
		JsonNode jsonResult = Json.parse(contentAsString(result));
		assertTrue("Updateing via controller, id should remain the same", jsonResult.get("id").asInt() == origAT.id);
		assertTrue("Updateing via controller, name should change", jsonResult.get("name").asText().equals(newName));
		assertTrue("Updateing via controller, description should change", jsonResult.get("description").asText().equals(newDescription));
		
		ActivityType.find.byId(origAT.id).delete();
	}
	
	@Test
	public void testRetrieveActivityType(){
		ActivityType at = new ActivityType(name, description);
		at.save();
		
		Result result = callAction(controllers.routes.ref.ActivityTypeController.getActivityType(at.id), 
				fakeRequest()
				);
		
		JsonNode aTypeJson = Json.parse(contentAsString(result));
		String retreivedName = aTypeJson.findValue("name").asText();
		String retreivedDescription = aTypeJson.findValue("description").asText();
		
		assertTrue("retreived activityType by id should retrieve the right name", retreivedName.equals(name));
		assertTrue("retreived activityType by id should retrieve the right description", retreivedDescription.equals(description));
		
		at.delete();
	}
	
	@Test
	public void testRetrieveAllActivityTypes(){
		
		int modelCount = ActivityType.find.findRowCount();
		Result result = callAction(controllers.routes.ref.ActivityTypeController.getAllActivityTypes(), 
				fakeRequest()
				);
		JsonNode aTypeJson = Json.parse(contentAsString(result));
		assertTrue("comparing retrieve through model and through controller should yeild the same number", aTypeJson.size()==modelCount);
		
		
	}
		
	@Test
	public void testActivateActivityType(){
		ActivityType at = new ActivityType(name, description, false);
		at.save();
		
		int id = at.id;
		assertTrue("Create ActivityType with active=false should set active to false", at.active==false);
		Result result = callAction(controllers.routes.ref.ActivityTypeController.activateActivityType(id), 
				fakeRequest()
				);
		JsonNode aTypeJson = Json.parse(contentAsString(result));
		assertTrue("Activate account via controller should set active to true", aTypeJson.findValue("active").asBoolean()==true);
		ActivityType.find.byId(id).delete();
	}
	
	@Test
	public void testUnactivateActivityType(){
		ActivityType at = new ActivityType(name, description, true);
		at.save();
		
		int id = at.id;
		assertTrue("Create ActivityType with active=false should set active to false", at.active==true);
		Result result = callAction(controllers.routes.ref.ActivityTypeController.inactivateActivityType(id), 
				fakeRequest()
				);
		JsonNode aTypeJson = Json.parse(contentAsString(result));
		assertTrue("Activate account via controller should set active to true", aTypeJson.findValue("active").asBoolean()==false);
		ActivityType.find.byId(id).delete();
		
	}

}


