package com.example.instagram;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ProfilePostFragment extends Fragment {
	public ProfilePostFragment() {
		// Required empty public constructor
	}

	public static ProfilePostFragment newInstance(String param1, String param2) {
		return new ProfilePostFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_profile_post, container, false);
	}
}
