package com;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class ItemGetter implements Runnable {

	private DataOutputStream out;
	private BufferedReader in;
	private Socket socket;
	private BlockingQueue<Item> itemQueue;
	private BlockingQueue<String> cmdQueue;

	/**
	 * Constructor
	 * 
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	ItemGetter(String host, int port) throws UnknownHostException, IOException {
		socket = new Socket(host, port);
		out = new DataOutputStream(socket.getOutputStream());
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
		this.itemQueue = new LinkedBlockingQueue<Item>(100);
		this.cmdQueue = new LinkedBlockingQueue<String>(10);
	}

	/**
	 * Destructor
	 * 
	 * @Override
	 */
	protected void finalize() throws Throwable {
		out.close();
		in.close();
		socket.close();
		super.finalize();
	}

	/**
	 * itemList getter
	 * @return
	 */
	public BlockingQueue<Item> getItemQueue() {
		return this.itemQueue;
	}
	
	/**
	 * cmdQueue getter
	 * @return
	 */
	public BlockingQueue<String> getCmdQueue() {
		return this.cmdQueue;
	}
	
	@Override
	public void run() {
		if (socket.isClosed()) {
			System.out.println("[-] Socket is Closed");
			System.exit(0);
		}
		
		while (true) {
			// get cmds
			String cmd = null;
			try {
				cmd = cmdQueue.take();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				continue;
			}
			
			// send a cmd and receive contents returned
			try {
				if(cmd.length() < 2) {
					System.out.println("[-]Too short!");
				}
				byte[] cmdBytes = cmd.getBytes("utf-8");
				out.write(cmdBytes);
				out.writeByte('\n');
				out.flush();
				Item item = null;
				String path = null;
				while ((path = in.readLine()) != null) {
					item = new Item(path);
					if (item.isPathItem()) {
						itemQueue.put(item);
					} else if (item.isEndMarkItem()) {
						itemQueue.put(item);
						break;
					} else if (item.isStatusItem()){
						itemQueue.put(item);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

}
