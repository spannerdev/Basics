package com.spanner.basics.config;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.spanner.basics.Basics;

import java.io.*;
import java.util.Map;

public class ConfigLoader {

	public static Config load(Basics basics) {
		try {
			InputStream stream = basics.getResource("config.json");
			if (stream == null) return null;
			Object document = Configuration.defaultConfiguration().jsonProvider().parse(stream,"UTF-8");
			stream.close();
			return new Config(basics,document);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
