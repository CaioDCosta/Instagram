package com.example.instagram;


import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.instagram.model.Interaction;
import com.example.instagram.model.Post;
import com.example.instagram.onClicks.OnClickLike;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DetailFragment extends DialogFragment {


	private Post post;
	private ImageButton ibLikeParent;


	interface OnLikeClickListener {
		public void onLikeClick();
	}

	@BindView(R.id.ibComment)       ImageButton ibComment;
	@BindView(R.id.ibDirect)        ImageButton ibDirect;
	@BindView(R.id.ibLike)          ImageButton ibLike;
	@BindView(R.id.ibSave)          ImageButton ibSave;
	@BindView(R.id.ibPostComment)   ImageButton ibPostComment;
	@BindView(R.id.ivPicture)   	ImageView ivPicture;
	@BindView(R.id.ivProfile)       ImageView ivProfile;
	@BindView(R.id.tvUsername)  	TextView tvUsername;
	@BindView(R.id.tvDescription)   TextView tvDescription;
	@BindView(R.id.rvComments)      RecyclerView rvComments;
	@BindView(R.id.etComment)       EditText etComment;


	public static DetailFragment newInstance(Post post, ImageButton ibLike) {
		DetailFragment detailFragment = new DetailFragment();
		detailFragment.post = post;
		detailFragment.ibLikeParent = ibLike;
		return detailFragment;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		ButterKnife.bind(this, view);
		tvDescription.setText(post.getDescription());
		tvUsername.setText(post.getUser().getUsername());
		ibLike.setSelected(ibLikeParent.isSelected());
		Glide.with(getContext()).load(post.getImage().getUrl().replace("http", "https"))
				.placeholder(R.drawable.nav_logo_whiteout).error(R.drawable.nav_logo_whiteout).into(ivPicture);
		ParseUser user = ParseUser.getCurrentUser();
		ParseFile profilePicture = user.getParseFile("profilePicture");
		if(profilePicture != null)
			Glide.with(getContext()).load(profilePicture.getUrl()
					.replace("http", "https"))
					.placeholder(R.drawable.instagram_user_filled_24)
					.error(R.drawable.instagram_user_filled_24)
					.into(ivProfile);


		final CommentAdapter adapter = new CommentAdapter(getContext(), post);
		rvComments.setAdapter(adapter);
		rvComments.setLayoutManager(new LinearLayoutManager(getContext()));
		rvComments.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

		ibPostComment.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				final Interaction interaction = new Interaction();
				interaction.setUser(ParseUser.getCurrentUser());
				interaction.setPost(post);
				interaction.setComment(etComment.getText().toString());
				interaction.saveInBackground(new SaveCallback() {
					@Override
					public void done(ParseException e) {
						if(e == null) {
							Log.d("MainActivity", "Comment successful!");
							adapter.addComment(interaction);
							rvComments.smoothScrollToPosition(0);
							etComment.setText("");
						}
						else {
							Log.e("MainActivity", "Comment failed");
						}
					}
				});
			}
		});
		ibLike.setOnClickListener(new OnClickLike(post, ibLikeParent));
	}

	public DetailFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		//this.getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		// Inflate the layout for this fragment
		this.getDialog().setCanceledOnTouchOutside(true);
		return inflater.inflate(R.layout.fragment_detail, container, false);
	}

	public void onResume() {
		// Store access variables for window and blank point
		Window window = getDialog().getWindow();
		Point size = new Point();
		// Store dimensions of the screen in `size`
		Display display = window.getWindowManager().getDefaultDisplay();
		display.getSize(size);
		// Set the width of the dialog proportional to 100% of the screen width
		window.setLayout((int) (size.x * .95), WindowManager.LayoutParams.WRAP_CONTENT);
		window.setGravity(Gravity.CENTER);
		// Call super onResume after sizing
		super.onResume();
	}
}
