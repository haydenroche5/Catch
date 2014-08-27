package katch;

import java.awt.EventQueue;
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
	private DefaultListModel<URL> matchedUrls;
	private JTextField selectedMatchField;
	private int pageStart;
	private int pageLimit;
	private ArrayList<JButton> picButtons;

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
		matchedUrls = new DefaultListModel<URL>();
		setDefaultCloseOperation(3);
		setBounds(25, 25, 450, 700);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

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
				try {
					ArrayList<JButton> picButtons = new ArrayList<JButton>();
					int width = 0;
					int height = 0;
					int x = 35;
					int y = 0;
					int row = 0;
					pageLimit = matchedUrls.size();
					if(matchedUrls.size() > 25) {
						pageLimit = 25;
					}
					pageStart = 0;
					for(int i = 0; i < pageLimit; i++) {
						URL matchUrl = matchedUrls.get(i);
						BufferedImage match = ImageIO.read(matchUrl);
						match = Thumbnailator.createThumbnail(match, 55, 55);
						JButton thumbButton = new JButton(new ImageIcon(match));
						picButtons.add(thumbButton);
						x += width;
						y = 181 + row;
						if((i % 5) == 0 && i != 0) {
							row += 85;
							x = 35;
						}
						thumbButton.setBounds(x, y, 75, 75);
						width = 75;
						height = match.getHeight();
						contentPane.add(thumbButton);
						pageStart = i;
						thumbButton.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								selectedMatchField.setText(matchUrl.toString());
							}
						});
					}
					if(matchedUrls.size() > 25) {
						JButton btnNextPage = new JButton("Next page");
						btnNextPage.setBounds(166, 607, 117, 29);
						contentPane.add(btnNextPage);
						pageLimit += 25;
						pageStart += 1;
						btnNextPage.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								int j = 0;
								for(int i = pageStart; i < pageLimit; i++) {
									URL matchUrl = matchedUrls.get(i);
									try {
										BufferedImage match = ImageIO.read(matchUrl);
										match = Thumbnailator.createThumbnail(match, 55, 55);
										picButtons.get(j).setIcon(new ImageIcon(match));
									} catch (IOException e1) {
										e1.printStackTrace();
									}
									j++;
								}
							}
						});
					}
					contentPane.validate();
					contentPane.repaint();
				}
				catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		btnKatch.setBounds(167, 640, 116, 29);
		contentPane.add(btnKatch);
	}
}