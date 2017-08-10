package weibo.video;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;

public class Remove_video_watermark {
    static final int CV_CAP_PROP_FPS = 5;
    static final int CV_FOURCC_XVID = 1482049860;
    static final int CV_FOURCC_FLV1 = 827739206;
    static String video_path = "abc";

    public static void main(String[] args) {
	System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

	Mat inpaintMask = new Mat();
	Mat inpainted = new Mat();
	Mat template = Imgcodecs.imread("e:/t/1.png");
	Imgproc.cvtColor(template, inpaintMask, Imgproc.COLOR_BGR2GRAY);
	Mat frame = new Mat();

	VideoCapture cap = new VideoCapture("e:/t/ab.mp4");
	if (!cap.isOpened()) // check if we succeeded
	    return;
	String fileName = video_path + "VideoTest12.avi";
	Size size = new Size(cap.get(Videoio.CV_CAP_PROP_FRAME_WIDTH), cap.get(Videoio.CV_CAP_PROP_FRAME_HEIGHT));
	double fps = cap.get(CV_CAP_PROP_FPS);
	// VideoWriter vw = new VideoWriter(fileName, VideoWriter.fourcc('X',
	// 'V', 'I', 'D'), fps, size, true);
	VideoWriter vw1 = new VideoWriter(fileName, CV_FOURCC_FLV1, fps, size, true);
	int ord = 0;
	while (cap.isOpened()) {
	    if (!cap.read(frame))// get a new frame from camera or video
	    {
		System.out.println("finlish!");
		break;
	    }
	    Photo.inpaint(frame, inpaintMask, inpainted, 8, Photo.INPAINT_TELEA);
	    vw1.write(inpainted);
	    ord++;
	    System.out.println(ord);
	}
    }
}
