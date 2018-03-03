package com;

public class Item {
	private String path;
	
	/**
	 * Constructor
	 * @param path
	 */
	Item(String path) {
		this.path = path;
	}
	
	/**
	 * path getter
	 * @return
	 */
	String getPath() {return path;}
	
	/**
	 * check pathName
	 * @param s
	 * @return whether s is path
	 */
	public static boolean isItemPath(final String s) {
		return s.startsWith("/");
	}
	
	public static boolean isEndMarkItem(final Item item) {
		return item.path.startsWith("#");
	}
}
