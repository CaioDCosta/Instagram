package com.example.instagram;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements SignUpFragment.OnSignUpListener, LoginFragment.OnButtonClickListener {


	// Fragments
	private FragmentTransaction ft;
	private LoginFragment loginFragment;
	private SignUpFragment signUpFragment;
	private HomeFragment homeFragment;
	private PostFragment postFragment;
	private ProfileFragment profileFragment;


	// Fragment Tags
	private static final String FRAGMENT_KEY_LOGIN = "login";
	private static final String FRAGMENT_KEY_SIGNUP = "signup";
	private static final String FRAGMENT_KEY_HOME = "home";
	private static final String FRAGMENT_KEY_POST = "post";
	private static final String FRAGMENT_KEY_PROFILE = "profile";


	// savedInstanceState keys
	private static final String KEY_FRAGMENT_STATE = "state";
	private static final String KEY_FRAGMENT_MAP = "map";
	private static final String KEY_LOGGED_IN = "loggedin";

	// Current fragment
	private String fragmentState;

	// Mapping from Tag to fragments
	private Map<String, Fragment> fragmentMap;

	// Is the user currently logged in?
	private boolean loggedIn = false;

	// Main Activity bottom navigation
	@BindView(R.id.bottom_navigation) BottomNavigationView bottom_navigation;


	@Override
	public void onSignedUp(String username, String password) {
		signup(username, password);
	}

	@Override
	public void onLogin(String username, String password) {
		login(username, password);
	}

	@Override
	public void onSignUp(String username, String password) {
		displayFragment(FRAGMENT_KEY_SIGNUP);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
		ParseUser currentUser = ParseUser.getCurrentUser();

		if(savedInstanceState != null) {
			loginFragment = (LoginFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_KEY_LOGIN);
			signUpFragment = (SignUpFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_KEY_SIGNUP);
			homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_KEY_HOME);
			postFragment = (PostFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_KEY_POST);
			profileFragment = (ProfileFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_KEY_PROFILE);

			loggedIn = savedInstanceState.getBoolean(KEY_LOGGED_IN);
			fragmentState = savedInstanceState.getString(KEY_FRAGMENT_STATE);
			fragmentMap = (HashMap<String, Fragment>) savedInstanceState.getSerializable(KEY_FRAGMENT_MAP);
		}

		// Initialize fragments and map if they are null
		if(fragmentMap == null) {
			fragmentMap = new HashMap<String, Fragment>();
		}
		if(loginFragment == null ){
			loginFragment = LoginFragment.newInstance();
			fragmentMap.put(FRAGMENT_KEY_LOGIN, loginFragment);
		}
		if(signUpFragment == null) {
			signUpFragment = SignUpFragment.newInstance("","");
			fragmentMap.put(FRAGMENT_KEY_SIGNUP, signUpFragment);
		}
		if(homeFragment == null) {
			homeFragment = HomeFragment.newInstance();
			fragmentMap.put(FRAGMENT_KEY_HOME, homeFragment);
		}
		if(postFragment == null) {
			postFragment = PostFragment.newInstance();
			fragmentMap.put(FRAGMENT_KEY_POST, postFragment);
		}
		if(profileFragment == null) {
			profileFragment = ProfileFragment.newInstance();
			fragmentMap.put(FRAGMENT_KEY_PROFILE, profileFragment);
		}

		if(fragmentState == null) {
			if (currentUser != null) {
				fragmentState = FRAGMENT_KEY_HOME;
				loggedIn = true;
			} else {
				fragmentState = FRAGMENT_KEY_LOGIN;
			}
		}

		bottom_navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
				switch (menuItem.getItemId()) {
					case R.id.action_home:
						return displayFragment(FRAGMENT_KEY_HOME);
					case R.id.action_post:
						return displayFragment(FRAGMENT_KEY_POST);
					case R.id.action_profile:
						return displayFragment(FRAGMENT_KEY_PROFILE);
				}
				return false;
			}
		});
		//TODO set reselected navigation listener?

		displayFragment(fragmentState);
	}

	private boolean displayFragment(String fragmentKey) {
		if(!fragmentMap.containsKey(fragmentKey)) return false;
//		if(fragmentKey.equals(fragmentState)) return; // Already in state
		if(loggedIn) bottom_navigation.setVisibility(View.VISIBLE);
		else bottom_navigation.setVisibility(View.GONE);
		FragmentManager fm = getSupportFragmentManager();
		ft = fm.beginTransaction();
		ft.replace(R.id.flPlaceholder, fragmentMap.get(fragmentKey), fragmentKey);
		ft.addToBackStack(fragmentState);
		fragmentState = fragmentKey;
		ft.commit();
		return true;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString(KEY_FRAGMENT_STATE, fragmentState);
		outState.putSerializable(KEY_FRAGMENT_MAP, (Serializable) fragmentMap);
		outState.putBoolean(KEY_LOGGED_IN, loggedIn);
		super.onSaveInstanceState(outState);
	}

	private void signup(String username, String password) {
		ParseUser user = new ParseUser();
		// Set core properties
		user.setUsername(username);
		user.setPassword(password);
		// Invoke signUpInBackground
		user.signUpInBackground(new SignUpCallback() {
			public void done(ParseException e) {
				if (e == null) {
					displayFragment(FRAGMENT_KEY_HOME);
					loggedIn = true;
					Log.d("MainActivity", "Signup successful!");
				} else {
					Log.e("MainActivity", "Signup failure.");
				}
			}
		});
	}

	private void login(String username, String password) {
		ParseUser.logInInBackground(username, password, new LogInCallback() {
			@Override
			public void done(ParseUser user, ParseException e) {
				if(e == null) {
					displayFragment(FRAGMENT_KEY_HOME);
					loggedIn = true;
					Log.d("MainActivity", "Login successful!");
				}
				else {
					Log.e("MainActivity", "Login failure.");
					e.printStackTrace();
				}
			}
		});
	}
}
