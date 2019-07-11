// Todo: Indeterminate progress bar on posting, restyle login in and sign pages,
//  show user posts by clicking, save post(?)

package com.example.instagram;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LoginActivity extends AppCompatActivity
		implements SignUpFragment.Listener, LoginFragment.Listener {

	// Fragments
	private LoginFragment loginFragment = LoginFragment.newInstance();;
	private SignUpFragment signUpFragment = SignUpFragment.newInstance();



	// Fragment Tags
	private static final String FRAGMENT_KEY_LOGIN = "login";
	private static final String FRAGMENT_KEY_SIGNUP = "signup";

	// SignupFragment Listeners
	@Override
	public void onSignUp(String username, String password) {
		signup(username, password);
	}

	// LoginFragment Listeners
	@Override
	public void onLogin(String username, String password) {
		login(username, password);
	}

	@Override
	public void onSignUpTransition(String username, String password) {
		getSupportFragmentManager().beginTransaction().replace(R.id.flPlaceholder, signUpFragment, FRAGMENT_KEY_SIGNUP).addToBackStack(FRAGMENT_KEY_LOGIN).commit();
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(ParseUser.getCurrentUser() != null) {
			startActivity(new Intent(this, MainActivity.class));
			finish();
		}
		setContentView(R.layout.activity_login);
		getSupportFragmentManager().beginTransaction().add(R.id.flPlaceholder, loginFragment, FRAGMENT_KEY_LOGIN).commit();
	}

	private void signup(final String username, final String password) {
		ParseUser user = new ParseUser();
		// Set core properties
		user.setUsername(username);
		user.setPassword(password);
		// Invoke signUpInBackground
		user.signUpInBackground(new SignUpCallback() {
			public void done(ParseException e) {
				if (e == null) {
					login(username, password);
					Log.d("LoginActivity", "Signup successful!");
				} else {
					Log.e("LoginActivity", "Signup failure.");
					e.printStackTrace();
				}
			}
		});
	}

	private void login(final String username, final String password) {
		ParseUser.logInInBackground(username, password, new LogInCallback() {
			@Override
			public void done(ParseUser user, ParseException e) {
				if(e == null) {
					Intent intent = new Intent(LoginActivity.this, MainActivity.class);
					Log.d("LoginActivity", "Login successful!");
					startActivity(intent);
					finish();
				}
				else {
					Toast.makeText(LoginActivity.this, "Login failed! Check your username and password.", Toast.LENGTH_SHORT).show();
					Log.e("LoginActivity", "Login failure.");
					e.printStackTrace();
				}
			}
		});
	}
}
