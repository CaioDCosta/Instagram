package com.example.instagram;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.instagram.utils.Photos;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.example.instagram.utils.Photos.PICK_PHOTO_CODE;

public class ProfileFragment extends Fragment {

	@BindView(R.id.btnSignOut)      Button btnSignOut;
	@BindView(R.id.btnAddPicture)   Button btnAddPicture;
	@BindView(R.id.tvUsername)      TextView tvUsername;
	@BindView(R.id.ivProfile)       ImageView ivProfile;

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
	public void onStart() {
		super.onStart();
		btnSignOut.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.onSignOut();
			}
		});
		ParseUser user = ParseUser.getCurrentUser();
		if(user != null) {
			tvUsername.setText(user.getUsername());
			ParseFile profilePicture = user.getParseFile("profilePicture");
			if(profilePicture != null) {
				String url = profilePicture.getUrl();
				if(url != null)
					Glide.with(getContext()).load(url.replace("http", "https"))
						.placeholder(R.drawable.instagram_user_filled_24)
						.error(R.drawable.instagram_user_filled_24)
						.bitmapTransform(new CropCircleTransformation(getContext()))
						.into(ivProfile);
			}
		}
		btnAddPicture.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_PICK,
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
					// Bring up gallery to select a photo
					startActivityForResult(intent, PICK_PHOTO_CODE);
				}
			}
		});
		getFragmentManager().beginTransaction().replace(R.id.flProfilePosts, ViewCurUserFragment.newInstance(ParseUser.getCurrentUser()), "view_user").commit();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		ButterKnife.bind(this, view);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		final Bitmap selectedImage = Photos.onActivityResult(requestCode, resultCode, data, getActivity(), null, ivProfile);
		if(selectedImage == null) return;
		Glide.with(getContext()).load(bitmapToByte(selectedImage)).bitmapTransform(new CropCircleTransformation(getContext())).into(ivProfile);
		ParseUser.getCurrentUser().put("profilePicture", Photos.getParseFile(selectedImage));
		ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
			@Override
			public void done(ParseException e) {
				if(e == null) {
					Log.d("LoginActivity", "Profile picture successful!");
				}
				else {
					Log.e("LoginActivity", "Profile picture failed");
				}
			}
		});
	}

	private byte[] bitmapToByte(Bitmap bitmap){
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		byte[] byteArray = stream.toByteArray();
		return byteArray;
	}
}
