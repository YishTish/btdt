package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import play.db.ebean.Model;
import constraints.*;



@Unique.List({
	@Unique(modelClass = Location.class, fields = {"code"}, message = "Codes are unique for each location")
}
)

@Entity
public class Location extends Model{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@Column (name = "location_id")
	public Integer id;
	public String name;
	public String description;
	public Double longitude;
	public Double latitude;
	//@TODO: Need to ensure that code is unique. Seems like not part of the model, but the controller
	public String code;
	
	@OneToMany(mappedBy="location", cascade= CascadeType.ALL)
	public List<Activity> activities;
	
	public Location(String iName, String iDescription, Double iLongitude, Double iLatitude, String iCode){
		name = iName;
		description = iDescription;
		longitude = iLongitude;
		latitude = iLatitude;
		code = iCode;
	}
	
	public static Model.Finder<Integer, Location> find = new Model.Finder<Integer, Location>(Integer.class, Location.class);
	

}
