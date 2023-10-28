import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Model implements IModel {
  private String input;
  private String result; // Store the result from processing the file

  static ImageOperations imageObj = null;

  public Model() {
    input = "";
    result = "";
  }

  @Override
  public void setString(String i) {
    input = i;
  }

  @Override
  public String getString() {
    return input;
  }

  public void processFileContents(String fileContents) {
    // Split file contents into lines
    String[] lines = fileContents.split("\n");
    StringBuilder processedResult = new StringBuilder();

    for (String line : lines) {
      // Process each line using the parseAndExecute method
      try {
        parseAndExecute(line);
        processedResult.append("Processed: ").append(line).append("\n");
      } catch (IOException e) {
        processedResult.append("Error: ").append(e.getMessage()).append("\n");
      }
    }

    // Store the final result
    result = processedResult.toString();
  }

  public String getResult() {
    return result;
  }

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

  // You may need to adjust this method's signature to handle exceptions
  protected void parseAndExecute(String command) throws IOException {
    String[] parts = command.split(" ");
    if (parts.length < 2) {
      System.out.println("Invalid command: " + command);
      return;
    }

    String cmd = parts[0];
    String arg1 = parts[1];
    String arg2 = parts.length > 2 ? parts[2] : null;
    String extension = identifyFileFormat(arg1);
    if(extension != null) {
      if ((extension.equalsIgnoreCase("png") || (extension.equalsIgnoreCase("jpg")) || (extension.equalsIgnoreCase("jpeg")))) {
        imageObj = new PNGJPGImage();
      } else if (extension.equalsIgnoreCase("ppm")) {
        imageObj = new PPMImage();
      } else {
        throw new IllegalArgumentException("Unsupported image format");
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
          System.out.println("Invalid 'horizontal-flip' command: Usage is 'horizontal-flip source-image-name dest-image-name'");
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
          System.out.println("Invalid 'brighten' command: Usage is 'brighten increment source-image-name dest-image-name'");
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
          System.out.println("Invalid 'rgb-combine' command: Usage is 'rgb-combine combined-image red-image green-image blue-image'");
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
          System.out.println("Invalid 'rgb-split' command: Usage is 'rgb-split image-name dest-image-name-red dest-image-name-green dest-image-name-blue'");
        } else {
          String sourceImageName = parts[1];
          String destImageNameRed = parts[2];
          String destImageNameGreen = parts[3];
          String destImageNameBlue = parts[4];
          imageObj.rgbSplitImage(sourceImageName, destImageNameRed, destImageNameGreen, destImageNameBlue);
        }
        break;

      case "red-component":
        if (parts.length < 3) {
          System.out.println("Invalid 'extract-component' command: Usage is 'red-component source-image-name dest-image-name'");
        } else {
          String sourceImageName = parts[1];
          String destImageName = parts[2];
          imageObj.extractComponent(sourceImageName, destImageName, "red");
        }
        break;

      case "green-component":
        if (parts.length < 3) {
          System.out.println("Invalid 'extract-component' command: Usage is 'green-component source-image-name dest-image-name'");
        } else {
          String sourceImageName = parts[1];
          String destImageName = parts[2];
          imageObj.extractComponent(sourceImageName, destImageName, "green");
        }
        break;
      case "blue-component":
        if (parts.length < 3) {
          System.out.println("Invalid 'blue-component' command: Usage is 'blue-component source-image-name dest-image-name'");
        } else {
          String sourceImageName = parts[1];
          String destImageName = parts[2];
          imageObj.extractComponent(sourceImageName, destImageName, "blue");
        }
        break;

      case "value-component":
        if (parts.length < 3) {
          System.out.println("Invalid 'value-component' command: Usage is 'value-component source-image-name dest-image-name'");
        } else {
          String sourceImageName = parts[1];
          String destImageName = parts[2];
          imageObj.extractComponent(sourceImageName, destImageName, "value");
        }
        break;

      case "intensity-component":
        if (parts.length < 3) {
          System.out.println("Invalid 'intensity-component' command: Usage is 'intensity-component source-image-name dest-image-name'");
        } else {
          String sourceImageName = parts[1];
          String destImageName = parts[2];
          imageObj.extractComponent(sourceImageName, destImageName, "intensity");
        }
        break;

      case "luma-component":
        if (parts.length < 3) {
          System.out.println("Invalid 'luma-component' command: Usage is 'luma-component source-image-name dest-image-name'");
        } else {
          String sourceImageName = parts[1];
          String destImageName = parts[2];
          imageObj.extractComponent(sourceImageName, destImageName, "luma");
        }
        break;

      default:
        System.out.println("Unknown command: " + command);
    }
  }
}
