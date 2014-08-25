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

public class ImageCollector {
	private String apiKey;

	public ImageCollector() {
		this.apiKey = "3308c011a950366105f3dfd4a0d4b324";
	}

	public ArrayList<String> collectImages(String search, boolean isUser) throws IOException {
		Pattern space = Pattern.compile("\\s");
		Matcher spaceMatcher = space.matcher(search);
		while (spaceMatcher.find()) {
			search = spaceMatcher.replaceAll("%20");
		}
		Document photoPage;
		if (isUser) {
			Document idPage = Jsoup.connect("https://api.flickr.com/services/rest/?&method=flickr.people.findByUsername&api_key=" + 
					this.apiKey + "&username=" + search).get();
			Elements pageElements = idPage.getElementsByAttribute("id");
			Element userElement = pageElements.first();
			String nsid = userElement.id();
			photoPage = Jsoup.connect("https://api.flickr.com/services/rest/?&method=flickr.people.getPublicPhotos&api_key=" + this.apiKey + "&user_id=" + 
					nsid).get();
		}
		else {
			photoPage = Jsoup.connect("https://api.flickr.com/services/rest/?&method=flickr.photos.search&api_key=" + 
					this.apiKey + "&text=" + search).get();
		}

		Elements photoPageElements = photoPage.getElementsByAttribute("owner");
		int photoCount = photoPageElements.size();
		ArrayList<String> photos = new ArrayList<String>();
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