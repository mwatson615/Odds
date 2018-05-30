package com.rosebay.odds.model;

import com.google.gson.annotations.SerializedName;

public class ImageItem{

	@SerializedName("link")
	private String link;

	public String getLink(){
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
}