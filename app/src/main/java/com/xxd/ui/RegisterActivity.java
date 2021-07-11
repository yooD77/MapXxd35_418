package com.xxd.ui;

import java.sql.SQLException;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.example.mapxxd35_418.MainActivity;
import com.example.mapxxd35_418.OrmDatabaseHelper;
import com.example.mapxxd35_418.R;
import com.example.mapxxd35_418.SharePreferencesManager;
import com.xxd.bean.User;
import com.xxd.utils.StringUtil;


public class RegisterActivity extends BackActivity {
	
	OrmDatabaseHelper helper = new OrmDatabaseHelper(RegisterActivity.this);
	Dao<User, Integer>userDao;
	private EditText et_register_username;
	private EditText et_register_password;
	private EditText et_register_confirm_password;
	
	private Button bt_register;
	String username ;
	String password ;
	User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		userDao = helper.getUserDao();
		initActionbar();
		initView();
	}

	private void initActionbar() {
		setCenterTitle("注册");
	}

	private void initView() {
		et_register_username = (EditText) findViewById(R.id.et_register_username);
		et_register_password = (EditText) findViewById(R.id.et_register_password);
		et_register_confirm_password = (EditText) findViewById(R.id.et_register_confirm_password);
		bt_register = (Button) findViewById(R.id.btn_register_register);
		bt_register.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!confirmInfo()){
					return;
				}
				if(!confirmPassword()){
					Toast.makeText(RegisterActivity.this, "您输入的密码不一致，请重新输入", Toast.LENGTH_SHORT).show();
					return;
				}
				register();
				
				Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
				startActivity(intent);
				RegisterActivity.this.finish();
			}
		});
	}

	/**
	 * 注册信息符合要求，将用户信息注册到服务器
	 */
	protected void register() {
		try {
			userDao.create(user);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		SharePreferencesManager spm = SharePreferencesManager.getInstance();
		spm.putString(RegisterActivity.this, "user",Context.MODE_PRIVATE,"username", username);
		spm.putString(RegisterActivity.this, "user",Context.MODE_PRIVATE,"password", password);
	}
	
	
	/**
	 * 检测两次输入的密码是否一致
	 */
	private Boolean confirmPassword() {
		String psw = et_register_password.getText().toString();
		String pswAgain = et_register_confirm_password.getText().toString();
		if(psw.equals(pswAgain)){
			return true;
		}else{
			return false;
		}
		
	}
	/**
	 * 验证注册消息是否符合要求
	 */
	private boolean confirmInfo() {
		user = new User();
		username = et_register_username.getText().toString();
		password = et_register_password.getText().toString();
		String passwordAgain = et_register_confirm_password.getText().toString();
		if(username==null||password==null){
			Toast.makeText(RegisterActivity.this, "请确认是否完整输入用户名和密码", Toast.LENGTH_LONG).show();
			return false;
		}
		if(!StringUtil.isUsername(username)){
			Toast.makeText(RegisterActivity.this, "用户名只能由4~15个字母或数字组成", Toast.LENGTH_LONG).show();
			return false;
		}
		if((!StringUtil.isPassword(password))||(!StringUtil.isPassword(passwordAgain))){
			Toast.makeText(RegisterActivity.this, "密码只能由6~15个字母或数字组成", Toast.LENGTH_LONG).show();
			return false;
		}
		user.setPassword(password);
		user.setUsername(username);
		return true;
	}
}
