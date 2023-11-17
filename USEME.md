# USE-ME file

This document provides a comprehensive guide on the script commands supported by the image
processing application. Each command is detailed with examples and specific conditions, if any.

## Script Commands

- *Load an Image*:
    - `load <image_path> <image_name>`
    - Example: `load res/testImage.png testImage`
- *Save an Image*:
    - `save <output_image_path> <image_name>`
    - Example: `save res/testImage.png testImage`
- *Color Correction*:
    - `color-correct <source_image> <output_image>`
    - Example: `color-correct small small_color-correct`
- *Levels Adjustment*:
    - `levels-adjust <black_point> <midtone_point> <white_point> <source_image> <output_image>`
    - Example: `levels-adjust 20 100 255 small small_levels-adjust`
- *Brightness and Darkness Adjustment*:
    - Brighten: `brighten <increment> <source_image> <output_image>`
    - Darken: `darken <decrement> <source_image> <output_image>`
    - Example: `brighten 10 small small-brighter`
- *Image Flipping*:
    - Horizontal Flip: `horizontal-flip <source_image> <output_image>`
    - Vertical Flip: `vertical-flip <source_image> <output_image>`
    - Example: `horizontal-flip small small-horizontal`
- *Blur and Sharpen*:
    - `blur <source_image> <output_image>`
    - `sharpen <source_image> <output_image>`
    - Example: `blur small small-blur`
- *Color Filters*:
    - Sepia: `sepia <source_image> <output_image>`
    - Greyscale: `greyscale <source_image> <output_image>`
    - Example: `sepia small small-sepia`
- *RGB Component Splitting and Combining*:
    - Split: `rgb-split <source_image> <red_output> <green_output> <blue_output>`
    - Combine: `rgb-combine <output_image> <red_input> <green_input> <blue_input>`
    - Example: `rgb-split small small-red small-green small-blue`
- *Value, Intensity, and Luma Visualization*:
    - Value: `value-component <source_image> <output_image>`
    - Intensity: `intensity <source_image> <output_image>`
    - Luma: `luma <source_image> <output_image>`
    - Example: `value-component small small-value`
- *Create a Histogram Image*:
    - `histogram <source_image> <output_histogram_image>`
    - Example: `histogram small small-histogram`
- Commands can be executed for different image formats (PPM, JPG, PNG) with the same syntax.
- Example for JPG format:
    - `load images/small_4x3.jpg small`
    - `brighten 10 small small-brighter`
