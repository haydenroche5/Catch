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
		//		int[] counts = new int[width*height];

		//		int pinkRLow 
		//		int pinkRHigh
		//		int pinkGLow
		//		int pinkGHigh
		//		int pinkBLow
		//		int pinkBHigh

		//these bounds come from the Wikiedpia page "Web colors"
		ArrayList<int[]> colorBounds = new ArrayList<int[]>();
		//pink contains none of the other bounds
		int[] pinkBounds = { 199, 255, 20, 192, 133, 203 };
		colorBounds.add(pinkBounds);
		//red contains orange
		int[] redBounds = { 139, 254, 0, 160, 0, 128 };
		colorBounds.add(redBounds);
		//orange contains none of the other bounds
		//solution: only orange if R=255 and other conditions hold (this means red RHigh = 254 now)
		int[] orangeBounds = { 255, 255, 69, 165, 0, 80 };
		colorBounds.add(orangeBounds);
		//yellow contains none of the other bounds
		int[] yellowBounds = { 189, 255, 183, 255, 0, 224 };
		colorBounds.add(yellowBounds);
		//brown contains pink, red, orange
		//solution: if not contained in anything else, it's brown
		int[] brownBounds = { 128, 255, 0, 248, 0, 220 };
		//green contains none of the other bounds
		int[] greenBounds = { 0, 173, 100, 255, 0, 154 };
		colorBounds.add(greenBounds);
		//cyan contains none of the other bounds
		int[] cyanBounds = { 0, 224, 128, 255, 128, 255 };
		colorBounds.add(cyanBounds);
		//blue contains none of the other bounds
		int[] blueBounds = { 0, 176, 0, 224, 112, 255 };
		colorBounds.add(blueBounds);
		//purple contains pink
		int[] purpleBounds = { 72, 255, 0, 230, 128, 255 };
		colorBounds.add(purpleBounds);
		//white contains none of the other bounds
		int[] whiteBounds = { 240, 255, 228, 255, 215, 255 };
		colorBounds.add(whiteBounds);
		//black contains none of the other bounds
		int[] blackGrayBounds = { 0, 220, 0, 220, 0, 220 };
		colorBounds.add(blackGrayBounds);

		//colorBounds's size is 11 and doesn't include brown
		//counts's size is 12 because it includes brown at the end
		//0 = pink, 1 = red, 2 = orange, 3 = yellow, 4 = green, 5 = cyan, 6 = blue, 7 = purple, 8 = white, 9 = black
		int[] counts = new int[colorBounds.size() + 1];

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				Color color = new Color(seedImage.getRGB(x, y));
				int red = color.getRed();
				int green = color.getGreen();
				int blue = color.getBlue();
				for(int i = 0; i < colorBounds.size(); i++) {
					//if the color falls into one of the bounds defined above, excluding brown,
					//increment that bounds's count in the counts array
					if(red >= colorBounds.get(i)[0] && red <= colorBounds.get(i)[1] && 
							green >= colorBounds.get(i)[2] && green <= colorBounds.get(i)[3] &&
							blue >= colorBounds.get(i)[4] && blue <= colorBounds.get(i)[5]) {
						counts[i]++;
					}
					//if the color does not fall into any of the bounds, but falls into brown, increment
					//brown's count
					else if (red >= brownBounds[0] && red <= brownBounds[1] && 
							green >= brownBounds[2] && green <= brownBounds[3] &&
							blue >= brownBounds[4] && blue <= brownBounds[5]) {
						counts[colorBounds.size()]++;
					}
				}
			}
		}
		for(int j = 0; j < counts.length; j++) {
			System.out.println(counts[j]);
		}
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
		CatchImage test = new CatchImage("http://southernson.com/photos/international/usa/miscellaneous/monument-valley/sentinel-mesa-monument-valley.jpg");
		test.getColorModes();
	}
}