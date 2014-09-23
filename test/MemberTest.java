import static org.junit.Assert.*;

import java.util.List;

import models.Member;
import static play.test.Helpers.*;

import org.junit.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;
import play.mvc.Result;




public class MemberTest {
	
	
	int id;
	String fname = "Bob";
	String lname = "Mark";
	String email = "yishial@gmial.com";
	String password = "1235";
	Integer type = 1;
	
	
	public void insertMember(){
		/*
		 
		if(members!=null){
			for(Member member : members)
				member.delete();
		}
		*/
		Member member = Member.find.where().eq("email", email).findUnique();
		if(member == null){
			member = new Member(fname, lname, email, password, type);
			member.save();
		}
		id = member.getId();
	}
	
	public void deleteMember(){
		Member deadMember = Member.find.byId(id);
		if(deadMember != null) deadMember.delete();
	}
	
	private void deleteMember(Integer memberId){
		Member tempMember = Member.find.byId(memberId);
		assertNotNull(tempMember);
		tempMember.delete();
		Member deadMember = Member.find.byId(memberId);
		assertNull(deadMember);
	}
	
	@Before
	public void startApp(){
		start(fakeApplication());
		
	}
	
	@After
	public void stopApp(){
		if(id > 0){
			deleteMember();
		}
		stop(fakeApplication());
		id =0;
	}
	
	@Test
	public void testFindMemberByEmail(){
		insertMember();
    	Member member = Member.find.where().like("email", email).findUnique();
    	assertNotNull("Testing find member by Email", member);
    	deleteMember();
	}
	
	
	@Test
	public void testGetAllMemebers(){
		insertMember();
	   	List<Member> members = Member.find.all();
    	assertTrue("Testing call to retrieve all members",members != null && members.size() > 1);
    	deleteMember();
	}
	
	@Test
	public void testUpdateMember(){
		insertMember();
		Member origMember = Member.find.byId(id);
		assertTrue(origMember != null);
		String newEmail = "updated_"+origMember.getEmail();
		origMember.setEmail(newEmail);
		origMember.update();
		
		Member newMember = Member.find.byId(id);
		assertTrue("Test that updated member has new email address",newMember.getEmail().equals(newEmail));
		
		deleteMember();
}
	
	@Test
	public void testDeleteMember(){
		insertMember();
		Member member = Member.find.byId(id);
		assertNotNull("Testing delete member, checking that insert worked",member);
		member.delete();
		Member deaMember = Member.find.byId(id);
		assertNull("Testing delete member, checking that delete worked",deaMember);
	}
	
	@Test
	public void testinsertMemberByController(){
		Member memberToDelete = Member.find.where().eq("email", "yishail@gmail.com").findUnique();
		if(memberToDelete!=null){
			memberToDelete.delete();
		}
		ObjectNode memberJson = Json.newObject();
		memberJson.put("fname", "firstName");
		memberJson.put("lname", "lastName");
		memberJson.put("email", "yishail@gmail.com");
		memberJson.put("type", 1);
		
    	Member tempMember = Member.find.where().eq("email", "yishail@gmail.com").findUnique();
    	if(tempMember != null ){
    		tempMember.delete();
		}
    	Result result = callAction(controllers.routes.ref.MemberController.insertMember(), 
				fakeRequest().withHeader("Conent-Type","application/json").withJsonBody(memberJson)
				);
		assertEquals(OK, status(result));
    	JsonNode responseJson = runInsertMemberController();
    	Integer memberId = responseJson.findPath("id").asInt();
		deleteMember(memberId);
	}
	
	
	
	private JsonNode runInsertMemberController(){
		String instanceEmail = "yishail@gmail.com";
		Member memberToDelete = Member.find.where().eq("email", instanceEmail).findUnique();
		if(memberToDelete!=null){
			memberToDelete.delete();
		}
		ObjectNode memberJson = Json.newObject();
		memberJson.put("fname", "firstName");
		memberJson.put("lname", "lastName");
		memberJson.put("email", instanceEmail);
		memberJson.put("type", 1);
		
		Result result = callAction(controllers.routes.ref.MemberController.insertMember(), 
				fakeRequest().withHeader("Conent-Type","application/json").withJsonBody(memberJson)
				);
		assertEquals(OK, status(result));
		JsonNode responseJson = Json.parse(contentAsString(result));
		return responseJson;
	}
	
	@Test 
	public void testUpdateMemberByController(){
				insertMember();

				ObjectNode memberJson = Json.newObject();
				
				memberJson.put("id",id);
				memberJson.put("fname", fname.substring(1));
				memberJson.put("lname", lname.substring(1));
				memberJson.put("email", email.substring(1));
				memberJson.put("type", 2);
				Result result = callAction(controllers.routes.ref.MemberController.updateMember(), 
						fakeRequest().withHeader("Conent-Type","application/json").withJsonBody(memberJson));
				
				Member updatedMember = Member.find.byId(id);
				assertEquals(status(result),OK);
				assertEquals(updatedMember.getFname(),fname.substring(1));
				assertEquals(updatedMember.getLname(),lname.substring(1));
				assertEquals(updatedMember.getEmail(),email.substring(1));
				assertEquals(updatedMember.getType(),(Integer)2);
				
				deleteMember();
	}
	
	@Test 
	public void testRetrieveAllMembersByController(){
				insertMember();
		    	int numOfMembers = Member.find.all().size();
		    	Result result = callAction(controllers.routes.ref.MemberController.getAllMembers(), 
						fakeRequest());
		    	assertTrue(status(result)==OK);
				JsonNode responseJson = Json.parse(contentAsString(result));
				int numOfJsonAttr = responseJson.size();
				assertEquals(numOfMembers, numOfJsonAttr);
				deleteMember();
	}
	
	@Test 
	public void testRetrieveMemberByEmailByController(){
				insertMember();
		    	Member tempMember = Member.find.where().eq("email", email).findUnique();
		    	assertNotNull("testing member retreival by email", tempMember);
		    	Result result = callAction(controllers.routes.ref.MemberController.getMemberByEmail(email), 
						fakeRequest());
		    	assertTrue(status(result)==OK);
				JsonNode responseJson = Json.parse(contentAsString(result));
				Integer jsonMemberId = responseJson.findValue("id").asInt();
				assertNotNull(jsonMemberId);
				assertEquals("Testing that retrieved member by email is same via Controller as directly",jsonMemberId, tempMember.getId());
				
				deleteMember();
	}
	
	@Test 
	public void testDeleteMemberByController(){
		insertMember();		
		Result result = callAction(controllers.routes.ref.MemberController.deleteMember(id), 
						fakeRequest());
		Member deadMember = Member.find.byId(id);
		assertNull("testing delete member through Controller",deadMember);
	}
	
	@Test 
	public void testInsertMemberWithExistingEmail(){
		insertMember();
    	ObjectNode memberJson = Json.newObject();
		memberJson.put("fname", fname+"aa");
		memberJson.put("lname", lname+"ww");
		memberJson.put("email", email);
		memberJson.put("type", 2);
		Result result = callAction(controllers.routes.ref.MemberController.insertMember(), 
				fakeRequest().withHeader("Conent-Type","application/json").withJsonBody(memberJson)
				);
		assertTrue(status(result)==BAD_REQUEST);
		assertTrue(contentAsString(result).contains("Member with this email address already exists"));
		deleteMember();
	}
	
	@Test
	public void testUpdateMemberWithExistingEmail(){
		insertMember();
		
		Member newMember = new Member("tempFname","tempLname","temp@email.com","tempPassword",2);
		newMember.save();
		
		ObjectNode memberJson = Json.newObject();
		memberJson.put("id",id);
		memberJson.put("fname", "updatedFname");
		memberJson.put("lname", lname);
		memberJson.put("email", newMember.getEmail());
		memberJson.put("type", type);
		Result result = callAction(controllers.routes.ref.MemberController.updateMember(), 
				fakeRequest().withHeader("Conent-Type","application/json").withJsonBody(memberJson));
		//test trying to update to an email that exists
		assertTrue("Testing update of a member's email to an address that already exists in the db. Wanted "+ BAD_REQUEST+", got "+status(result),status(result) == BAD_REQUEST);
		assertTrue(contentAsString(result).contains("Update failed. Trying to update with an email address that is already registered"));

		newMember.delete();
		
	}
	
	@Test
	public void testUpdateWithInvalidId(){
		ObjectNode memberJson = Json.newObject();
		memberJson.put("fname", fname);
		memberJson.put("lname", lname);
		memberJson.put("email", email);
		memberJson.put("type", type);
		memberJson.put("id", -1);
		
		Result result = callAction(controllers.routes.ref.MemberController.updateMember(), 
				fakeRequest().withHeader("Conent-Type","application/json").withJsonBody(memberJson));
		assertTrue("Testing updating a member with an invalid id",status(result)==BAD_REQUEST);
		//test updating to an account that doesn't exist
		assertTrue(contentAsString(result).contains("Trying to update a member that doesn't exist"));
		deleteMember();
	}
	
		
	@Test
	public void testCheckPassword(){
		insertMember();
    	
    	ObjectNode memberJson = Json.newObject();
		memberJson.put("id",id);
		memberJson.put("password", password);
		Result result = callAction(controllers.routes.ref.MemberController.login(), 
				fakeRequest().withHeader("Conent-Type","application/json").withJsonBody(memberJson));
		assertEquals(OK, status(result));
		JsonNode responseJson = Json.parse(contentAsString(result));
		String retreivedEmail = responseJson.findValue("email").asText();
		assertTrue("Testing check password request returns a valid and correct user object",retreivedEmail.equals(email));
		deleteMember();
		
	}
}
