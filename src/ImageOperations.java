import java.io.IOException;

public interface ImageOperations {

  void loadImage(String imagePath, String imageName) throws IOException;
  void saveImage(String imagePath, String imageName) throws IOException;

  void horizontalFlipImage(String sourceImageName, String destImageName);

  void verticalFlipImage(String sourceName, String destName);

  void sharpenImage(String sourceName, String destName);

  void blurImage(String sourceName, String destName);

  void brightenImage(String sourceName, String destName, int increment);

  void sepiaImage(String sourceName, String destName);

  void combineRGBImages(String combinedName, String redName, String greenName, String blueName);

  void rgbSplitImage(String sourceName, String destNameRed, String destNameGreen, String destNameBlue);

  void extractComponent(String sourceName, String destName, String component);

}
