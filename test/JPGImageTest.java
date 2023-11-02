import org.junit.Before;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import model.JPGImage;

import static org.junit.Assert.*;

public class JPGImageTest {

  private static JPGImage pngJpgImage;
  private static String imageName = "outputJPG";
  private static String imagePath = "outputJPG.jpg";

  private static String image2Name = "outputJPG2";
  private static String image2Path = "outputJPG2.jpg";

  int[][][] rgbMatrix = {
          { {255, 0, 0}, {0, 255, 0} },
          { {0, 0, 255}, {255, 255, 255} }
  };

  int[][][] rgbMatrix2 = {
          {{128, 64, 192}, {255, 128, 32}, {64, 192, 128}, {32, 96, 255}, {192, 64, 128}, {160, 32, 192}, {96, 255, 128}, {224, 64, 32}, {128, 160, 160}, {192, 128, 96}},
          {{96, 224, 160}, {128, 192, 64}, {160, 255, 32}, {224, 64, 160}, {255, 128, 96}, {32, 192, 96}, {128, 64, 224}, {160, 32, 128}, {96, 160, 64}, {160, 224, 255}},
          {{32, 192, 96}, {128, 64, 224}, {160, 32, 128}, {96, 160, 64}, {160, 224, 255}, {128, 64, 192}, {255, 128, 32}, {64, 192, 128}, {32, 96, 255}, {192, 64, 128}},
          {{160, 32, 192}, {96, 255, 128}, {224, 64, 32}, {128, 160, 160}, {192, 128, 96}, {96, 224, 160}, {128, 192, 64}, {160, 255, 32}, {224, 64, 160}, {255, 128, 96}},
          {{64, 128, 96}, {255, 160, 192}, {96, 96, 128}, {192, 32, 128}, {32, 192, 255}, {128, 64, 192}, {255, 128, 32}, {64, 192, 128}, {32, 96, 255}, {192, 64, 128}},
          {{192, 64, 128}, {160, 32, 192}, {96, 255, 128}, {224, 64, 32}, {128, 160, 160}, {32, 192, 96}, {128, 64, 224}, {160, 32, 128}, {96, 160, 64}, {160, 224, 255}},
          {{96, 255, 128}, {224, 64, 32}, {128, 160, 160}, {192, 128, 96}, {96, 224, 160}, {128, 192, 64}, {160, 255, 32}, {224, 64, 160}, {255, 128, 96}, {32, 192, 96}},
          {{224, 64, 32}, {128, 160, 160}, {192, 128, 96}, {96, 224, 160}, {128, 192, 64}, {160, 255, 32}, {224, 64, 160}, {255, 128, 96}, {32, 192, 96}, {128, 64, 224}},
          {{128, 160, 160}, {192, 128, 96}, {96, 224, 160}, {128, 192, 64}, {160, 255, 32}, {224, 64, 160}, {255, 128, 96}, {32, 192, 96}, {128, 64, 224}, {160, 32, 128}},
          {{192, 128, 96}, {96, 224, 160}, {128, 192, 64}, {160, 255, 32}, {224, 64, 160}, {255, 128, 96}, {32, 192, 96}, {128, 64, 224}, {160, 32, 128}, {96, 255, 128}}
  };

  @Before
  public void setUp() {
    pngJpgImage = new JPGImage();
    createAndSaveJPG(rgbMatrix, imageName, imagePath);
    createAndSaveJPG(rgbMatrix2,image2Name,image2Path);
  }

  public static void createAndSaveJPG(int[][][] matrix, String fileName, String filePath) {
    int width = matrix[0].length;
    int height = matrix.length;

    // Create a BufferedImage with the specified width and height
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int r = matrix[y][x][0];
        int g = matrix[y][x][1];
        int b = matrix[y][x][2];

        // Create an RGB color from the values
        int rgb = (r << 16) | (g << 8) | b;

        // Set the pixel color in the BufferedImage
        image.setRGB(x, y, rgb);
      }
    }

    try {
      // Save the BufferedImage as a JPG image at the specified file path
      File output = new File(filePath);
      ImageIO.write(image, "jpg", output);
      System.out.println("Image saved as " + filePath);
      pngJpgImage.loadImage(filePath, fileName);
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("Failed to save the image as " + filePath);
    }
  }


  @Test
  public void testLoadImageAndPrintRGB() throws IOException {
    // Load the image
    pngJpgImage.loadImage(imagePath, imageName);

    // Check if the image was loaded successfully
    assertTrue(pngJpgImage.getImageMap().containsKey(imageName));
    // Get the RGB data
    int[][][] rgbData = pngJpgImage.getRgbDataMap().get(imageName);

    // Check if the dimensions match
    assertEquals(rgbMatrix.length, rgbData.length);
    assertEquals(rgbMatrix[0].length, rgbData[0].length);

    // Print the RGB data
    for (int y = 0; y < rgbData.length; y++) {
      for (int x = 0; x < rgbData[y].length; x++) {
        int r = rgbData[y][x][0];
        int g = rgbData[y][x][1];
        int b = rgbData[y][x][2];
        System.out.println("RGB at (" + x + ", " + y + "): R=" + r + " G=" + g + " B=" + b);
      }
    }
  }




}