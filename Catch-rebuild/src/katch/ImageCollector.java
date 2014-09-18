package katch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.ParserConfigurationException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

//the image collector is responsible for pulling batches of images from flickr
public class ImageCollector {
	
	private String apiKey;
	
	public ImageCollector() {
		this.apiKey = "3308c011a950366105f3dfd4a0d4b324";
	}
	
	//collectImages is a method use by the imageCollector to get the first 100 images that come up for a given user
	//or search term
	public ArrayList<String> collectImages(String search, boolean isUser) throws IOException {
		//we have to replace all whitespace in the username with %20 to compose a valid request to the flickr API
		Pattern space = Pattern.compile("\\s");
		Matcher spaceMatcher = space.matcher(search);
		while (spaceMatcher.find()) {
			search = spaceMatcher.replaceAll("%20");
		}
		Document photoPage;
		//protocol for getting images from a user's page
		if (isUser) {
			Document idPage = Jsoup.connect("https://api.flickr.com/services/rest/?&method=flickr.people.findByUsername&api_key=" + 
					this.apiKey + "&username=" + search).get();
			Elements pageElements = idPage.getElementsByAttribute("id");
			Element userElement = pageElements.first();
			String nsid = userElement.id();
			photoPage = Jsoup.connect("https://api.flickr.com/services/rest/?&method=flickr.people.getPublicPhotos&api_key=" + this.apiKey + "&user_id=" + 
					nsid).get();
		}
		//protocol for getting images from a search
		else {
			photoPage = Jsoup.connect("https://api.flickr.com/services/rest/?&method=flickr.photos.search&api_key=" + 
					this.apiKey + "&text=" + search).get();
		}
		
		//all HTML elements on the photoPage that have the attribute "owner" are photos
		Elements photoPageElements = photoPage.getElementsByAttribute("owner");
		int photoCount = photoPageElements.size();
		ArrayList<String> photos = new ArrayList<String>();
		//we need to iterate through all the photos on the page and construct URLs per the instructions provided by flickr
		//and then add these URls to our photos collection
		for (int i = 0; i < photoCount; i++) {
			Element photo = photoPageElements.get(i);
			Attributes photoAttributes = photo.attributes();
			String farm = photoAttributes.get("farm");
			String serverId = photoAttributes.get("server");
			String photoId = photo.id();
			String secret = photoAttributes.get("secret");
			String urlText = "https://farm" + farm + ".staticflickr.com/" + serverId + 
					"/" + photoId + "_" + secret + ".jpg";
			photos.add(urlText);
		}
		return photos;
	}

	public static void main(String[] args) throws ParserConfigurationException, IOException {
		ImageCollector collector = new ImageCollector();
		collector.collectImages("David Olkarny Photography", true);
	}
}