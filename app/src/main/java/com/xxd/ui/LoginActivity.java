package com.xxd.ui;


import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.example.mapxxd35_418.MainActivity;
import com.example.mapxxd35_418.OrmDatabaseHelper;
import com.example.mapxxd35_418.R;
import com.example.mapxxd35_418.SharePreferencesManager;
import com.xxd.bean.User;
import com.xxd.utils.StringUtil;

public class LoginActivity extends BackActivity {

	OrmDatabaseHelper helper = new OrmDatabaseHelper(LoginActivity.this);
	Dao<User, Integer>userDao;
	private EditText et_username;
	private EditText et_password;
	private Button bt_login;
	// 定义actionbar控件
	private TextView actionbarRightTextView;
	String username = "1";
	String password = "1";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		initActionbar();
		init();
	}

	private void initActionbar() {
		setCenterTitle("登录");
		userDao = helper.getUserDao();
		actionbarRightTextView = setRightTitle("注册");
		actionbarRightTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this,
						RegisterActivity.class);
				startActivity(intent);
//				LoginActivity.this.finish();
			}
		});
	}

	private void init() {
		SharePreferencesManager sp = SharePreferencesManager.getInstance();
		et_username = (EditText) findViewById(R.id.et_login_username);
		et_password = (EditText) findViewById(R.id.et_login_password);
		username = sp.getString(LoginActivity.this, "user",Context.MODE_PRIVATE,"username");
		
		
		if(!username.equals("1")&&!username.equals("")){
			et_username.setText(username);
		}
		password = sp.getString(LoginActivity.this, "user",Context.MODE_PRIVATE,"password");
		if(!password.equals("1")&&!username.equals("")){
			et_password.setText(password);
			et_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		}
		bt_login = (Button) findViewById(R.id.btn_login_land);
		bt_login.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(confirmInfo())
					login();
			}

		});
	}
	/**
	 * 检测登录信息是否符合要求
	 */
	private boolean confirmInfo() {
		username = et_username.getText().toString();
		password = et_password.getText().toString();
		if(username==null||password==null){
			Toast.makeText(LoginActivity.this, "请确认是否完整输入用户名和密码", Toast.LENGTH_LONG).show();
			return false;
		}
		if(!StringUtil.isUsername(username)){
			Toast.makeText(LoginActivity.this, "用户名只能由4~15个字母或数字组成", Toast.LENGTH_LONG).show();
			return false;
		}
		if(!StringUtil.isPassword(password)){
			Toast.makeText(LoginActivity.this, "密码只能由6~15个字母或数字组成", Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}
	/**
	 * 信息符合要求，验证账号密码
	 */
	private void login(){
		userDao = helper.getUserDao();
		try {
			List<User> list= userDao.queryForAll();
			for(int i = 0;i<list.size();i++){
				String un = list.get(i).getUsername();
				if(un.equals(username)){
					if(list.get(i).getPassword().equals(password)){
						Intent intent = new Intent(LoginActivity.this,MainActivity.class);
						startActivity(intent);
						LoginActivity.this.finish();
						return;
					}
				}
			}
			Toast.makeText(LoginActivity.this, "用户不存在或者密码错误", Toast.LENGTH_LONG).show();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
