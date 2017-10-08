import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import java.awt.Image;
import java.awt.image.*;

public class Image_Process {
	private static final int zero = 0;
	private static final int sixteen = 16;
	private static final int eight = 8;
	private static final double R = 0.299;
	private static final double G = 0.587;
	private static final double B = 0.114;
	private static final int RR = 0x00ff0000;
	private static final int GG = 0x0000ff00;
	private static final int BB = 0x000000ff;
	
	public static Image Process(Image image) throws IOException {
		int width = image.getWidth(null);
		int height = image.getHeight(null);
		int index = 0;
		int indexrgb = 0;
		int pixels[] = new int[width * height];
		int pixelrgb[] = new int[width * height * 3];
		int gray_ = 0;
		
		BufferedImage bufim = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		//三个参数分别代表图像的长度、宽度、以及类型
		//  BufferedImage是Image的一个子类，BufferedImage生成的图片在内存里有一个图像缓冲区，
		//利用这个缓冲区我们可以很方便的操作这个图片，
		//通常用来做图片修改操作如大小变换、图片变灰、设置图片透明或不透明等。
		bufim.getGraphics().drawImage(image, zero, zero, width, height, null);
		//获取一个图像，0,0是图像起始的位置,sourceImage.getWidth(null),是获取图片的宽度，，null则是绘画图片以默认类型画出

		//获取图片的所有像素点
		//将像素的RGB值分离出来
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				index = j * width + i;
				indexrgb = index * 3;
				pixels[index] = bufim.getRGB(i, j);
				//在获取图像的像素点的时候就将图像转化为灰度图并且存下图像的RGB值, pixelrgb的每三个数存储的是一个像素点的rgb值
				gray_ = (int)(((pixels[index] & RR) >> sixteen) * R + 
						((pixels[index] & GG) >> eight) * G + (pixels[index] & BB) * B);
				pixels[index] = gray_ << sixteen | gray_ << eight | gray_;
				pixelrgb[indexrgb] = (pixels[index] & RR) >> 16;
				pixelrgb[indexrgb + 1] = (pixels[index] & GG) >> 8;
				pixelrgb[indexrgb + 2] = pixels[index] & BB;
			}
		}
		bufim.setRGB(zero, zero, width, height, pixels, zero, width);
		
		double gray = 0;
		int rederror = 0;
		int greenerror = 0;
		int blackerror = 0;
		int offsetindex;
		int red = 0;
		int green = 0;
		int black = 0;
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				index = j * width + i;
				indexrgb = index * 3;
				gray = (0.299 * pixelrgb[indexrgb] + 0.587 * pixelrgb[indexrgb + 1] + 0.114 * pixelrgb[indexrgb + 2]);
				if (gray < 127) {
					//在比较的时候如果灰度值小于128的话，那么
					rederror = pixelrgb[indexrgb];
					greenerror = pixelrgb[indexrgb + 1];
					blackerror = pixelrgb[indexrgb + 2];
				} else {
					rederror = pixelrgb[indexrgb] - 255;
					greenerror = pixelrgb[indexrgb + 1] - 255;
					blackerror = pixelrgb[indexrgb + 2] -255;
				}
				if (i + 1 < width) {
					//右边分散7/16的误差
					//计算的时候用移位来代替除法相对而言会简单一些
					offsetindex = index + 1;
					indexrgb = offsetindex * 3;
					red = (pixelrgb[indexrgb] + ((rederror * 7) >> 4));
					green = (pixelrgb[indexrgb + 1] + ((greenerror * 7 ) >> 4));
					black = (pixelrgb[indexrgb + 2] + ((blackerror * 7 ) >> 4));
					pixelrgb[indexrgb] = red;
					pixelrgb[indexrgb + 1] = green;
					pixelrgb[indexrgb + 2] = black;
				}
				
				if (j + 1 < height) {
					if (i > 0) {
						//left and down
						//新的像素下标
						offsetindex = index + width - 1;
						//新的rgb下标
						indexrgb = offsetindex * 3;
						red = (pixelrgb[indexrgb] + ((rederror * 3) >> 4));
						green = (pixelrgb[indexrgb + 1] + ((greenerror * 3) >> 4));
						black = (pixelrgb[indexrgb + 2] + ((blackerror * 3) >> 4));
						pixelrgb[indexrgb] = red;
						pixelrgb[indexrgb + 1] = green;
						pixelrgb[indexrgb + 2] = black;
						
					}
					
					//down
					offsetindex = index + width;
					//新的rgb下标
					indexrgb = offsetindex * 3;
					red = (pixelrgb[indexrgb] + ((rederror * 5) >> 4));
					green = (pixelrgb[indexrgb + 1] + ((greenerror * 5) >> 4));
					black = (pixelrgb[indexrgb + 2] + ((blackerror * 5) >> 4));
					pixelrgb[indexrgb] = red;
					pixelrgb[indexrgb + 1] = green;
					pixelrgb[indexrgb + 2] = black;
					
					if (i + 1 < width) {
						//right and down
						offsetindex = index + width + 1;
						//新的rgb下标
						indexrgb = offsetindex * 3;
						red = (pixelrgb[indexrgb] + ((rederror * 1) >> 4));
						green = (pixelrgb[indexrgb + 1] + ((greenerror * 1) >> 4));
						black = (pixelrgb[indexrgb + 2] + ((blackerror * 1) >> 4));
						pixelrgb[indexrgb] = red;
						pixelrgb[indexrgb + 1] = green;
						pixelrgb[indexrgb + 2] = black;
					}
					
				}
			}
		}
		
		for (int i = 0; i < width * height; i++) {
			indexrgb = i * 3;
			pixels[i] = (pixelrgb[indexrgb] << 16) | (pixelrgb[indexrgb + 1] << 8) | (pixelrgb[indexrgb + 2]);
		}
		
		bufim.setRGB(zero, zero, image.getWidth(null), 
			image.getHeight(null), pixels, 0, image.getWidth(null));
		return bufim;
	}
	
	public static Image myWrite(Image image, String filePath) throws java.io.IOException {
		try {
			File imagefile = new File(filePath + ".bmp");
			//创建一个bmp格式的图像文件
			BufferedImage bufim = new BufferedImage(image.getWidth(null),
				image.getHeight(null), BufferedImage.TYPE_INT_RGB);
			bufim.getGraphics().drawImage(image, zero, zero, image.getWidth(null),
				image.getHeight(null), null);
			ImageIO.write(bufim, "bmp", imagefile);
		}
		catch(Exception e) {
			System.out.println(e);
		} 
		return null;
	}
}
