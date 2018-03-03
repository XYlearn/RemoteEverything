package com;

import java.awt.Point;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListModel;

public class ItemLister extends JFrame {

	private JList<Item> lister;
	private EverythingGUI parent;
	private BoxLayout layout;
	private JScrollPane scrollPane;

	private ItemGetter itemGetter;
	private BlockingQueue<Item> itemQueue;
	private ArrayList<Item> itemListCache;

	/**
	 * ItemLister Constructor
	 * 
	 * @param parent
	 *            parent Frame
	 * @param itemGetter
	 *            itemGetter to use
	 */
	ItemLister(EverythingGUI parent, ItemGetter itemGetter) {
		initComponents();
		
		itemListCache = new ArrayList<>();
		this.parent = parent;
		parent.setItemLister(this);
		this.itemGetter = itemGetter;
		this.itemQueue = itemGetter.getItemQueue();

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// set location
		Point loc = parent.getLocationOnScreen();
		double locX = loc.getX();
		double locY = loc.getY() + parent.getHeight();
		this.setLocation((int) locX, (int) locY);
		
		// set Size
		int height = Toolkit.getDefaultToolkit().getScreenSize().height - 
				(int)locY - parent.getHeight();
		this.setSize(parent.getWidth(), height);
		
		// set undecorate
		this.setUndecorated(true);

		// is hidden in the beginning
		this.setVisible(false);
	}

	/**
	 * send command
	 */
	public void sendCmd(String cmd) {
		BlockingQueue<String> cmdQueue = itemGetter.getCmdQueue();
		if (null == cmdQueue)
			return;
		try {
			cmdQueue.put(cmd);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void initComponents() {
		// init layout
		this.layout = new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS);
		
		// add scrollPane
		this.scrollPane = new JScrollPane();
		this.getContentPane().add(scrollPane);

		// add JLister
		lister = new JList<>();
		lister.setCellRenderer(new ItemRender());
		scrollPane.setViewportView(lister);
		
	}

	public void doSwitch(boolean on) {
		this.setVisible(on);
	}
	
	public void asyncGetList() {
		Thread thread = new Thread(new GetListRunner(this));
		thread.start();
	}

	synchronized
	public void getList() {
		DefaultListModel<Item> listModel = new DefaultListModel<>();
		// add Items to listModel
		while (true) {
			try {
				Item item = itemQueue.take();
				if (Item.isEndMarkItem(item))
					break;
				else {
					itemListCache.add(item);
					listModel.addElement(item);
					System.out.println(item.getPath());
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		lister.setModel(listModel);
	}

	public void clearList() {
		itemListCache.clear();
	}

	/**
	 * Inner class
	 * run get List async
	 *
	 */
	class GetListRunner implements Runnable {
		private ItemLister lister;
		
		GetListRunner(ItemLister lister) {
			this.lister = lister;
		}
		
		@Override
		public void run() {
			synchronized(lister) {
				lister.getList();
			}
		}
		
	}
}
