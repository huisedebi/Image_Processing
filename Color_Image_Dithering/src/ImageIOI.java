import java.awt.*;
import java.awt.image.*;
import javax.imageio.*;
import java.io.*;

public class ImageIOI {
	private int biwidth;
	private int biheight;
	private int bibitcount;
	private int bisizeImage;
	private static final int FF = 0xff;
	private static final int fourty = 40;
	private static final int zero = 0;
	private static final int one = 1;
	private static final int two = 2;
	private static final int three = 3;
	private static final int four = 4;
	private static final int five = 5;
	private static final int six = 6;
	private static final int seven = 7;
	private static final int eight = 8;
	private static final int nine = 9;
	private static final int ten = 10;
	private static final int eleven = 11;
	private static final int sixteen = 16;
	private static final int fourteen = 14;
	private static final int fifteen = 15;
	private static final int twenty = 20;
	private static final int tone = 21;
	private static final int ttwo = 22;
	private static final int tthree = 23;
	private static final int tfour =24;
	private static final int tfivef = 255;
	private int[] data;

	public Image myRead(String filePath) throws IOException {
		try {
			//首先定义一个输入文件流
			FileInputStream fs = new FileInputStream(filePath);
			//位图头的大小为14
			int bflen = fourteen;
			byte bf[] = new byte[bflen];
			//从0开始将长度为14的位图头读入到bf里面
			fs.read(bf, zero, bflen);
			
			//文件输入流以只读方式打开了一个文件流，而且这个文件流只能依次往后读，
			//所以这里是从0开始读，而不是从14开始读
			int bilen = fourty;
			byte bi[] = new byte[bilen];
			fs.read(bi, zero, bilen);

			bisizeImage = (((int)bi[tthree] & FF) << tfour) |
						(((int)bi[ttwo] & FF) << sixteen) |
						(((int)bi[tone] & FF) << eight) |
						(int)bi[twenty] & FF;
			
			biwidth = (((int)bi[seven] & FF) << tfour) |
						(((int)bi[six] & FF) << sixteen) |
						(((int)bi[five] & FF) << eight) |
						(int)bi[four] & FF;

			biheight =  (((int)bi[eleven] & FF) << tfour) |
						(((int)bi[ten] & FF) << sixteen) |
						(((int)bi[nine] & FF) << eight) |
						(int)bi[eight] & FF;
			//原理与宽度的原理是一样的；
			
			//打印一些基本信息方便了解图像信息
			System.out.printf("宽度（字节）：%d\n", biwidth);
			System.out.printf("高度（字节）：%d\n", biheight);
			System.out.printf("位图全部像素占用的字节数：%d\n", bisizeImage);
			
			//每个像素的位数 1-黑白图，4-16色，8-256色，24-真彩色
			bibitcount = (((int)bi[fifteen] & FF) << eight) | (int)bi[fourteen] & FF;

			if (bibitcount == tfour) {
				//像素的位数为24代表现目前是真彩图
				//彩色图像由3个色彩通道组成
				int numodemptybyte = (bisizeImage / biheight) - biwidth * three;
				if (numodemptybyte == four)
					numodemptybyte = zero;
				data = new int[biheight * biwidth];
				byte dataRGB[] = new byte[bisizeImage];
				//dataRGB 存储的是色彩信息
				fs.read(dataRGB, 0, bisizeImage);
				int index = zero;
				//拷贝位图上RGB数据的信息
				for (int j = zero; j < biheight; j++) {
					for (int i = zero; i < biwidth; i++) {
						data[biwidth * (biheight - j - one) + i] = 
						(tfivef & FF) << tfour | (((int)dataRGB[index + two] & FF) << sixteen)
						| (((int)dataRGB[index + one] & FF) << eight) |
						(int)dataRGB[index] & FF;
						index += three;
						//加3是因为彩色图像由三个色彩通道组成，即R G B
					}
					index += numodemptybyte;
					//有的位图的长度不是由4的倍数组成的所以需要加numofemptybyte的长度；
				}
			} else {
				System.out.println("it is not a 24_bit BitMap!!!");
				fs.close();  //关闭流文件
				return null;
			}
			fs.close();  //关闭流文件
			
			//Toolkit.getDefaultToolkit().getImage(...) 方法可接受 String 
			//或者是 URL 参数，用以指定图像文件的路径。
			return Toolkit.getDefaultToolkit().createImage(
				new MemoryImageSource(biwidth, biheight, data, zero, biwidth));
			//MemoryImageSource	该类是ImageProducer接口的一个实现，它使用一个数组为图像生成像素值。
			//该类是ImageProducer接口的一个实现，它使用一个数组为图像生成像素值。
			//MemoryImageSource还能够管理随时间变化的存储器映像，以允许动画或自定义渲染
		} catch(Exception e) {
			System.out.println(e);
		}
		return null;
	}
} 