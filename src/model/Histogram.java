package model;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * The `Histogram` class represents a histogram for RGB color values and provides methods like
 * creating histogram images, adding values to the histogram and calculating peak values.
 */
public class Histogram {
  public int[] histogramR;
  public int[] histogramG;
  public int[] histogramB;
  private final int minValue;
  private final int maxValue;
  private int maxCount;

  /**
   * Constructs a `Histogram` instance with the specified minimum and maximum values for color and
   * initializes the histogram arrays.
   *
   * @param minValue The minimum value for color intensity.
   * @param maxValue The maximum value for color intensity.
   */
  public Histogram(int minValue, int maxValue) {
    if (minValue >= maxValue || minValue < 0 || maxValue > 255) {
      throw new IllegalArgumentException("Invalid Histogram");

    }
    this.minValue = minValue;
    this.maxValue = maxValue;
    this.histogramR = new int[maxValue - minValue + 1];
    this.histogramG = new int[maxValue - minValue + 1];
    this.histogramB = new int[maxValue - minValue + 1];
  }

  /**
   * Add a color value to the histogram.
   *
   * @param redValue   The red color value.
   * @param greenValue The green color value.
   * @param blueValue  The blue color value.
   */
  public void addValue(int redValue, int greenValue, int blueValue) {
    if (redValue >= minValue && redValue <= maxValue) {
      histogramR[redValue - minValue]++;
    }
    if (greenValue >= minValue && greenValue <= maxValue) {
      histogramG[greenValue - minValue]++;
    }
    if (blueValue >= minValue && blueValue <= maxValue) {
      histogramB[blueValue - minValue]++;
    }
  }

  /**
   * Calculate the maximum count of all color values in the histogram.
   */
  public int calculateMaxCount() {
    maxCount = 0;
    for (int i = 0; i < histogramR.length; i++) {
      if (histogramR[i] > maxCount) {
        maxCount = histogramR[i];
      }
      if (histogramG[i] > maxCount) {
        maxCount = histogramG[i];
      }
      if (histogramB[i] > maxCount) {
        maxCount = histogramB[i];
      }
    }
    return maxCount;
  }

  /**
   * Create a histogram image with the specified width and height.
   *
   * @param width  The width of the histogram image.
   * @param height The height of the histogram image.
   * @return The histogram image.
   */
  public BufferedImage createHistogramImage(int width, int height) {
    BufferedImage histogramImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    Graphics2D g2d = histogramImage.createGraphics();

    // Clear the image with a white background.
    g2d.setColor(Color.WHITE);
    g2d.fillRect(0, 0, width, height);

    g2d.setColor(Color.RED);
    drawHistogramLine(g2d, histogramR, width, height);

    g2d.setColor(Color.GREEN);
    drawHistogramLine(g2d, histogramG, width, height);

    g2d.setColor(Color.BLUE);
    drawHistogramLine(g2d, histogramB, width, height);

    g2d.dispose();
    return histogramImage;
  }

  private void drawHistogramLine(Graphics2D g2d, int[] values, int width, int height) {
    g2d.setStroke(new BasicStroke(1.0f));
    for (int i = 1; i < values.length; i++) {
      int x1 = (i - 1) * width / (values.length - 1);
      int y1 = height - values[i - 1] * height / maxCount;
      int x2 = i * width / (values.length - 1);
      int y2 = height - values[i] * height / maxCount;
      g2d.drawLine(x1, y1, x2, y2);
    }
  }

  /**
   * Finds the peak value in the given histogram array.
   *
   * @param histogram The array representing the histogram.
   * @return The peak value in the histogram.
   */
  public int findPeakValue(int[] histogram) {
    int peak = minValue;
    int maxCount = 0;

    for (int i = 0; i < histogram.length; i++) {
      if (histogram[i] > maxCount) {
        maxCount = histogram[i];
        peak = i + minValue;
      }
    }

    return peak;
  }

  /**
   * Creates a histogram from the RGB data of an image. The histogram is represented by three
   * arrays, one for each color channel (red, green, and blue).
   *
   * @param sourceRGBData The RGB data of the source image, represented as a 3D array.
   */
  public void createHistogram(int[][][] sourceRGBData) {
    int height = sourceRGBData.length;
    int width = sourceRGBData[0].length;
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {

        int redValue = sourceRGBData[y][x][0];
        int greenValue = sourceRGBData[y][x][1];
        int blueValue = sourceRGBData[y][x][2];
        addValue(redValue, greenValue, blueValue);

      }
    }
    calculateMaxCount();

  }


}