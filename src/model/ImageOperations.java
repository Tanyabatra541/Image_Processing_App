package model;

import java.io.IOException;

public interface ImageOperations {


  void loadImage(String imagePath, String imageName) throws IOException;

  void saveImage(String imagePath, String imageName) throws IOException;

  void horizontalFlipImage(String sourceImageName, String destImageName);

  void verticalFlipImage(String sourceName, String destName);

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