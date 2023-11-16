package model;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.imageio.ImageIO;


/**
 * This abstract class defines a set of image manipulation operations and provides common
 * functionality for image processing. Subclass implements methods for loading and saving
 * images in specific formats.
 */
public abstract class AbstractImage implements ImageOperations {


  protected static final Map<String, ImageContent> imageMap = new HashMap<>();

  // protected static final Map<String, int[][][]> rgbDataMap = new HashMap<>();


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
      int[][][] sourceRGBData = imageMap.get(sourceImageName).getRgbDataMap();

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

        ImageContent flippedImage = new ImageContent(destImageName, flippedRGBData);
        imageMap.put(destImageName, flippedImage);

        //rgbDataMap.put(destImageName, flippedRGBData);

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

    int[][][] sourceRGBData = imageMap.get(sourceImageName).getRgbDataMap();

    int height = sourceRGBData.length;
    int width = sourceRGBData[0].length;
    int[][][] flippedRGBData = new int[height][width][3];

    for (int y = 0; y < height; y++) {
      int newY = height - 1 - y;
      flippedRGBData[newY] = sourceRGBData[y];
    }

    StringBuilder flippedContent = createPPMContent(width, height, flippedRGBData);

    ImageContent flippedImage = new ImageContent(destImageName, flippedRGBData);
    imageMap.put(destImageName, flippedImage);
    //rgbDataMap.put(destImageName, flippedRGBData);

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

    int[][][] sourceRGBData = imageMap.get(sourceImageName).getRgbDataMap();

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

    ImageContent sharpenedImage = new ImageContent(destImageName, sharpenedRGBData);
    imageMap.put(destImageName, sharpenedImage);
    //rgbDataMap.put(destImageName, sharpenedRGBData);

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

    int[][][] sourceRGBData = imageMap.get(sourceImageName).getRgbDataMap();

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

    ImageContent blurredImage = new ImageContent(destImageName, blurredRGBData);
    imageMap.put(destImageName, blurredImage);
    // rgbDataMap.put(destImageName, blurredRGBData);

    System.out.println("Image blurring by " + splitPercentage + "% completed. Blurred image saved as " + destImageName);
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

    int[][][] sourceRGBData = imageMap.get(sourceImageName).getRgbDataMap();

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

    ImageContent brightenedImage = new ImageContent(destImageName, brightenedRGBData);
    imageMap.put(destImageName, brightenedImage);
    //rgbDataMap.put(destImageName, brightenedRGBData);

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

    int[][][] sourceRGBData = imageMap.get(sourceName).getRgbDataMap();

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

    ImageContent sepiaImage = new ImageContent(destName, sepiaRGBData);
    imageMap.put(destName, sepiaImage);
    //rgbDataMap.put(destName, sepiaRGBData);

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

      int[][][] redRGBData = imageMap.get(redName).getRgbDataMap();
      int[][][] greenRGBData = imageMap.get(greenName).getRgbDataMap();
      int[][][] blueRGBData = imageMap.get(blueName).getRgbDataMap();

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

      ImageContent combinedImage = new ImageContent(combinedName, combinedRGBData);
      imageMap.put(combinedName, combinedImage);
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
    ImageContent sourceImage = imageMap.get(sourceName);
    if (sourceImage == null) {
      System.out.println("Source image not found: " + sourceName);
    } else {

      int[][][] sourceRGBData = imageMap.get(sourceName).getRgbDataMap();

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

      ImageContent redImage = new ImageContent(destNameRed, redRGBData);
      ImageContent greenImage = new ImageContent(destNameGreen, greenRGBData);
      ImageContent blueImage = new ImageContent(destNameBlue, blueRGBData);

      imageMap.put(destNameRed, redImage);
      imageMap.put(destNameGreen, greenImage);
      imageMap.put(destNameBlue, blueImage);

     /* rgbDataMap.put(destNameRed, redRGBData);
      rgbDataMap.put(destNameGreen, greenRGBData);
      rgbDataMap.put(destNameBlue, blueRGBData);*/

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
      int[][][] sourceRGBData = imageMap.get(sourceName).getRgbDataMap();

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

          ImageContent destImage = new ImageContent(destName, extractedRGBData);
          imageMap.put(destName, destImage);
          //rgbDataMap.put(destName, extractedRGBData);
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
  public int[][][] getRgbDataMap(String imageName) {
    return imageMap.get(imageName).getRgbDataMap();
  }
/*


  double[][] redTrans;
  double[][] greenTrans;
  double[][] blueTrans;
  double[][] paddedRed;
  double[][] paddedGreen;
  double[][] paddedBlue;
  int[][] initialArray;
  double threshold;
  Set<Double> uniqueValues = new HashSet<Double>();

  BufferedImage img;
  BufferedImage out;

  int[][] outputImage;
  public void padImages(int[][][] imageRGBData) {
    int height = imageRGBData.length;
    int width = imageRGBData[0].length;

    double[][] redArr = new double[height][width];
    double[][] greenArr = new double[height][width];
    double[][] blueArr = new double[height][width];

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        redArr[y][x] = imageRGBData[y][x][0];
        greenArr[y][x] = imageRGBData[y][x][1];
        blueArr[y][x] = imageRGBData[y][x][2];
      }
    }

    paddedRed = padToPowerOfTwo(redArr);
    paddedGreen = padToPowerOfTwo(greenArr);
    paddedBlue = padToPowerOfTwo(blueArr);
    initialArray=new int[paddedRed.length][paddedRed[0].length];

     redTrans=new double[paddedRed.length][paddedRed[0].length];
    greenTrans=new double[paddedGreen.length][paddedGreen[0].length];
    blueTrans=new double[paddedBlue.length][paddedBlue[0].length];

  }
  private double[][] padToPowerOfTwo(double[][] matrix) {
    int originalRows = matrix.length;
    int originalCols = matrix[0].length;
    int lengthTobe = Math.max(originalRows, originalCols);

    int newRows = nextPowerOfTwo(lengthTobe);
    int newCols = newRows;


    // If the matrix dimensions are already powers of 2, no padding is needed
    if (newRows == originalRows && newCols == originalCols) {
      return copy2DArray(matrix);
    }



    // Pad the rows
    double[][] paddedMatrix = new double[newRows][newCols];
    for (int i = 0; i < originalRows; i++) {
      System.arraycopy(matrix[i], 0, paddedMatrix[i], 0, originalCols);
    }
    return paddedMatrix;
  }
  private int nextPowerOfTwo(int n) {
    int newSize = 1;
    while (newSize < n) {
      newSize <<= 1;
    }
    return newSize;
  }

  private double[][] copy2DArray(double[][] original) {
    int rows = original.length;
    int cols = original[0].length;
    double[][] copy = new double[rows][cols];
    for (int i = 0; i < rows; i++) {
      System.arraycopy(original[i], 0, copy[i], 0, cols);
    }
    return copy;
  }

  public void logic(){
    redTrans = haar2D(paddedRed);
    greenTrans = haar2D(paddedGreen);
    blueTrans = haar2D(paddedBlue);

    //
    calculateThreshold(50);
    resetValuesBelowThreshold();

    redTrans = inverseHaar2D(redTrans);
    greenTrans = inverseHaar2D(greenTrans);
    blueTrans = inverseHaar2D(blueTrans);


  }

  public void calculateThreshold(int compressionPercentage) {

    gatherUniqueValues(redTrans);
    gatherUniqueValues(greenTrans);
    gatherUniqueValues(blueTrans);
    List<Double> sortedList = new ArrayList<>(uniqueValues);
    Collections.sort(sortedList);
    int totalCount = sortedList.size();
    int cutOffIndex = Math.round((float) (compressionPercentage * totalCount) /100);
    threshold = sortedList.get(cutOffIndex);
    System.out.println(threshold);
  }

  public void gatherUniqueValues(double[][] matrix) {
    int rows = matrix.length;
    int cols = matrix[0].length;
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        uniqueValues.add(matrix[i][j]);
      }
    }
  }
  public double[][] haar2D(double[][] matrix) {
    int rows = matrix.length;
    int cols = matrix[0].length;

    // Apply Haar transform to rows
    for (int i = 0; i < rows; i++) {
      matrix[i] = transform1(matrix[i], cols);
    }

    // Apply Haar transform to columns
    for (int j = 0; j < cols; j++) {
      double[] column = new double[rows];
      for (int i = 0; i < rows; i++) {
        column[i] = matrix[i][j];
      }
      column = transform1(column, rows);
      for (int i = 0; i < rows; i++) {
        matrix[i][j] = column[i];
      }
    }

    return matrix;
  }

  public double[] transform1(double[] s, int length) {
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

  public double[][] inverseHaar2D(double[][] matrix) {
    int rows = matrix.length;
    int cols = matrix[0].length;

    // Apply inverse Haar transform to columns
    for (int j = 0; j < cols; j++) {
      double[] column = new double[rows];
      for (int i = 0; i < rows; i++) {
        column[i] = matrix[i][j];
      }
      column = inverseTransform1(column, rows);
      for (int i = 0; i < rows; i++) {
        matrix[i][j] = column[i];
      }
    }

    // Apply inverse Haar transform to rows
    for (int i = 0; i < rows; i++) {
      matrix[i] = inverseTransform1(matrix[i], cols);
    }

    return matrix;
  }

  public double[] inverseTransform1(double[] s, int length) {
    int mid = length / 2;
    double[] originalSequence = new double[length];

    for (int i = 0, j = 0; i < mid; i++, j += 2) {
      double avg = s[i];
      double diff = s[i + mid];
      originalSequence[j] = (avg + diff) / Math.sqrt(2);
      originalSequence[j + 1] = (avg - diff) / Math.sqrt(2);
    }

    return originalSequence;
  }

  public void resetValuesBelowThreshold(){
    int width = initialArray.length;
    int height = initialArray[0].length;
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        if(redTrans[x][y]<=threshold){
          redTrans[x][y] = 0;
        }
        if(greenTrans[x][y]<=threshold){
          greenTrans[x][y] = 0;
        }
        if(blueTrans[x][y]<=threshold){
          blueTrans[x][y] = 0;
        }
      }
    }
  }

  public void combinePixel(int[][][] sourceRgb){

    int height = sourceRgb.length;
    int width = sourceRgb[0].length;

    int[][][] imageRGBData = new int[height][width][3];

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        // Round and convert each channel to int
        int red = (int) Math.round(redTrans[y][x]);
        int green = (int) Math.round(greenTrans[y][x]);
        int blue = (int) Math.round(blueTrans[y][x]);

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

    ImageContent correctedImage = new ImageContent("destName", imageRGBData);
    imageMap.put("destName", correctedImage);

  }

  public void compress(String imageName, double compressionPercentage, int maxValue) {
    int[][][] sourceRgb =imageMap.get(imageName).getRgbDataMap();
    padImages(sourceRgb);
    logic();
    combinePixel(sourceRgb);
    ImageContent correctedImage = new ImageContent("destName", imageRGBData);
        imageMap.put("destName", correctedImage);


  }

*/




  /**
   * Color-correct the image by aligning the meaningful peaks of its histogram.
   *
   * @param sourceName The name of the source image.
   * @param destName   The name of the destination color-corrected image.
   */
  @Override
  public void colorCorrectImage(String sourceName, String destName, int splitPercentage) {
    ImageContent sourceImage = imageMap.get(sourceName);

    if (sourceImage != null) {
      int[][][] sourceRGBData = imageMap.get(sourceName).getRgbDataMap();

      int height = sourceRGBData.length;
      int width = sourceRGBData[0].length;

      int[][][] colorCorrectedImage = new int[height][width][3];

      Histogram histogram = new Histogram(10, 245);

      // Populate the histogram with values from the image data.
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          int redValue = sourceRGBData[y][x][0];
          int greenValue = sourceRGBData[y][x][1];
          int blueValue = sourceRGBData[y][x][2];
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

          // Offset the values
          int offsetR = averagePeak - peakR;
          int offsetG = averagePeak - peakG;
          int offsetB = averagePeak - peakB;

          // Apply offsets and ensure values stay within the valid range (10 to 245)
          int correctedRed = Math.min(245, Math.max(10, redValue + offsetR));
          int correctedGreen = Math.min(245, Math.max(10, greenValue + offsetG));
          int correctedBlue = Math.min(245, Math.max(10, blueValue + offsetB));

          // Apply split
          split(colorCorrectedImage, splitPosition, y, x, redValue, greenValue, blueValue, correctedRed, correctedGreen, correctedBlue);

          colorCorrectedImage[y][x][0] = Math.max(0, Math.min(255, redValue + offsetR));
          colorCorrectedImage[y][x][1] = Math.max(0, Math.min(255, greenValue + offsetG));
          colorCorrectedImage[y][x][2] = Math.max(0, Math.min(255, blueValue + offsetB));
        }
      }

      // Create a StringBuilder for the corrected image content.
      createPPMContent(width, height, colorCorrectedImage);

      // Create and store the corrected image.
      ImageContent correctedImage = new ImageContent(destName, colorCorrectedImage);
      imageMap.put(destName, correctedImage);
      //rgbDataMap.put(destName, sourceRGBData);
      System.out.println("Color correction completed with " + splitPercentage + "% split. Corrected image saved as " + destName);
    } else {
      System.out.println("Source image not found: " + sourceName);
    }
  }


  public void createHistogram(String sourceName, String destName) {
    Histogram histogram = new Histogram(0, 255);
    int[][][] sourceRGBData = imageMap.get(sourceName).getRgbDataMap();
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
    ImageContent image = new ImageContent(destName, imageRGBData);
    imageMap.put(destName, image);
    //rgbDataMap.put(destName, imageRGBData);
    System.out.println("Histogram of the image saved as " + destName);
  }

  @Override
  public void applyLevelsAdjustment(int shadowPoint, int midPoint, int highlightPoint, String sourceImageName, String destImageName, int splitPercentage) {
    ImageContent sourceImage = imageMap.get(sourceImageName);

    if (sourceImage != null) {
      int[][][] sourceRGBData = imageMap.get(sourceImageName).getRgbDataMap();

      int width = sourceRGBData[0].length;
      int height = sourceRGBData.length;

      int[][][] adjustedRGBData = new int[height][width][3];

      int splitPosition = width * splitPercentage / 100;

      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          int redValue = sourceRGBData[y][x][0];
          int greenValue = sourceRGBData[y][x][1];
          int blueValue = sourceRGBData[y][x][2];

          int adjustedRed = applyCurvesFunction(redValue, shadowPoint, midPoint, highlightPoint);
          int adjustedGreen = applyCurvesFunction(greenValue, shadowPoint, midPoint, highlightPoint);
          int adjustedBlue = applyCurvesFunction(blueValue, shadowPoint, midPoint, highlightPoint);

          // Apply split
          split(adjustedRGBData, splitPosition, y, x, redValue, greenValue, blueValue, adjustedRed, adjustedGreen, adjustedBlue);
        }
      }

      ImageContent adjustedImage = new ImageContent(destImageName, adjustedRGBData);
      imageMap.put(destImageName, adjustedImage);

      System.out.println("Adjusted image with " + splitPercentage + "% split. Image saved as " + destImageName);
    } else {
      System.out.println("Source image not found: " + sourceImageName);
    }
  }

  private void split(int[][][] adjustedRGBData, int splitPosition, int y, int x, int redValue, int greenValue, int blueValue, int adjustedRed, int adjustedGreen, int adjustedBlue) {
    if (x >= splitPosition) {
      adjustedRGBData[y][x][0] = adjustedRed;
      adjustedRGBData[y][x][1] = adjustedGreen;
      adjustedRGBData[y][x][2] = adjustedBlue;
    } else {
      adjustedRGBData[y][x][0] = redValue;
      adjustedRGBData[y][x][1] = greenValue;
      adjustedRGBData[y][x][2] = blueValue;
    }
  }


  private int applyCurvesFunction(int value, double shadowPoint, double midPoint, double highlightPoint) {
    double A = shadowPoint * shadowPoint * (midPoint - highlightPoint)
            - shadowPoint * (midPoint * midPoint - highlightPoint * highlightPoint)
            + midPoint * midPoint * highlightPoint
            - midPoint * highlightPoint * highlightPoint;

    double A_a = -shadowPoint * (128 - 255) + 128 * highlightPoint - 255 * midPoint;
    double A_b = shadowPoint * shadowPoint * (128 - 255) + 255 * midPoint * midPoint - 128 * highlightPoint * highlightPoint;
    double A_c = shadowPoint * shadowPoint * (255 * midPoint - 128 * highlightPoint) - shadowPoint * (255 * midPoint * midPoint - 128 * highlightPoint * highlightPoint);

    double a = A_a / A;
    double b = A_b / A;
    double c = A_c / A;

    int adjustedValue = (int) (a * value * value + b * value + c);
    return Math.min(255, Math.max(0, adjustedValue));
  }


  @Override
  public void convertToGrayscale(String sourceName, String destName, int splitPercentage) {
    ImageContent sourceImage = imageMap.get(sourceName);

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

          // Apply the specified transformation
          int grayscaleValue = (int) (grayscaleMatrix[0][0] * red + grayscaleMatrix[0][1] * green + grayscaleMatrix[0][2] * blue);

          // Set the same grayscale value for all channels
          grayscalePixels[y][x][0] = grayscaleValue;
          grayscalePixels[y][x][1] = grayscaleValue;
          grayscalePixels[y][x][2] = grayscaleValue;

          // Apply vertical split
          split(grayscalePixels, splitPosition, y, x, red, green, blue, grayscaleValue, grayscaleValue, grayscaleValue);
        }
      }

      // Create a new ImageContent with the grayscale pixels
      ImageContent grayscaleImage = new ImageContent(destName, grayscalePixels);
      imageMap.put(destName, grayscaleImage);

      // Store the grayscale image
      System.out.println("Grayscale image with " + splitPercentage + "% split saved as " + destName);
    } else {
      System.out.println("Source image not found: " + sourceName);
    }
  }

  public void compress(String imageName, String destName, double compressionPercentage) {
    int[][][] sourceRgb =imageMap.get(imageName).getRgbDataMap();
    Compression compressedImage = new Compression();
    int[][][] imageRGBData = compressedImage.compress(sourceRgb,compressionPercentage);
    if(imageRGBData != null){
      ImageContent correctedImage = new ImageContent(destName, imageRGBData);
      imageMap.put(destName, correctedImage);
      System.out.println("Compress image with " + compressionPercentage + "% saved as " + destName);
    }else{
      System.out.println("Error in compressing " + imageName + " by " + compressionPercentage +" %");
    }

  }

}
