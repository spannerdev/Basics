package com.spanner.basics.config;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.spanner.basics.Basics;

import java.util.HashMap;
import java.util.Map;

public class Config {

	Object configDocument;
	Basics basics;
	protected Config(Basics basics, Object configDocument) {
		this.basics = basics;
		this.configDocument = configDocument;
	}

	public <T> T get(String path) {
		try {
			return JsonPath.read(this.configDocument, "$." + path);
		} catch (PathNotFoundException e) {
			basics.getLogger().error("PathNotFoundException while getting from config: "+path);
			return null;
		}
	}

}
