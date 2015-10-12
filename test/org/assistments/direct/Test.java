package org.assistments.direct;

import java.util.List;

import org.assistments.edmodo.domain.EdmodoGroup;
import org.assistments.edmodo.domain.EdmodoLaunchRequest;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Test {

	public static void main(String[] args) {
		demo2();
		
	}
	
	public static void demo1() {
		String s = "{ \"user_type\":\"TEACHER\","
				+ "    \"access_token\":\"atok_abbd3e01\","
    		+ "    \"user_token\":\"b020c42d1\","
    		+ "    \"first_name\":\"Bob\","
    		+ "    \"last_name\":\"Smith\","
    		+ "    \"avatar_url\":\"https://edmodoimages.s3.amazonaws.com/default_avatar.png\","
    		+ "    \"thumb_url\":\"https://edmodoimages.s3.amazonaws.com/default_avatar_t.png\","
    		+ "    \"time_zone\":\"PST\","
    		+ "    \"user_id\":1234567,"
    		+ "    \"groups\":[{\"group_id\":379557,\"is_owner\":1},"
    		+ "{\"group_id\":379562,\"is_owner\":1 }]}";
		
		Gson gson = new Gson();
		EdmodoLaunchRequest request = gson.fromJson(s, EdmodoLaunchRequest.class);
		System.out.println(request.getAccess_token());
		System.out.println(request.getGroups().get(0).groupId);
	}
	
	public static void demo2() {
		String s = "[{\"group_id\":379557,"
				+ "\"title\":\"Period 1\","
				+ "\"member_count\":20,"
				+ "\"owners\":[\"b020c42d1\",\"693d5c765\"],"
				+ "\"subject\":\"Math\",\"sub-subject\":\"Algebra\","
				+ "\"start_level\":\"9th\", \"end_level\":\"9th\"},"
				+ "{\"group_id\":379562,"
				+ "\"title\":\"Period 4\",\"member_count\":28,"
				+ "\"owners\":[\"b020c42d1\"],"
				+ "\"subject\":\"Math\",\"sub-subject\":\"Geometry\","
				+ "\"start_level\":\"3rd\",\"end_level\":\"3rd\"}]";
		Gson gson = new Gson();
		List<EdmodoGroup> groups = 
				gson.fromJson(s, new TypeToken<List<EdmodoGroup>>(){}.getType());
		for (EdmodoGroup group : groups) {
			System.out.println(group.getTitle() + "\t" + group.getOwners().get(0));
		}
	}
}
