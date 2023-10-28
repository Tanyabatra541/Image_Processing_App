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

  private static String serializeImageData(int[][][] imageData) throws IOException {
    BufferedImage bufferedImage = new BufferedImage(imageData[0].length, imageData.length,
            BufferedImage.TYPE_INT_RGB);
    for (int y = 0; y < imageData.length; y++) {
      for (int x = 0; x < imageData[y].length; x++) {
        int rgb = (imageData[y][x][0] << 16) | (imageData[y][x][1] << 8) | imageData[y][x][2];
        bufferedImage.setRGB(x, y, rgb);
      }
    }
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    ImageIO.write(bufferedImage, "PNG", outputStream);
    byte[] imageBytes = outputStream.toByteArray();
    return Base64.getEncoder().encodeToString(imageBytes);
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
  public void saveImage(String imagePath, String imageName) {
    ImageContent image = imageMap.get(imageName);

    if (image != null) {
      try {
        // Get the image content
        String imageContent = image.getContent();

        // Convert the image content to a BufferedImage
        BufferedImage bufferedImage = convertStringToBufferedImage(imageContent);

        if (bufferedImage != null) {
          // Determine the image format based on the file extension
          String format = imagePath.substring(imagePath.lastIndexOf('.') + 1);

          // Create a file and save the BufferedImage as an image
          File output = new File(imagePath);
          ImageIO.write(bufferedImage, format, output);

          System.out.println("Image saved as " + imagePath);
        } else {
          System.out.println("Failed to convert image content to BufferedImage.");
        }
      } catch (IOException e) {
        e.printStackTrace();
        System.out.println("Failed to save the image as " + imagePath);
      }
    } else {
      System.out.println("Image not found: " + imageName);
    }
  }

  private static BufferedImage convertStringToBufferedImage(String imageContent) {
    try {
      byte[] bytes = Base64.getDecoder().decode(imageContent);
      ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
      return ImageIO.read(inputStream);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

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
            int rgb = sourceRGBData[y][x][0] << 16 | sourceRGBData[y][x][1] << 8 | sourceRGBData[y][x][2];
            int flippedX = width - x - 1; // Horizontal flip
            flippedRGBData[y][flippedX][0] = (rgb >> 16) & 0xFF; // Red component
            flippedRGBData[y][flippedX][1] = (rgb >> 8) & 0xFF;  // Green component
            flippedRGBData[y][flippedX][2] = rgb & 0xFF;         // Blue component
          }
        }

        // Store the horizontally flipped image in the rgbDataMap
        rgbDataMap.put(destImageName, flippedRGBData);

        // Serialize and store the flipped image in the imageMap
        ImageContent flippedImage;
        try {
          flippedImage = new ImageContent(destImageName, serializeImageData(flippedRGBData));
          imageMap.put(destImageName, flippedImage);
          System.out.println("Image '" + sourceImageName + "' flipped horizontally and saved as '" + destImageName + "'.");
        } catch (IOException e) {
          e.printStackTrace();
        }
      } else {
        System.out.println("Failed to flip the source image; invalid RGB data.");
      }
    } else {
      System.out.println("Source image not found: " + sourceImageName);
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
        try {
          flippedImage = new ImageContent(destName, serializeImageData(flippedRGBData));
          imageMap.put(destName, flippedImage);
          System.out.println("Image '" + sourceName + "' flipped vertically and saved as '" + destName + "'.");
        } catch (IOException e) {
          e.printStackTrace();
        }
      } else {
        System.out.println("Failed to flip the source image vertically; invalid RGB data.");
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

        // Store the sharpened image in the rgbDataMap
        rgbDataMap.put(destName, sharpenedRGBData);

        // Serialize and store the sharpened image in the imageMap
        ImageContent sharpenedImage;
        try {
          sharpenedImage = new ImageContent(destName, serializeImageData(sharpenedRGBData));
          imageMap.put(destName, sharpenedImage);
          System.out.println("Image '" + sourceName + "' sharpened and saved as '" + destName + "'");
        } catch (IOException e) {
          e.printStackTrace();
        }
      } else {
        System.out.println("Failed to sharpen the image; invalid RGB data.");
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
        try {
          blurredImage = new ImageContent(destName, serializeImageData(blurredRGBData));
          imageMap.put(destName, blurredImage);
          System.out.println("Image '" + sourceName + "' Gaussian blurred and saved as '" + destName + "'");
        } catch (IOException e) {
          e.printStackTrace();
        }
      } else {
        System.out.println("Failed to blur the image; invalid RGB data.");
      }
    } else {
      System.out.println("Source image not found: " + sourceName);
    }
  }


  @Override
  public void brightenImage(String sourceName, String destName, int increment) {
    ImageContent sourceImage = imageMap.get(sourceName);

    if (sourceImage != null) {
      int[][][] sourceRGBData = rgbDataMap.get(sourceName);

      if (sourceRGBData != null) {
        // Brighten the image by the specified increment
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
        ImageContent brightenedImage;
        try {
          brightenedImage = new ImageContent(destName, serializeImageData(brightenedRGBData));
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
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
  public void sepiaImage(String sourceName, String destName) {
    ImageContent sourceImage = imageMap.get(sourceName);

    if (sourceImage != null) {
      int[][][] sourceRGBData = rgbDataMap.get(sourceName);

      if (sourceRGBData != null) {
        // Apply sepia filter to the image
        int width = sourceRGBData[0].length;
        int height = sourceRGBData.length;
        int[][][] sepiaRGBData = new int[height][width][3];

        for (int y = 0; y < height; y++) {
          for (int x = 0; x < width; x++) {
            int r = sourceRGBData[y][x][0];
            int g = sourceRGBData[y][x][1];
            int b = sourceRGBData[y][x][2];

            // Calculate new RGB values for the sepia effect
            int tr = (int) (0.393 * r + 0.769 * g + 0.189 * b);
            int tg = (int) (0.349 * r + 0.686 * g + 0.168 * b);
            int tb = (int) (0.272 * r + 0.534 * g + 0.131 * b);

            // Ensure values are within the 0-255 range
            sepiaRGBData[y][x][0] = clamp(tr);
            sepiaRGBData[y][x][1] = clamp(tg);
            sepiaRGBData[y][x][2] = clamp(tb);
          }
        }

        // Store the sepia image in the rgbDataMap
        rgbDataMap.put(destName, sepiaRGBData);

        ImageContent sepiaImage;
        try {
          sepiaImage = new ImageContent(destName, serializeImageData(sepiaRGBData));
        } catch (IOException e) {
          throw new RuntimeException(e);
        }

        imageMap.put(destName, sepiaImage);

        System.out.println("Image '" + sourceName + "' converted to sepia and saved as '" + destName + "'");
      } else {
        System.out.println("Failed to apply the sepia effect; invalid RGB data.");
      }
    } else {
      System.out.println("Source image not found: " + sourceName);
    }
  }

  @Override
  public void combineRGBImages(String combinedName, String redName, String greenName, String blueName) {
    ImageContent redImage = imageMap.get(redName);
    ImageContent greenImage = imageMap.get(greenName);
    ImageContent blueImage = imageMap.get(blueName);

    if (redImage != null && greenImage != null && blueImage != null) {
      int[][][] redData = rgbDataMap.get(redName);
      int[][][] greenData = rgbDataMap.get(greenName);
      int[][][] blueData = rgbDataMap.get(blueName);

      if (redData.length != greenData.length || greenData.length != blueData.length ||
              redData[0].length != greenData[0].length || greenData[0].length != blueData[0].length) {
        System.out.println("Image dimensions do not match.");
        return;
      }

      int height = redData.length;
      int width = redData[0].length;

      int[][][] combinedData = new int[height][width][3];

      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          combinedData[y][x][0] = redData[y][x][0];    // Red component
          combinedData[y][x][1] = greenData[y][x][1];  // Green component
          combinedData[y][x][2] = blueData[y][x][2];   // Blue component
        }
      }

      // Store the combined image in the rgbDataMap
      rgbDataMap.put(combinedName, combinedData);

      // Serialize and store the combined image in the imageMap
      try {
        ImageContent combinedImage = new ImageContent(combinedName, serializeImageData(combinedData));
        imageMap.put(combinedName, combinedImage);
        System.out.println("Combined image created as '" + combinedName + "'");
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      System.out.println("One or more images not found: " + redName + ", " + greenName + ", " + blueName);
    }
  }

  @Override
  public void rgbSplitImage(String sourceName, String destNameRed, String destNameGreen, String destNameBlue) {
    ImageContent sourceImage = imageMap.get(sourceName);

    if (sourceImage != null) {
      String sourceContent = sourceImage.getContent();
      BufferedImage sourceBufferedImage = convertStringToBufferedImage(sourceContent);

      if (sourceBufferedImage != null) {
        int width = sourceBufferedImage.getWidth();
        int height = sourceBufferedImage.getHeight();
        int[][][] sourceRGBData = rgbDataMap.get(sourceName);

        if (sourceRGBData != null) {
          int[][][] redData = new int[height][width][3];
          int[][][] greenData = new int[height][width][3];
          int[][][] blueData = new int[height][width][3];

          for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
              int r = sourceRGBData[y][x][0];
              int g = sourceRGBData[y][x][1];
              int b = sourceRGBData[y][x][2];

              // Create grayscale values for each component
              redData[y][x][0] = r;
              redData[y][x][1] = r;
              redData[y][x][2] = r;

              greenData[y][x][0] = g;
              greenData[y][x][1] = g;
              greenData[y][x][2] = g;

              blueData[y][x][0] = b;
              blueData[y][x][1] = b;
              blueData[y][x][2] = b;
            }
          }

          // Store the separated RGB images in the rgbDataMap
          rgbDataMap.put(destNameRed, redData);
          rgbDataMap.put(destNameGreen, greenData);
          rgbDataMap.put(destNameBlue, blueData);

          // Serialize and store the separated images in the imageMap
          try {
            ImageContent destImageRed = new ImageContent(destNameRed, serializeImageData(redData));
            ImageContent destImageGreen = new ImageContent(destNameGreen, serializeImageData(greenData));
            ImageContent destImageBlue = new ImageContent(destNameBlue, serializeImageData(blueData));

            imageMap.put(destNameRed, destImageRed);
            imageMap.put(destNameGreen, destImageGreen);
            imageMap.put(destNameBlue, destImageBlue);

            System.out.println("Image '" + sourceName + "' split into red, green, and blue components and saved as '" + destNameRed + "', '" + destNameGreen + "', and '" + destNameBlue + "'");
          } catch (IOException e) {
            e.printStackTrace();
          }
        } else {
          System.out.println("Failed to split the image; invalid RGB data.");
        }
      } else {
        System.out.println("Failed to split the source image; invalid image content.");
      }
    } else {
      System.out.println("Source image not found: " + sourceName);
    }
  }

  @Override
  public void extractComponent(String sourceName, String destName, String component) {
    ImageContent sourceImage = imageMap.get(sourceName);

    if (sourceImage != null) {
      String sourceContent = sourceImage.getContent();
      BufferedImage sourceBufferedImage = convertStringToBufferedImage(sourceContent);

      if (sourceBufferedImage != null) {
        BufferedImage componentImage = new BufferedImage(sourceBufferedImage.getWidth(), sourceBufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < sourceBufferedImage.getHeight(); y++) {
          for (int x = 0; x < sourceBufferedImage.getWidth(); x++) {
            int rgb = sourceBufferedImage.getRGB(x, y);
            int extractedValue;

            switch (component) {
              case "red":
                extractedValue = (rgb >> 16) & 0xFF;
                break;
              case "green":
                extractedValue = (rgb >> 8) & 0xFF;
                break;
              case "blue":
                extractedValue = rgb & 0xFF;
                break;
              case "luma":
                // Calculate luma from RGB (standard BT.709 formula)
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                extractedValue = (int) (0.2126 * r + 0.7152 * g + 0.0722 * b);
                break;
              case "intensity":
                // Calculate intensity from RGB (average of RGB components)
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;
                extractedValue = (red + green + blue) / 3;
                break;
              case "value":
                // Extract value (brightness) from RGB (maximum of RGB components)
                int rValue = (rgb >> 16) & 0xFF;
                int gValue = (rgb >> 8) & 0xFF;
                int bValue = rgb & 0xFF;
                extractedValue = Math.max(rValue, Math.max(gValue, bValue));
                break;
              default:
                System.out.println("Invalid component parameter: " + component);
                return;
            }

            int newRGB = (extractedValue << 16) | (extractedValue << 8) | extractedValue;
            componentImage.setRGB(x, y, newRGB);
          }
        }

        // Convert the component image to a byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
          ImageIO.write(componentImage, "png", outputStream);
        } catch (IOException e) {
          e.printStackTrace();
        }
        String componentContent = Base64.getEncoder().encodeToString(outputStream.toByteArray());

        // Store the component image in the image map
        ImageContent destImage = new ImageContent(destName, componentContent);
        imageMap.put(destName, destImage);

        System.out.println(component + " component image created from '" + sourceName + "' and saved as '" + destName + "'");
      } else {
        System.out.println("Failed to extract the " + component + " component; invalid image content.");
      }
    } else {
      System.out.println("Source image not found: " + sourceName);
    }
  }

  private static int clamp(int value) {
    return Math.min(255, Math.max(0, value));
  }
}