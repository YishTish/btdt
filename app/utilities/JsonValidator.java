package utilities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import play.libs.Json;
import models.Activity;
import models.ActivityType;
import models.Location;
import models.Member;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class JsonValidator {
	
	public static List<String> validateFieldsExist(JsonNode request, String[] fields){
		List<String> result = new ArrayList<String>();
		for(String field : fields){
			if(request.findValue(field) == null){
				result.add(String.format("field [%s] is missing",field));
			}
		}
		return result;
	}
	
}