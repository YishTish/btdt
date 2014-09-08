import static org.junit.Assert.*;

import java.util.List;

import models.Location;
import static play.test.Helpers.*;

import org.junit.*;



public class LocationTest {
	
	String name = "TestGate";
	String description = "Test location";
	Double longitude = 0.123456;
	Double latitude = 6.543210;
	String code = "dsf s sd";
	
	
	@Before
	public void insertLocation(){
		running(fakeApplication(), new Runnable() {
		    public void run() {
		    	
			    deleteLocations(name);
				Location location= new Location(name,description, longitude,latitude,code);
				location.save();
		    }
		});
	}
	
	@Test
	public void testFindLocationByName(){
		running(fakeApplication(), new Runnable() {
		    public void run() {
		    	List<Location> locations = Location.find.where().like("name",name).findList();
		    	//assertThat("query for members by emails returned 0",(members.size() > 0) ,is(not(0)));
				assertEquals(1,locations.size());
		    }
		  });
	}
	
	
	@Test
	public void testGetAllLocations(){
		running(fakeApplication(), new Runnable() {
		    public void run() {
		    	List<Location> locations = Location.find.all();
		    	assertFalse(locations != null && locations.size() < 1);
		    }
		});
	}
	
	@Test
	public void testUpdateLocation(){
		running(fakeApplication(), new Runnable() {
		    public void run() {
		    Location location= new Location(name,description, longitude,latitude,code);
			location.save();
			Integer locationId = location.id;
			location.name = "updated_"+location.name;
			String newName= location.name;
			location.update();
			
			Location newLocation = Location.find.byId(locationId);
			assertEquals(newLocation.name,newName);
			
			deleteLocations(newName);
		    }
		});
	}
	
	@Test
	public void testDeleteLocation(){
		running(fakeApplication(), new Runnable() {
		    public void run() {
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
		});
	}
	
	@Test
	public void testDuplicateLocationCode(){
		running(fakeApplication(), new Runnable() {
		    public void run() {
		    	Location location = new Location("New name","new Description", 0.1, 1.0, code);
		    	location.save();
		    	assertNull(location.id);
		    }
		});
	}
	
	
	private void deleteLocations(String locationName){
		List<Location> locations= Location.find.where().eq("name", locationName).findList();
		if(locations!=null){
			for(Location location: locations)
				location.delete();
		}
		locations= Location.find.where().eq("name", locationName).findList();
		assertTrue(locations.size() == 0);
	}
	
	@Test
	public void testInsertLocationByController(){
		
	}
	
	@Test
	public void testUpdateLocationByController(){
		
	}
	
	@Test
	public void testRetrieveAllLocationsByController(){
		
	}
	
	@Test
	public void testRetrieveLocationByIdByController(){
		
	}
	
	@Test
	public void testRetrieveAllLocationByCodeByController(){
		
	}
	
	@Test
	public void testRetrieveAllLocationsByNameByController(){
		
	}
	
	@Test
	public void testUpdateLocationCodeByController(){
		
	}
	
	@Test
	public void testDeleteLocationByController(){
		
	}
	
}
