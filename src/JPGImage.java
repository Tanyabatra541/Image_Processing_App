import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * The `JPGImage` class extends the `PNGImage` class and provides functionality to save an image
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

    if (rgbData != null) {
      BufferedImage bufferedImage = convertRGBDataToBufferedImage(rgbData);

      File output = new File(imagePath);
      ImageIO.write(bufferedImage, "jpg", output);

      System.out.println("Image saved as " + imagePath + " in the jpg format");
    }
  }
}

