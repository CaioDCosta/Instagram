package com.example.instagram;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.instagram.model.Interaction;
import com.example.instagram.model.Post;
import com.example.instagram.onClicks.OnClickLike;
import com.example.instagram.utils.Time;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

	private List<Post> posts;
	private Context context;
	private OnPostClickListener listener;
	private boolean isGrid;

	interface OnPostClickListener {
		public void onCardClick(Post post, boolean isSelected);
		public void onProfileClick(ParseUser user);
	}

	public PostAdapter(List<Post> posts, Context context, OnPostClickListener listener, boolean isGrid) {
		this.posts = posts;
		this.context = context;
		this.listener = listener;
		this.isGrid = isGrid;
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
		LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
		View view;

		// Inflate the custom layout
		if(isGrid) {
			view = inflater.inflate(R.layout.item_post_grid, viewGroup, false);
		} else {
			view = inflater.inflate(R.layout.item_post, viewGroup, false);
		}


		// Return a new holder instance
		final ViewHolder viewHolder = new ViewHolder(view);
		viewHolder.cvBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.onCardClick(viewHolder.post, viewHolder.ibLike.isSelected());
			}
		});
		viewHolder.ibComment.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.onCardClick(viewHolder.post, viewHolder.ibLike.isSelected());
			}
		});
		return viewHolder;
	}

	@Override
	public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
		// Get the data model based on position
		Post post = posts.get(i);

		viewHolder.post = post;

		// Set item views based on your views and data model
		viewHolder.tvUsername.setText(post.getUser().getUsername());
		viewHolder.tvLikeCount.setText(String.valueOf(post.getLikes()));

		Glide.with(context).load(post.getImage().getUrl().replace("http", "https"))
				.placeholder(android.R.drawable.ic_menu_report_image)
				.error(android.R.drawable.ic_menu_report_image)
				.into(viewHolder.ivPicture);

		ParseFile profilePicture = post.getUser().getParseFile("profilePicture");

		Interaction.Query query = new Interaction.Query();
		query.getLikes().onPost(post).byUser(ParseUser.getCurrentUser()).getFirstInBackground(new GetCallback<Interaction>() {
			@Override
			public void done(Interaction object, ParseException e) {
				viewHolder.ibLike.setSelected(object != null);
			}
		});

		viewHolder.ibLike.setOnClickListener(new OnClickLike(post, viewHolder.tvLikeCount));

		if(profilePicture != null)
			Glide.with(context).load(profilePicture.getUrl()
				.replace("http", "https"))
				.placeholder(R.drawable.instagram_user_filled_24)
				.error(R.drawable.instagram_user_filled_24)
				.bitmapTransform(new CropCircleTransformation(context))
				.fitCenter().into(viewHolder.ivProfile);
		else {
			Glide.with(context).load(R.drawable.instagram_user_filled_24)
					.error(R.drawable.instagram_user_filled_24)
					.bitmapTransform(new CropCircleTransformation(context))
					.fitCenter().into(viewHolder.ivProfile);
		}

		viewHolder.tvDescription.setText(post.getDescription());
		viewHolder.tvTime.setText(Time.getRelativeTimeAgo(post.getCreatedAt()));

		viewHolder.tvUsername.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.onProfileClick(viewHolder.post.getUser());
			}
		});
		viewHolder.ivProfile.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.onProfileClick(viewHolder.post.getUser());
			}
		});
	}

	@Override
	public int getItemCount() {
		return posts.size();
	}

	public void clear() {
		posts.clear();
		notifyDataSetChanged();
	}

	public void addAll(List<Post> list) {
		posts.addAll(list);
		notifyDataSetChanged();
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
		@BindView(R.id.tvLikeCount)     TextView tvLikeCount;
		@BindView(R.id.tvTime)          TextView tvTime;

		public Post post;

		public ViewHolder(View view) {
			super(view);
			ButterKnife.bind(this, view);
		}


	}
}
