package com.ixm.core.Magento;

public class GraphQLConstants {
	private GraphQLConstants() {
		// prevent outside initialization
	}

	public static final String QUERY = "{\"query\":\"{ products(search : \\\"MH\\\") { items{sku name} } }\"}";
}
