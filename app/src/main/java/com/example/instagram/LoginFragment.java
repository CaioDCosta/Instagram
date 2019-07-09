package com.example.instagram;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

	private String username;
	private String password;

	private static final String KEY_USERNAME = "username";
	private static final String KEY_PASSWORD = "password";

	private OnButtonClickListener listener;

	@BindView(R.id.btnLogin) Button btnLogin;
	@BindView(R.id.etUsername) TextInputEditText etUsername;
	@BindView(R.id.etPassword) TextInputEditText etPassword;
	@BindView(R.id.btnSignup) Button btnSignup;

	public LoginFragment() {
		// Required empty public constructor
	}

	public static LoginFragment newInstance() {
		return new LoginFragment();
	}

	interface OnButtonClickListener {
		public void onLogin(String username, String password);
		public void onSignUp(String username, String password);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment

		View view = inflater.inflate(R.layout.fragment_login, container, false);
		if (savedInstanceState != null) {
			username = savedInstanceState.getString(KEY_USERNAME);
			password = savedInstanceState.getString(KEY_PASSWORD);
		}
		return view;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		ButterKnife.bind(this, view);
		btnLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Log.d("MainActivity", "Click");
				listener.onLogin(etUsername.getText().toString(), etPassword.getText().toString());
			}
		});

		btnSignup.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.onSignUp(etUsername.getText().toString(), etPassword.getText().toString());
			}
		});
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof OnButtonClickListener) {
			listener = (OnButtonClickListener) context;
		} else {
			throw new ClassCastException(context.toString()
					+ " must implement LoginFragment.OnButtonClickListener");
		}
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		outState.putString(KEY_USERNAME, username);
		outState.putString(KEY_PASSWORD, password);
		super.onSaveInstanceState(outState);
	}
}
