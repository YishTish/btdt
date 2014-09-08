package models;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import play.db.ebean.Model;
import play.data.format.*;
import play.data.validation.*;
import play.data.format.Formats.DateTime;

import org.mindrot.jbcrypt.BCrypt;



@Entity
public class Member extends Model{
	
	
	
	private static final long serialVersionUID = 1L;


	public Member(String fname2, String lname2, String email2,
		String password2, Integer type2) {
		setFname(fname2);
		setLname(lname2);
		setEmail(email2);
		setPassword(password2);
		setType(type2);
		joinDate = new Date();
	}

	@Id
	@Column (name="member_id")
	public Integer id;
	@Constraints.Required
	private String fname;
	private String lname;
	@Constraints.Email
	@Constraints.Required
	private String email;
	private String password;
	private Integer type;
	@Column (name="join_date")
	@Formats.DateTime(pattern = "dd/mm/yyyy")
	private  Date joinDate;
	
	@OneToMany(mappedBy="member", cascade=CascadeType.ALL)
	private List<Activity> activities;
	
	

	
	public Integer getId() {
		return id;
	}




	public void setId(Integer id) {
		this.id = id;
	}




	public String getFname() {
		return fname;
	}




	public void setFname(String fname) {
		this.fname = fname;
	}




	public String getLname() {
		return lname;
	}




	public void setLname(String lname) {
		this.lname = lname;
	}




	public String getEmail() {
		return email;
	}




	public void setEmail(String email) {
		this.email = email;
	}




	public String getPassword() {
		return "*****";
	}


	


	public List<Activity> getActivities() {
		return activities;
	}




	public void setActivities(List<Activity> activities) {
		this.activities = activities;
	}




	public void setPassword(String iPassword) {
		String hashedPw = org.mindrot.jbcrypt.BCrypt.hashpw(iPassword,BCrypt.gensalt());

		this.password = hashedPw;
	}
	
	public boolean checkPassword(String iPassword){
		return BCrypt.checkpw(iPassword, this.password);
	}




	public Integer getType() {
		return type;
	}




	public void setType(Integer type) {
		this.type = type;
	}




	public Date getJoinDate() {
		return joinDate;
	}




	public void setJoinDate(Date joinDate) {
		this.joinDate = joinDate;
	}



	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
	public static Model.Finder<Integer, Member> find = new Model.Finder<Integer, Member>(Integer.class, Member.class);
	

}
