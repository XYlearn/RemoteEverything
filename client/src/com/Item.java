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
	public String getPath() { return path; }
	public String getText() { return path; } 
	
	/**
	 * check pathName
	 * @param s
	 * @return whether item is a PathItem
	 */
	public boolean isPathItem() {
		return this.path.startsWith("/");
	}
	
	/**
	 * check endmark
	 * @return whether item is a EndMarkItem
	 */
	public boolean isEndMarkItem() {
		return this.path.equals("#");
	}
	
	/**
	 * check statusItem
	 * @return whether item is a StatusItem
	 */
	public boolean isStatusItem() {
		return this.path.startsWith("#") && this.path.length() > 1;
	}
	
	public String getStatusText() {
		return this.path.substring(1);
	}
}
