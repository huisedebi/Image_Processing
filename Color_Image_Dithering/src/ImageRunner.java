// ImagaReaderRunner.java
import java.awt.Image;
import java.io.IOException;
import java.awt.image.*;
import javax.imageio.*;
import java.io.*;

public class ImageRunner {

	public static void main(String[] args) throws IOException {
		ImageIOI imageioer = new ImageIOI();
		//¶ÁÈ¡Í¼Ïñ
		Image image = imageioer.myRead("C:/Users/lenovo/Desktop/image/timbmp.bmp");
		image = Image_Process.Process(image);
		image = Image_Process.myWrite(image, "C:/Users/lenovo/Desktop/image/new031");
		
	}
}