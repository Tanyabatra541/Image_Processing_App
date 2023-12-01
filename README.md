# Image Editor

**Model**
1. ImageContent:
   The `ImageContent` class is designed to represent an image along with its associated name and
   content (the rgb map). This class serves as a container for storing image data.
2. ImageOperations:
   The `ImageOperations` interface has all the function signatures of the operations that
   can be performed on an image like horizontal flip, blur, sharpen, etc. This interface is
   implemented by the `ImageModel` class.
3. ImageModel:
   All the operation implementations for images are done in `ImageModel` class (except the load and 
   save operations), which implements the `ImageOperations` interface. 
4. Histogram:
   The `Histogram` class represents a histogram for RGB color values and offers functionality for
   creating histogram images, adding color values to the histogram, calculating the maximum count
   of color values, and finding peak values in the histogram arrays. This class facilitates the
   analysis and visualization of color distribution in an image through histograms. It can take an 
   image rgb values as the input and return a BufferedImage of the histogram formed by them.
5. Compression:
   The `Compression` class provides methods for compressing images using Haar Wavelet Transform
   and thresholding techniques. It initializes, pads, and transforms the color channels of an image
   based on the specified compression percentage, applies threshold, and performs inverse
   transformations and un-pads the image to achieve image compression while maintaining visual 
   quality. The class ultimately returns the compressed RGB data of the image.

**Controller**:
1. The `Controller` class serves as the controller in the Model-View-Controller (MVC) architectural
  pattern. The class handles various image processing operations based on user input, such as
  loading and saving images, applying filters, adjusting levels, and generating histograms and
  tells the model what to do and sends the control to the model based on the user input.
  It supports the execution of image processing scripts from a text file, interactive mode as well 
  as through the GUI.
2. The `Controller` class is responsible for executing the commands in the script file, the command
  passed through the command line or the GUI.
3. The controller package consists of `ControllerFeatures` which is an interface
  that acts as an intermediary between the view and the controller. This was done to avoid coupling
  between the view and controller. Only methods that are required by the view are exposed to the
  view. Others are not included in the interface. This interface consists of 4 functions which are
  implemented in the controller. There functions are the only functions accessed by view through
  this interface.
4. It also consists of a class `IOImageOperations` which is responsible for loading and saving
  images. It is used by the controller to load and save images of any form, be it PNG, JPG or PPM.
  All the IO Operations take place in the controller and not the model.

**View**:
1. We have implemented a GUI which is divided into 4 main parts: Loading an image, image viewing, 
   applying the filter to the image and finally saving the image.
   - The user first loads an image from their device
   - The GUI contains three frames, first is to view the loaded original image, second is to view 
     to image after applying any filter, and third is to view the histogram.
   - After the layout of these three frames, there is an "Apply Filter" button.
   - At the end, there is an option for the user to save the file at the desired location.
   - There is a small help button on the top too for the user to get help regarding the GUI.

`Main`:
This class serves as the entry point of the application.
It initializes the controller and the program starts from the controller having the control.

**How to run with JAR file Command line arguments**:
1. `java -jar Assignment4_MVC.jar`: This command will launch the GUI directly.(run this in the 
    terminal inside the res folder)
2. `java -jar Assignment4_MVC.jar -text`: This command will open an interactive mode where the user 
    can enter commands one by one.
3. `java -jar Assignment4_MVC.jar -file scriptFile.txt`: This command will execute the file and 
    execute all the commands in the file.(run this command too in the res folder. The scriptFile 
    is also in the res folder)


**Overall Flow**:

The `imageEditor` method initializes the controller and the controller then has access to the 
view and model. The controller calls the setView function in the controller which in turn calls a 
function addFeatures in the view which sets the GUI and contains all the action listeners whenever 
a button is clicked. The view sends the controller the information about what the user has clicked, 
and then the controller sends the control to the model based on what operation needs to be applied. 
The controller again calls the view to display the operated image and the output to the user.

**Parts of the program that are complete**:

1. The program can perform all the operations on PNG, JPG, JPEG and PPM images.
2. The program can perform all the operations on PNG, JPG, JPEG and PPM images using the interactive
   command approach.
3. The program can perform all the operations on PNG, JPG, JPEG and PPM images using the script file.
4. The program can perform all the operations on PNG, JPG, JPEG and PPM images in the GUI.
5. MVC architecture is implemented.
6. Supports all the operations mentioned in the assignment document.
7. Supports all the operations from the previous assignment also.
8. JAR file is created and working.
9. res folder is prepared for the output reference.
10. README and UseMe file also created.
11. Put single quotes around the image path. For example, if the image path is "test-image.png", then
    the command should be `load 'test-image.png' img` and not `load test-image.png img`.

**Design changes and justifications**:

- No change made in the model.
- An interface `ControlerFeatures` added in the controller to act as an intermediary between the view
  and the controller. This was done to avoid coupling between the view and controller. Only methods that
  are required by the view are exposed to the view. Others are not included in the interface.
  This interface consists of 4 functions which are implemented in the controller. There functions are the only
  functions accessed by view through this interface. A new interface was needed in this case to not
  expose all the controller methods to view and only expose the ones that are required by the view.
- The `ImageEditorView` is the major implementation of this assignment which implements the GUI and
  interacts with the user taking input as well as displaying the output.
- The `IOImageOperations` class is responsible for loading and saving images. It is used by the
  controller to load and save images of any form, be it PNG, JPG or PPM. All the IO Operations take
  place in the controller and not the model. This was done because all the IO operations are 
  supposed to be done in the controller and not the model.

**Citations**:

test-image: https://images.unsplash.com/photo-1575270631421-f4c69e17b2ce?q=80&w=2969&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D
(png image)
Proof of the terms of usage of the image: https://unsplash.com/license

