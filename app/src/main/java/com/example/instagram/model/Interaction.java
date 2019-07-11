package com.example.instagram.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("Interaction")
public class Interaction extends ParseObject {
	private static final String KEY_COMMMENT = "comment";
	private static final String KEY_POST = "post";
	private static final String KEY_USER = "user";
	private static final String KEY_CREATED = "createdAt";

	// Required empty constructor
	public Interaction() {}

//	// Getters
//	public String getComment() {
//		return getString(KEY_COMMMENT);
//	}
//	public Post getPost() {
//		return (Post) getParseObject(KEY_POST);
//	}
//	public ParseUser getUser() {
//		return getParseUser(KEY_USER);
//	}

	// Setters
	public void setComment(String comment) {
		put(KEY_COMMMENT, comment);
	}
	public void setPost(Post post) {
		put(KEY_POST, post);
	}
	public void setUser(ParseUser user) {
		put(KEY_USER, user);
	}

	public static class Query extends ParseQuery<Interaction> {
		public Query() {
			super(Interaction.class);
		}

		public Query getLikes() {
			whereEqualTo(KEY_COMMMENT, "");
			return this;
		}

		public Query getComments() {
			whereNotEqualTo(KEY_COMMMENT, "");
			return this;
		}

		public Query onPost(Post post) {
			whereEqualTo(KEY_POST, post);
			return this;
		}

		public Query withUser(ParseUser user) {
			whereEqualTo(KEY_USER, user);
			return this;
		}
	}
}
