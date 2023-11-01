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
  private static String imageName = "output";
  private static String imagePath = "output.png";


  @Before
  public void setUp() {
    pngJpgImage = new PNGJPGImage();
//    createAndSavePNG(rgbMatrix, imagePath);
  }

  @Test
  public void testLoadImageAndPrintRGB() throws IOException {
    imageName = "manhattan_3x3";
    imagePath = "/Users/tanyabatra/Desktop/pdp/manhattan-3x3.png";
    // Load the image
    pngJpgImage.loadImage(imagePath, imageName);

    // Check if the image was loaded successfully
    assertTrue(pngJpgImage.getImageMap().containsKey(imageName));

    // Get the RGB data
    int[][][] rgbData = pngJpgImage.getRgbDataMap().get(imageName);

    int[][][] rgbMatrix = new int[3][3][3];
    //Calculate the rgb values
    rgbMatrix[0][0] = new int[]{0, 0, 0};
    rgbMatrix[0][1] = new int[]{0, 0, 0};
    rgbMatrix[0][2] = new int[]{0, 0, 0};
    rgbMatrix[1][0] = new int[]{156, 167, 176};
    rgbMatrix[1][1] = new int[]{140, 149, 158};
    rgbMatrix[1][2] = new int[]{134, 167, 176};
    rgbMatrix[2][0] = new int[]{0, 0, 0};
    rgbMatrix[2][1] = new int[]{0, 0, 0};
    rgbMatrix[2][2] = new int[]{0, 0, 0};

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

//  public static void createAndSavePNG(int[][][] rgbMatrix, String filePath) {
//
//    int width = rgbMatrix[0].length;
//    int height = rgbMatrix.length;
//
//    // Create a BufferedImage with the specified width and height
//    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//
//    for (int y = 0; y < height; y++) {
//      for (int x = 0; x < width; x++) {
//        int r = rgbMatrix[y][x][0];
//        int g = rgbMatrix[y][x][1];
//        int b = rgbMatrix[y][x][2];
//
//        // Create an RGB color from the values
//        int rgb = (r << 16) | (g << 8) | b;
//
//        // Set the pixel color in the BufferedImage
//        image.setRGB(x, y, rgb);
//      }
//    }
//
//    try {
//      // Save the BufferedImage as a PNG image at the specified file path
//      File output = new File(filePath);
//      ImageIO.write(image, "png", output);
//      System.out.println("Image saved as " + filePath);
//      pngJpgImage.loadImage(imagePath, imageName);
//    } catch (IOException e) {
//      e.printStackTrace();
//      System.out.println("Failed to save the image as " + filePath);
//    }
//  }


  @Test
  public void testVerticalFlipImage() throws IOException {

    imageName = "manhattan_3x3";
    imagePath = "/Users/tanyabatra/Desktop/pdp/manhattan-3x3.png";
    // Perform a vertical flip
    pngJpgImage.loadImage(imagePath, imageName);

    // Get the original image data

    // Perform a horizontal flip
    pngJpgImage.verticalFlipImage(imageName, "manhattan_vertical-img");

    // Get the flipped image data
    int[][][] flippedImageData = pngJpgImage.getRgbDataMap().get("manhattan_vertical-img");

    // Check if the flipped image matches the expected result
    int[][][] expectedFlippedImageData = new int[3][3][3];

    expectedFlippedImageData[0][0] = new int[]{0, 0, 0};
    expectedFlippedImageData[0][1] = new int[]{0, 0, 0};
    expectedFlippedImageData[0][2] = new int[]{0, 0, 0};
    expectedFlippedImageData[1][0] = new int[]{156, 167, 176};
    expectedFlippedImageData[1][1] = new int[]{140, 149, 158};
    expectedFlippedImageData[1][2] = new int[]{134, 143, 153};
    expectedFlippedImageData[2][0] = new int[]{0, 0, 0};
    expectedFlippedImageData[2][1] = new int[]{0, 0, 0};
    expectedFlippedImageData[2][2] = new int[]{0, 0, 0};

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

    imageName = "manhattan_3x3";
    imagePath = "/Users/tanyabatra/Desktop/pdp/manhattan-3x3.png";
    // Perform a vertical flip
    pngJpgImage.loadImage(imagePath, imageName);

    // Get the original image data

    // Perform a horizontal flip
    pngJpgImage.brightenImage(imageName, "manhattan_vertical-img", 50);

    // Get the flipped image data
    int[][][] brightenedImageData = pngJpgImage.getRgbDataMap().get("manhattan_vertical-img");

    // Check if the flipped image matches the expected result
    int[][][] expectedBrightenedImageData = new int[3][3][3];

    expectedBrightenedImageData[0][0] = new int[]{50, 50, 50};
    expectedBrightenedImageData[0][1] = new int[]{50, 50, 50};
    expectedBrightenedImageData[0][2] = new int[]{50, 50, 50};
    expectedBrightenedImageData[1][0] = new int[]{206, 217, 226};
    expectedBrightenedImageData[1][1] = new int[]{190, 199, 208};
    expectedBrightenedImageData[1][2] = new int[]{184, 193, 203};
    expectedBrightenedImageData[2][0] = new int[]{50, 50, 50};
    expectedBrightenedImageData[2][1] = new int[]{50, 50, 50};
    expectedBrightenedImageData[2][2] = new int[]{50, 50, 50};

    for (int y = 0; y < expectedBrightenedImageData.length; y++) {
      for (int x = 0; x < expectedBrightenedImageData[y].length; x++) {
        for (int c = 0; c < 3; c++) {
          System.out.println("Expected: " + expectedBrightenedImageData[y][x][c] + " Actual: " + brightenedImageData[y][x][c]);
          assertEquals(expectedBrightenedImageData[y][x][c], brightenedImageData[y][x][c]);
        }
      }
    }
  }

  @Test
  public void testHorizontalFlipImage() throws IOException {

    imageName = "manhattan_3x3";
    imagePath = "/Users/tanyabatra/Desktop/pdp/manhattan-3x3.png";

    // load the image
    pngJpgImage.loadImage(imagePath, imageName);


    // Perform a horizontal flip
    pngJpgImage.horizontalFlipImage(imageName, "manhattan_horizontal-img");

    // Get the flipped image data
    int[][][] flippedImageData = pngJpgImage.getRgbDataMap().get("manhattan_horizontal-img");

    // Check if the flipped image matches the expected result
    int[][][] expectedFlippedImageData = new int[3][3][3];

    //Print original image RGB values
    System.out.println("Original Image RGB values");
    for (int y = 0; y < flippedImageData.length; y++) {
      for (int x = 0; x < flippedImageData[y].length; x++) {
        for (int c = 0; c < 3; c++) {
          System.out.println("RGB at (" + x + ", " + y + "): R=" + flippedImageData[y][x][0] + " G=" + flippedImageData[y][x][1] + " B=" + flippedImageData[y][x][2]);
        }
      }
    }


    expectedFlippedImageData[0][0] = new int[]{0, 0, 0};
    expectedFlippedImageData[0][1] = new int[]{0, 0, 0};
    expectedFlippedImageData[0][2] = new int[]{0, 0, 0};
    expectedFlippedImageData[1][0] = new int[]{134, 143, 153};
    expectedFlippedImageData[1][1] = new int[]{140, 149, 158};
    expectedFlippedImageData[1][2] = new int[]{156, 167, 176};
    expectedFlippedImageData[2][0] = new int[]{0, 0, 0};
    expectedFlippedImageData[2][1] = new int[]{0, 0, 0};
    expectedFlippedImageData[2][2] = new int[]{0, 0, 0};


    for (int y = 0; y < expectedFlippedImageData.length; y++) {
      for (int x = 0; x < expectedFlippedImageData[y].length; x++) {
        for (int c = 0; c < 3; c++) {
          assertEquals(expectedFlippedImageData[y][x][c], flippedImageData[y][x][c]);
        }
      }
    }
  }


  @Test
  public void testSharpenImage() throws IOException {

    imagePath = "/Users/tanyabatra/Desktop/pdp/Koala_1x5.png";
    imageName = "Koala_1x5";


    // Load the original image
    pngJpgImage.loadImage(imagePath, imageName);

    // Apply the sharpening filter
    pngJpgImage.sharpenImage(imageName, "sharpened-img");

    // Get the sharpened image data
    int[][][] sharpenedImageData = pngJpgImage.getRgbDataMap().get("sharpened-img");

    // Define the expected sharpened image data
    int[][][] expectedSharpenedImageData = new int[2][2][3];

    //Print original image RGB values
    System.out.println("Original Image RGB values");

    for (int y = 0; y < sharpenedImageData.length; y++) {
      for (int x = 0; x < sharpenedImageData[y].length; x++) {
        for (int c = 0; c < 3; c++) {
          System.out.println("RGB at (" + x + ", " + y + "): R=" + sharpenedImageData[y][x][0] + " G=" + sharpenedImageData[y][x][1] + " B=" + sharpenedImageData[y][x][2]);
        }
      }
    }






    // Define the expected RGB values after sharpening (you may need to calculate this manually or using an image processing library)

//    for (int y = 0; y < expectedSharpenedImageData.length; y++) {
//      for (int x = 0; x < expectedSharpenedImageData[y].length; x++) {
//        for (int c = 0; c < 3; c++) {
//          // Set the expected values
//          // expectedSharpenedImageData[y][x][c] = ...
//          expectedSharpenedImageData[0][0] = new int[]{0, 0, 0};
//          expectedSharpenedImageData[0][1] = new int[]{0, 0, 0};
//          expectedSharpenedImageData[1][0] = new int[]{0, 50, 255};
//          expectedSharpenedImageData[1][1] = new int[]{255, 255, 255};
//
//          // Ensure that the actual value matches the expected value
//          System.out.println("Expected: " + expectedSharpenedImageData[y][x][c] + " Actual: " + sharpenedImageData[y][x][c]);
//          assertEquals(expectedSharpenedImageData[y][x][c], sharpenedImageData[y][x][c]);
//        }
//      }
//    }
  }


}
