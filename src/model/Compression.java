package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * The `Compression` class provides methods for compressing images using Haar Wavelet Transform
 * and thresholding techniques.
 */
class Compression {

  private double[][] rPadded;
  private double[][] gPadded;
  private double[][] bPadded;

  private double[][] transformedR;
  private double[][] transformedG;
  private double[][] transformedB;


  private void initializeArrays(int height, int width) {
    rPadded = new double[height][width];
    gPadded = new double[height][width];
    bPadded = new double[height][width];

    transformedR = new double[height][width];
    transformedG = new double[height][width];
    transformedB = new double[height][width];
  }

  private void initialPadding(int[][][] imageRGBData, double compressionPercentage) {
    int height = imageRGBData.length;
    int width = imageRGBData[0].length;
    initializeArrays(height, width);

    double[][] r = new double[height][width];
    double[][] g = new double[height][width];
    double[][] b = new double[height][width];

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        r[y][x] = imageRGBData[y][x][0];
        g[y][x] = imageRGBData[y][x][1];
        b[y][x] = imageRGBData[y][x][2];
      }
    }


    // Check if compressionPercentage is non-zero before padding
    if (compressionPercentage > 0) {
      rPadded = padImage(r);
      gPadded = padImage(g);
      bPadded = padImage(b);
    } else {
      rPadded = r;
      gPadded = g;
      bPadded = b;
    }


    transformedR = new double[rPadded.length][rPadded[0].length];
    transformedG = new double[gPadded.length][gPadded[0].length];
    transformedB = new double[bPadded.length][bPadded[0].length];
  }

  private double[][] padImage(double[][] imageArr) {
    int rows = imageArr.length;
    int cols = imageArr[0].length;
    int maxLen = Math.max(rows, cols);
    int newSize = 1;
    while (newSize < maxLen) {
      newSize <<= 1;
    }
    int newRows = newSize;
    int newCols = newSize;
    double[][] paddedImage = new double[newRows][newCols];

    if (newRows == rows && newCols == cols) {
      paddedImage = new double[imageArr.length][imageArr[0].length];
    }

    for (int i = 0; i < rows; i++) {
      System.arraycopy(imageArr[i], 0, paddedImage[i], 0, cols);
    }

    return paddedImage;
  }


  private void applyCompression(double compressionPercentage, int[][][] sourceImage) {
    transformColorChannels();
    double threshold = calculateThreshold(compressionPercentage);
    applyThreshold(threshold);
    inverseTransformColorChannels(sourceImage.length, sourceImage[0].length);
  }


  private void transformColorChannels() {
    transformedR = applyHaarWaveletTransform(rPadded);
    transformedG = applyHaarWaveletTransform(gPadded);
    transformedB = applyHaarWaveletTransform(bPadded);
  }

  private void inverseTransformColorChannels(int originalRows, int originalCols) {
    transformedR = inverseHaarWaveletTransform(transformedR, originalRows, originalCols);
    transformedG = inverseHaarWaveletTransform(transformedG, originalRows, originalCols);
    transformedB = inverseHaarWaveletTransform(transformedB, originalRows, originalCols);
  }


  private void applyThreshold(double threshold) {
    applyThresholdToChannel(transformedR, threshold);
    applyThresholdToChannel(transformedG, threshold);
    applyThresholdToChannel(transformedB, threshold);
  }

  private void applyThresholdToChannel(double[][] channel, double threshold) {
    int width = channel.length;
    int height = channel[0].length;

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        if (Math.abs(channel[x][y]) <= threshold) {
          channel[x][y] = 0;
        }
      }
    }
  }

  private double calculateThreshold(double compressionPercentage) {
    Set<Double> uniqueValues = new HashSet<>();
    uniqueValues = addToUniqueValues(transformedR, uniqueValues);
    uniqueValues = addToUniqueValues(transformedG, uniqueValues);
    uniqueValues = addToUniqueValues(transformedB, uniqueValues);
    List<Double> sortedList = new ArrayList<>(uniqueValues);
    Collections.sort(sortedList);
    int totalCount = sortedList.size();
    int cutOffIndex = Math.min(Math.round((float) (compressionPercentage * totalCount) / 100),
            totalCount - 1);

    double threshold = sortedList.get(cutOffIndex);
    return threshold;
  }

  private Set<Double> addToUniqueValues(double[][] imageArr, Set<Double> uniqueValues) {
    int rows = imageArr.length;
    int cols = imageArr[0].length;
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        uniqueValues.add(Math.abs(imageArr[i][j]));
      }
    }
    return uniqueValues;
  }

  private double[][] applyHaarWaveletTransform(double[][] imageArr) {
    int rows = imageArr.length;
    int cols = imageArr[0].length;

    for (int i = 0; i < rows; i++) {
      imageArr[i] = transformImage(imageArr[i], cols);
    }
    for (int j = 0; j < cols; j++) {
      double[] column = new double[rows];
      for (int i = 0; i < rows; i++) {
        column[i] = imageArr[i][j];
      }
      column = transformImage(column, rows);
      for (int i = 0; i < rows; i++) {
        imageArr[i][j] = column[i];
      }
    }

    return imageArr;
  }

  private double[] transformImage(double[] s, int length) {
    int mid = length / 2;
    double[] avg = new double[mid];
    double[] diff = new double[mid];

    for (int i = 0, j = 0; i < mid; i++, j += 2) {
      double a = s[j];
      double b = s[j + 1];
      avg[i] = (a + b) / Math.sqrt(2);
      diff[i] = (a - b) / Math.sqrt(2);
    }

    double[] transformedSequence = new double[length];
    System.arraycopy(avg, 0, transformedSequence, 0, mid);
    System.arraycopy(diff, 0, transformedSequence, mid, mid);

    return transformedSequence;
  }

  private double[][] inverseHaarWaveletTransform(double[][] imageArr, int originalRows, int
          originalCols) {
    int rows = imageArr.length;
    int cols = imageArr[0].length;

    // Apply inverse Haar transform to columns
    for (int j = 0; j < cols; j++) {
      double[] column = new double[rows];
      for (int i = 0; i < rows; i++) {
        column[i] = imageArr[i][j];
      }
      column = inverseTransformImage(column, rows);
      for (int i = 0; i < rows; i++) {
        imageArr[i][j] = column[i];
      }
    }

    for (int i = 0; i < rows; i++) {
      imageArr[i] = inverseTransformImage(imageArr[i], cols);
    }

    double[][] unpaddedImage = new double[originalRows][originalCols];
    for (int i = 0; i < originalRows; i++) {
      for (int j = 0; j < originalCols; j++) {
        unpaddedImage[i][j] = (i < rows && j < cols) ? imageArr[i][j] : 0.0;
      }
    }

    return unpaddedImage;
  }


  private double[] inverseTransformImage(double[] s, int length) {
    int mid = length / 2;
    double[] originalSequence = new double[length];

    for (int i = 0, j = 0; i < mid; i++, j += 2) {
      if (j < length - 1) {
        double avg = s[i];
        double diff = s[i + mid];
        originalSequence[j] = (avg + diff) / Math.sqrt(2);
        originalSequence[j + 1] = (avg - diff) / Math.sqrt(2);
      } else {
        // If length is odd, handle the last element
        originalSequence[j] = s[i];
      }
    }

    return originalSequence;
  }


  private int[][][] combinePixel(int[][][] sourceRgb) {

    int height = sourceRgb.length;
    int width = sourceRgb[0].length;

    int[][][] imageRGBData = new int[height][width][3];

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        // Round and convert each channel to int
        int red = (int) Math.round(transformedR[y][x]);
        int green = (int) Math.round(transformedG[y][x]);
        int blue = (int) Math.round(transformedB[y][x]);

        // Ensure that the values are within the valid range (0 to 255)
        red = Math.max(0, Math.min(255, red));
        green = Math.max(0, Math.min(255, green));
        blue = Math.max(0, Math.min(255, blue));

        // Store the values in the imageRGBData array
        imageRGBData[y][x][0] = red;
        imageRGBData[y][x][1] = green;
        imageRGBData[y][x][2] = blue;
      }
    }

    return imageRGBData;
  }


  /**
   * Compress an image using Haar Wavelet Transform and thresholding techniques.
   *
   * @param sourceRgb             The RGB data of the source image.
   * @param compressionPercentage The percentage of compression to be applied to the image.
   * @return The RGB data of the compressed image.
   */
  protected int[][][] compress(int[][][] sourceRgb, double compressionPercentage) {
    // Check if compressionPercentage is within a valid range
    if (compressionPercentage < 0 || compressionPercentage > 100) {
      System.out.println("Compression percentage must be between 0 and 100.");
      return null;
    } else {
      initialPadding(sourceRgb, compressionPercentage);
      applyCompression(compressionPercentage, sourceRgb);
      return combinePixel(sourceRgb);
    }

  }

}