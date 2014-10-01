package katch;

import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import net.coobird.thumbnailator.Thumbnailator;

public class KatchFrame extends JFrame {
	private JLabel matchUrlLabel;
	private JPanel contentPane;
	private JTextField seedField;
	private JTextField userField;
	private JTextField searchField;
	private JPanel displayPanel;
	private DefaultListModel<URL> matchedUrls;
	private JTextField selectedMatchField;
	private ArrayList<JButton> picButtons;
	private int pageNumber;
	private int startingIndex;
	private int btnIndex;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					KatchFrame frame = new KatchFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public KatchFrame() {
		picButtons = new ArrayList<JButton>();
		for(int i = 0; i < 25; i++) {
			JButton thumbButton = new JButton();
			picButtons.add(thumbButton);
		}
		startingIndex = 0;
		pageNumber = 1;
		matchedUrls = new DefaultListModel<URL>();
		setDefaultCloseOperation(3);
		setBounds(25, 25, 1100, 700);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		displayPanel = new JPanel();
		contentPane.add(displayPanel);

		selectedMatchField = new JTextField();
		selectedMatchField.setColumns(10);
		contentPane.add(selectedMatchField);
		selectedMatchField.setVisible(false);
		selectedMatchField.setEditable(false);

		seedField = new JTextField();
		seedField.setBounds(6, 35, 438, 28);
		contentPane.add(seedField);
		seedField.setColumns(10);

		userField = new JTextField();
		userField.setColumns(10);
		userField.setBounds(6, 91, 134, 28);
		contentPane.add(userField);

		searchField = new JTextField();
		searchField.setBounds(310, 91, 134, 28);
		contentPane.add(searchField);
		searchField.setColumns(10);

		JLabel lblNewLabel_1 = new JLabel("Seed URL");
		lblNewLabel_1.setBounds(10, 19, 61, 16);
		contentPane.add(lblNewLabel_1);

		JLabel lblNewLabel = new JLabel("Username");
		lblNewLabel.setBounds(10, 73, 72, 16);
		contentPane.add(lblNewLabel);

		JLabel lblSearch = new JLabel("Search");
		lblSearch.setBounds(313, 73, 61, 16);
		contentPane.add(lblSearch);

		matchUrlLabel = new JLabel("URL");
		contentPane.add(matchUrlLabel);
		matchUrlLabel.setVisible(false);

		JLabel lblOr = new JLabel("OR");
		lblOr.setBounds(207, 97, 61, 16);
		contentPane.add(lblOr);

		JButton btnPrevPage = new JButton("Previous page");
		btnPrevPage.setBounds(86, 607, 117, 29);
		contentPane.add(btnPrevPage);
		btnPrevPage.setVisible(false);

		JButton btnNextPage = new JButton("Next page");
		btnNextPage.setBounds(246, 607, 117, 29);
		contentPane.add(btnNextPage);
		btnNextPage.setVisible(false);

		JButton btnKatch = new JButton("Katch");
		btnKatch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for(int i = 0; i < picButtons.size(); i++) {
					ActionListener[] listeners = picButtons.get(i).getActionListeners();
					if(listeners.length != 0) {
						picButtons.get(i).removeActionListener(listeners[0]);
					}
					contentPane.remove(picButtons.get(i));
				}
				displayPanel.removeAll();
				selectedMatchField.setVisible(false);
				matchedUrls.clear();
				String search = userField.getText();
				boolean isUser = true;
				if ((userField.getText().length() > 0) && (searchField.getText().length() > 0)) {
					System.out.println("INVALID: Cannot specify both a user and "
							+ "search term to match with");
				}
				else if ((userField.getText().length() == 0) && (searchField.getText().length() == 0)) {
					System.out.println("INVALID: No user or search term entered.");
				}
				else {
					if (searchField.getText().length() > 0) {
						search = searchField.getText();
						isUser = false;
					}
					try {
						CatchImage seedImage = new CatchImage(seedField.getText());
						ImageCollector collector = new ImageCollector();
						ArrayList<String> compUrls = collector.collectImages(search, isUser);

						ArrayList<CatchImage> compPhotos = new ArrayList<CatchImage>();
						for (int i = 0; i < compUrls.size(); i++) {
							CatchImage photoToCompare = new CatchImage(compUrls.get(i));
							compPhotos.add(photoToCompare);
						}
						//REMOVE this variable after testing complete
						int count = 0;
						for (int i = 0; i < compPhotos.size(); i++) {
							boolean theyMatch = seedImage.compareImages(compPhotos.get(i));
							if (theyMatch) {
								count++;
								matchedUrls.addElement(compPhotos.get(i).getImageURL());
								System.out.println("Hey, a match! (" + count + ")");
							}
							else {
								System.out.println("Nope.");
							}
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				try {
					if(matchedUrls.size() < 25) {
						createThumbs(0, matchedUrls.size());
					}
					else {
						createThumbs(0, 25);
					}		
				} catch (IOException e2) {
					e2.printStackTrace();
				}
				if(matchedUrls.size() > 25) {
					btnNextPage.setVisible(true);
					btnNextPage.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							int finalIndex = 0;
							startingIndex += 25;
							pageNumber++;
							btnPrevPage.setVisible(true);
							for(int i = 0; i < picButtons.size(); i++) {
								picButtons.get(i).setIcon(null);
								ActionListener[] listeners = picButtons.get(i).getActionListeners();
								if(listeners.length != 0) {
									picButtons.get(i).removeActionListener(listeners[0]);
								}
							}
							if((pageNumber * 25) > matchedUrls.size()) {
								finalIndex = matchedUrls.size();
								btnNextPage.setVisible(false);
							}
							else {
								finalIndex = pageNumber * 25;
							}
							try {
								createThumbs(startingIndex, finalIndex);
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						}
					});
				}
				btnPrevPage.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						btnNextPage.setVisible(true);
						int finalIndex = 0;
						startingIndex -= 25;
						pageNumber--;
						for(int i = 0; i < picButtons.size(); i++) {
							picButtons.get(i).setIcon(null);
							ActionListener[] listeners = picButtons.get(i).getActionListeners();
							if(listeners.length != 0) {
								picButtons.get(i).removeActionListener(listeners[0]);
							}
						}
						finalIndex = pageNumber * 25;
						if(pageNumber == 1) {
							btnPrevPage.setVisible(false);
						}
						try {
							createThumbs(startingIndex, finalIndex);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				});
				contentPane.validate();
				contentPane.repaint();
			}
		});
		btnKatch.setBounds(167, 640, 116, 29);
		contentPane.add(btnKatch);
	}

	public void createThumbs(int startingIndex, int finalIndex) throws IOException {
		int width = 0;
		int height = 0;
		int x = 38;
		int y = 0;
		int row = 0;
		btnIndex = 0;
		for(int i = startingIndex; i < finalIndex; i++) {
			URL matchUrl = matchedUrls.get(i);
			BufferedImage match = ImageIO.read(matchUrl);
			BufferedImage matchThumb = Thumbnailator.createThumbnail(match, 55, 55);
			picButtons.get(btnIndex).setIcon(new ImageIcon(matchThumb));
			x += width;
			if((i % 5) == 0 && (i % 25) != 0) {
				row += 85;
				x = 38;
			}
			y = 161 + row;
			picButtons.get(btnIndex).setBounds(x, y, 75, 75);
			width = 75;
			height = matchThumb.getHeight();
			contentPane.add(picButtons.get(btnIndex));
			picButtons.get(btnIndex).addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					displayPanel.removeAll();
					selectedMatchField.setText(matchUrl.toString());
					ImageIcon bigImageIcon = new ImageIcon(match);
					JLabel bigImageLabel = new JLabel(bigImageIcon);
					displayPanel.setBounds(495, 35, bigImageIcon.getIconWidth(), bigImageIcon.getIconHeight());
					displayPanel.add(bigImageLabel);
					selectedMatchField.setBounds(490, 35+bigImageIcon.getIconHeight()+33, 458, 28);
					selectedMatchField.setVisible(true);
					matchUrlLabel.setBounds(492, 35+bigImageIcon.getIconHeight()+10, 100, 16);
					matchUrlLabel.setVisible(true);
					contentPane.validate();
					contentPane.repaint();
				}
			});
			btnIndex++;
		}

	}
}