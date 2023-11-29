package controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

import javax.imageio.ImageIO;

import model.ImageOperations;
import model.JPGImage;
import model.PNGImage;
import model.PPMImage;
import view.ImageEditorView;
//import view.IView;

import static java.lang.System.exit;

/**
 * The `Controller` class serves as the controller in the MVC architectural pattern.
 * It handles user interactions from the view, processes user input, and communicates with the
 * model and view components.
 */
public class Controller implements ControllerFeatures {

  public ImageOperations imageObj = null;

  private ImageEditorView view;

  private Reader reader;

  private String extension;

  public Controller(Reader reader) {
    this.reader = reader;
  }

  /**
   * The parts of the command entered by the user.
   */
  public static String[] PARTS;

  /**
   * Parses the file and executes each line as a command and decides what operation is to be
   * performed on it.
   *
   * @param command The command to parse and execute.
   * @throws IOException If an I/O error occurs while executing the command.
   */
  public void parseAndExecute(String command) throws IOException {
    int[][][] rgb;
    double[][] pixels;
    System.out.println("Executing command: " + command);
    PARTS = command.split(" ");
    if (PARTS.length < 2) {
      System.out.println("Invalid command: " + command);
      return;
    }
    String cmd = PARTS[0];
    String arg1 = PARTS[1];
    String arg2 = PARTS.length > 2 ? PARTS[2] : null;
    extension = identifyFileFormat(arg1);
    if (!Objects.equals(PARTS[0], "run")) {
      if (extension != null) {
        if ((extension.equalsIgnoreCase("png"))) {
          imageObj = new PNGImage();
        } else if (extension.equalsIgnoreCase("ppm")) {
          imageObj = new PPMImage();
        } else if (extension.equalsIgnoreCase("jpg")
                || (extension.equalsIgnoreCase("jpeg"))) {
          imageObj = new JPGImage();
        } else {
          System.out.println("Unsupported image format");
        }
      }
    }
    switch (cmd) {
      case "load":
        System.out.println("in load");
        System.out.println("arg1" + arg1);
        rgb = pngJpgOrPpm(arg1);
        imageObj.loadImage(arg2, rgb);
        break;
      case "save":
        imageObj.saveImage(arg1, arg2);
        break;
      case "horizontal-flip":
        if (PARTS.length < 3) {
          System.out.println("Invalid 'horizontal-flip' command: Usage is 'horizontal-flip "
                  + "source-image-name dest-image-name'");
        } else {
          String sourceImageName = PARTS[1];
          String destImageName = PARTS[2];
          imageObj.horizontalFlipImage(sourceImageName, destImageName);
        }
        break;
      case "vertical-flip":
        imageObj.verticalFlipImage(arg1, arg2);
        break;
      case "sharpen":
        if (PARTS.length > 3 && PARTS[3].equals("split")) {
          int splitPercentage = Integer.parseInt(PARTS[4]);
          imageObj.sharpenImage(arg1, arg2, splitPercentage);
        } else {
          imageObj.sharpenImage(arg1, arg2, 0);
        }
        break;
      case "blur":
        if (PARTS.length > 3 && PARTS[3].equals("split")) {
          int splitPercentage = Integer.parseInt(PARTS[4]);
          imageObj.blurImage(arg1, arg2, splitPercentage);
        } else {
          imageObj.blurImage(arg1, arg2, 0);
        }
        break;
      case "brighten":
        if (PARTS.length < 4) {
          System.out.println("Invalid 'brighten' command: Usage is 'brighten increment "
                  + "source-image-name dest-image-name'");
        } else {
          int increment = Integer.parseInt(PARTS[1]);
          String sourceImageName = PARTS[2];
          String destImageName = PARTS[3];
          imageObj.brightenImage(sourceImageName, destImageName, increment);
        }
        break;
      case "sepia":
        if (PARTS.length > 3 && PARTS[3].equals("split")) {
          int splitPercentage = Integer.parseInt(PARTS[4]);
          imageObj.sepiaImage(arg1, arg2, splitPercentage);
        } else {
          imageObj.sepiaImage(arg1, arg2, 0);
        }
        break;
      case "rgb-combine":
        if (PARTS.length < 5) {
          System.out.println("Invalid 'rgb-combine' command: Usage is 'rgb-combine "
                  + "combined-image red-image green-image blue-image'");
        } else {
          String combinedImageName = PARTS[1];
          String redImageName = PARTS[2];
          String greenImageName = PARTS[3];
          String blueImageName = PARTS[4];
          imageObj.combineRGBImages(combinedImageName, redImageName, greenImageName, blueImageName);
        }
        break;
      case "rgb-split":
        if (PARTS.length < 4) {
          System.out.println("Invalid 'rgb-split' command: Usage is 'rgb-split image-name "
                  + "dest-image-name-red dest-image-name-green dest-image-name-blue'");
        } else {
          String sourceImageName = PARTS[1];
          String destImageNameRed = PARTS[2];
          String destImageNameGreen = PARTS[3];
          String destImageNameBlue = PARTS[4];
          imageObj.rgbSplitImage(sourceImageName, destImageNameRed, destImageNameGreen,
                  destImageNameBlue);
        }
        break;

      case "red-component":
        if (PARTS.length < 3) {
          System.out.println("Invalid 'extract-component' command: Usage is 'red-component "
                  + "source-image-name dest-image-name'");
        } else {
          String sourceImageName = PARTS[1];
          String destImageName = PARTS[2];
          imageObj.extractComponent(sourceImageName, destImageName, "red");
        }
        break;

      case "green-component":
        if (PARTS.length < 3) {
          System.out.println("Invalid 'extract-component' command: Usage is 'green-component "
                  + "source-image-name dest-image-name'");
        } else {
          String sourceImageName = PARTS[1];
          String destImageName = PARTS[2];
          imageObj.extractComponent(sourceImageName, destImageName, "green");
        }
        break;
      case "blue-component":
        if (PARTS.length < 3) {
          System.out.println("Invalid 'blue-component' command: Usage is 'blue-component "
                  + "source-image-name dest-image-name'");
        } else {
          String sourceImageName = PARTS[1];
          String destImageName = PARTS[2];
          imageObj.extractComponent(sourceImageName, destImageName, "blue");
        }
        break;

      case "value-component":
        if (PARTS.length < 3) {
          System.out.println("Invalid 'value-component' command: Usage is 'value-component "
                  + "source-image-name dest-image-name'");
        } else {
          String sourceImageName = PARTS[1];
          String destImageName = PARTS[2];
          imageObj.extractComponent(sourceImageName, destImageName, "value");
        }
        break;
      case "intensity-component":
        if (PARTS.length < 3) {
          System.out.println("Invalid 'intensity-component' command: Usage is 'intensity-component"
                  + " source-image-name dest-image-name'");
        } else {
          String sourceImageName = PARTS[1];
          String destImageName = PARTS[2];
          imageObj.extractComponent(sourceImageName, destImageName, "intensity");
        }
        break;
      case "luma-component":
        if (PARTS.length < 3) {
          System.out.println("Invalid 'luma-component' command: Usage is 'luma-component "
                  + "source-image-name dest-image-name'");
        } else {
          String sourceImageName = PARTS[1];
          String destImageName = PARTS[2];
          imageObj.extractComponent(sourceImageName, destImageName, "luma");
        }
        break;
      case "color-correct":
        if (PARTS.length > 3 && PARTS[3].equals("split")) {
          int splitPercentage = Integer.parseInt(PARTS[4]);
          imageObj.colorCorrectImage(arg1, arg2, splitPercentage);
        } else {
          imageObj.colorCorrectImage(arg1, arg2, 0);
        }
        break;
      case "histogram":
        if (PARTS.length < 3) {
          System.out.println("Invalid 'histogram' command: Usage is 'histogram "
                  + "source-image-name dest-image-name'");
        } else {
          String sourceImageName = PARTS[1];
          String destImageName = PARTS[2];
          imageObj.createHistogram(sourceImageName, destImageName);
        }
        break;

      case "levels-adjust":
        if (PARTS.length < 6) {
          System.out.println("Invalid 'levels-adjust' command: Usage is 'levels-adjust "
                  + "b m w source-image-name dest-image-name'");
        } else {
          String sourceImageName = PARTS[4];
          String destImageName = PARTS[5];
          int b = Integer.parseInt(PARTS[1]);
          int m = Integer.parseInt(PARTS[2]);
          int w = Integer.parseInt(PARTS[3]);
          if (PARTS.length > 6 && PARTS[6].equals("split")) {
            int splitPercentage = Integer.parseInt(PARTS[7]);
            imageObj.applyLevelsAdjustment(b, m, w, sourceImageName, destImageName,
                    splitPercentage);
          } else {
            imageObj.applyLevelsAdjustment(b, m, w, sourceImageName, destImageName,
                    0);
          }
        }
        break;

      case "greyscale":
        if (PARTS.length < 3) {
          System.out.println("Invalid 'brighten' command: Usage is 'greyscale "
                  + "source-image-name dest-image-name'");
        } else {
          String sourceImageName = PARTS[1];
          String destImageName = PARTS[2];
          if (PARTS.length > 3 && PARTS[3].equals("split")) {
            int splitPercentage = Integer.parseInt(PARTS[4]);
            imageObj.convertToGrayscale(sourceImageName, destImageName, splitPercentage);
          } else {
            imageObj.convertToGrayscale(sourceImageName, destImageName, 0);
          }
        }
        break;

      case "compress":
        double percentage = Double.parseDouble(PARTS[1]);
        String sourceImageName = PARTS[2];
        String destImageName = PARTS[3];
        imageObj.compress(sourceImageName, destImageName, percentage);
        break;
      case "-file":
        String scriptFilename = PARTS[1];
        executeScript(scriptFilename);
        exit(0);
        break;
      default:
        System.out.println("Invalid command: " + command);
        break;
    }
  }

  /**
   * Executes the commands using given reader
   */
  public void executeCommands() {
    try {
      Scanner sc = new Scanner(reader);
      while (sc.hasNextLine()) {
        String line = sc.nextLine().trim();
        if (!line.startsWith("#") && !line.isEmpty()) { // Skip comments and empty lines
          parseAndExecute(line);
        }
      }
      sc.close();
    } catch (FileNotFoundException e) {
      System.out.println("Error executing command: " + e.getMessage());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


  private void executeScript(String scriptFilename) {
    try {
      Scanner sc = new Scanner(new FileReader(scriptFilename));
      while (sc.hasNextLine()) {
        String line = sc.nextLine().trim();
        if (!line.startsWith("#") && !line.isEmpty()) { // Skip comments and empty lines
          parseAndExecute(line);
        }
      }
      sc.close();
    } catch (FileNotFoundException e) {
      System.out.println("Error executing command: " + e.getMessage());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Identifies the file format of an image based on its file extension.
   *
   * @param filePath The path to the image file.
   * @return The file format or null if the format is unsupported or not recognized.
   */
  public static String identifyFileFormat(String filePath) {
    // Get the index of the last dot in the file path
    int lastDotIndex = filePath.lastIndexOf('.');

    if (lastDotIndex > 0) {
      // Extract the substring after the last dot
      String fileExtension = filePath.substring(lastDotIndex + 1);

      // Convert the file extension to lowercase for consistency
      return fileExtension.toLowerCase();
    } else {
      // No file extension found
      return null;
    }
  }

  public int[][][] convertPNGToRGB(String imagePath) {
    int HEIGHT;
    int WIDTH;
    try {
      File imageFile = new File(imagePath);
//      URI uri = new URI(imagePath);
//      File imageFile = new File(uri);
      if (!imageFile.exists()) {
        System.out.println("Image file not found: " + imagePath);
        return null;
      }

      BufferedImage bufferedImage = ImageIO.read(imageFile);
      if (bufferedImage == null) {
        System.out.println("Failed to read image from: " + imagePath);
        return null;
      }

      WIDTH = bufferedImage.getWidth();
      HEIGHT = bufferedImage.getHeight();

      int[][][] imageRGBData = new int[HEIGHT][WIDTH][3];

      for (int y = 0; y < HEIGHT; y++) {
        for (int x = 0; x < WIDTH; x++) {
          int rgb = bufferedImage.getRGB(x, y);
          imageRGBData[y][x][0] = (rgb >> 16) & 0xFF; // Red component
          imageRGBData[y][x][1] = (rgb >> 8) & 0xFF;  // Green component
          imageRGBData[y][x][2] = rgb & 0xFF;         // Blue component
        }
      }
      return imageRGBData;
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("Error while converting PNG to RGB: " + imagePath);
      return null;
    }
  }

  public int[][][] readImageRGBData(String filename) {
    Scanner sc = null;

    try {
      sc = new Scanner(new FileInputStream(filename));
//      URI uri = new URI(filename);
//      sc = new Scanner(new FileInputStream(new File(uri)));
    } catch (FileNotFoundException e) {
      System.out.println("File " + filename + " not found!");
    }

    StringBuilder builder = new StringBuilder();
    while (sc.hasNextLine()) {
      String s = sc.nextLine();
      if (s.charAt(0) != '#') {
        builder.append(s).append(System.lineSeparator());
      }
    }

    sc = new Scanner(builder.toString());

    String token = sc.next();
    if (!token.equals("P3")) {
      System.out.println("Invalid PPM file: plain RAW file should begin with P3");
    }
    int width = sc.nextInt();
    int height = sc.nextInt();

    int[][][] imageRGBData = new int[height][width][3];
    int maxValue = sc.nextInt(); // Read the maximum color value

    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        imageRGBData[i][j][0] = sc.nextInt(); // Red component
        imageRGBData[i][j][1] = sc.nextInt(); // Green component
        imageRGBData[i][j][2] = sc.nextInt(); // Blue component
      }
    }
    return imageRGBData;
  }

  private int[][][] pngJpgOrPpm(String imagePath) {
    System.out.println("in pngJpgOrPpm function" + extension);

    if (Objects.equals(extension, "png") || Objects.equals(extension, "jpg")) {
      System.out.println("in png or jpg");
      return convertPNGToRGB(imagePath);
    } else {
      System.out.println("in ppm");
      return readImageRGBData(imagePath);
    }
  }

  @Override
  public void setView(ImageEditorView view) {
    this.view = view;
    view.addFeatures(this);
  }

  @Override
  public void load(String command, String destImageName) {
    try {
      parseAndExecute(command);
      int[][][] destImageData = imageObj.getRgbDataMap(destImageName);
      System.out.println(Arrays.deepToString(destImageData));
      view.updateImageForIndex(destImageData, 0);
      parseAndExecute("histogram " + destImageName + " " + destImageName + "-histogram");
      int[][][] destHistogramData = imageObj.getRgbDataMap(destImageName + "-histogram");
      view.updateImageForIndex(destHistogramData, 2);
    } catch (IOException e) {
      e.printStackTrace();
    }
    System.out.println("Loading image: " + command);
//    parseAndExecute(command);
  }

  @Override
  public void saveImage(String command) {
    try {
      parseAndExecute(command);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void applyFeatures(String command, String destImageName) {
    try {
      System.out.println("applyFeatures(String command" + command);
      if (command != null) {
        parseAndExecute(command);
        /*int[][][] destImageData = imageObj.getRgbDataMap(destImageName);
        view.updateImageForIndex(destImageData, 1);*/
        System.out.println("*****Applying feature on destImageName: " + destImageName);
        parseAndExecute("histogram " + destImageName + " " + destImageName + "-histogram");
       /* int[][][] destHistogramData = imageObj.getRgbDataMap(destImageName + "-histogram");
        view.updateImageForIndex(destHistogramData, 2);*/
      }
      int[][][] destImageData = imageObj.getRgbDataMap(destImageName);
      view.updateImageForIndex(destImageData, 1);
      System.out.println("Applying feature on destImageName: " + destImageName);

      int[][][] destHistogramData = imageObj.getRgbDataMap(destImageName + "-histogram");
      view.updateImageForIndex(destHistogramData, 2);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


}

