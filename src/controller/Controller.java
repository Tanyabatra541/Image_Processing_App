package controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import model.ImageModel;
import view.ImageEditorView;

import static java.lang.System.exit;

/**
 * The `Controller` class serves as the controller in the MVC architectural pattern.
 * It handles user interactions from the view, processes user input, and communicates with the
 * model and view components.
 */
public class Controller implements ControllerFeatures {

  private final Reader reader;

  static String filePath = null;
  private static String extension = null;

  public ImageModel imageObj = new ImageModel();

  private ImageEditorView view;

  /**
   * Constructs a controller object with the given reader.
   *
   * @param reader The reader to use for reading input from the user.
   */
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
  public String parseAndExecute(String command) throws IOException {
    int[][][] rgb;
    double[][] pixels;
    String sourceImageName;
    String message = null;
    System.out.println("Executing command: " + command);
    PARTS = command.split(" ");
    if (PARTS.length < 2) {
      message = "Invalid command";
      return message;
    }

    String cmd = PARTS[0];
    String arg1 = PARTS[1];
    String arg2 = PARTS.length > 2 ? PARTS[2] : null;

    if (arg1 == null || arg2 == null) {
      message = "Image Name not found";
      return message;
    }

    if (Objects.equals(cmd, "load") || Objects.equals(cmd, "save")) {
      filePath = extractFilePath(command);
      extension = identifyFileFormat(filePath);
      arg2 = extractName(command);
    }

    IOImageOperations ioImageOperations = new IOImageOperations();
    switch (cmd) {
      case "load":
        arg1 = filePath;
        if (arg1 != null) {
          rgb = ioImageOperations.load(arg1, extension);
          imageObj.loadImageInMap(arg2, rgb);
          message="Operation Successful";
        } else {
          message = "Unable to load";
        }
        System.out.println("in load");
        System.out.println("arg1" + arg1);
        break;
      case "save":
        arg1 = filePath;
        if (arg1 != null) {
          rgb = imageObj.getRgbDataMap(arg2);
          pixels = imageObj.getPixels(arg2);
          ioImageOperations.save(arg1, arg2, extension, rgb, pixels);
          message="Operation Successful";
        } else {
          message = "Unable to save";
        }
        break;
      case "horizontal-flip":
        sourceImageName = PARTS[1];
        if (!imageObj.getImageMap().containsKey(sourceImageName)) {
          message = "Source Image not found";
        } else {
          if (PARTS.length < 3) {
            message = "Invalid 'horizontal-flip' command: Usage is 'horizontal-flip "
                    + "source-image-name dest-image-name'";
          } else {
            String destImageName = PARTS[2];
            imageObj.horizontalFlipImage(sourceImageName, destImageName);
            message="Operation Successful";
          }
        }
        break;
      case "vertical-flip":
        sourceImageName = PARTS[1];
        if (!imageObj.getImageMap().containsKey(sourceImageName)) {
          message = "Source Image not found";
        } else {
          imageObj.verticalFlipImage(sourceImageName, arg2);
          message="Operation Successful";
        }
        break;
      case "sharpen":
        sourceImageName = PARTS[1];
        if (!imageObj.getImageMap().containsKey(sourceImageName)) {
          message = "Source Image not found";
        } else {
          if (PARTS.length > 3 && PARTS[3].equals("split")) {
            int splitPercentage = Integer.parseInt(PARTS[4]);
            if (splitPercentage < 0 || splitPercentage > 100) {
              message = "Split percentage should be between 0 and 100";
            } else {
              imageObj.sharpenImage(sourceImageName, arg2, splitPercentage);
              message="Operation Successful";
            }
          } else {
            imageObj.sharpenImage(sourceImageName, arg2, 0);
            message="Operation Successful";
          }
        }
        break;
      case "blur":
        sourceImageName = PARTS[1];
        if (!imageObj.getImageMap().containsKey(sourceImageName)) {
          message = "Source Image not found";
        } else {
          if (PARTS.length > 3 && PARTS[3].equals("split")) {
            int splitPercentage = Integer.parseInt(PARTS[4]);
            if (splitPercentage < 0 || splitPercentage > 100) {
              message = "Split percentage should be between 0 and 100";
            } else {
              imageObj.blurImage(sourceImageName, arg2, splitPercentage);
              message="Operation Successful";
            }
          } else {
            imageObj.blurImage(sourceImageName, arg2, 0);
            message="Operation Successful";
          }
        }
        break;
      case "brighten":
        sourceImageName = PARTS[2];
        if (!imageObj.getImageMap().containsKey(sourceImageName)) {
          message = "Source Image not found";
        } else {
          if (PARTS.length < 4) {
            message = "Invalid 'brighten' command: Usage is 'brighten increment "
                    + "source-image-name dest-image-name'";
          } else {
            int increment = Integer.parseInt(PARTS[1]);
            String destImageName = PARTS[3];
            imageObj.brightenImage(sourceImageName, destImageName, increment);
            message="Operation Successful";
          }
        }
        break;
      case "sepia":
        sourceImageName = PARTS[1];
        if (!imageObj.getImageMap().containsKey(sourceImageName)) {
          message = "Source Image not found";
        } else {
          if (PARTS.length > 3 && PARTS[3].equals("split")) {
            int splitPercentage = Integer.parseInt(PARTS[4]);
            if (splitPercentage < 0 || splitPercentage > 100) {
              message = "Split percentage should be between 0 and 100";
            } else {
              imageObj.sepiaImage(sourceImageName, arg2, splitPercentage);
              message="Operation Successful";
            }
          } else {
            imageObj.sepiaImage(sourceImageName, arg2, 0);
            message="Operation Successful";
          }
        }
        break;
      case "rgb-combine":
        if (PARTS.length < 5) {
          message = "Invalid 'rgb-combine' command: Usage is 'rgb-combine "
                  + "combined-image red-image green-image blue-image'";
        } else {
          String combinedImageName = PARTS[1];
          String redImageName = PARTS[2];
          String greenImageName = PARTS[3];
          String blueImageName = PARTS[4];
          if (!imageObj.getImageMap().containsKey(redImageName) ||
                  !imageObj.getImageMap().containsKey(greenImageName) ||
                  !imageObj.getImageMap().containsKey(blueImageName)) {
            message = "One or more Source Image not found";
          } else {
            imageObj.combineRGBImages(combinedImageName, redImageName, greenImageName,
                    blueImageName);
            message="Operation Successful";
          }
        }
        break;
      case "rgb-split":
        if (PARTS.length < 4) {
          message = "Invalid 'rgb-split' command: Usage is 'rgb-split image-name "
                  + "dest-image-name-red dest-image-name-green dest-image-name-blue'";
        } else {
          sourceImageName = PARTS[1];
          String destImageNameRed = PARTS[2];
          String destImageNameGreen = PARTS[3];
          String destImageNameBlue = PARTS[4];
          if (!imageObj.getImageMap().containsKey(sourceImageName)) {
            message = "Source Image not found";
          } else {
            imageObj.rgbSplitImage(sourceImageName, destImageNameRed, destImageNameGreen,
                    destImageNameBlue);
            message="Operation Successful";
          }
        }
        break;

      case "red-component":
        if (PARTS.length < 3) {
          message = "Invalid 'extract-component' command: Usage is 'red-component "
                  + "source-image-name dest-image-name'";
        } else {
          sourceImageName = PARTS[1];
          String destImageName = PARTS[2];
          if (!imageObj.getImageMap().containsKey(sourceImageName)) {
            message = "Source Image not found";
          } else {
            imageObj.extractComponent(sourceImageName, destImageName, "red");
            message="Operation Successful";
          }
        }
        break;

      case "green-component":
        if (PARTS.length < 3) {
          message = "Invalid 'extract-component' command: Usage is 'green-component "
                  + "source-image-name dest-image-name'";
        } else {
          sourceImageName = PARTS[1];
          String destImageName = PARTS[2];
          if (!imageObj.getImageMap().containsKey(sourceImageName)) {
            message = "Source Image not found";
          } else {
            imageObj.extractComponent(sourceImageName, destImageName, "green");
            message="Operation Successful";
          }
        }
        break;
      case "blue-component":
        if (PARTS.length < 3) {
          message = "Invalid 'blue-component' command: Usage is 'blue-component "
                  + "source-image-name dest-image-name'";
        } else {
          sourceImageName = PARTS[1];
          String destImageName = PARTS[2];
          if (!imageObj.getImageMap().containsKey(sourceImageName)) {
            message = "Source Image not found";
          } else {
            imageObj.extractComponent(sourceImageName, destImageName, "blue");
            message="Operation Successful";
          }
        }
        break;

      case "value-component":
        if (PARTS.length < 3) {
          message = "Invalid 'value-component' command: Usage is 'value-component "
                  + "source-image-name dest-image-name'";
        } else {
          sourceImageName = PARTS[1];
          String destImageName = PARTS[2];
          if (!imageObj.getImageMap().containsKey(sourceImageName)) {
            message = "Source Image not found";
          } else {
            imageObj.extractComponent(sourceImageName, destImageName, "value");
            message="Operation Successful";
          }
        }
        break;
      case "intensity-component":
        if (PARTS.length < 3) {
          message = "Invalid 'intensity-component' command: Usage is 'intensity-component"
                  + " source-image-name dest-image-name'";
        } else {
          sourceImageName = PARTS[1];
          String destImageName = PARTS[2];
          if (!imageObj.getImageMap().containsKey(sourceImageName)) {
            message = "Source Image not found";
          } else {
            imageObj.extractComponent(sourceImageName, destImageName, "intensity");
            message="Operation Successful";
          }
        }
        break;
      case "luma-component":
        sourceImageName = PARTS[1];
        if (!imageObj.getImageMap().containsKey(sourceImageName)) {
          message = "luma-component: Source Image not found";
        } else {
          if (PARTS.length < 3) {
            message = "Invalid 'luma-component' command: Usage is 'luma-component "
                    + "source-image-name dest-image-name'";
          } else if (PARTS.length > 3 && PARTS[3].equals("split")) {
            int splitPercentage = Integer.parseInt(PARTS[4]);
            String destImageName = PARTS[2];
            if (splitPercentage < 0 || splitPercentage > 100) {
              message = "Split percentage should be between 0 and 100";
            } else {
              imageObj.extractComponent(sourceImageName, destImageName, "luma",
                      splitPercentage);
              message="Operation Successful";
            }
          } else {
            String destImageName = PARTS[2];
            imageObj.extractComponent(sourceImageName, destImageName, "luma");
            message="Operation Successful";
          }
        }
        break;
      case "color-correct":
        sourceImageName = PARTS[1];
        if (!imageObj.getImageMap().containsKey(sourceImageName)) {
          System.out.println("Source Image not found");
        } else {
          if (PARTS.length > 3 && PARTS[3].equals("split")) {
            int splitPercentage = Integer.parseInt(PARTS[4]);
            if (splitPercentage < 0 || splitPercentage > 100) {
              message = "Split percentage should be between 0 and 100";
            } else {
              imageObj.colorCorrectImage(sourceImageName, arg2, splitPercentage);
              message="Operation Successful";
            }
          } else {
            imageObj.colorCorrectImage(sourceImageName, arg2, 0);
            message="Operation Successful";
          }
        }
        break;
      case "histogram":
        if (PARTS.length < 3) {
          message = "Invalid 'histogram' command: Usage is 'histogram "
                  + "source-image-name dest-image-name'";
        } else {
          sourceImageName = PARTS[1];
          String destImageName = PARTS[2];
          imageObj.createHistogram(sourceImageName, destImageName);
          message="Operation Successful";
        }
        break;

      case "levels-adjust":
        if (PARTS.length < 6) {
          message = "Invalid 'levels-adjust' command: Usage is 'levels-adjust "
                  + "b m w source-image-name dest-image-name'";
        } else {
          sourceImageName = PARTS[4];
          String destImageName = PARTS[5];
          System.out.println("{{{source"+PARTS[1]);
          System.out.println("{{{source"+PARTS[2]);
          System.out.println("{{{source"+PARTS[3]);
          System.out.println("{{{source"+PARTS[0]);
          System.out.println("{{{source"+PARTS[4]);
          System.out.println("{{{source"+PARTS[5]);
          if (!imageObj.getImageMap().containsKey(sourceImageName)) {
            message = "Source Image not found";
          } else {
            int b = Integer.parseInt(PARTS[1]);
            int m = Integer.parseInt(PARTS[2]);
            int w = Integer.parseInt(PARTS[3]);
            if (b < m && m < w && b >= 0 && b <= 255 && m <= 255 && w <= 255) {
              if (PARTS.length > 6 && PARTS[6].equals("split")) {
                int splitPercentage = Integer.parseInt(PARTS[7]);
                if (splitPercentage < 0 || splitPercentage > 100) {
                  message = "Split percentage should be between 0 and 100";
                } else {
                  imageObj.applyLevelsAdjustment(b, m, w, sourceImageName, destImageName,
                          splitPercentage);
                  message="Operation Successful";
                }
              } else {
                imageObj.applyLevelsAdjustment(b, m, w, sourceImageName, destImageName,
                        0);
                message="Operation Successful";
              }
            } else {
              message = "Invalid shadow, mid, highlight points";
            }
          }
        }
        break;

      case "greyscale":
        if (PARTS.length < 3) {
          message = "Invalid 'brighten' command: Usage is 'greyscale "
                  + "source-image-name dest-image-name'";
        } else {
          sourceImageName = PARTS[1];
          String destImageName = PARTS[2];
          if (!imageObj.getImageMap().containsKey(sourceImageName)) {
            message = "Source Image not found";
          } else {
            if (PARTS.length > 3 && PARTS[3].equals("split")) {
              int splitPercentage = Integer.parseInt(PARTS[4]);
              if (splitPercentage < 0 || splitPercentage > 100) {
                message = "Split percentage should be between 0 and 100";
              } else {
                imageObj.convertToGrayscale(sourceImageName, destImageName, splitPercentage);
                message="Operation Successful";
              }
            } else {
              imageObj.convertToGrayscale(sourceImageName, destImageName, 0);
              message="Operation Successful";
            }
          }
        }
        break;

      case "compress":
        sourceImageName = PARTS[2];
        if (!imageObj.getImageMap().containsKey(sourceImageName)) {
          message = "Source Image not found";
        } else {
          double percentage = Double.parseDouble(PARTS[1]);
          if (percentage < 0 || percentage > 100) {
            message = "Compression percentage should be between 0 and 100";
          } else {
            String destImageName = PARTS[3];
            imageObj.compress(sourceImageName, destImageName, percentage);
            message="Operation Successful";
          }
        }
        break;
      case "-file":
        String scriptFilename = PARTS[1];
        if (scriptFilename == null) {
          message = "Script file not found";
        }
        executeScriptFromFile(scriptFilename);
        exit(0);
        break;
      default:
        message = "Invalid command: " + command;
        break;
    }
    return message;
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
   * Identifies the file format of an image based on its file extension.
   *
   * @param filePath The path to the image file.
   * @return The file format or null if the format is unsupported or not recognized.
   */
  public String identifyFileFormat(String filePath) {
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
   * Sets the view for this controller. Calls the addFeatures function in the view.
   *
   * @param view The view to use.
   */
  @Override
  public void setView(ImageEditorView view) {
    this.view = view;
    view.addFeatures(this);
  }


  /**
   * Loads an image based on the provided command and updates the view with the image data and
   * its histogram. The method parses and executes the given command to retrieve the image data
   * and histogram data.
   *
   * @param command       The command specifying the image loading operation.
   * @param destImageName The name of the destination image for which data is loaded.
   */
  @Override
  public void loadImage(String command, String destImageName) {
    String m;
    try {
      m = parseAndExecute(command);
      int[][][] destImageData = imageObj.getRgbDataMap(destImageName);
      view.updateImageForIndex(destImageData, 0);
      m = parseAndExecute("histogram " + destImageName + " " + destImageName
              + "-histogram");
      int[][][] destHistogramData = imageObj.getRgbDataMap(destImageName + "-histogram");
      view.updateImageForIndex(destHistogramData, 2);
    } catch (IOException e) {
      e.printStackTrace();
    }
    System.out.println("Loading image: " + command);
  }

  /**
   * Saves an image based on the provided command.
   * The method parses and executes the given command to perform the image saving operation.
   *
   * @param command The command specifying the image saving operation.
   */
  @Override
  public void saveImage(String command) {
    String m;
    try {
      m = parseAndExecute(command);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Applies specified features to an image based on the provided command and updates the view.
   * The method parses and executes the given command to perform the image transformation,
   * updates the view with the modified image, and displays the histogram of the modified image.
   *
   * @param command       The command specifying the features to be applied to the image.
   * @param destImageName The name of the destination image where the features are applied.
   */
  @Override
  public String applyFeatures(String command, String destImageName) {
    String m = null;
    try {
      System.out.println("applyFeatures(String command" + command);
      if (command != null) {
        m = parseAndExecute(command);
        System.out.println("&*(&^"+m);
        System.out.println("*****Applying feature on destImageName: " + destImageName);

        System.out.println("&*(&^"+m);
      }
      int[][][] destImageData = imageObj.getRgbDataMap(destImageName);
      view.updateImageForIndex(destImageData, 1);
      System.out.println("Applying feature on destImageName: " + destImageName);
      m = parseAndExecute("histogram " + destImageName + " " + destImageName
              + "-histogram");
      int[][][] destHistogramData = imageObj.getRgbDataMap(destImageName + "-histogram");
      view.updateImageForIndex(destHistogramData, 2);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return m;
  }

  /**
   * Executes commands read from the input source (reader). The method reads each line
   * from the input source, trims leading and trailing whitespaces, and skips comments
   * and empty lines. For each non-empty and non-comment line, the method invokes the
   * parseAndExecute method to process and execute the specified command.
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

  /**
   * Extracts the file path from a given command string. It utilizes a regular expression to
   * extract the content inside the single quotes, representing the file path.
   *
   * @param command The command string from which the file path needs to be extracted.
   * @return The extracted file path if found; otherwise, returns null.
   */
  public static String extractFilePath(String command) {
    String filePath = null;
    // Check if the command starts with "load" or "save"
    if (command.trim().startsWith("load") || command.trim().startsWith("save")) {
      // Use regular expression to extract the file path inside single quotes
      Pattern pattern = Pattern.compile("'(.*?)'");
      Matcher matcher = pattern.matcher(command);

      // Check if there is a match
      if (matcher.find()) {
        filePath = matcher.group(1);

        // Check if the extracted path is a valid file path
      }
    }
    return filePath;
  }

  /**
   * Extracts the name from a given command string that starts with "load" or "save."
   * The method assumes that the file path is enclosed in single quotes ('') and extracts
   * the content inside the single quotes as well as the second part after the file path,
   * which typically represents the name.
   *
   * @param command The command string from which the name needs to be extracted.
   * @return The extracted name if found; otherwise, returns null.
   */
  public static String extractName(String command) {
    String extractedName = null;

    // Check if the command starts with "load" or "save"
    if (command.trim().startsWith("load") || command.trim().startsWith("save")) {
      // Use regular expressions to extract the file path and the second part
      Pattern pattern = Pattern.compile("'(.*?)'\\s(.+)");
      Matcher matcher = pattern.matcher(command);

      if (matcher.find()) {
        String afterFilePath = matcher.group(2);
        String[] parts = afterFilePath.split(" ");
        extractedName = parts[0];

      }

    }
    return extractedName;
  }
}

