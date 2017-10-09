import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import java.awt.Image;
import java.awt.image.*;

public class Image_Process {
	private static final int zero = 0;
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
				//gray_ = (int)(((pixels[index] & RR) >> sixteen) * R + 
					//	((pixels[index] & GG) >> eight) * G + (pixels[index] & BB) * B);
				//pixels[index] = gray_ << sixteen | gray_ << eight | gray_;
				pixelrgb[indexrgb] = (pixels[index] & RR) >> 16;
				pixelrgb[indexrgb + 1] = (pixels[index] & GG) >> 8;
				pixelrgb[indexrgb + 2] = pixels[index] & BB;
			}
		}
		bufim.setRGB(zero, zero, width, height, pixels, zero, width);
		
		int rederror = 0;
		int greenerror = 0;
		int blueerror = 0;
		int offsetindex;
		int red = 0;
		int green = 0;
		int blue = 0;
		int ss = 8;
		int d = 1;
		//d = 1的时候误差从左到右传递，等于0时从右到左传递
		int val = 0;
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				if (d == 1) {
					//误差从左到右传递
					index = j * width + i;
					indexrgb = index * 3;
					
					val = ss * (pixelrgb[indexrgb] / ss);
					rederror = pixelrgb[indexrgb] - val;
					pixelrgb[indexrgb] = val;
					
					val = ss * (pixelrgb[indexrgb + 1] / ss);
					greenerror = pixelrgb[indexrgb + 1] - val;
					pixelrgb[indexrgb + 1] = val;
					
					val = ss * (pixelrgb[indexrgb + 2] / ss);
					blueerror = pixelrgb[indexrgb + 2] - val;
					pixelrgb[indexrgb + 2] = val;
					
					
					if (i + 1 < width) {
						//右边分散3/8的误差
						//计算的时候用移位来代替除法相对而言会简单一些
						offsetindex = index + 1;
						indexrgb = offsetindex * 3;
						red = (int) (pixelrgb[indexrgb] + rederror * 0.375);
						green = (int) (pixelrgb[indexrgb + 1] + greenerror * 0.375);
						blue = (int)(pixelrgb[indexrgb + 2] + blueerror * 0.375);
						pixelrgb[indexrgb] = red;
						pixelrgb[indexrgb + 1] = green;
						pixelrgb[indexrgb + 2] = blue;
					}
					
					if (j + 1 < height) {
						
						//down
						offsetindex = index + width;
						//新的rgb下标
						indexrgb = offsetindex * 3;
						red = (int) (pixelrgb[indexrgb] + rederror * 0.375);
						green = (int) (pixelrgb[indexrgb + 1] + greenerror * 0.375);
						blue = (int)(pixelrgb[indexrgb + 2] + blueerror * 0.375);
						pixelrgb[indexrgb] = red;
						pixelrgb[indexrgb + 1] = green;
						pixelrgb[indexrgb + 2] = blue;
						
						if (i + 1 < width) {
							//right and down
							offsetindex = index + width + 1;
							//新的rgb下标
							indexrgb = offsetindex * 3;
							red = (int) (pixelrgb[indexrgb] + rederror * 0.25);
							green = (int) (pixelrgb[indexrgb + 1] + greenerror * 0.25);
							blue = (int)(pixelrgb[indexrgb + 2] + blueerror * 0.25);
							pixelrgb[indexrgb] = red;
							pixelrgb[indexrgb + 1] = green;
							pixelrgb[indexrgb + 2] = blue;
						}
						
					}
					d = 0;
				}
				
				if (d == 0) {
					index = j * width + width - i - 1;
					indexrgb = index * 3;
					
					val = ss * (pixelrgb[indexrgb] / ss);
					rederror = pixelrgb[indexrgb] - val;
					pixelrgb[indexrgb] = val;
					
					val = ss * (pixelrgb[indexrgb + 1] / ss);
					greenerror = pixelrgb[indexrgb + 1] - val;
					pixelrgb[indexrgb + 1] = val;
					
					val = ss * (pixelrgb[indexrgb + 2] / ss);
					blueerror = pixelrgb[indexrgb + 2] - val;
					pixelrgb[indexrgb + 2] = val;
					
					if ((width - i) >= 0) {
						//对左侧传递误差
						indexrgb = (j * width + width - i - 1)* 3;
						red = (int) (pixelrgb[indexrgb] + rederror * 0.375);
						green = (int) (pixelrgb[indexrgb + 1] + greenerror * 0.375);
						blue = (int)(pixelrgb[indexrgb + 2] + blueerror * 0.375);
						pixelrgb[indexrgb] = red;
						pixelrgb[indexrgb + 1] = green;
						pixelrgb[indexrgb + 2] = blue;
					}
					if (j + 1 < height) {
						//对下侧传递误差
						indexrgb = ((j + 1) * width + i) * 3;
						red = (int) (pixelrgb[indexrgb] + rederror * 0.375);
						green = (int) (pixelrgb[indexrgb + 1] + greenerror * 0.375);
						blue = (int)(pixelrgb[indexrgb + 2] + blueerror * 0.375);
						pixelrgb[indexrgb] = red;
						pixelrgb[indexrgb + 1] = green;
						pixelrgb[indexrgb + 2] = blue;
						
						if (width - i >= 0) {
							//对左下侧传递误差
							indexrgb = ((j + 1) * width + width - i - 1);
							red = (int) (pixelrgb[indexrgb] + rederror * 0.25);
							green = (int) (pixelrgb[indexrgb + 1] + greenerror * 0.25);
							blue = (int)(pixelrgb[indexrgb + 2] + blueerror * 0.25);
							pixelrgb[indexrgb] = red;
							pixelrgb[indexrgb + 1] = green;
							pixelrgb[indexrgb + 2] = blue;
						}
					}
					d = 1;
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
