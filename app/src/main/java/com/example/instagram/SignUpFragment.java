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
	private Listener listener;

	@BindView(R.id.btnSignup) Button btnSignup;
	@BindView(R.id.etPassword) EditText etPassword;
	@BindView(R.id.etPasswordConfirm) EditText etPasswordConfirm;
	@BindView(R.id.etUsername) EditText etUsername;

	public SignUpFragment() {
		// Required empty public constructor
	}

	// Interface for passing username and password back to MainActivity
	public interface Listener {
		public void onSignUp(String username, String password);
	}

	public static SignUpFragment newInstance() {
		return new SignUpFragment();
	}

	// Store the listener (activity) that will have events fired once the fragment is attached
	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof Listener) {
			listener = (Listener) context;
		} else {
			throw new ClassCastException(context.toString()
					+ " must implement SignUpFragment.OnSignUpListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_sign_up, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		ButterKnife.bind(this, view);
	}

	@Override
	public void onResume() {
		super.onResume();
		etPassword.setText("");
		etUsername.setText("");
		btnSignup.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!etPassword.getText().toString().equals(etPasswordConfirm.getText().toString())) {
					Toast.makeText(getContext(), "Passwords do not match!", Toast.LENGTH_SHORT).show();
					return;
				}
				listener.onSignUp(etUsername.getText().toString(), etUsername.getText().toString());
			}
		});
	}
}
