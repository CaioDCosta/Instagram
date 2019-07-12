package com.example.instagram;


import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.instagram.model.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

public class ViewUserFragment extends DialogFragment {

	private List<Post> posts;
	private PostAdapter adapter;
	private static final int PAGE_SIZE = 10;
	private int page = 0;
	private EndlessRecyclerViewScrollListener scrollListener;
	private OnDismissListener listener;

	private ParseUser user;

	@BindView(R.id.rvProfile) RecyclerView rvProfile;
	@BindView(R.id.ivProfile) ImageView ivProfile;
	@BindView(R.id.tvUsername) TextView tvUsername;

	interface OnDismissListener {
		public void onDismiss();
	}

	public ViewUserFragment() {
		// Required empty public constructor
	}

	public static ViewUserFragment newInstance(ParseUser user) {
		ViewUserFragment viewUserFragment = new ViewUserFragment();
		viewUserFragment.user = user;
		viewUserFragment.setRetainInstance(true);
		return viewUserFragment;
	}

	public void setOnDismissListener(OnDismissListener listener) {
		this.listener = listener;
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
		if(listener != null) listener.onDismiss();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		this.getDialog().setCanceledOnTouchOutside(true);
		return inflater.inflate(R.layout.fragment_view_user, container, false);
	}

	@Override
	public void onDestroyView() {
		if (getDialog() != null && getRetainInstance()) {
			getDialog().setDismissMessage(null);
		}
		super.onDestroyView();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		ButterKnife.bind(this, view);
		posts = new ArrayList<Post>();
		// Create adapter passing in the posts
		adapter = new PostAdapter(posts, getContext(), new PostAdapter.OnPostClickListener() {
			@Override
			public void onCardClick(Post post, boolean isSelected) {
				FragmentManager fm = getFragmentManager();
				DetailFragment detailFragment = DetailFragment.newInstance(post, isSelected);
				detailFragment.show(fm,null);
			}

			@Override
			public void onProfileClick(ParseUser user) {
				FragmentManager fm = getFragmentManager();
				ViewUserFragment viewUserFragment = ViewUserFragment.newInstance(user);
				viewUserFragment.setOnDismissListener(new ViewUserFragment.OnDismissListener() {
					@Override
					public void onDismiss() {
						adapter.notifyItemRangeChanged(0, adapter.getItemCount());
					}
				});
				viewUserFragment.show(fm,null);
			}
		}, true);
		// Attach the adapter to the recyclerview to populate items
		AlphaInAnimationAdapter alphaInAnimationAdapter = new AlphaInAnimationAdapter(adapter);
		alphaInAnimationAdapter.setFirstOnly(false);
		ScaleInAnimationAdapter scaleInAnimationAdapter = new ScaleInAnimationAdapter(alphaInAnimationAdapter);
		scaleInAnimationAdapter.setFirstOnly(false);
		rvProfile.setAdapter(scaleInAnimationAdapter);
		// Set layout manager to position the items
		StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
//		LinearLayoutManager gridLayoutManager = new LinearLayoutManager(getContext());
		rvProfile.setLayoutManager(gridLayoutManager);
		// Retain an instance for resetting state if needed
		scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
			@Override
			public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
				loadNextPage();
			}
		};
		// Adds the scroll listener to RecyclerView
		rvProfile.addOnScrollListener(scrollListener);
		tvUsername.setText(user.getUsername());
		ParseFile profilePicture = user.getParseFile("profilePicture");
		if(profilePicture != null)
			Glide.with(getContext()).load(profilePicture.getUrl().replace("http", "https"))
			.placeholder(R.drawable.instagram_user_filled_24).error(R.drawable.instagram_user_filled_24)
			.bitmapTransform(new CropCircleTransformation(getContext())).into(ivProfile);
		loadNextPage();
//		// Set up swipe refresh listener
//		swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//			@Override
//			public void onRefresh() {
//				loadFirstPage();
//			}
//		});
//
//		// Configure swipe container colors
//		swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
//				android.R.color.holo_green_light,
//				android.R.color.holo_orange_light,
//				android.R.color.holo_red_light);
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

	public void loadNextPage() {
		Post.Query query = new Post.Query();
		query.withUser().byUser(user).limit(PAGE_SIZE).skip(page++ * PAGE_SIZE).byNewestFirst();
		query.findInBackground(new FindCallback<Post>() {
			@Override
			public void done(List<Post> objects, ParseException e) {
				for(Post post : objects) {
					posts.add(post);
					adapter.notifyItemInserted(posts.size() - 2);
				}
			}
		});
	}
}
