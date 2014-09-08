package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.db.ebean.Model;

@Entity(name = "activity_type")
public class ActivityType extends Model{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "activity_type_id")
	public Integer id;
	public String name;
	public String description;
	
	@OneToMany(mappedBy="activityType", cascade=CascadeType.ALL)
	public List<Activity> activities;
	
	
	
	
	public ActivityType(String iName, String iDescription){
		name = iName;
		description = iDescription;
	}
	
	public static Model.Finder<Integer, ActivityType> find = new Model.Finder<Integer, ActivityType>(Integer.class, ActivityType.class);


}


 

 