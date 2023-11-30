package controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

import javax.imageio.ImageIO;

public class IOImageOperations {

  public int[][][] convertPNGToRGB(String imagePath) {
    System.out.println("convertPNGToRGB");
    int HEIGHT;
    int WIDTH;
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

      WIDTH = bufferedImage.getWidth();
      HEIGHT = bufferedImage.getHeight();

      int[][][] imageRGBData = new int[HEIGHT][WIDTH][3];

      for (int y = 0; y < HEIGHT; y++) {
        for (int x = 0; x < WIDTH; x++) {
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

  public int[][][] readImageRGBData(String filename) {
    Scanner sc = null;

    try {
      sc = new Scanner(new FileInputStream(filename));
    } catch (FileNotFoundException e) {
      System.out.println("File " + filename + " not found!");
    }

    StringBuilder builder = new StringBuilder();
    while (sc.hasNextLine()) {
      String s = sc.nextLine();
      if (s.charAt(0) != '#') {
        builder.append(s).append(System.lineSeparator());
      }
    }

    sc = new Scanner(builder.toString());

    String token = sc.next();
    if (!token.equals("P3")) {
      System.out.println("Invalid PPM file: plain RAW file should begin with P3");
    }
    int width = sc.nextInt();
    int height = sc.nextInt();

    int[][][] imageRGBData = new int[height][width][3];
    int maxValue = sc.nextInt(); // Read the maximum color value

    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        imageRGBData[i][j][0] = sc.nextInt(); // Red component
        imageRGBData[i][j][1] = sc.nextInt(); // Green component
        imageRGBData[i][j][2] = sc.nextInt(); // Blue component
      }
    }
    return imageRGBData;
  }

  public int[][][] load(String imagePath, String extension) {

    if (Objects.equals(extension, "png") || Objects.equals(extension, "jpg")) {
      System.out.println("in png or jpg");
      return convertPNGToRGB(imagePath);
    } else {
      System.out.println("in ppm");
      return readImageRGBData(imagePath);
    }
  }

  /**
   * Saves an image as a PNG file to the specified file path.
   *
   * @param imagePath The file path where the PNG image should be saved.
   * @param imageName The name of the image to be saved.
   */
  public void save(String imagePath, String imageName, String extension, int[][][] rgbData, double[][] pixels) {
    System.out.println("Saving" + extension);
    if (extension.equalsIgnoreCase("png") || extension.equalsIgnoreCase("jpg")
            || extension.equalsIgnoreCase("jpeg")) {
      savePNG(imagePath, imageName, extension, rgbData, pixels);
    } else if (extension.equalsIgnoreCase("ppm")) {
      savePPM(imagePath, rgbData, pixels);
    }
  }

  private String convertToPPMFormat(int[][][] imageRGBData) {
    StringBuilder ppmContent = new StringBuilder();
    int height = imageRGBData.length;
    int width = imageRGBData[0].length;

    ppmContent.append("P3\n");
    ppmContent.append(width + " " + height + "\n");
    ppmContent.append("255\n");

    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        ppmContent.append(imageRGBData[i][j][0] + " "); // Red component
        ppmContent.append(imageRGBData[i][j][1] + " "); // Green component
        ppmContent.append(imageRGBData[i][j][2] + " "); // Blue component
      }
      ppmContent.append("\n");
    }

    return ppmContent.toString();
  }

  private void savePPM(String imagePath, int[][][] rgbData, double[][] pixels) {
    //ImageContent image = IMAGE_MAP.get(imageName);
//    ImageContent image =


//   if (rgb != null) {
    String content = convertToPPMFormat(rgbData);

    // Create a file and write the image content to it
    File file = new File(imagePath);

    try {
      java.io.FileWriter fileWriter = new java.io.FileWriter(file);
      fileWriter.write(content);
      fileWriter.write(content);
      fileWriter.close();
      System.out.println("Image saved as " + imagePath + " in the ppm format");


    } catch (IOException e) {
      System.out.println("Error in saving File");
    }

  }


  private void savePNG(String imagePath, String imageName, String extension, int[][][] rgbData, double[][] pixels) {
    System.out.println("SavingPNG");
    BufferedImage bufferedImage;

    if (rgbData != null) {
      if (pixels != null) {
        bufferedImage = convertRGBAndPixelsDataToBufferedImage(rgbData, pixels);
      } else {
        bufferedImage = convertRGBDataToBufferedImage(rgbData);
      }
      File output = new File(imagePath);
      System.out.println("Saviweng");
      try {
        javax.imageio.ImageIO.write(bufferedImage, extension, output);
        System.out.println("Image saved as " + imagePath + " in the png format");
      } catch (Exception e) {
        System.out.println("Error in saving File");
        e.printStackTrace(); // Print the stack trace for better error diagnostics
      }
    } else {
      System.out.println("RGB data is null for image: " + imageName);
    }
  }


  /**
   * Converts RGB data and corresponding pixel values to a BufferedImage.
   *
   * @param rgbData The 3D array representing the RGB values of an image.
   * @param pixels  The 2D array representing pixel values of the same image.
   * @return A BufferedImage generated from the provided RGB data and pixel values.
   */
  protected BufferedImage convertRGBAndPixelsDataToBufferedImage(int[][][] rgbData,
                                                                 double[][] pixels) {
    int height = rgbData.length;
    int width = rgbData[0].length;

    BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int r = rgbData[y][x][0];
        int g = rgbData[y][x][1];
        int b = rgbData[y][x][2];

        int grayValue = (int) (pixels[y][x] * 255); // Scale to 0-255 range

        int rgb = (r << 16) | (g << 8) | b;
        rgb = (rgb & 0xFF000000) | (grayValue << 16) | (grayValue << 8) | grayValue;

        bufferedImage.setRGB(x, y, rgb);
      }
    }

    return bufferedImage;
  }

  /**
   * Converts RGB image data represented as a three-dimensional array into a `BufferedImage`.
   *
   * @param rgbData The three-dimensional array representing the RGB image data.
   * @return A `BufferedImage` object containing the image data.
   */
  protected BufferedImage convertRGBDataToBufferedImage(int[][][] rgbData) {
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

}
