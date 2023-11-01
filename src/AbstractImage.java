import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public abstract class AbstractImage implements ImageOperations{


  static final Map<String, ImageContent> imageMap = new HashMap<>();

  static final Map<String, int[][][]> rgbDataMap = new HashMap<>();


  static ImageOperations imageObj = null;

  // Define the sharpening kernel
  float[] sharpeningKernel = {
          -1.0f / 8.0f, -1.0f / 8.0f, -1.0f / 8.0f, -1.0f / 8.0f, -1.0f / 8.0f,
          -1.0f / 8.0f, 1.0f / 4.0f, 1.0f / 4.0f, 1.0f / 4.0f, -1.0f / 8.0f,
          -1.0f / 8.0f, 1.0f / 4.0f, 1.0f, 1.0f / 4.0f, -1.0f / 8.0f,
          -1.0f / 8.0f, 1.0f / 4.0f, 1.0f / 4.0f, 1.0f / 4.0f, -1.0f / 8.0f,
          -1.0f / 8.0f, -1.0f / 8.0f, -1.0f / 8.0f, -1.0f / 8.0f, -1.0f / 8.0f
  };

  // Define the Gaussian blur kernel
  float[] gaussianKernel = {
          1.0f / 16.0f, 1.0f / 8.0f, 1.0f / 16.0f,
          1.0f / 8.0f, 1.0f / 4.0f, 1.0f / 8.0f,
          1.0f / 16.0f, 1.0f / 8.0f, 1.0f / 16.0f
  };


  public abstract void loadImage(String imagePath, String imageName) throws IOException;

  public abstract void saveImage(String imagePath, String imageName) throws IOException;


  @Override
  public void horizontalFlipImage(String sourceImageName, String destImageName) {
    ImageContent sourceImage = imageMap.get(sourceImageName);

    if (sourceImage != null) {
      int[][][] sourceRGBData = rgbDataMap.get(sourceImageName);

      if (sourceRGBData != null) {
        int width = sourceRGBData[0].length;
        int height = sourceRGBData.length;

        // Create a new RGB data array for the horizontally flipped image
        int[][][] flippedRGBData = new int[height][width][3];

        // Iterate over the source RGB data and flip it horizontally in the new array
        for (int y = 0; y < height; y++) {
          for (int x = 0; x < width; x++) {
            flippedRGBData[y][x] = sourceRGBData[y][width - x - 1];
          }
        }

        // Create a new content string for the flipped image using createPPMContent function
        StringBuilder flippedContent = createPPMContent(width, height, flippedRGBData);

        // Store the horizontally flipped image in the image map
        ImageContent flippedImage = new ImageContent(destImageName, flippedContent.toString());
        imageMap.put(destImageName, flippedImage);

        // Also save the flipped RGB data in the rgbDataMap
        rgbDataMap.put(destImageName, flippedRGBData);

        System.out.println("Image '" + sourceImageName + "' flipped horizontally and saved as '" + destImageName + "'.");
      } else {
        System.out.println("Failed to flip the source image; invalid RGB data.");
      }
    } else {
      throw new IllegalArgumentException("Source image not found: " + sourceImageName);
    }
  }


  @Override
  public void verticalFlipImage(String sourceImageName, String destImageName) {
    // Check if the source image exists
    ImageContent sourceImage = imageMap.get(sourceImageName);
    if (sourceImage == null) {
      throw new IllegalArgumentException("Source image not found: " + sourceImageName);

    }

    // Get the RGB data for the source image
    int[][][] sourceRGBData = rgbDataMap.get(sourceImageName);

    // Create a new RGB data array for the flipped image
    int height = sourceRGBData.length;
    int width = sourceRGBData[0].length;
    int[][][] flippedRGBData = new int[height][width][3];

    // Perform the vertical flip by reversing the order of rows
    for (int y = 0; y < height; y++) {
      int newY = height - 1 - y; // Calculate the new Y coordinate for the flipped image
      flippedRGBData[newY] = sourceRGBData[y]; // Copy the row to the flipped position
    }

    // Create a new content string for the flipped image using createPPMContent function
    StringBuilder flippedContent = createPPMContent(width, height, flippedRGBData);

    // Store the flipped image in the image map
    ImageContent flippedImage = new ImageContent(destImageName, flippedContent.toString());
    imageMap.put(destImageName, flippedImage);
    rgbDataMap.put(destImageName, flippedRGBData);

    System.out.println("Vertical flip completed. Flipped image saved as " + destImageName);
  }


  @Override
  public void sharpenImage(String sourceImageName, String destImageName) {
    // Check if the source image exists
    ImageContent sourceImage = imageMap.get(sourceImageName);
    if (sourceImage == null) {
      throw new IllegalArgumentException("Source image not found: " + sourceImageName);

    }

    // Get the RGB data for the source image
    int[][][] sourceRGBData = rgbDataMap.get(sourceImageName);

    // Create a new RGB data array for the sharpened image
    int height = sourceRGBData.length;
    int width = sourceRGBData[0].length;
    int[][][] sharpenedRGBData = new int[height][width][3];

    // Perform the sharpening operation
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
          int newValue = Math.min(255, Math.max(0, (int) sum)); // Ensure the value is in the 0-255 range
          sharpenedRGBData[y][x][channel] = newValue;
        }
      }
    }

    // Create a new content string for the sharpened image using createPPMContent function
    StringBuilder sharpenedContent = createPPMContent(width, height, sharpenedRGBData);

    // Store the sharpened image in the image map
    ImageContent sharpenedImage = new ImageContent(destImageName, sharpenedContent.toString());
    imageMap.put(destImageName, sharpenedImage);
    rgbDataMap.put(destImageName, sharpenedRGBData);

    System.out.println("Image sharpening completed. Sharpened image saved as " + destImageName);
  }



  @Override
  public void blurImage(String sourceImageName, String destImageName) {
    // Check if the source image exists
    ImageContent sourceImage = imageMap.get(sourceImageName);
    if (sourceImage == null) {
      throw new IllegalArgumentException("Source image not found: " + sourceImageName);

    }

    // Get the RGB data for the source image
    int[][][] sourceRGBData = rgbDataMap.get(sourceImageName);

    // Create a new RGB data array for the blurred image
    int height = sourceRGBData.length;
    int width = sourceRGBData[0].length;
    int[][][] blurredRGBData = new int[height][width][3];

    // Perform the blurring operation
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
          int newValue = Math.min(255, Math.max(0, (int) sum)); // Ensure the value is in the 0-255 range
          blurredRGBData[y][x][channel] = newValue;
        }
      }
    }

    // Create a new content string for the blurred image using createPPMContent function
    StringBuilder blurredContent = createPPMContent(width, height, blurredRGBData);

    // Store the blurred image in the image map
    ImageContent blurredImage = new ImageContent(destImageName, blurredContent.toString());
    imageMap.put(destImageName, blurredImage);
    rgbDataMap.put(destImageName, blurredRGBData);

    System.out.println("Image blurring completed. Blurred image saved as " + destImageName);
  }



  @Override
  public void brightenImage(String sourceImageName, String destImageName, int increment) {
    // Check if the source image exists
    ImageContent sourceImage = imageMap.get(sourceImageName);
    if (sourceImage == null) {
      throw new IllegalArgumentException("Source image not found: " + sourceImageName);
    }

    // Get the RGB data for the source image
    int[][][] sourceRGBData = rgbDataMap.get(sourceImageName);

    // Create a new RGB data array for the brightened image
    int height = sourceRGBData.length;
    int width = sourceRGBData[0].length;
    int[][][] brightenedRGBData = new int[height][width][3];

    // Perform the brightening by incrementing the color values
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        for (int channel = 0; channel < 3; channel++) {
          int originalValue = sourceRGBData[y][x][channel];
          int newValue = originalValue + increment;
          newValue = Math.min(255, Math.max(0, newValue)); // Ensure the value is in the 0-255 range
          brightenedRGBData[y][x][channel] = newValue;
        }
      }
    }

    // Create a new content string for the brightened image using createPPMContent function
    StringBuilder brightenedContent = createPPMContent(width, height, brightenedRGBData);

    // Store the brightened image in the image map
    ImageContent brightenedImage = new ImageContent(destImageName, brightenedContent.toString());
    imageMap.put(destImageName, brightenedImage);
    rgbDataMap.put(destImageName, brightenedRGBData);

    System.out.println("Image brightening completed. Brightened image saved as " + destImageName);
  }


  @Override
  public void sepiaImage(String sourceName, String destName) {
    // Check if the source image exists
    ImageContent sourceImage = imageMap.get(sourceName);
    if (sourceImage == null) {
      throw new IllegalArgumentException("Source image not found: " + sourceName);
    }

    // Get the RGB data for the source image
    int[][][] sourceRGBData = rgbDataMap.get(sourceName);

    // Create a new RGB data array for the sepia-toned image
    int height = sourceRGBData.length;
    int width = sourceRGBData[0].length;
    int[][][] sepiaRGBData = new int[height][width][3];

    // Apply the sepia filter to each pixel
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int r = sourceRGBData[y][x][0];
        int g = sourceRGBData[y][x][1];
        int b = sourceRGBData[y][x][2];

        int tr = (int) (0.393 * r + 0.769 * g + 0.189 * b);
        int tg = (int) (0.349 * r + 0.686 * g + 0.168 * b);
        int tb = (int) (0.272 * r + 0.534 * g + 0.131 * b);

        // Ensure color values are in the 0-255 range
        tr = Math.min(255, Math.max(0, tr));
        tg = Math.min(255, Math.max(0, tg));
        tb = Math.min(255, Math.max(0, tb));

        sepiaRGBData[y][x][0] = tr;
        sepiaRGBData[y][x][1] = tg;
        sepiaRGBData[y][x][2] = tb;
      }
    }

    // Create a new content string for the sepia-toned image using createPPMContent function
    StringBuilder sepiaContent = createPPMContent(width, height, sepiaRGBData);

    // Store the sepia-toned image in the image map
    ImageContent sepiaImage = new ImageContent(destName, sepiaContent.toString());
    imageMap.put(destName, sepiaImage);
    rgbDataMap.put(destName, sepiaRGBData);

    System.out.println("Sepia filter applied. Sepia-toned image saved as " + destName);
  }


  @Override
  public void combineRGBImages(String combinedName, String redName, String greenName, String blueName) {
    // Check if the source images exist
    ImageContent redImage = imageMap.get(redName);
    ImageContent greenImage = imageMap.get(greenName);
    ImageContent blueImage = imageMap.get(blueName);

    if (redImage == null || greenImage == null || blueImage == null) {
      System.out.println("One or more source images not found.");
      return;
    }

    // Get the RGB data for the source images
    int[][][] redRGBData = rgbDataMap.get(redName);
    int[][][] greenRGBData = rgbDataMap.get(greenName);
    int[][][] blueRGBData = rgbDataMap.get(blueName);

    // Check if the dimensions of the images match
    int height = redRGBData.length;
    int width = redRGBData[0].length;

    if (height != greenRGBData.length || height != blueRGBData.length ||
            width != greenRGBData[0].length || width != blueRGBData[0].length) {
      System.out.println("Source images have different dimensions.");
      return;
    }

    // Create a new RGB data array for the combined image
    int[][][] combinedRGBData = new int[height][width][3];

    // Combine the RGB channels into a single image
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        combinedRGBData[y][x][0] = redRGBData[y][x][0];
        combinedRGBData[y][x][1] = greenRGBData[y][x][1];
        combinedRGBData[y][x][2] = blueRGBData[y][x][2];
      }
    }

    // Create a new content string for the combined image using createPPMContent function
    StringBuilder combinedContent = createPPMContent(width, height, combinedRGBData);

    // Store the combined image in the image map
    ImageContent combinedImage = new ImageContent(combinedName, combinedContent.toString());
    imageMap.put(combinedName, combinedImage);
    rgbDataMap.put(combinedName, combinedRGBData);

    System.out.println("RGB channels combined. Combined image saved as " + combinedName);
  }



  @Override
  public void rgbSplitImage(String sourceName, String destNameRed, String destNameGreen, String destNameBlue) {
    // Check if the source image exists
    ImageContent sourceImage = imageMap.get(sourceName);
    if (sourceImage == null) {
      throw new IllegalArgumentException("Source image not found: " + sourceName);
    }

    // Get the RGB data for the source image
    int[][][] sourceRGBData = rgbDataMap.get(sourceName);

    // Create new RGB data arrays for the red, green, and blue channels
    int height = sourceRGBData.length;
    int width = sourceRGBData[0].length;
    int[][][] redRGBData = new int[height][width][3];
    int[][][] greenRGBData = new int[height][width][3];
    int[][][] blueRGBData = new int[height][width][3];

    // Split the RGB channels into separate images
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int r = sourceRGBData[y][x][0];
        int g = sourceRGBData[y][x][1];
        int b = sourceRGBData[y][x][2];

        // Red channel image
        redRGBData[y][x][0] = r;
        redRGBData[y][x][1] = 0;
        redRGBData[y][x][2] = 0;

        // Green channel image
        greenRGBData[y][x][0] = 0;
        greenRGBData[y][x][1] = g;
        greenRGBData[y][x][2] = 0;

        // Blue channel image
        blueRGBData[y][x][0] = 0;
        blueRGBData[y][x][1] = 0;
        blueRGBData[y][x][2] = b;
      }
    }

    // Create content strings for the red, green, and blue channel images using createPPMContent function
    StringBuilder redContent = createPPMContent(width, height, redRGBData);
    StringBuilder greenContent = createPPMContent(width, height, greenRGBData);
    StringBuilder blueContent = createPPMContent(width, height, blueRGBData);

    // Store the red, green, and blue channel images in the image map
    ImageContent redImage = new ImageContent(destNameRed, redContent.toString());
    ImageContent greenImage = new ImageContent(destNameGreen, greenContent.toString());
    ImageContent blueImage = new ImageContent(destNameBlue, blueContent.toString());

    imageMap.put(destNameRed, redImage);
    imageMap.put(destNameGreen, greenImage);
    imageMap.put(destNameBlue, blueImage);

    rgbDataMap.put(destNameRed, redRGBData);
    rgbDataMap.put(destNameGreen, greenRGBData);
    rgbDataMap.put(destNameBlue, blueRGBData);

    System.out.println("RGB channels split and saved as " + destNameRed + ", " + destNameGreen + ", " + destNameBlue);
  }

  // Helper method to create PPM content for a given RGB data
  private StringBuilder createPPMContent(int width, int height, int[][][] rgbData) {
    StringBuilder content = new StringBuilder();
    content.append("P3\n");
    content.append(width).append(" ").append(height).append("\n");
    content.append("255\n"); // Maximum color component value

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


  @Override
  public void extractComponent(String sourceName, String destName, String component) {
    ImageContent sourceImage = imageMap.get(sourceName);

    if (sourceImage != null) {
      int[][][] sourceRGBData = rgbDataMap.get(sourceName);

      if (sourceRGBData != null) {
        int height = sourceRGBData.length;
        int width = sourceRGBData[0].length;

        // Create a new RGB data array for the extracted component
        int[][][] extractedRGBData = new int[height][width][3];

        for (int y = 0; y < height; y++) {
          for (int x = 0; x < width; x++) {
            int r = sourceRGBData[y][x][0];
            int g = sourceRGBData[y][x][1];
            int b = sourceRGBData[y][x][2];

            switch (component) {
              case "red":
                // Set the red component to the extracted value, keep green and blue
                g = 0;
                b = 0;
                break;
              case "green":
                // Set the green component to the extracted value, keep red and blue
                r = 0;
                b = 0;
                break;
              case "blue":
                // Set the blue component to the extracted value, keep red and green
                r = 0;
                g = 0;
                break;
              case "luma":
                // Calculate luma from RGB (standard BT.709 formula)
                int luma = (int) (0.2126 * r + 0.7152 * g + 0.0722 * b);
                r = luma;
                g = luma;
                b = luma;
                break;
              case "intensity":
                // Calculate intensity from RGB (average of RGB components)
                int intensity = (r + g + b) / 3;
                r = intensity;
                g = intensity;
                b = intensity;
                break;
              case "value":
                // Extract value (brightness) from RGB (maximum of RGB components)
                int value = Math.max(r, Math.max(g, b));
                r = value;
                g = value;
                b = value;
                break;
              default:
                throw new IllegalArgumentException("Invalid component parameter: " + component);

            }

            extractedRGBData[y][x][0] = r;
            extractedRGBData[y][x][1] = g;
            extractedRGBData[y][x][2] = b;
          }
        }

        // Create a new content string for the extracted component
        StringBuilder extractedContent = createPPMContent(width, height, extractedRGBData);

        // Store the extracted component image in the image map
        ImageContent destImage = new ImageContent(destName, extractedContent.toString());
        imageMap.put(destName, destImage);
        rgbDataMap.put(destName, extractedRGBData);
        System.out.println(component + " component image created from '" + sourceName + "' and saved as '" + destName + "'");
      } else {
        System.out.println("Failed to extract the " + component + " component; invalid RGB data.");
      }
    } else {
      throw new IllegalArgumentException("Source image not found: " + sourceName);
    }
  }
  public Map<String, ImageContent> getImageMap(){
    return imageMap;
  }

  public Map<String, int[][][]> getRgbDataMap(){
    return rgbDataMap;
  }

  protected boolean isValidFilename(String filename) {
    // Implement your filename validation logic here
    // For example, you can check for illegal characters or other criteria
    return filename.matches("[a-zA-Z0-9_\\-]+\\.png");
  }


}
