import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class PPMImage extends AbstractImage {

  @Override
  public void loadImage(String imagePath, String imageName) {
    int[][][] imageRGBData = readImageRGBData(imagePath);
    ImageContent image = new ImageContent(imageName, convertToPPMFormat(imageRGBData));
    imageMap.put(imageName, image);
    rgbDataMap.put(imageName, imageRGBData);
    System.out.println("Loaded image: " + imageName);
  }

  private String convertToPPMFormat(int[][][] imageRGBData) {
    StringBuilder ppmContent = new StringBuilder();
    int height = imageRGBData.length;
    int width = imageRGBData[0].length;

    ppmContent.append("P3\n");
    ppmContent.append(width + " " + height + "\n");
    ppmContent.append("255\n");

    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        ppmContent.append(imageRGBData[i][j][0] + " "); // Red component
        ppmContent.append(imageRGBData[i][j][1] + " "); // Green component
        ppmContent.append(imageRGBData[i][j][2] + " "); // Blue component
      }
      ppmContent.append("\n");
    }

    return ppmContent.toString();
  }

  private static int[][][] readImageRGBData(String filename) {
    Scanner sc = null;

    try {
      sc = new Scanner(new FileInputStream(filename));
    } catch (FileNotFoundException e) {
      System.out.println("File " + filename + " not found!");
    }

    StringBuilder builder = new StringBuilder();
    while (sc.hasNextLine()) {
      String s = sc.nextLine();
      if (s.charAt(0) != '#') {
        builder.append(s).append(System.lineSeparator());
      }
    }

    sc = new Scanner(builder.toString());

    String token = sc.next();
    if (!token.equals("P3")) {
      System.out.println("Invalid PPM file: plain RAW file should begin with P3");
    }
    int width = sc.nextInt();
    int height = sc.nextInt();

    int[][][] imageRGBData = new int[height][width][3];
    int maxValue = sc.nextInt(); // Read the maximum color value

    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        imageRGBData[i][j][0] = sc.nextInt(); // Red component
        imageRGBData[i][j][1] = sc.nextInt(); // Green component
        imageRGBData[i][j][2] = sc.nextInt(); // Blue component
      }
    }
    return imageRGBData;
  }


  @Override
  public void saveImage(String imagePath, String imageName) {
    ImageContent image = imageMap.get(imageName);

    if (image != null) {
      String content = image.getContent();

      try {
        // Create a file and write the image content to it
        File file = new File(imagePath);
        java.io.FileWriter fileWriter = new java.io.FileWriter(file);
        fileWriter.write(content);
        fileWriter.close();

        System.out.println("Image saved as " + imagePath + " in the ppm format");
      } catch (java.io.IOException e) {
        e.printStackTrace();
        System.out.println("Failed to save the image as " + imagePath);
      }
    } else {
      System.out.println("Image not found: " + imageName);
    }
  }

}
