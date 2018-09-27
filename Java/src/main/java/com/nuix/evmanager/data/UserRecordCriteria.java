package com.nuix.evmanager.data;

import java.util.HashMap;
import java.util.Map;

/***
 * Encapsulates criteria used to query for user records
 * @author Jason Wells
 *
 */
public class UserRecordCriteria {
	private String nameCriteria;
	private String titleCriteria;
	private String departmentCriteria;
	private String employeeIdCriteria;
	private String locationCriteria;
	
	public UserRecordCriteria(){
	}
	
	public Map<String,String> toMap(){
		Map<String,String> result = new HashMap<String,String>();
		
		if(nameCriteria != null && !nameCriteria.trim().equals("")){
			result.put("Name",nameCriteria);
		}
		
		if(titleCriteria != null && !titleCriteria.trim().equals("")){
			result.put("Title",titleCriteria);
		}
		
		if(departmentCriteria != null && !departmentCriteria.trim().equals("")){
			result.put("Department",departmentCriteria);
		}
		
		if(employeeIdCriteria != null && !employeeIdCriteria.trim().equals("")){
			result.put("EmployeeID",employeeIdCriteria);
		}
		
		if(locationCriteria != null && !locationCriteria.trim().equals("")){
			result.put("Location",locationCriteria);
		}
		
		return result;
	}

	public String getNameCriteria() {
		return nameCriteria;
	}
	
	public void setNameCriteria(String nameCriteria) {
		this.nameCriteria = nameCriteria;
	}

	public String getTitleCriteria() {
		return titleCriteria;
	}

	public void setTitleCriteria(String titleCriteria) {
		this.titleCriteria = titleCriteria;
	}

	public String getDepartmentCriteria() {
		return departmentCriteria;
	}

	public void setDepartmentCriteria(String departmentCriteria) {
		this.departmentCriteria = departmentCriteria;
	}

	public String getEmployeeIdCriteria() {
		return employeeIdCriteria;
	}

	public void setEmployeeIdCriteria(String uniqueIdCriteria) {
		this.employeeIdCriteria = uniqueIdCriteria;
	}

	public String getLocationCriteria() {
		return locationCriteria;
	}

	public void setLocationCriteria(String locationCriteria) {
		this.locationCriteria = locationCriteria;
	}
}
