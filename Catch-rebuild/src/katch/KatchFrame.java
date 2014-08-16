package katch;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class KatchFrame extends JFrame {
	private JPanel contentPane;
	private JTextField seedField;
	private JTextField userField;
	private JTextField searchField;
	private DefaultListModel<URL> matchedUrls;

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
		this.matchedUrls = new DefaultListModel<URL>();
		setDefaultCloseOperation(3);
		setBounds(100, 100, 610, 310);
		this.contentPane = new JPanel();
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(this.contentPane);
		this.contentPane.setLayout(null);

		this.seedField = new JTextField();
		this.seedField.setBounds(6, 37, 438, 28);
		this.contentPane.add(this.seedField);
		this.seedField.setColumns(10);

		this.userField = new JTextField();
		this.userField.setColumns(10);
		this.userField.setBounds(6, 91, 134, 28);
		this.contentPane.add(this.userField);

		this.searchField = new JTextField();
		this.searchField.setBounds(6, 145, 134, 28);
		this.contentPane.add(this.searchField);
		this.searchField.setColumns(10);

		JLabel lblNewLabel_1 = new JLabel("Seed URL");
		lblNewLabel_1.setBounds(10, 19, 61, 16);
		this.contentPane.add(lblNewLabel_1);

		JLabel lblNewLabel = new JLabel("Username");
		lblNewLabel.setBounds(10, 73, 72, 16);
		this.contentPane.add(lblNewLabel);

		JLabel lblSearch = new JLabel("Search");
		lblSearch.setBounds(10, 127, 61, 16);
		this.contentPane.add(lblSearch);

		final JScrollPane matchPane = new JScrollPane();
		matchPane.setBounds(152, 91, 442, 181);
		this.contentPane.add(matchPane);

		JLabel lblMatches = new JLabel("Matches");
		lblMatches.setBounds(154, 73, 61, 16);
		this.contentPane.add(lblMatches);

		JList matchList = new JList();
		matchPane.setViewportView(matchList);

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
							boolean theyMatch = seedImage.compareImages((CatchImage)compPhotos.get(i));
							if (theyMatch) {
								matchedUrls.addElement(((CatchImage)compPhotos.get(i)).getImageURL());
								matchList.setModel(matchedUrls);
								matchPane.validate();
								matchPane.repaint();
								System.out.println("Hey, a match!");
							}
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		btnCatch.setBounds(6, 196, 117, 29);
		this.contentPane.add(btnCatch);
	}
}