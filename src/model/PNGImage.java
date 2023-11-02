package model;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

import javax.imageio.ImageIO;

/**
 * The `PNGImage` class extends `AbstractImage` and provides specific functionality for
 * working with PNG image files. It includes methods for loading PNG images and saving
 * images in the PNG format.
 */
public class PNGImage extends AbstractImage {

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


  /**
   * Converts RGB image data represented as a three-dimensional array into a `BufferedImage`.
   *
   * @param rgbData The three-dimensional array representing the RGB image data.
   * @return A `BufferedImage` object containing the image data.
   */
  public static BufferedImage convertRGBDataToBufferedImage(int[][][] rgbData) {
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


  /**
   * Loads a PNG image from the specified file path and stores it in the image map and RGB data map.
   *
   * @param imagePath The file path of the PNG image to load.
   * @param imageName The name to associate with the loaded image.
   * @throws IOException If an I/O error occurs during the loading process.
   */
  @Override
  public void loadImage(String imagePath, String imageName) throws IOException {
    int[][][] imageRGBData = null;

    imageRGBData = convertPNGToRGB(imagePath);


    if (imageRGBData != null) {
      ImageContent image = new ImageContent(imageName, serializeImageData(imageRGBData));
      imageMap.put(imageName, image);
      rgbDataMap.put(imageName, imageRGBData);
      System.out.println("Loaded image: " + imageName);
    } else {
      System.out.println("Failed to load the image from: " + imagePath);
    }
  }

  private int[][][] convertPNGToRGB(String imagePath) {
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
      System.out.println("Error while converting PNG to RGB: " + imagePath);
      return null;
    }
  }

  /**
   * Saves an image as a PNG file to the specified file path.
   *
   * @param imagePath The file path where the PNG image should be saved.
   * @param imageName The name of the image to be saved.
   */
  @Override
  public void saveImage(String imagePath, String imageName)  {
    int[][][] rgbData = rgbDataMap.get(imageName);


    if (rgbData != null) {
      BufferedImage bufferedImage = convertRGBDataToBufferedImage(rgbData);

      //String format = imagePath.substring(imagePath.lastIndexOf('.') + 1);

      // Check if the format is "png" before saving as PNG
      File output = new File(imagePath);



      try {
        ImageIO.write(bufferedImage, "png", output);
        System.out.println("Image saved as " + imagePath + " in the png format");

      } catch (Exception e) {
        System.out.println("Error in saving File");
      }


      //ImageIO.write(bufferedImage, "png", output);

    }
  }
}