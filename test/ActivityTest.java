import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static play.test.Helpers.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import models.Activity;
import models.ActivityType;
import models.Location;
import models.Member;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import akka.actor.Status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import play.libs.Json;
import play.mvc.Result;

public class ActivityTest {

	Integer id=0;
	//ActivityType activityType = new ActivityType("A Test activity type","Not a real activity type");
	Activity activity;
	ActivityType activityType;
	Date localTime;
	

	@Before
	public void setUp(){
		start(fakeApplication());
		localTime = new Date();
		String email = "email@test.com";
		List<Member> members = Member.find.where().eq("email",email).findList();
		if(members !=null){
			for(Member member : members){
				member.delete();
			}
		}
		String locationCode = "assafds";
		List<Location> locations = Location.find.where().eq("code", locationCode).findList();
		for(Location location : locations){
			location.delete();
		}
		
		String comments = "Test comments for test activity";
		Member member = new Member("fname","lname","email@test.com","password",2);
		member.save();
		Location location = new Location("locName", "locDesc",1.234, 432.1, locationCode);
		location.save();
		ActivityType activityType = new ActivityType("testTyime", "test description");
		activity = new Activity(member, activityType,localTime, location, comments);
		
	}
	
	@After
	public void close(){
		if(id != null && id > 0){
			Activity deadActivity = Activity.find.byId(id);
			if(deadActivity != null){
				Member member = deadActivity.getMember();
				Location location = deadActivity.getLocation();
				ActivityType activityType = deadActivity.getActivityType();
				deadActivity.delete();
				if(member!=null) member.delete();
				if(location!=null) location.delete();
				if(activityType!=null) activityType.delete();
			}
		}
		stop(fakeApplication());
	}
	
	private void insertActivity(){
			activity.save();
			id = activity.getId();
	}
	
	
	
	
	@Test
	public void testInsertActivity(){
		insertActivity();
		assertTrue("Testing that activity was created",(id != null && id > 0));
	}
	
	@Test 
	public void testUpdateActivity(){
		insertActivity();
		Activity activity = Activity.find.byId(id);
		String newComments = "Testing update";
		activity.setComments(newComments);
		activity.update();
		
		Activity newActivity = Activity.find.byId(id);
		assertTrue("Testing activity update - updated comments",newActivity.getComments().equals(newComments));
	}
	
	@Test
	public void testGetActivitiesByMember(){
		insertActivity();
		Member newMember = new Member("fname","lname","activityTest@email.com","password",3);
		newMember.save();
		ActivityType newActivityType = new ActivityType("test type 2","test description 2");
		newActivityType.save();
		Activity newActivity = new Activity(newMember, newActivityType, localTime,activity.getLocation(), "No comments");
		newActivity.save();

		List<Activity> memberActivities = Member.find.byId(newMember.getId()).getActivities();
		assertTrue("testing fetching activities according to member - non-empty list retrieved", (memberActivities != null && memberActivities.size() > 0 ));
		
		for(Activity activity : memberActivities){
			assertTrue("Activity retrieved by Memeber (id "+newMember.getId()+") equals member "
					+ "id retrieved ("+activity.getMember().id+")",activity.getMember().id
					.equals(newMember.id));
		}
		
		newActivity.delete();
		newMember.delete();
		newActivityType.delete();
	}
	
	@Test
	public void testGetActivitiesByLocation(){
		insertActivity();
		Location newLocation = new Location("testTempLocation","A test location",0.222,2.122,"zdfzxxz");
		newLocation.save();
		ActivityType newActivityType = new ActivityType("test type 2","test description 2");
		newActivityType.save();
		Activity newActivity = new Activity(activity.getMember(), newActivityType,localTime, newLocation, "No Comments");
		newActivity.save();

		List<Activity> locationActivities = Location.find.byId(newLocation.id).activities;
		
		assertTrue("testing fetching activities according to location - list is null", (locationActivities != null));
		assertTrue("testing fetching activities according to location id "+newLocation.id+" - list is empty", locationActivities.size() > 0 );
		
		for(Activity activity : locationActivities){
			assertTrue("activity retrieved has the same location ("+activity.getLocation().id+") as the one requested( "+newLocation.id+" ).", newLocation.id.equals(activity.getLocation().id));
		}
		
		newActivity.delete();
		newLocation.delete();
		newActivityType.delete();
	}
	
	@Test
	public void testDeleteActivity(){
		//id = null;
		insertActivity();
		int tempId = id;
		Activity.find.byId(tempId).delete();
		Activity deadActivity = Activity.find.byId(tempId);
		assertNull(deadActivity);
		
	}
	
	@Test
	public void testInsertActivityByController(){
		
		String email = "ActivityTestEamil@email.com";
		Member oldMember = Member.find.where().eq("email",email).findUnique();
		if(oldMember != null) 
			oldMember.delete();
		Member member = new Member("ActivityTestFname", "ActivityTestLname",email,"ActivityTestPassword",3);
		member.save();
		
		String locationName = "ActivityTestName";
		Location oldLocation = Location.find.where().eq("name", locationName).findUnique();
		if(oldLocation != null) 
			oldLocation.delete();
		
		Location location = new Location(locationName,"ActivityTest Description",1.2345, 3.12121,"ActivityTestCode");
		location.save();
		
		String activityTypeName =  "ActivityTestNAme";
		ActivityType oldAT = ActivityType.find.where().eq("name", activityTypeName).findUnique();
		if(oldAT != null){
			oldAT.delete();
		}
		
		ActivityType activityType = new ActivityType(activityTypeName, "ActivityTestDescription");
		activityType.save();
		String date = "2014-08-08 12:34:33";
		String comments = "comments";

		List<Activity> oldActivities = Activity.find.where().eq("comments", comments).findList();
		for(Activity oldActivity : oldActivities){
			oldActivity.delete();
		}
		
		ObjectNode activityJson = Json.newObject();
		activityJson.put("member",member.id);
		activityJson.put("location", location.id);
		activityJson.put("activityType", activityType.id);
		activityJson.put("date", date);
		activityJson.put("comments", comments);
		
		Result result = callAction(controllers.routes.ref.ActivityController.insertActivity(), 
				fakeRequest().withHeader("Conent-Type","application/json").withJsonBody(activityJson));
		
		
		assertTrue("Insert Activty via controller should return 200",status(result) == OK);
		
		JsonNode responseJson = Json.parse(contentAsString(result));
    	Integer activityId = responseJson.findPath("id").asInt();
    	assertTrue("Insert Activity via controller should return created member", activityId > 0);
    	
    	Activity.find.byId(activityId).delete();
    	member.delete();
    	location.delete();
    	activityType.delete();
	}
	
	public void testUpdateActivityByController(){
		
		String date = "2014-08-18 23:22:11";
		String comments = "new Comments";
		
		ObjectNode activityJson = Json.newObject();
		activityJson.put("member",id);
		activityJson.put("location", activity.getLocation().id);
		activityJson.put("activityType", activity.getActivityType().id);
		activityJson.put("date", date);
		activityJson.put("comments", comments);
		
		Result result = callAction(controllers.routes.ref.ActivityController.updateActivity(), 
				fakeRequest().withHeader("Conent-Type","application/json").withJsonBody(activityJson));
		
		assertTrue("Update Activty via controller should return 200",status(result) == OK);
		JsonNode responseJson = Json.parse(contentAsString(result));
		String retrievedDate = responseJson.get("date").asText();
		String retrievedComments = responseJson.get("comments").asText();
		
		System.out.println("retreived date: "+retrievedDate);
		System.out.println("retreived comments: "+retrievedComments);
	}
	
	public void testDeleteActivityByController(){
		Member member = new Member("ActivityTestFname", "ActivityTestLname","ActivityTestEamil@email.com","ActivityTestPassword",3);
		member.save();
		Location location = new Location("ActivityTestName","ActivityTest Description",1.2345, 3.12121,"ActivityTestCode");
		location.save();
		ActivityType activityType = new ActivityType("ActivityTestNAme", "ActivityTestDescription");
		activityType.save();
		String comments = "comments";
		
		Activity activity = new Activity(member,activityType,localTime,location,comments);
		activity.save();
		int activityId = activity.getId();
		Result result = callAction(controllers.routes.ref.ActivityController.deleteActivity(activityId), 
				fakeRequest());
		
		assertTrue("Delete activity via controller" , status(result) == OK);
		
		Activity deadActivity = Activity.find.byId(activityId);
		assertTrue("testing deleted activity via controller has been deleted", deadActivity==null);
		
		member.delete();
		location.delete();
		activityType.delete();
		
	}
	
	public void testGetAllActivitiesByController(){
		int numOfActivities = Activity.find.findRowCount();
		Result result = callAction(controllers.routes.ref.ActivityController.getAllActivities(), 
				fakeRequest());
		JsonNode jsonResponse = Json.parse(contentAsString(result));
		assertTrue("Retreiving all activities, should be same number through controller and model",jsonResponse.size() == numOfActivities);
		
		
	}
	
	public void testGetActivityByController(){
		Result result = callAction(controllers.routes.ref.ActivityController.getActivity(id), 
				fakeRequest());
		assertTrue("Call to retrieve specific activity via controller",status(result)== OK);
		JsonNode jsonResponse = Json.parse(contentAsString(result));
		
		int retrievedActivityId = jsonResponse.findValue("id").asInt();
		assertTrue("Retrieve activty via controller. Retrieved id should be the same as the inital id", retrievedActivityId==id);
		
	}
	
	
}