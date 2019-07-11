package com.example.instagram;

import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.instagram.model.Post;
import com.parse.LogInCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
		implements SignUpFragment.Listener, LoginFragment.Listener,
		ProfileFragment.Listener, ComposeFragment.Listener {


	// Fragments
	private LoginFragment loginFragment = LoginFragment.newInstance();;
	private SignUpFragment signUpFragment = SignUpFragment.newInstance();
	private HomeFragment homeFragment = HomeFragment.newInstance();
	private ComposeFragment composeFragment = ComposeFragment.newInstance();
	private ProfileFragment profileFragment = ProfileFragment.newInstance();


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
	private Fragment activeFragment;
	private final FragmentManager fm = getSupportFragmentManager();
	private FragmentTransaction ft;

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
		displayFragment(signUpFragment);
	}

	@Override
	public void onBackPressed() {
		if(activeFragment == signUpFragment)
			activeFragment = loginFragment;
		super.onBackPressed();
	}

	// ProfileFragment Listener
	@Override
	public void onSignOut() {
		signout();
	}

	// ComposeFragment Listener
	@Override
	public void onPost(Post post) {
		displayFragment(homeFragment);
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
			loginFragment = (LoginFragment) fm.findFragmentByTag(FRAGMENT_KEY_LOGIN);
			signUpFragment = (SignUpFragment) fm.findFragmentByTag(FRAGMENT_KEY_SIGNUP);
			homeFragment = (HomeFragment) fm.findFragmentByTag(FRAGMENT_KEY_HOME);
			composeFragment = (ComposeFragment) fm.findFragmentByTag(FRAGMENT_KEY_COMPOSE);
			profileFragment = (ProfileFragment) fm.findFragmentByTag(FRAGMENT_KEY_PROFILE);

			loggedIn = savedInstanceState.getBoolean(KEY_LOGGED_IN);
			activeFragment = fm.findFragmentByTag(savedInstanceState.getString(KEY_FRAGMENT_STATE));
			savedInstanceState.remove(KEY_LOGGED_IN);
			savedInstanceState.remove(KEY_FRAGMENT_STATE);
		}

		fm.beginTransaction().add(R.id.flPlaceholder, loginFragment, FRAGMENT_KEY_LOGIN).hide(loginFragment).commit();
		fm.beginTransaction().add(R.id.flPlaceholder, signUpFragment, FRAGMENT_KEY_SIGNUP).hide(signUpFragment).commit();
		fm.beginTransaction().add(R.id.flPlaceholder, composeFragment, FRAGMENT_KEY_COMPOSE).hide(composeFragment).commit();
		fm.beginTransaction().add(R.id.flPlaceholder, profileFragment, FRAGMENT_KEY_PROFILE).hide(profileFragment).commit();
		fm.beginTransaction().add(R.id.flPlaceholder, homeFragment, FRAGMENT_KEY_HOME).hide(homeFragment).commit();

		//Todo why does first switch not work?
		bottom_navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
				switch (menuItem.getItemId()) {
					case R.id.action_home:
						return displayFragment(homeFragment);
					case R.id.action_compose:
						return displayFragment(composeFragment);
					case R.id.action_profile:
						return displayFragment(profileFragment);
				}
				return false;
			}
		});
		//TODO set reselected navigation listener?

		if(activeFragment == null) {
			if (currentUser != null) {
				loggedIn = true;
				displayFragment(homeFragment);
			} else {
				loggedIn = false;
				displayFragment(loginFragment);
			}
		}
		else {
			displayFragment(activeFragment);
		}
	}

	private boolean displayFragment(Fragment fragment) {
		if(fragment == null) return false;
		if(loggedIn) bottom_navigation.setVisibility(View.VISIBLE);
		else bottom_navigation.setVisibility(View.GONE);

		if(activeFragment != null && activeFragment.equals(fragment)) return true; // Already in state

		ft = fm.beginTransaction();
		if(activeFragment != null) ft.hide(activeFragment);
		ft.show(fragment);
		if(fragment.equals(signUpFragment))
			ft.addToBackStack(fragment.getTag());
		ft.commit();
		activeFragment = fragment;

		// Set bottom navigation icon
		if (fragment.getTag() != null) {
			switch(fragment.getTag()) {
				case FRAGMENT_KEY_HOME:
					bottom_navigation.setSelectedItemId(R.id.action_home);
					break;
				case FRAGMENT_KEY_COMPOSE:
					bottom_navigation.setSelectedItemId(R.id.action_compose);
					break;
				case FRAGMENT_KEY_PROFILE:
					bottom_navigation.setSelectedItemId(R.id.action_profile);
					break;
			}
		}
		return true;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString(KEY_FRAGMENT_STATE, activeFragment.getTag());
		outState.putBoolean(KEY_LOGGED_IN, loggedIn);
		super.onSaveInstanceState(outState);
	}

	private void signout() {
		if(!loggedIn) return;
		homeFragment.rvFeed.scrollToPosition(0);
		ParseUser.logOutInBackground(new LogOutCallback() {
			@Override
			public void done(ParseException e) {
				if(e == null) {
					loggedIn = false;
					displayFragment(loginFragment);
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
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		ParseUser.logInInBackground(username, password, new LogInCallback() {
			@Override
			public void done(ParseUser user, ParseException e) {
				if(e == null) {
					loggedIn = true;
					displayFragment(homeFragment);
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
