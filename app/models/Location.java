package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import play.db.ebean.Model;
import constraints.*;



@Unique.List({
	@Unique(modelClass = Location.class, fields = {"name","code"}, message = "Name and Code are unique for each location")
}
)

@Entity
@Table(uniqueConstraints=@UniqueConstraint(columnNames="code"))
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
	public String code;
	
	@JsonManagedReference
	@OneToMany(mappedBy="location", cascade= CascadeType.ALL)
	public List<Activity> activities;
	
	public Location(String iName, String iDescription, Double iLongitude, Double iLatitude, String iCode){
		name = iName;
		description = iDescription;
		longitude = iLongitude;
		latitude = iLatitude;
		code = iCode;
	}
	
	public Location(){
		
	}
	
	public static Model.Finder<Integer, Location> find = new Model.Finder<Integer, Location>(Integer.class, Location.class);
	

}
