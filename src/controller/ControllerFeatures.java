package controller;

import view.ImageEditorView;

/**
 * The ControllerFeatures interface defines the functions of the controller in the image editor
 * application that can be accessed by the view. It acts as an intermediary between the view and
 * the controller. It provides methods for setting the view, loading and saving images, and
 * applying various features to images.
 */
public interface ControllerFeatures {
  /**
   * Sets the view for the controller to interact with.
   *
   * @param view The ImageEditorView instance to be set for the controller.
   */
  void setView(ImageEditorView view);

  /**
   * Loads an image based on the given command and assigns it the specified destination image name.
   *
   * @param command       The command string specifying the image loading operation.
   * @param destImageName The name to be assigned to the loaded image.
   */
  void loadImage(String command, String destImageName);

  /**
   * Saves an image based on the given command.
   *
   * @param command The command string specifying the image saving operation.
   */
  void saveImage(String command);

  /**
   * Applies various features to an image based on the given command and assigns the result
   * the specified destination image name.
   *
   * @param command              The command string specifying the feature application operation.
   * @param destinationImageName The name to be assigned to the resulting image after applying
   *                             features.
   */
  String applyFeatures(String command, String destinationImageName);
}
