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
			//���ȶ���һ�������ļ���
			FileInputStream fs = new FileInputStream(filePath);
			//λͼͷ�Ĵ�СΪ14
			int bflen = fourteen;
			byte bf[] = new byte[bflen];
			//��0��ʼ������Ϊ14��λͼͷ���뵽bf����
			fs.read(bf, zero, bflen);
			
			//�ļ���������ֻ����ʽ����һ���ļ�������������ļ���ֻ�������������
			//���������Ǵ�0��ʼ���������Ǵ�14��ʼ��
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
			//ԭ�����ȵ�ԭ����һ���ģ�
			
			//��ӡһЩ������Ϣ�����˽�ͼ����Ϣ
			System.out.printf("��ȣ��ֽڣ���%d\n", biwidth);
			System.out.printf("�߶ȣ��ֽڣ���%d\n", biheight);
			System.out.printf("λͼȫ������ռ�õ��ֽ�����%d\n", bisizeImage);
			
			//ÿ�����ص�λ�� 1-�ڰ�ͼ��4-16ɫ��8-256ɫ��24-���ɫ
			bibitcount = (((int)bi[fifteen] & FF) << eight) | (int)bi[fourteen] & FF;

			if (bibitcount == tfour) {
				//���ص�λ��Ϊ24������Ŀǰ�����ͼ
				//��ɫͼ����3��ɫ��ͨ�����
				int numodemptybyte = (bisizeImage / biheight) - biwidth * three;
				if (numodemptybyte == four)
					numodemptybyte = zero;
				data = new int[biheight * biwidth];
				byte dataRGB[] = new byte[bisizeImage];
				//dataRGB �洢����ɫ����Ϣ
				fs.read(dataRGB, 0, bisizeImage);
				int index = zero;
				//����λͼ��RGB���ݵ���Ϣ
				for (int j = zero; j < biheight; j++) {
					for (int i = zero; i < biwidth; i++) {
						data[biwidth * (biheight - j - one) + i] = 
						(tfivef & FF) << tfour | (((int)dataRGB[index + two] & FF) << sixteen)
						| (((int)dataRGB[index + one] & FF) << eight) |
						(int)dataRGB[index] & FF;
						index += three;
						//��3����Ϊ��ɫͼ��������ɫ��ͨ����ɣ���R G B
					}
					index += numodemptybyte;
					//�е�λͼ�ĳ��Ȳ�����4�ı�����ɵ�������Ҫ��numofemptybyte�ĳ��ȣ�
				}
			} else {
				System.out.println("it is not a 24_bit BitMap!!!");
				fs.close();  //�ر����ļ�
				return null;
			}
			fs.close();  //�ر����ļ�
			
			//Toolkit.getDefaultToolkit().getImage(...) �����ɽ��� String 
			//������ URL ����������ָ��ͼ���ļ���·����
			return Toolkit.getDefaultToolkit().createImage(
				new MemoryImageSource(biwidth, biheight, data, zero, biwidth));
			//MemoryImageSource	������ImageProducer�ӿڵ�һ��ʵ�֣���ʹ��һ������Ϊͼ����������ֵ��
			//������ImageProducer�ӿڵ�һ��ʵ�֣���ʹ��һ������Ϊͼ����������ֵ��
			//MemoryImageSource���ܹ�������ʱ��仯�Ĵ洢��ӳ�������������Զ�����Ⱦ
		} catch(Exception e) {
			System.out.println(e);
		}
		return null;
	}
} 