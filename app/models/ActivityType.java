package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import play.db.ebean.*;

@Entity
public class ActivityType extends Model{

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "activity_type_id")
	public Integer id;
	public String name;
	public String description;
	public boolean active;
	
	@JsonManagedReference
	@OneToMany(mappedBy="activityType", cascade=CascadeType.ALL)
	public List<Activity> activities;
	
	public ActivityType(String iName, String iDescription, boolean iActive){
		name = iName;
		description = iDescription;
		active = iActive;
	}
	
	public ActivityType(String iName, String iDescription){
		name = iName;
		description = iDescription;
		active = true;
	}
	
	public ActivityType(){
		
	}
	
	public static Model.Finder<Integer, ActivityType> find = new Model.Finder<Integer, ActivityType>(Integer.class, ActivityType.class);


}


 

 