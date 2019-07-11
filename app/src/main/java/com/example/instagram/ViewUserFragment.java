//package com.example.instagram;
//
//
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v4.app.DialogFragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.widget.SwipeRefreshLayout;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageButton;
//import android.widget.TextView;
//
//import com.example.instagram.model.Post;
//
//import java.util.ArrayList;
//
//import butterknife.ButterKnife;
//
//public class ViewUserFragment extends DialogFragment {
//
//
//	public ViewUserFragment() {
//		// Required empty public constructor
//	}
//
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//	                         Bundle savedInstanceState) {
//		// Inflate the layout for this fragment
//		return inflater.inflate(R.layout.fragment_view_user, container, false);
//	}
//
//	@Override
//	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//		ButterKnife.bind(this, view);
//		posts = new ArrayList<Post>();
//		// Create adapter passing in the posts
//		adapter = new PostAdapter(posts, getContext(), new PostAdapter.OnCardClick() {
//			@Override
//			public void onCardClick(Post post, ImageButton ibLike, TextView tvLikeCount) {
//				FragmentManager fm = getFragmentManager();
//				DetailFragment detailFragment = DetailFragment.newInstance(post, ibLike, tvLikeCount);
//				detailFragment.show(fm,null);
//			}
//		});
//		// Attach the adapter to the recyclerview to populate items
//		rvFeed.setAdapter(adapter);
//		// Set layout manager to position the items
//		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
//		rvFeed.setLayoutManager(linearLayoutManager);
//		// Retain an instance for resetting state if needed
//		scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
//			@Override
//			public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
//				loadNextPage();
//			}
//		};
//		// Adds the scroll listener to RecyclerView
//		rvFeed.addOnScrollListener(scrollListener);
//
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
//	}
//}
