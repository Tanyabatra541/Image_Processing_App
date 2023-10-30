import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

import javax.imageio.ImageIO;

public class PNGJPGImage extends AbstractImage {


  @Override
  public void loadImage(String imagePath, String imageName) throws IOException {
    // Implement the logic to load an image from imagePath and name it imageName
    int[][][] imageRGBData = readImageRGBData(imagePath);

    if (imageRGBData != null) {
      ImageContent image = new ImageContent(imageName, serializeImageData(imageRGBData));
      imageMap.put(imageName, image); // Store the Image object in a map
      rgbDataMap.put(imageName, imageRGBData); // Store the RGB data in another map
      System.out.println("Loaded image: " + imageName);
    } else {
      System.out.println("Failed to load the image from: " + imagePath);
    }
  }


  private static int[][][] readImageRGBData(String imagePath) {
    try {
      File imageFile = new File(imagePath);
      if (!imageFile.exists()) {
        System.out.println("Image file not found: " + imagePath);
        return null;
      }

      BufferedImage bufferedImage = ImageIO.read(imageFile);
      if (bufferedImage == null) {
        System.out.println("Failed to read image from: " + imagePath);
        return null;
      }

      int width = bufferedImage.getWidth();
      int height = bufferedImage.getHeight();

      int[][][] imageRGBData = new int[height][width][3];

      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          int rgb = bufferedImage.getRGB(x, y);
          imageRGBData[y][x][0] = (rgb >> 16) & 0xFF; // Red component
          imageRGBData[y][x][1] = (rgb >> 8) & 0xFF;  // Green component
          imageRGBData[y][x][2] = rgb & 0xFF;         // Blue component
        }
      }

      return imageRGBData;
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("Error while reading image from: " + imagePath);
      return null;
    }
  }

  @Override
  public void horizontalFlipImage(String sourceImageName, String destImageName) {
    int[][][] sourceRGBData = rgbDataMap.get(sourceImageName);

    if (sourceRGBData != null) {
      int height = sourceRGBData.length;
      int width = sourceRGBData[0].length;
      int[][][] flippedRGBData = new int[height][width][3];

      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          for (int channel = 0; channel < 3; channel++) {
            int rgb = sourceRGBData[y][x][0] << 16 | sourceRGBData[y][x][1] << 8 | sourceRGBData[y][x][2];
            int flippedX = width - x - 1; // Horizontal flip
            int r = (rgb >> 16) & 0xFF;
            int g = (rgb >> 8) & 0xFF;
            int b = rgb & 0xFF;
            int flippedRGB = (r << 16) | (g << 8) | b;
            flippedRGBData[y][flippedX][0] = (flippedRGB >> 16) & 0xFF;
            flippedRGBData[y][flippedX][1] = (flippedRGB >> 8) & 0xFF;
            flippedRGBData[y][flippedX][2] = flippedRGB & 0xFF;
          }
        }
      }

      // Store the horizontally flipped image in the rgbDataMap
      rgbDataMap.put(destImageName, flippedRGBData);

      // Serialize and store the flipped image in the imageMap
      ImageContent flippedImage = new ImageContent(destImageName, serializeImageData(flippedRGBData));
      imageMap.put(destImageName, flippedImage);
      System.out.println("Image '" + sourceImageName + "' flipped horizontally and saved as '" + destImageName + "'.");
    } else {
      System.out.println("Failed to flip the source image; invalid RGB data.");
    }
  }

  @Override
  public void verticalFlipImage(String sourceName, String destName) {
    ImageContent sourceImage = imageMap.get(sourceName);

    if (sourceImage != null) {
      int[][][] sourceRGBData = rgbDataMap.get(sourceName);

      if (sourceRGBData != null) {
        int height = sourceRGBData.length;
        int width = sourceRGBData[0].length;
        int[][][] flippedRGBData = new int[height][width][3];

        for (int y = 0; y < height; y++) {
          int flippedY = height - y - 1; // Vertical flip
          for (int x = 0; x < width; x++) {
            flippedRGBData[flippedY][x][0] = sourceRGBData[y][x][0]; // Red component
            flippedRGBData[flippedY][x][1] = sourceRGBData[y][x][1]; // Green component
            flippedRGBData[flippedY][x][2] = sourceRGBData[y][x][2]; // Blue component
          }
        }

        // Store the vertically flipped image in the rgbDataMap
        rgbDataMap.put(destName, flippedRGBData);

        // Serialize and store the flipped image in the imageMap
        ImageContent flippedImage;
        flippedImage = new ImageContent(destName, serializeImageData(flippedRGBData));
        imageMap.put(destName, flippedImage);
        System.out.println("Image '" + sourceName + "' flipped vertically and saved as '" + destName + "'.");
      } else {
        System.out.println("Failed to flip the source image vertically; invalid RGB data.");
      }
    } else {
      System.out.println("Source image not found: " + sourceName);
    }
  }


  // Modify this function to work with `int[][][]` data directly
  private static String serializeImageData(int[][][] imageData) {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    for (int y = 0; y < imageData.length; y++) {
      for (int x = 0; x < imageData[y].length; x++) {
        int rgb = (imageData[y][x][0] << 16) | (imageData[y][x][1] << 8) | imageData[y][x][2];
        outputStream.write((rgb >> 16) & 0xFF);
        outputStream.write((rgb >> 8) & 0xFF);
        outputStream.write(rgb & 0xFF);
      }
    }
    byte[] imageBytes = outputStream.toByteArray();
    return Base64.getEncoder().encodeToString(imageBytes);
  }

  @Override
  public void saveImage(String imagePath, String imageName) {
    int[][][] rgbData = rgbDataMap.get(imageName);

    if (rgbData != null) {
      try {
        BufferedImage bufferedImage = convertRGBDataToBufferedImage(rgbData);

        if (bufferedImage != null) {
          // Determine the image format based on the file extension
          String format = imagePath.substring(imagePath.lastIndexOf('.') + 1);

          // Create a file and save the BufferedImage as an image
          File output = new File(imagePath);
          ImageIO.write(bufferedImage, format, output);

          System.out.println("Image saved as " + imagePath);
        } else {
          System.out.println("Failed to convert RGB data to BufferedImage.");
        }
      } catch (IOException e) {
        e.printStackTrace();
        System.out.println("Failed to save the image as " + imagePath);
      }
    } else {
      System.out.println("Image not found: " + imageName);
    }
  }

  private static BufferedImage convertRGBDataToBufferedImage(int[][][] rgbData) {
    int height = rgbData.length;
    int width = rgbData[0].length;

    BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int r = rgbData[y][x][0];
        int g = rgbData[y][x][1];
        int b = rgbData[y][x][2];

        int rgb = (r << 16) | (g << 8) | b;

        bufferedImage.setRGB(x, y, rgb);
      }
    }

    return bufferedImage;
  }

  @Override
  public void brightenImage(String sourceName, String destName, int increment) {
    ImageContent sourceImage = imageMap.get(sourceName);

    if (sourceImage != null) {
      int[][][] sourceRGBData = rgbDataMap.get(sourceName);

      if (sourceRGBData != null) {
        int width = sourceRGBData[0].length;
        int height = sourceRGBData.length;
        int[][][] brightenedRGBData = new int[height][width][3];

        for (int y = 0; y < height; y++) {
          for (int x = 0; x < width; x++) {
            for (int channel = 0; channel < 3; channel++) {
              int value = sourceRGBData[y][x][channel] + increment;
              brightenedRGBData[y][x][channel] = Math.min(255, Math.max(0, value));
            }
          }
        }

        // Store the brightened image in the rgbDataMap
        rgbDataMap.put(destName, brightenedRGBData);

        // Serialize and store the brightened image in the imageMap
        ImageContent brightenedImage;
        brightenedImage = new ImageContent(destName, serializeImageData(brightenedRGBData));
        imageMap.put(destName, brightenedImage);
        System.out.println("Image '" + sourceName + "' brightened and saved as '" + destName + "'");
      } else {
        System.out.println("Failed to brighten the image; invalid RGB data.");
      }
    } else {
      System.out.println("Source image not found: " + sourceName);
    }
  }

  @Override
  public void blurImage(String sourceName, String destName) {
    ImageContent sourceImage = imageMap.get(sourceName);

    if (sourceImage != null) {
      int[][][] sourceRGBData = rgbDataMap.get(sourceName);

      if (sourceRGBData != null) {
        int height = sourceRGBData.length;
        int width = sourceRGBData[0].length;
        int[][][] blurredRGBData = new int[height][width][3];

        // Kernel dimensions
        int kernelSize = 3;
        int kernelRadius = kernelSize / 2;

        for (int y = 0; y < height; y++) {
          for (int x = 0; x < width; x++) {
            for (int channel = 0; channel < 3; channel++) {
              float sum = 0.0f;

              for (int ky = -kernelRadius; ky <= kernelRadius; ky++) {
                for (int kx = -kernelRadius; kx <= kernelRadius; kx++) {
                  int targetY = y + ky;
                  int targetX = x + kx;

                  if (targetY >= 0 && targetY < height && targetX >= 0 && targetX < width) {
                    // Apply the kernel
                    sum += sourceRGBData[targetY][targetX][channel] * gaussianKernel[(ky + kernelRadius) * kernelSize + (kx + kernelRadius)];
                  }
                }
              }

              blurredRGBData[y][x][channel] = (int) sum;
            }
          }
        }

        // Store the blurred image in the rgbDataMap
        rgbDataMap.put(destName, blurredRGBData);

        // Serialize and store the blurred image in the imageMap
        ImageContent blurredImage;
        blurredImage = new ImageContent(destName, serializeImageData(blurredRGBData));
        imageMap.put(destName, blurredImage);
        System.out.println("Image '" + sourceName + "' Gaussian blurred and saved as '" + destName + "'");
      } else {
        System.out.println("Failed to blur the image; invalid RGB data.");
      }
    } else {
      System.out.println("Source image not found: " + sourceName);
    }
  }

  @Override
  public void sharpenImage(String sourceName, String destName) {
    ImageContent sourceImage = imageMap.get(sourceName);

    if (sourceImage != null) {
      int[][][] sourceRGBData = rgbDataMap.get(sourceName);

      if (sourceRGBData != null) {
        int height = sourceRGBData.length;
        int width = sourceRGBData[0].length;
        int[][][] sharpenedRGBData = new int[height][width][3];

        // Kernel dimensions
        int kernelSize = 5;
        int kernelRadius = kernelSize / 2;

        for (int y = 0; y < height; y++) {
          for (int x = 0; x < width; x++) {
            for (int channel = 0; channel < 3; channel++) {
              float sum = 0.0f;

              for (int ky = -kernelRadius; ky <= kernelRadius; ky++) {
                for (int kx = -kernelRadius; kx <= kernelRadius; kx++) {
                  int targetY = y + ky;
                  int targetX = x + kx;

                  if (targetY >= 0 && targetY < height && targetX >= 0 && targetX < width) {
                    // Apply the kernel
                    sum += sourceRGBData[targetY][targetX][channel] * sharpeningKernel[(ky + kernelRadius) * kernelSize + (kx + kernelRadius)];
                  }
                }
              }

              sharpenedRGBData[y][x][channel] = (int) sum;
            }
          }
        }

        // Ensure pixel values are within the valid range [0, 255]
        for (int y = 0; y < height; y++) {
          for (int x = 0; x < width; x++) {
            for (int channel = 0; channel < 3; channel++) {
              sharpenedRGBData[y][x][channel] = Math.min(255, Math.max(0, sharpenedRGBData[y][x][channel]));
            }
          }
        }

        // Store the sharpened image in the rgbDataMap
        rgbDataMap.put(destName, sharpenedRGBData);

        // Serialize and store the sharpened image in the imageMap
        ImageContent sharpenedImage;
        sharpenedImage = new ImageContent(destName, serializeImageData(sharpenedRGBData));
        imageMap.put(destName, sharpenedImage);
        System.out.println("Image '" + sourceName + "' sharpened and saved as '" + destName + "'");
      } else {
        System.out.println("Failed to sharpen the image; invalid RGB data.");
      }
    } else {
      System.out.println("Source image not found: " + sourceName);
    }
  }
  @Override
  public void sepiaImage(String sourceName, String destName) {
    ImageContent sourceImage = imageMap.get(sourceName);

    if (sourceImage != null) {
      int[][][] sourceRGBData = rgbDataMap.get(sourceName);

      if (sourceRGBData != null) {
        int height = sourceRGBData.length;
        int width = sourceRGBData[0].length;
        int[][][] sepiaRGBData = new int[height][width][3];

        for (int y = 0; y < height; y++) {
          for (int x = 0; x < width; x++) {
            int r = sourceRGBData[y][x][0];
            int g = sourceRGBData[y][x][1];
            int b = sourceRGBData[y][x][2];

            int sepiaR = (int) (0.393 * r + 0.769 * g + 0.189 * b);
            int sepiaG = (int) (0.349 * r + 0.686 * g + 0.168 * b);
            int sepiaB = (int) (0.272 * r + 0.534 * g + 0.131 * b);

            sepiaRGBData[y][x][0] = Math.min(255, Math.max(0, sepiaR));
            sepiaRGBData[y][x][1] = Math.min(255, Math.max(0, sepiaG));
            sepiaRGBData[y][x][2] = Math.min(255, Math.max(0, sepiaB));
          }
        }

        // Store the sepia image in the rgbDataMap
        rgbDataMap.put(destName, sepiaRGBData);

        // Serialize and store the sepia image in the imageMap
        ImageContent sepiaImage;
        sepiaImage = new ImageContent(destName, serializeImageData(sepiaRGBData));
        imageMap.put(destName, sepiaImage);
        System.out.println("Image '" + sourceName + "' converted to sepia and saved as '" + destName + "'");
      } else {
        System.out.println("Failed to convert the image to sepia; invalid RGB data.");
      }
    } else {
      System.out.println("Source image not found: " + sourceName);
    }
  }

  @Override
  public void combineRGBImages(String destName, String sourceName1, String sourceName2, String sourceName3) {
    ImageContent sourceImage1 = imageMap.get(sourceName1);
    ImageContent sourceImage2 = imageMap.get(sourceName2);
    ImageContent sourceImage3 = imageMap.get(sourceName3);

    if (sourceImage1 != null && sourceImage2 != null && sourceImage3 != null) {
      int[][][] sourceRGBData1 = rgbDataMap.get(sourceName1);
      int[][][] sourceRGBData2 = rgbDataMap.get(sourceName2);
      int[][][] sourceRGBData3 = rgbDataMap.get(sourceName3);

      if (sourceRGBData1 != null && sourceRGBData2 != null && sourceRGBData3 != null) {
        int height = sourceRGBData1.length;
        int width = sourceRGBData1[0].length;
        int[][][] combinedRGBData = new int[height][width][3];

        for (int y = 0; y < height; y++) {
          for (int x = 0; x < width; x++) {
            combinedRGBData[y][x][0] = sourceRGBData1[y][x][0]; // Red channel
            combinedRGBData[y][x][1] = sourceRGBData2[y][x][1]; // Green channel
            combinedRGBData[y][x][2] = sourceRGBData3[y][x][2]; // Blue channel
          }
        }

        // Store the combined image in the rgbDataMap
        rgbDataMap.put(destName, combinedRGBData);

        // Serialize and store the combined image in the imageMap
        ImageContent combinedImage = new ImageContent(destName, serializeImageData(combinedRGBData));
        imageMap.put(destName, combinedImage);
        System.out.println("Images '" + sourceName1 + "', '" + sourceName2 + "', and '" + sourceName3 + "' combined and saved as '" + destName + "'");
      } else {
        System.out.println("Failed to combine images; invalid RGB data.");
      }
    } else {
      System.out.println("Source images not found: " + sourceName1 + ", " + sourceName2 + ", " + sourceName3);
    }
  }





  @Override
  public void rgbSplitImage(String sourceName, String destNameRed, String destNameGreen, String destNameBlue) {
    ImageContent sourceImage = imageMap.get(sourceName);

    if (sourceImage != null) {
      int[][][] sourceRGBData = rgbDataMap.get(sourceName);

      if (sourceRGBData != null) {
        int height = sourceRGBData.length;
        int width = sourceRGBData[0].length;
        int[][][] rChannel = new int[height][width][3];
        int[][][] gChannel = new int[height][width][3];
        int[][][] bChannel = new int[height][width][3];

        for (int y = 0; y < height; y++) {
          for (int x = 0; x < width; x++) {
            int redValue = sourceRGBData[y][x][0];
            int greenValue = sourceRGBData[y][x][1];
            int blueValue = sourceRGBData[y][x][2];

            rChannel[y][x][0] = redValue;
            gChannel[y][x][1] = greenValue;
            bChannel[y][x][2] = blueValue;
          }
        }

        // Store the split RGB channels in the rgbDataMap
        rgbDataMap.put(destNameRed, rChannel);
        rgbDataMap.put(destNameGreen, gChannel);
        rgbDataMap.put(destNameBlue, bChannel);

        // Serialize and store the split RGB channels in the imageMap
        ImageContent rChannelImage = new ImageContent(destNameRed, serializeImageData(rChannel));
        ImageContent gChannelImage = new ImageContent(destNameGreen, serializeImageData(gChannel));
        ImageContent bChannelImage = new ImageContent(destNameBlue, serializeImageData(bChannel));

        imageMap.put(destNameRed, rChannelImage);
        imageMap.put(destNameGreen, gChannelImage);
        imageMap.put(destNameBlue, bChannelImage);

        System.out.println("Image '" + sourceName + "' split into RGB channels and saved as '" + destNameRed + "', '" + destNameGreen + "', and '" + destNameBlue + "'");
      } else {
        System.out.println("Failed to split image into RGB channels; invalid RGB data.");
      }
    } else {
      System.out.println("Source image not found: " + sourceName);
    }
  }

  @Override
  public void extractComponent(String sourceName, String destName, String component) {
    ImageContent sourceImage = imageMap.get(sourceName);

    if (sourceImage != null) {
      int[][][] sourceRGBData = rgbDataMap.get(sourceName);

      if (sourceRGBData != null) {
        int height = sourceRGBData.length;
        int width = sourceRGBData[0].length;

        int[][][] componentRGBData = new int[height][width][3];

        for (int y = 0; y < height; y++) {
          for (int x = 0; x < width; x++) {
            int r = 0;
            int g = 0;
            int b = 0;

            switch (component) {
              case "red":
                r = sourceRGBData[y][x][0];
                break;
              case "green":
                g = sourceRGBData[y][x][1];
                break;
              case "blue":
                b = sourceRGBData[y][x][2];
                break;
              case "luma":
                r = g = b = (int) (0.2126 * sourceRGBData[y][x][0] + 0.7152 * sourceRGBData[y][x][1] + 0.0722 * sourceRGBData[y][x][2]);
                break;
              case "intensity":
                int sum = sourceRGBData[y][x][0] + sourceRGBData[y][x][1] + sourceRGBData[y][x][2];
                r = g = b = sum / 3;
                break;
              case "value":
                int max = Math.max(sourceRGBData[y][x][0], Math.max(sourceRGBData[y][x][1], sourceRGBData[y][x][2]));
                r = g = b = max;
                break;
              default:
                System.out.println("Invalid component parameter: " + component);
                return;
            }

            componentRGBData[y][x][0] = r;
            componentRGBData[y][x][1] = g;
            componentRGBData[y][x][2] = b;
          }
        }

        rgbDataMap.put(destName, componentRGBData);

        ImageContent destImage;
        destImage = new ImageContent(destName, serializeImageData(componentRGBData));
        imageMap.put(destName, destImage);
        System.out.println(component + " component image created from '" + sourceName + "' and saved as '" + destName + "'");
      } else {
        System.out.println("Failed to extract the " + component + " component; invalid RGB data.");
      }
    } else {
      System.out.println("Source image not found: " + sourceName);
    }
  }



}
//need to check extractComponent, rgb-split and combine