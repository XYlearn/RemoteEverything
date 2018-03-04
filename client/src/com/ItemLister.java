package com;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class ItemLister extends JFrame {

	private JList<Item> lister;
	private EverythingGUI parent;
	private BoxLayout layout;
	private JLabel statusLabel;
	private JPanel statusPanel;
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
		itemListCache = new ArrayList<>();
		this.parent = parent;
		parent.setItemLister(this);
		this.itemGetter = itemGetter;
		this.itemQueue = itemGetter.getItemQueue();
		
		initComponents();
		initFrame();
	}
	
	/**
	 * connect to EverythingGUI
	 * @param parent parent EverythingGUI
	 */
	public void connectToPanel(EverythingGUI parent) {
		this.parent = parent;
		parent.setItemLister(this);
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

	/**
	 * init lister components
	 */
	private void initComponents() {
		// init layout
		this.layout = new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS);
		this.setLayout(layout);

		// add statusLabel
		this.statusLabel = new JLabel("Waiting");
		statusLabel.setFont(new Font("»ªÎÄ²ÊÔÆ", Font.PLAIN, 16));
		// add statusPane
		this.statusPanel = new JPanel();
		this.getContentPane().add(statusPanel);
		statusPanel.add(statusLabel);
		statusPanel.setMaximumSize(new Dimension(
				EverythingConfig.LISTER_DEFAULT_WIDTH, 
				EverythingConfig.LISTER_STATUS_PANEL_HEIGHT)
				);
		statusPanel.setBackground(Color.white);
		
		// add scrollPane
		this.scrollPane = new JScrollPane();
		this.getContentPane().add(scrollPane);

		// add JLister
		lister = new JList<>();
		lister.setCellRenderer(new ItemRender());
		scrollPane.setViewportView(lister);
		
	}
	
	private void initFrame() {
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
	 * switch to status given by param
	 * @param on dest on status
	 */
	public void doSwitch(boolean on) {
		this.setVisible(on);
	}
	
	public void showStatus(String statusText) {
		this.statusLabel.setText(statusText);
	}
	
	/**
	 * use another thread to set list to avoid blocking
	 */
	public void asyncGetList() {
		Thread thread = new Thread(new ListerRunner(this));
		thread.start();
	}

	/**
	 * Get Item List from getter
	 */
	synchronized
	public void getList() {
		statusLabel.setText("Searching");
		// add Items to listCache
		while (true) {
			try {
				Item item = itemQueue.take();
				if (item.isEndMarkItem())
					break;				
				else if(item.isPathItem()) {
					itemListCache.add(item);
					System.out.println(item.getPath());
				}
				else if(item.isStatusItem()){
					showStatus(item.getText());
					return;
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		statusLabel.setText("Searching Done");
	}
	
	/**
	 * show items in itemListCache
	 */
	synchronized
	public void showList() {
		DefaultListModel<Item> listModel = new DefaultListModel<>();
		for(Item item : itemListCache) {
			listModel.addElement(item);
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
	class ListerRunner implements Runnable {
		private ItemLister lister;
		
		ListerRunner(ItemLister lister) {
			this.lister = lister;
		}
		
		@Override
		public void run() {
			synchronized(lister) {
				lister.clearList();
				lister.getList();
				lister.showList();
			}
		}
		
	}
}
