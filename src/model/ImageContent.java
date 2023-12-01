package model;

/**
 * The `ImageContent` class represents an image with its associated name and content.
 * This class is used to store image data.
 */
public class ImageContent {

  private double[][] pixels;
  private final String name;
  private final int[][][] rgbDataMap;

  /**
   * Constructs an `ImageContent` instance with the specified name and content.
   *
   * @param name    The name or identifier of the image.
   * @param content The content data of the image.
   */
  public ImageContent(String name, int[][][] content) {
    this.name = name;
    this.rgbDataMap = content;
  }

  /**
   * Get the name or identifier of the image.
   *
   * @return The name of the image.
   */
  public String getName() {
    return name;
  }

  /**
   * Get the RGB data of the image.
   *
   * @return The content data of the image.
   */
  public int[][][] getRgbDataMap() {
    return rgbDataMap;
  }

  /**
   * Gets the pixel values of the image.
   *
   * @return A 2D array representing the pixel values of the image.
   */
  public double[][] getPixels() {
    return pixels;
  }
}