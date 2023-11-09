package model;

import java.io.IOException;

/**
 * This interface defines a set of image processing operations that can be applied to images.
 */
public interface ImageOperations {

  /**
   * Load an image from a file and store it in the image map and RGB data map.
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
   * @param sourceName The name of the source image.
   * @param destName   The name of the destination image.
   */
  void verticalFlipImage(String sourceName, String destName);

  /**
   * Applies a sharpening kernel to the source image, creating a sharpened image with the given
   * name.
   *
   * @param sourceName The name of the source image.
   * @param destName   The name of the destination sharpened image.
   */
  void sharpenImage(String sourceName, String destName, int splitPercentage);

  /**
   * Applies a blurring effect to the source image, creating a blurred version with the given name.
   * This method uses a Gaussian blurring kernel.
   *
   * @param sourceName The name of the source image.
   * @param destName   The name of the destination blurred image.
   */
  void blurImage(String sourceName, String destName, int splitPercentage);

  /**
   * Brighten the colors of the source image by a specified increment and save the brightened
   * image with the given name.
   *
   * @param sourceName The name of the source image.
   * @param destName   The name of the destination brightened image.
   * @param increment  The amount by which to increment the color values. Positive values
   *                   brighten, negative values darken.
   */
  void brightenImage(String sourceName, String destName, int increment);

  /**
   * Applies a sepia filter to the source image and save the sepia-toned image with the given name.
   *
   * @param sourceName The name of the source image.
   * @param destName   The name of the destination sepia-toned image.
   */
  void sepiaImage(String sourceName, String destName, int splitPercentage);

  /**
   * Combine three source images representing the red, green, and blue channels into a single
   * image.
   * The combined image is saved with the given name.
   *
   * @param combinedName The name of the combined RGB image.
   * @param redName      The name of the source image for the red channel.
   * @param greenName    The name of the source image for the green channel.
   * @param blueName     The name of the source image for the blue channel.
   */
  void combineRGBImages(String combinedName, String redName, String greenName, String blueName);

  /**
   * Split the RGB components of a source image into separate images representing the red, green,
   * and blue channels.
   * The resulting images are saved with the specified names.
   *
   * @param sourceName    The name of the source image to split into RGB channels.
   * @param destNameRed   The name of the resulting image representing the red channel.
   * @param destNameGreen The name of the resulting image representing the green channel.
   * @param destNameBlue  The name of the resulting image representing the blue channel.
   */
  void rgbSplitImage(String sourceName, String destNameRed, String destNameGreen,
                     String destNameBlue);

  /**
   * Extract a specific component from a source image and save it as a separate image.
   *
   * @param sourceName The name of the source image from which to extract the component.
   * @param destName   The name of the resulting image that will contain the extracted component.
   * @param component  The component to extract, which can be one of the following:
   *                   - "red": Extract the red channel.
   *                   - "green": Extract the green channel.
   *                   - "blue": Extract the blue channel.
   *                   - "luma": Convert the image to grayscale using luminance.
   *                   - "intensity": Convert the image to grayscale using intensity.
   *                   - "value": Extract the value (brightness) component of an image.
   */
  void extractComponent(String sourceName, String destName, String component);

  void colorCorrectImage(String sourceImageName, String destImageName);

  void createHistogram(String sourceName, String destName);

  void applyLevelsAdjustment(int b, int m, int w, String sourceImageName, String destImageName);

}