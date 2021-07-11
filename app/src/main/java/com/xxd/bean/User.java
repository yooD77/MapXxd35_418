package com.xxd.bean;
import com.j256.ormlite.field.DatabaseField;
public class User {
	/** id 主键,自增 */
	@DatabaseField(generatedId = true)
	private int id;

	/** GUID */
	@DatabaseField
	private String GUID;

	@DatabaseField
	private String username;
	@DatabaseField
	private String password;


	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
