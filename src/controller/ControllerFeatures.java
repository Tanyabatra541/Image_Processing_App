package controller;

import view.ImageEditorView;

public interface ControllerFeatures {
  void setView(ImageEditorView view);
  void loadImage(String command, String destImageName);
  void saveImage(String command);
  void applyFeatures(String command, String destinationImageName);
}
