package model;

import java.awt.image.BufferedImage;
import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

/**
 * The `AbstractImage` class is a class that implements the `ImageOperations` interface. It provides
 * functionality to apply various image operations like blue, sharpen, brighten, sepia, color
 * correct, levels adjust, etc.
 */
public class ImageModel implements ImageOperations {

  /**
   * The `imageMap` is a map that stores the name of the image as the key and the image content as
   * the value.
   */
  public static final Map<String, ImageContent> IMAGE_MAP = new HashMap<>();


  protected float[] sharpeningKernel = {
          -1.0f / 8.0f, -1.0f / 8.0f, -1.0f / 8.0f, -1.0f / 8.0f, -1.0f / 8.0f,
          -1.0f / 8.0f, 1.0f / 4.0f, 1.0f / 4.0f, 1.0f / 4.0f, -1.0f / 8.0f,
          -1.0f / 8.0f, 1.0f / 4.0f, 1.0f, 1.0f / 4.0f, -1.0f / 8.0f,
          -1.0f / 8.0f, 1.0f / 4.0f, 1.0f / 4.0f, 1.0f / 4.0f, -1.0f / 8.0f,
          -1.0f / 8.0f, -1.0f / 8.0f, -1.0f / 8.0f, -1.0f / 8.0f, -1.0f / 8.0f
  };

  protected float[] gaussianKernel = {
          1.0f / 16.0f, 1.0f / 8.0f, 1.0f / 16.0f,
          1.0f / 8.0f, 1.0f / 4.0f, 1.0f / 8.0f,
          1.0f / 16.0f, 1.0f / 8.0f, 1.0f / 16.0f
  };


  /**
   * Load an image from a file and store it in the image map.
   *
   * @param imageName The name to associate with the loaded image.
   * @throws IOException If an error occurs while loading the image.
   */
  @Override
  public void loadImageInMap(String imageName, int[][][] imageRGBData) {
    if (imageRGBData != null) {
      ImageContent image = new ImageContent(imageName, imageRGBData);

      IMAGE_MAP.put(imageName, image);
      System.out.println("Loaded image: " + imageName);
    } else {
      System.out.println("Failed to load the image from: " + imageName);
    }
  }

  /**
   * Flip an image horizontally and save it as a new image with the given name.
   *
   * @param sourceImageName The name of the source image.
   * @param destImageName   The name of the destination image.
   */
  @Override
  public void horizontalFlipImage(String sourceImageName, String destImageName) {
    ImageContent sourceImage = IMAGE_MAP.get(sourceImageName);

    if (sourceImage != null) {
      int[][][] sourceRGBData = IMAGE_MAP.get(sourceImageName).getRgbDataMap();

      if (sourceRGBData != null) {
        int width = sourceRGBData[0].length;
        int height = sourceRGBData.length;

        int[][][] flippedRGBData = new int[height][width][3];

        for (int y = 0; y < height; y++) {
          for (int x = 0; x < width; x++) {
            flippedRGBData[y][x] = sourceRGBData[y][width - x - 1];
          }
        }
        createPPMContent(width, height, flippedRGBData);
        ImageContent flippedImage = new ImageContent(destImageName, flippedRGBData);
        IMAGE_MAP.put(destImageName, flippedImage);
        System.out.println("Image '" + sourceImageName + "' flipped horizontally and saved as '"
                + destImageName + "'.");
      } else {
        System.out.println("Flip failed: " + sourceImageName);
      }
    } else {
      System.out.println("Source image not found: " + sourceImageName);
    }
  }


  /**
   * Flip an image vertically and save it as a new image with the given name.
   *
   * @param sourceImageName The name of the source image.
   * @param destImageName   The name of the destination image.
   */
  @Override
  public void verticalFlipImage(String sourceImageName, String destImageName) {
    ImageContent sourceImage = IMAGE_MAP.get(sourceImageName);
    if (sourceImage == null) {
      System.out.println("Source image not found: " + sourceImageName);
    }

    int[][][] sourceRGBData = IMAGE_MAP.get(sourceImageName).getRgbDataMap();

    int height = sourceRGBData.length;
    int width = sourceRGBData[0].length;
    int[][][] flippedRGBData = new int[height][width][3];

    for (int y = 0; y < height; y++) {
      int newY = height - 1 - y;
      flippedRGBData[newY] = sourceRGBData[y];
    }

    createPPMContent(width, height, flippedRGBData);

    ImageContent flippedImage = new ImageContent(destImageName, flippedRGBData);
    IMAGE_MAP.put(destImageName, flippedImage);

    System.out.println("Vertical flip completed. Flipped image saved as " + destImageName);
  }


  private void applyConvolutionHelper(String sourceImageName, String destImageName, int
          splitPercentage, float[] kernel) {
    ImageContent sourceImage = IMAGE_MAP.get(sourceImageName);
    if (sourceImage == null) {
      System.out.println("Source image not found: " + sourceImageName);
      return;
    }

    int[][][] sourceRGBData = IMAGE_MAP.get(sourceImageName).getRgbDataMap();

    int height = sourceRGBData.length;
    int width = sourceRGBData[0].length;
    int[][][] resultRGBData = new int[height][width][3];

    int splitPosition = width * splitPercentage / 100;

    int kernelSize = (int) Math.sqrt(kernel.length);
    int kernelRadius = kernelSize / 2;

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        if (splitPercentage == 0 || (splitPercentage > 0 && x < splitPosition)) {
          for (int channel = 0; channel < 3; channel++) {
            float sum = 0.0f;
            int kernelIndex = 0;
            for (int ky = -kernelRadius; ky <= kernelRadius; ky++) {
              for (int kx = -kernelRadius; kx <= kernelRadius; kx++) {
                int pixelX = Math.min(width - 1, Math.max(0, x + kx));
                int pixelY = Math.min(height - 1, Math.max(0, y + ky));
                float kernelValue = kernel[kernelIndex];
                int pixelValue = sourceRGBData[pixelY][pixelX][channel];
                sum += kernelValue * pixelValue;
                kernelIndex++;
              }
            }
            int newValue = Math.min(255, Math.max(0, (int) sum));
            resultRGBData[y][x][channel] = newValue;
          }
        } else {
          System.arraycopy(sourceRGBData[y][x], 0, resultRGBData[y][x], 0, 3);
        }
      }
    }

    createPPMContent(width, height, resultRGBData);

    ImageContent resultImage = new ImageContent(destImageName, resultRGBData);
    IMAGE_MAP.put(destImageName, resultImage);

    System.out.println("Convolution operation completed. Result image saved as " + destImageName);
  }

  private void sharpenImageHelper(String sourceImageName, String destImageName,
                                  int splitPercentage) {
    applyConvolutionHelper(sourceImageName, destImageName, splitPercentage, sharpeningKernel);
  }

  private void blurImageHelper(String sourceImageName, String destImageName, int splitPercentage) {
    applyConvolutionHelper(sourceImageName, destImageName, splitPercentage, gaussianKernel);
  }


  /**
   * Applies a sharpening kernel to a particular percentage of the source image depending on the
   * splitPercentage parameter passed, creating a split sharpened image having both the operated
   * image and the original image with the given name.
   *
   * @param sourceName      The name of the source image.
   * @param destName        The name of the destination sharpened image.
   * @param splitPercentage The percentage of the image to be sharpened.
   */
  @Override
  public void sharpenImage(String sourceName, String destName, int splitPercentage) {
    sharpenImageHelper(sourceName, destName, splitPercentage);
  }

  /**
   * Applies a sharpening kernel to the source image, creating a sharpened image with the given
   * name.
   *
   * @param sourceName The name of the source image.
   * @param destName   The name of the destination sharpened image.
   */
  @Override
  public void sharpenImage(String sourceName, String destName) {
    sharpenImageHelper(sourceName, destName, 0);
  }


  /**
   * Applies a Gaussian blurring kernel to a particular percentage of the source image depending on
   * the splitPercentage parameter passed, creating a split blurred image having both the operated
   * image and the original image with the given name.
   *
   * @param sourceName      The name of the source image.
   * @param destName        The name of the destination sharpened image.
   * @param splitPercentage The percentage of the image to be blurred.
   */
  @Override
  public void blurImage(String sourceName, String destName, int splitPercentage) {
    blurImageHelper(sourceName, destName, splitPercentage);
  }

  /**
   * Applies a blurring effect to the source image, creating a blurred version with the given name.
   * This method uses a Gaussian blurring kernel.
   *
   * @param sourceName The name of the source image.
   * @param destName   The name of the destination blurred image.
   */
  @Override
  public void blurImage(String sourceName, String destName) {
    blurImageHelper(sourceName, destName, 0);
  }


  /**
   * Brighten the colors of the source image by a specified increment and save the brightened
   * image with the given name.
   *
   * @param sourceImageName The name of the source image.
   * @param destImageName   The name of the destination brightened image.
   * @param increment       The amount by which to increment the color values. Positive values
   *                        brighten, negative values darken.
   */
  @Override
  public void brightenImage(String sourceImageName, String destImageName, int increment) {
    ImageContent sourceImage = IMAGE_MAP.get(sourceImageName);
    if (sourceImage == null) {
      System.out.println("Source image not found: " + sourceImageName);
    }

    int[][][] sourceRGBData = IMAGE_MAP.get(sourceImageName).getRgbDataMap();

    int height = sourceRGBData.length;
    int width = sourceRGBData[0].length;
    int[][][] brightenedRGBData = new int[height][width][3];

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        for (int channel = 0; channel < 3; channel++) {
          int originalValue = sourceRGBData[y][x][channel];
          int newValue = originalValue + increment;
          newValue = Math.min(255, Math.max(0, newValue));
          brightenedRGBData[y][x][channel] = newValue;
        }
      }
    }

    createPPMContent(width, height, brightenedRGBData);

    ImageContent brightenedImage = new ImageContent(destImageName, brightenedRGBData);
    IMAGE_MAP.put(destImageName, brightenedImage);

    System.out.println("Image brightening completed. Brightened image saved as " + destImageName);
  }

  private void sepiaImageHelper(String sourceName, String destName, int splitPercentage) {
    ImageContent sourceImage = IMAGE_MAP.get(sourceName);
    if (sourceImage == null) {
      System.out.println("Source image not found: " + sourceName);
      return;
    }
    int[][][] sourceRGBData = IMAGE_MAP.get(sourceName).getRgbDataMap();
    int height = sourceRGBData.length;
    int width = sourceRGBData[0].length;
    int[][][] sepiaRGBData = new int[height][width][3];

    int splitPosition = width * splitPercentage / 100;

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        if (splitPercentage == 0 || x <= splitPosition) {
          int r = sourceRGBData[y][x][0];
          int g = sourceRGBData[y][x][1];
          int b = sourceRGBData[y][x][2];

          int tr = (int) (0.393 * r + 0.769 * g + 0.189 * b);
          int tg = (int) (0.349 * r + 0.686 * g + 0.168 * b);
          int tb = (int) (0.272 * r + 0.534 * g + 0.131 * b);

          tr = Math.min(255, Math.max(0, tr));
          tg = Math.min(255, Math.max(0, tg));
          tb = Math.min(255, Math.max(0, tb));

          sepiaRGBData[y][x][0] = tr;
          sepiaRGBData[y][x][1] = tg;
          sepiaRGBData[y][x][2] = tb;
        } else {
          sepiaRGBData[y][x][0] = sourceRGBData[y][x][0];
          sepiaRGBData[y][x][1] = sourceRGBData[y][x][1];
          sepiaRGBData[y][x][2] = sourceRGBData[y][x][2];
        }
      }
    }

    createPPMContent(width, height, sepiaRGBData);

    ImageContent sepiaImage = new ImageContent(destName, sepiaRGBData);
    IMAGE_MAP.put(destName, sepiaImage);

    System.out.println("Sepia filter applied with " + splitPercentage + "% split. Sepia-toned "
            + "image saved as " + destName);
  }


  /**
   * Applies the sepia tone to a particular percentage of the source image depending on
   * the splitPercentage parameter passed, creating a split sepia image having both the operated
   * image and the original image with the given name.
   *
   * @param sourceName      The name of the source image.
   * @param destName        The name of the destination sharpened image.
   * @param splitPercentage The percentage of the image to be sepia-toned.
   */
  @Override
  public void sepiaImage(String sourceName, String destName, int splitPercentage) {
    sepiaImageHelper(sourceName, destName, splitPercentage);
  }

  /**
   * Applies a sepia filter to the source image and save the sepia-toned image with the given name.
   *
   * @param sourceName The name of the source image.
   * @param destName   The name of the destination sepia-toned image.
   */
  @Override
  public void sepiaImage(String sourceName, String destName) {
    sepiaImageHelper(sourceName, destName, 0);
  }


  /**
   * Combine three source images representing the red, green, and blue channels into a single
   * image.
   * The combined image is saved with the given name.
   *
   * @param combinedName The name of the combined RGB image.
   * @param redName      The name of the source image for the red channel.
   * @param greenName    The name of the source image for the green channel.
   * @param blueName     The name of the source image for the blue channel.
   */
  @Override
  public void combineRGBImages(String combinedName, String redName, String greenName,
                               String blueName) {
    ImageContent redImage = IMAGE_MAP.get(redName);
    ImageContent greenImage = IMAGE_MAP.get(greenName);
    ImageContent blueImage = IMAGE_MAP.get(blueName);

    if (redImage == null || greenImage == null || blueImage == null) {
      System.out.print("One or more source images not found.");

    } else {

      int[][][] redRGBData = IMAGE_MAP.get(redName).getRgbDataMap();
      int[][][] greenRGBData = IMAGE_MAP.get(greenName).getRgbDataMap();
      int[][][] blueRGBData = IMAGE_MAP.get(blueName).getRgbDataMap();

      int height = redRGBData.length;
      int width = redRGBData[0].length;


      if (height != greenRGBData.length || height != blueRGBData.length
              || width != greenRGBData[0].length || width != blueRGBData[0].length) {
        System.out.print("Source images have different dimensions.");

      }

      int[][][] combinedRGBData = new int[height][width][3];

      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          combinedRGBData[y][x][0] = redRGBData[y][x][0];
          combinedRGBData[y][x][1] = greenRGBData[y][x][1];
          combinedRGBData[y][x][2] = blueRGBData[y][x][2];
        }
      }

      createPPMContent(width, height, combinedRGBData);

      ImageContent combinedImage = new ImageContent(combinedName, combinedRGBData);
      IMAGE_MAP.put(combinedName, combinedImage);
      //rgbDataMap.put(combinedName, combinedRGBData);

      System.out.print("RGB channels combined. Combined image saved as " + combinedName);
    }
  }


  /**
   * Split the RGB components of a source image into separate images representing the red, green,
   * and blue channels.
   * The resulting images are saved with the specified names.
   *
   * @param sourceName    The name of the source image to split into RGB channels.
   * @param destNameRed   The name of the resulting image representing the red channel.
   * @param destNameGreen The name of the resulting image representing the green channel.
   * @param destNameBlue  The name of the resulting image representing the blue channel.
   */
  @Override
  public void rgbSplitImage(String sourceName, String destNameRed, String destNameGreen,
                            String destNameBlue) {
    ImageContent sourceImage = IMAGE_MAP.get(sourceName);
    if (sourceImage == null) {
      System.out.println("Source image not found: " + sourceName);
    } else {

      int[][][] sourceRGBData = IMAGE_MAP.get(sourceName).getRgbDataMap();

      int height = sourceRGBData.length;
      int width = sourceRGBData[0].length;
      int[][][] redRGBData = new int[height][width][3];
      int[][][] greenRGBData = new int[height][width][3];
      int[][][] blueRGBData = new int[height][width][3];

      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          int r = sourceRGBData[y][x][0];
          int g = sourceRGBData[y][x][1];
          int b = sourceRGBData[y][x][2];

          redRGBData[y][x][0] = r;
          redRGBData[y][x][1] = 0;
          redRGBData[y][x][2] = 0;

          greenRGBData[y][x][0] = 0;
          greenRGBData[y][x][1] = g;
          greenRGBData[y][x][2] = 0;

          blueRGBData[y][x][0] = 0;
          blueRGBData[y][x][1] = 0;
          blueRGBData[y][x][2] = b;
        }
      }

      createPPMContent(width, height, redRGBData);
      createPPMContent(width, height, greenRGBData);
      createPPMContent(width, height, blueRGBData);

      ImageContent redImage = new ImageContent(destNameRed, redRGBData);
      ImageContent greenImage = new ImageContent(destNameGreen, greenRGBData);
      ImageContent blueImage = new ImageContent(destNameBlue, blueRGBData);

      IMAGE_MAP.put(destNameRed, redImage);
      IMAGE_MAP.put(destNameGreen, greenImage);
      IMAGE_MAP.put(destNameBlue, blueImage);


      System.out.println("RGB channels split and saved as " + destNameRed + ", " + destNameGreen
              + ", " + destNameBlue);
    }
  }

  private void createPPMContent(int width, int height, int[][][] rgbData) {
    StringBuilder content;
    content = new StringBuilder();
    content.append("P3\n");
    content.append(width).append(" ").append(height).append("\n");
    content.append("255\n");

    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        int r = rgbData[i][j][0];
        int g = rgbData[i][j][1];
        int b = rgbData[i][j][2];
        content.append(r).append(" ").append(g).append(" ").append(b).append(" ");
      }
      content.append("\n");
    }

  }


  /**
   * Extract a specific component from a source image and save it as a separate image.
   *
   * @param sourceName The name of the source image from which to extract the component.
   * @param destName   The name of the resulting image that will contain the extracted component.
   * @param component  The component to extract, which can be one of the following:
   *                   - "red": Extract the red channel.
   *                   - "green": Extract the green channel.
   *                   - "blue": Extract the blue channel.
   *                   - "luma": Convert the image to grayscale using luminance.
   *                   - "intensity": Convert the image to grayscale using intensity.
   *                   - "value": Extract the value (brightness) component of an image.
   */
  private void extractComponentHelper(String sourceName, String destName, String component, int splitPercentage) {
    ImageContent sourceImage = IMAGE_MAP.get(sourceName);
    boolean flag = true;

    if (sourceImage != null) {
      int[][][] sourceRGBData = IMAGE_MAP.get(sourceName).getRgbDataMap();

      if (sourceRGBData != null) {
        int height = sourceRGBData.length;
        int width = sourceRGBData[0].length;

        int[][][] extractedRGBData = new int[height][width][3];

        int splitPosition = width * splitPercentage / 100;

        for (int y = 0; y < height; y++) {
          for (int x = 0; x < width; x++) {
            int r = sourceRGBData[y][x][0];
            int g = sourceRGBData[y][x][1];
            int b = sourceRGBData[y][x][2];

            switch (component) {
              case "red":
                g = 0;
                b = 0;
                break;
              case "green":
                r = 0;
                b = 0;
                break;
              case "blue":
                r = 0;
                g = 0;
                break;
              case "luma":
                if (x <= splitPosition || splitPercentage == 0) {
                  int luma = (int) (0.2126 * r + 0.7152 * g + 0.0722 * b);
                  r = luma;
                  g = luma;
                  b = luma;
                }
                break;
              case "intensity":
                int intensity = (r + g + b) / 3;
                r = intensity;
                g = intensity;
                b = intensity;
                break;
              case "value":
                int value = Math.max(r, Math.max(g, b));
                r = value;
                g = value;
                b = value;
                break;
              default:
                flag = false;
                System.out.print("Invalid component parameter.");
            }
            extractedRGBData[y][x][0] = r;
            extractedRGBData[y][x][1] = g;
            extractedRGBData[y][x][2] = b;
          }
        }
        if (flag) {
          createPPMContent(width, height, extractedRGBData);

          ImageContent destImage = new ImageContent(destName, extractedRGBData);
          IMAGE_MAP.put(destName, destImage);
          System.out.print(component + " component image created from '" + sourceName
                  + "' and saved as '" + destName + "'");

        }
      } else {
        System.out.println("Failed to extract the " + component + " component; invalid RGB data.");
      }
    } else {
      System.out.println("Source image not found: " + sourceName);
    }
  }

  @Override
  public void extractComponent(String sourceName, String destName, String component, int splitPercentage) {
    extractComponentHelper(sourceName, destName, component, splitPercentage);
  }


  @Override
  public void extractComponent(String sourceName, String destName, String component) {
    extractComponentHelper(sourceName, destName, component, 0);
  }


  /**
   * Get a map of image names to their corresponding RGB data represented as a 3D integer array.
   *
   * @return A map where keys are image names and values are the corresponding RGB data.
   */
  @Override
  public int[][][] getRgbDataMap(String imageName) {
    return IMAGE_MAP.get(imageName).getRgbDataMap();
  }

  @Override
  public double[][] getPixels(String imageName) {
    return IMAGE_MAP.get(imageName).getPixels();
  }

  private void colorCorrectImageHelper(String sourceName, String destName, int splitPercentage) {
    ImageContent sourceImage = IMAGE_MAP.get(sourceName);

    if (sourceImage != null) {
      int[][][] sourceRGBData = IMAGE_MAP.get(sourceName).getRgbDataMap();

      int height = sourceRGBData.length;
      int width = sourceRGBData[0].length;

      int[][][] colorCorrectedImage = new int[height][width][3];

      Histogram histogram = new Histogram(10, 245);

      // Populate the histogram with values from the image data.
      for (int[][] sourceRGBDatum : sourceRGBData) {
        for (int x = 0; x < width; x++) {
          int redValue = sourceRGBDatum[x][0];
          int greenValue = sourceRGBDatum[x][1];
          int blueValue = sourceRGBDatum[x][2];
          histogram.addValue(redValue, greenValue, blueValue);
        }
      }

      // Calculate the max count across all channels.
      histogram.calculateMaxCount();

      // Find the peak values for each channel.
      int peakR = histogram.findPeakValue(histogram.histogramR);
      int peakG = histogram.findPeakValue(histogram.histogramG);
      int peakB = histogram.findPeakValue(histogram.histogramB);

      // Calculate the average value across peaks.
      int averagePeak = (peakR + peakG + peakB) / 3;

      System.out.println("Average Peak: " + averagePeak);

      int splitPosition = width * splitPercentage / 100;

      // Offset each channel's values so that their histogram peak occurs at the average value.
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          int redValue = sourceRGBData[y][x][0];
          int greenValue = sourceRGBData[y][x][1];
          int blueValue = sourceRGBData[y][x][2];

          if (splitPercentage == 0 || x <= splitPosition) {
            // Offset the values
            int offsetR = averagePeak - peakR;
            int offsetG = averagePeak - peakG;
            int offsetB = averagePeak - peakB;

            // Apply offsets and ensure values stay within the valid range (10 to 245)
            int correctedRed = Math.min(245, Math.max(10, redValue + offsetR));
            int correctedGreen = Math.min(245, Math.max(10, greenValue + offsetG));
            int correctedBlue = Math.min(245, Math.max(10, blueValue + offsetB));

            colorCorrectedImage[y][x][0] = correctedRed;
            colorCorrectedImage[y][x][1] = correctedGreen;
            colorCorrectedImage[y][x][2] = correctedBlue;
          } else {
            colorCorrectedImage[y][x][0] = redValue;
            colorCorrectedImage[y][x][1] = greenValue;
            colorCorrectedImage[y][x][2] = blueValue;
          }
        }
      }

      // Create a StringBuilder for the corrected image content.
      createPPMContent(width, height, colorCorrectedImage);

      // Create and store the corrected image.
      ImageContent correctedImage = new ImageContent(destName, colorCorrectedImage);
      IMAGE_MAP.put(destName, correctedImage);
      //rgbDataMap.put(destName, sourceRGBData);
      System.out.println("Color correction completed with " + splitPercentage + "% split. "
              + "Corrected " + "image saved as " + destName);
    } else {
      System.out.println("Source image not found: " + sourceName);
    }
  }


  /**
   * Color-correct a part of the image depending on the splitPercentage parameter passed by
   * aligning the meaningful peaks of its histogram. The final image will have a part of color
   * corrected image and a part of the original image
   *
   * @param sourceName      The name of the source image.
   * @param destName        The name of the destination color-corrected image.
   * @param splitPercentage The percentage of the image to be color corrected.
   */
  @Override
  public void colorCorrectImage(String sourceName, String destName, int splitPercentage) {
    colorCorrectImageHelper(sourceName, destName, splitPercentage);
  }

  /**
   * Color-correct the image by aligning the meaningful peaks of its histogram.
   *
   * @param sourceName The name of the source image.
   * @param destName   The name of the destination color-corrected image.
   */
  @Override
  public void colorCorrectImage(String sourceName, String destName) {
    colorCorrectImageHelper(sourceName, destName, 0);
  }


  /**
   * Create a histogram of the source image and save it as a separate image.
   *
   * @param sourceName The name of the source image.
   * @param destName   The name of the destination histogram image.
   */
  @Override
  public void createHistogram(String sourceName, String destName) {
    Histogram histogram = new Histogram(0, 255);
    int[][][] sourceRGBData = IMAGE_MAP.get(sourceName).getRgbDataMap();
    histogram.createHistogram(sourceRGBData);
    // Calculate the average value across peaks.
    BufferedImage histogramImage = histogram.createHistogramImage(256, 256);
    int width = histogramImage.getWidth();
    int height = histogramImage.getHeight();

    int[][][] imageRGBData = new int[height][width][3];

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int rgb = histogramImage.getRGB(x, y);
        imageRGBData[y][x][0] = (rgb >> 16) & 0xFF;
        imageRGBData[y][x][1] = (rgb >> 8) & 0xFF;
        imageRGBData[y][x][2] = rgb & 0xFF;
      }
    }
    ImageContent image = new ImageContent(destName, imageRGBData);
    IMAGE_MAP.put(destName, image);
    System.out.println("Histogram of the image saved as " + destName);
  }


  private void applyLevelsAdjustmentHelper(int shadowPoint, int midPoint, int highlightPoint,
                                           String sourceImageName, String destImageName,
                                           int splitPercentage) {


    if (shadowPoint < midPoint && midPoint < highlightPoint && shadowPoint >= 0 && shadowPoint <=
            255 && midPoint <= 255 && highlightPoint <= 255) {


      ImageContent sourceImage = IMAGE_MAP.get(sourceImageName);

      if (sourceImage != null) {
        int[][][] sourceRGBData = IMAGE_MAP.get(sourceImageName).getRgbDataMap();

        int width = sourceRGBData[0].length;
        int height = sourceRGBData.length;

        int[][][] adjustedRGBData = new int[height][width][3];

        int splitPosition = width * splitPercentage / 100;

        for (int y = 0; y < height; y++) {
          for (int x = 0; x < width; x++) {
            int redValue = sourceRGBData[y][x][0];
            int greenValue = sourceRGBData[y][x][1];
            int blueValue = sourceRGBData[y][x][2];

            if (splitPercentage == 0 || x <= splitPosition) {

              int adjustedRed = applyCurvesFunction(redValue, shadowPoint, midPoint, highlightPoint);
              int adjustedGreen = applyCurvesFunction(greenValue, shadowPoint, midPoint,
                      highlightPoint);
              int adjustedBlue = applyCurvesFunction(blueValue, shadowPoint, midPoint,
                      highlightPoint);

              adjustedRGBData[y][x][0] = adjustedRed;
              adjustedRGBData[y][x][1] = adjustedGreen;
              adjustedRGBData[y][x][2] = adjustedBlue;

            } else {

              adjustedRGBData[y][x][0] = redValue;
              adjustedRGBData[y][x][1] = greenValue;
              adjustedRGBData[y][x][2] = blueValue;
            }
          }
        }

        ImageContent adjustedImage = new ImageContent(destImageName, adjustedRGBData);
        IMAGE_MAP.put(destImageName, adjustedImage);

        System.out.println("Adjusted image with " + splitPercentage + "% split. Image saved as "
                + destImageName);
      } else {
        System.out.println("Source image not found: " + sourceImageName);
      }
    } else {
      System.out.println("Invalid shadow, mid, highlight points");
    }
  }

  /**
   * Levels-adjust a part of the image depending on the splitPercentage parameter passed.
   * The final image will have a part of level-adjusted image and a part of the original image
   *
   * @param shadowPoint     The shadow point.
   * @param midPoint        The mid-point.
   * @param highlightPoint  The highlight point.
   * @param sourceImageName The name of the source image.
   * @param destImageName   The name of the destination adjusted image.
   * @param splitPercentage The percentage of the image to apply the adjustment to.
   */
  @Override
  public void applyLevelsAdjustment(int shadowPoint, int midPoint, int highlightPoint, String
          sourceImageName, String destImageName, int splitPercentage) {
    applyLevelsAdjustmentHelper(shadowPoint, midPoint, highlightPoint, sourceImageName,
            destImageName, splitPercentage);
  }

  /**
   * Levels-adjust the image and saves it in the destination image passed.
   *
   * @param shadowPoint     The shadow point.
   * @param midPoint        The mid-point.
   * @param highlightPoint  The highlight point.
   * @param sourceImageName The name of the source image.
   * @param destImageName   The name of the destination adjusted image.
   */
  @Override
  public void applyLevelsAdjustment(int shadowPoint, int midPoint, int highlightPoint, String
          sourceImageName, String destImageName) {
    applyLevelsAdjustmentHelper(shadowPoint, midPoint, highlightPoint, sourceImageName,
            destImageName, 0);
  }

  private int applyCurvesFunction(int value, double shadowPoint, double midPoint, double
          highlightPoint) {
    double var = shadowPoint * shadowPoint * (midPoint - highlightPoint)
            - shadowPoint * (midPoint * midPoint - highlightPoint * highlightPoint)
            + midPoint * midPoint * highlightPoint
            - midPoint * highlightPoint * highlightPoint;

    double aA = -shadowPoint * (128 - 255) + 128 * highlightPoint - 255 * midPoint;
    double aB = shadowPoint * shadowPoint * (128 - 255) + 255 * midPoint * midPoint - 128
            * highlightPoint * highlightPoint;
    double aC = shadowPoint * shadowPoint * (255 * midPoint - 128 * highlightPoint) - shadowPoint
            * (255 * midPoint * midPoint - 128 * highlightPoint * highlightPoint);

    double a = aA / var;
    double b = aB / var;
    double c = aC / var;

    int adjustedValue = (int) (a * value * value + b * value + c);
    return Math.min(255, Math.max(0, adjustedValue));
  }


  /**
   * Convert the source image to grayscale using the specified transformation.
   *
   * @param sourceName      The name of the source image.
   * @param destName        The name of the destination grayscale image.
   * @param splitPercentage The percentage of the image to apply the transformation to.
   */
  @Override
  public void convertToGrayscale(String sourceName, String destName, int splitPercentage) {
    ImageContent sourceImage = IMAGE_MAP.get(sourceName);

    if (sourceImage != null) {
      int[][][] sourceRGBData = sourceImage.getRgbDataMap();
      int height = sourceRGBData.length;
      int width = sourceRGBData[0].length;

      int[][][] grayscalePixels = new int[height][width][3];

      // Grayscale transformation matrix
      double[][] grayscaleMatrix = {
              {0.2126, 0.7152, 0.0722},
              {0.2126, 0.7152, 0.0722},
              {0.2126, 0.7152, 0.0722}
      };

      int splitPosition = width * splitPercentage / 100;

      // Convert color to grayscale using the specified transformation with vertical split
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          int red = sourceRGBData[y][x][0];
          int green = sourceRGBData[y][x][1];
          int blue = sourceRGBData[y][x][2];

          if (splitPercentage == 0 || x <= splitPosition) {
            // Apply the specified transformation
            int grayscaleValue = (int) (grayscaleMatrix[0][0] * red + grayscaleMatrix[0][1] * green
                    + grayscaleMatrix[0][2] * blue);

            // Set the same grayscale value for all channels
            grayscalePixels[y][x][0] = grayscaleValue;
            grayscalePixels[y][x][1] = grayscaleValue;
            grayscalePixels[y][x][2] = grayscaleValue;

          } else {
            // Copy the original image data to the destination image for the other side
            grayscalePixels[y][x][0] = red;
            grayscalePixels[y][x][1] = green;
            grayscalePixels[y][x][2] = blue;
          }
        }
      }

      // Create a new ImageContent with the grayscale pixels
      ImageContent grayscaleImage = new ImageContent(destName, grayscalePixels);
      IMAGE_MAP.put(destName, grayscaleImage);

      // Store the grayscale image
      System.out.println("Grayscale image with " + splitPercentage + "% split saved as "
              + destName);
    } else {
      System.out.println("Source image not found: " + sourceName);
    }
  }

  /**
   * Compresses the specified image with the given compression percentage and saves the compressed
   * image with the provided destination name.
   *
   * @param imageName             The name of the source image to be compressed.
   * @param destName              The name to be assigned to the compressed image.
   * @param compressionPercentage The percentage of compression to be applied to the image.
   */
  public void compress(String imageName, String destName, double compressionPercentage) {
    int[][][] sourceRgb = IMAGE_MAP.get(imageName).getRgbDataMap();
    Compression compressedImage = new Compression();
    int[][][] imageRGBData = compressedImage.compress(sourceRgb, compressionPercentage);
    if (imageRGBData != null) {
      ImageContent correctedImage = new ImageContent(destName, imageRGBData);
      IMAGE_MAP.put(destName, correctedImage);
      System.out.println("Compress image with " + compressionPercentage + "% saved as " + destName);
    } else {
      System.out.println("Error in compressing " + imageName + " by " + compressionPercentage
              + " %");
    }

  }

}