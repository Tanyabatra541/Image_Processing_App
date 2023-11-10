package model;
//
//import java.util.Arrays;
//import javax.imageio.ImageIO;
//import java.awt.*;
//import java.awt.image.BufferedImage;
//import java.io.File;
//import java.io.IOException;
//
//public class Histogram {
//  private int[] histogram;
//  private int minValue;
//  private int maxValue;
//
//  private int[] values;
//
//  private int[] valuesR;
//  private int[] valuesG;
//  private int[] valuesB;
//
//  public Histogram(int minValue, int maxValue) {
//    this.minValue = minValue;
//    this.maxValue = maxValue;
//    this.histogram = new int[maxValue - minValue + 1];
//    this.valuesR = new int[maxValue - minValue + 1];
//    this.valuesG = new int[maxValue - minValue + 1];
//    this.valuesB = new int[maxValue - minValue + 1];
//  }
//
//  public void addValue(int redValue, int greenValue, int blueValue) {
//    if (redValue >= minValue && redValue <= maxValue
//            && greenValue >= minValue && greenValue <= maxValue
//            && blueValue >= minValue && blueValue <= maxValue) {
//      histogram[redValue - minValue]++;
//      valuesR[redValue - minValue]++;
//      valuesG[greenValue - minValue]++;
//      valuesB[blueValue - minValue]++;
//    }
//  }
//
//  public int findPeakValue() {
//    int peak = minValue;
//    int maxCount = 0;
//
//    for (int i = 0; i < histogram.length; i++) {
//      if (histogram[i] > maxCount) {
//        maxCount = histogram[i];
//        peak = i + minValue;
//      }
//    }
//
//    return peak;
//  }
//
//  public int getMaxCount() {
//    return Arrays.stream(histogram).max().orElse(0);
//  }
//
//  public int getMinValue() {
//    return minValue;
//  }
//
//  public int getMaxValue() {
//    return maxValue;
//  }
//
//  public BufferedImage createHistogramImage(int width, int height, Color color) {
//    BufferedImage histogramImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//    Graphics2D g2d = histogramImage.createGraphics();
//
//    // Clear the image with a white background.
//    g2d.setColor(Color.WHITE);
//    g2d.fillRect(0, 0, width, height);
//
//    g2d.setColor(color);
//    g2d.setStroke(new BasicStroke(2.0f));
//
//    for (int i = 1; i < values.length; i++) {
//      int x1 = (i - 1) * width / (values.length - 1);
//      int y1 = height - values[i - 1] * height / maxValue;
//      int x2 = i * width / (values.length - 1);
//      int y2 = height - values[i] * height / maxValue;
//      g2d.drawLine(x1, y1, x2, y2);
//    }
//
//    g2d.dispose();
//    return histogramImage;
//  }
//
//  public void saveHistogramImage(String destName, int width, int height, Color color) {
//    BufferedImage histogramImage = createHistogramImage(width, height, color);
//
//    try {
//      ImageIO.write(histogramImage, "PNG", new File(destName));
//      System.out.println("Histogram image saved as " + destName);
//    } catch (IOException e) {
//      System.out.println("Failed to save histogram image: " + e.getMessage());
//    }
//  }
//
//  public BufferedImage createCombinedHistogramImage(int width, int height) {
//    BufferedImage histogramImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//    Graphics2D g2d = histogramImage.createGraphics();
//
//    // Clear the image with a white background.
//    g2d.setColor(Color.WHITE);
//    g2d.fillRect(0, 0, width, height);
//
//    g2d.setColor(Color.RED);
//    drawHistogramLine(g2d, valuesR, width, height);
//
//    g2d.setColor(Color.GREEN);
//    drawHistogramLine(g2d, valuesG, width, height);
//
//    g2d.setColor(Color.BLUE);
//    drawHistogramLine(g2d, valuesB, width, height);
//
//    g2d.dispose();
//    return histogramImage;
//  }
//
//  private void drawHistogramLine(Graphics2D g2d, int[] values, int width, int height) {
//    g2d.setStroke(new BasicStroke(2.0f));
//    for (int i = 1; i < values.length; i++) {
//      int x1 = (i - 1) * width / (values.length - 1);
//      int y1 = height - values[i - 1] * height / getMaxCount();
//      int x2 = i * width / (values.length - 1);
//      int y2 = height - values[i] * height / getMaxCount();
//      g2d.drawLine(x1, y1, x2, y2);
//    }
//  }
//}
//

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Histogram {
  int[] histogramR;
  int[] histogramG;
  int[] histogramB;
  private int minValue;
  private int maxValue;
  private int maxCount;

  public Histogram(int minValue, int maxValue) {
    this.minValue = minValue;
    this.maxValue = maxValue;
    this.histogramR = new int[maxValue - minValue + 1];
    this.histogramG = new int[maxValue - minValue + 1];
    this.histogramB = new int[maxValue - minValue + 1];
  }

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

  public void calculateMaxCount() {
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
  }

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

}