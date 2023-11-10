package model;

/**
 * The `Model.Model.Model.Model.ImageContent` class represents an image with
 * its associated name and content.
 * This class is used to store image data within the application.
 */
class ImageContent {

  double[][] pixels;
  private final String name;
  private final int[][][] rgbDataMap;

  private int height;
  private int width;

 // private int[][][] rgbDataMap;

  /**
   * Constructs an `Model.ImageContent` instance with the specified name and content.
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
   * Get the content data of the image.
   *
   * @return The content data of the image.
   */
  public int[][][] getRgbDataMap() {
    return rgbDataMap;
  }

  public void setPixels(double[][] pixels) {
    this.pixels = pixels;
  }

  public void setWidth(int w) {
    this.width = w;
  }

  public void setHeight(int h) {
    this.height = h;
  }

  public double[][] getPixels() {
    return pixels;
  }
}