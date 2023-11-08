package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

import model.IModel;
import model.ImageOperations;
import model.JPGImage;
import model.PNGImage;
import model.PPMImage;
import view.IView;

/**
 * The `Controller` class serves as the controller in the MVC
 * architectural pattern. It handles user interactions from the view, processes user input, and
 * communicates with the model and view components.
 */
public class Controller implements ActionListener {

  private static ImageOperations imageObj = null;
  private static IModel model = null;
  private final IView view;

  /**
   * Constructs a new controller.Controller.controller.Controller instance.
   *
   * @param m The model to work with.
   * @param v The view to interact with.
   */
  public Controller(IModel m, IView v) {
    model = m;
    view = v;
    view.setListener(this);
    view.display();
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
          StringBuilder fileContents = new StringBuilder();
          try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
              fileContents.append(line).append("\n");
            }
          }

          // Pass the file contents to the model for processing
          model.executeScriptFromFile(inputText);

          // Display any output or result from the model in the view
          String result = model.getResult(); // This method depends on your model structure
          view.setEchoOutput(result);
        } catch (IOException ex) {
          // Handle any exceptions that occur during file reading
          view.setEchoOutput("Error reading the file: " + ex.getMessage());
        }
      } else {
        // Send the text to the model
        model.setString(inputText);

        // Clear the input textField
        view.clearInputString();

        // Finally, echo the string in the view
        String text = model.getString();
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

  /**
   * Parses the file and executes each line as a command and decides what operation is to be
   * performed on it.
   *
   * @param command The command to parse and execute.
   * @throws IOException If an I/O error occurs while executing the command.
   */
  public static void parseAndExecute(String command) throws IOException {
    String[] parts = command.split(" ");
    if (parts.length < 2) {
      System.out.println("Invalid command: " + command);
      return;
    }

    String cmd = parts[0];
    String arg1 = parts[1];
    String arg2 = parts.length > 2 ? parts[2] : null;
    String extension = model.identifyFileFormat(arg1);

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
        if (parts.length < 3) {
          System.out.println("Invalid 'horizontal-flip' command: Usage is 'horizontal-flip "
                  + "source-image-name dest-image-name'");
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
        if (parts.length > 2 && parts[3].equals("split")) {
          int splitPercentage = Integer.parseInt(parts[4]);
          imageObj.sharpenImage(arg1, arg2, splitPercentage);
        } else {
          imageObj.sharpenImage(arg1, arg2, 0);
        }
        break;
      case "blur":
        if (parts.length > 2 && parts[3].equals("split")) {
          int splitPercentage = Integer.parseInt(parts[4]);
          imageObj.blurImage(arg1, arg2, splitPercentage);
        } else {
          imageObj.blurImage(arg1, arg2, 0);
        }
        break;
      case "brighten":
        if (parts.length < 4) {
          System.out.println("Invalid 'brighten' command: Usage is 'brighten increment "
                  + "source-image-name dest-image-name'");
        } else {
          int increment = Integer.parseInt(parts[1]);
          String sourceImageName = parts[2];
          String destImageName = parts[3];
          imageObj.brightenImage(sourceImageName, destImageName, increment);
        }
        break;
      case "sepia":
        if (parts.length > 2 && parts[3].equals("split")) {
          int splitPercentage = Integer.parseInt(parts[4]);
          imageObj.sepiaImage(arg1, arg2, splitPercentage);
        } else {
          imageObj.sepiaImage(arg1, arg2, 0);
        }
        break;
      case "rgb-combine":
        if (parts.length < 5) {
          System.out.println("Invalid 'rgb-combine' command: Usage is 'rgb-combine "
                  + "combined-image red-image green-image blue-image'");
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
          System.out.println("Invalid 'rgb-split' command: Usage is 'rgb-split image-name "
                  + "dest-image-name-red dest-image-name-green dest-image-name-blue'");
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
          System.out.println("Invalid 'extract-component' command: Usage is 'red-component "
                  + "source-image-name dest-image-name'");
        } else {
          String sourceImageName = parts[1];
          String destImageName = parts[2];
          imageObj.extractComponent(sourceImageName, destImageName, "red");
        }
        break;

      case "green-component":
        if (parts.length < 3) {
          System.out.println("Invalid 'extract-component' command: Usage is 'green-component "
                  + "source-image-name dest-image-name'");
        } else {
          String sourceImageName = parts[1];
          String destImageName = parts[2];
          imageObj.extractComponent(sourceImageName, destImageName, "green");
        }
        break;
      case "blue-component":
        if (parts.length < 3) {
          System.out.println("Invalid 'blue-component' command: Usage is 'blue-component "
                  + "source-image-name dest-image-name'");
        } else {
          String sourceImageName = parts[1];
          String destImageName = parts[2];
          imageObj.extractComponent(sourceImageName, destImageName, "blue");
        }
        break;

      case "value-component":
        if (parts.length < 3) {
          System.out.println("Invalid 'value-component' command: Usage is 'value-component "
                  + "source-image-name dest-image-name'");
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
          System.out.println("Invalid 'luma-component' command: Usage is 'luma-component "
                  + "source-image-name dest-image-name'");
        } else {
          String sourceImageName = parts[1];
          String destImageName = parts[2];
          imageObj.extractComponent(sourceImageName, destImageName, "luma");
        }
        break;

        case "color-correct":
          if (parts.length < 3) {
            System.out.println("Invalid 'color-correct' command: Usage is 'color-correct "
                    + "source-image-name dest-image-name'");
          } else {
            String sourceImageName = parts[1];
            String destImageName = parts[2];
            imageObj.colorCorrectImage(sourceImageName, destImageName);
          }
          break;

      case "histogram":
        if (parts.length < 3) {
          System.out.println("Invalid 'histogram' command: Usage is 'histogram "
                  + "source-image-name dest-image-name'");
        } else {
          String sourceImageName = parts[1];
          String destImageName = parts[2];
          imageObj.createHistogram(sourceImageName, destImageName);
        }
        break;

      case "run":
        String scriptFilename = parts[1];
        model.executeScriptFromFile(scriptFilename);
        break;

      default:
        System.out.println("Invalid command: " + command);
        break;
    }
  }
}


