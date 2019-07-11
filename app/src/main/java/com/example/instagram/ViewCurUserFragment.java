package com.example.instagram;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class ViewCurUserFragment extends Fragment {
	private List<Post> posts;
	private PostAdapter adapter;
	private static final int PAGE_SIZE = 10;
	private int page = 0;
	private EndlessRecyclerViewScrollListener scrollListener;

	private ParseUser user;

	@BindView(R.id.rvProfile)
	RecyclerView rvProfile;

	public ViewCurUserFragment() {
		// Required empty public constructor
	}

	public static ViewCurUserFragment newInstance(ParseUser user) {
		ViewCurUserFragment viewCurUserFragment = new ViewCurUserFragment();
		viewCurUserFragment.user = user;
		viewCurUserFragment.setRetainInstance(true);
		return viewCurUserFragment;
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_view_user, container, false);
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

	public void loadNextPage() {
		Post.Query query = new Post.Query();
		query.withUser().byUser(user).limit(PAGE_SIZE).skip(page++ * PAGE_SIZE).byNewestFirst();
		query.findInBackground(new FindCallback<Post>() {
			@Override
			public void done(List<Post> objects, ParseException e) {
				adapter.addAll(objects);
			}
		});
	}
}
