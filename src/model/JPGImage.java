package model;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

/**
 * The `JPGImage` class extends the `PNGImage` class and provides functionality to save an image
 * in the JPG format.
 */
public class JPGImage extends PNGImage {

  /**
   * Saves the image with the specified name in the JPG format at the given image path.
   *
   * @param imagePath The path where the image should be saved.
   * @param imageName The name of the image to be saved.
   */
  @Override
  public void saveImage(String imagePath, String imageName)  {
    int[][][] rgbData = IMAGE_MAP.get(imageName).getRgbDataMap();
    if (rgbData != null) {
      BufferedImage bufferedImage = convertRGBDataToBufferedImage(rgbData);
      File output = new File(imagePath);
      try {
        ImageIO.write(bufferedImage, "jpg", output);
        System.out.println("Image saved as " + imagePath + " in the png format");

      } catch (Exception e) {
        System.out.println("Error in saving File");
      }


    }
  }
}