package com.example.instagram.onClicks;

import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.example.instagram.model.Interaction;
import com.example.instagram.model.Post;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class OnClickLike implements View.OnClickListener {

	private Post post;
	private ImageButton parent;

	public OnClickLike(Post post, ImageButton parent) {
		this.post = post;
		this.parent = parent;
	}

	@Override
	public void onClick(View v) {
		boolean isLiked = v.isSelected();
		v.setSelected(!v.isSelected());
		if(parent != null) {
			parent.setSelected(v.isSelected());
		}
		if (isLiked) {
			Interaction.Query query = new Interaction.Query();
			query.getLikes().withUser(ParseUser.getCurrentUser()).onPost(post);
			query.getFirstInBackground(new GetCallback<Interaction>() {
				@Override
				public void done(Interaction object, ParseException e) {
					if(e == null) {
						try {
							object.delete();
							Log.d("MainActivity", "Unlike successful");
						} catch (ParseException e1) {
							e1.printStackTrace();
						}
					}
				}
			});
		}
		else {
			Interaction interaction = new Interaction();
			interaction.setComment("");
			interaction.setPost(post);
			interaction.setUser(ParseUser.getCurrentUser());
			interaction.saveInBackground(new SaveCallback() {
				@Override
				public void done(ParseException e) {
					if (e == null) {
						Log.d("MainActivity", "Like successful");
					} else {
						Log.e("MainActivity", "Like failed");
						e.printStackTrace();
					}
				}
			});
		}
	}
}
