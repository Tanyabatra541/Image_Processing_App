import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class JPGImage extends PNGImage {

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

