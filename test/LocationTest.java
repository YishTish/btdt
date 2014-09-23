import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.PersistenceException;

import models.Location;

import org.junit.*;

import play.libs.Json;
import play.mvc.Result;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import controllers.LocationController;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;


public class LocationTest {
	
	String name = "TestGate";
	String description = "Test location";
	Double longitude = 0.123456;
	Double latitude = 6.543210;
	String code = "dsf s sd";
	
	
	@Before
	public void setup(){
		start(fakeApplication());
		createNewLocation();
	}
	
	@After
	public void complete(){
		stop(fakeApplication());
	}
	
	public void insertLocation(){
		    	deleteLocations(name);
				Location location= new Location(name,description, longitude,latitude,code);
				location.save();
	}
	
	@Test
	public void testFindLocationByName(){
		    	List<Location> locations = Location.find.where().like("name",name).findList();
		    	//assertThat("query for members by emails returned 0",(members.size() > 0) ,is(not(0)));
				assertEquals(1,locations.size());
    }

	
	
	@Test
	public void testGetAllLocations(){
		    	List<Location> locations = Location.find.all();
		    	assertFalse(locations != null && locations.size() < 1);
	}
	
	@Test
	public void testUpdateLocation(){
		    Location location= createNewLocation();
			Integer locationId = location.id;
			location.name = "updated_"+location.name;
			String newName= location.name;
			location.update();
			
			Location newLocation = Location.find.byId(locationId);
			assertEquals(newLocation.name,newName);
			
			deleteLocations(newName);
	}
	
	@Test
	public void testDeleteLocation(){
				List<Location> locations= Location.find.where().like("name", "%"+name).findList();
				if(locations!=null){
					System.out.printf("found %d members \n",locations.size());
					for(Location location : locations){
						location.delete();
					}
					locations.clear();
					locations = Location.find.where().like("name", "%"+name).findList();
					assertTrue(locations==null || locations.size() == 0);
				}
	}
	
	@Test
	public void testDuplicateLocationCode(){
		    	Location location = new Location("New name","new Description", 0.1, 1.0, code);
		    	boolean insertFailed = false;
		    	try{
		    	location.save();
		    	}
		    	catch(PersistenceException pe){
		    		insertFailed = true;
		    	}
		    	
		    	assertTrue(insertFailed);
	}
	
	
	private void deleteLocations(String locationName){
		List<Location> locations= Location.find.where().eq("name", locationName).findList();
		if(locations!=null){
			for(Location location: locations)
				location.delete();
		}
		locations= Location.find.where().eq("name", locationName).findList();
	}
	
	
	private Location createNewLocation(){
		List<Location> nameLocations = Location.find.where().eq("name",name).findList();
		for(Location location : nameLocations){
			location.delete();
		}
		List<Location> codeLocations = Location.find.where().eq("code",code).findList();
		for(Location location : codeLocations){
			location.delete();
		}
		Location newLocation = new Location(name, description, longitude, latitude, code);
		newLocation.save();
		
		return newLocation;
		
	}
	@Test
	public void testInsertLocationByController(){
		ObjectNode locationJson = Json.newObject();
		locationJson.put("name", "testLocationByController");
		locationJson.put("description", "testLocationDescription");
		locationJson.put("longitude",0.123);
		locationJson.put("latitude",3.21);
		
		Result result = callAction(controllers.routes.ref.LocationController.insertLocation(), 
				fakeRequest().withHeader("Conent-Type","application/json").withJsonBody(locationJson)
				);
		
		assertEquals("Insert Activity should succeeed. response: "+contentAsString(result),OK, status(result));
		JsonNode responseJson = Json.parse(contentAsString(result));
		int id = responseJson.findValue("id").asInt();
		assertTrue("Insert Location by controller, generated automatic id",id > 0);
		String Code = responseJson.findValue("code").asText();
		assertTrue("Insert Location by controller, generated automatic code",(code !=null && !"".equals(code)));
		
	}
	
	@Test
	public void testUpdateLocationByController(){
		Location newLocation = createNewLocation();
		
		int newLocationId = newLocation.id;
		ObjectNode locationJson = Json.newObject();
		locationJson.put("name", "testLocationByController");
		locationJson.put("description", "testLocationDescription");
		locationJson.put("longitude",0.123);
		locationJson.put("latitude",3.21);
		locationJson.put("id", newLocationId);
		
		
		Result result = callAction(controllers.routes.ref.LocationController.updateLocation(), 
				fakeRequest().withHeader("Conent-Type","application/json").withJsonBody(locationJson)
				);
		
		assertEquals(OK, status(result));
		Location updatedLocation = Location.find.byId(newLocationId);
		assertTrue("updated location by controller succeeded",updatedLocation.name.equals("testLocationByController"));
		assertTrue("updated location should not change code",updatedLocation.code.equals(code));
		
		updatedLocation.delete();
		
	}
	
	@Test
	public void testRetrieveAllLocationsByController(){
		Location newLocation = createNewLocation();
		
		int numOfLocations = Location.find.findRowCount();
		
		Result result = callAction(controllers.routes.ref.LocationController.getAllLocations(), 
				fakeRequest());
		assertEquals(OK, status(result));
		JsonNode responseJson = Json.parse(contentAsString(result));
		int numOfJsonAttr = responseJson.size();
		assertEquals(numOfLocations, numOfJsonAttr);
		
		newLocation.delete();
		
	}
	
	@Test
	public void testRetrieveLocationByIdByController(){
		Location newLocation = createNewLocation();
		
		Result result = callAction(controllers.routes.ref.LocationController.getLocation(newLocation.id), 
				fakeRequest());
		assertEquals(OK, status(result));
		JsonNode responseJson = Json.parse(contentAsString(result));
		assertTrue("retreived location by id via controller. checking id", responseJson.findValue("id").asInt() == newLocation.id);
		assertTrue("retreived location by id via controller. checking code", responseJson.findValue("code").asText().equals(code));		
		
		newLocation.delete();
	}
	
	@Test
	public void testRetrieveLocationByCodeByController(){
		Location newLocation = createNewLocation();
		
		int newLocationId = newLocation.id;
		Result result = callAction(controllers.routes.ref.LocationController.getLocationByCode(code), 
				fakeRequest());
		assertEquals(OK, status(result));
		JsonNode responseJson = Json.parse(contentAsString(result));
		int retrievedId = responseJson.findValue("id").asInt();
		assertTrue("retrieved location by code - checking validity of id",retrievedId==newLocationId);
		newLocation.delete();
	}
	
	@Test
	public void testRetrieveAllLocationsByNameByController(){
		
		Location newLocation = createNewLocation();
		int numOfLocations = Location.find.where().like("name", name).findRowCount();
		
		Result result = callAction(controllers.routes.ref.LocationController.getLocationsByName(name), 
				fakeRequest());
		
		assertEquals("Testing retrieve all locations by name. Failed: "+contentAsString(result),OK, status(result));

		JsonNode responseJson = Json.parse(contentAsString(result));
		int numOfJsonAttr = responseJson.size();
		assertEquals("Looking for locations named "+name,numOfLocations, numOfJsonAttr);
		
		
		
		newLocation.delete();
	}
	
		
	@Test
	public void testDeleteLocationByController(){
		Location newLocation = createNewLocation();
		Result result = callAction(controllers.routes.ref.LocationController.deleteLocation(newLocation.id), 
				fakeRequest());
		assertEquals(OK, status(result));
		
		Location deadLocation = Location.find.byId(newLocation.id);
		assertTrue("Deleted Location through controller, trying to retrieve the deleted id",(deadLocation == null));
		
	}
	
	public void testUpdateCodeByController(){
		Location newLocation = createNewLocation();
		
		Result result = callAction(controllers.routes.ref.LocationController.generateLocationCode(newLocation.id), 
				fakeRequest());
		assertEquals(OK, status(result));
		JsonNode responseJson = Json.parse(contentAsString(result));
		assertTrue("Changed code for location - check that id remains", responseJson.findValue("id").asInt() == newLocation.id);
		assertTrue("Changed code for location - check that code is new", !(responseJson.findValue("code").asText().equals(newLocation.code)));
		
		newLocation.delete();
	}
	
}
