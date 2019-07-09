package com.example.instagram;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.instagram.model.Post;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

	private List<Post> posts;
	private Context context;
	public static final int VIEW_TYPE_LOADING = 0;
	public static final int VIEW_TYPE_ACTIVITY = 1;

	public PostAdapter(List<Post> posts, Context context) {
		this.posts = posts;
		this.context = context;
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
		Context context = viewGroup.getContext();
		LayoutInflater inflater = LayoutInflater.from(context);

		// Inflate the custom layout
		View view = inflater.inflate(R.layout.item_post, viewGroup, false);

		// Return a new holder instance
		ViewHolder viewHolder = new ViewHolder(view);
		return viewHolder;
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
		// Get the data model based on position
		Post post = posts.get(i);

		// Set item views based on your views and data model
		viewHolder.tvUsername.setText(post.getUser().getUsername());

		Log.d("MainActivity", post.getImage().getUrl());

		//TODO Why is this image not working??
		Glide.with(context).load("http://caiodcosta-fbu-instagram.herokuapp.com/parse/files/fbu-instagram/ed9a18727e73af7895ba62477d367baa_cdcReceipt.jpg")
				.placeholder(R.drawable.nav_logo_whiteout)
				.error(R.drawable.nav_logo_whiteout)
				.into(viewHolder.ivPicture);

		//TODO add profile pictures
		//Glide.with(context).load(post.getUser().getProfilePicture().getUrl()).into(viewHolder.ivProfile);

		viewHolder.tvDescription.setText(post.getDescription());
	}

	@Override
	public int getItemCount() {
		return posts.size() + 1;
	}

	@Override
	public int getItemViewType(int position) {
		return position >= posts.size() ? VIEW_TYPE_LOADING : VIEW_TYPE_ACTIVITY;
	}

	@Override
	public long getItemId(int position) {
		return getItemViewType(position) == VIEW_TYPE_LOADING ? -1 : position;
	}
	

	class ViewHolder extends RecyclerView.ViewHolder {

		@BindView(R.id.ibComment)       ImageButton ibComment;
		@BindView(R.id.ibDirect)        ImageButton ibDirect;
		@BindView(R.id.ibLike)          ImageButton ibLike;
		@BindView(R.id.ibSave)          ImageButton ibSave;
		@BindView(R.id.ivPicture)       ImageView ivPicture;
		@BindView(R.id.ivProfile)       ImageView ivProfile;
		@BindView(R.id.tvUsername)      TextView tvUsername;
		@BindView(R.id.tvDescription)   TextView tvDescription;

		public ViewHolder(View view) {
			super(view);
			ButterKnife.bind(this, view);
		}
	}
}
