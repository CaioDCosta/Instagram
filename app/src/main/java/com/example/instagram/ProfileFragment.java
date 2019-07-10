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

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileFragment extends Fragment {

	@BindView(R.id.btnSignOut) Button btnSignOut;
	private Listener listener;

	public ProfileFragment() {
		// Required empty public constructor
	}

	interface Listener {
		public void onSignOut();
	}

	public static ProfileFragment newInstance() {
		return new ProfileFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_profile, container, false);
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof Listener) {
			listener = (Listener) context;
		} else {
			throw new ClassCastException(context.toString()
					+ " must implement MyListFragment.OnItemSelectedListener");
		}
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		ButterKnife.bind(this, view);
		btnSignOut.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.onSignOut();
			}
		});
	}
}
