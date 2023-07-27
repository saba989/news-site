package com.ixm.core.solr.utils;

import java.util.Arrays;

public class ResponseEntity {
	private String id;
	private String[] pageText;
	private String[] title;

	public ResponseEntity(final String id, final String[] pageText, final String[] title) {
		this.id = id;
		this.pageText = Arrays.copyOf(pageText, pageText.length);
		this.title = Arrays.copyOf(title, title.length);
	}
}
