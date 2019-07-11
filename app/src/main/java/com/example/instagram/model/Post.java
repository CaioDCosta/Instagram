package com.example.instagram.model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("Post")
public class Post extends ParseObject {
	private static final String KEY_DESCRIPTION = "description";
	private static final String KEY_IMAGE = "image";
	private static final String KEY_USER = "user";
	private static final String KEY_CREATED = "createdAt";

	// Required empty constructor
	public Post() {}

	// Getters
	public String getDescription() {
		return getString(KEY_DESCRIPTION);
	}
	public ParseFile getImage() {
		return getParseFile(KEY_IMAGE);
	}
	public ParseUser getUser() {
		return getParseUser(KEY_USER);
	}

	// Setters
	public void setDescription(String description) {
		put(KEY_DESCRIPTION, description);
	}
	public void setImage(ParseFile image) {
		put(KEY_IMAGE, image);
	}
	public void setUser(ParseUser user) {
		put(KEY_USER, user);
	}

	public static class Query extends ParseQuery<Post> {
		public Query() {
			super(Post.class);
		}

		public Query byUser(ParseUser user) {
			whereEqualTo(KEY_USER, user);
			return this;
		}

		public Query byNewestFirst() {
			orderByDescending(KEY_CREATED);
			return this;
		}

		public Query limit(int num) {
			setLimit(num);
			return this;
		}

		public Query withUser() {
			include(KEY_USER);
			return this;
		}

		public Query skip(int num) {
			setSkip(num);
			return this;
		}
	}
}
