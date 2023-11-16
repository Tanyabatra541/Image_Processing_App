package model;

import java.io.IOException;

public interface ImageOperations {


  /**
   * Load an image from a file and store it in the image map.
   *
   * @param imagePath The file path of the image to load.
   * @param imageName The name to associate with the loaded image.
   * @throws IOException If an error occurs while loading the image.
   */
  void loadImage(String imagePath, String imageName) throws IOException;

  /**
   * Save an image to a file using a specific format.
   *
   * @param imagePath The file path to save the image.
   * @param imageName The name of the image to be saved.
   * @throws IOException If an error occurs while saving the image.
   */
  void saveImage(String imagePath, String imageName) throws IOException;

  /**
   * Flip an image horizontally and save it as a new image with the given name.
   *
   * @param sourceImageName The name of the source image.
   * @param destImageName   The name of the destination image.
   */
  void horizontalFlipImage(String sourceImageName, String destImageName);

  /**
   * Flip an image vertically and save it as a new image with the given name.
   *
   * @param sourceImageName The name of the source image.
   * @param destImageName   The name of the destination image.
   */
  void verticalFlipImage(String sourceImageName, String destImageName);


  void sharpenImage(String sourceName, String destName, int splitPercentage);

  void sharpenImage(String sourceImageName, String destImageName);

  void blurImage(String sourceName, String destName, int splitPercentage);

  void blurImage(String sourceName, String destName);

  void brightenImage(String sourceName, String destName, int increment);

  void sepiaImage(String sourceName, String destName, int splitPercentage);

  void sepiaImage(String sourceName, String destName);

  void combineRGBImages(String combinedName, String redName, String greenName, String blueName);

  void rgbSplitImage(String sourceName, String destNameRed, String destNameGreen,
                     String destNameBlue);

  void extractComponent(String sourceName, String destName, String component);


  void compress(String imageName, double compressionThreshold, int maxValue);

  void colorCorrectImage(String sourceImageName, String destImageName, int splitPercentage);

  void colorCorrectImage(String sourceImageName, String destImageName);

  void createHistogram(String sourceName, String destName);

  void applyLevelsAdjustment(int b, int m, int w, String sourceImageName, String destImageName, int splitPercentage);

  void applyLevelsAdjustment(int b, int m, int w, String sourceImageName, String destImageName);

  void convertToGrayscale(String sourceName, String destName, int splitPercentage);


}