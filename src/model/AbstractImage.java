package model;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;


/**
 * This abstract class defines a set of image manipulation operations and provides common
 * functionality for image processing. Subclass implements methods for loading and saving
 * images in specific formats.
 */
public abstract class AbstractImage implements ImageOperations {




  protected static final Map<String, ImageContent> imageMap = new HashMap<>();

  protected static final Map<String, int[][][]> rgbDataMap = new HashMap<>();


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
   * Load an image from a file and store it in the image map and RGB data map.
   *
   * @param imagePath The file path of the image to load.
   * @param imageName The name to associate with the loaded image.
   * @throws IOException If an error occurs while loading the image.
   */
  public abstract void loadImage(String imagePath, String imageName) throws IOException;

  /**
   * Save an image to a file using a specific format.
   *
   * @param imagePath The file path to save the image.
   * @param imageName The name of the image to be saved.
   * @throws IOException If an error occurs while saving the image.
   */
  public abstract void saveImage(String imagePath, String imageName) throws IOException;


  /**
   * Flip an image horizontally and save it as a new image with the given name.
   *
   * @param sourceImageName The name of the source image.
   * @param destImageName   The name of the destination image.
   */
  @Override
  public void horizontalFlipImage(String sourceImageName, String destImageName) {
    ImageContent sourceImage = imageMap.get(sourceImageName);

    if (sourceImage != null) {
      int[][][] sourceRGBData = rgbDataMap.get(sourceImageName);

      if (sourceRGBData != null) {
        int width = sourceRGBData[0].length;
        int height = sourceRGBData.length;

        int[][][] flippedRGBData = new int[height][width][3];

        for (int y = 0; y < height; y++) {
          for (int x = 0; x < width; x++) {
            flippedRGBData[y][x] = sourceRGBData[y][width - x - 1];
          }
        }

        StringBuilder flippedContent = createPPMContent(width, height, flippedRGBData);

        ImageContent flippedImage = new ImageContent(destImageName, flippedContent.toString());
        imageMap.put(destImageName, flippedImage);

        rgbDataMap.put(destImageName, flippedRGBData);

        System.out.println("Image '" + sourceImageName + "' flipped horizontally and saved as '"
                + destImageName + "'.");
      } else {
        System.out.println("Failed to flip the source image; invalid RGB data.");
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
    ImageContent sourceImage = imageMap.get(sourceImageName);
    if (sourceImage == null) {
      System.out.println("Source image not found: " + sourceImageName);
    }

    int[][][] sourceRGBData = rgbDataMap.get(sourceImageName);

    int height = sourceRGBData.length;
    int width = sourceRGBData[0].length;
    int[][][] flippedRGBData = new int[height][width][3];

    for (int y = 0; y < height; y++) {
      int newY = height - 1 - y;
      flippedRGBData[newY] = sourceRGBData[y];
    }

    StringBuilder flippedContent = createPPMContent(width, height, flippedRGBData);

    ImageContent flippedImage = new ImageContent(destImageName, flippedContent.toString());
    imageMap.put(destImageName, flippedImage);
    rgbDataMap.put(destImageName, flippedRGBData);

    System.out.println("Vertical flip completed. Flipped image saved as " + destImageName);
  }


  /**
   * Applies a sharpening kernel to the source image, creating a sharpened image with the given
   * name.
   *
   * @param sourceImageName The name of the source image.
   * @param destImageName   The name of the destination sharpened image.
   */
  @Override
  public void sharpenImage(String sourceImageName, String destImageName, int splitPercentage) {
    ImageContent sourceImage = imageMap.get(sourceImageName);
    if (sourceImage == null) {
      System.out.println("Source image not found: " + sourceImageName);
      return;
    }

    int[][][] sourceRGBData = rgbDataMap.get(sourceImageName);

    int height = sourceRGBData.length;
    int width = sourceRGBData[0].length;
    int[][][] sharpenedRGBData = new int[height][width][3];

    int splitPosition = width * splitPercentage / 100;

    for (int y = 2; y < height - 2; y++) {
      for (int x = 2; x < width - 2; x++) {
        for (int channel = 0; channel < 3; channel++) {
          float sum = 0.0f;
          for (int ky = -2; ky <= 2; ky++) {
            for (int kx = -2; kx <= 2; kx++) {
              int pixelX = Math.min(width - 1, Math.max(0, x + kx));
              int pixelY = Math.min(height - 1, Math.max(0, y + ky));
              float kernelValue = sharpeningKernel[(ky + 2) * 5 + (kx + 2)];
              int pixelValue = sourceRGBData[pixelY][pixelX][channel];
              sum += kernelValue * pixelValue;
            }
          }
          int newValue = Math.min(255, Math.max(0, (int) sum));
          if (splitPercentage == 0 || x < splitPosition) {
            sharpenedRGBData[y][x][channel] = newValue;
          } else {
            // Copy the original image data to the destination image for the other side
            sharpenedRGBData[y][x][channel] = sourceRGBData[y][x][channel];
          }
        }
      }
    }

    StringBuilder sharpenedContent = createPPMContent(width, height, sharpenedRGBData);

    ImageContent sharpenedImage = new ImageContent(destImageName, sharpenedContent.toString());
    imageMap.put(destImageName, sharpenedImage);
    rgbDataMap.put(destImageName, sharpenedRGBData);

    System.out.println("Image sharpening by " + splitPercentage + "% completed. Sharpened image saved as " + destImageName);
  }



  /**
   * Applies a blurring effect to the source image, creating a blurred version with the given name.
   * This method uses a Gaussian blurring kernel.
   *
   * @param sourceImageName The name of the source image.
   * @param destImageName   The name of the destination blurred image.
   */
  @Override
  public void blurImage(String sourceImageName, String destImageName, int splitPercentage) {
    ImageContent sourceImage = imageMap.get(sourceImageName);
    if (sourceImage == null) {
      System.out.println("Source image not found: " + sourceImageName);
      return;
    }

    int[][][] sourceRGBData = rgbDataMap.get(sourceImageName);

    int height = sourceRGBData.length;
    int width = sourceRGBData[0].length;
    int[][][] blurredRGBData = new int[height][width][3];

    int splitPosition = width * splitPercentage / 100;

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        for (int channel = 0; channel < 3; channel++) {
          float sum = 0.0f;
          int kernelIndex = 0;
          for (int ky = -1; ky <= 1; ky++) {
            for (int kx = -1; kx <= 1; kx++) {
              int pixelX = Math.min(width - 1, Math.max(0, x + kx));
              int pixelY = Math.min(height - 1, Math.max(0, y + ky));
              float kernelValue = gaussianKernel[kernelIndex];
              int pixelValue = sourceRGBData[pixelY][pixelX][channel];
              sum += kernelValue * pixelValue;
              kernelIndex++;
            }
          }
          int newValue = Math.min(255, Math.max(0, (int) sum));
          if (splitPercentage == 0 || x < splitPosition) {
            blurredRGBData[y][x][channel] = newValue;
          } else {
            // Copy the original image data to the destination image for the other side
            blurredRGBData[y][x][channel] = sourceRGBData[y][x][channel];
          }
        }
      }
    }

    StringBuilder blurredContent = createPPMContent(width, height, blurredRGBData);

    ImageContent blurredImage = new ImageContent(destImageName, blurredContent.toString());
    imageMap.put(destImageName, blurredImage);
    rgbDataMap.put(destImageName, blurredRGBData);

    System.out.println("Image blurring by " + splitPercentage +  "% completed. Blurred image saved as " + destImageName);
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
    ImageContent sourceImage = imageMap.get(sourceImageName);
    if (sourceImage == null) {
      System.out.println("Source image not found: " + sourceImageName);
    }

    int[][][] sourceRGBData = rgbDataMap.get(sourceImageName);

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

    StringBuilder brightenedContent = createPPMContent(width, height, brightenedRGBData);

    ImageContent brightenedImage = new ImageContent(destImageName, brightenedContent.toString());
    imageMap.put(destImageName, brightenedImage);
    rgbDataMap.put(destImageName, brightenedRGBData);

    System.out.println("Image brightening completed. Brightened image saved as " + destImageName);
  }


  /**
   * Applies a sepia filter to the source image and save the sepia-toned image with the given name.
   *
   * @param sourceName The name of the source image.
   * @param destName   The name of the destination sepia-toned image.
   */
  @Override
  public void sepiaImage(String sourceName, String destName, int splitPercentage) {
    ImageContent sourceImage = imageMap.get(sourceName);
    if (sourceImage == null) {
      System.out.println("Source image not found: " + sourceName);
      return;
    }

    int[][][] sourceRGBData = rgbDataMap.get(sourceName);

    int height = sourceRGBData.length;
    int width = sourceRGBData[0].length;
    int[][][] sepiaRGBData = new int[height][width][3];

    int splitPosition = width * splitPercentage / 100;

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int r = sourceRGBData[y][x][0];
        int g = sourceRGBData[y][x][1];
        int b = sourceRGBData[y][x][2];

        int tr = (int) (0.393 * r + 0.769 * g + 0.189 * b);
        int tg = (int) (0.349 * r + 0.686 * g + 0.168 * b);
        int tb = (int) (0.272 * r + 0.534 * g + 0.131 * b);

        tr = Math.min(255, Math.max(0, tr));
        tg = Math.min(255, Math.max(0, tg));
        tb = Math.min(255, Math.max(0, tb));

        if (x >= splitPosition) {
          sepiaRGBData[y][x][0] = tr;
          sepiaRGBData[y][x][1] = tg;
          sepiaRGBData[y][x][2] = tb;
        } else {
          // Copy the original image data to the destination image for the other side
          sepiaRGBData[y][x][0] = sourceRGBData[y][x][0];
          sepiaRGBData[y][x][1] = sourceRGBData[y][x][1];
          sepiaRGBData[y][x][2] = sourceRGBData[y][x][2];
        }
      }
    }

    StringBuilder sepiaContent = createPPMContent(width, height, sepiaRGBData);

    ImageContent sepiaImage = new ImageContent(destName, sepiaContent.toString());
    imageMap.put(destName, sepiaImage);
    rgbDataMap.put(destName, sepiaRGBData);

    System.out.println("Sepia filter applied with " + splitPercentage + "% split. Sepia-toned image saved as " + destName);
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
    ImageContent redImage = imageMap.get(redName);
    ImageContent greenImage = imageMap.get(greenName);
    ImageContent blueImage = imageMap.get(blueName);

    if (redImage == null || greenImage == null || blueImage == null) {
      System.out.print("One or more source images not found.");

    } else {

      int[][][] redRGBData = rgbDataMap.get(redName);
      int[][][] greenRGBData = rgbDataMap.get(greenName);
      int[][][] blueRGBData = rgbDataMap.get(blueName);

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

      StringBuilder combinedContent = createPPMContent(width, height, combinedRGBData);

      ImageContent combinedImage = new ImageContent(combinedName, combinedContent.toString());
      imageMap.put(combinedName, combinedImage);
      rgbDataMap.put(combinedName, combinedRGBData);

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
    ImageContent sourceImage = imageMap.get(sourceName);
    if (sourceImage == null) {
      System.out.println("Source image not found: " + sourceName);
    } else {

      int[][][] sourceRGBData = rgbDataMap.get(sourceName);

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

      StringBuilder redContent = createPPMContent(width, height, redRGBData);
      StringBuilder greenContent = createPPMContent(width, height, greenRGBData);
      StringBuilder blueContent = createPPMContent(width, height, blueRGBData);

      ImageContent redImage = new ImageContent(destNameRed, redContent.toString());
      ImageContent greenImage = new ImageContent(destNameGreen, greenContent.toString());
      ImageContent blueImage = new ImageContent(destNameBlue, blueContent.toString());

      imageMap.put(destNameRed, redImage);
      imageMap.put(destNameGreen, greenImage);
      imageMap.put(destNameBlue, blueImage);

      rgbDataMap.put(destNameRed, redRGBData);
      rgbDataMap.put(destNameGreen, greenRGBData);
      rgbDataMap.put(destNameBlue, blueRGBData);

      System.out.println("RGB channels split and saved as " + destNameRed + ", " + destNameGreen
              + ", " + destNameBlue);
    }
  }

  private StringBuilder createPPMContent(int width, int height, int[][][] rgbData) {
    StringBuilder content = new StringBuilder();
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

    return content;
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
  @Override
  public void extractComponent(String sourceName, String destName, String component) {
    ImageContent sourceImage = imageMap.get(sourceName);
    boolean flag = true;

    if (sourceImage != null) {
      int[][][] sourceRGBData = rgbDataMap.get(sourceName);

      if (sourceRGBData != null) {
        int height = sourceRGBData.length;
        int width = sourceRGBData[0].length;

        int[][][] extractedRGBData = new int[height][width][3];

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
                int luma = (int) (0.2126 * r + 0.7152 * g + 0.0722 * b);
                r = luma;
                g = luma;
                b = luma;
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
          StringBuilder extractedContent = createPPMContent(width, height, extractedRGBData);

          ImageContent destImage = new ImageContent(destName, extractedContent.toString());
          imageMap.put(destName, destImage);
          rgbDataMap.put(destName, extractedRGBData);
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

  /**
   * Get a map of image names to their corresponding ImageContent objects.
   *
   * @return A map where keys are image names and values are the corresponding ImageContent objects.
   */
  public Map<String, ImageContent> getImageMap() {
    return imageMap;
  }


  /**
   * Get a map of image names to their corresponding RGB data represented as a 3D integer array.
   *
   * @return A map where keys are image names and values are the corresponding RGB data.
   */
  public Map<String, int[][][]> getRgbDataMap() {
    return rgbDataMap;
  }


  /**
   * Color-correct the image by aligning the meaningful peaks of its histogram.
   *
   * @param sourceName The name of the source image.
   * @param destName   The name of the destination color-corrected image.
   */
  @Override
  public void colorCorrectImage(String sourceName, String destName) {
    ImageContent sourceImage = imageMap.get(sourceName);

    if (sourceImage != null) {
      int[][][] sourceRGBData = rgbDataMap.get(sourceName);

      int height = sourceRGBData.length;
      int width = sourceRGBData[0].length;


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

      // Offset each channel's values so that their histogram peak occurs at the average value.
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          int redValue = sourceRGBData[y][x][0];
          int greenValue = sourceRGBData[y][x][1];
          int blueValue = sourceRGBData[y][x][2];

          // Offset the values
          int offsetR = averagePeak - peakR;
          int offsetG = averagePeak - peakG;
          int offsetB = averagePeak - peakB;

          // Apply offsets and ensure values stay within the valid range (10 to 245)
          int correctedRed = Math.min(245, Math.max(10, redValue + offsetR));
          int correctedGreen = Math.min(245, Math.max(10, greenValue + offsetG));
          int correctedBlue = Math.min(245, Math.max(10, blueValue + offsetB));

          sourceRGBData[y][x][0] = correctedRed;
          sourceRGBData[y][x][1] = correctedGreen;
          sourceRGBData[y][x][2] = correctedBlue;
        }
      }

      // Create a StringBuilder for the corrected image content.
      StringBuilder correctedContent = createPPMContent(width, height, sourceRGBData);

      // Create and store the corrected image.
      ImageContent correctedImage = new ImageContent(destName, correctedContent.toString());
      imageMap.put(destName, correctedImage);
      rgbDataMap.put(destName, sourceRGBData);
      System.out.println("Color correction completed. Corrected image saved as " + destName);
    } else {
      System.out.println("Source image not found: " + sourceName);
    }
  }

  public void createHistogram(String sourceName, String destName) {
    Histogram histogram = new Histogram(10, 245);
    int[][][] sourceRGBData = rgbDataMap.get(sourceName);
    int height = sourceRGBData.length;
    int width = sourceRGBData[0].length;
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {

        int redValue = sourceRGBData[y][x][0];
        int greenValue = sourceRGBData[y][x][1];
        int blueValue = sourceRGBData[y][x][2];
        histogram.addValue(redValue, greenValue, blueValue);

      }
    }
    histogram.calculateMaxCount();

    // Calculate the average value across peaks.
    BufferedImage histogramImage = histogram.createHistogramImage(256, 256);
    width = histogramImage.getWidth();
    height = histogramImage.getHeight();

    int[][][] imageRGBData = new int[height][width][3];

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int rgb = histogramImage.getRGB(x, y);
        imageRGBData[y][x][0] = (rgb >> 16) & 0xFF;
        imageRGBData[y][x][1] = (rgb >> 8) & 0xFF;
        imageRGBData[y][x][2] = rgb & 0xFF;
      }
    }
    ImageContent image = new ImageContent(destName, serializeImageData(imageRGBData));
    imageMap.put(destName, image);
    rgbDataMap.put(destName, imageRGBData);
    System.out.println("Histogram of the image saved as " + destName);
  }

  private static String serializeImageData(int[][][] imageData) {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    for (int[][] imageDatum : imageData) {
      for (int[] ints : imageDatum) {
        int rgb = (ints[0] << 16) | (ints[1] << 8) | ints[2];
        outputStream.write((rgb >> 16) & 0xFF);
        outputStream.write((rgb >> 8) & 0xFF);
        outputStream.write(rgb & 0xFF);
      }
    }
    byte[] imageBytes = outputStream.toByteArray();
    return Base64.getEncoder().encodeToString(imageBytes);
  }

  @Override
  public void applyLevelsAdjustment(int shadowPoint, int midPoint, int highlightPoint, String sourceImageName, String destImageName) {
    ImageContent sourceImage = imageMap.get(sourceImageName);

    if (sourceImage != null) {
      int[][][] sourceRGBData = rgbDataMap.get(sourceImageName);

      int height = sourceRGBData.length;
      int width = sourceRGBData[0].length;

      double scaleR = 255.0 / (highlightPoint - shadowPoint);
      double scaleG = 255.0 / (highlightPoint - shadowPoint);
      double scaleB = 255.0 / (highlightPoint - shadowPoint);

      // Apply the levels adjustment to each channel
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          int redValue = sourceRGBData[y][x][0];
          int greenValue = sourceRGBData[y][x][1];
          int blueValue = sourceRGBData[y][x][2];

          int adjustedRed = (int) (scaleR * (redValue - shadowPoint));
          int adjustedGreen = (int) (scaleG * (greenValue - shadowPoint));
          int adjustedBlue = (int) (scaleB * (blueValue - shadowPoint));

          // Ensure adjusted values stay within the valid range (0 to 255)
          adjustedRed = Math.min(255, Math.max(0, adjustedRed));
          adjustedGreen = Math.min(255, Math.max(0, adjustedGreen));
          adjustedBlue = Math.min(255, Math.max(0, adjustedBlue));

          sourceRGBData[y][x][0] = adjustedRed;
          sourceRGBData[y][x][1] = adjustedGreen;
          sourceRGBData[y][x][2] = adjustedBlue;
        }
      }

      // Create a StringBuilder for the adjusted image content.
      StringBuilder adjustedContent = createPPMContent(width, height, sourceRGBData);

      // Create and store the adjusted image.
      ImageContent adjustedImage = new ImageContent(destImageName, adjustedContent.toString());
      imageMap.put(destImageName, adjustedImage);
      rgbDataMap.put(destImageName, sourceRGBData);
      System.out.println("Levels adjustment completed. Adjusted image saved as " + destImageName);
    } else {
      System.out.println("Source image not found: " + sourceImageName);
    }
  }


}
