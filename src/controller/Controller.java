package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

import model.imageOperations;
import model.JPGImage;
import model.PNGImage;
import model.PPMImage;
import view.IView;

import static java.lang.System.exit;

/**
 * The `Controller` class serves as the controller in the MVC architectural pattern.
 * It handles user interactions from the view, processes user input, and communicates with the
 * model and view components.
 */
public class Controller implements ActionListener {

  private String input;
  private final String result;

  public static imageOperations imageObj = null;
  private final IView view;

  /**
   * Constructs a new Controller instance.
   *
   * @param v The view to interact with.
   */
  public Controller(IView v) {
    view = v;
    view.setListener(this);
    view.display();
    input = "";
    result = "";

  }

  /**
   * Handles user actions, such as button clicks and text input.
   *
   * @param e An ActionEvent representing the user's action.
   */
  @Override
  public void actionPerformed(ActionEvent e) {
    if (Objects.equals(e.getActionCommand(), "Execute Button")) {
      // Read the text from the input textField
      String inputText = view.getInputString();
      // Check if the input text represents a file path
      File file = new File(inputText);
      if (file.exists() && file.isFile()) {
        try {
          // Read the contents of the file and display them in the view
          StringBuilder fileContents;
          fileContents = new StringBuilder();
          try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
              fileContents.append(line).append("\n");
            }
          }
          // Pass the file contents to the model for processing
          executeScriptFromFile(inputText);
          // Display any output or result from the model in the view
          String result = getResult(); // This method depends on your model structure
          view.setEchoOutput(result);
        } catch (IOException ex) {
          // Handle any exceptions that occur during file reading
          view.setEchoOutput("Error reading the file: " + ex.getMessage());
        }
      } else {
        // Send the text to the model
        setString(inputText);
        // Clear the input textField
        view.clearInputString();
        // Finally, echo the string in the view
        String text = getString();
        view.setEchoOutput(text);
      }
    } else if (Objects.equals(e.getActionCommand(), "Exit Button")) {
      view.closeOrDispose();
      Scanner scanner = new Scanner(System.in);
      while (true) {
        System.out.print("Enter a command (or type 'exit' to quit): ");
        String command = scanner.nextLine();
        if (command.equals("exit")) {
          break;
        }
        try {
          parseAndExecute(command);
        } catch (IOException ex) {
          throw new RuntimeException(ex);
        }
      }
    }
  }

  private void setString(String i) {
    input = i;
  }

  /**
   * Gets the input string.
   *
   * @return The input string.
   */
  private String getString() {
    return input;
  }

  /**
   * Retrieves the result from processing commands, if available.
   *
   * @return The result string obtained from command execution.
   */
  private String getResult() {
    return result;
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
        imageObj.loadImage(arg1, arg2);
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

