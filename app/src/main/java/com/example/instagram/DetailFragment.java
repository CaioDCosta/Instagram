package com.example.instagram;


import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.instagram.model.Post;
import com.example.instagram.onClicks.OnClickLike;

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
	@BindView(R.id.ivPicture)   	ImageView ivPicture;
	@BindView(R.id.ivProfile)       ImageView ivProfile;
	@BindView(R.id.tvUsername)  	TextView tvUsername;
	@BindView(R.id.tvDescription)   TextView tvDescription;

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


//		ibComment.setOnClickListener(new MyClickListener(post));
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
