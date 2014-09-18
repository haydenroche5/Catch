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
	
	//deprecated
	//this is an old means of processing images that computed raw averages of the R, G, and B values of the image
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

	//this is the currently used method for segregating image into particular color schemes
	private int[] calculateColorModes() throws IOException {
		BufferedImage seedImage = ImageIO.read(imageUrl);
		height = seedImage.getHeight();
		width = seedImage.getWidth();
		int totalPix = width*height;

		//these hue bounds come from WorkWithColor.com
		ArrayList<int[]> colorBounds = new ArrayList<int[]>();

		int[] redOrangeHues = { 11, 20 }; //0
		colorBounds.add(redOrangeHues);
		int[] orangeBrownHues = { 21, 40 }; //1
		colorBounds.add(orangeBrownHues);
		int[] orangeYellowHues = { 41, 50 }; //2
		colorBounds.add(orangeYellowHues);
		int[] yellowHues = { 51, 60 }; //3
		colorBounds.add(yellowHues);
		int[] yellowGreenHues = { 61, 80 }; //4
		colorBounds.add(yellowGreenHues);
		int[] greenHues = { 81, 140 }; //5
		colorBounds.add(greenHues);
		int[] greenCyanHues = { 141, 169 }; //6
		colorBounds.add(greenCyanHues);
		int[] cyanHues = { 170, 200 }; //7
		colorBounds.add(cyanHues);
		int[] cyanBlueHues = { 201, 220 }; //8
		colorBounds.add(cyanBlueHues);
		int[] blueHues = { 221, 240 }; //9
		colorBounds.add(blueHues);
		int[] blueMagentaHues = { 241, 280 }; //10
		colorBounds.add(blueMagentaHues);
		int[] magentaHues = { 281, 320 }; //11
		colorBounds.add(magentaHues);
		int[] magentaPinkHues = { 321, 330 }; //12
		colorBounds.add(magentaPinkHues);
		int[] pinkHues = { 331, 345 }; //13
		colorBounds.add(pinkHues);
		int[] pinkRedHues = { 346, 355 }; //14
		colorBounds.add(pinkRedHues);
		int[] redHues = { 355, 10 }; //15
		colorBounds.add(redHues);

		//white = 16, black = 17

		int[] counts = new int[colorBounds.size() + 2];
		
		//TODO: this needs to be generalized so that the skip increases generically depending on pixel count
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
				//the hsb values are returned as fractions that need to be converted to be multiplied by some
				//constant factor to get their "true" value
				//hue is measured in degrees
				hsb[0] = hsb[0] * 360;
				//saturation and brightness are both percentages
				hsb[1] = hsb[1] * 100;
				hsb[2] = hsb[2] * 100;
				boolean foundIt = false;
				//if the brightness is less than or equal to 15, the color is black
				if(hsb[2] <= 15) {
					counts[17]++;
					foundIt = true;
				}
				//if saturation is 0 and brightness is 100, the color is white
				else if(hsb[1] == 0 && hsb[2] == 100 && !foundIt) {
					counts[16]++;
					foundIt = true;
				}
				//all other colors are checked for here
				if(!foundIt) {
					for(int i = 0; i < colorBounds.size() - 1; i++) {
						if(colorBounds.get(i)[0] <= hsb[0] && hsb[0] <= colorBounds.get(i)[1]) {
							counts[i]++;
							foundIt = true;
						}
					}
				}
				//red has to be checked for separately because it crosses the boundary between the high end
				//and low end of hues (i.e. high end = ~360; low end = ~0)
				if(!foundIt && ((redHues[0] <= hsb[0]) || (hsb[0] <= redHues[1]))) {
					counts[colorBounds.size() - 1]++;
					foundIt = true;
				}
			}
		}
		return counts;
	}

	//deprecated
	//this is another older method that used standard deviation to try to find matches
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

	//short exhaustive method for finding the maximum value of the modes array
	public int findMax(int[] modes) throws IOException {
		int max = 0;
		int indexOfMax = 0;
		for(int i = 0; i < modes.length; i++) {
			if(modes[i] > max) {
				max = modes[i];
				indexOfMax = i;
			}
		}
		//if the dominant color is pure white (saturation of 0, brightness of 100), use the second most frequent color
		//as the max
		if(indexOfMax == 16) {
			modes[16] = 0;
			indexOfMax = findMax(modes);
		}
		return indexOfMax;
	}

	//if two images have the same dominant color scheme (i.e. the same color mode) then it's a match!
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
		CatchImage test = new CatchImage("http://zoxkitchen.com/wp-content/uploads/2014/08/tomato.jpg");
		int[] modes = test.calculateColorModes();
		for(int i = 0; i < modes.length; i++) {
			System.out.println(modes[i]);
		}
		System.out.println("MAX: " + test.findMax(modes));
	}
}