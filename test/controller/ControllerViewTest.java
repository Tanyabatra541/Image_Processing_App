package controller;

import model.Histogram;
import model.ImageModel;
import org.junit.Before;
import org.junit.Test;
import view.ImageEditorView;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ControllerViewTest {

    private MockImageEditorView mockView;
    private Controller controller;
    private static ImageModel pngJpgImage;


    private static String imageName = "img";
    private static String imagePath = "output.png";
    static int[][][] rgbMatrix = {
            {{255, 0, 0}, {0, 255, 0}},
            {{0, 0, 255}, {255, 255, 255}}
    };

    @Before
    public void setUp() {
        mockView = new MockImageEditorView();
        Reader reader = new InputStreamReader(System.in);
        controller = new Controller(reader);
        //controller = new Controller(reader);
        controller.setView(mockView);
        pngJpgImage = new ImageModel();
        createAndSavePNG(rgbMatrix, imageName, imagePath);
    }

    private static void createAndSavePNG(int[][][] matrix, String fileName, String filePath) {

        int width = matrix[0].length;
        int height = matrix.length;

        // Create a BufferedImage with the specified width and height
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int r = matrix[y][x][0];
                int g = matrix[y][x][1];
                int b = matrix[y][x][2];

                // Create an RGB color from the values
                int rgb = (r << 16) | (g << 8) | b;

                // Set the pixel color in the BufferedImage
                image.setRGB(x, y, rgb);
            }
        }

        try {
            // Save the BufferedImage as a PNG image at the specified file path
            File output = new File(filePath);
            ImageIO.write(image, "png", output);
            System.out.println("Image saved as " + filePath);
            pngJpgImage.loadImageInMap(imageName,rgbMatrix);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to save the image as " + filePath);
        }
    }


    @Test
    public void testLoadImage() {
        mockView.setFilePath(imagePath);

        JButton loadButton = mockView.getButtonForLoad();
        ActionEvent actionEvent = new ActionEvent(loadButton, ActionEvent.ACTION_PERFORMED, "Load");
        for (ActionListener listener : loadButton.getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }

        // controller.loadImage("load '"+imagePath+"' "+imageName, imageName);
        assertTrue(pngJpgImage.getImageMap().containsKey(imageName));
        assertEquals(mockView.getUpdateImageCallCount(), 2);
        //Single image update triggers this function twice: it updates the image and then the histogram of the image
    }

    @Test
    public void testSaveImage() {
        mockView.setFilePath(imagePath);
        JButton loadButton = mockView.getButtonForLoad();
        ActionEvent actionEvent = new ActionEvent(loadButton, ActionEvent.ACTION_PERFORMED, "Load");
        for (ActionListener listener : loadButton.getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }
        mockView.setFilePath("savedOutput.png");
        JButton saveButton = mockView.getButtonForSave();
        actionEvent = new ActionEvent(saveButton, ActionEvent.ACTION_PERFORMED, "Save");
        for (ActionListener listener : saveButton.getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }


        // controller.saveImage("save '/Users/snigdhabose/Documents/Hello World/testSave.jpg' img");

        File savedFile = new File("savedOutput.png");
        assertTrue("Image file should exist after saving.", savedFile.exists());

    }

    @Test
    public void testHistogram() {
        mockView.setFilePath(imagePath);

        JButton loadButton = mockView.getButtonForLoad();
        ActionEvent actionEvent = new ActionEvent(loadButton, ActionEvent.ACTION_PERFORMED, "Load");
        for (ActionListener listener : loadButton.getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }

        // controller.loadImage("load '"+imagePath+"' "+imageName, imageName);
        assertTrue(pngJpgImage.getImageMap().containsKey(imageName));
        assertEquals(mockView.getUpdateImageCallCount(), 2);
        //Single image update triggers this function twice: it updates the image and then the histogram of the image

        // Get the flipped image data
        int[][][] histogramRGB = pngJpgImage.getRgbDataMap("img-histogram");


        Histogram histogram = new Histogram(0, 255);
        histogram.createHistogram(histogramRGB);
        assertEquals(65536, histogram.calculateMaxCount());

        assertEquals(255, histogram.findPeakValue(histogram.histogramR));
        assertEquals(255, histogram.findPeakValue(histogram.histogramG));
        assertEquals(255, histogram.findPeakValue(histogram.histogramB));


    }


    @Test
    public void testVerticalFlip() {
        mockView.setFilePath(imagePath);
        JButton loadButton = mockView.getButtonForLoad();
        ActionEvent actionEvent = new ActionEvent(loadButton, ActionEvent.ACTION_PERFORMED, "Load");
        for (ActionListener listener : loadButton.getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }
        // controller.loadImage("load '/Users/snigdhabose/Documents/Hello World/Koala Img.jpg' img", "img");

        actionEvent = new ActionEvent(mockView.getEmptyComboBox(), ActionEvent.ACTION_PERFORMED, "vertical-flip");
        for (ActionListener listener : mockView.getEmptyComboBox().getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }

//testing combo box, if loaded and filter selected correctly
        assertEquals("vertical-flip", mockView.getSelectedFilter());

        assertEquals(mockView.getUpdateImageCallCount(), 2);

        actionEvent = new ActionEvent(mockView.getButtonForApply() , ActionEvent.ACTION_PERFORMED, "Apply filter");
        for (ActionListener listener : mockView.getButtonForApply().getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }
        assertTrue(pngJpgImage.getImageMap().containsKey("vertical-flip-"+imageName));
        assertEquals(mockView.getUpdateImageCallCount(), 4);


        // Get the flipped image data
        int[][][] flippedImageData = pngJpgImage.getRgbDataMap("vertical-flip-img");

        // Check if the flipped image matches the expected result
        int[][][] expectedFlippedImageData = new int[2][2][3];
        expectedFlippedImageData[0][0] = new int[]{0, 0, 255};
        expectedFlippedImageData[0][1] = new int[]{255, 255, 255};
        expectedFlippedImageData[1][0] = new int[]{255, 0, 0};
        expectedFlippedImageData[1] [1] = new int[]{0, 255, 0};

        for (int y = 0; y < expectedFlippedImageData.length; y++) {
            for (int x = 0; x < expectedFlippedImageData[y].length; x++) {
                for (int c = 0; c < 3; c++) {

                    assertEquals(expectedFlippedImageData[y][x][c], flippedImageData[y][x][c]);
                }
            }
        }


    }


    @Test
    public void testHorizontalFlip() {
        mockView.setFilePath(imagePath);
        JButton loadButton = mockView.getButtonForLoad();
        ActionEvent actionEvent = new ActionEvent(loadButton, ActionEvent.ACTION_PERFORMED, "Load");
        for (ActionListener listener : loadButton.getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }
        // controller.loadImage("load '/Users/snigdhabose/Documents/Hello World/Koala Img.jpg' img", "img");

        actionEvent = new ActionEvent(mockView.getEmptyComboBox(), ActionEvent.ACTION_PERFORMED, "horizontal-flip");
        for (ActionListener listener : mockView.getEmptyComboBox().getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }

//testing combo box, if loaded and filter selected correctly
        assertEquals("horizontal-flip", mockView.getSelectedFilter());

        assertEquals(mockView.getUpdateImageCallCount(), 2);

        actionEvent = new ActionEvent(mockView.getButtonForApply() , ActionEvent.ACTION_PERFORMED, "Apply filter");
        for (ActionListener listener : mockView.getButtonForApply().getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }
        assertTrue(pngJpgImage.getImageMap().containsKey("horizontal-flip-"+imageName));
        assertEquals(mockView.getUpdateImageCallCount(), 4);


        // Get the flipped image data
        int[][][] flippedImageData = pngJpgImage.getRgbDataMap("horizontal-flip-img");

        // Check if the flipped image matches the expected result
        int[][][] expectedFlippedImageData = new int[2][2][3];
        expectedFlippedImageData[0][0] = new int[]{0, 255, 0};
        expectedFlippedImageData[0][1] = new int[]{255, 0, 0};
        expectedFlippedImageData[1][0] = new int[]{255, 255, 255};
        expectedFlippedImageData[1][1] = new int[]{0, 0, 255};


        for (int y = 0; y < expectedFlippedImageData.length; y++) {
            for (int x = 0; x < expectedFlippedImageData[y].length; x++) {
                for (int c = 0; c < 3; c++) {
                    //System.out.println(flippedImageData[y][x][c]);
                    assertEquals(expectedFlippedImageData[y][x][c], flippedImageData[y][x][c]);

                }
            }
        }


    }




}

// MockImageEditorView class to simulate the ImageEditorView for testing purposes
class MockImageEditorView extends ImageEditorView {
    private int updateImageCallCount;
    JButton fileOpenButtonForLoad;
    JButton buttonForSave;

    JButton buttonForApply;
    String selectedFilter = null;
    String filePath = null;
    JComboBox<String> emptyComboBox;

    public MockImageEditorView() {
        fileOpenButtonForLoad = new JButton("Open File");
        buttonForSave = new JButton("Save File");
        buttonForApply = new JButton("Save File");
        updateImageCallCount = 0;
        emptyComboBox = new JComboBox<>();
    }

    public void setFilePath(String path) {
        filePath = "'" + path + "'";
    }

    public void setFilterOptions(String filter) {
        selectedFilter = filter;
    }

    public String getSelectedFilter() {
        return selectedFilter;
    }

    // Inside the MockImageEditorView class
    public JComboBox<String> getEmptyComboBox() {
        return emptyComboBox;
    }

    JButton getButtonForLoad() {
        updateImageCallCount=0;
        return fileOpenButtonForLoad;
    }

    JButton getButtonForApply() {
        return buttonForApply;
    }

    JButton getButtonForSave() {
        return buttonForSave;
    }

    @Override
    public void updateImageForIndex(int[][][] rgbValues, int index) {
        // Count number of times it simulates updating the image in the view
        updateImageCallCount++;


    }

    // Getter for testing purposes
    public int getUpdateImageCallCount() {
        return updateImageCallCount;
    }

    @Override
    public void addFeatures(ControllerFeatures features) {

        emptyComboBox.addActionListener(evt -> {
            System.out.println("ComboBox is triggered. Selected Filter:" + evt.getActionCommand());
            selectedFilter= evt.getActionCommand();
        });

        buttonForApply.addActionListener(evt -> {
            features.applyFeatures(selectedFilter + " img "+selectedFilter+"-img", selectedFilter+"-img");
            System.out.println("Applying Filter from Mock View: " + selectedFilter);

        });
        fileOpenButtonForLoad.addActionListener(evt -> {
            features.applyFeatures("load " + filePath + " img", "img");
            System.out.println("Loading from Mock View: " + selectedFilter);

        });
        buttonForSave.addActionListener(evt -> {
            features.applyFeatures("save " + filePath + " img", "img");
            System.out.println("Saving from Mock View: " + selectedFilter);

        });
    }
}