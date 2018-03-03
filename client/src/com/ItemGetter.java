package com;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

public class ItemGetter implements Runnable {

	private static final String DEFAULT_HOST = "192.168.199.158";
	private static final int DEFAULT_PORT = 15536;

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
				byte[] cmdBytes = cmd.getBytes("utf-8");
				out.write(cmdBytes);
				out.writeByte('\n');
				out.flush();
				Item item = null;
				String path = null;
				while ((path = in.readLine()) != null) {
					if (Item.isItemPath(path)) {
						item = new Item(path);
						itemQueue.put(item);
					} else if (path.startsWith("#")) {
						item = new Item(path);
						itemQueue.put(item);
						break;
					} else {
						System.out.println(path);
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
