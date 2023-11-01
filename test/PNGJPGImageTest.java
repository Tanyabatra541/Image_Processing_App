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

  private static String image2Name = "output2";
  private static String image2Path = "output2.png";

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
    pngJpgImage = new PNGJPGImage();
    createAndSavePNG(rgbMatrix, imageName, imagePath);
    createAndSavePNG(rgbMatrix2,image2Name,image2Path);
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

  public static void createAndSavePNG(int[][][] matrix,String fileName, String filePath) {

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
      // Save the BufferedImage as a PNG image at the specified file path
      File output = new File(filePath);
      ImageIO.write(image, "png", output);
      System.out.println("Image saved as " + filePath);
      pngJpgImage.loadImage(filePath, fileName);
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("Failed to save the image as " + filePath);
    }
  }


  @Test
  public void testVerticalFlipImage() throws IOException {

    // Perform vertical-flip on the image
    pngJpgImage.verticalFlipImage(imageName, "vertical-flip-img");

    // Get the flipped image data
    int[][][] flippedImageData = pngJpgImage.getRgbDataMap().get("vertical-flip-img");

    // Check if the flipped image matches the expected result
    int[][][] expectedFlippedImageData  = new int[2][2][3];
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


    pngJpgImage.brightenImage(imageName, "brighten-img", 50); // Increase brightness by 50 (you can adjust the increment)

    int[][][] brightenedImageData = pngJpgImage.getRgbDataMap().get("brighten-img");

    // Check if the brightened image matches the expected result
    // You need to define expected RGB values after brightening with an increment of 50.
    int[][][] expectedBrightenedImageData = new int[2][2][3];
    expectedBrightenedImageData[0][0] = new int[]{255, 50, 50};
    expectedBrightenedImageData[0][1] = new int[]{50, 255, 50};
    expectedBrightenedImageData[1][0] = new int[]{50, 50, 255};
    expectedBrightenedImageData[1][1] = new int[]{255, 255, 255};


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
  public void testDarkenImage() throws IOException {
    // Perform a brightness adjustment
    pngJpgImage.brightenImage(imageName, "darken-img", -50); // Increase brightness by 50 (you can adjust the increment)

    // Get the brightened image data
    int[][][] brightenedImageData = pngJpgImage.getRgbDataMap().get("darken-img");

    // Check if the brightened image matches the expected result
    // You need to define expected RGB values after brightening with an increment of 50.
    int[][][] expectedBrightenedImageData = new int[2][2][3];
    expectedBrightenedImageData[0][0] = new int[]{205, 0, 0};
    expectedBrightenedImageData[0][1] = new int[]{0, 205, 0};
    expectedBrightenedImageData[1][0] = new int[]{0, 0, 205};
    expectedBrightenedImageData[1][1] = new int[]{205, 205, 205};

    for (int y = 0; y < expectedBrightenedImageData.length; y++) {
      for (int x = 0; x < expectedBrightenedImageData[y].length; x++) {
        for (int c = 0; c < 3; c++) {

          assertEquals(expectedBrightenedImageData[y][x][c], brightenedImageData[y][x][c]);
        }
      }
    }
  }

  @Test
  public void testHorizontalFlipImage() throws IOException {


    // Perform a horizontal flip
    pngJpgImage.horizontalFlipImage(imageName, "horizontal-flip-img");

    // Get the flipped image data
    int[][][] flippedImageData = pngJpgImage.getRgbDataMap().get("horizontal-flip-img");

    // Check if the flipped image matches the expected result
    int[][][] expectedFlippedImageData  = new int[2][2][3];
    expectedFlippedImageData[0][0] = new int[]{0, 255, 0};
    expectedFlippedImageData[0][1] = new int[]{255, 0, 0};
    expectedFlippedImageData[1][0] = new int[]{255, 255, 255};
    expectedFlippedImageData[1][1] = new int[]{0, 0, 255};




    for (int y = 0; y < expectedFlippedImageData.length; y++) {
      for (int x = 0; x < expectedFlippedImageData[y].length; x++) {
        for (int c = 0; c < 3; c++) {
          //System.out.println(flippedImageData[y][x][c]);
          assertEquals(expectedFlippedImageData[y][x][c], flippedImageData[y][x][c]);

        }
      }
    }
  }


  @Test
  public void testSharpenImage() throws IOException {

    // Perform sharpening on the image
    pngJpgImage.sharpenImage(image2Name, "sharp-img");

    // Get the sharpened image data
    int[][][] sharpenedImageData = pngJpgImage.getRgbDataMap().get("sharp-img");

    // Define the expected RGB values for the sharpened image
    int[][][] expectedSharpenedImageData = {
            {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}},
            {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}},
            {{0, 0, 0}, {0, 0, 0}, {180, 75, 40}, {220, 167, 0}, {160, 255, 251}, {163, 136, 180}, {195, 175, 0}, {68, 179, 84}, {0, 0, 0}, {0, 0, 0}},
            {{0, 0, 0}, {0, 0, 0}, {223, 12, 52}, {132, 96, 163}, {116, 148, 239}, {143, 255, 191}, {179, 255, 0}, {155, 255, 0}, {0, 0, 0}, {0, 0, 0}},
            {{0, 0, 0}, {0, 0, 0}, {179, 75, 92}, {196, 31, 83}, {8, 152, 255}, {71, 88, 239}, {203, 135, 0}, {68, 155, 104}, {0, 0, 0}, {0, 0, 0}},
            {{0, 0, 0}, {0, 0, 0}, {191, 135, 112}, {168, 64, 103}, {80, 108, 199}, {0, 220, 163}, {136, 12, 188}, {171, 0, 131}, {0, 0, 0}, {0, 0, 0}},
            {{0, 0, 0}, {0, 0, 0}, {180, 108, 136}, {172, 192, 88}, {56, 255, 80}, {64, 255, 64}, {232, 198, 8}, {255, 51, 108}, {0, 0, 0}, {0, 0, 0}},
            {{0, 0, 0}, {0, 0, 0}, {184, 124, 112}, {56, 255, 160}, {116, 255, 32}, {204, 255, 0}, {255, 115, 88}, {255, 132, 68}, {0, 0, 0}, {0, 0, 0}},
            {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}},
            {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}}
    };

    for (int y = 0; y < sharpenedImageData.length; y++) {
      for (int x = 0; x < sharpenedImageData[y].length; x++) {
        for (int c = 0; c < 3; c++) {
          //System.out.print(sharpenedImageData[y][x][c]+ " ");
          assertEquals(expectedSharpenedImageData[y][x][c], sharpenedImageData[y][x][c]);
        }
      }
      System.out.println(" ");
    }
  }

  @Test
  public void testBlurImage() {
    // Perform blurring on the image
    pngJpgImage.blurImage(image2Name, "blurred-img");

    // Get the blurred image data
    int[][][] blurredImageData = pngJpgImage.getRgbDataMap().get("blurred-img");

    // Check if the blurred image matches the expected result
    int[][][] expectedBlurredImageData = {
            {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}},
            {{0, 0, 0}, {135, 161, 106}, {143, 151, 103}, {159, 127, 135}, {165, 122, 147}, {135, 125, 145}, {135, 113, 138}, {135, 107, 123}, {126, 132, 139}, {0, 0, 0}},
            {{0, 0, 0}, {124, 135, 134}, {152, 117, 108}, {159, 135, 119}, {153, 156, 155}, {143, 144, 145}, {147, 139, 110}, {127, 141, 119}, {125, 123, 155}, {0, 0, 0}},
            {{0, 0, 0}, {141, 131, 140}, {157, 109, 114}, {148, 122, 127}, {134, 152, 163}, {139, 154, 143}, {151, 171, 88}, {135, 171, 103}, {147, 119, 151}, {0, 0, 0}},
            {{0, 0, 0}, {157, 129, 146}, {157, 119, 124}, {148, 109, 129}, {118, 136, 163}, {119, 142, 149}, {147, 139, 110}, {127, 141, 119}, {125, 123, 155}, {0, 0, 0}},
            {{0, 0, 0}, {159, 115, 140}, {153, 131, 124}, {154, 129, 115}, {120, 150, 137}, {105, 157, 133}, {141, 123, 126}, {147, 105, 129}, {131, 132, 139}, {0, 0, 0}},
            {{0, 0, 0}, {164, 123, 112}, {158, 141, 118}, {150, 161, 116}, {128, 185, 108}, {126, 193, 94}, {165, 149, 106}, {189, 113, 118}, {155, 136, 119}, {0, 0, 0}},
            {{0, 0, 0}, {164, 137, 110}, {150, 160, 122}, {134, 189, 112}, {138, 203, 88}, {169, 181, 84}, {197, 143, 102}, {177, 131, 116}, {131, 126, 134}, {0, 0, 0}},
            {{0, 0, 0}, {150, 160, 122}, {134, 189, 112}, {138, 203, 88}, {169, 181, 84}, {197, 143, 102}, {177, 131, 116}, {131, 126, 134}, {117, 103, 154}, {0, 0, 0}},
            {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}},
            {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}},
            {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}}
    };

    for (int y = 0; y < blurredImageData.length; y++) {
      for (int x = 0; x < blurredImageData[y].length; x++) {
        for (int c = 0; c < 3; c++) {
          //System.out.print(blurredImageData[y][x][c] + " ");
          assertEquals(expectedBlurredImageData[y][x][c], blurredImageData[y][x][c]);
        }
      }
    }
  }


  @Test
  public void testSepiaImage() {

    pngJpgImage.sepiaImage(imageName, "sepia-img");

    // Get the sepia image data
    int[][][] sepiaImageData = pngJpgImage.getRgbDataMap().get("sepia-img");

    // Define the expected RGB values for the sepia-toned image
    int[][][] expectedSepiaImageData = new int[2][2][3];

    // Define the expected RGB values for the sepia image
    expectedSepiaImageData[0][0] = new int[]{100, 88, 69};
    expectedSepiaImageData[0][1] = new int[]{196, 174, 136};
    expectedSepiaImageData[1][0] = new int[]{48, 42, 33};
    expectedSepiaImageData[1][1] = new int[]{255, 255, 238};

    // Compare the actual sepia-toned image data with the expected result
    for (int y = 0; y < expectedSepiaImageData.length; y++) {
      for (int x = 0; x < expectedSepiaImageData[y].length; x++) {
        for (int c = 0; c < 3; c++) {
          //System.out.println(sepiaImageData[y][x][c]);
          assertEquals(expectedSepiaImageData[y][x][c], sepiaImageData[y][x][c]);
        }
      }
    }
  }



}
//sharpen,blur gives 0s