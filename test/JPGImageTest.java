import org.junit.Before;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import model.JPGImage;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * The `JPGImageTest` class contains JUnit tests for the `JPGImage` class.
 */
public class JPGImageTest {

  private static JPGImage pngJpgImage;
  private static String imageName = "outputJPG";
  private static String imagePath = "outputJPG.jpg";

  private static String image2Name = "outputJPG2";
  private static String image2Path = "outputJPG2.jpg";

  int[][][] originalMatrix = {
          {{255, 0, 0}, {0, 255, 0}},
          {{0, 0, 255}, {255, 255, 255}}
  };

  static int[][][] rgbMatrix = new int[2][2][3];

  @Before
  public void setUp() {
    pngJpgImage = new JPGImage();
    createAndSaveJPG(originalMatrix, imageName, imagePath);
    //createAndSaveJPG(rgbMatrix2,image2Name,image2Path);
  }

  /**
   * Creates a JPG image with the specified RGB data and saves it to a file at the specified file
   * path.
   *
   * @param matrix   The RGB data of the image.
   * @param fileName The name of the image file.
   * @param filePath The path where the image should be saved.
   */
  private static void createAndSaveJPG(int[][][] matrix, String fileName, String filePath) {

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
      ImageIO.write(image, "jpg", output);
      System.out.println("Image saved as " + filePath);
      rgbMatrix = new int[][][]{
              {{66, 62, 63}, {167, 163, 164}},
              {{53, 49, 50}, {249, 245, 246}}
      };
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
        assertEquals(rgbMatrix[y][x][0], r);
        assertEquals(rgbMatrix[y][x][1], g);
        assertEquals(rgbMatrix[y][x][2], b);
      }
    }
  }

  @Test
  public void testVerticalFlipImage() throws IOException {

    // Perform vertical-flip on the image
    pngJpgImage.verticalFlipImage(imageName, "vertical-flip-img");

    // Get the flipped image data
    int[][][] flippedImageData = pngJpgImage.getRgbDataMap().get("vertical-flip-img");

    // Check if the flipped image matches the expected result
    int[][][] expectedFlippedImageData = new int[2][2][3];
    expectedFlippedImageData[0][0] = new int[]{53, 49, 50};
    expectedFlippedImageData[0][1] = new int[]{249, 245, 246};
    expectedFlippedImageData[1][0] = new int[]{66, 62, 63};
    expectedFlippedImageData[1][1] = new int[]{167, 163, 164};

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


    pngJpgImage.brightenImage(imageName, "brighten-img", 50);
    int[][][] brightenedImageData = pngJpgImage.getRgbDataMap().get("brighten-img");

    // Check if the brightened image matches the expected result
    // You need to define expected RGB values after brightening with an increment of 50.
    int[][][] expectedBrightenedImageData = new int[2][2][3];
    expectedBrightenedImageData[0][0] = new int[]{116, 112, 113};
    expectedBrightenedImageData[0][1] = new int[]{217, 213, 214};
    expectedBrightenedImageData[1][0] = new int[]{103, 99, 100};
    expectedBrightenedImageData[1][1] = new int[]{255, 255, 255};


    for (int y = 0; y < expectedBrightenedImageData.length; y++) {
      for (int x = 0; x < expectedBrightenedImageData[y].length; x++) {
        for (int c = 0; c < 3; c++) {
          System.out.println("Expected: " + expectedBrightenedImageData[y][x][c]
                  + " Actual: " + brightenedImageData[y][x][c]);
          assertEquals(expectedBrightenedImageData[y][x][c], brightenedImageData[y][x][c]);
        }
      }
    }
  }

  @Test
  public void testDarkenImage() throws IOException {
    // Perform a brightness adjustment
    pngJpgImage.brightenImage(imageName, "darken-img", -50);
    int[][][] brightenedImageData = pngJpgImage.getRgbDataMap().get("darken-img");

    // Check if the brightened image matches the expected result
    // You need to define expected RGB values after brightening with an increment of 50.
    int[][][] expectedBrightenedImageData = new int[2][2][3];
    expectedBrightenedImageData[0][0] = new int[]{16, 12, 13};
    expectedBrightenedImageData[0][1] = new int[]{117, 113, 114};
    expectedBrightenedImageData[1][0] = new int[]{3, 0, 0};
    expectedBrightenedImageData[1][1] = new int[]{199, 195, 196};

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
    int[][][] expectedFlippedImageData = new int[2][2][3];
    expectedFlippedImageData[0][0] = new int[]{167, 163, 164};
    expectedFlippedImageData[0][1] = new int[]{66, 62, 63};
    expectedFlippedImageData[1][0] = new int[]{249, 245, 246};
    expectedFlippedImageData[1][1] = new int[]{53, 49, 50};


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
  public void testHorizontalVerticalFlipImage() throws IOException {

    pngJpgImage.horizontalFlipImage(imageName, "horizontal-flip-img");

    // Perform a horizontal flip
    pngJpgImage.verticalFlipImage("horizontal-flip-img", "horizontal-vertical-flip-img");

    // Get the flipped image data
    int[][][] flippedImageData = pngJpgImage.getRgbDataMap().get("horizontal-vertical-flip-img");

    // Check if the flipped image matches the expected result
    int[][][] expectedFlippedImageData = new int[2][2][3];
    expectedFlippedImageData[0][0] = new int[]{249, 245, 246};
    expectedFlippedImageData[0][1] = new int[]{53, 49, 50};
    expectedFlippedImageData[1][0] = new int[]{167, 163, 164};
    expectedFlippedImageData[1][1] = new int[]{66, 62, 63};


    for (int y = 0; y < expectedFlippedImageData.length; y++) {
      for (int x = 0; x < expectedFlippedImageData[y].length; x++) {
        for (int c = 0; c < 3; c++) {
          // System.out.println(flippedImageData[y][x][c]);
          assertEquals(expectedFlippedImageData[y][x][c], flippedImageData[y][x][c]);

        }
      }
    }
  }

  @Test
  public void testVerticalHorizontalFlipImage() throws IOException {

    pngJpgImage.verticalFlipImage(imageName, "vertical-flip-img");

    // Perform a horizontal flip
    pngJpgImage.horizontalFlipImage("vertical-flip-img", "vertical-horizontal-flip-img");

    // Get the flipped image data
    int[][][] flippedImageData = pngJpgImage.getRgbDataMap().get("vertical-horizontal-flip-img");

    // Check if the flipped image matches the expected result
    int[][][] expectedFlippedImageData = new int[2][2][3];
    expectedFlippedImageData[0][0] = new int[]{249, 245, 246};
    expectedFlippedImageData[0][1] = new int[]{53, 49, 50};
    expectedFlippedImageData[1][0] = new int[]{167, 163, 164};
    expectedFlippedImageData[1][1] = new int[]{66, 62, 63};


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

    // Perform sharpening on the image
    pngJpgImage.sharpenImage(imageName, "sharp-img");

    // Get the sharpened image data
    int[][][] sharpenedImageData = pngJpgImage.getRgbDataMap().get("sharp-img");

    // Define the expected RGB values for the sharpened image
    int[][][] expectedSharpenedImageData = {
            {{0, 0, 0}, {0, 0, 0}},
            {{0, 0, 0}, {0, 0, 0}}
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
    pngJpgImage.blurImage(imageName, "blurred-img");

    // Get the blurred image data
    int[][][] blurredImageData = pngJpgImage.getRgbDataMap().get("blurred-img");

    // Check if the blurred image matches the expected result
    int[][][] expectedBlurredImageData = {
            {{0, 0, 0}, {0, 0, 0}},
            {{0, 0, 0}, {0, 0, 0}}
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
    expectedSepiaImageData[0][0] = new int[]{85, 76, 59};
    expectedSepiaImageData[0][1] = new int[]{221, 197, 153};
    expectedSepiaImageData[1][0] = new int[]{67, 60, 47};
    expectedSepiaImageData[1][1] = new int[]{255, 255, 230};

    // Compare the actual sepia-toned image data with the expected result
    for (int y = 0; y < expectedSepiaImageData.length; y++) {
      for (int x = 0; x < expectedSepiaImageData[y].length; x++) {
        for (int c = 0; c < 3; c++) {

          assertEquals(expectedSepiaImageData[y][x][c], sepiaImageData[y][x][c]);
        }
      }
    }
  }

  @Test
  public void testExtractRedComponent() {
    // Define the expected result after extracting the red component
    // Define the expected RGB values for the sepia-toned image
    int[][][] expectedRedComponent = {
            {{66, 0, 0}, {167, 0, 0}},
            {{53, 0, 0}, {249, 0, 0}}
    };


    // Perform the extraction of the "red" component
    pngJpgImage.extractComponent(imageName, "red-component-img", "red");

    // Get the extracted red component data
    int[][][] extractedRedComponent = pngJpgImage.getRgbDataMap().get("red-component-img");

    // Check if the extracted red component matches the expected result
    for (int y = 0; y < expectedRedComponent.length; y++) {
      for (int x = 0; x < expectedRedComponent[y].length; x++) {
        for (int c = 0; c < 3; c++) {
          //System.out.println(extractedRedComponent[y][x][c]);
          assertEquals(expectedRedComponent[y][x][c], extractedRedComponent[y][x][c]);
        }
      }
    }
  }

  @Test
  public void testExtractGreenComponent() {
    // Define the expected result after extracting the red component
    // Define the expected RGB values for the sepia-toned image
    int[][][] expectedGreenComponent = {
            {{0, 62, 0}, {0, 163, 0}},
            {{0, 49, 0}, {0, 245, 0}}
    };


    // Perform the extraction of the "red" component
    pngJpgImage.extractComponent(imageName, "green-component-img", "green");

    // Get the extracted red component data
    int[][][] extractedGreenComponent = pngJpgImage.getRgbDataMap().get("green-component-img");

    // Check if the extracted red component matches the expected result
    for (int y = 0; y < expectedGreenComponent.length; y++) {
      for (int x = 0; x < expectedGreenComponent[y].length; x++) {
        for (int c = 0; c < 3; c++) {
          assertEquals(expectedGreenComponent[y][x][c], extractedGreenComponent[y][x][c]);
        }
      }
    }
  }

  @Test
  public void testExtractBlueComponent() {
    // Define the expected result after extracting the red component
    // Define the expected RGB values for the sepia-toned image
    int[][][] expectedBlueComponent = {
            {{0, 0, 63}, {0, 0, 164}},
            {{0, 0, 50}, {0, 0, 246}}
    };


    // Perform the extraction of the "red" component
    pngJpgImage.extractComponent(imageName, "blue-component-img", "blue");

    // Get the extracted red component data
    int[][][] extractedBlueComponent = pngJpgImage.getRgbDataMap().get("blue-component-img");

    // Check if the extracted red component matches the expected result
    for (int y = 0; y < expectedBlueComponent.length; y++) {
      for (int x = 0; x < expectedBlueComponent[y].length; x++) {
        for (int c = 0; c < 3; c++) {
          assertEquals(expectedBlueComponent[y][x][c], extractedBlueComponent[y][x][c]);
        }
      }
    }
  }

  @Test
  public void testExtractValueComponent() {
    // Define the expected result after extracting the red component
    // Define the expected RGB values for the sepia-toned image
    int[][][] expectedValueComponent = {
            {{66, 66, 66}, {167, 167, 167}},
            {{53, 53, 53}, {249, 249, 249}}
    };


    // Perform the extraction of the "red" component
    pngJpgImage.extractComponent(imageName, "value-component-img", "value");

    // Get the extracted red component data
    int[][][] extractedValueComponent = pngJpgImage.getRgbDataMap().get("value-component-img");

    // Check if the extracted red component matches the expected result
    for (int y = 0; y < expectedValueComponent.length; y++) {
      for (int x = 0; x < expectedValueComponent[y].length; x++) {
        for (int c = 0; c < 3; c++) {

          assertEquals(expectedValueComponent[y][x][c], extractedValueComponent[y][x][c]);
        }
      }
    }
  }

  @Test
  public void testExtractLumaComponent() {
    // Define the expected result after extracting the red component
    // Define the expected RGB values for the sepia-toned image
    int[][][] expectedLumaComponent = {
            {{62, 62, 62}, {163, 163, 163}},
            {{49, 49, 49}, {245, 245, 245}}
    };


    // Perform the extraction of the "red" component
    pngJpgImage.extractComponent(imageName, "luma-component-img", "luma");

    // Get the extracted red component data
    int[][][] extractedLumaComponent = pngJpgImage.getRgbDataMap().get("luma-component-img");

    // Check if the extracted red component matches the expected result
    for (int y = 0; y < expectedLumaComponent.length; y++) {
      for (int x = 0; x < expectedLumaComponent[y].length; x++) {
        for (int c = 0; c < 3; c++) {
          //System.out.println(extractedLumaComponent[y][x][c] + " ");
          assertEquals(expectedLumaComponent[y][x][c], extractedLumaComponent[y][x][c]);
        }
      }
    }
  }

  @Test
  public void testExtractIntensityComponent() {
    // Define the expected result after extracting the red component
    // Define the expected RGB values for the sepia-toned image
    int[][][] expectedIntensityComponent = {
            {{63, 63, 63}, {164, 164, 164}},
            {{50, 50, 50}, {246, 246, 246}}
    };


    // Perform the extraction of the "red" component
    pngJpgImage.extractComponent(imageName, "intensity-component-img", "intensity");

    // Get the extracted red component data
    int[][][] extractedIntensityComponent = pngJpgImage.getRgbDataMap()
            .get("intensity-component-img");

    // Check if the extracted red component matches the expected result
    for (int y = 0; y < expectedIntensityComponent.length; y++) {
      for (int x = 0; x < expectedIntensityComponent[y].length; x++) {
        for (int c = 0; c < 3; c++) {
          //System.out.println(extractedIntensityComponent[y][x][c] + " ");
          assertEquals(expectedIntensityComponent[y][x][c], extractedIntensityComponent[y][x][c]);
        }
      }
    }
  }

  @Test
  public void testRGBSplit() {
    // Perform RGB splitting on the image
    pngJpgImage.rgbSplitImage(imageName, "red-component-img"
            , "green-component-img", "blue-component-img");

    // Get the split RGB components
    int[][][] extractedRedComponent = pngJpgImage.getRgbDataMap().get("red-component-img");
    int[][][] extractedGreenComponent = pngJpgImage.getRgbDataMap().get("green-component-img");
    int[][][] extractedBlueComponent = pngJpgImage.getRgbDataMap().get("blue-component-img");

    // Check if the split components are not null
    assertNotNull(extractedRedComponent);
    assertNotNull(extractedGreenComponent);
    assertNotNull(extractedBlueComponent);

    // Check if the dimensions match the original image
    assertEquals(rgbMatrix.length, extractedRedComponent.length);
    assertEquals(rgbMatrix[0].length, extractedRedComponent[0].length);

    assertEquals(rgbMatrix.length, extractedGreenComponent.length);
    assertEquals(rgbMatrix[0].length, extractedGreenComponent[0].length);

    assertEquals(rgbMatrix.length, extractedBlueComponent.length);
    assertEquals(rgbMatrix[0].length, extractedBlueComponent[0].length);

    int[][][] expectedRedComponent = {
            {{66, 0, 0}, {167, 0, 0}},
            {{53, 0, 0}, {249, 0, 0}}
    };

    int[][][] expectedGreenComponent = {
            {{0, 62, 0}, {0, 163, 0}},
            {{0, 49, 0}, {0, 245, 0}}
    };

    int[][][] expectedBlueComponent = {
            {{0, 0, 63}, {0, 0, 164}},
            {{0, 0, 50}, {0, 0, 246}}
    };

    // Compare the extracted components with the original image
    for (int y = 0; y < rgbMatrix.length; y++) {
      for (int x = 0; x < rgbMatrix[y].length; x++) {
        for (int c = 0; c < 3; c++) {
          assertEquals(expectedRedComponent[y][x][c], extractedRedComponent[y][x][c]);
          assertEquals(expectedGreenComponent[y][x][c], extractedGreenComponent[y][x][c]);
          assertEquals(expectedBlueComponent[y][x][c], extractedBlueComponent[y][x][c]);
        }
      }
    }
  }

  @Test
  public void testRGBCombine() {
    // Create separate red, green, and blue components from the original image
    pngJpgImage.extractComponent(imageName, "red-component-img2", "red");
    pngJpgImage.extractComponent(imageName, "green-component-img2", "green");
    pngJpgImage.extractComponent(imageName, "blue-component-img2", "blue");

    // Perform RGB combining on the separated components
    pngJpgImage.combineRGBImages("combined-img", "red-component-img2"
            , "green-component-img2", "blue-component-img2");

    // Get the combined RGB data
    int[][][] combinedRGBData = pngJpgImage.getRgbDataMap().get("combined-img");

    // Check if the combined RGB data is not null
    assertNotNull(combinedRGBData);

    // Check if the dimensions match the original image
    assertEquals(rgbMatrix.length, combinedRGBData.length);
    assertEquals(rgbMatrix[0].length, combinedRGBData[0].length);

    // Compare the combined RGB data with the original image
    for (int y = 0; y < rgbMatrix.length; y++) {
      for (int x = 0; x < rgbMatrix[y].length; x++) {
        for (int c = 0; c < 3; c++) {
          assertEquals(rgbMatrix[y][x][c], combinedRGBData[y][x][c]);
        }
      }
    }
  }

  @Test
  public void testSaveImage() throws IOException {
    // Create a new image content to save


    // Save the image
    pngJpgImage.saveImage(imageName, imagePath);

    // Check if the file exists at the specified path
    File savedFile = new File(imagePath);
    assertTrue("Image file should exist after saving.", savedFile.exists());

    // Clean up: Delete the saved file after the test
    savedFile.delete();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testExtractComponentWithInvalidComponent() {

    pngJpgImage.extractComponent(imageName, "destName", "invalidComponent");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testExtractComponentWithInvalidSourceName() {
    pngJpgImage.extractComponent("nonExistentImage", "destName", "red");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCombineRGBImagesWithMismatchedDimensions() {
    pngJpgImage.combineRGBImages("combinedName", "redName", "greenName", "blueName");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRGBSplitImageWithInvalidSourceName() {
    pngJpgImage.rgbSplitImage("nonExistentImage", "destNameRed", "destNameGreen", "destNameBlue");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSaveImageWithInvalidPath() throws IOException {
    String invalidPath = "invalid_path/invalid_file.png";
    pngJpgImage.saveImage(invalidPath, imageName);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSaveImageWithInvalidFilename() throws IOException {
    String invalidFilename = "bac#";
    JPGImage img = new JPGImage();
    img.saveImage(imagePath, invalidFilename);
  }


}