package com.example.instagram;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.instagram.model.Post;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

public class ComposeFragment extends Fragment {

	// Photo capture
	public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
	public String photoFileName = "photo.jpg";
	File photoFile;

	// Bind Views
	@BindView(R.id.ivPicture)       ImageView ivPicture;
	@BindView(R.id.btnPost)	        Button btnPost;
	@BindView(R.id.btnSelect)       Button btnSelect;
	@BindView(R.id.etDescription)   EditText etDescription;

	private Listener listener;

	interface Listener {
		void onPost(Post post);
		void onPostComplete();
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if(context instanceof Listener) {
			listener = (Listener) context;
		}
	}

	public ComposeFragment() {
		// Required empty public constructor
	}

	public static ComposeFragment newInstance() {
		return new ComposeFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		return inflater.inflate(R.layout.fragment_compose, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		ButterKnife.bind(this, view);
		btnPost.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(photoFile == null || ivPicture.getDrawable() == null) {
					Log.e("MainActivity", "No photo selected");
					Toast.makeText(getContext(), "No photo!", Toast.LENGTH_SHORT).show();
					return;
				}
				Post post = new Post();
				post.setImage(new ParseFile(photoFile));
				post.setUser(ParseUser.getCurrentUser());
				post.setDescription(etDescription.getText().toString());
				post.saveInBackground(new SaveCallback() {
					@Override
					public void done(ParseException e) {
						if(e == null) {
							listener.onPostComplete();
							Log.d("MainActivity", "Post successful!");
						}
						else {
							e.printStackTrace();
						}
					}
				});
				etDescription.setText("");
				ivPicture.setImageResource(0);
				listener.onPost(post);
			}
		});

		btnSelect.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				launchCamera(v);
			}
		});
	}

	public void launchCamera(View view) {


		// create Intent to take a picture and return control to the calling application
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// Create a File reference to access to future access
		photoFile = getPhotoFileUri(photoFileName);

		// wrap File object into a content provider
		// required for API >= 24
		// See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
		Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.example.instagram.fileprovider", photoFile);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

		// If you call startActivityForResult() using an intent that no app can handle, your app will crash.
		// So as long as the result is not null, it's safe to use the intent.
		if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
			// Start the image capture intent to take photo
			startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
		}
	}

	// Returns the File for a photo stored on disk given the fileName
	public File getPhotoFileUri(String fileName) {
		// Get safe storage directory for photos
		// Use `getExternalFilesDir` on Context to access package-specific directories.
		// This way, we don't need to request external read/write runtime permissions.
		File mediaStorageDir = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "MainActivity");

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
			Log.d("MainActivity", "Failed to create directory");
		}

		// Return the file target for the photo based on filename
		File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

		return file;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				// by this point we have the camera photo on disk
				Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
				// Load the taken image into a preview
				ivPicture.setImageBitmap(takenImage);
			} else { // Result was a failure
				Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
