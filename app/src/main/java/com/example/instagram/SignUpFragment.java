package com.example.instagram;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SignUpFragment extends Fragment {
	private static final String KEY_USERNAME = "username";
	private static final String KEY_PASSWORD = "password";
	private OnSignUpListener listener;

	private String username;
	private String password;

	@BindView(R.id.btnSignup) Button btnSignup;
	@BindView(R.id.etPassword) EditText etPassword;
	@BindView(R.id.etPasswordConfirm) EditText etPasswordConfirm;
	@BindView(R.id.etUsername) EditText etUsername;

	public SignUpFragment() {
		// Required empty public constructor
	}

	// Interface for passing username and password back to MainActivity
	public interface OnSignUpListener {
		public void onSignedUp(String username, String password);
	}

	public static SignUpFragment newInstance(String username, String password) {
		SignUpFragment fragment = new SignUpFragment();
		Bundle args = new Bundle();
		args.putString(KEY_USERNAME, username);
		args.putString(KEY_PASSWORD, password);
		fragment.setArguments(args);
		return fragment;
	}

	// Store the listener (activity) that will have events fired once the fragment is attached
	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof OnSignUpListener) {
			listener = (OnSignUpListener) context;
		} else {
			throw new ClassCastException(context.toString()
					+ " must implement SignUpFragment.OnSignUpListener");
		}
	}

	// Now we can fire the event when the user selects something in the fragment
	public void onSomeClick(View v) {
		listener.onSignedUp(etUsername.getText().toString(), etPassword.getText().toString());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
		if(savedInstanceState != null) {
			username = savedInstanceState.getString(KEY_USERNAME);
			password = savedInstanceState.getString(KEY_PASSWORD);
		}
		return view;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		ButterKnife.bind(this, view);
		if (getArguments() != null) {
			username = getArguments().getString(KEY_USERNAME);
			if(username == null) username = "";
			password = getArguments().getString(KEY_PASSWORD);
			if(password == null) password = "";
			etUsername.setText(username);
			etPassword.setText(password);
		}
		btnSignup.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!etPassword.getText().toString().equals(etPasswordConfirm.getText().toString())) {
					Toast.makeText(getContext(), "Passwords do not match!", Toast.LENGTH_SHORT).show();
					return;
				}

			}
		});
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		outState.putString(KEY_USERNAME, username);
		outState.putString(KEY_PASSWORD, password);
		super.onSaveInstanceState(outState);
	}
}
