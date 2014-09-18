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
	private URL matchUrl;
	private BufferedImage match;

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
	
	//TODO: the GUI needs to update actionListeners so that when the page is changed, the URLs for each button
	//reflect the image icon they hold. Right now these aren't being updated properly. Also need to build in a
	//gallery feature that brings the image up to "full resolution"* when clicked to the right of the thumbnail 
	//gallery.
	//*=the method for creating flickr URLs compresses the image resolution.

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
		displayPanel.setBounds(495, 35, 600, 600);
		contentPane.add(displayPanel);

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

		JLabel selectedMatch = new JLabel("Selected Match");
		selectedMatch.setBounds(9, 131, 100, 16);
		contentPane.add(selectedMatch);

		JLabel lblOr = new JLabel("OR");
		lblOr.setBounds(207, 97, 61, 16);
		contentPane.add(lblOr);

		selectedMatchField = new JTextField();
		selectedMatchField.setColumns(10);
		selectedMatchField.setBounds(6, 149, 438, 28);
		contentPane.add(selectedMatchField);

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
					contentPane.remove(picButtons.get(i));
				}
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
							CatchImage photoToCompare = new CatchImage((String)compUrls.get(i));
							compPhotos.add(photoToCompare);
						}
						//REMOVE this variable after testing complete
						int count = 0;
						for (int i = 0; i < compPhotos.size(); i++) {
							boolean theyMatch = seedImage.compareImages(compPhotos.get(i));
							if (theyMatch) {
								count++;
								matchedUrls.addElement(((CatchImage)compPhotos.get(i)).getImageURL());
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
				btnPrevPage.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						btnNextPage.setVisible(true);
						int finalIndex = 0;
						startingIndex -= 25;
						pageNumber--;
						for(int i = 0; i < picButtons.size(); i++) {
							picButtons.get(i).setIcon(null);
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
				try {
					createThumbs(0, 25);
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
				contentPane.validate();
				contentPane.repaint();
			}
		});
		btnKatch.setBounds(167, 640, 116, 29);
		contentPane.add(btnKatch);
	}

	public void createThumbs(int startingIndex, int finalIndex) throws IOException {
		for(int i = 0; i < picButtons.size(); i++) {
			removeListeners(picButtons.get(i));
		}
		int width = 0;
		int height = 0;
		int x = 38;
		int y = 0;
		int row = 0;
		int btnIndex = 0;
		for(int i = startingIndex; i < finalIndex; i++) {
			matchUrl = matchedUrls.get(i);
			match = ImageIO.read(matchUrl);
			BufferedImage matchThumb = Thumbnailator.createThumbnail(match, 55, 55);
			picButtons.get(btnIndex).setIcon(new ImageIcon(matchThumb));
			x += width;
			if((i % 5) == 0 && (i % 25) != 0) {
				row += 85;
				x = 38;
			}
			y = 181 + row;
			picButtons.get(btnIndex).setBounds(x, y, 75, 75);
			width = 75;
			height = matchThumb.getHeight();
			contentPane.add(picButtons.get(btnIndex));
			btnIndex++;
		}
		for(int i = 0; i < 25; i++) {
			picButtons.get(0).addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					selectedMatchField.setText(matchUrl.toString());
					Graphics matchGraphics = match.getGraphics();
					matchGraphics.drawImage(match, match.getWidth(), match.getHeight(), displayPanel);  
					contentPane.validate();
					contentPane.repaint();
				}
			});
		}
	}

	public void removeListeners(JButton button) {
		ActionListener[] listeners = button.getActionListeners();
		for(ActionListener aListener : listeners) {
			button.removeActionListener(aListener);
		}
	}
}