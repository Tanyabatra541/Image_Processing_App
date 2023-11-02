# Assignment4_MVC

1. ImageContent:
   The `ImageContent` class is a simple data class used to store the name and content of images.
2. model.model.AbstractImage:
   Manipulation operations for images are based on `model.model.AbstractImage` class, which serves as an
   abstract foundation. For image operations, a set of abstract methods is defined which are
   for the three types of images(i.e. PNG, JPG, and PPM). there are two kernels defined as well
   which are used for image operations.
3. PNGJPGImage:
   Extending the abstract `model.model.AbstractImage` class is the `PNGJPGImage` class.
   The PNG image formats utilize the image operations specified in the `model.model.ImageOperations` interface
   that are implemented in the abstract class.
   Images in PNG format can be loaded, converted from RGB data, and saved with this class.
4. model.PPMImage:
   The `model.PPMImage` class, which makes use of the abstract `model.model.AbstractImage` class, has been formulated.
   The PPM image format is utilized with the `model.model.ImageOperations` interface to execute image
   operations.
   In PPM format, images can be loaded, RGB data converted, and images saved.
5. model.model.JPGImage:
   Extending the `PNGJPGImage` class is the `model.model.JPGImage` class.
   To capture photos in the JPG file format, the `saveImage` function is overridden and the rest
   of the functions are the same as the PNG class.
6. model.model-view-controller.controller:

   controller.controller (controller.controller): The `controller.controller` class is responsible for controlling the interaction
   between the model and view.
   It implements the ActionListener interface to handle user actions in the view.
   It interacts with the model and the view to update and display data based on user input.

   model.model.IModel (Interface): The `model.model.IModel` interface defines methods that the `model` must implement.

   view.IView (Interface): The `view.IView` interface defines methods that the `view` must implement.
7. `MVCExampleBasicMVC`:
   This class serves as the entry point of the application.
   It initializes the model, view, and controller and sets them up to work together.

The model.model class is responsible for interpreting the script file and executing the commands in it.
model.model command processing can be executed by following a specific protocol.
Using the executeScriptFromFile approach, the model.model class of the model is responsible for
administering and performing commands in a sequential order.
Using image manipulation techniques, the program carries out instructions found in a script
file that it interprets.

Implementing the model.model.ImageOperations interface are three classes: model.PPMImage, model.model.JPGImage, and PNGJPGImage,
each with their unique image manipulation abilities.

**How to run**:
1. Run the main method in the `Main` class.
2. Enter the file path of the script file in the text field and click execute or click the button 
   Go to command prompt to enter the commands manually.
3. The resulting images will be saved in the specified file mentioned in the script.
4. Make sure to change the file path in the script file to the path of the file you want to save 
   the images in.

**Overall Flow**:

The main method initializes the model, view, and controller.
The user interacts with the view by entering either the file path or clicking the button
that redirects them to the command prompt.
The controller responds to user actions and communicates with the model.
The model processes image commands and saves the resulting images in the specified file.

**Citations**:

test-image: "https://w7.pngwing.com/pngs/306/257/png-transparent-spongebob-squarepants-spongebob-heroes-cartoon-vehicle.png" 
(png image)