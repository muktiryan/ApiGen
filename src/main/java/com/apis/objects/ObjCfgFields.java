package com.apis.objects;

public class ObjCfgFields {
	private Integer dbID;
	private Integer tenantDBID;
	private String name;
	private String type;
	private Integer length;
	private String fieldType;
	private String  isPrimaryKey;
	private String isUnique;
	private String isNullable;
	private String state;
	private String userProperties;
	public ObjCfgFields() {
		// TODO Auto-generated constructor stub
	}
	public ObjCfgFields(Integer dbID, Integer tenantDBID, String name, String type, Integer length, String fieldType,
			String isPrimaryKey, String isUnique, String isNullable, String state, String userProperties) {
		super();
		this.dbID = dbID;
		this.tenantDBID = tenantDBID;
		this.name = name;
		this.type = type;
		this.length = length;
		this.fieldType = fieldType;
		this.isPrimaryKey = isPrimaryKey;
		this.isUnique = isUnique;
		this.isNullable = isNullable;
		this.state = state;
		this.userProperties = userProperties;
	}






	public Integer getDbID() {
		return dbID;
	}
	public void setDbID(Integer dbID) {
		this.dbID = dbID;
	}
	public Integer getTenantDBID() {
		return tenantDBID;
	}
	public void setTenantDBID(Integer tenantDBID) {
		this.tenantDBID = tenantDBID;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Integer getLength() {
		return length;
	}
	public void setLength(Integer length) {
		this.length = length;
	}
	public String getFieldType() {
		return fieldType;
	}
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}
	public String getIsPrimaryKey() {
		return isPrimaryKey;
	}
	public void setIsPrimaryKey(String isPrimaryKey) {
		this.isPrimaryKey = isPrimaryKey;
	}
	public String getIsUnique() {
		return isUnique;
	}
	public void setIsUnique(String isUnique) {
		this.isUnique = isUnique;
	}
	public String getIsNullable() {
		return isNullable;
	}
	public void setIsNullable(String isNullable) {
		this.isNullable = isNullable;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getUserProperties() {
		return userProperties;
	}
	public void setUserProperties(String userProperties) {
		this.userProperties = userProperties;
	}
	
	
	
	
	
	
	
}
