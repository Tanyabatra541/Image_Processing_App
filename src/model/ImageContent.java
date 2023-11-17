package model;

/**
 * The `ImageContent` class represents an image with its associated name and content.
 * This class is used to store image data.
 */
public class ImageContent {

  double[][] pixels;
  private final String name;
  private final int[][][] rgbDataMap;

  int height;
  int width;


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
   * Sets the width of the image.
   *
   * @param w The width of the image.
   */
  public void setWidth(int w) {
    this.width = w;
  }

  /**
   * Sets the height of the image.
   *
   * @param h The height of the image.
   */
  public void setHeight(int h) {
    this.height = h;
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