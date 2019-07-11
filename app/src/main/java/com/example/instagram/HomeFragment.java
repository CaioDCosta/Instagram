package com.example.instagram;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.instagram.model.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

public class HomeFragment extends Fragment {

	private EndlessRecyclerViewScrollListener scrollListener;
	private static final int PAGE_SIZE = 10;
	private int page = 0;
	private List<Post> posts;
	private PostAdapter adapter;

	@BindView(R.id.rvFeed) RecyclerView rvFeed;
	@BindView(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;

	public HomeFragment() {
		// Required empty public constructor
	}

	public static HomeFragment newInstance() {
		HomeFragment fragment = new HomeFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_home, container, false);
	}

	@Override
	public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
		ButterKnife.bind(this, view);
		posts = new ArrayList<Post>();
		// Create adapter passing in the posts
		adapter = new PostAdapter(posts, getContext(), new PostAdapter.OnPostClickListener() {
			@Override
			public void onCardClick(final Post post, boolean isSelected) {
				FragmentManager fm = getFragmentManager();
				DetailFragment detailFragment = DetailFragment.newInstance(post, isSelected);
				detailFragment.setOnDismissListener(new DetailFragment.OnDismissListener() {
					@Override
					public void onDismiss() {
						adapter.notifyItemRangeChanged(0, adapter.getItemCount());
					}
				});
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
		}, false);
		// Attach the adapter to the recyclerview to populate items

		ScaleInAnimationAdapter scaleAdapter = new ScaleInAnimationAdapter(adapter);
		scaleAdapter.setFirstOnly(false);
		AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(scaleAdapter);
		alphaAdapter.setFirstOnly(false);
		rvFeed.setAdapter(alphaAdapter);
		// Set layout manager to position the items
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
		rvFeed.setLayoutManager(linearLayoutManager);

		// Retain an instance for resetting state if needed
		scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
			@Override
			public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
				loadNextPage();
			}
		};
		// Adds the scroll listener to RecyclerView
		rvFeed.addOnScrollListener(scrollListener);

		// Set up swipe refresh listener
		swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				loadFirstPage();
			}
		});

		// Configure swipe container colors
		swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);


		loadNextPage();
	}

	private void loadFirstPage() {
		swipeContainer.setRefreshing(true);
		page = 0;
		scrollListener.resetState();
		adapter.clear();
		loadNextPage();
	}

	private void loadNextPage() {
		Log.d("LoginActivity", "Loading more data from API");
		Post.Query query = new Post.Query();
		query.withUser().limit(PAGE_SIZE).skip(page++ * PAGE_SIZE).byNewestFirst();
		query.findInBackground(new FindCallback<Post>() {
			@Override
			public void done(List<Post> objects, ParseException e) {
				adapter.addAll(objects);
				if(swipeContainer.isRefreshing()) swipeContainer.setRefreshing(false);
			}
		});
	}

	private void loadMostRecentPost() {
		Post.Query query = new Post.Query();
		query.withUser().limit(1).byUser(ParseUser.getCurrentUser()).byNewestFirst();
		query.findInBackground(new FindCallback<Post>() {
			@Override
			public void done(List<Post> objects, ParseException e) {
				posts.add(0, objects.get(0));
				adapter.notifyItemInserted(0);
				((MainActivity) getActivity()).pbLoading.setVisibility(View.GONE);
				Snackbar sb = Snackbar.make(rvFeed, "New post created!", Snackbar.LENGTH_LONG)
						.setAction("See post", new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								rvFeed.smoothScrollToPosition(0);
							}
				});
				sb.setActionTextColor(getResources().getColor(android.R.color.holo_blue_dark));
				View view = sb.getView();
				view.setBackgroundColor(getResources().getColor(R.color.action_bar_semi_transparent_white));
				((TextView) sb.getView().findViewById(R.id.snackbar_text))
						.setTextColor(getResources().getColor(R.color.black));
				FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
				params.gravity = Gravity.TOP;
				view.setLayoutParams(params);
				sb.show();
			}
		});
	}

	public void onPostComplete() {
		loadMostRecentPost();
	}
}
