// ImagaReaderRunner.java
import java.awt.Image;
import java.io.IOException;
import java.awt.image.*;
import javax.imageio.*;
import java.io.*;

public class IamgeRunner {
	private static final int zero = 0;
	public static void main(String[] args) throws IOException {
		ImageIOI imageioer = new ImageIOI();
		//读取图像
		Image image = imageioer.myRead("C:/Users/lenovo/Desktop/image/timg_bmp.bmp");
		image = Image_Process.Process(image);
		image = Image_Process.myWrite(image, "C:/Users/lenovo/Desktop/image/new011");
		
	}
}