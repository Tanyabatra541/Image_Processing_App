import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public abstract class AbstractImage implements ImageOperations{


  static final Map<String, ImageContent> imageMap = new HashMap<>();

  static final Map<String, int[][][]> rgbDataMap = new HashMap<>();


  static ImageOperations imageObj = null;

  // Define the sharpening kernel
  float[] sharpeningKernel = {
          -1.0f / 8.0f, -1.0f / 8.0f, -1.0f / 8.0f, -1.0f / 8.0f, -1.0f / 8.0f,
          -1.0f / 8.0f, 1.0f / 4.0f, 1.0f / 4.0f, 1.0f / 4.0f, -1.0f / 8.0f,
          -1.0f / 8.0f, 1.0f / 4.0f, 1.0f, 1.0f / 4.0f, -1.0f / 8.0f,
          -1.0f / 8.0f, 1.0f / 4.0f, 1.0f / 4.0f, 1.0f / 4.0f, -1.0f / 8.0f,
          -1.0f / 8.0f, -1.0f / 8.0f, -1.0f / 8.0f, -1.0f / 8.0f, -1.0f / 8.0f
  };

  // Define the Gaussian blur kernel
  float[] gaussianKernel = {
          1.0f / 16.0f, 1.0f / 8.0f, 1.0f / 16.0f,
          1.0f / 8.0f, 1.0f / 4.0f, 1.0f / 8.0f,
          1.0f / 16.0f, 1.0f / 8.0f, 1.0f / 16.0f
  };


  public abstract void loadImage(String imagePath, String imageName) throws IOException;

  public abstract void saveImage(String imagePath, String imageName);


  public abstract void horizontalFlipImage(String sourceImageName, String destImageName);

  public abstract void verticalFlipImage(String sourceName, String destName);


  public abstract void sharpenImage(String sourceName, String destName);


  public abstract void blurImage(String sourceName, String destName);


  public abstract void brightenImage(String sourceName, String destName, int increment);

  public abstract void sepiaImage(String sourceName, String destName);

  public abstract void combineRGBImages(String combinedName, String redName, String greenName, String blueName);

  public abstract void rgbSplitImage(String sourceName, String destNameRed, String destNameGreen, String destNameBlue);


  public abstract void extractComponent(String sourceName, String destName, String component);


}
