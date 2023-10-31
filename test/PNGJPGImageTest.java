import org.junit.Before;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PNGJPGImageTest {
  private static PNGJPGImage pngJpgImage;
  private static final String imageName = "output";
  private static final String imagePath = "output.png";

  int[][][] rgbMatrix = {
          {{255, 0, 0},
                  {0, 255, 0}},
          {{0, 0, 255},
                  {255, 255, 255}}
  };

  //private PNGJPGImage pngJpgImage;

  @Before
  public void setUp() {
    pngJpgImage = new PNGJPGImage();
    createAndSavePNG(rgbMatrix, imagePath);
  }

  @Test
  public void testLoadImageAndPrintRGB() throws IOException {
    // Load the image
    pngJpgImage.loadImage(imagePath, imageName);

    // Check if the image was loaded successfully
    assertTrue(pngJpgImage.getImageMap().containsKey(imageName));

    // Get the RGB data
    int[][][] rgbData = pngJpgImage.getRgbDataMap().get(imageName);

    // Print the RGB data
    for (int y = 0; y < rgbData.length; y++) {
      for (int x = 0; x < rgbData[y].length; x++) {
        int r = rgbData[y][x][0];
        int g = rgbData[y][x][1];
        int b = rgbData[y][x][2];
        System.out.println("RGB at (" + x + ", " + y + "): R=" + r + " G=" + g + " B=" + b);
        assertEquals(rgbMatrix[y][x][0], r);
        assertEquals(rgbMatrix[y][x][1], g);
        assertEquals(rgbMatrix[y][x][2], b);
      }
    }
  }


  public static void createAndSavePNG(int[][][] rgbMatrix, String filePath) {
    int width = rgbMatrix[0].length;
    int height = rgbMatrix.length;

    // Create a BufferedImage with the specified width and height
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int r = rgbMatrix[y][x][0];
        int g = rgbMatrix[y][x][1];
        int b = rgbMatrix[y][x][2];

        // Create an RGB color from the values
        int rgb = (r << 16) | (g << 8) | b;

        // Set the pixel color in the BufferedImage
        image.setRGB(x, y, rgb);
      }
    }

    try {
      // Save the BufferedImage as a PNG image at the specified file path
      File output = new File(filePath);
      ImageIO.write(image, "png", output);
      System.out.println("Image saved as " + filePath);
      pngJpgImage.loadImage(imagePath,imageName);
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("Failed to save the image as " + filePath);
    }
  }


  @Test
  public void testHorizontalFlipImage() throws IOException {
    // Perform a horizontal flip
    pngJpgImage.horizontalFlipImage(imageName, "horizontal-img");

    // Get the flipped image data
    int[][][] flippedImageData = pngJpgImage.getRgbDataMap().get("horizontal-img");

    // Check if the flipped image matches the expected result
    int[][][] expectedFlippedImageData = new int[2][2][3];
    expectedFlippedImageData[0][0] = new int[]{0, 255, 0};
    expectedFlippedImageData[0][1] = new int[]{255, 0, 0};
    expectedFlippedImageData[1][0] = new int[]{255, 255, 255};
    expectedFlippedImageData[1][1] = new int[]{0, 0, 255};


    for (int y = 0; y < expectedFlippedImageData.length; y++) {
      for (int x = 0; x < expectedFlippedImageData[y].length; x++) {
        for (int c = 0; c < 3; c++) {
          assertEquals(expectedFlippedImageData[y][x][c], flippedImageData[y][x][c]);
        }
      }
    }
  }

  @Test
  public void testVerticalFlipImage() throws IOException {
    // Perform a vertical flip
    pngJpgImage.verticalFlipImage(imageName, "vertical-img");

    // Get the flipped image data
    int[][][] flippedImageData = pngJpgImage.getRgbDataMap().get("vertical-img");

    // Check if the flipped image matches the expected result
    int[][][] expectedFlippedImageData = new int[2][2][3];
    expectedFlippedImageData[0][0] = new int[]{0, 0, 255};
    expectedFlippedImageData[0][1] = new int[]{255, 255, 255};
    expectedFlippedImageData[1][0] = new int[]{255, 0, 0};
    expectedFlippedImageData[1][1] = new int[]{0, 255, 0};

    for (int y = 0; y < expectedFlippedImageData.length; y++) {
      for (int x = 0; x < expectedFlippedImageData[y].length; x++) {
        for (int c = 0; c < 3; c++) {
          assertEquals(expectedFlippedImageData[y][x][c], flippedImageData[y][x][c]);
        }
      }
    }
  }

  @Test
  public void testBrightenImage() throws IOException {
    // Perform a brightness adjustment
    pngJpgImage.brightenImage(imageName, "brightened-img", 50); // Increase brightness by 50 (you can adjust the increment)

    // Get the brightened image data
    int[][][] brightenedImageData = pngJpgImage.getRgbDataMap().get("brightened-img");

    // Check if the brightened image matches the expected result
    // You need to define expected RGB values after brightening with an increment of 50.
    int[][][] expectedBrightenedImageData = new int[2][2][3];
    expectedBrightenedImageData[0][0] = new int[]{255, 0, 0};
    expectedBrightenedImageData[0][1] = new int[]{0, 255, 0};
    expectedBrightenedImageData[1][0] = new int[]{0, 0, 255};
    expectedBrightenedImageData[1][1] = new int[]{255, 255, 255};

    for (int y = 0; y < expectedBrightenedImageData.length; y++) {
      for (int x = 0; x < expectedBrightenedImageData[y].length; x++) {
        for (int c = 0; c < 3; c++) {
          assertEquals(expectedBrightenedImageData[y][x][c], brightenedImageData[y][x][c]);
        }
      }
    }
  }

}
