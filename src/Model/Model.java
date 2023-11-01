package Model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

/**
 * The `Model.Model` class represents the application's model. The model interacts with
 * various image operations through the `Model.ImageOperations` interface to perform image-related tasks.
 */
public class Model implements IModel {
  private String input;
  private String result; // Store the result from processing the file

  static ImageOperations imageObj = null;

  /**
   * Initializes the model with default values.
   */
  public Model() {
    input = "";
    result = "";
  }

  /**
   * Sets the input string.
   *
   * @param i The input string.
   */
  @Override
  public void setString(String i) {
    input = i;
  }

  /**
   * Gets the input string.
   *
   * @return The input string.
   */
  @Override
  public String getString() {
    return input;
  }

  /**
   * Executes a script loaded from a file, processing each line as a command.
   *
   * @param scriptFilename The filename of the script to execute.
   */
  public void executeScriptFromFile(String scriptFilename) {
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
          parseAndExecute(line);
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
   * Retrieves the result from processing commands, if available.
   *
   * @return The result string obtained from command execution.
   */
  public String getResult() {
    return result;
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

  /**
   * Parses the file and executes each line as a command and decides what operation is to be
   * performed on it.
   *
   * @param command The command to parse and execute.
   * @throws IOException If an I/O error occurs while executing the command.
   */
  public void parseAndExecute(String command) throws IOException {
    String[] parts = command.split(" ");
    if (parts.length < 2) {
      System.out.println("Invalid command: " + command);
      return;
    }

    String cmd = parts[0];
    String arg1 = parts[1];
    String arg2 = parts.length > 2 ? parts[2] : null;
    String extension = identifyFileFormat(arg1);

    if (!Objects.equals(parts[0], "run")) {

      if (extension != null) {
        if ((extension.equalsIgnoreCase("png"))) {
          imageObj = new PNGImage();
        } else if (extension.equalsIgnoreCase("ppm")) {
          imageObj = new PPMImage();
        } else if (extension.equalsIgnoreCase("jpg")
                || (extension.equalsIgnoreCase("jpeg"))) {
          imageObj = new JPGImage();
        } else {
          throw new IllegalArgumentException("Unsupported image format");
        }
      }
    }

    switch (cmd) {
      case "load":
        imageObj.loadImage(arg1, arg2);
        break;
      case "save":
        imageObj.saveImage(arg1, arg2);
        break;
      case "horizontal-flip":
        if (parts.length < 3) {
          System.out.println("Invalid 'horizontal-flip' command: Usage is 'horizontal-flip " +
                  "source-image-name dest-image-name'");
        } else {
          String sourceImageName = parts[1];
          String destImageName = parts[2];
          imageObj.horizontalFlipImage(sourceImageName, destImageName);
        }
        break;
      case "vertical-flip":
        imageObj.verticalFlipImage(arg1, arg2);
        break;
      case "sharpen":
        imageObj.sharpenImage(arg1, arg2);
        break;
      case "blur":
        imageObj.blurImage(arg1, arg2);
        break;
      case "brighten":
        if (parts.length < 4) {
          System.out.println("Invalid 'brighten' command: Usage is 'brighten increment " +
                  "source-image-name dest-image-name'");
        } else {
          int increment = Integer.parseInt(parts[1]);
          String sourceImageName = parts[2];
          String destImageName = parts[3];
          imageObj.brightenImage(sourceImageName, destImageName, increment);
        }
        break;
      case "sepia":
        imageObj.sepiaImage(arg1, arg2);
        break;
      case "rgb-combine":
        if (parts.length < 5) {
          System.out.println("Invalid 'rgb-combine' command: Usage is 'rgb-combine " +
                  "combined-image red-image green-image blue-image'");
        } else {
          String combinedImageName = parts[1];
          String redImageName = parts[2];
          String greenImageName = parts[3];
          String blueImageName = parts[4];
          imageObj.combineRGBImages(combinedImageName, redImageName, greenImageName, blueImageName);
        }
        break;
      case "rgb-split":
        if (parts.length < 4) {
          System.out.println("Invalid 'rgb-split' command: Usage is 'rgb-split image-name " +
                  "dest-image-name-red dest-image-name-green dest-image-name-blue'");
        } else {
          String sourceImageName = parts[1];
          String destImageNameRed = parts[2];
          String destImageNameGreen = parts[3];
          String destImageNameBlue = parts[4];
          imageObj.rgbSplitImage(sourceImageName, destImageNameRed, destImageNameGreen,
                  destImageNameBlue);
        }
        break;

      case "red-component":
        if (parts.length < 3) {
          System.out.println("Invalid 'extract-component' command: Usage is 'red-component " +
                  "source-image-name dest-image-name'");
        } else {
          String sourceImageName = parts[1];
          String destImageName = parts[2];
          imageObj.extractComponent(sourceImageName, destImageName, "red");
        }
        break;

      case "green-component":
        if (parts.length < 3) {
          System.out.println("Invalid 'extract-component' command: Usage is 'green-component " +
                  "source-image-name dest-image-name'");
        } else {
          String sourceImageName = parts[1];
          String destImageName = parts[2];
          imageObj.extractComponent(sourceImageName, destImageName, "green");
        }
        break;
      case "blue-component":
        if (parts.length < 3) {
          System.out.println("Invalid 'blue-component' command: Usage is 'blue-component " +
                  "source-image-name dest-image-name'");
        } else {
          String sourceImageName = parts[1];
          String destImageName = parts[2];
          imageObj.extractComponent(sourceImageName, destImageName, "blue");
        }
        break;

      case "value-component":
        if (parts.length < 3) {
          System.out.println("Invalid 'value-component' command: Usage is 'value-component " +
                  "source-image-name dest-image-name'");
        } else {
          String sourceImageName = parts[1];
          String destImageName = parts[2];
          imageObj.extractComponent(sourceImageName, destImageName, "value");
        }
        break;

      case "intensity-component":
        if (parts.length < 3) {
          System.out.println("Invalid 'intensity-component' command: Usage is 'intensity-component"
                  + " source-image-name dest-image-name'");
        } else {
          String sourceImageName = parts[1];
          String destImageName = parts[2];
          imageObj.extractComponent(sourceImageName, destImageName, "intensity");
        }
        break;

      case "luma-component":
        if (parts.length < 3) {
          System.out.println("Invalid 'luma-component' command: Usage is 'luma-component " +
                  "source-image-name dest-image-name'");
        } else {
          String sourceImageName = parts[1];
          String destImageName = parts[2];
          imageObj.extractComponent(sourceImageName, destImageName, "luma");
        }
        break;

      case "run":
        String scriptFilename = parts[1];
        executeScriptFromFile(scriptFilename);
        break;

      default:
        System.out.println("Invalid command: " + command);
        break;
    }
  }
}
