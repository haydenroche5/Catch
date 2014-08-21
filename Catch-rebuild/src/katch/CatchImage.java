package katch;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

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

		int[] redOrangeHues = { 11, 20 };
		colorBounds.add(redOrangeHues);
		int[] orangeBrownHues = { 21, 40 };
		colorBounds.add(orangeBrownHues);
		int[] orangeYellowHues = { 41, 50 };
		colorBounds.add(orangeYellowHues);
		int[] yellowHues = { 51, 60 };
		colorBounds.add(yellowHues);
		int[] yellowGreenHues = { 61, 80 };
		colorBounds.add(yellowGreenHues);
		int[] greenHues = { 81, 140 };
		colorBounds.add(greenHues);
		int[] greenCyanHues = { 141, 169 };
		colorBounds.add(greenCyanHues);
		int[] cyanHues = { 170, 200 };
		colorBounds.add(cyanHues);
		int[] cyanBlueHues = { 201, 220 };
		colorBounds.add(cyanBlueHues);
		int[] blueHues = { 221, 240 };
		colorBounds.add(blueHues);
		int[] blueMagentaHues = { 241, 280 };
		colorBounds.add(blueMagentaHues);
		int[] magentaHues = { 281, 320 };
		colorBounds.add(magentaHues);
		int[] magentaPinkHues = { 321, 330 };
		colorBounds.add(magentaPinkHues);
		int[] pinkHues = { 331, 345 };
		colorBounds.add(pinkHues);
		int[] pinkRedHues = { 346, 355 };
		colorBounds.add(pinkRedHues);
		int[] redHues = { 355, 10 };
		colorBounds.add(redHues);

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
				float[] hsb = new float[3];
				Color.RGBtoHSB(red, green, blue, hsb);
				hsb[0] = hsb[0] * 360;
				hsb[1] = hsb[1] * 360;
				hsb[2] = hsb[2] * 360;
				for(int i = 0; i < colorBounds.size() - 1; i++) {
					if(colorBounds.get(i)[0] <= hsb[0] && hsb[0] <= colorBounds.get(i)[1]) {
						counts[i]++;
					}
				}
				if((redHues[0] <= hsb[0]) || (hsb[0] <= redHues[1])) {
					counts[colorBounds.size() - 1]++;
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
	for(int i = 0; i < modes.length; i++) {
		if(modes[i] > max) {
			max = modes[i];
			indexOfMax = i;
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