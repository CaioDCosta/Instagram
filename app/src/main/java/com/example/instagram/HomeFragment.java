package com.example.instagram;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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

public class HomeFragment extends Fragment {

	private EndlessRecyclerViewScrollListener scrollListener;
	private static final int PAGE_SIZE = 5;
	private int page = 0;
	private List<Post> posts;
	private PostAdapter adapter;

	@BindView(R.id.rvFeed)      RecyclerView rvFeed;

	public HomeFragment() {
		// Required empty public constructor
	}

	// TODO: Rename and change types and number of parameters
	public static HomeFragment newInstance() {
		HomeFragment fragment = new HomeFragment();
		return fragment;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_home, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		ButterKnife.bind(this, view);
		posts = new ArrayList<Post>();
		// Create adapter passing in the posts
		adapter = new PostAdapter(posts, getContext(), new PostAdapter.OnCardClick() {
			@Override
			public void onCardClick(Post post) {
				FragmentManager fm = getFragmentManager();
				DetailFragment detailFragment = DetailFragment.newInstance(post);
				detailFragment.show(fm,null);
			}
		});
		// Attach the adapter to the recyclerview to populate items
		rvFeed.setAdapter(adapter);
		// Set layout manager to position the items
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
		rvFeed.setLayoutManager(linearLayoutManager);
		// Retain an instance for resetting state if needed
		scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
			@Override
			public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
				loadNextDataFromApi();
			}
		};
		// Adds the scroll listener to RecyclerView
		rvFeed.addOnScrollListener(scrollListener);
		loadNextDataFromApi();
	}

	private void loadNextDataFromApi() {
		Log.d("MainActivity", "Loading more data from API");
		Post.Query query = new Post.Query();
		query.withUser().limit(PAGE_SIZE).skip(page++ * PAGE_SIZE).byNewestFirst();
		query.findInBackground(new FindCallback<Post>() {
			@Override
			public void done(List<Post> objects, ParseException e) {
				for(Post post : objects) {
					posts.add(post);
					adapter.notifyItemInserted(adapter.getItemCount() - 1);
				}
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
				Snackbar sb = Snackbar.make(rvFeed, "New post created!", Snackbar.LENGTH_LONG)
						.setAction("See post", new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								rvFeed.scrollToPosition(0);
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

	public void onPost() {
		page = 0;
		loadNextDataFromApi();
	}


	public void onPostComplete() {
		loadMostRecentPost();
	}
}
