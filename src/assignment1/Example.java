package assignment1;

import java.io.FileInputStream; 
import java.io.FileNotFoundException; 
import javafx.application.Application;
import javafx.beans.value.ChangeListener; 
import javafx.beans.value.ObservableValue; 
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;  
import javafx.scene.image.WritableImage;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.*;

// OK this is not best practice - maybe you'd like to create
// a volume data class?
// I won't give extra marks for that though.

public class Example extends Application {
    short cthead[][][]; //store the 3D volume data set
    short min, max; //min/max value in the 3D volume data set
    int CT_x_axis = 256;
    int CT_y_axis = 256;
    int CT_z_axis = 113;
	
    @Override
    public void start(Stage stage) throws FileNotFoundException, IOException {
	stage.setTitle("CThead Viewer");

        
	ReadData();

	//Good practice: Define your top view, front view and side view images (get the height and width correct)
	//Here's the top view - looking down on the top of the head (each slice we are looking at is CT_x_axis x CT_y_axis)
	int Top_width = CT_x_axis;
        int Top_height = CT_y_axis;
		
	//Here's the front view - looking at the front (nose) of the head (each slice we are looking at is CT_x_axis x CT_z_axis)
	int Front_width = CT_x_axis;
	int Front_height = CT_z_axis;
		
	//and you do the other (side view) - looking at the ear of the head
        int Side_width = CT_y_axis;
        int Side_height = CT_z_axis;
        
	//We need 3 things to see an image
	//1. We create an image we can write to
	WritableImage top_image = new WritableImage(Top_width, Top_height);
        WritableImage front_image = new WritableImage(Front_width, Front_height);
        WritableImage side_image = new WritableImage(Side_width, Side_height);
        
        WritableImage top_imageVT = new WritableImage(Top_width, Top_height);
        WritableImage front_imageVT = new WritableImage(Front_width, Front_height);
        WritableImage side_imageVT = new WritableImage(Side_width, Side_height);
	
        //2. We create a view of that image
	ImageView TopView = new ImageView(top_image);
        ImageView FrontView = new ImageView(front_image);
        ImageView SideView = new ImageView(side_image);
        
        ImageView TopViewVT = new ImageView(top_imageVT);
        ImageView FrontViewVT = new ImageView(front_imageVT);
        ImageView SideViewVT = new ImageView(side_imageVT);

	Button slice76_button=new Button("slice76"); //an example button to get the slice 76
	//sliders to step through the slices (top and front directions) (remember 113 slices in top direction 0-112)
	Slider Top_slider = new Slider(0, CT_z_axis-1, 0);
	Slider Front_slider = new Slider(0, CT_y_axis-1, 0);
        Slider Side_slider = new Slider(0, CT_x_axis-1, 0);
        
        Slider Top_sliderVT = new Slider(0, 100, 0);
	Slider Front_sliderVT = new Slider(0, 100, 0);
        Slider Side_sliderVT = new Slider(0, 100, 0);
	
	slice76_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                TopDownSlice(top_image, 76);
                FrontBackSlice(front_image, 76);
                SideSlice(side_image, 76);
                TopDownSliceVT(top_imageVT, 12);
                FrontBackSliceVT(front_imageVT, 12);
                SideSliceVT(side_imageVT, 12);
            }
        });
		
	Top_slider.valueProperty().addListener( 
        new ChangeListener<Number>() { 
            public void changed(ObservableValue <? extends Number >  
                                observable, Number oldValue, Number newValue) 
            { 
                System.out.println(newValue.intValue());
                TopDownSlice(top_image, newValue.intValue());
            } 
        }); 
        	
	Front_slider.valueProperty().addListener( 
        new ChangeListener<Number>() { 
            public void changed(ObservableValue <? extends Number >  
                                observable, Number oldValue, Number newValue) 
            { 
                System.out.println(newValue.intValue());
                FrontBackSlice(front_image, newValue.intValue());
            } 
        }); 
        
        Side_slider.valueProperty().addListener( 
        new ChangeListener<Number>() { 
            public void changed(ObservableValue <? extends Number >  
                                observable, Number oldValue, Number newValue) 
            { 
                System.out.println(newValue.intValue());
                SideSlice(side_image, newValue.intValue());
            } 
        }); 
        
        Top_sliderVT.valueProperty().addListener( 
        new ChangeListener<Number>() { 
            public void changed(ObservableValue <? extends Number >  
                                observable, Number oldValue, Number newValue) 
            { 
                System.out.println(newValue.intValue());
                TopDownSliceVT(top_imageVT, newValue.intValue());
            } 
        }); 
        	
	Front_sliderVT.valueProperty().addListener( 
        new ChangeListener<Number>() { 
            public void changed(ObservableValue <? extends Number >  
                                observable, Number oldValue, Number newValue) 
            { 
                System.out.println(newValue.intValue());
                FrontBackSliceVT(front_imageVT, newValue.intValue());
            } 
        }); 
        
        Side_sliderVT.valueProperty().addListener( 
        new ChangeListener<Number>() { 
            public void changed(ObservableValue <? extends Number >  
                                observable, Number oldValue, Number newValue) 
            { 
                System.out.println(newValue.intValue());
                SideSliceVT(side_imageVT, newValue.intValue());
            } 
        }); 
		
	FlowPane root = new FlowPane();
	root.setVgap(8);
        root.setHgap(4);
        //https://examples.javacodegeeks.com/desktop-java/javafx/scene/image-scene/javafx-image-example/

	//3. (referring to the 3 things we need to display an image)
	//we need to add it to the flow pane
	root.getChildren().addAll(TopView, Top_slider);
        root.getChildren().addAll(FrontView, Front_slider);
        root.getChildren().addAll(SideView, Side_slider);
        root.getChildren().addAll(TopViewVT, Top_sliderVT);
        root.getChildren().addAll(FrontViewVT, Front_sliderVT);
        root.getChildren().addAll(SideViewVT, Side_sliderVT);
        root.getChildren().add(slice76_button);

        Scene scene = new Scene(root, 640, 480);
        stage.setScene(scene);
        stage.show();
    }
	
    //Function to read in the cthead data set
    public void ReadData() throws IOException {
	//File name is hardcoded here - much nicer to have a dialog to select it and capture the size from the user
	File file = new File("CThead");
	//Read the data quickly via a buffer (in C++ you can just do a single fread - I couldn't find if there is an equivalent in Java)
	DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
	
	int i, j, k; //loop through the 3D data set
		
	min=Short.MAX_VALUE; max=Short.MIN_VALUE; //set to extreme values
	short read; //value read in
	int b1, b2; //data is wrong Endian (check wikipedia) for Java so we need to swap the bytes around
		
	cthead = new short[CT_z_axis][CT_y_axis][CT_x_axis]; //allocate the memory - note this is fixed for this data set
	//loop through the data reading it in
	for (k=0; k<CT_z_axis; k++) {
            for (j=0; j<CT_y_axis; j++) {
		for (i=0; i<CT_x_axis; i++) {
                    //because the Endianess is wrong, it needs to be read byte at a time and swapped
                    b1=((int)in.readByte()) & 0xff; //the 0xff is because Java does not have unsigned types
                    b2=((int)in.readByte()) & 0xff; //the 0xff is because Java does not have unsigned types
                    read=(short)((b2<<8) | b1); //and swizzle the bytes around
                    if (read<min) min=read; //update the minimum
                    if (read>max) max=read; //update the maximum
                    cthead[k][j][i]=read; //put the short into memory (in C++ you can replace all this code with one fread)
		}
            }
	}
	System.out.println(min+" "+max); //diagnostic - for CThead this should be -1117, 2248
	//(i.e. there are 3366 levels of grey (we are trying to display on 256 levels of grey)
	//therefore histogram equalization would be a good thing
	//maybe put your histogram equalization code here to set up the mapping array
    }
        public void TopDownSlice(WritableImage image, int sliceNumber) {
        //Get image dimensions, and declare loop variables
        int w=(int) image.getWidth(), h=(int) image.getHeight();
        PixelWriter image_writer = image.getPixelWriter();
        int z = sliceNumber;
	double col;
	short datum;
        //Shows how to loop through each pixel and colour
        //Try to always use j for loops in y, and i for loops in x
        //as this makes the code more readable
        for (int j=0; j<h; j++) {
            for (int i=0; i<w; i++) {
                //at this point (i,j) is a single pixel in the image
                //here you would need to do something to (i,j) if the image size
                //does not match the slice size (e.g. during an image resizing operation
                //If you don't do this, your j,i could be outside the array bounds
                //In the framework, the image is 256x256 and the data set slices are 256x256
                //so I don't do anything - this also leaves you something to do for the assignment
                
                    if(j >= h && i >= w) {
                        datum = cthead[z][h][w];
                    }   
                    else if (i >= w && j < h) {
                        datum = cthead[z][j][w];                   
                    }
                    else if (j >= h && i < w) {
                        datum = cthead[z][h][i];
                    }
                    else {
                        datum = cthead[z][j][i];
                    }
                     
                //get values from slice 76 (change this in your assignment)
                //calculate the colour by performing a mapping from [min,max] -> 0 to 1 (float)
                //Java setColor uses float values from 0 to 1 rather than 0-255 bytes for colour
                col=(((float)datum-(float)min)/((float)(max-min)));
                image_writer.setColor(i, j, new Color(col, col, col, 1.0));		
                } // column loop
        } // row loop
    }
	
 /*
    This function shows how to carry out an operation on an image.
    It obtains the dimensions of the image, and then loops through
    the image carrying out the copying of a slice of data into the
    image.
    */
    public void TopDownSliceVT(WritableImage image, int skinOpacityNumber) {
        //Get image dimensions, and declare loop variables
        int w=(int) image.getWidth(), h=(int) image.getHeight();
        double skinOpacity = (double)skinOpacityNumber/100;
        PixelWriter image_writer = image.getPixelWriter();
	short datum;
        //Shows how to loop through each pixel and colour
        //Try to always use j for loops in y, and i for loops in x
        //as this makes the code more readable
        for (int j=0; j<h; j++) {
            for (int i=0; i<w; i++) {
                //at this point (i,j) is a single pixel in the image
                //here you would need to do something to (i,j) if the image size
                //does not match the slice size (e.g. during an image resizing operation
                //If you don't do this, your j,i could be outside the array bounds
                //In the framework, the image is 256x256 and the data set slices are 256x256
                //so I don't do anything - this also leaves you something to do for the assignment
                double currentRed = 0;
                double currentGreen = 0;
                double currentBlue = 0;

                for(int k=CT_z_axis-1;k>=0;k--) {
                    if(j >= h && i >= w) {
                        datum = cthead[k][h][w];
                    }   
                    else if (i >= w && j < h) {
                        datum = cthead[k][j][w];                   
                    }
                    else if (j >= h && i < w) {
                        datum = cthead[k][h][i];
                    }
                    else {
                        datum = cthead[k][j][i];
                    }
                    
                    if (datum < -300) {
                        
                    }
                    else if (datum >= -300 && datum <= 49){
                        currentRed = 1.0*skinOpacity+(1-skinOpacity)*currentRed;
                        if(currentRed < 0) {
                            currentRed = 0;
                        }
                        else if (currentRed > 1) {
                            currentRed = 1;
                        }
                        
                        currentGreen = 0.79*skinOpacity+(1-skinOpacity)*currentGreen;
                        if(currentGreen < 0) {
                            currentGreen = 0;
                        }
                        else if (currentGreen > 1) {
                            currentGreen = 1;
                        }
                        
                        currentBlue = 0.6*skinOpacity+(1-skinOpacity)*currentBlue;
                        if(currentBlue < 0) {
                            currentBlue = 0;
                        }
                        else if (currentBlue > 1) {
                            currentBlue = 1;
                        }
                    }
                    else if (datum >= 50 && datum <= 299) {
                        
                    }
                    else if (datum >= 300 && datum <= 4096) {
                        currentRed = 1.0*0.8+(1-0.8)*currentRed;
                        if(currentRed < 0) {
                            currentRed = 0;
                        }
                        else if (currentRed > 1) {
                            currentRed = 1;
                        }
                        
                        currentGreen = 1.0*0.8+(1-0.8)*currentGreen;
                        if(currentGreen < 0) {
                            currentGreen = 0;
                        }
                        else if (currentGreen > 1) {
                            currentGreen = 1;
                        }
                        
                        currentBlue = 1.0*0.8+(1-0.8)*currentBlue;
                        if(currentBlue < 0) {
                            currentBlue = 0;
                        }
                        else if (currentBlue > 1) {
                            currentBlue = 1;
                        }
                    }   
                }
                image_writer.setColor(i, j, new Color(currentRed, currentGreen, currentBlue, 1));
                currentBlue = 0;
                currentGreen = 0;
                currentRed = 0;
                //get values from slice 76 (change this in your assignment)
                //calculate the colour by performing a mapping from [min,max] -> 0 to 1 (float)
                //Java setColor uses float values from 0 to 1 rather than 0-255 bytes for colour
                //col=(((float)datum-(float)min)/((float)(max-min)));
                //image_writer.setColor(i, j, color);		
                } // column loop
        } // row loop
    }
    
    public void FrontBackSlice(WritableImage image, int sliceNumber) {
        //Get image dimensions, and declare loop variables
        int w=(int) image.getWidth(), h=(int) image.getHeight();
        PixelWriter image_writer = image.getPixelWriter();
        int y = sliceNumber;
        double col;
        short datum;
        //Shows how to loop through each pixel and colour
        //Try to always use j for loops in y, and i for loops in x
        //as this makes the code more readable
        for (int j=0; j<h; j++) {
            for (int i=0; i<w; i++) {
                //at this point (i,j) is a single pixel in the image
                //here you would need to do something to (i,j) if the image size
                //does not match the slice size (e.g. during an image resizing operation
                //If you don't do this, your j,i could be outside the array bounds
                //In the framework, the image is 256x256 and the data set slices are 256x256
                //so I don't do anything - this also leaves you something to do for the assignment
                if(j >= h && i >= w) {
                    datum=cthead[h][y][w];
                }
                else if (i >= w && j < h) {
                    datum=cthead[j][y][w];
                }
                else if (j >= h && i < w) {
                    datum=cthead[h][y][i];
                }
                else {
                    datum=cthead[j][y][i];
                }
                //get values from slice 76 (change this in your assignment)
                //calculate the colour by performing a mapping from [min,max] -> 0 to 1 (float)
                //Java setColor uses float values from 0 to 1 rather than 0-255 bytes for colour
                col=(((float)datum-(float)min)/((float)(max-min)));
                image_writer.setColor(i, j, new Color(col, col, col, 1.0));	
                } // column loop
        } // row loop
    }

    public void FrontBackSliceVT(WritableImage image, int skinOpacityNumber) {
        //Get image dimensions, and declare loop variables
        int w=(int) image.getWidth(), h=(int) image.getHeight();
        PixelWriter image_writer = image.getPixelWriter();
        double skinOpacity = (double)skinOpacityNumber/100;
	short datum;
        //Shows how to loop through each pixel and colour
        //Try to always use j for loops in y, and i for loops in x
        //as this makes the code more readable
        for (int j=0; j<h; j++) {
            for (int i=0; i<w; i++) {
                //at this point (i,j) is a single pixel in the image
                //here you would need to do something to (i,j) if the image size
                //does not match the slice size (e.g. during an image resizing operation
                //If you don't do this, your j,i could be outside the array bounds
                //In the framework, the image is 256x256 and the data set slices are 256x256
                //so I don't do anything - this also leaves you something to do for the assignment
                double currentRed = 0;
                double currentGreen = 0;
                double currentBlue = 0;

                for(int k=CT_y_axis-1;k>=0;k--) {
                    if(j >= h && i >= w) {
                        datum=cthead[h][k][w];
                    }
                    else if (i >= w && j < h) {
                        datum=cthead[j][k][w];
                    }
                    else if (j >= h && i < w) {
                        datum=cthead[h][k][i];
                    }
                    else {
                        datum=cthead[j][k][i];
                    }
                    
                    if (datum < -300) {
                        
                    }
                    else if (datum >= -300 && datum <= 49){
                        currentRed = 1.0*skinOpacity+(1-skinOpacity)*currentRed;
                        if(currentRed < 0) {
                            currentRed = 0;
                        }
                        else if (currentRed > 1) {
                            currentRed = 1;
                        }

                        currentGreen = 0.79*skinOpacity+(1-skinOpacity)*currentGreen;
                        if(currentGreen < 0) {
                            currentGreen = 0;
                        }
                        else if (currentGreen > 1) {
                            currentGreen = 1;
                        }

                        currentBlue = 0.6*skinOpacity+(1-skinOpacity)*currentBlue;
                        if(currentBlue < 0) {
                            currentBlue = 0;
                        }
                        else if (currentBlue > 1) {
                            currentBlue = 1;
                        }
                    }
                    else if (datum >= 50 && datum <= 299) {
                        
                    }
                    else if (datum >= 300 && datum <= 4096) {
                        currentRed = 1.0*0.8+(1-0.8)*currentRed;
                        if(currentRed < 0) {
                            currentRed = 0;
                        }
                        else if (currentRed > 1) {
                            currentRed = 1;
                        }
                        
                        currentGreen = 1.0*0.8+(1-0.8)*currentGreen;
                        if(currentGreen < 0) {
                            currentGreen = 0;
                        }
                        else if (currentGreen > 1) {
                            currentGreen = 1;
                        }
                        
                        currentBlue = 1.0*0.8+(1-0.8)*currentBlue;
                        if(currentBlue < 0) {
                            currentBlue = 0;
                        }
                        else if (currentBlue > 1) {
                            currentBlue = 1;
                        }
                    }   
                }
                image_writer.setColor(i, j, new Color(currentRed, currentGreen, currentBlue, 1));
                currentBlue = 0;
                currentGreen = 0;
                currentRed = 0;
                //get values from slice 76 (change this in your assignment)
                //calculate the colour by performing a mapping from [min,max] -> 0 to 1 (float)
                //Java setColor uses float values from 0 to 1 rather than 0-255 bytes for colour
                //col=(((float)datum-(float)min)/((float)(max-min)));
                //image_writer.setColor(i, j, color);		
                } // column loop
        } // row loop
    }
    
    public void SideSlice(WritableImage image, int sliceNumber) {
        //Get image dimensions, and declare loop variables
        int w=(int) image.getWidth(), h=(int) image.getHeight();
        PixelWriter image_writer = image.getPixelWriter();
        int x = sliceNumber;
        double col;
        short datum;
        //Shows how to loop through each pixel and colour
        //Try to always use j for loops in y, and i for loops in x
        //as this makes the code more readable
        for (int j=0; j<h; j++) {
            for (int i=0; i<w; i++) {
                //at this point (i,j) is a single pixel in the image
                //here you would need to do something to (i,j) if the image size
                //does not match the slice size (e.g. during an image resizing operation
                //If you don't do this, your j,i could be outside the array bounds
                //In the framework, the image is 256x256 and the data set slices are 256x256
                //so I don't do anything - this also leaves you something to do for the assignment
                if(j >= h && i >= w) {
                    datum=cthead[h][w][x];
                }
                else if (i >= w && j < h) {
                    datum=cthead[j][w][x];
                }
                else if (j >= h && i < w) {
                    datum=cthead[h][i][x];
                }
                else {
                    datum=cthead[j][i][x];
                }
                //get values from slice 76 (change this in your assignment)
                //calculate the colour by performing a mapping from [min,max] -> 0 to 1 (float)
                //Java setColor uses float values from 0 to 1 rather than 0-255 bytes for colour
                col=(((float)datum-(float)min)/((float)(max-min)));
                image_writer.setColor(i, j, new Color(col, col, col, 1.0));		
                } // column loop
        } // row loop
    }
    
        public void SideSliceVT(WritableImage image, int skinOpacityNumber) {
        //Get image dimensions, and declare loop variables
        int w=(int) image.getWidth(), h=(int) image.getHeight();
        PixelWriter image_writer = image.getPixelWriter();
        double skinOpacity = (double)skinOpacityNumber/100;
	short datum;
        //Shows how to loop through each pixel and colour
        //Try to always use j for loops in y, and i for loops in x
        //as this makes the code more readable
        for (int j=0; j<h; j++) {
            for (int i=0; i<w; i++) {
                //at this point (i,j) is a single pixel in the image
                //here you would need to do something to (i,j) if the image size
                //does not match the slice size (e.g. during an image resizing operation
                //If you don't do this, your j,i could be outside the array bounds
                //In the framework, the image is 256x256 and the data set slices are 256x256
                //so I don't do anything - this also leaves you something to do for the assignment
                double currentRed = 0;
                double currentGreen = 0;
                double currentBlue = 0;

                for(int k=CT_x_axis-1;k>=0;k--) {
                    if(j >= h && i >= w) {
                        datum=cthead[h][w][k];
                    }
                    else if (i >= w && j < h) {
                        datum=cthead[j][w][k];
                    }
                    else if (j >= h && i < w) {
                        datum=cthead[h][i][k];
                    }
                    else {
                        datum=cthead[j][i][k];
                    }
                    
                    if (datum < -300) {
                        
                    }
                    else if (datum >= -300 && datum <= 49){
                        currentRed = 1.0*skinOpacity+(1-skinOpacity)*currentRed;
                        if(currentRed < 0) {
                            currentRed = 0;
                        }
                        else if (currentRed > 1) {
                            currentRed = 1;
                        }

                        currentGreen = 0.79*skinOpacity+(1-skinOpacity)*currentGreen;
                        if(currentGreen < 0) {
                            currentGreen = 0;
                        }
                        else if (currentGreen > 1) {
                            currentGreen = 1;
                        }

                        currentBlue = 0.6*skinOpacity+(1-skinOpacity)*currentBlue;
                        if(currentBlue < 0) {
                            currentBlue = 0;
                        }
                        else if (currentBlue > 1) {
                            currentBlue = 1;
                        }
                    }
                    else if (datum >= 50 && datum <= 299) {
                        
                    }
                    else if (datum >= 300 && datum <= 4096) {
                        currentRed = 1.0*0.8+(1-0.8)*currentRed;
                        if(currentRed < 0) {
                            currentRed = 0;
                        }
                        else if (currentRed > 1) {
                            currentRed = 1;
                        }
                        
                        currentGreen = 1.0*0.8+(1-0.8)*currentGreen;
                        if(currentGreen < 0) {
                            currentGreen = 0;
                        }
                        else if (currentGreen > 1) {
                            currentGreen = 1;
                        }
                        
                        currentBlue = 1.0*0.8+(1-0.8)*currentBlue;
                        if(currentBlue < 0) {
                            currentBlue = 0;
                        }
                        else if (currentBlue > 1) {
                            currentBlue = 1;
                        }
                    }   
                }
                image_writer.setColor(i, j, new Color(currentRed, currentGreen, currentBlue, 1));
                currentBlue = 0;
                currentGreen = 0;
                currentRed = 0;
                //get values from slice 76 (change this in your assignment)
                //calculate the colour by performing a mapping from [min,max] -> 0 to 1 (float)
                //Java setColor uses float values from 0 to 1 rather than 0-255 bytes for colour
                //col=(((float)datum-(float)min)/((float)(max-min)));
                //image_writer.setColor(i, j, color);		
                } // column loop
        } // row loop
    }

    public Color transferFunction(short cthread) {
        if (cthread < -300) {
            return new Color(0, 0, 0, 1.0);
        }
        else if (cthread >= -300 && cthread <= 49){
            return new Color(1.0, 0.79, 0.6, 0.88);
        }
        else if (cthread >= 50 && cthread <= 299) {
            return new Color(0, 0, 0, 1.0);
        }
        else {
            return new Color(1.0, 1.0, 1.0, 0.2);
        }
    }
    
    public static void main(String[] args) {
        launch();
    }

}