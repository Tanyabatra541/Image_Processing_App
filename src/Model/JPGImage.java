package Model;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import Model.PNGImage;

/**
 * The `Model.JPGImage` class extends the `Model.PNGImage` class and provides functionality to save an image
 * in the JPG format. It converts RGB image data to a JPG image and saves it to a specified file.
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
  public void saveImage(String imagePath, String imageName) throws IOException {
    int[][][] rgbData = rgbDataMap.get(imageName);


    // Check if the filename is valid
    if (!isValidFileName(imageName)) {
      throw new IllegalArgumentException("Invalid filename: " + imageName);
    }

    if (rgbData != null) {
      BufferedImage bufferedImage = convertRGBDataToBufferedImage(rgbData);

      //String format = imagePath.substring(imagePath.lastIndexOf('.') + 1);

      // Check if the format is "png" before saving as PNG
      File output = new File(imagePath);



      if (output.getParentFile() != null && !output.getParentFile().exists()) {
        throw new IllegalArgumentException("Invalid path: " + imagePath);
      }


      if (!ImageIO.write(bufferedImage, "jpg", output)) {
        throw new IllegalArgumentException("Failed to save the image as " + imagePath);
      }

      //ImageIO.write(bufferedImage, "png", output);
      System.out.println("Image saved as " + imagePath + " in the png format");
    }
  }
}