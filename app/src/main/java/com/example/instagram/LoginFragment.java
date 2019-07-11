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

public class LoginFragment extends Fragment {

	private Listener listener;

	View.OnClickListener onClickLogin = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Log.d("LoginActivity", "Click");
			listener.onLogin(etUsername.getText().toString(), etPassword.getText().toString());
		}
	};

	View.OnClickListener onClickSignup = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			listener.onSignUpTransition(etUsername.getText().toString(), etPassword.getText().toString());
		}
	};

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

	interface Listener {
		public void onLogin(String username, String password);
		public void onSignUpTransition(String username, String password);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_login, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		ButterKnife.bind(this, view);
	}

	@Override
	public void onResume() {
		super.onResume();
		etPassword.setText("");
		etUsername.setText("");
		btnLogin.setOnClickListener(onClickLogin);
		btnSignup.setOnClickListener(onClickSignup);
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof Listener) {
			listener = (Listener) context;
		} else {
			throw new ClassCastException(context.toString()
					+ " must implement LoginFragment.OnButtonClickListener");
		}
	}
}
