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
import java.util.Objects;

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
            pngJpgImage.loadImageInMap(imageName, rgbMatrix);
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
        assertEquals(mockView.getMessage(), "Operation Successful");
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
        assertEquals(mockView.getMessage(), "Operation Successful");
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
        assertEquals(mockView.getMessage(), "Operation Successful");
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

        actionEvent = new ActionEvent(mockView.getButtonForApply(), ActionEvent.ACTION_PERFORMED, "img");
        for (ActionListener listener : mockView.getButtonForApply().getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }
        assertTrue(pngJpgImage.getImageMap().containsKey("vertical-flip-" + imageName));
        assertEquals(mockView.getUpdateImageCallCount(), 4);
        assertEquals(mockView.getMessage(), "Operation Successful");

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

        actionEvent = new ActionEvent(mockView.getButtonForApply(), ActionEvent.ACTION_PERFORMED, "img");
        for (ActionListener listener : mockView.getButtonForApply().getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }
        assertTrue(pngJpgImage.getImageMap().containsKey("horizontal-flip-" + imageName));
        assertEquals(mockView.getUpdateImageCallCount(), 4);
        assertEquals(mockView.getMessage(), "Operation Successful");

    }

    @Test
    public void testSharpen() {
        mockView.setFilePath(imagePath);
        JButton loadButton = mockView.getButtonForLoad();
        ActionEvent actionEvent = new ActionEvent(loadButton, ActionEvent.ACTION_PERFORMED, "Load");
        for (ActionListener listener : loadButton.getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }
        // controller.loadImage("load '/Users/snigdhabose/Documents/Hello World/Koala Img.jpg' img", "img");

        actionEvent = new ActionEvent(mockView.getEmptyComboBox(), ActionEvent.ACTION_PERFORMED, "sharpen");
        for (ActionListener listener : mockView.getEmptyComboBox().getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }

//testing combo box, if loaded and filter selected correctly
        assertEquals("sharpen", mockView.getSelectedFilter());

        assertEquals(mockView.getUpdateImageCallCount(), 2);

        actionEvent = new ActionEvent(mockView.getButtonForApply(), ActionEvent.ACTION_PERFORMED, "img");
        for (ActionListener listener : mockView.getButtonForApply().getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }
        assertTrue(pngJpgImage.getImageMap().containsKey("sharpen-" + imageName));
        assertEquals(mockView.getUpdateImageCallCount(), 4);
        assertEquals(mockView.getMessage(), "Operation Successful");

    }

    @Test
    public void testBlur() {
        mockView.setFilePath(imagePath);
        JButton loadButton = mockView.getButtonForLoad();
        ActionEvent actionEvent = new ActionEvent(loadButton, ActionEvent.ACTION_PERFORMED, "Load");
        for (ActionListener listener : loadButton.getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }
        // controller.loadImage("load '/Users/snigdhabose/Documents/Hello World/Koala Img.jpg' img", "img");

        actionEvent = new ActionEvent(mockView.getEmptyComboBox(), ActionEvent.ACTION_PERFORMED, "blur");
        for (ActionListener listener : mockView.getEmptyComboBox().getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }

//testing combo box, if loaded and filter selected correctly
        assertEquals("blur", mockView.getSelectedFilter());

        assertEquals(mockView.getUpdateImageCallCount(), 2);

        actionEvent = new ActionEvent(mockView.getButtonForApply(), ActionEvent.ACTION_PERFORMED, "img");
        for (ActionListener listener : mockView.getButtonForApply().getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }
        assertTrue(pngJpgImage.getImageMap().containsKey("blur-" + imageName));
        assertEquals(mockView.getUpdateImageCallCount(), 4);
        assertEquals(mockView.getMessage(), "Operation Successful");

    }

    @Test
    public void testSepia() {
        mockView.setFilePath(imagePath);
        JButton loadButton = mockView.getButtonForLoad();
        ActionEvent actionEvent = new ActionEvent(loadButton, ActionEvent.ACTION_PERFORMED, "Load");
        for (ActionListener listener : loadButton.getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }
        // controller.loadImage("load '/Users/snigdhabose/Documents/Hello World/Koala Img.jpg' img", "img");

        actionEvent = new ActionEvent(mockView.getEmptyComboBox(), ActionEvent.ACTION_PERFORMED, "sepia");
        for (ActionListener listener : mockView.getEmptyComboBox().getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }

//testing combo box, if loaded and filter selected correctly
        assertEquals("sepia", mockView.getSelectedFilter());

        assertEquals(mockView.getUpdateImageCallCount(), 2);

        actionEvent = new ActionEvent(mockView.getButtonForApply(), ActionEvent.ACTION_PERFORMED, "img");
        for (ActionListener listener : mockView.getButtonForApply().getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }
        assertTrue(pngJpgImage.getImageMap().containsKey("sepia-" + imageName));
        assertEquals(mockView.getUpdateImageCallCount(), 4);
        assertEquals(mockView.getMessage(), "Operation Successful");

    }

    @Test
    public void testRedComponent() {
        mockView.setFilePath(imagePath);
        JButton loadButton = mockView.getButtonForLoad();
        ActionEvent actionEvent = new ActionEvent(loadButton, ActionEvent.ACTION_PERFORMED, "Load");
        for (ActionListener listener : loadButton.getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }
        // controller.loadImage("load '/Users/snigdhabose/Documents/Hello World/Koala Img.jpg' img", "img");

        actionEvent = new ActionEvent(mockView.getEmptyComboBox(), ActionEvent.ACTION_PERFORMED, "red-component");
        for (ActionListener listener : mockView.getEmptyComboBox().getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }

//testing combo box, if loaded and filter selected correctly
        assertEquals("red-component", mockView.getSelectedFilter());

        assertEquals(mockView.getUpdateImageCallCount(), 2);

        actionEvent = new ActionEvent(mockView.getButtonForApply(), ActionEvent.ACTION_PERFORMED, "img");
        for (ActionListener listener : mockView.getButtonForApply().getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }
        assertTrue(pngJpgImage.getImageMap().containsKey("red-component-" + imageName));
        assertEquals(mockView.getUpdateImageCallCount(), 4);
        assertEquals(mockView.getMessage(), "Operation Successful");

    }

    @Test
    public void testGreenComponent() {
        mockView.setFilePath(imagePath);
        JButton loadButton = mockView.getButtonForLoad();
        ActionEvent actionEvent = new ActionEvent(loadButton, ActionEvent.ACTION_PERFORMED, "Load");
        for (ActionListener listener : loadButton.getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }
        // controller.loadImage("load '/Users/snigdhabose/Documents/Hello World/Koala Img.jpg' img", "img");

        actionEvent = new ActionEvent(mockView.getEmptyComboBox(), ActionEvent.ACTION_PERFORMED, "green-component");
        for (ActionListener listener : mockView.getEmptyComboBox().getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }

//testing combo box, if loaded and filter selected correctly
        assertEquals("green-component", mockView.getSelectedFilter());

        assertEquals(mockView.getUpdateImageCallCount(), 2);

        actionEvent = new ActionEvent(mockView.getButtonForApply(), ActionEvent.ACTION_PERFORMED, "img");
        for (ActionListener listener : mockView.getButtonForApply().getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }
        assertTrue(pngJpgImage.getImageMap().containsKey("green-component-" + imageName));
        assertEquals(mockView.getUpdateImageCallCount(), 4);
        assertEquals(mockView.getMessage(), "Operation Successful");

    }

    @Test
    public void testBlueComponent() {
        mockView.setFilePath(imagePath);
        JButton loadButton = mockView.getButtonForLoad();
        ActionEvent actionEvent = new ActionEvent(loadButton, ActionEvent.ACTION_PERFORMED, "Load");
        for (ActionListener listener : loadButton.getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }
        // controller.loadImage("load '/Users/snigdhabose/Documents/Hello World/Koala Img.jpg' img", "img");

        actionEvent = new ActionEvent(mockView.getEmptyComboBox(), ActionEvent.ACTION_PERFORMED, "blue-component");
        for (ActionListener listener : mockView.getEmptyComboBox().getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }

//testing combo box, if loaded and filter selected correctly
        assertEquals("blue-component", mockView.getSelectedFilter());

        assertEquals(mockView.getUpdateImageCallCount(), 2);

        actionEvent = new ActionEvent(mockView.getButtonForApply(), ActionEvent.ACTION_PERFORMED, "img");
        for (ActionListener listener : mockView.getButtonForApply().getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }
        assertTrue(pngJpgImage.getImageMap().containsKey("blue-component-" + imageName));
        assertEquals(mockView.getUpdateImageCallCount(), 4);
        assertEquals(mockView.getMessage(), "Operation Successful");

    }

    @Test
    public void testValueComponent() {
        mockView.setFilePath(imagePath);
        JButton loadButton = mockView.getButtonForLoad();
        ActionEvent actionEvent = new ActionEvent(loadButton, ActionEvent.ACTION_PERFORMED, "Load");
        for (ActionListener listener : loadButton.getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }
        // controller.loadImage("load '/Users/snigdhabose/Documents/Hello World/Koala Img.jpg' img", "img");

        actionEvent = new ActionEvent(mockView.getEmptyComboBox(), ActionEvent.ACTION_PERFORMED, "value-component");
        for (ActionListener listener : mockView.getEmptyComboBox().getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }

//testing combo box, if loaded and filter selected correctly
        assertEquals("value-component", mockView.getSelectedFilter());

        assertEquals(mockView.getUpdateImageCallCount(), 2);

        actionEvent = new ActionEvent(mockView.getButtonForApply(), ActionEvent.ACTION_PERFORMED, "img");
        for (ActionListener listener : mockView.getButtonForApply().getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }
        assertTrue(pngJpgImage.getImageMap().containsKey("value-component-" + imageName));
        assertEquals(mockView.getUpdateImageCallCount(), 4);
        assertEquals(mockView.getMessage(), "Operation Successful");

    }

    @Test
    public void testColorCorrect() {
        mockView.setFilePath(imagePath);
        JButton loadButton = mockView.getButtonForLoad();
        ActionEvent actionEvent = new ActionEvent(loadButton, ActionEvent.ACTION_PERFORMED, "Load");
        for (ActionListener listener : loadButton.getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }
        // controller.loadImage("load '/Users/snigdhabose/Documents/Hello World/Koala Img.jpg' img", "img");

        actionEvent = new ActionEvent(mockView.getEmptyComboBox(), ActionEvent.ACTION_PERFORMED, "color-correct");
        for (ActionListener listener : mockView.getEmptyComboBox().getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }

//testing combo box, if loaded and filter selected correctly
        assertEquals("color-correct", mockView.getSelectedFilter());

        assertEquals(mockView.getUpdateImageCallCount(), 2);

        actionEvent = new ActionEvent(mockView.getButtonForApply(), ActionEvent.ACTION_PERFORMED, "img");
        for (ActionListener listener : mockView.getButtonForApply().getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }
        assertTrue(pngJpgImage.getImageMap().containsKey("color-correct-" + imageName));
        assertEquals(mockView.getUpdateImageCallCount(), 4);
        assertEquals(mockView.getMessage(), "Operation Successful");

    }

    @Test
    public void testLevelsAdjust() {
        mockView.setFilePath(imagePath);

        JButton loadButton = mockView.getButtonForLoad();
        ActionEvent actionEvent = new ActionEvent(loadButton, ActionEvent.ACTION_PERFORMED, "Load");
        for (ActionListener listener : loadButton.getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }
        // controller.loadImage("load '/Users/snigdhabose/Documents/Hello World/Koala Img.jpg' img", "img");

        actionEvent = new ActionEvent(mockView.getEmptyComboBox(), ActionEvent.ACTION_PERFORMED, "levels-adjust");
        for (ActionListener listener : mockView.getEmptyComboBox().getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }

//testing combo box, if loaded and filter selected correctly
        assertEquals("levels-adjust", mockView.getSelectedFilter());

        assertEquals(mockView.getUpdateImageCallCount(), 2);
        mockView.setBMW(20, 50, 200);

        actionEvent = new ActionEvent(mockView.getButtonForApply(), ActionEvent.ACTION_PERFORMED, "img");
        for (ActionListener listener : mockView.getButtonForApply().getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }
        assertTrue(pngJpgImage.getImageMap().containsKey("levels-adjust-" + imageName));
        assertEquals(mockView.getUpdateImageCallCount(), 4);
        assertEquals(mockView.getMessage(), "Operation Successful");

    }

    @Test
    public void testCompress() {
        mockView.setFilePath(imagePath);

        JButton loadButton = mockView.getButtonForLoad();
        ActionEvent actionEvent = new ActionEvent(loadButton, ActionEvent.ACTION_PERFORMED, "Load");
        for (ActionListener listener : loadButton.getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }
        // controller.loadImage("load '/Users/snigdhabose/Documents/Hello World/Koala Img.jpg' img", "img");

        actionEvent = new ActionEvent(mockView.getEmptyComboBox(), ActionEvent.ACTION_PERFORMED, "compress");
        for (ActionListener listener : mockView.getEmptyComboBox().getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }

//testing combo box, if loaded and filter selected correctly
        assertEquals("compress", mockView.getSelectedFilter());

        assertEquals(mockView.getUpdateImageCallCount(), 2);
        mockView.setCompressionPercentage(50);

        actionEvent = new ActionEvent(mockView.getButtonForApply(), ActionEvent.ACTION_PERFORMED, "img");
        for (ActionListener listener : mockView.getButtonForApply().getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }
        assertTrue(pngJpgImage.getImageMap().containsKey("compress-" + imageName));
        assertEquals(mockView.getUpdateImageCallCount(), 4);
        assertEquals(mockView.getMessage(), "Operation Successful");

    }

    @Test
    public void testCompressError() {
        mockView.setFilePath(imagePath);

        JButton loadButton = mockView.getButtonForLoad();
        ActionEvent actionEvent = new ActionEvent(loadButton, ActionEvent.ACTION_PERFORMED, "Load");
        for (ActionListener listener : loadButton.getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }
        // controller.loadImage("load '/Users/snigdhabose/Documents/Hello World/Koala Img.jpg' img", "img");

        actionEvent = new ActionEvent(mockView.getEmptyComboBox(), ActionEvent.ACTION_PERFORMED, "compress");
        for (ActionListener listener : mockView.getEmptyComboBox().getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }

//testing combo box, if loaded and filter selected correctly
        assertEquals("compress", mockView.getSelectedFilter());

        assertEquals(mockView.getUpdateImageCallCount(), 2);
        mockView.setCompressionPercentage(500);

        actionEvent = new ActionEvent(mockView.getButtonForApply(), ActionEvent.ACTION_PERFORMED, "img");
        for (ActionListener listener : mockView.getButtonForApply().getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }

        assertEquals(mockView.getUpdateImageCallCount(), 2);
        assertEquals(mockView.getMessage(), "Compression percentage should be between 0 and 100");

    }

    @Test
    public void testCompressError2() {
        mockView.setFilePath(imagePath);

        JButton loadButton = mockView.getButtonForLoad();
        ActionEvent actionEvent = new ActionEvent(loadButton, ActionEvent.ACTION_PERFORMED, "Load");
        for (ActionListener listener : loadButton.getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }
        // controller.loadImage("load '/Users/snigdhabose/Documents/Hello World/Koala Img.jpg' img", "img");

        actionEvent = new ActionEvent(mockView.getEmptyComboBox(), ActionEvent.ACTION_PERFORMED, "compress");
        for (ActionListener listener : mockView.getEmptyComboBox().getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }

//testing combo box, if loaded and filter selected correctly
        assertEquals("compress", mockView.getSelectedFilter());

        assertEquals(mockView.getUpdateImageCallCount(), 2);
        mockView.setCompressionPercentage(-50);

        actionEvent = new ActionEvent(mockView.getButtonForApply(), ActionEvent.ACTION_PERFORMED, "img");
        for (ActionListener listener : mockView.getButtonForApply().getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }

        assertEquals(mockView.getUpdateImageCallCount(), 2);
        assertEquals(mockView.getMessage(), "Compression percentage should be between 0 and 100");

    }

    @Test
    public void testLevelsAdjustError() {
        mockView.setFilePath(imagePath);

        JButton loadButton = mockView.getButtonForLoad();
        ActionEvent actionEvent = new ActionEvent(loadButton, ActionEvent.ACTION_PERFORMED, "Load");
        for (ActionListener listener : loadButton.getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }
        // controller.loadImage("load '/Users/snigdhabose/Documents/Hello World/Koala Img.jpg' img", "img");

        actionEvent = new ActionEvent(mockView.getEmptyComboBox(), ActionEvent.ACTION_PERFORMED, "levels-adjust");
        for (ActionListener listener : mockView.getEmptyComboBox().getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }

//testing combo box, if loaded and filter selected correctly
        assertEquals("levels-adjust", mockView.getSelectedFilter());

        assertEquals(mockView.getUpdateImageCallCount(), 2);
        mockView.setBMW(20, 500, 200);

        actionEvent = new ActionEvent(mockView.getButtonForApply(), ActionEvent.ACTION_PERFORMED, "img");
        for (ActionListener listener : mockView.getButtonForApply().getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }

        assertEquals(mockView.getUpdateImageCallCount(), 2);

        assertEquals(mockView.getMessage(), "Invalid shadow, mid, highlight points");

    }

    @Test
    public void testLevelsAdjustError2() {
        mockView.setFilePath(imagePath);

        JButton loadButton = mockView.getButtonForLoad();
        ActionEvent actionEvent = new ActionEvent(loadButton, ActionEvent.ACTION_PERFORMED, "Load");
        for (ActionListener listener : loadButton.getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }
        // controller.loadImage("load '/Users/snigdhabose/Documents/Hello World/Koala Img.jpg' img", "img");

        actionEvent = new ActionEvent(mockView.getEmptyComboBox(), ActionEvent.ACTION_PERFORMED, "levels-adjust");
        for (ActionListener listener : mockView.getEmptyComboBox().getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }

//testing combo box, if loaded and filter selected correctly
        assertEquals("levels-adjust", mockView.getSelectedFilter());

        assertEquals(mockView.getUpdateImageCallCount(), 2);
        mockView.setBMW(-5, 100, 200);

        actionEvent = new ActionEvent(mockView.getButtonForApply(), ActionEvent.ACTION_PERFORMED, "img");
        for (ActionListener listener : mockView.getButtonForApply().getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }

        assertEquals(mockView.getUpdateImageCallCount(), 2);

        assertEquals(mockView.getMessage(), "Invalid shadow, mid, highlight points");

    }

    @Test
    public void testLevelsAdjustError3() {
        mockView.setFilePath(imagePath);

        JButton loadButton = mockView.getButtonForLoad();
        ActionEvent actionEvent = new ActionEvent(loadButton, ActionEvent.ACTION_PERFORMED, "Load");
        for (ActionListener listener : loadButton.getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }
        // controller.loadImage("load '/Users/snigdhabose/Documents/Hello World/Koala Img.jpg' img", "img");

        actionEvent = new ActionEvent(mockView.getEmptyComboBox(), ActionEvent.ACTION_PERFORMED, "levels-adjust");
        for (ActionListener listener : mockView.getEmptyComboBox().getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }

//testing combo box, if loaded and filter selected correctly
        assertEquals("levels-adjust", mockView.getSelectedFilter());

        assertEquals(mockView.getUpdateImageCallCount(), 2);
        mockView.setBMW(20, 100, 10);

        actionEvent = new ActionEvent(mockView.getButtonForApply(), ActionEvent.ACTION_PERFORMED, "img");
        for (ActionListener listener : mockView.getButtonForApply().getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }

        assertEquals(mockView.getUpdateImageCallCount(), 2);

        assertEquals(mockView.getMessage(), "Invalid shadow, mid, highlight points");

    }

    @Test
    public void testSepiaWithSplit() {
        mockView.setFilePath(imagePath);
        JButton loadButton = mockView.getButtonForLoad();
        ActionEvent actionEvent = new ActionEvent(loadButton, ActionEvent.ACTION_PERFORMED, "Load");
        for (ActionListener listener : loadButton.getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }
        // controller.loadImage("load '/Users/snigdhabose/Documents/Hello World/Koala Img.jpg' img", "img");

        actionEvent = new ActionEvent(mockView.getEmptyComboBox(), ActionEvent.ACTION_PERFORMED, "sepia");
        for (ActionListener listener : mockView.getEmptyComboBox().getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }

        mockView.setSlider(50);

//testing combo box, if loaded and filter selected correctly
        assertEquals("sepia", mockView.getSelectedFilter());

        assertEquals(mockView.getUpdateImageCallCount(), 2);

        actionEvent = new ActionEvent(mockView.getButtonForApply(), ActionEvent.ACTION_PERFORMED, "img");
        for (ActionListener listener : mockView.getButtonForApply().getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }
        assertTrue(pngJpgImage.getImageMap().containsKey("sepia-" + imageName + "-split"));
        assertEquals(mockView.getUpdateImageCallCount(), 4);
        assertEquals(mockView.getMessage(), "Operation Successful");

    }

    @Test
    public void testSharpenWithSplitError() {
        mockView.setFilePath(imagePath);
        JButton loadButton = mockView.getButtonForLoad();
        ActionEvent actionEvent = new ActionEvent(loadButton, ActionEvent.ACTION_PERFORMED, "Load");
        for (ActionListener listener : loadButton.getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }
        // controller.loadImage("load '/Users/snigdhabose/Documents/Hello World/Koala Img.jpg' img", "img");

        actionEvent = new ActionEvent(mockView.getEmptyComboBox(), ActionEvent.ACTION_PERFORMED, "sharpen");
        for (ActionListener listener : mockView.getEmptyComboBox().getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }

        mockView.setSlider(500);

//testing combo box, if loaded and filter selected correctly
        assertEquals("sharpen", mockView.getSelectedFilter());

        assertEquals(mockView.getUpdateImageCallCount(), 2);

        actionEvent = new ActionEvent(mockView.getButtonForApply(), ActionEvent.ACTION_PERFORMED, "img");
        for (ActionListener listener : mockView.getButtonForApply().getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }
        //assertTrue(pngJpgImage.getImageMap().containsKey("sharpen-"+imageName+"-split"));
        //assertEquals(mockView.getUpdateImageCallCount(), 4);
        assertEquals(mockView.getMessage(), "Split percentage should be between 0 and 100");

    }

    @Test
    public void testMultipleFilters() {
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

        actionEvent = new ActionEvent(mockView.getButtonForApply(), ActionEvent.ACTION_PERFORMED, "img");
        for (ActionListener listener : mockView.getButtonForApply().getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }
        assertTrue(pngJpgImage.getImageMap().containsKey("horizontal-flip-" + imageName));
        assertEquals(mockView.getUpdateImageCallCount(), 4);
        assertEquals(mockView.getMessage(), "Operation Successful");

        actionEvent = new ActionEvent(mockView.getEmptyComboBox(), ActionEvent.ACTION_PERFORMED, "vertical-flip");
        for (ActionListener listener : mockView.getEmptyComboBox().getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }

//testing combo box, if loaded and filter selected correctly
        assertEquals("vertical-flip", mockView.getSelectedFilter());
        assertEquals(mockView.getUpdateImageCallCount(), 4);

        actionEvent = new ActionEvent(mockView.getButtonForApply(), ActionEvent.ACTION_PERFORMED, "horizontal-flip-" + imageName);
        for (ActionListener listener : mockView.getButtonForApply().getActionListeners()) {
            listener.actionPerformed(actionEvent);
        }

        assertTrue(pngJpgImage.getImageMap().containsKey("horizontal-flip-" + imageName));
        assertEquals(mockView.getUpdateImageCallCount(), 6);
        assertEquals(mockView.getMessage(), "Operation Successful");


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
    String message = null;

    int b = 0;
    int m = 0;
    int w = 0;

    int compressionPercentage = 0;
    int slider = 0;

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

    public void setBMW(int bV, int mV, int wV) {
        b = bV;
        m = mV;
        w = wV;
    }

    public void setCompressionPercentage(int c) {
        compressionPercentage = c;
    }

    public void setFilterOptions(String filter) {
        selectedFilter = filter;
    }

    public String getSelectedFilter() {
        return selectedFilter;
    }

    public String getMessage() {
        return message;
    }

    // Inside the MockImageEditorView class
    public JComboBox<String> getEmptyComboBox() {
        return emptyComboBox;
    }

    JButton getButtonForLoad() {
        updateImageCallCount = 0;
        compressionPercentage = 0;
        b = 0;
        m = 0;
        w = 0;
        slider = 0;
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

    public void setSlider(int s) {
        slider = s;
    }

    @Override
    public void addFeatures(ControllerFeatures features) {

        emptyComboBox.addActionListener(evt -> {
            System.out.println("ComboBox is triggered. Selected Filter:" + evt.getActionCommand());
            selectedFilter = evt.getActionCommand();
        });

        buttonForApply.addActionListener(evt -> {
            String source = evt.getActionCommand();
            String msg = null;
            if (slider != 0) {
                msg = features.applyFeatures(selectedFilter + " " + source + " " + selectedFilter + "-img-split split " + slider, selectedFilter + "-img-split");
            } else {
                if (Objects.equals(selectedFilter, "levels-adjust")) {
                    msg = features.applyFeatures(selectedFilter + " " + b + " " + m + " " + w + " " + source + " " + selectedFilter + "-img", selectedFilter + "-img");
                } else if (Objects.equals(selectedFilter, "compress")) {
                    msg = features.applyFeatures(selectedFilter + " " + compressionPercentage + " " + source + " " + selectedFilter + "-img", selectedFilter + "-img");
                } else {
                    msg = features.applyFeatures(selectedFilter + " " + source + " " + selectedFilter + "-img", selectedFilter + "-img");
                }
            }
            System.out.println("Applying Filter from Mock View: " + selectedFilter);
            slider = 0;
            message = msg;

        });
        fileOpenButtonForLoad.addActionListener(evt -> {
            String msg = features.applyFeatures("load " + filePath + " img", "img");
            System.out.println("Loading from Mock View: " + selectedFilter);
            message = msg;

        });
        buttonForSave.addActionListener(evt -> {
            String msg = features.applyFeatures("save " + filePath + " img", "img");
            System.out.println("Saving from Mock View: " + selectedFilter);
            message = msg;
        });
    }
}