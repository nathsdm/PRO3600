import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;


public class yu {
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat image = Imgcodecs.imread("tocrop2.jpg");
        int scale_percent = 40;
        int width = (int) (image.cols() * scale_percent / 100);
        int height = (int) (image.rows() * scale_percent / 100);
        Size newSize = new Size(width, height);
        Imgproc.resize(image, image, newSize);

        Mat gray = new Mat();
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(gray, gray, new Size(5, 5), 0);
        Mat edged = new Mat();
        Imgproc.Canny(gray, edged, 35, 200);

        Imgcodecs.imwrite("step1.jpg", edged);

        List<MatOfPoint> cnts = new ArrayList<MatOfPoint>();
        Imgproc.findContours(edged.clone(), cnts, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        Collections.sort(cnts, new Comparator<MatOfPoint>() {
            public int compare(MatOfPoint o1, MatOfPoint o2) {
                double area1 = Imgproc.contourArea(o1);
                double area2 = Imgproc.contourArea(o2);
                if (area1 > area2)
                    return -1;
                else if (area1 < area2)
                    return 1;
                else
                    return 0;
            }
        });

        MatOfPoint screenCnt = new MatOfPoint();
        for (int i = 0; i < cnts.size(); i++) {
            MatOfPoint2f approx = new MatOfPoint2f();
            MatOfPoint2f contour2f = new MatOfPoint2f(cnts.get(i).toArray());
            double peri = Imgproc.arcLength(contour2f, true);
            Imgproc.approxPolyDP(contour2f, approx, 0.02 * peri, true);
            if (approx.toArray().length == 4) {
                screenCnt = cnts.get(i);
                break;
            }
        }

        Imgproc.drawContours(image, Arrays.asList(screenCnt), -1, new Scalar(0, 0, 255), 3);
        Imgcodecs.imwrite("step2.jpg", image);

        Point[] pts = screenCnt.toArray();
        Point[] rect = new Point[4];
        double[] s = new double[4];
        double[] diff = new double[4];
        for (int i = 0; i < pts.length; i++) {
            s[i] = pts[i].x + pts[i].y;
        }
        rect[0] = pts[Utils.minIndex(s)];
        rect[2] = pts[Utils.maxIndex(s)];
        for (int i = 0; i < pts.length; i++) {
            diff[i] = pts[i].x - pts[i].y;
        }
        rect[1] = pts[Utils.minIndex(diff)];
        rect[3] = pts[Utils.maxIndex(diff)];

        double widthA = Math.sqrt(Math.pow(rect[2].x - rect[0].x, 2) + Math.pow(rect[2].y - rect[0].y, 2));
        double widthB = Math.sqrt(Math.pow(rect[1].x - rect[3].x, 2) + Math.pow(rect[1].y - rect[3].y, 2));
        double maxWidth = Math.max(widthA, widthB);
        double heightA = Math.sqrt(Math.pow(rect[1].x - rect[2].x, 2) + Math.pow(rect[1].y - rect[2].y, 2));
        double heightB = Math.sqrt(Math.pow(rect[0].x - rect[3].x, 2) + Math.pow(rect[0].y - rect[3].y, 2));
        double maxHeight = Math.max(heightA, heightB);

        Point[] dst = new Point[4];
        dst[0] = new Point(0, 0);
        dst[1] = new Point(maxWidth - 1, 0);
        dst[2] = new Point(maxWidth - 1, maxHeight - 1);
        dst[3] = new Point(0, maxHeight - 1);

        Mat M = Imgproc.getPerspectiveTransform(Converters.vector_Point2f_to_Mat(Arrays.asList(rect)), Converters.vector_Point2f_to_Mat(Arrays.asList(dst)));
        Mat warped = new Mat();
        Imgproc.warpPerspective(image, warped, M, new Size(maxWidth * 2, maxHeight * 2));
        Imgcodecs.imwrite("step3.jpg", warped);
    }
}

class Utils {
    public static int minIndex(double[] arr) {
        int minIndex = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] < arr[minIndex])
                minIndex = i;
        }
        return minIndex;
    }

    public static int maxIndex(double[] arr) {
        int maxIndex = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > arr[maxIndex])
                maxIndex = i;
        }
        return maxIndex;
    }
}
