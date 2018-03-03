package com;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.*;

import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;

public class EverythingGUI extends JFrame {
	private static final int DEFAULT_WIDTH = 600;
	private static final int DEFAULT_HEIGHT = 70;
	private static Font defaultFont;

	private static final int SWITCH_KEY_MARK = 100;
	private static final int HIDE_KEY_MARK = 101;

	private static final String DEFAULT_TITLE = "Remote Everything";

	static {
		defaultFont = new Font("微软雅黑", Font.BOLD, 25);
	}

	private JTextField textField;
	private ItemLister itemLister;
	private BoxLayout layout;
	private boolean switchedOn;

	protected JTextField getTextField() {
		return this.textField;
	}

	protected boolean isSwitchedOn() {
		return this.switchedOn;
	}

	protected void setSwitchedOn(boolean status) {
		this.switchedOn = status;
	}

	/**
	 * Constructor
	 */
	public EverythingGUI() {
		initComponents();
		try {
			registerHotKeys();
		} catch (Exception e) {
			
		}
		this.switchedOn = true;
		this.itemLister = null;

		// set frame
		this.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		this.setLocationRelativeTo(null);
		this.setBackground(Color.RED);
		this.setUndecorated(true);
		this.setVisible(this.switchedOn);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle(DEFAULT_TITLE);
	}

	/**
	 * init GUI Components
	 */
	protected void initComponents() {
		layout = new BoxLayout(this.getContentPane(), BoxLayout.LINE_AXIS);
		this.setLayout(layout);

		// set textField
		this.textField = new JTextField();
		textField.setToolTipText("输入要搜索的文件名");
		textField.setFont(defaultFont);
		
		// keylistener
		textField.addKeyListener(new KeyListener() {
			private int prevLength = 0;

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					String currentText = textField.getText();
					if (!currentText.isEmpty()) {
						if (!itemLister.isVisible())
							itemLister.doSwitch(true);
						itemLister.sendCmd(currentText);
						itemLister.asyncGetList();
						//itemLister.doList();
						e.getComponent().requestFocus();
					}
				}
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				
			}

			@Override
			public void keyTyped(KeyEvent e) {

			}

		});
		this.getContentPane().add(textField);
	}

	/**
	 * Add HotKeys to GUI
	 */
	protected void registerHotKeys() throws Exception {
		JIntellitype.getInstance().registerHotKey(SWITCH_KEY_MARK, JIntellitype.MOD_ALT | JIntellitype.MOD_CONTROL,
				'S');
		JIntellitype.getInstance().registerHotKey(HIDE_KEY_MARK, "escape");
		JIntellitype.getInstance().addHotKeyListener(new HotkeyListener() {
			@Override
			public void onHotKey(int markCode) {
				switch (markCode) {
				case SWITCH_KEY_MARK:
					// first switch off itemLister
					if (null != itemLister) {
						itemLister.doSwitch(false);
					}
					doSwitch();
					break;
				case HIDE_KEY_MARK:
					if (switchedOn)
						doSwitch();
					if (null != itemLister)
						if (!itemLister.isVisible())
							itemLister.doSwitch(false);
					break;
				}
			}
		});
	}

	/**
	 * Change Visibility of InputPanel and ItemLister panel
	 */
	protected void doSwitch() {
		switchedOn = !switchedOn;

		if (!switchedOn) {
			this.setVisible(false);
		} else {
			this.setVisible(true);
			this.textField.setText("");
		}
		System.out.println("[+] Hot Key Pressed");
	}

	/**
	 * set itemLister, the old itemLister will be disposed
	 * 
	 * @param itemLister
	 *            new itemLister to set
	 */
	public void setItemLister(ItemLister itemLister) {
		if (this.itemLister != null)
			this.itemLister.dispose();
		this.itemLister = itemLister;
	}

}
