package com.example.instagram;


import android.app.Activity;
import android.content.DialogInterface;
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
import com.example.instagram.utils.Time;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DetailFragment extends DialogFragment {
	private Post post;
	private boolean isLiked;

	private OnDismissListener listener;

	@BindView(R.id.ibLike)          ImageButton ibLike;
	@BindView(R.id.ibSave)          ImageButton ibSave;
	@BindView(R.id.ibPostComment)   ImageButton ibPostComment;
	@BindView(R.id.ivPicture)   	ImageView ivPicture;
	@BindView(R.id.ivProfile)       ImageView ivProfile;
	@BindView(R.id.tvUsername)  	TextView tvUsername;
	@BindView(R.id.tvDescription)   TextView tvDescription;
	@BindView(R.id.tvLikeCount)     TextView tvLikeCount;
	@BindView(R.id.tvTime)          TextView tvTime;
	@BindView(R.id.rvComments)      RecyclerView rvComments;
	@BindView(R.id.etComment)       EditText etComment;

	interface OnDismissListener {
		public void onDismiss();
	}

	public void setOnDismissListener(OnDismissListener listener) {
		this.listener = listener;
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
		if(listener != null) listener.onDismiss();
	}

	public static DetailFragment newInstance(Post post, boolean isLiked) {
		DetailFragment detailFragment = new DetailFragment();
		detailFragment.post = post;
		detailFragment.isLiked = isLiked;
		detailFragment.setRetainInstance(true);
		return detailFragment;
	}

	@Override
	public void onDestroyView() {
		if (getDialog() != null && getRetainInstance()) {
			getDialog().setDismissMessage(null);
		}
		super.onDestroyView();
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		ButterKnife.bind(this, view);
		tvDescription.setText(post.getDescription());
		tvUsername.setText(post.getUser().getUsername());
		ibLike.setSelected(isLiked);
		Glide.with(getContext()).load(post.getImage().getUrl().replace("http", "https"))
				.placeholder(android.R.drawable.ic_menu_report_image).error(android.R.drawable.ic_menu_report_image).into(ivPicture);
		ParseUser user = post.getUser();
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
							Log.d("LoginActivity", "Comment successful!");
							adapter.addComment(interaction);
							rvComments.smoothScrollToPosition(0);
							etComment.setText("");
						}
						else {
							Log.e("LoginActivity", "Comment failed");
						}
					}
				});
			}
		});
		ibLike.setOnClickListener(new OnClickLike(post, tvLikeCount));
		tvLikeCount.setText(String.valueOf(post.getLikes()));
		tvTime.setText(Time.getRelativeTimeAgo(post.getCreatedAt()));
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
