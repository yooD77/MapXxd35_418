package com.xxd.ui;


import com.example.mapxxd35_418.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class LaunchActivity extends Activity {

	private Button btn_login;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_launch);
		init();
	}
	private void init() {
		btn_login = (Button) findViewById(R.id.btn_launch_login);
		btn_login.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LaunchActivity.this,LoginActivity.class);
				startActivity(intent);
				LaunchActivity.this.finish();
			}
		});
	}
}
