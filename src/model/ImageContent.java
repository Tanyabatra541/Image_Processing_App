package model;

/**
 * The `Model.Model.Model.Model.ImageContent` class represents an image with
 * its associated name and content.
 * This class is used to store image data within the application.
 */
class ImageContent {
  private final String name;
  private final String content;

  /**
   * Constructs an `Model.ImageContent` instance with the specified name and content.
   *
   * @param name    The name or identifier of the image.
   * @param content The content data of the image.
   */
  public ImageContent(String name, String content) {
    this.name = name;
    this.content = content;
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
  public String getContent() {
    return content;
  }
}