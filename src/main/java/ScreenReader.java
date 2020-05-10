import org.opencv.core.Point;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoWriter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

public class ScreenReader {

    private int FRAMES_TO_RECORD = 10;

    CascadeClassifier faceDetector;
    Mat mat;
    MatOfRect faceDetections;
    Rectangle rectangle;
    Robot robot;
    VideoWriter videoWriter;

    public ScreenReader() throws AWTException {
        faceDetector = new CascadeClassifier();
        mat = new Mat();
        faceDetections = new MatOfRect();
        rectangle = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        robot = new Robot();
        videoWriter = new VideoWriter("recordings/test2.avi", VideoWriter.fourcc('M', 'J', 'P', 'G'),
                1, new Size(rectangle.width, rectangle.height), true);

        faceDetector.load("haars/haarcascade_frontalface_alt.xml");

        init();
    }

    private void init() {
        int i = 0;
        while (true && i < FRAMES_TO_RECORD) {
            BufferedImage bufferedImage = robot.createScreenCapture(rectangle);
            Mat mat = bufferedImageToMat(bufferedImage);
            evaluateImage(mat);
            i++;
        }
        videoWriter.release();
    }

    private void evaluateImage(Mat mat) {
        faceDetector.detectMultiScale(mat, faceDetections);
        if (faceDetections.elemSize() > 0) {
            Arrays.stream(faceDetections.toArray()).forEach(rect -> addRectangle(rect, mat, new Scalar(0, 255, 0)));
            videoWriter.write(mat);
        }
    }

    private void addRectangle(Rect rect, Mat mat, Scalar scalar) {
        Point point = new Point(rect.x, rect.y);
        Date date = java.util.Calendar.getInstance().getTime();
        Imgproc.rectangle(mat, point,
                new Point(rect.x + rect.width, rect.y + rect.height),
                scalar);
        Imgproc.putText(
                mat,
                date.toString(),
                new Point(10, 30),
                6,
                0.75,
                new Scalar(255, 255, 255),
                4
        );
    }

    private static Mat bufferedImageToMat(BufferedImage bi) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(bi, "jpg", byteArrayOutputStream);
            byteArrayOutputStream.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return Imgcodecs.imdecode(new MatOfByte(byteArrayOutputStream.toByteArray()), Imgcodecs.IMREAD_UNCHANGED);
    }

}
