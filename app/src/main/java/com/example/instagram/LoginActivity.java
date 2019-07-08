package com.example.instagram;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

	@BindView(R.id.btnLogin)
	Button btnLogin;
	@BindView(R.id.etUsername)
	TextInputEditText etUsername;
	@BindView(R.id.etPassword)
	TextInputEditText etPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		ButterKnife.bind(this);

		btnLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				final String username = etUsername.getText().toString();
				final String password = etPassword.getText().toString();
				Log.d("LoginActivity", "Click");
				login(username, password);
			}
		});
	}

	private void login(String username, String password) {
		ParseUser.logInInBackground(username, password, new LogInCallback() {
			@Override
			public void done(ParseUser user, ParseException e) {
				if(e == null) {
					Log.d("LoginActivity", "Login successful!");
				}
				else {
					Log.e("LoginActivity", "Login failure.");
					e.printStackTrace();
				}
			}
		});
	}
}
