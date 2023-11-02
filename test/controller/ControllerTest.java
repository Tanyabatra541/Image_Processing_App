package controller;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.awt.event.ActionEvent;
import org.junit.Before;
import org.junit.Test;
import java.awt.event.ActionEvent;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.View;

import model.IModel;
import model.JPGImage;
import model.Model;
import model.PNGImage;
import view.IView;
import view.JFrameView;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;


public class ControllerTest {

  private static JPGImage pngJpgImage;

  private static String imageName = "outputJPG";
  private static String imagePath = "outputJPG.jpg";

  static int[][][] rgbMatrix = new int[2][2][3];

  private Model model;
  private IView view;
  private Controller controller;

  int[][][] originalMatrix = {
          {{255, 0, 0}, {0, 255, 0}},
          {{0, 0, 255}, {255, 255, 255}}
  };


  @Before
  public void setUp() {
    pngJpgImage = new JPGImage();
    createAndSaveJPG(originalMatrix, imageName, imagePath);
    model = new Model();
    view = new JFrameView(null);
    controller = new Controller(model, view);
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
    controller.parseAndExecute("load "+ imagePath + " "+ imageName);
    assertTrue(pngJpgImage.getImageMap().containsKey(imageName));
  }
}