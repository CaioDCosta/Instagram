package com.example.instagram;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity
		implements SignUpFragment.Listener, LoginFragment.Listener {

	// Fragments
	private FragmentManager fm = getSupportFragmentManager();
	private LoginFragment loginFragment;
	private SignUpFragment signUpFragment;

	// Fragment Tags
	private static final String FRAGMENT_KEY_LOGIN = "login";
	private static final String FRAGMENT_KEY_SIGNUP = "signup";

	@BindView(R.id.ivLogo) ImageView ivLogo;

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
		ButterKnife.bind(this);

		if(savedInstanceState != null) {
			loginFragment = (LoginFragment) fm.findFragmentByTag(FRAGMENT_KEY_LOGIN);
			signUpFragment = (SignUpFragment) fm.findFragmentByTag(FRAGMENT_KEY_SIGNUP);
		}
		if(loginFragment == null)
			loginFragment = LoginFragment.newInstance();
		if(signUpFragment == null)
			signUpFragment = SignUpFragment.newInstance();

		ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) ivLogo.getLayoutParams();
		switch(getResources().getConfiguration().orientation) {
			case Configuration.ORIENTATION_PORTRAIT:
				params.verticalBias = 0.35f; // here is one modification for example. modify anything else you want :)
				ivLogo.setLayoutParams(params);
				break;
			case Configuration.ORIENTATION_LANDSCAPE:
				params.verticalBias = 0.0f; // here is one modification for example. modify anything else you want :)
				ivLogo.setLayoutParams(params);
				break;
		}
		if(!(loginFragment.isAdded() || signUpFragment.isAdded()))
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
