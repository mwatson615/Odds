package com.rosebay.odds.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ImageResponse {

	@SerializedName("items")
	private List<ImageItem> items;

	public List<ImageItem> getItems(){
		return items;
	}

	public void setItems(List<ImageItem> items) {
		this.items = items;
	}
}