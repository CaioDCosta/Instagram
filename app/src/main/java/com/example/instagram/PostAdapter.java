package com.example.instagram;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
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
	private OnCardClick listener;

	interface OnCardClick {
		public void onCardClick(Post post);
	}

	public PostAdapter(List<Post> posts, Context context, OnCardClick listener) {
		this.posts = posts;
		this.context = context;
		this.listener = listener;
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
		LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

		// Inflate the custom layout
		View view = inflater.inflate(R.layout.item_post, viewGroup, false);

		// Return a new holder instance
		final ViewHolder viewHolder = new ViewHolder(view);
		viewHolder.cvBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.onCardClick(viewHolder.post);
			}
		});
		return viewHolder;
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
		// Get the data model based on position
		Post post = posts.get(i);

		viewHolder.post = post;

		// Set item views based on your views and data model
		viewHolder.tvUsername.setText(post.getUser().getUsername());

		Log.d("MainActivity", post.getImage().getUrl());

		//TODO Make this less hacky?
		Glide.with(context).load(post.getImage().getUrl().replace("http", "https"))
				.placeholder(R.drawable.nav_logo_whiteout)
				.error(R.drawable.nav_logo_whiteout)
				.into(viewHolder.ivPicture);

		//TODO add profile pictures
		//Glide.with(context).load(post.getUser().getProfilePicture().getUrl()).into(viewHolder.ivProfile);

		viewHolder.tvDescription.setText(post.getDescription());
	}

	@Override
	public int getItemCount() {
		return posts.size();
	}

	class ViewHolder extends RecyclerView.ViewHolder {

		@BindView(R.id.cvBack)          CardView cvBack;
		@BindView(R.id.ibComment)       ImageButton ibComment;
		@BindView(R.id.ibDirect)        ImageButton ibDirect;
		@BindView(R.id.ibLike)          ImageButton ibLike;
		@BindView(R.id.ibSave)          ImageButton ibSave;
		@BindView(R.id.ivPicture)       ImageView ivPicture;
		@BindView(R.id.ivProfile)       ImageView ivProfile;
		@BindView(R.id.tvUsername)      TextView tvUsername;
		@BindView(R.id.tvDescription)   TextView tvDescription;
		public Post post;

		public ViewHolder(View view) {
			super(view);
			ButterKnife.bind(this, view);
		}


	}
}
