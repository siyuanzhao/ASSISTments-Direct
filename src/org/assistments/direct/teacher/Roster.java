package org.assistments.direct.teacher;

import java.util.ArrayList;
import java.util.List;

import org.assistments.service.domain.User;

public class Roster {
	
	List<SectionInfo> sections;
	
	public Roster() {
		sections = new ArrayList<SectionInfo>();
	}
	
	public List<SectionInfo> getSections() {
		return sections;
	}

	public void setSections(List<SectionInfo> sections) {
		this.sections = sections;
	}

	public class SectionInfo {
		String sectionRef;
		String sectionName;
		List<User> students;
		public String getSectionRef() {
			return sectionRef;
		}
		public void setSectionRef(String sectionRef) {
			this.sectionRef = sectionRef;
		}
		public String getSectionName() {
			return sectionName;
		}
		public void setSectionName(String sectionName) {
			this.sectionName = sectionName;
		}
		public List<User> getStudents() {
			return students;
		}
		public void setStudents(List<User> students) {
			this.students = students;
		}
	}
}
