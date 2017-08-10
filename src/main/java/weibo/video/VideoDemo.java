package weibo.video;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

 

public class VideoDemo {
    // http://blog.csdn.net/dyx810601/article/details/51426339
    //http://blog.csdn.net/eguid_1/article/details/51659578
    public static void main(String[] args) throws InterruptedException {
	System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	// 转换器，用于Frame/Mat/IplImage相互转换  
        OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();  
 	Mat imgMat = Imgcodecs.imread("e:/t/1.png");
	Imgproc.cvtColor(imgMat, imgMat, Imgproc.COLOR_RGB2GRAY);

	// 打开摄像头或者视频文件
	VideoCapture capture = new VideoCapture("e:/t/ab.mp4");
	// capture.open(0);
	if (!capture.isOpened()) {
	    System.out.println("could not load video data...");
	    return;
	}
	int frame_width = (int) capture.get(3);
	int frame_height = (int) capture.get(4);

	// 总帧数
	double frameCount = capture.get(Videoio.CV_CAP_PROP_FRAME_COUNT);
	System.out.println(frameCount);

	// 帧率
	double fps = capture.get(Videoio.CV_CAP_PROP_FPS);
	System.out.println(fps);

	// 时间长度
	double len = frameCount / fps;
	System.out.println("len:" + len);

	ImageGUI gui = new ImageGUI();
	gui.createWin("OpenCV + Java视频读与播放演示", new Dimension(frame_width, frame_height));
	Mat frame = new Mat();
	int delay = (int) (1000 / fps);
	// for (int i = 0; i < d_s.intValue(); i++) {
	// // 设置视频的位置(单位:毫秒)
	// //capture.set(Videoio.CV_CAP_PROP_POS_MSEC, i * 1000);
	// }
	Mat inpainted = new Mat();
	while (capture.read(frame)) {
	    Photo.inpaint(frame, imgMat, inpainted, 8, Photo.INPAINT_TELEA);

	    // Core.flip(frame, frame, 1);// Win上摄像头 ,翻转左右画面
	    gui.imshow(conver2Image(frame));
	    gui.repaint();
	    Thread.sleep(delay);
	}
	// while (true) {
	// boolean have = capture.read(frame);
	// Core.flip(frame, frame, 1);// Win上摄像头
	// if (!have)
	// break;
	// if (!frame.empty()) {
	// gui.imshow(conver2Image(frame));
	// gui.repaint();
	// }
	//
	// }
	capture.release();
    }
 // 加文字水印  
    public void mark(BufferedImage bufImg, Image img, String text, Font font, Color color, int x, int y) {  
        Graphics2D g = bufImg.createGraphics();  
        g.drawImage(img, 0, 0, bufImg.getWidth(), bufImg.getHeight(), null);  
        g.setColor(color);  
        g.setFont(font);  
        g.drawString(text, x, y);  
        g.dispose();  
    }  
    // 加图片水印  
    public void mark(BufferedImage bufImg, Image img, Image markImg, int width, int height, int x, int y) {  
        Graphics2D g = bufImg.createGraphics();  
        g.drawImage(img, 0, 0, bufImg.getWidth(), bufImg.getHeight(), null);  
        g.drawImage(markImg, x, y, width, height, null);  
        g.dispose();  
    }  
    
    public static BufferedImage conver2Image(Mat mat) {
	int width = mat.cols();
	int height = mat.rows();
	int dims = mat.channels();
	int[] pixels = new int[width * height];
	byte[] rgbdata = new byte[width * height * dims];
	mat.get(0, 0, rgbdata);
	BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	int index = 0;
	int r = 0, g = 0, b = 0;
	for (int row = 0; row < height; row++) {
	    for (int col = 0; col < width; col++) {
		if (dims == 3) {
		    index = row * width * dims + col * dims;
		    b = rgbdata[index] & 0xff;
		    g = rgbdata[index + 1] & 0xff;
		    r = rgbdata[index + 2] & 0xff;
		    pixels[row * width + col] = ((255 & 0xff) << 24) | ((r & 0xff) << 16) | ((g & 0xff) << 8) | b & 0xff;
		}
		if (dims == 1) {
		    index = row * width + col;
		    b = rgbdata[index] & 0xff;
		    pixels[row * width + col] = ((255 & 0xff) << 24) | ((b & 0xff) << 16) | ((b & 0xff) << 8) | b & 0xff;
		}
	    }
	}
	setRGB(image, 0, 0, width, height, pixels);
	return image;
    }

    /**
     * A convenience method for setting ARGB pixels in an image. This tries to
     * avoid the performance penalty of BufferedImage.setRGB unmanaging the
     * image.
     */
    public static void setRGB(BufferedImage image, int x, int y, int width, int height, int[] pixels) {
	int type = image.getType();
	if (type == BufferedImage.TYPE_INT_ARGB || type == BufferedImage.TYPE_INT_RGB)
	    image.getRaster().setDataElements(x, y, width, height, pixels);
	else
	    image.setRGB(x, y, width, height, pixels, 0, width);
    }

}