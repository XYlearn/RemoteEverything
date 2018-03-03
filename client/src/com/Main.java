package com;

public class Main {
	public static void main(String[] args) {
		if(args.length < 2) {
			System.out.println("[-] Need 2 arguments");
			System.exit(0);
		}
		int port = Integer.parseInt(args[1]);
		String host = args[0];
		
		ItemGetter itemGetter = null;
		if((itemGetter = startItemGetter(host, port)) == null) {
			System.out.println("[-] Fail to connect to server");
			System.exit(0);
		}
		EverythingGUI app = new EverythingGUI();
		ItemLister itemLister = new ItemLister(app, itemGetter);
	}
	
	private static ItemGetter startItemGetter(String host, int port) {
		ItemGetter itemGetter = null;
		try {
			itemGetter = new ItemGetter(host, port);
			Thread itemGetterThread = new Thread(itemGetter);
			itemGetterThread.start();
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		return itemGetter;
	}
}
