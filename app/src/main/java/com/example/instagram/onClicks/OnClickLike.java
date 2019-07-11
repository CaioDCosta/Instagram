package com.example.instagram.onClicks;

import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.instagram.model.Interaction;
import com.example.instagram.model.Post;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class OnClickLike implements View.OnClickListener {

	private Post post;
	private ImageButton ibParent;
	private TextView tvParent;
	private TextView tvLikeCount;

	public OnClickLike(Post post, TextView tvLikeCount) {
		this(post, tvLikeCount, null, tvLikeCount);
	}

	public OnClickLike(Post post, TextView tvLikeCount, ImageButton ibParent, TextView tvParent) {
		this.post = post;
		this.ibParent = ibParent;
		this.tvParent = tvParent;
		this.tvLikeCount = tvLikeCount;
	}

	@Override
	public void onClick(View v) {
		boolean isLiked = v.isSelected();
		int likes = Integer.parseInt(tvParent.getText().toString());
		v.setSelected(!v.isSelected());
		if (isLiked) {
			tvLikeCount.setText(String.valueOf(Integer.parseInt(tvParent.getText().toString()) - 1));
			Interaction.Query query = new Interaction.Query();
			query.getLikes().byUser(ParseUser.getCurrentUser()).onPost(post);
			query.getFirstInBackground(new GetCallback<Interaction>() {
				@Override
				public void done(Interaction object, ParseException e) {
					if(e == null) {
						try {
							object.delete();
							Log.d("LoginActivity", "Unlike successful");
						} catch (ParseException e1) {
							e1.printStackTrace();
						}
					}
				}
			});
		}
		else {
			tvLikeCount.setText(String.valueOf(Integer.parseInt(tvParent.getText().toString()) + 1));
			Interaction interaction = new Interaction();
			interaction.setComment("");
			interaction.setPost(post);
			interaction.setUser(ParseUser.getCurrentUser());
			interaction.saveInBackground(new SaveCallback() {
				@Override
				public void done(ParseException e) {
					if (e == null) {
						Log.d("LoginActivity", "Like successful");
					} else {
						Log.e("LoginActivity", "Like failed");
						e.printStackTrace();
					}
				}
			});
		}
		if(ibParent != null) {
			ibParent.setSelected(v.isSelected());
			tvParent.setText(tvLikeCount.getText());
		}
	}
}
