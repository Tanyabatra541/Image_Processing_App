package controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


import model.Histogram;
import org.junit.Before;
import org.junit.Test;


import javax.imageio.ImageIO;


import model.JPGImage;
//import model.Model;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;


/**
 * The `ControllerTest` class contains JUnit tests for the `Controller` class.
 */
public class ControllerTest {

  private static JPGImage pngJpgImage;

  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

  private static final String imageName = "outputJPG";
  private static final String imagePath = "outputJPG.jpg";

  private static final String image3Name = "output3";
  private static final String image3Path = "output3.jpg";

  static int[][][] rgbMatrix = new int[2][2][3];

  private Controller controller;

  int[][][] rgbMatrix2 = {
          {{128, 64, 192}, {255, 128, 32}, {64, 192, 128}, {32, 96, 255}, {192, 64, 128},
                  {160, 32, 192}, {96, 255, 128}, {224, 64, 32}, {128, 160, 160}, {192, 128, 96}},
          {{96, 224, 160}, {128, 192, 64}, {160, 255, 32}, {224, 64, 160}, {255, 128, 96},
                  {32, 192, 96}, {128, 64, 224}, {160, 32, 128}, {96, 160, 64}, {160, 224, 255}},
          {{32, 192, 96}, {128, 64, 224}, {160, 32, 128}, {96, 160, 64}, {160, 224, 255},
                  {128, 64, 192}, {255, 128, 32}, {64, 192, 128}, {32, 96, 255}, {192, 64, 128}},
          {{160, 32, 192}, {96, 255, 128}, {224, 64, 32}, {128, 160, 160}, {192, 128, 96},
                  {96, 224, 160}, {128, 192, 64}, {160, 255, 32}, {224, 64, 160}, {255, 128, 96}},
          {{64, 128, 96}, {255, 160, 192}, {96, 96, 128}, {192, 32, 128}, {32, 192, 255},
                  {128, 64, 192}, {255, 128, 32}, {64, 192, 128}, {32, 96, 255}, {192, 64, 128}},
          {{192, 64, 128}, {160, 32, 192}, {96, 255, 128}, {224, 64, 32}, {128, 160, 160},
                  {32, 192, 96}, {128, 64, 224}, {160, 32, 128}, {96, 160, 64}, {160, 224, 255}},
          {{96, 255, 128}, {224, 64, 32}, {128, 160, 160}, {192, 128, 96}, {96, 224, 160},
                  {128, 192, 64}, {160, 255, 32}, {224, 64, 160}, {255, 128, 96}, {32, 192, 96}},
          {{224, 64, 32}, {128, 160, 160}, {192, 128, 96}, {96, 224, 160}, {128, 192, 64},
                  {160, 255, 32}, {224, 64, 160}, {255, 128, 96}, {32, 192, 96}, {128, 64, 224}},
          {{128, 160, 160}, {192, 128, 96}, {96, 224, 160}, {128, 192, 64}, {160, 255, 32},
                  {224, 64, 160}, {255, 128, 96}, {32, 192, 96}, {128, 64, 224}, {160, 32, 128}},
          {{192, 128, 96}, {96, 224, 160}, {128, 192, 64}, {160, 255, 32}, {224, 64, 160},
                  {255, 128, 96}, {32, 192, 96}, {128, 64, 224}, {160, 32, 128}, {96, 255, 128}}
  };

  int[][][] originalMatrix = {
          {{255, 0, 0}, {0, 255, 0}},
          {{0, 0, 255}, {255, 255, 255}}
  };

  int[][][] rgbMatrix3 = {
          {{255, 200, 80}, {100, 255, 150}, {80, 90, 255}},
          {{255, 150, 150}, {100, 255, 80}, {110, 110, 255}},
          {{190, 170, 255}, {255, 255, 255}, {255, 255, 255}}
  };



  @Before
  public void setUp() throws FileNotFoundException {
    pngJpgImage = new JPGImage();
    createAndSaveJPG(originalMatrix, imageName, imagePath);

    createAndSaveJPG(rgbMatrix3, image3Name, image3Path);

    System.setOut(new PrintStream(outContent));

    Reader in = new FileReader("res/scriptFile.txt");
    controller = new Controller(in);

  }

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
  public void testLoad() throws IOException {
    Controller.parseAndExecute("load " + imagePath + " " + imageName);

    // Check if the image was loaded successfully
    assertTrue(pngJpgImage.getImageMap().containsKey(imageName));
    // Get the RGB data
    int[][][] rgbData = pngJpgImage.getRgbDataMap(imageName);

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
    Controller.parseAndExecute("load " + imagePath + " " + imageName);
    Controller.parseAndExecute("vertical-flip " + imageName + " vertical-flip-img");


    // Get the flipped image data
    int[][][] flippedImageData = pngJpgImage.getRgbDataMap("vertical-flip-img");

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
  public void testHorizontalFlipImage() throws IOException {

    controller.parseAndExecute("load " + imagePath + " " + imageName);
    controller.parseAndExecute("horizontal-flip " + imageName + " horizontal-flip-img");

    // Perform a horizontal flip


    // Get the flipped image data
    int[][][] flippedImageData = pngJpgImage.getRgbDataMap("horizontal-flip-img");

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
  public void testSharpenImage() throws IOException {

    Controller.parseAndExecute("load " + imagePath + " " + imageName);
    Controller.parseAndExecute("sharpen " + imageName + " sharp-img");
    // Perform sharpening on the image

    // Get the sharpened image data
    int[][][] sharpenedImageData = pngJpgImage.getRgbDataMap("sharp-img");

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
  public void testBlurImage() throws IOException {

    controller.parseAndExecute("load " + imagePath + " " + imageName);
    controller.parseAndExecute("blur " + imageName + " blurred-img");

    // Perform blurring on the image

    // Get the blurred image data
    int[][][] blurredImageData = pngJpgImage.getRgbDataMap("blurred-img");

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
  public void testSepiaImage() throws IOException {
    controller.parseAndExecute("load " + imagePath + " " + imageName);
    controller.parseAndExecute("sepia " + imageName + " sepia-img");
    pngJpgImage.sepiaImage(imageName, "sepia-img", 0);

    // Get the sepia image data
    int[][][] sepiaImageData = pngJpgImage.getRgbDataMap("sepia-img");

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
  public void testExtractRedComponent() throws IOException {

    controller.parseAndExecute("load " + imagePath + " " + imageName);
    controller.parseAndExecute("red-component " + imageName + " red-component-img");
    // Define the expected result after extracting the red component
    // Define the expected RGB values for the sepia-toned image
    int[][][] expectedRedComponent = {
            {{66, 0, 0}, {167, 0, 0}},
            {{53, 0, 0}, {249, 0, 0}}
    };


    // Perform the extraction of the "red" component


    // Get the extracted red component data
    int[][][] extractedRedComponent = pngJpgImage.getRgbDataMap("red-component-img");

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
  public void testExtractGreenComponent() throws IOException {

    controller.parseAndExecute("load " + imagePath + " " + imageName);
    controller.parseAndExecute("green-component " + imageName + " green-component-img");
    // Define the expected result after extracting the red component
    // Define the expected RGB values for the sepia-toned image
    int[][][] expectedGreenComponent = {
            {{0, 62, 0}, {0, 163, 0}},
            {{0, 49, 0}, {0, 245, 0}}
    };


    // Perform the extraction of the "red" component
    pngJpgImage.extractComponent(imageName, "green-component-img", "green");

    // Get the extracted red component data
    int[][][] extractedGreenComponent = pngJpgImage.getRgbDataMap("green-component-img");

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
  public void testExtractBlueComponent() throws IOException {

    controller.parseAndExecute("load " + imagePath + " " + imageName);
    controller.parseAndExecute("blue-component " + imageName + " blue-component-img");
    // Define the expected result after extracting the red component
    // Define the expected RGB values for the sepia-toned image
    int[][][] expectedBlueComponent = {
            {{0, 0, 63}, {0, 0, 164}},
            {{0, 0, 50}, {0, 0, 246}}
    };




    // Get the extracted red component data
    int[][][] extractedBlueComponent = pngJpgImage.getRgbDataMap("blue-component-img");

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
  public void testRGBSplit() throws IOException {

    controller.parseAndExecute("load " + imagePath + " " + imageName);
    controller.parseAndExecute("rgb-split " + imageName + " red-component-img"
            + " green-component-img" + " blue-component-img");
    // Perform RGB splitting on the image

    // Get the split RGB components
    int[][][] extractedRedComponent = pngJpgImage.getRgbDataMap("red-component-img");
    int[][][] extractedGreenComponent = pngJpgImage.getRgbDataMap("green-component-img");
    int[][][] extractedBlueComponent = pngJpgImage.getRgbDataMap("blue-component-img");

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
  public void testRGBCombine() throws IOException {

    controller.parseAndExecute("load " + imagePath + " " + imageName);
    controller.parseAndExecute("rgb-split " + imageName + " red-component-img"
            + " green-component-img" + " blue-component-img");
    controller.parseAndExecute("rgb-combine " + "combined-img" + " red-component-img"
            + " green-component-img" + " blue-component-img");

    // Get the combined RGB data
    int[][][] combinedRGBData = pngJpgImage.getRgbDataMap("combined-img");

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
  public void testBrightenImage() throws IOException {
    controller.parseAndExecute("load " + imagePath + " " + imageName);
    controller.parseAndExecute("brighten 50 " + imageName + " brighten-img");


    int[][][] brightenedImageData = pngJpgImage.getRgbDataMap("brighten-img");

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
    controller.parseAndExecute("load " + imagePath + " " + imageName);
    controller.parseAndExecute("brighten -50 " + imageName + " darken-img");
    int[][][] brightenedImageData = pngJpgImage.getRgbDataMap("darken-img");

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
  public void testHorizontalVerticalFlipImage() throws IOException {

    controller.parseAndExecute("load " + imagePath + " " + imageName);
    controller.parseAndExecute("horizontal-flip " + imageName + " horizontal-img");
    controller.parseAndExecute("vertical-flip " + "horizontal-img"
            + " horizontal-vertical-flip-img");

    // Get the flipped image data
    int[][][] flippedImageData = pngJpgImage.getRgbDataMap("horizontal-vertical-flip-img");

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

    controller.parseAndExecute("load " + imagePath + " " + imageName);
    controller.parseAndExecute("vertical-flip " + imageName + " horizontal-img");
    controller.parseAndExecute("horizontal-flip " + "horizontal-img"
            + " vertical-horizontal-flip-img");

    // Get the flipped image data
    int[][][] flippedImageData = pngJpgImage.getRgbDataMap("vertical-horizontal-flip-img");

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
  public void testExtractValueComponent() throws IOException {
    // Define the expected result after extracting the red component
    // Define the expected RGB values for the sepia-toned image
    int[][][] expectedValueComponent = {
            {{66, 66, 66}, {167, 167, 167}},
            {{53, 53, 53}, {249, 249, 249}}
    };
    controller.parseAndExecute("load " + imagePath + " " + imageName);
    controller.parseAndExecute("value-component " + imageName + " value-component-img");


    // Get the extracted red component data
    int[][][] extractedValueComponent = pngJpgImage.getRgbDataMap("value-component-img");

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
  public void testExtractLumaComponent() throws IOException {
    // Define the expected result after extracting the red component
    // Define the expected RGB values for the sepia-toned image
    int[][][] expectedLumaComponent = {
            {{62, 62, 62}, {163, 163, 163}},
            {{49, 49, 49}, {245, 245, 245}}
    };


    // Perform the extraction of the "red" component
    controller.parseAndExecute("load " + imagePath + " " + imageName);
    controller.parseAndExecute("luma-component " + imageName + " luma-component-img");

    // Get the extracted red component data
    int[][][] extractedLumaComponent = pngJpgImage.getRgbDataMap("luma-component-img");

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
  public void testExtractIntensityComponent() throws IOException {
    // Define the expected result after extracting the red component
    // Define the expected RGB values for the sepia-toned image
    int[][][] expectedIntensityComponent = {
            {{63, 63, 63}, {164, 164, 164}},
            {{50, 50, 50}, {246, 246, 246}}
    };


    // Perform the extraction of the "red" component
    controller.parseAndExecute("load " + imagePath + " " + imageName);
    controller.parseAndExecute("intensity-component " + imageName + " intensity-component-img");

    // Get the extracted red component data
    int[][][] extractedIntensityComponent = pngJpgImage.getRgbDataMap("intensity-component-img");

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
  public void testCompressby90() throws IOException {

    controller.parseAndExecute("load " + image3Path + " " + image3Name);
    controller.parseAndExecute("compress 90 " + image3Name + " compressed-90-img");


    int[][][] compressedImage = pngJpgImage.getRgbDataMap("compressed-90-img");


    int[][][] expectedCompressedImage = {
            {{0, 0, 201},{0, 0, 201}, {0, 0, 0}},
            {{0, 0, 201},{0, 0, 201}, {0, 0, 0}},
            {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}}
    };


    for (int y = 0; y < compressedImage.length; y++) {
      for (int x = 0; x < compressedImage[y].length; x++) {
        for (int c = 0; c < 3; c++) {



          assertEquals(expectedCompressedImage[y][x][c], compressedImage[y][x][c]);

        }
      }
    }


  }

  @Test
  public void testCompressby50() throws IOException {



    controller.parseAndExecute("load " + image3Path + " " + image3Name);
    controller.parseAndExecute("compress 50 " + image3Name + " compressed-50-img");

    int[][][] compressedImage = pngJpgImage.getRgbDataMap("compressed-50-img");


    int[][][] expectedCompressedImage = {
            {{174, 200, 201},{174, 200, 201}, {0, 116, 195}},
            {{174, 200, 201},{174, 200, 201}, {0, 116, 195}},
            {{216, 230, 190}, {216, 230, 190}, {0, 242, 0}}
    };


    for (int y = 0; y < compressedImage.length; y++) {
      for (int x = 0; x < compressedImage[y].length; x++) {
        for (int c = 0; c < 3; c++) {

          assertEquals(expectedCompressedImage[y][x][c], compressedImage[y][x][c]);

        }
      }
    }


  }

  @Test
  public void testCompressby10() throws IOException {


    controller.parseAndExecute("load " + image3Path + " " + image3Name);
    controller.parseAndExecute("compress 10 " + image3Name + " compressed-10-img");


    int[][][] compressedImage = pngJpgImage.getRgbDataMap("compressed-10-img");


    int[][][] expectedCompressedImage = {
            {{181, 207, 213},{167, 193, 199}, {89, 102, 180}},
            {{167, 193, 188},{181, 207, 202}, {118, 131, 209}},
            {{187, 206, 160}, {246, 255, 219}, {228, 242, 229}}
    };


    for (int y = 0; y < compressedImage.length; y++) {
      for (int x = 0; x < compressedImage[y].length; x++) {
        for (int c = 0; c < 3; c++) {

          assertEquals(expectedCompressedImage[y][x][c], compressedImage[y][x][c]);

        }
      }
    }


  }

  @Test
  public void testCompressby0() throws IOException {


    controller.parseAndExecute("load " + image3Path + " " + image3Name);
    controller.parseAndExecute("compress 0 " + image3Name + " compressed-0-img");


    int[][][] compressedImage = pngJpgImage.getRgbDataMap("compressed-0-img");


    int[][][] expectedCompressedImage = {
            {{185, 211, 212}, {174, 200, 201}, {0, 0, 0}},
            {{160, 186, 187}, {177, 203, 204}, {0, 0, 0}},
            {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}}
    };


    for (int y = 0; y < compressedImage.length; y++) {
      for (int x = 0; x < compressedImage[y].length; x++) {
        for (int c = 0; c < 3; c++) {

          assertEquals(expectedCompressedImage[y][x][c], compressedImage[y][x][c]);

        }
      }
    }


  }

  @Test
  public void testCompressbyNegative() throws IOException {
    controller.parseAndExecute("load " + image3Path + " " + image3Name);
    controller.parseAndExecute("compress -5 " + image3Name + " compressed--5-img");


    assertEquals("Loaded image: output3\n" +
            "Compression percentage must be between 0 and 100.\n" +
            "Error in compressing output3 by -5.0 %", outContent.toString().trim());
  }

  @Test
  public void testCompressWithMaxPercentage() throws IOException {
    // Compression with 100% should result in a completely black image

    controller.parseAndExecute("load " + image3Path + " " + image3Name);
    controller.parseAndExecute("compress 100 " + image3Name + " compressed-100-img");

    int[][][] compressedImage = pngJpgImage.getRgbDataMap("compressed-100-img");

    int[][][] expectedCompressedImage = {
            {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}},
            {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}},
            {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}}
    };



    for (int y = 0; y < compressedImage.length; y++) {
      for (int x = 0; x < compressedImage[y].length; x++) {
        for (int c = 0; c < 3; c++) {

          assertEquals(expectedCompressedImage[y][x][c], compressedImage[y][x][c]);
        }
      }
    }

  }

  @Test
  public void testCompressWithNonPowerOfTwoDimensions() throws IOException {
    // Test compressing an image with non-power-of-two dimensions
    // For simplicity, you can create a small image with dimensions like 3x3
    int[][][] nonPowerOfTwoImage = {
            {{255, 200, 80}, {100, 255, 150}, {80, 90, 255}},
            {{255, 150, 150}, {100, 255, 80}, {110, 110, 255}},
            {{190, 170, 255}, {255, 255, 255}, {255, 255, 255}}
    };

    createAndSaveJPG(nonPowerOfTwoImage, "nonPowerOfTwoImage", "nonPowerOfTwoImage.png");
    controller.parseAndExecute("load nonPowerOfTwoImage.png nonPowerOfTwoImage");
    controller.parseAndExecute("compress 50 nonPowerOfTwoImage compressed-non-power-of-two-img");

    // Try compressing the non-power-of-two image with 50% compression


    // Verify that the compression was successful and the image dimensions remain the same
    int[][][] compressedImage = pngJpgImage.getRgbDataMap("compressed-non-power-of-two-img");

    assertEquals(nonPowerOfTwoImage.length, compressedImage.length);
    assertEquals(nonPowerOfTwoImage[0].length, compressedImage[0].length);
  }

  @Test
  public void testCompressWithInvalidPercentage() throws IOException {
    // Attempting to compress with an invalid percentage should result in an error message

    controller.parseAndExecute("load " + image3Path + " " + image3Name);
    controller.parseAndExecute("compress 115 " + image3Name + " invalid-percentage-img");


    assertEquals("Loaded image: output3\n" +
            "Compression percentage must be between 0 and 100.\n" +
            "Error in compressing output3 by 115.0 %", outContent.toString().trim());
  }

  @Test
  public void testCompressWithLargeImage() throws IOException {
    // Test compressing a large image to ensure efficiency
    int[][][] largeImage = new int[1000][1000][3];

    createAndSaveJPG(largeImage, "largeImage", "largeImage.png");
    controller.parseAndExecute("load largeImage.png largeImage");
    controller.parseAndExecute("compress 50 largeImage compressedLargeImage");

    // Try compressing the non-power-of-two image with 50% compression


    int[][][] result = pngJpgImage.getRgbDataMap("compressedLargeImage");

    // Ensure that the dimensions are still correct
    assertEquals(1000, result.length);
    assertEquals(1000, result[0].length);
  }


  @Test
  public void testBlurImageWith50Split() throws IOException {


    controller.parseAndExecute("load " + imagePath + " " + imageName);
    controller.parseAndExecute("blur " + imageName + " blurred-img split 50");

    int[][][] blurredImageData = pngJpgImage.getRgbDataMap("blurred-img");


    int[][][] expectedBlurredImageData = {
            {{93, 89, 90, },{167, 163, 164, },},
            {{99, 95, 96, },{249, 245, 246, },}
    };

    for (int y = 0; y < blurredImageData.length; y++) {

      for (int x = 0; x < blurredImageData[y].length; x++) {

        for (int c = 0; c < 3; c++) {

          assertEquals(expectedBlurredImageData[y][x][c], blurredImageData[y][x][c]);
        }
      }
    }

  }


  @Test
  public void testBlurImageWith0Split() {

    pngJpgImage.blurImage(imageName, "blurred-img", 0);


    int[][][] blurredImageData = pngJpgImage.getRgbDataMap("blurred-img");


    int[][][] expectedBlurredImageData = {
            {{93, 89, 90,}, {156, 152, 153,},},
            {{99, 95, 96,}, {185, 181, 182,},}};

    for (int y = 0; y < blurredImageData.length; y++) {

      for (int x = 0; x < blurredImageData[y].length; x++) {

        for (int c = 0; c < 3; c++) {

          assertEquals(expectedBlurredImageData[y][x][c], blurredImageData[y][x][c]);
        }

      }

    }
  }

  @Test
  public void testBlurImageWith100Split() {
    // Perform blurring on the image
    pngJpgImage.blurImage(imageName, "blurred-img", 100);


    int[][][] blurredImageData = pngJpgImage.getRgbDataMap("blurred-img");


    int[][][] expectedBlurredImageData = {
            {{93, 89, 90,}, {156, 152, 153,},},
            {{99, 95, 96,}, {185, 181, 182,},}};

    for (int y = 0; y < blurredImageData.length; y++) {

      for (int x = 0; x < blurredImageData[y].length; x++) {

        for (int c = 0; c < 3; c++) {

          assertEquals(expectedBlurredImageData[y][x][c], blurredImageData[y][x][c]);
        }

      }

    }
  }


  @Test
  public void testSharpenImageWith50Split() throws IOException {


    pngJpgImage.sharpenImage(imageName, "sharp-img", 50);


    int[][][] sharpenedImageData = pngJpgImage.getRgbDataMap("sharp-img");


    int[][][] expectedSharpenedImageData = {
            {{0, 0, 0, },{0, 0, 0, },},
            {{0, 0, 0, },{0, 0, 0, },}    };

    for (int y = 0; y < sharpenedImageData.length; y++) {

      for (int x = 0; x < sharpenedImageData[y].length; x++) {

        for (int c = 0; c < 3; c++) {

          assertEquals(expectedSharpenedImageData[y][x][c], sharpenedImageData[y][x][c]);
        }
      }
    }

  }

  @Test
  public void testSharpenImageWith0Split() throws IOException {


    pngJpgImage.sharpenImage(imageName, "sharp-img", 0);


    int[][][] sharpenedImageData = pngJpgImage.getRgbDataMap("sharp-img");


    int[][][] expectedSharpenedImageData = {
            {{0, 0, 0, },{0, 0, 0, },},
            {{0, 0, 0, },{0, 0, 0, },}
    };

    for (int y = 0; y < sharpenedImageData.length; y++) {

      for (int x = 0; x < sharpenedImageData[y].length; x++) {

        for (int c = 0; c < 3; c++) {

          assertEquals(expectedSharpenedImageData[y][x][c], sharpenedImageData[y][x][c]);
        }
      }
    }

  }

  @Test
  public void testSharpenImageWith100Split() throws IOException {


    pngJpgImage.sharpenImage(imageName, "sharp-img", 100);


    int[][][] sharpenedImageData = pngJpgImage.getRgbDataMap("sharp-img");


    int[][][] expectedSharpenedImageData = {
            {{0, 0, 0,}, {0, 0, 0,},},
            {{0, 0, 0,}, {0, 0, 0,},}
    };

    for (int y = 0; y < sharpenedImageData.length; y++) {

      for (int x = 0; x < sharpenedImageData[y].length; x++) {

        for (int c = 0; c < 3; c++) {

          assertEquals(expectedSharpenedImageData[y][x][c], sharpenedImageData[y][x][c]);
        }

      }

    }
  }

  @Test
  public void testSepiaImageWith0Split() {

    pngJpgImage.sepiaImage(imageName, "sepia-img", 0);


    int[][][] sepiaImageData = pngJpgImage.getRgbDataMap("sepia-img");


    int[][][] expectedSepiaImageData = {
            {{85, 76, 59, },{221, 197, 153, },},
            {{67, 60, 47, },{255, 255, 230, },}
    };



    for (int y = 0; y < sepiaImageData.length; y++) {

      for (int x = 0; x < sepiaImageData[y].length; x++) {

        for (int c = 0; c < 3; c++) {

          assertEquals(expectedSepiaImageData[y][x][c], sepiaImageData[y][x][c]);
        }
      }
    }

  }

  @Test
  public void testSepiaImageWith50Split() {

    pngJpgImage.sepiaImage(imageName, "sepia-img", 50);


    int[][][] sepiaImageData = pngJpgImage.getRgbDataMap("sepia-img");


    int[][][] expectedSepiaImageData = {
            {{66, 62, 63, },{221, 197, 153, },},
            {{53, 49, 50, },{255, 255, 230, },}};



    for (int y = 0; y < sepiaImageData.length; y++) {

      for (int x = 0; x < sepiaImageData[y].length; x++) {

        for (int c = 0; c < 3; c++) {

          assertEquals(expectedSepiaImageData[y][x][c], sepiaImageData[y][x][c]);
        }
      }
    }

  }

  @Test
  public void testSepiaImageWith100Split() {

    pngJpgImage.sepiaImage(imageName, "sepia-img", 100);


    int[][][] sepiaImageData = pngJpgImage.getRgbDataMap("sepia-img");


    int[][][] expectedSepiaImageData = {
            {{66, 62, 63, },{167, 163, 164, },},
            {{53, 49, 50, },{249, 245, 246, },}
    };


    for (int y = 0; y < sepiaImageData.length; y++) {

      for (int x = 0; x < sepiaImageData[y].length; x++) {

        for (int c = 0; c < 3; c++) {

          assertEquals(expectedSepiaImageData[y][x][c], sepiaImageData[y][x][c]);
        }
      }
    }

  }


  @Test
  public void testColorCorrectionWith0Split() {

    pngJpgImage.colorCorrectImage(imageName, "color-correct-img", 0);


    int[][][] sepiaImageData = pngJpgImage.getRgbDataMap("color-correct-img");


    int[][][] expectedSepiaImageData = {
            { {63, 63, 63, },{164, 164, 164, },},
            {{50, 50, 50, },{246, 246, 246, },}
    };



    for (int y = 0; y < sepiaImageData.length; y++) {

      for (int x = 0; x < sepiaImageData[y].length; x++) {

        for (int c = 0; c < 3; c++) {

          assertEquals(expectedSepiaImageData[y][x][c], sepiaImageData[y][x][c]);
        }
      }
    }

  }


  @Test
  public void testColorCorrectionWith50Split() {

    pngJpgImage.colorCorrectImage(imageName, "color-correct-img", 50);


    int[][][] sepiaImageData = pngJpgImage.getRgbDataMap("color-correct-img");


    int[][][] expectedSepiaImageData = {
            {{63, 63, 63, },{164, 164, 164, },},
            {{50, 50, 50, },{246, 246, 246, },}
    };



    for (int y = 0; y < sepiaImageData.length; y++) {

      for (int x = 0; x < sepiaImageData[y].length; x++) {

        for (int c = 0; c < 3; c++) {

          assertEquals(expectedSepiaImageData[y][x][c], sepiaImageData[y][x][c]);
        }
      }
    }

  }

  @Test
  public void testColorCorrectionWith100Split() {

    pngJpgImage.colorCorrectImage(imageName, "color-correct-img", 100);


    int[][][] sepiaImageData = pngJpgImage.getRgbDataMap("color-correct-img");


    int[][][] expectedSepiaImageData = {
            {{63, 63, 63, },{164, 164, 164, },},
            {{50, 50, 50, },{246, 246, 246, },}
    };



    for (int y = 0; y < sepiaImageData.length; y++) {

      for (int x = 0; x < sepiaImageData[y].length; x++) {

        for (int c = 0; c < 3; c++) {

          assertEquals(expectedSepiaImageData[y][x][c], sepiaImageData[y][x][c]);
        }
      }
    }

  }

  @Test
  public void testLevelAdjustment1() {

    pngJpgImage.applyLevelsAdjustment(20,100,255,imageName, "color-correct-img");


    int[][][] sepiaImageData = pngJpgImage.getRgbDataMap("color-correct-img");


    int[][][] expectedSepiaImageData = {
            {{78, 72, 74, },{167, 163, 164, },},
            {{57, 51, 52, },{249, 245, 246, },}
    };



    for (int y = 0; y < sepiaImageData.length; y++) {

      for (int x = 0; x < sepiaImageData[y].length; x++) {

        for (int c = 0; c < 3; c++) {

          assertEquals(expectedSepiaImageData[y][x][c], sepiaImageData[y][x][c]);
        }
      }
    }

  }

  @Test
  public void testLevelAdjustment2() {

    pngJpgImage.applyLevelsAdjustment(0, 128, 255, imageName, "color-correct-img");


    int[][][] sepiaImageData = pngJpgImage.getRgbDataMap("color-correct-img");


    int[][][] expectedSepiaImageData = {
            {{66, 62, 63,}, {167, 163, 164,},},
            {{53, 49, 50,}, {249, 245, 246,},}
    };



    for (int y = 0; y < sepiaImageData.length; y++) {

      for (int x = 0; x < sepiaImageData[y].length; x++) {

        for (int c = 0; c < 3; c++) {

          assertEquals(expectedSepiaImageData[y][x][c], sepiaImageData[y][x][c]);
        }

      }

    }
  }

  @Test
  public void testLevelAdjustment3() {

    pngJpgImage.applyLevelsAdjustment(0,0,0,imageName, "color-correct-img");


    assertEquals("Invalid shadow, mid, highlight points", outContent.toString().trim());
  }
  @Test
  public void testLevelAdjustment4() {

    pngJpgImage.applyLevelsAdjustment(255,255,255,imageName, "color-correct-img");

    assertEquals("Invalid shadow, mid, highlight points", outContent.toString().trim());
  }


  @Test
  public void testLevelAdjustment5() {

    pngJpgImage.applyLevelsAdjustment(0,50,0,imageName, "color-correct-img");
    assertEquals("Invalid shadow, mid, highlight points", outContent.toString().trim());
  }

  @Test
  public void testLevelAdjustment6() {

    pngJpgImage.applyLevelsAdjustment(150,100,70,imageName, "color-correct-img");
    assertEquals("Invalid shadow, mid, highlight points", outContent.toString().trim());
  }

  @Test
  public void testLevelAdjustment7() {

    pngJpgImage.applyLevelsAdjustment(0,20,500,imageName, "color-correct-img");
    assertEquals("Invalid shadow, mid, highlight points", outContent.toString().trim());
  }

  @Test
  public void testLevelAdjustment8() {

    pngJpgImage.applyLevelsAdjustment(-3,20,70,imageName, "color-correct-img");
    assertEquals("Invalid shadow, mid, highlight points", outContent.toString().trim());
  }

  @Test
  public void testLevelAdjustmentWithSplit() {

    pngJpgImage.applyLevelsAdjustment(20,100,255,imageName, "color-correct-img", 50);


    int[][][] sepiaImageData = pngJpgImage.getRgbDataMap("color-correct-img");


    int[][][] expectedSepiaImageData = {
            {{78, 72, 74, },{202, 198, 199, },},
            {{57, 51, 52, },{253, 251, 251, },}};



    for (int y = 0; y < sepiaImageData.length; y++) {

      for (int x = 0; x < sepiaImageData[y].length; x++) {

        for (int c = 0; c < 3; c++) {

          assertEquals(expectedSepiaImageData[y][x][c], sepiaImageData[y][x][c]);
        }
      }
    }

  }
  @Test
  public void testColorCorrection() {

    pngJpgImage.colorCorrectImage(imageName, "color-correct-img", 50);


    int[][][] imageData = pngJpgImage.getRgbDataMap("color-correct-img");


    int[][][] expectedImageData = {
            {{63, 63, 63, },{164, 164, 164, },},
            {{50, 50, 50, },{245, 245, 245, }}
    };



    for (int y = 0; y < imageData.length; y++) {

      for (int x = 0; x < imageData[y].length; x++) {

        for (int c = 0; c < 3; c++) {

          assertEquals(expectedImageData[y][x][c], imageData[y][x][c]);
        }
      }
    }

  }

  @Test
  public void testGrayScaleWith50Split() {

    pngJpgImage.convertToGrayscale(imageName, "gray-scale-img", 50);


    int[][][] imageData = pngJpgImage.getRgbDataMap("gray-scale-img");


    int[][][] expectedImageData = {
            {{62, 62, 62, },{163, 163, 163, },},
            {{49, 49, 49, },{245, 245, 245, }}
    };



    for (int y = 0; y < imageData.length; y++) {

      for (int x = 0; x < imageData[y].length; x++) {

        for (int c = 0; c < 3; c++) {

          assertEquals(expectedImageData[y][x][c], imageData[y][x][c]);
        }
      }
    }

  }

  @Test
  public void testGrayScaleWith0Split() {

    pngJpgImage.convertToGrayscale(imageName, "gray-scale-img", 0);


    int[][][] imageData = pngJpgImage.getRgbDataMap("gray-scale-img");


    int[][][] expectedImageData = {
            {{62, 62, 62, },{167, 163, 164, },},
            {{49, 49, 49, },{249, 245, 246, },}
    };



    for (int y = 0; y < imageData.length; y++) {

      for (int x = 0; x < imageData[y].length; x++) {

        for (int c = 0; c < 3; c++) {

          assertEquals(expectedImageData[y][x][c], imageData[y][x][c]);
        }
      }
    }

  }

  @Test
  public void testGrayScaleWith100Split() {

    pngJpgImage.convertToGrayscale(imageName, "gray-scale-img", 100);


    int[][][] imageData = pngJpgImage.getRgbDataMap("gray-scale-img");


    int[][][] expectedImageData = {
            {{62, 62, 62, },{163, 163, 163, },},
            {{49, 49, 49, },{245, 245, 245, },}
    };



    for (int y = 0; y < imageData.length; y++) {

      for (int x = 0; x < imageData[y].length; x++) {

        for (int c = 0; c < 3; c++) {

          assertEquals(expectedImageData[y][x][c], imageData[y][x][c]);
        }
      }
    }

  }


  @Test
  public void testHistogram() throws IOException {

    Histogram histogram = new Histogram(0, 255);

    histogram.createHistogram(rgbMatrix2);

    assertEquals(24,histogram.calculateMaxCount());
    assertEquals(128,histogram.findPeakValue(histogram.histogramR));
    assertEquals(64,histogram.findPeakValue(histogram.histogramG));
    assertEquals(96,histogram.findPeakValue(histogram.histogramB));

  }

  @Test
  public void testHistogram2() throws IOException {

    Histogram histogram = new Histogram(0, 255);
    int[][][] rgbEmptyMatrix = {
            {{0, 0, 0}, {0, 0, 0}},
            {{0, 0, 0}, {0, 0, 0}}
    };
    histogram.createHistogram(rgbEmptyMatrix);
    assertEquals(4,histogram.calculateMaxCount());
    assertEquals(0,histogram.findPeakValue(histogram.histogramR));
    assertEquals(0,histogram.findPeakValue(histogram.histogramG));
    assertEquals(0,histogram.findPeakValue(histogram.histogramB));


  }
  @Test
  public void testHistogramValues() throws IOException {

    Histogram histogram = new Histogram(0, 255);
    int[][][] rgbEmptyMatrix = {
            {{8, 0, 0}, {1, 0, 0}},
            {{3, 0, 0}, {5, 0, 0}}
    };
    histogram.createHistogram(rgbEmptyMatrix);
    assertEquals(4,histogram.calculateMaxCount());

    assertEquals(1,histogram.findPeakValue(histogram.histogramR));
    assertEquals(0,histogram.findPeakValue(histogram.histogramG));
    assertEquals(0,histogram.findPeakValue(histogram.histogramB));


    assertEquals(1, histogram.histogramR[1]);
    assertEquals(1, histogram.histogramR[3]);
    assertEquals(1, histogram.histogramR[5]);
    assertEquals(1, histogram.histogramR[8]);

  }

  @Test
  public void testHistogramValues2() throws IOException {

    Histogram histogram = new Histogram(0, 255);
    int[][][] rgbEmptyMatrix = {
            {{1, 5, 0}, {1, 9, 0}},
            {{1, 2, 0}, {1, 2, 7}}
    };
    histogram.createHistogram(rgbEmptyMatrix);
    assertEquals(4,histogram.calculateMaxCount());

    assertEquals(1,histogram.findPeakValue(histogram.histogramR));
    assertEquals(2,histogram.findPeakValue(histogram.histogramG));
    assertEquals(0,histogram.findPeakValue(histogram.histogramB));


    assertEquals(2, histogram.histogramG[2]);
    assertEquals(1, histogram.histogramG[5]);
    assertEquals(1, histogram.histogramG[9]);

    assertEquals(4, histogram.histogramR[1]);

    assertEquals(3, histogram.histogramB[0]);
    assertEquals(1, histogram.histogramB[7]);


  }

  @Test
  public void testHistogramValues4() throws IOException {
    // Test case with a single pixel and maximum values
    Histogram histogram = new Histogram(0, 255);
    int[][][] rgbMatrix = {
            {{255, 255, 255}}
    };
    histogram.createHistogram(rgbMatrix);
    assertEquals(1, histogram.calculateMaxCount());
    assertEquals(255, histogram.findPeakValue(histogram.histogramR));
    assertEquals(255, histogram.findPeakValue(histogram.histogramG));
    assertEquals(255, histogram.findPeakValue(histogram.histogramB));
  }

  @Test
  public void testHistogramValues5() throws IOException {
    // Test case with a single color image
    Histogram histogram = new Histogram(0, 255);
    int[][][] rgbMatrix = new int[256][256][3];
    for (int i = 0; i < 256; i++) {
      for (int j = 0; j < 256; j++) {
        rgbMatrix[i][j][0] = 100;
        rgbMatrix[i][j][1] = 50;
        rgbMatrix[i][j][2] = 200;
      }
    }
    histogram.createHistogram(rgbMatrix);
    assertEquals(256 * 256, histogram.calculateMaxCount());
    assertEquals(100, histogram.findPeakValue(histogram.histogramR));
    assertEquals(50, histogram.findPeakValue(histogram.histogramG));
    assertEquals(200, histogram.findPeakValue(histogram.histogramB));
  }

  @Test
  public void testHistogramValues6() throws IOException {
    // Test case with a random distribution of pixel values
    Histogram histogram = new Histogram(0, 255);
    int[][][] rgbMatrix1 = {
            {{10, 20, 30}, {40, 50, 60}, {70, 80, 90}},
            {{100, 110, 120}, {130, 140, 150}, {160, 170, 180}},
            {{190, 200, 210}, {220, 230, 240}, {250, 255, 0}}
    };
    histogram.createHistogram(rgbMatrix1);
    assertEquals(1, histogram.calculateMaxCount());
    assertEquals(10, histogram.findPeakValue(histogram.histogramR));
    assertEquals(20, histogram.findPeakValue(histogram.histogramG));
    assertEquals(0, histogram.findPeakValue(histogram.histogramB));
  }


  @Test(expected = IllegalArgumentException.class)
  public void testHistogramException1() throws IOException {
    // Test case with an invalid range (minValue > maxValue)
    new Histogram(255, 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testHistogramException2() throws IOException {
    // Test case with a negative minimum value
    new Histogram(-10, 255);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testHistogramException3() throws IOException {
    // Test case with a maximum value less than the minimum value
    new Histogram(0, -5);
  }


  @Test
  public void testColorCorrectionHistogram() {

    pngJpgImage.colorCorrectImage(imageName, "color-correct-img", 0);


    int[][][] imageData = pngJpgImage.getRgbDataMap("color-correct-img");
    Histogram histogram = new Histogram(0, 255);

    histogram.createHistogram(imageData);
    assertEquals(1,histogram.calculateMaxCount());

    assertEquals(50,histogram.findPeakValue(histogram.histogramR));
    assertEquals(50,histogram.findPeakValue(histogram.histogramG));
    assertEquals(50,histogram.findPeakValue(histogram.histogramB));


    assertEquals(1, histogram.histogramR[63]);
    assertEquals(1, histogram.histogramR[167]);
    assertEquals(1, histogram.histogramR[249]);


    assertEquals(1, histogram.histogramG[50]);
    assertEquals(1, histogram.histogramG[63]);
    assertEquals(1, histogram.histogramG[245]);





  }

  @Test
  public void testColorCorrectionHistogram2() {

    int[][][] imageData = {
            {{245, 10, 10, },{10, 245, 10, },},
            {{10, 10, 245, },{245, 245, 245, },}
    };

    Histogram histogram = new Histogram(0, 255);

    histogram.createHistogram(imageData);
    assertEquals(2,histogram.calculateMaxCount());

    assertEquals(10,histogram.findPeakValue(histogram.histogramR));
    assertEquals(10,histogram.findPeakValue(histogram.histogramG));
    assertEquals(10,histogram.findPeakValue(histogram.histogramB));

    assertEquals(2, histogram.histogramR[10]);
    assertEquals(2, histogram.histogramR[245]);

    assertEquals(2, histogram.histogramG[10]);
    assertEquals(2, histogram.histogramR[245]);

    assertEquals(2, histogram.histogramB[10]);
    assertEquals(2, histogram.histogramB[245]);


  }


  @Test
  public void testLevelAdjustmentWithHistogram() {

    Histogram sourceHistogram = new Histogram(0, 255);
    int[][][] sourceImageData = pngJpgImage.getRgbDataMap(imageName);
    sourceHistogram.createHistogram(sourceImageData);
    assertEquals(1,sourceHistogram.calculateMaxCount());

    assertEquals(53,sourceHistogram.findPeakValue(sourceHistogram.histogramR));
    assertEquals(49,sourceHistogram.findPeakValue(sourceHistogram.histogramG));
    assertEquals(50,sourceHistogram.findPeakValue(sourceHistogram.histogramB));


    assertEquals(1, sourceHistogram.histogramR[53]);
    assertEquals(1, sourceHistogram.histogramR[66]);
    assertEquals(1, sourceHistogram.histogramR[167]);
    assertEquals(1, sourceHistogram.histogramR[249]);

    assertEquals(1, sourceHistogram.histogramG[49]);
    assertEquals(1, sourceHistogram.histogramG[62]);
    assertEquals(1, sourceHistogram.histogramG[163]);
    assertEquals(1, sourceHistogram.histogramG[245]);

    assertEquals(1, sourceHistogram.histogramB[50]);
    assertEquals(1, sourceHistogram.histogramB[63]);
    assertEquals(1, sourceHistogram.histogramB[164]);
    assertEquals(1, sourceHistogram.histogramB[246]);

    pngJpgImage.applyLevelsAdjustment(20, 100, 255, imageName, "color-correct-img");


    int[][][] imageData = pngJpgImage.getRgbDataMap("color-correct-img");


    Histogram histogram = new Histogram(0, 255);

    histogram.createHistogram(imageData);
    assertEquals(1,histogram.calculateMaxCount());

    assertEquals(57,histogram.findPeakValue(histogram.histogramR));
    assertEquals(51,histogram.findPeakValue(histogram.histogramG));
    assertEquals(52,histogram.findPeakValue(histogram.histogramB));

    assertEquals(1, histogram.histogramR[57]);
    assertEquals(1, histogram.histogramR[78]);
    assertEquals(0, histogram.histogramR[167]);
    assertEquals(0, histogram.histogramR[249]);


  }




}