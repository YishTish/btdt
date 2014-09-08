import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.start;
import static play.test.Helpers.stop;

import java.util.Date;
import java.util.List;

import models.Activity;
import models.ActivityType;
import models.Location;
import models.Member;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ActivityTest {

	Integer id=0;
	ActivityType activityType = new ActivityType("A Test activity type","Not a real activity type");
	Member member = new Member("fname","lname","email@test.com","password",2);
	Location location = new Location("locName", "locDesc",1.234, 432.1, "assafds ");
	Date date = new Date();
	Activity activity= new Activity();
	String comments = "Test comments for test activity";
	

	@Before
	public void setUp(){
		start(fakeApplication());
	}
	
	@After
	public void close(){
		if(id != null && id > 0){
			Activity.find.byId(id).delete();
		}
		stop(fakeApplication());
	}
	
	private void insertActivity(){
			activity.setActivityType(activityType);
			activity.setMember(member);
			activity.setDate(date);
			activity.setLocation(location);
			activity.setComments(comments);
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
		Member origMember = member;
		Member newMember = new Member("fname","lname","activityTest@email.com","password",3);
		newMember.save();
		member = newMember;
		insertActivity();
		List<Activity> memberActivities = Member.find.byId(origMember.getId()).getActivities();
		assertTrue("testing fetching activities according to member", (memberActivities != null));
		assertTrue("testing fetching activities according to member", memberActivities.size() > 0 );
		boolean foundWrongActivities = false;
		for(Activity activity : memberActivities){
			if(activity.getMember().getId() != origMember.getId()){
				foundWrongActivities = true;
			}
		}
		assertFalse("testing that fetch activities by member did not retrieve wrong members",foundWrongActivities);
		Activity.find.byId(id).delete();
		newMember.delete();
		member = origMember;
	}
	
	@Test
	public void testGetActivitiesByLocation(){
		insertActivity();
		Location newLocation = new Location("testTempLocation","A test location",0.222,2.122,"zdfzxxz");
		newLocation.save();
		Location origLocation = location;
		location = newLocation;
		insertActivity();
		List<Activity> locationActivities = Location.find.byId(origLocation.id).activities;
		
		assertTrue("testing fetching activities according to location", (locationActivities != null));
		assertTrue("testing fetching activities according to member", locationActivities.size() > 0 );
		boolean foundWrongActivities = false;
		for(Activity activity : locationActivities){
			if(activity.getLocation().id != origLocation.id){
				foundWrongActivities = true;
			}
		}
		assertFalse("testing that fetch activities by member did not retrieve wrong members",foundWrongActivities);
		Activity.find.byId(id).delete();
		newLocation.delete();
		
		
		location = origLocation;
		
		
	}
	
	@Test
	public void testDeleteActivity(){
		id = null;
		insertActivity();
		Activity.find.byId(id).delete();
		assertTrue(id != null && id > 0);
		Activity deadActivity = Activity.find.byId(id);
		assertNull(deadActivity);
		
	}
	
	
}