package katch;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;

public class CatchImage
{
	private URL imageUrl;

	public CatchImage(String url)
			throws MalformedURLException
	{
		this.imageUrl = new URL(url);
	}

	private double[] processImage() throws IOException {
		BufferedImage seedImage = ImageIO.read(imageUrl);
		int height = seedImage.getHeight();
		int width = seedImage.getWidth();

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

	private int[] getColorModes() throws IOException {
		BufferedImage seedImage = ImageIO.read(imageUrl);
		int height = seedImage.getHeight();
		int width = seedImage.getWidth();
		//this list holds arrays containing the RGB values of every pixel (one array for every pixel)
		ArrayList<Color> pixelColors = new ArrayList<Color>();
		//a representative rgb value array (for one pixel)
		//int[] color = new int[3];
		//this array has a one to one correspondence with each pixel in the image, that is, the first entry in the array
		//corresponds to the first pixel, the second entry to the second pixel, etc.
		//the values in the array correspond to the number of times a specific color appears in the image
		//the color is at the same index in the pixelColors list
		int[] counts = new int[width*height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				Color color = new Color(seedImage.getRGB(x, y));
				System.out.println("x: " + x + ", y: " + y);
				boolean foundIt = false;
				int i = 0;
				if(pixelColors.contains(color)) {
					counts[pixelColors.indexOf(color)]++;
					foundIt = true;
				}
//				while(!foundIt && i < pixelColors.size()) {
//					if(color.equals(pixelColors.get(i))) {
//						counts[i]++;
//						foundIt = true;
//					}
//					i++;
//				}
				if(!foundIt) {
					pixelColors.add(color);
					counts[(y*width)+x] = 1;
				}
			}
		}
		Arrays.sort(counts);
		System.out.println(counts);
		return counts;
	}

	public double[] calculateStandDev(double[] seedAverages) throws IOException {
		BufferedImage seedImage = ImageIO.read(this.imageUrl);
		int height = seedImage.getHeight();
		int width = seedImage.getWidth();

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

	public boolean compareImages(CatchImage otherPhoto) throws IOException {
		double[] colors = processImage();
		double[] otherColors = otherPhoto.calculateStandDev(colors);
		boolean isMatch = false;
		if ((otherColors[0] < 80.0) && (otherColors[1] < 80.0) && (otherColors[2] < 80.0)) {
			isMatch = true;
		}
		return isMatch;
	}

	public URL getImageURL() {
		return this.imageUrl;
	}

	public static void main(String[] args) throws IOException {
		CatchImage test = new CatchImage("http://www.mariposajunglelodge.com/images/bg-page.jpg");
		test.getColorModes();
	}
}