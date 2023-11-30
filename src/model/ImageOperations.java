package model;

import java.io.IOException;

/**
 * This interface represents the operations that can be performed on an image.
 */
public interface ImageOperations {

  /**
   * Load an image from a file and store it in the image map.
   *
   * @param imageName The name to associate with the loaded image.
   * @throws IOException If an error occurs while loading the image.
   */
  void loadImageInMap(String imageName, int[][][] rgb) throws IOException;

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

  /**
   * Applies a sharpening kernel to a particular percentage of the source image depending on the
   * splitPercentage parameter passed, creating a split sharpened image having both the operated
   * image and the original image with the given name.
   *
   * @param sourceName      The name of the source image.
   * @param destName        The name of the destination sharpened image.
   * @param splitPercentage The percentage of the image to be sharpened.
   */
  void sharpenImage(String sourceName, String destName, int splitPercentage);

  /**
   * Applies a sharpening kernel to the source image, creating a sharpened image with the given
   * name.
   *
   * @param sourceImageName The name of the source image.
   * @param destImageName   The name of the destination sharpened image.
   */
  void sharpenImage(String sourceImageName, String destImageName);

  /**
   * Applies a Gaussian blurring kernel to a particular percentage of the source image depending on
   * the splitPercentage parameter passed, creating a split blurred image having both the operated
   * image and the original image with the given name.
   *
   * @param sourceName      The name of the source image.
   * @param destName        The name of the destination sharpened image.
   * @param splitPercentage The percentage of the image to be blurred.
   */
  void blurImage(String sourceName, String destName, int splitPercentage);

  /**
   * Applies a blurring effect to the source image, creating a blurred version with the given name.
   * This method uses a Gaussian blurring kernel.
   *
   * @param sourceName The name of the source image.
   * @param destName   The name of the destination blurred image.
   */
  void blurImage(String sourceName, String destName);

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
   * Applies the sepia tone to a particular percentage of the source image depending on
   * the splitPercentage parameter passed, creating a split sepia image having both the operated
   * image and the original image with the given name.
   *
   * @param sourceName      The name of the source image.
   * @param destName        The name of the destination sharpened image.
   * @param splitPercentage The percentage of the image to be sepia-toned.
   */
  void sepiaImage(String sourceName, String destName, int splitPercentage);

  /**
   * Applies a sepia filter to the source image and save the sepia-toned image with the given name.
   *
   * @param sourceName The name of the source image.
   * @param destName   The name of the destination sepia-toned image.
   */
  void sepiaImage(String sourceName, String destName);

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


  /**
   * Extracts a specific color component (red, green, or blue) from the source image and saves
   * the result as a new image. The split percentage determines the split point,
   * and only the specified percentage of the image will be transformed.
   *
   * @param sourceName      The name of the source image.
   * @param destName        The name of the destination image.
   * @param component       The color component to be extracted ("red", "green", or "blue").
   * @param splitPercentage The percentage of the image width at which the split occurs.
   */
  void extractComponent(String sourceName, String destName, String component, int splitPercentage);

  /**
   * Compresses the specified image with the given compression percentage and saves the compressed
   * image with the provided destination name.
   *
   * @param imageName            The name of the source image to be compressed.
   * @param destName             The name to be assigned to the compressed image.
   * @param compressionThreshold The percentage of compression to be applied to the image.
   */
  void compress(String imageName, String destName, double compressionThreshold);

  /**
   * Color-correct a part of the image depending on the splitPercentage parameter passed by
   * aligning the meaningful peaks of its histogram. The final image will have a part of color
   * corrected image and a part of the original image
   *
   * @param sourceImageName The name of the source image.
   * @param destImageName   The name of the destination color-corrected image.
   * @param splitPercentage The percentage of the image to be color corrected.
   */
  void colorCorrectImage(String sourceImageName, String destImageName, int splitPercentage);

  /**
   * Color-correct the image by aligning the meaningful peaks of its histogram.
   *
   * @param sourceImageName The name of the source image.
   * @param destImageName   The name of the destination color-corrected image.
   */
  void colorCorrectImage(String sourceImageName, String destImageName);

  /**
   * Create a histogram of the source image and save it as a separate image.
   *
   * @param sourceName The name of the source image.
   * @param destName   The name of the destination histogram image.
   */
  void createHistogram(String sourceName, String destName);

  /**
   * Levels-adjust a part of the image depending on the splitPercentage parameter passed.
   * The final image will have a part of level-adjusted image and a part of the original image
   *
   * @param b               The shadow point.
   * @param m               The mid-point.
   * @param w               The highlight point.
   * @param sourceImageName The name of the source image.
   * @param destImageName   The name of the destination adjusted image.
   * @param splitPercentage The percentage of the image to apply the adjustment to.
   */
  void applyLevelsAdjustment(int b, int m, int w, String sourceImageName, String destImageName,
                             int splitPercentage);

  /**
   * Levels-adjust the image and saves it in the destination image passed.
   *
   * @param b               The shadow point.
   * @param m               The mid-point.
   * @param w               The highlight point.
   * @param sourceImageName The name of the source image.
   * @param destImageName   The name of the destination adjusted image.
   */
  void applyLevelsAdjustment(int b, int m, int w, String sourceImageName, String destImageName);

  /**
   * Convert the source image to grayscale using the specified transformation.
   *
   * @param sourceName      The name of the source image.
   * @param destName        The name of the destination grayscale image.
   * @param splitPercentage The percentage of the image to apply the transformation to.
   */
  void convertToGrayscale(String sourceName, String destName, int splitPercentage);

  /**
   * Retrieves the RGB data of an image stored in the map, represented as a three-dimensional array.
   *
   * @param imageName The name of the image.
   * @return The RGB data of the image as a three-dimensional array.
   */
  int[][][] getRgbDataMap(String imageName);

  /**
   * Retrieves the pixel values of an image stored in the map, represented as a two-dimensional
   * array.
   *
   * @param imageName The name of the image.
   * @return The pixel values of the image as a two-dimensional array.
   */
  double[][] getPixels(String imageName);


}