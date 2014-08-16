package katch;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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
    BufferedImage seedImage = ImageIO.read(this.imageUrl);
    int height = seedImage.getHeight();
    int width = seedImage.getWidth();

    double totalRed = 0.0D;
    double totalGreen = 0.0D;
    double totalBlue = 0.0D;
    double totalPix = width * height;

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
        redVariance = (int)(redVariance + Math.pow(pixelColor.getRed() - seedAverages[0], 2.0D));
        greenVariance = (int)(greenVariance + Math.pow(pixelColor.getGreen() - seedAverages[1], 2.0D));
        blueVariance = (int)(blueVariance + Math.pow(pixelColor.getBlue() - seedAverages[2], 2.0D));
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
    if ((otherColors[0] < 80.0D) && 
      (otherColors[1] < 80.0D) && 
      (otherColors[2] < 80.0D)) {
      isMatch = true;
    }

    return isMatch;
  }

  public URL getImageURL() {
    return this.imageUrl;
  }

  public static void main(String[] args) throws IOException {
    CatchImage test = new CatchImage("http://www.dinosaurusi.com/video_slike/pkEAf4Bymg-Dinosaurus_-_Dinosaur_-_Dinosaurio_-_Dinosaure_-_Pisanosaurus-003.jpg");
    test.processImage();
  }
}