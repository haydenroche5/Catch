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
	private JTextField textField;

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
		matchedUrls = new DefaultListModel<URL>();
		setDefaultCloseOperation(3);
		setBounds(25, 25, 450, 700);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		seedField = new JTextField();
		seedField.setBounds(6, 152, 438, 28);
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
		selectedMatch.setBounds(6, 131, 100, 16);
		contentPane.add(selectedMatch);
		
		JButton btnCatch = new JButton("Catch");
		btnCatch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
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
						for (int i = 0; i < compPhotos.size(); i++) {
							boolean theyMatch = seedImage.compareImages(compPhotos.get(i));
							if (theyMatch) {
								matchedUrls.addElement(((CatchImage)compPhotos.get(i)).getImageURL());
								System.out.println("Hey, a match!");
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
					int width = 0;
					int height = 0;
					int x = 56;
					int y = 0;
					int row = 0;
					for(int i = 0; i < matchedUrls.size(); i++) {
						URL matchUrl = matchedUrls.get(i);
						BufferedImage match = ImageIO.read(matchUrl);
						match = Thumbnailator.createThumbnail(match, 75, 75);
						JButton thumbButton = new JButton(new ImageIcon(match));
						x += width;
						y = 131 + row;
						if((i % 4) == 0 && i != 0) {
							row += 85;
							x = 56;
						}
						thumbButton.setBounds(x, y, 75, 75);
						width = match.getWidth();
						height = match.getHeight();
						contentPane.add(thumbButton);
						thumbButton.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								selectedMatch.setText(matchUrl.toString());
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
		btnCatch.setBounds(167, 640, 116, 29);
		contentPane.add(btnCatch);

		JLabel lblOr = new JLabel("OR");
		lblOr.setBounds(207, 97, 61, 16);
		contentPane.add(lblOr);
		
		textField = new JTextField();
		textField.setColumns(10);
		textField.setBounds(6, 36, 438, 28);
		contentPane.add(textField);
	}
}