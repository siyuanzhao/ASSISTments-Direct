package org.assistments.direct.teacher;

import java.util.HashMap;
import java.util.Map;

import org.assistments.connector.service.AssignmentLogService;
import org.assistments.connector.service.impl.AssignmentLogServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AssignmentLog {
	
	@RequestMapping(value = "/complete_assignment_progress", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> 
		completeAssignmentProgress(@RequestParam(value="assignment_id") String assignmentId, 
				@RequestParam(value="student_ids[]") String[] studentIds) {
//		String assignmentId = (String)req.get("assignment_id");
//		
//		String studentIds = (String)req.get("student_ids[]");
		
		long assignmentIdLong = Long.valueOf(assignmentId);
		AssignmentLogService als = new AssignmentLogServiceImpl();
		for(String studentId: studentIds) {
			long studentIdLong = Long.valueOf(studentId);
			als.completeAssignmentProgress(studentIdLong, assignmentIdLong);
		}
		Map<String, Object> body = new HashMap<>();
		ResponseEntity<Map<String, Object>> resp = 
				new ResponseEntity<Map<String,Object>>(body , HttpStatus.ACCEPTED);
		
		return resp;
	}
	
	@RequestMapping(value = "/delete_assignment_progress", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> 
		deleteAssignmentProgress(@RequestParam(value="assignment_id") String assignmentId, 
				@RequestParam(value="student_ids[]") String[] studentIds) {
//		String assignmentId = (String)req.get("assignment_id");
//		
//		String studentIds = (String)req.get("student_ids[]");
		
		long assignmentIdLong = Long.valueOf(assignmentId);
		AssignmentLogService als = new AssignmentLogServiceImpl();
		for(String studentId: studentIds) {
			long studentIdLong = Long.valueOf(studentId);
			als.deleteAssignmentProgress(studentIdLong, assignmentIdLong);
		}
		Map<String, Object> body = new HashMap<>();
		ResponseEntity<Map<String, Object>> resp = 
				new ResponseEntity<Map<String,Object>>(body , HttpStatus.ACCEPTED);
		
		return resp;
	}
}
