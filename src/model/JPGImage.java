package model;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * The `Model.JPGImage` class extends the `Model.PNGImage`
 * class and provides functionality to save an image in the JPG format.
 * It converts RGB image data to a JPG image and saves it to a
 * specified file.
 */
public class JPGImage extends PNGImage {

  /**
   * Saves the image with the specified name in the JPG format at the given image path.
   *
   * @param imagePath The path where the image should be saved.
   * @param imageName The name of the image to be saved.
   * @throws IOException If an I/O error occurs while saving the image.
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
        ImageIO.write(bufferedImage, "jpg", output);
        System.out.println("Image saved as " + imagePath + " in the png format");

      } catch (Exception e) {
        System.out.println("Error in saving File");
      }


      //ImageIO.write(bufferedImage, "png", output);

    }
  }
}