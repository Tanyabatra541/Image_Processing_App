package controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.util.Objects;
import java.util.Scanner;

import model.ImageOperations;
import model.JPGImage;
import model.PNGImage;
import model.PPMImage;

import static java.lang.System.exit;

/**
 * The `Controller` class serves as the controller in the MVC architectural pattern.
 * It handles user interactions from the view, processes user input, and communicates with the
 * model and view components.
 */
public class Controller {

  private final Reader reader;

  public static ImageOperations IMAGE_OBJ = null;

  /**
   * Constructs a new Controller instance.
   *
   * @param in The input source, such as a file or command line.
   */
  public Controller(Reader in) {
    reader = in;
  }

  /**
   * Runs the program by continuously prompting the user for commands until the "exit" command is
   * entered.
   */
  public void runProgram() {

    Scanner scanner = new Scanner(reader);
    while (true) {
      System.out.print("Enter a command (or type 'exit' to quit): ");
      String command = scanner.nextLine();
      if (command.equals("exit")) {
        break;
      }
      try {
        parseAndExecute(command);
        System.out.println(output.toString());
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }
    }
  }

  /**
   * The parts of the command entered by the user.
   */
  public static String[] PARTS;

  static Appendable output = new StringBuilder();

  private static void passOrFail(boolean pass) throws IOException {
    if (pass) {
      output.append("Operation successful");
    } else {
      output.append("Operation failed");
    }

  }

  /**
   * Parses the file and executes each line as a command and decides what operation is to be
   * performed on it.
   *
   * @param command The command to parse and execute.
   * @throws IOException If an I/O error occurs while executing the command.
   */
  public static void parseAndExecute(String command) throws IOException {
    PARTS = command.split(" ");
    if (PARTS.length < 2) {
      System.out.println("Invalid command: " + command);
      return;
    }

    String cmd = PARTS[0];
    String arg1 = PARTS[1];
    String arg2 = PARTS.length > 2 ? PARTS[2] : null;
    String extension = identifyFileFormat(arg1);

    if (!Objects.equals(PARTS[0], "run")) {

      if (extension != null) {
        if ((extension.equalsIgnoreCase("png"))) {
          IMAGE_OBJ = new PNGImage();
        } else if (extension.equalsIgnoreCase("ppm")) {
          IMAGE_OBJ = new PPMImage();
        } else if (extension.equalsIgnoreCase("jpg")
                || (extension.equalsIgnoreCase("jpeg"))) {
          IMAGE_OBJ = new JPGImage();
        } else {
          System.out.println("Unsupported image format");
        }
      }
    }


    switch (cmd) {
      case "load":
        IMAGE_OBJ.loadImage(arg1, arg2);
        break;
      case "save":
        IMAGE_OBJ.saveImage(arg1, arg2);
        break;
      case "horizontal-flip":
        if (PARTS.length < 3) {
          System.out.println("Invalid 'horizontal-flip' command: Usage is 'horizontal-flip "
                  + "source-image-name dest-image-name'");
        } else {
          String sourceImageName = PARTS[1];
          String destImageName = PARTS[2];
          boolean pass = IMAGE_OBJ.horizontalFlipImage(sourceImageName, destImageName);
          passOrFail(pass);
        }
        break;
      case "vertical-flip":
        IMAGE_OBJ.verticalFlipImage(arg1, arg2);
        break;
      case "sharpen":
        if (PARTS.length > 3 && PARTS[3].equals("split")) {
          int splitPercentage = Integer.parseInt(PARTS[4]);
          IMAGE_OBJ.sharpenImage(arg1, arg2, splitPercentage);
        } else {
          IMAGE_OBJ.sharpenImage(arg1, arg2, 0);
        }
        break;
      case "blur":
        if (PARTS.length > 3 && PARTS[3].equals("split")) {
          int splitPercentage = Integer.parseInt(PARTS[4]);
          IMAGE_OBJ.blurImage(arg1, arg2, splitPercentage);
        } else {
          IMAGE_OBJ.blurImage(arg1, arg2, 0);
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
          IMAGE_OBJ.brightenImage(sourceImageName, destImageName, increment);
        }
        break;
      case "sepia":
        if (PARTS.length > 3 && PARTS[3].equals("split")) {
          int splitPercentage = Integer.parseInt(PARTS[4]);
          IMAGE_OBJ.sepiaImage(arg1, arg2, splitPercentage);
        } else {
          IMAGE_OBJ.sepiaImage(arg1, arg2, 0);
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
          IMAGE_OBJ.combineRGBImages(combinedImageName, redImageName, greenImageName,
                  blueImageName);
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
          IMAGE_OBJ.rgbSplitImage(sourceImageName, destImageNameRed, destImageNameGreen,
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
          IMAGE_OBJ.extractComponent(sourceImageName, destImageName, "red");
        }
        break;

      case "green-component":
        if (PARTS.length < 3) {
          System.out.println("Invalid 'extract-component' command: Usage is 'green-component "
                  + "source-image-name dest-image-name'");
        } else {
          String sourceImageName = PARTS[1];
          String destImageName = PARTS[2];
          IMAGE_OBJ.extractComponent(sourceImageName, destImageName, "green");
        }
        break;
      case "blue-component":
        if (PARTS.length < 3) {
          System.out.println("Invalid 'blue-component' command: Usage is 'blue-component "
                  + "source-image-name dest-image-name'");
        } else {
          String sourceImageName = PARTS[1];
          String destImageName = PARTS[2];
          IMAGE_OBJ.extractComponent(sourceImageName, destImageName, "blue");
        }
        break;

      case "value-component":
        if (PARTS.length < 3) {
          System.out.println("Invalid 'value-component' command: Usage is 'value-component "
                  + "source-image-name dest-image-name'");
        } else {
          String sourceImageName = PARTS[1];
          String destImageName = PARTS[2];
          IMAGE_OBJ.extractComponent(sourceImageName, destImageName, "value");
        }
        break;
      case "intensity-component":
        if (PARTS.length < 3) {
          System.out.println("Invalid 'intensity-component' command: Usage is 'intensity-component"
                  + " source-image-name dest-image-name'");
        } else {
          String sourceImageName = PARTS[1];
          String destImageName = PARTS[2];
          IMAGE_OBJ.extractComponent(sourceImageName, destImageName, "intensity");
        }
        break;
      case "luma-component":
        if (PARTS.length < 3) {
          System.out.println("Invalid 'luma-component' command: Usage is 'luma-component "
                  + "source-image-name dest-image-name'");
        } else {
          String sourceImageName = PARTS[1];
          String destImageName = PARTS[2];
          IMAGE_OBJ.extractComponent(sourceImageName, destImageName, "luma");
        }
        break;
      case "color-correct":
        if (PARTS.length > 3 && PARTS[3].equals("split")) {
          int splitPercentage = Integer.parseInt(PARTS[4]);
          IMAGE_OBJ.colorCorrectImage(arg1, arg2, splitPercentage);
        } else {
          IMAGE_OBJ.colorCorrectImage(arg1, arg2, 0);
        }
        break;
      case "histogram":
        if (PARTS.length < 3) {
          System.out.println("Invalid 'histogram' command: Usage is 'histogram "
                  + "source-image-name dest-image-name'");
        } else {
          String sourceImageName = PARTS[1];
          String destImageName = PARTS[2];
          IMAGE_OBJ.createHistogram(sourceImageName, destImageName);
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
            IMAGE_OBJ.applyLevelsAdjustment(b, m, w, sourceImageName, destImageName,
                    splitPercentage);
          } else {
            IMAGE_OBJ.applyLevelsAdjustment(b, m, w, sourceImageName, destImageName,
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
            IMAGE_OBJ.convertToGrayscale(sourceImageName, destImageName, splitPercentage);
          } else {
            IMAGE_OBJ.convertToGrayscale(sourceImageName, destImageName, 0);
          }
        }
        break;

      case "compress":
        double percentage = Double.parseDouble(PARTS[1]);
        String sourceImageName = PARTS[2];
        String destImageName = PARTS[3];
        IMAGE_OBJ.compress(sourceImageName, destImageName, percentage);
        break;
      case "-file":
        String scriptFilename = PARTS[1];
        executeScriptFromFile(scriptFilename);
        exit(0);
        break;
      default:
        System.out.println("Invalid command: " + command);
        break;
    }
  }

  /**
   * Executes a script loaded from a file, processing each line as a command.
   *
   * @param scriptFilename The filename of the script to execute.
   */
  public static void executeScriptFromFile(String scriptFilename) {
    try {
      File scriptFile = new File(scriptFilename);
      if (!scriptFile.exists()) {
        System.out.println("Script file not found: " + scriptFilename);
        return;
      }

      Scanner sc = new Scanner(scriptFile);
      while (sc.hasNextLine()) {
        String line = sc.nextLine().trim();
        if (!line.startsWith("#") && !line.isEmpty()) { // Skip comments and empty lines
          Controller.parseAndExecute(line);
        }
      }
      sc.close();
    } catch (FileNotFoundException e) {
      System.out.println("Error reading script file: " + e.getMessage());
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
}

