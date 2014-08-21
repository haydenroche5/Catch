package katch;

import java.awt.Color;
import java.awt.List;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.imageio.ImageIO;

public class CatchImage {
	private URL imageUrl;
	private int width;
	private int height;
	private int skip;
	
	public CatchImage(String url) throws MalformedURLException {
		this.imageUrl = new URL(url);
	}

	private double[] processImage() throws IOException {
		BufferedImage seedImage = ImageIO.read(imageUrl);
		height = seedImage.getHeight();
		width = seedImage.getWidth();

		double totalRed = 0.0;
		double totalGreen = 0.0;
		double totalBlue = 0.0;
		double totalPix = width*height;

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				Color pixelColor = new Color(seedImage.getRGB(x, y));
				totalRed += pixelColor.getRed();
				totalGreen += pixelColor.getGreen();
				totalBlue += pixelColor.getBlue();
			}
		}

		double averageRed = totalRed / totalPix;
		double averageGreen = totalGreen / totalPix;
		double averageBlue = totalBlue / totalPix;
		double[] rgbArray = { averageRed, averageGreen, averageBlue };
		return rgbArray;
	}

	private int[] calculateColorModes() throws IOException {
		BufferedImage seedImage = ImageIO.read(imageUrl);
		height = seedImage.getHeight();
		width = seedImage.getWidth();
		int totalPix = width*height;

		//these bounds come from the Wikiedpia page "Web colors"
		ArrayList<int[]> colorBounds = new ArrayList<int[]>();
		int[] pinks = { 255, 192, 203, 255, 182, 193, 255, 105, 180, 255, 20, 147, 219, 112, 147, 199, 21, 133 };
		colorBounds.add(pinks);
		
		int[] reds = { 255, 160, 122, 250, 128, 114, 133, 150, 122, 140, 128, 128, 205, 92, 92, 220, 20, 60, 
				178, 34, 34, 139, 0, 0, 255, 0, 0};
		colorBounds.add(reds);

		int[] oranges = {255, 69, 0, 255, 99, 71, 255, 127, 80, 255, 140, 0, 255, 165, 0};
		colorBounds.add(oranges);

		int[] yellows = { 255, 255, 0, 255, 255, 224, 255, 250, 205, 250, 250, 210, 255, 239, 213, 
				255, 228, 181, 255, 218, 185, 238, 232, 170, 240, 230, 140, 189, 183, 107, 255, 215, 0 };
		colorBounds.add(yellows);

		int[] browns = { 255, 248, 220, 255, 235, 205, 255, 228, 196, 255, 222, 173, 245, 222, 179,
				222, 184, 135, 210, 180, 140, 188, 143, 143, 244, 164, 96, 218, 165, 32, 184, 134, 11, 205, 133, 63,
				210, 105, 30, 139, 69, 19, 160, 82, 45, 165, 42, 42, 128, 0, 0};

		int[] greens = { 85, 107, 47, 128, 128, 0, 107, 142, 35, 154, 205, 50, 50, 205, 50, 0, 255, 0, 124, 252, 0,
				127, 255, 0, 173, 255, 47, 0, 255, 127, 0, 250, 154, 144, 238, 144, 152, 251, 152, 143, 188, 143, 60, 179,
				113, 46, 139, 87, 34, 139, 34, 0, 128, 0, 0, 100, 0};
		colorBounds.add(greens);
		
		int[] cyans = { 102, 205, 170, 0, 255, 255, 0, 255, 255, 224, 255, 255, 175, 238, 238, 127, 255, 212, 64, 224,
				208, 72, 209, 204, 0, 206, 209, 32, 178, 170, 95, 158, 160, 0, 139, 139, 0, 128, 128};
		colorBounds.add(cyans);

		int[] blues = { 176, 196, 222, 176, 224, 230, 173, 216, 230, 135, 206, 235, 135, 206, 235, 135, 206, 250, 0,
				191, 255, 30, 144, 255, 100, 149, 237, 70, 130, 180, 65, 105, 225, 0, 0, 255, 0, 0, 205, 0, 0, 139, 0, 0,
				128, 25, 25, 112, 47, 79, 79};
		colorBounds.add(blues);

		int[] purples = { 230, 230, 250, 216, 191, 216, 221, 160, 221, 238, 130, 238, 218, 112, 214, 255, 0, 255, 186, 85,
				211, 147, 112, 219, 138, 43, 226, 148, 0, 211, 153, 50, 204, 139, 0, 139, 128, 0, 128, 75, 0, 130, 75, 61,
				139, 106, 90, 205};
		colorBounds.add(purples);

//		int[] whites = { 255, 255, 255, 255, 250, 250, 240, 255, 240, 245, 255, 250, 240, 255, 255, 240, 248, 255, 248, 248,
//				255, 245, 245, 245, 255, 245, 238, 245, 245, 220, 253, 245, 230, 255, 250, 240, 255, 255, 240, 250, 235, 215,
//				250, 240, 230, 255, 240, 245, 255, 228, 225};
//		colorBounds.add(whites);

//		int[] blacksAndGrays = { 0, 0, 0, 220, 220, 220, 211, 211, 211, 192, 192, 192, 169, 169, 169, 128, 128, 128, 105, 105,
//				105, 119, 136, 153, 112, 128, 144};
//		colorBounds.add(blacksAndGrays);

		//colorBounds's size is 10 and doesn't include brown
		//counts's size is 11 because it includes brown at the end
		//0 = pink, 1 = red, 2 = orange, 3 = yellow, 4 = green, 5 = cyan, 6 = blue, 7 = purple, 8 = white, 9 = black/gray,
		//10 = brown
		int[] counts = new int[colorBounds.size()];
		skip = 2;
		if(totalPix > 640000) {
			skip = 3;
		}
		for (int y = 0; y < height; y = y + skip) {
			for (int x = 0; x < width; x = x + skip) {
				Color color = new Color(seedImage.getRGB(x, y));
				int red = color.getRed();
				int green = color.getGreen();
				int blue = color.getBlue();
				for(int i = 0; i < colorBounds.size(); i++) {
					//TODO: Put method to find median from R, G, and B. If the other two values are 5-10 pts
					//away from the median each, the color is gray. Use Math library to calculate median from
					//the array.
					//if the color falls into one of the bounds defined above, excluding brown,
					//increment that bounds's count in the counts array
					for(int j = 0; j < colorBounds.get(i).length; j = j + 3) {
						if((red >= colorBounds.get(i)[0 + j] - 10) && (red <= colorBounds.get(i)[0 + j] + 10) 
								&& (green >= colorBounds.get(i)[1 + j] - 10) && (green <= colorBounds.get(i)[1 + j] + 10)
								&& (blue >= colorBounds.get(i)[2 + j] - 10) && (blue <= colorBounds.get(i)[2 + j] + 10)) {
							counts[i]++;
						}
					}
				}
			}
		}
		return counts;
	}

	public double[] calculateStandDev(double[] seedAverages) throws IOException {
		BufferedImage seedImage = ImageIO.read(this.imageUrl);
		height = seedImage.getHeight();
		width = seedImage.getWidth();

		int redVariance = 0;
		int greenVariance = 0;
		int blueVariance = 0;
		int totalPix = width * height;
		double divisor = totalPix - 1;

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				Color pixelColor = new Color(seedImage.getRGB(x, y));
				redVariance += Math.pow(pixelColor.getRed() - seedAverages[0], 2.0);
				greenVariance += Math.pow(pixelColor.getGreen() - seedAverages[1], 2.0);
				blueVariance += Math.pow(pixelColor.getBlue() - seedAverages[2], 2.0);
			}
		}

		double standDevRed = Math.sqrt(redVariance / divisor);
		double standDevGreen = Math.sqrt(greenVariance / divisor);
		double standDevBlue = Math.sqrt(blueVariance / divisor);
		double[] standDevs = { standDevRed, standDevGreen, standDevBlue };
		return standDevs;
	}

	public int findMax(int[] modes) throws IOException {
		int max = 0;
		int indexOfMax = 0;
		int totalPix = width*height;
		for(int i = 0; i < modes.length; i++) {
			if(modes[i] > max) {
				max = modes[i];
				indexOfMax = i;
			}
		}
		if(indexOfMax == 9) {
			if(((double) modes[9] / (((double) totalPix) / (double) skip)) > .75) {
				indexOfMax = findSecondLargest(modes);
			}
		}
		return indexOfMax;
	}

	public int findSecondLargest(int[] modes) throws IOException {
		int maxIndex = findMax(modes);
		modes[maxIndex] = 0;
		int secondLargestIndex = findMax(modes);
		return secondLargestIndex;
	}

	public boolean compareImages(CatchImage otherPhoto) throws IOException {
		boolean isMatch = false;
		if (this.findMax(this.calculateColorModes()) == otherPhoto.findMax(otherPhoto.calculateColorModes())) {
			isMatch = true;
			System.out.println("MAX: " + otherPhoto.findMax(otherPhoto.calculateColorModes()));
		}
		return isMatch;
	}

	public URL getImageURL() {
		return this.imageUrl;
	}

	public static void main(String[] args) throws IOException {
		CatchImage test = new CatchImage("https://farm6.staticflickr.com/5585/14984978262_35cac9c358.jpg");
		int[] modes = test.calculateColorModes();
		for(int i = 0; i < modes.length; i++) {
			System.out.println(modes[i]);
		}
		System.out.println("MAX: " + test.findMax(test.calculateColorModes()));
	}
}