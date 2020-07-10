package org.silentsoft.arguments.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Argument implements Comparable<Argument> {
	
	private String key;
	
	private List<String> values;
	
	private Argument() { }
	
	public String getKey() {
		return key;
	}
	
	public String getValue() {
		return values.isEmpty() ? null : String.join(" ", values);
	}
	
	public List<String> getValues() {
		return values;
	}
	
	protected static Argument of(String key) {
		return of(key, new String[] {});
	}
	
	protected static Argument of(String key, String... values) {
		Argument argument = new Argument();
		argument.key = key;
		argument.values = new ArrayList<String>(Arrays.asList(values));
		return argument;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + key.hashCode();
		result = prime * result + values.hashCode();
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj == null ? false : toString().equals(obj.toString());
	}
	
	@Override
	public int compareTo(Argument o) {
		return toString().compareTo(o.toString());
	}
	
	@Override
	public String toString() {
		return values.isEmpty() ? key : String.format("%s=%s", key, String.join(" ", values));
	}
	
}
