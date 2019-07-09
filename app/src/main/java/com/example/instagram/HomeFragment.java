package com.example.instagram;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.instagram.model.Post;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeFragment extends Fragment {

	private EndlessRecyclerViewScrollListener scrollListener;
	private static final int PAGE_SIZE = 20;
	private int page; // TODO persist with life cycle
	private List<Post> posts;
	private PostAdapter adapter;

	@BindView(R.id.rvFeed) RecyclerView rvFeed;

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
		adapter = new PostAdapter(posts, getContext());
		// Attach the adapter to the recyclerview to populate items
		rvFeed.setAdapter(adapter);
		// Set layout manager to position the items
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
		rvFeed.setLayoutManager(linearLayoutManager);
		// Retain an instance for resetting state if needed
		scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
			@Override
			public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
				//TODO figure out how to stop scrolling
				loadNextDataFromApi(page);
			}
		};
		// Adds the scroll listener to RecyclerView
		rvFeed.addOnScrollListener(scrollListener);
		loadNextDataFromApi(0);
	}

	private void loadNextDataFromApi(int page) {
		Post.Query query = new Post.Query();
		query.withUser().getTop();
		query.findInBackground(new FindCallback<Post>() {
			@Override
			public void done(List<Post> objects, ParseException e) {
				for(Post post: objects) {
					posts.add(post);
					adapter.notifyItemInserted(adapter.getItemCount() - 1);
				}
			}
		});
	}
}
