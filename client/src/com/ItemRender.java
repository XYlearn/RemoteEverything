package com;

import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.ListCellRenderer;

public class ItemRender extends JPanel implements ListCellRenderer<Item> {

	private static ImageIcon default_icon;
	private static final int DEFAULT_ICON_WIDTH = 50;
	private static final int DEFAULT_ICON_HEIGHT = 50;
	private static final int DEFAULT_FILENAME_HEIGHT = 45;
	private static final int DEFAULT_PATHNAME_HEIGHT = 45;
	private static final int DEFAULT_TEXTLABEL_WIDTH = 600 - 70;
	private static final int DEFAULT_HEIGHT = 70;

	private JLabel fileNameLabel;
	private JLabel filePathLabel;

	static {
		default_icon = new ImageIcon("res/img/file_icon.png");
		default_icon.setImage(default_icon.getImage().getScaledInstance(DEFAULT_ICON_WIDTH, DEFAULT_ICON_HEIGHT,
				Image.SCALE_DEFAULT));
	}

	/**
	 * Constructor
	 */
	public ItemRender() {
		initComponents();

	}

	/**
	 * initComonents
	 */
	private void initComponents() {
		BoxLayout topLayout = new BoxLayout(this, BoxLayout.LINE_AXIS);
		this.setLayout(topLayout);

		JLabel iconLabel = new JLabel(default_icon);
		iconLabel.setSize(DEFAULT_HEIGHT, DEFAULT_HEIGHT);
		this.add(iconLabel);

		JTextPane textPane = new JTextPane();
		BoxLayout textLayout = new BoxLayout(textPane, BoxLayout.Y_AXIS);
		textPane.setLayout(textLayout);
		fileNameLabel = new JLabel("");
		fileNameLabel.setSize(DEFAULT_TEXTLABEL_WIDTH, DEFAULT_FILENAME_HEIGHT);
		filePathLabel = new JLabel("");
		filePathLabel.setSize(DEFAULT_TEXTLABEL_WIDTH, DEFAULT_PATHNAME_HEIGHT);

		// set fonts

		Font fileNameFont = new Font("ºÚÌå", Font.BOLD, 22);
		fileNameLabel.setFont(fileNameFont);
		Font filePathFont = new Font("Î¢ÈíÑÅºÚ", Font.ITALIC, 15);
		filePathLabel.setFont(filePathFont);

		textPane.add(fileNameLabel);
		textPane.add(filePathLabel);
		textPane.setSize(DEFAULT_TEXTLABEL_WIDTH, DEFAULT_HEIGHT);
		this.add(textPane);
	}

	@Override
	public Component getListCellRendererComponent(JList list, Item item, int index, boolean isSelected,
			boolean hasFocus) {
		String path = item.getPath();
		fileNameLabel.setText(new File(path).getName());
		filePathLabel.setText(path);
		return this;
	}

}
