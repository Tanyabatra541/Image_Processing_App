package controller;

public interface ControllerFeatures {
  void addFeaturesToView(ControllerFeatures features);
  void loadImage(String command, String destImageName);
  void saveImage(String command);
  void applyFeatures(String command, String destinationImageName,boolean isSplit);
}
