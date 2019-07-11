package com.example.instagram.onClicks;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.instagram.model.Interaction;
import com.example.instagram.model.Post;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class OnClickLike implements View.OnClickListener {

	private Post post;
	private TextView tvLikeCount;

	public OnClickLike(Post post, TextView tvLikeCount) {
		this.post = post;
		this.tvLikeCount = tvLikeCount;
	}

	@Override
	public void onClick(View v) {
		boolean isLiked = v.isSelected();
		int likes = post.getLikes();
		v.setSelected(!v.isSelected());
		if (isLiked) {
			tvLikeCount.setText(String.valueOf(likes - 1));
			Interaction.Query query = new Interaction.Query();
			query.getLikes().byUser(ParseUser.getCurrentUser()).onPost(post);
			query.getFirstInBackground(new GetCallback<Interaction>() {
				@Override
				public void done(Interaction object, ParseException e) {
					if(e == null) {
						try {
							object.delete();
							post.unlike();
							Log.d("LoginActivity", "Unlike successful");
						} catch (ParseException e1) {
							e1.printStackTrace();
						}
					}
				}
			});
		}
		else {
			tvLikeCount.setText(String.valueOf(likes + 1));
			Interaction interaction = new Interaction();
			interaction.setComment("");
			interaction.setPost(post);
			interaction.setUser(ParseUser.getCurrentUser());
			interaction.saveInBackground(new SaveCallback() {
				@Override
				public void done(ParseException e) {
					if (e == null) {
						post.like();
						Log.d("LoginActivity", "Like successful");
					} else {
						Log.e("LoginActivity", "Like failed");
						e.printStackTrace();
					}
				}
			});
		}
	}
}
