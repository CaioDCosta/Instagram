package com.example.instagram;

import android.app.Application;

import com.example.instagram.model.Interaction;
import com.example.instagram.model.Post;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApp extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		ParseObject.registerSubclass(Post.class);
		ParseObject.registerSubclass(Interaction.class);
		final Parse.Configuration config = new Parse.Configuration.Builder(this)
				.applicationId("fbu-instagram").clientKey(getString(R.string.master_key))
				.server("https://caiodcosta-fbu-instagram.herokuapp.com/parse").build();
		Parse.initialize(config);
	}
}
