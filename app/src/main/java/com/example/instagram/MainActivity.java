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
import android.widget.Toast;

import com.example.instagram.model.Post;
import com.parse.LogInCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
		implements SignUpFragment.Listener, LoginFragment.Listener,
		ProfileFragment.Listener, ComposeFragment.Listener {


	// Fragments
	private FragmentTransaction ft;
	private LoginFragment loginFragment;
	private SignUpFragment signUpFragment;
	private HomeFragment homeFragment;
	private ComposeFragment composeFragment;
	private ProfileFragment profileFragment;


	// Fragment Tags
	private static final String FRAGMENT_KEY_LOGIN = "login";
	private static final String FRAGMENT_KEY_SIGNUP = "signup";
	private static final String FRAGMENT_KEY_HOME = "home";
	private static final String FRAGMENT_KEY_COMPOSE = "post";
	private static final String FRAGMENT_KEY_PROFILE = "profile";


	// savedInstanceState keys
	private static final String KEY_FRAGMENT_STATE = "state";
	private static final String KEY_LOGGED_IN = "loggedin";

	// Current fragment
	private String fragmentState;

	// Mapping from Tag to fragments
	private Map<String, Fragment> fragmentMap;

	// Is the user currently logged in?
	private boolean loggedIn = false;

	// Main Activity bottom navigation
	@BindView(R.id.bottom_navigation)   BottomNavigationView bottom_navigation;


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
		displayFragment(FRAGMENT_KEY_SIGNUP);
	}

	@Override
	public void onLoginDisplayed() {
		fragmentState = FRAGMENT_KEY_LOGIN;
	}

	// ProfileFragment Listener
	@Override
	public void onSignOut() {
		signout();
	}

	// ComposeFragment Listener
	@Override
	public void onPost(Post post) {
		homeFragment.onPost();
		displayFragment(FRAGMENT_KEY_HOME);
	}

	@Override
	public void onPostComplete() {
		homeFragment.onPostComplete();
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
			composeFragment = (ComposeFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_KEY_COMPOSE);
			profileFragment = (ProfileFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_KEY_PROFILE);

			loggedIn = savedInstanceState.getBoolean(KEY_LOGGED_IN);
			fragmentState = savedInstanceState.getString(KEY_FRAGMENT_STATE);
			savedInstanceState.remove(KEY_LOGGED_IN);
			savedInstanceState.remove(KEY_FRAGMENT_STATE);
		}

		// Initialize fragments and map if they are null
		if(fragmentMap == null)     { fragmentMap = new HashMap<String, Fragment>(); }

		if(loginFragment == null )  { loginFragment = LoginFragment.newInstance(); }
		if(signUpFragment == null)  { signUpFragment = SignUpFragment.newInstance(); }
		if(composeFragment == null) { composeFragment = ComposeFragment.newInstance(); }
		if(profileFragment == null) { profileFragment = ProfileFragment.newInstance(); }
		if(homeFragment == null)    { homeFragment = HomeFragment.newInstance(); }

		fragmentMap.put(FRAGMENT_KEY_LOGIN, loginFragment);
		fragmentMap.put(FRAGMENT_KEY_SIGNUP, signUpFragment);
		fragmentMap.put(FRAGMENT_KEY_COMPOSE, composeFragment);
		fragmentMap.put(FRAGMENT_KEY_PROFILE, profileFragment);
		fragmentMap.put(FRAGMENT_KEY_HOME, homeFragment);

		bottom_navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
				switch (menuItem.getItemId()) {
					case R.id.action_home:
						return displayFragment(FRAGMENT_KEY_HOME);
					case R.id.action_compose:
						return displayFragment(FRAGMENT_KEY_COMPOSE);
					case R.id.action_profile:
						return displayFragment(FRAGMENT_KEY_PROFILE);
				}
				return false;
			}
		});
		//TODO set reselected navigation listener?
		if(fragmentState == null) {
			if (currentUser != null) {
				loggedIn = true;
				displayFragment(FRAGMENT_KEY_HOME);
			} else {
				loggedIn = false;
				displayFragment(FRAGMENT_KEY_LOGIN);
			}
		}
	}

	private boolean displayFragment(String fragmentKey) {
		if(!fragmentMap.containsKey(fragmentKey)) return false;

		if(loggedIn) bottom_navigation.setVisibility(View.VISIBLE);
		else bottom_navigation.setVisibility(View.GONE);

		if(fragmentKey.equals(fragmentState)) return true; // Already in state

		FragmentManager fm = getSupportFragmentManager();
		ft = fm.beginTransaction();
		ft.replace(R.id.flPlaceholder, fragmentMap.get(fragmentKey), fragmentKey);
		if(fragmentKey.equals(FRAGMENT_KEY_SIGNUP)) ft.addToBackStack(fragmentState);
		fragmentState = fragmentKey;
		ft.commit();

		// Set bottom navigation icon
		switch(fragmentKey) {
			case FRAGMENT_KEY_HOME:
				bottom_navigation.setSelectedItemId(R.id.action_home);
				break;
			case FRAGMENT_KEY_COMPOSE:
				bottom_navigation.setSelectedItemId(R.id.action_compose);
				break;
			case FRAGMENT_KEY_PROFILE:
				bottom_navigation.setSelectedItemId(R.id.action_profile);
				break;
			default:
				bottom_navigation.setSelectedItemId(R.id.action_home);
		}
		return true;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString(KEY_FRAGMENT_STATE, fragmentState);
		outState.putBoolean(KEY_LOGGED_IN, loggedIn);
		super.onSaveInstanceState(outState);
	}

	private void signout() {
		if(!loggedIn) return;
		ParseUser.logOutInBackground(new LogOutCallback() {
			@Override
			public void done(ParseException e) {
				if(e == null) {
					loggedIn = false;
					displayFragment(FRAGMENT_KEY_LOGIN);

					Log.d("MainActivity", "Logout succesful!");
				}
				else {
					loggedIn = true;
					Log.d("MainActivity", "Logout failed.");
					e.printStackTrace();
				}
			}
		});

	}

	private void signup(final String username, final String password) {
		if(loggedIn) return;
		ParseUser user = new ParseUser();
		// Set core properties
		user.setUsername(username);
		user.setPassword(password);
		// Invoke signUpInBackground
		user.signUpInBackground(new SignUpCallback() {
			public void done(ParseException e) {
				if (e == null) {
					login(username, password);
					Log.d("MainActivity", "Signup successful!");
				} else {
					loggedIn = false;
					Log.e("MainActivity", "Signup failure.");
					e.printStackTrace();
				}
			}
		});
	}

	private void login(final String username, final String password) {
		if(loggedIn) return;
		ParseUser.logInInBackground(username, password, new LogInCallback() {
			@Override
			public void done(ParseUser user, ParseException e) {
				if(e == null) {
					loggedIn = true;
					displayFragment(FRAGMENT_KEY_HOME);
					Log.d("MainActivity", "Login successful!");
				}
				else {
					Toast.makeText(MainActivity.this, "Login failed! Check your username and password.", Toast.LENGTH_SHORT).show();
					Log.e("MainActivity", "Login failure.");
					loggedIn = false;
					e.printStackTrace();
				}
			}
		});
	}
}
