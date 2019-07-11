package com.example.instagram;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.instagram.model.Interaction;
import com.example.instagram.model.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

	List<Interaction> comments;
	Context context;

	CommentAdapter(Context context, Post post) {
		comments = new ArrayList<Interaction>();
		Interaction.Query query = new Interaction.Query();
		query.onPost(post).getComments().withUser().byNewestFirst();
		query.findInBackground(new FindCallback<Interaction>() {
			@Override
			public void done(List<Interaction> objects, ParseException e) {
				comments.addAll(objects);
				notifyDataSetChanged();
			}
		});
		this.context = context;
	}

	public void addComment(Interaction comment) {
		comments.add(0, comment);
		notifyItemInserted(0);
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
		LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
		// Inflate the custom layout
		View view = inflater.inflate(R.layout.item_comment, viewGroup, false);
		// Return a new holder instance
		return new CommentAdapter.ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
		Interaction comment = comments.get(i);
		viewHolder.tvComment.setText(comment.getComment());
		viewHolder.tvUsername.setText(comment.getUser().getUsername());
		ParseFile profilePicture = comment.getUser().getParseFile("profilePicture");
		if(profilePicture != null)
			Glide.with(context).load(profilePicture.getUrl().replace("http", "https")).placeholder(R.drawable.instagram_user_filled_24).into(viewHolder.ivProfile);
	}

	@Override
	public int getItemCount() {
		return comments.size();
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		@BindView(R.id.tvComment) TextView tvComment;
		@BindView(R.id.tvUsername) TextView tvUsername;
		@BindView(R.id.ivProfile) ImageView ivProfile;

		public ViewHolder(@NonNull View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}
