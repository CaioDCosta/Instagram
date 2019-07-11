package com.example.instagram;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.example.instagram.model.Post;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements ProfileFragment.Listener, ComposeFragment.Listener {

	// Fragments
	private HomeFragment homeFragment = HomeFragment.newInstance();
	private ComposeFragment composeFragment = ComposeFragment.newInstance();
	private ProfileFragment profileFragment = ProfileFragment.newInstance();

	private static final String FRAGMENT_KEY_HOME = "home";
	private static final String FRAGMENT_KEY_COMPOSE = "post";
	private static final String FRAGMENT_KEY_PROFILE = "profile";

	// savedInstanceState keys
	private static final String KEY_FRAGMENT_STATE = "state";

	// Current fragment
	private Fragment activeFragment;
	private final FragmentManager fm = getSupportFragmentManager();
	private FragmentTransaction ft;

	// Main Activity bottom navigation
	@BindView(R.id.bottom_navigation)   BottomNavigationView bottom_navigation;

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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
		ParseUser currentUser = ParseUser.getCurrentUser();

		if(savedInstanceState != null) {
			homeFragment = (HomeFragment) fm.findFragmentByTag(FRAGMENT_KEY_HOME);
			composeFragment = (ComposeFragment) fm.findFragmentByTag(FRAGMENT_KEY_COMPOSE);
			profileFragment = (ProfileFragment) fm.findFragmentByTag(FRAGMENT_KEY_PROFILE);

			activeFragment = fm.findFragmentByTag(savedInstanceState.getString(KEY_FRAGMENT_STATE));
			savedInstanceState.remove(KEY_FRAGMENT_STATE);
		}

		if(!composeFragment.isAdded()) fm.beginTransaction().add(R.id.flPlaceholder, composeFragment, FRAGMENT_KEY_COMPOSE).hide(composeFragment).commit();
		if(!profileFragment.isAdded()) fm.beginTransaction().add(R.id.flPlaceholder, profileFragment, FRAGMENT_KEY_PROFILE).hide(profileFragment).commit();
		if(!homeFragment.isAdded()) fm.beginTransaction().add(R.id.flPlaceholder, homeFragment, FRAGMENT_KEY_HOME).hide(homeFragment).commit();

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
			displayFragment(homeFragment);
		}
		else {
			displayFragment(activeFragment);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString(KEY_FRAGMENT_STATE, activeFragment.getTag());
		super.onSaveInstanceState(outState);
	}

	private boolean displayFragment(Fragment fragment) {
		if(fragment == null) return false;

		if(activeFragment != null && activeFragment.equals(fragment)) return true; // Already in state

		ft = fm.beginTransaction();
		if(activeFragment != null) ft.hide(activeFragment);
		ft.show(fragment);
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

	private void signout() {
		ParseUser.logOutInBackground(new LogOutCallback() {
			@Override
			public void done(ParseException e) {
				if (e == null) {
					Log.d("LoginActivity", "Logout succesful!");
					Intent intent = new Intent(MainActivity.this, LoginActivity.class);
					startActivity(intent);
				} else {
					Log.d("LoginActivity", "Logout failed.");
					e.printStackTrace();
				}
			}
		});

	}
}
