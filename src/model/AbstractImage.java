package model;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


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
  public void sharpenImage(String sourceImageName, String destImageName) {
    ImageContent sourceImage = imageMap.get(sourceImageName);
    if (sourceImage == null) {
      System.out.println("Source image not found: " + sourceImageName);

    }
    int[][][] sourceRGBData = rgbDataMap.get(sourceImageName);

    int height = sourceRGBData.length;
    int width = sourceRGBData[0].length;
    int[][][] sharpenedRGBData = new int[height][width][3];

    for (int y = 2; y < height - 2; y++) {
      for (int x = 2; x < width - 2; x++) {
        for (int channel = 0; channel < 3; channel++) {
          float sum = 0.0f;
          for (int ky = -2; ky <= 2; ky++) {
            for (int kx = -2; kx <= 2; kx++) {
              float kernelValue = sharpeningKernel[(ky + 2) * 5 + (kx + 2)];
              int pixelValue = sourceRGBData[y + ky][x + kx][channel];
              sum += kernelValue * pixelValue;
            }
          }
          int newValue = Math.min(255, Math.max(0, (int) sum));
          sharpenedRGBData[y][x][channel] = newValue;
        }
      }
    }

    StringBuilder sharpenedContent = createPPMContent(width, height, sharpenedRGBData);

    ImageContent sharpenedImage = new ImageContent(destImageName, sharpenedContent.toString());
    imageMap.put(destImageName, sharpenedImage);
    rgbDataMap.put(destImageName, sharpenedRGBData);

    System.out.println("Image sharpening completed. Sharpened image saved as " + destImageName);
  }


  /**
   * Applies a blurring effect to the source image, creating a blurred version with the given name.
   * This method uses a Gaussian blurring kernel.
   *
   * @param sourceImageName The name of the source image.
   * @param destImageName   The name of the destination blurred image.
   */
  @Override
  public void blurImage(String sourceImageName, String destImageName) {
    ImageContent sourceImage = imageMap.get(sourceImageName);
    if (sourceImage == null) {
      System.out.println("Source image not found: " + sourceImageName);

    }

    int[][][] sourceRGBData = rgbDataMap.get(sourceImageName);

    int height = sourceRGBData.length;
    int width = sourceRGBData[0].length;
    int[][][] blurredRGBData = new int[height][width][3];

    for (int y = 1; y < height - 1; y++) {
      for (int x = 1; x < width - 1; x++) {
        for (int channel = 0; channel < 3; channel++) {
          float sum = 0.0f;
          int kernelIndex = 0;
          for (int ky = -1; ky <= 1; ky++) {
            for (int kx = -1; kx <= 1; kx++) {
              float kernelValue = gaussianKernel[kernelIndex];
              int pixelValue = sourceRGBData[y + ky][x + kx][channel];
              sum += kernelValue * pixelValue;
              kernelIndex++;
            }
          }
          int newValue = Math.min(255, Math.max(0, (int) sum));
          blurredRGBData[y][x][channel] = newValue;
        }
      }
    }

    StringBuilder blurredContent = createPPMContent(width, height, blurredRGBData);

    ImageContent blurredImage = new ImageContent(destImageName, blurredContent.toString());
    imageMap.put(destImageName, blurredImage);
    rgbDataMap.put(destImageName, blurredRGBData);

    System.out.println("Image blurring completed. Blurred image saved as " + destImageName);
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
  public void sepiaImage(String sourceName, String destName) {
    ImageContent sourceImage = imageMap.get(sourceName);
    if (sourceImage == null) {
      System.out.println("Source image not found: " + sourceName);
    }

    int[][][] sourceRGBData = rgbDataMap.get(sourceName);

    int height = sourceRGBData.length;
    int width = sourceRGBData[0].length;
    int[][][] sepiaRGBData = new int[height][width][3];

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

        sepiaRGBData[y][x][0] = tr;
        sepiaRGBData[y][x][1] = tg;
        sepiaRGBData[y][x][2] = tb;
      }
    }

    StringBuilder sepiaContent = createPPMContent(width, height, sepiaRGBData);

    ImageContent sepiaImage = new ImageContent(destName, sepiaContent.toString());
    imageMap.put(destName, sepiaImage);
    rgbDataMap.put(destName, sepiaRGBData);

    System.out.println("Sepia filter applied. Sepia-toned image saved as " + destName);
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

  public void calculatePixels(String imageName) {
    if (imageName != null && rgbDataMap.containsKey(imageName)) {
      int[][][] imageRGBData = rgbDataMap.get(imageName);
      int imageWidth = imageRGBData.length;
      int imageHeight = imageRGBData[0].length;

      double[][] pixels = new double[imageWidth][imageHeight];

      for (int i = 0; i < imageWidth; i++) {
        for (int j = 0; j < imageHeight; j++) {
          // Calculate the pixel value for (i, j) and store it in the pixels array
          pixels[i][j] = calculatePixelValue(imageRGBData[i][j]);
        }
      }
      imageMap.get(imageName).setPixels(pixels);
    } else {
      System.out.println("Image not found or RGB data missing.");
    }
  }

  public double calculatePixelValue(int[] rgb) {
    // You can choose any method to calculate the pixel value based on the RGB values.
    // For example, you can calculate the grayscale pixel value by averaging the RGB values.
    int red = rgb[0];
    int green = rgb[1];
    int blue = rgb[2];

    // Calculate the average of the RGB values (you can use different formulas if needed)
    int average = (red + green + blue) / 3;

    // Normalize the value as needed (e.g., divide by 255 for a range of 0 to 1)
    double normalizedValue = average / 255.0;

    return normalizedValue;
  }


  public void compress(String imageName, double compressionPercentage, int maxValue) {
    if (rgbDataMap.containsKey(imageName)) {
      int[][][] imageRGBData = rgbDataMap.get(imageName);

      // Calculate the compression threshold based on the percentage
      double compressionThreshold = calculateCompressionThreshold(compressionPercentage, maxValue);

      // Rest of your compression code...
      // Step 1: Perform Haar Wavelet Transform
      double[][][] transformedData = performHaarWaveletTransform(imageRGBData);

      // Step 2: Apply compression by setting small values to zero
      applyCompression(transformedData, compressionThreshold);

      // Step 3: Perform Inverse Haar Transform
      int[][][] compressedRGBData = performInverseHaarWaveletTransform(transformedData);

      // Update the RGB data map with the compressed data
      rgbDataMap.put(imageName, compressedRGBData);

      // Recalculate and update the pixels (if necessary)
      calculatePixels(imageName);

      System.out.println("Image " + imageName + " compressed with " + compressionPercentage + "% compression.");
    } else {
      System.out.println("Image not found or RGB data missing.");
    }
  }

  // Calculate the compression threshold based on the percentage
  private double calculateCompressionThreshold(double compressionPercentage, int maxValue) {
    if (compressionPercentage < 0) {
      compressionPercentage = 0;
    } else if (compressionPercentage > 100) {
      compressionPercentage = 100;
    }

    // Calculate the threshold as a percentage of the maximum value
    return compressionPercentage / 100.0 * maxValue;
  }

  private void applyCompression(double[][][] transformedData, double compressionThreshold) {
    int width = transformedData.length;
    int height = transformedData[0].length;
    int channels = transformedData[0][0].length;

    // Adjust the threshold by multiplying it by a factor (e.g., 2.0)
    double adjustedThreshold = compressionThreshold * 2.0;

    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        for (int c = 0; c < channels; c++) {
          if (Math.abs(transformedData[x][y][c]) < adjustedThreshold) {
            // Set small values to zero
            transformedData[x][y][c] = 0.0;
          }
        }
      }
    }
  }

  public double[][][] performHaarWaveletTransform(int[][][] channelData) {
    //System.out.println("channel"+ Arrays.toString(channelData[0]));
    int width = channelData[0].length;
    int height = channelData.length;
    double[][][] transformedData = new double[height][width][3];

    for (int channel = 0; channel < 3; channel++) {
      // Horizontal Haar Wavelet Transform
      for (int i = 0; i < height; i++) {
        //System.out.println("row " + i);
        int[] row = channelData[i][channel];
        //System.out.println("channelData[i][channel] " + Arrays.toString(row)); // Print the entire row
        double[] transformedRow = applyHaarWaveletTransform(row);
        //System.out.println("transformedRow " + Arrays.toString(transformedRow)); // Print the entire transformedRow
        for (int j = 0; j < width; j++) {
          //System.out.println("j " + j + " w "+ width);
          //System.out.println("transformedRow[j] " + transformedRow.length);
          transformedData[i][j][channel] = transformedRow[channel];
        }
      }

      // Vertical Haar Wavelet Transform
      for (int j = 0; j < width; j++) {
        int[] column = new int[height];
        for (int i = 0; i < height; i++) {
          column[i] = channelData[i][j][channel];
        }
        double[] transformedColumn = applyHaarWaveletTransform(column);
        for (int i = 0; i < height; i++) {
          transformedData[i][j][channel] = transformedColumn[i];
        }
      }
    }

    return transformedData;
  }

  private double[] applyHaarWaveletTransform(int[] data) {
    int n = data.length;
    double[] result = new double[n]; // Make sure the result has the same length as the input data

    int half = n / 2;

    // Calculate the averages and differences
    for (int i = 0; i < half; i++) {
      int sum = data[2 * i] + data[2 * i + 1];
      int diff = data[2 * i] - data[2 * i + 1];
      result[i] = (sum + diff) / 2.0; // Store the average
      result[i + half] = (sum - diff) / 2.0; // Store the difference
    }

    return result;
  }

  private int[][][] performInverseHaarWaveletTransform(double[][][] transformedData) {
    int width = transformedData.length;
    int height = transformedData[0].length;

    int[][][] result = new int[width][height][3]; // Assuming it's a color image

    for (int channel = 0; channel < 3; channel++) {
      // Perform inverse Haar Wavelet Transform for each color channel
      for (int i = 0; i < width; i++) {
        for (int j = 0; j < height / 2; j++) {
          double avg = (transformedData[i][2 * j][channel] + transformedData[i][2 * j + 1][channel]) / Math.sqrt(2);
          double diff = (transformedData[i][2 * j][channel] - transformedData[i][2 * j + 1][channel]) / Math.sqrt(2);
          result[i][j][channel] = (int) avg;
          result[i][j + height / 2][channel] = (int) diff;
        }
      }
    }

    return result;
  }










}