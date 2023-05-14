package com.example.cameraxapp;


import static com.example.cameraxapp.MainActivityKt.saveDebugImageToMediaStore;

import android.content.Context;

import org.opencv.core.*;
import org.opencv.imgproc.CLAHE;
import org.opencv.imgproc.Imgproc;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
public class Card_Finder {

    static {
        Core.setNumThreads(Core.getNumberOfCPUs());
    }

    public static Mat Card(Mat im, boolean debug, Context context) {
        // Sauvegarder les dimensions de l'image d'origine
        Size originalSize = new Size(im.width(), im.height());
        double ratioim = im.height() / 500.0;
        Size newSize = new Size(im.width() / ratioim, 500);
        Imgproc.resize(im, im, newSize, 0, 0, Imgproc.INTER_AREA);


        Size imShape = new Size(im.width(), im.height());
        double imArea = imShape.width * imShape.height;

        Mat imOrig = im.clone();
        if(debug){
            saveDebugImageToMediaStore(context, "/imageOriginel.png", im);
        }
        // Convert the input image to grayscale
        Mat gray = new Mat();
        Imgproc.cvtColor(im, gray, Imgproc.COLOR_BGR2GRAY);


        //Histogramme adaptative
        Mat equalizedImage = new Mat();
        CLAHE clahe = Imgproc.createCLAHE();
        clahe.setClipLimit(4);
        clahe.apply(gray, equalizedImage);

        // Perform Gaussian blur on the input image
        Mat blur = new Mat();
        Imgproc.bilateralFilter(equalizedImage,blur, 11, 17, 17);
        if (debug) {
            saveDebugImageToMediaStore(context, "/blur.png", blur);
        }

        Mat edged = new Mat();
        Imgproc.Canny(blur, edged, 70, 200);
        if (debug) {
            saveDebugImageToMediaStore(context, "/edged.png", edged);
        }
        // Create a structuring element for morphological operations
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3,3));
        Imgproc.dilate(edged,edged, kernel, new Point(-1, -1));
        if (debug) {
            saveDebugImageToMediaStore(context, "/dilate.png", edged);
        }
        // Apply morphological closing to the binary image to close small gaps in the edges
        Mat closed_image = new Mat();
        Imgproc.morphologyEx(edged, closed_image, Imgproc.MORPH_CLOSE, kernel);
        if (debug) {
            saveDebugImageToMediaStore(context, "/closed.png", closed_image);
        }
        gray.release();
        equalizedImage.release();
        blur.release();
        edged.release(); // libère la mémoire

        double minShape = 0.05 * imArea;
        double maxShape = 0.95 * imArea;

        double realCardRatio = 63.0 / 88.0;
        double margeRatio = 0.1;
        double lowerBound = realCardRatio - margeRatio;
        double upperBound = realCardRatio + margeRatio;


        // Find contours
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();

        Imgproc.findContours(closed_image, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_NONE);


        if (debug) {
            // Create a copy of the original image
            Mat imWithContours = imOrig.clone();
            // Draw all the contours in red
            Imgproc.drawContours(imWithContours, contours, -1, new Scalar(0, 0, 255), 5);
            saveDebugImageToMediaStore(context, "/all_contours.png", imWithContours);
            System.out.println("Nombre de contours détectés: " + contours.size());
            imWithContours.release();
        }

        Mat imContourCandidates = Mat.zeros(im.rows(), im.cols(), CvType.CV_8UC1);
        List<MatOfPoint> candidateContours = new ArrayList<>();

        for (MatOfPoint contour : contours) {
            RotatedRect rect = Imgproc.minAreaRect(new MatOfPoint2f(contour.toArray()));
            Point[] box = new Point[4];
            rect.points(box);

            double areaBox = Imgproc.contourArea(new MatOfPoint(box));

            if (debug) {
                System.out.println("areabox: " + areaBox);
                System.out.println("minShape: " + minShape);
                System.out.println("maxShape: " + maxShape);

            }


            // Check 1 for "is this contour a card?" : is the contour big enough
            if (areaBox >= minShape && areaBox <= maxShape) {
                Imgproc.drawContours(imContourCandidates, new ArrayList<>(List.of(new MatOfPoint(box))), -1, new Scalar(255), 10);

                // Check 2 : is the dimension ratio correct?
                double L1 = distance(new double[]{box[0].x, box[0].y}, new double[]{box[1].x, box[1].y});
                double L2 = distance(new double[]{box[1].x, box[1].y}, new double[]{box[2].x, box[2].y});
                double minRatio = Math.min(L1 / L2, L2 / L1);
                System.out.println("minRatio: " + minRatio);
                System.out.println("realcardratior: " + realCardRatio);
                System.out.println("upperBound: " + upperBound);
                System.out.println("lowerBound: " + lowerBound);



                if (minRatio >= lowerBound && minRatio <= upperBound) {
                    candidateContours.add(contour);
                }
            }
        }

        if (debug) {
            saveDebugImageToMediaStore(context, "/contour_candidates.png", imContourCandidates);
        }
        if (candidateContours.size() == 1) {
            MatOfPoint2f cardContour = new MatOfPoint2f(candidateContours.get(0).toArray());
            RotatedRect cardRect = Imgproc.minAreaRect(cardContour);
            Point[] cardBoxPoints = new Point[4];
            cardRect.points(cardBoxPoints);

            // Trouver les points de transformation pour redresser la carte
            Point[] srcPoints = sortCorners(cardBoxPoints);
            Point[] dstPoints = new Point[4];
            dstPoints[0] = new Point(0, 0);
            dstPoints[1] = new Point(cardRect.size.width - 1, 0);
            dstPoints[2] = new Point(cardRect.size.width - 1, cardRect.size.height - 1);
            dstPoints[3] = new Point(0, cardRect.size.height - 1);

            // Appliquer la transformation de perspective pour redresser la carte
            Mat warpMat = Imgproc.getPerspectiveTransform(new MatOfPoint2f(srcPoints), new MatOfPoint2f(dstPoints));
            Mat card = new Mat((int) cardRect.size.height, (int) cardRect.size.width, CvType.CV_8UC1);
            Imgproc.warpPerspective(imOrig, card, warpMat, card.size());
            // Redimensionner la carte pour qu'elle ait la taille de l'image d'origine
            Mat resizedCard = new Mat();
            Imgproc.resize(card, resizedCard, originalSize, 0, 0, Imgproc.INTER_LINEAR);

            return resizedCard;
        }

        if(candidateContours.size() >= 2){
            // Calculate dimension ratios for each contour candidate
            List<Double> ratios = new ArrayList<>();
            for (MatOfPoint contour : candidateContours) {
                RotatedRect rect = Imgproc.minAreaRect(new MatOfPoint2f(contour.toArray()));
                Point[] box = new Point[4];
                rect.points(box);
                double L1 = distance(new double[]{box[0].x, box[0].y}, new double[]{box[1].x, box[1].y});
                double L2 = distance(new double[]{box[1].x, box[1].y}, new double[]{box[2].x, box[2].y});
                double ratio = Math.min(L1 / L2, L2 / L1);
                ratios.add(ratio);
                if (debug){
                    System.out.println("ratio: " + ratio);
                }
            }
            // Sort the candidates by the difference between their ratios and the real card ratio
            double[] differences = new double[candidateContours.size()];
            for (int i = 0; i < candidateContours.size(); i++) {
                double ratio = ratios.get(i);
                differences[i] = Math.abs(ratio - realCardRatio);
            }
            int bestCandidateIndex = minIndex(differences);
            MatOfPoint2f cardContour = new MatOfPoint2f(candidateContours.get(bestCandidateIndex).toArray());
            RotatedRect cardRect = Imgproc.minAreaRect(cardContour);
            Point[] cardBoxPoints = new Point[4];
            cardRect.points(cardBoxPoints);

            // Trouver les points de transformation pour redresser la carte
            Point[] srcPoints = sortCorners(cardBoxPoints);
            Point[] dstPoints = new Point[4];
            dstPoints[0] = new Point(0, 0);
            dstPoints[1] = new Point(cardRect.size.width - 1, 0);
            dstPoints[2] = new Point(cardRect.size.width - 1, cardRect.size.height - 1);
            dstPoints[3] = new Point(0, cardRect.size.height - 1);

            // Appliquer la transformation de perspective pour redresser la carte
            Mat warpMat = Imgproc.getPerspectiveTransform(new MatOfPoint2f(srcPoints), new MatOfPoint2f(dstPoints));
            Mat card = new Mat((int) cardRect.size.height, (int) cardRect.size.width, CvType.CV_8UC1);
            Imgproc.warpPerspective(imOrig, card, warpMat, card.size());

            // Redimensionner la carte pour qu'elle ait la taille de l'image d'origine
            Mat resizedCard = new Mat();
            Imgproc.resize(card, resizedCard, originalSize, 0, 0, Imgproc.INTER_LINEAR);

            // Convertir l'image en niveaux de gris (si ce n'est pas déjà fait)
            Mat grayCard = new Mat();
            Imgproc.cvtColor(resizedCard, grayCard, Imgproc.COLOR_BGR2GRAY);


            return resizedCard;
        }
        return null;
    }



    public static Point[] sortCorners(Point[] corners) {
        Point[] sortedCorners = new Point[4];

        // Trouver le centre du rectangle
        Point center = new Point(0, 0);
        for (Point corner : corners) {
            center.x += corner.x / 4.0;
            center.y += corner.y / 4.0;
        }

        // Trier les coins en fonction de leur position par rapport au centre
        List<Point> top = new ArrayList<>();
        List<Point> bottom = new ArrayList<>();

        for (Point corner : corners) {
            if (corner.y < center.y) {
                top.add(corner);
            } else {
                bottom.add(corner);
            }
        }

        // Trier les points du haut et du bas par rapport à leur coordonnée x
        top.sort(Comparator.comparingDouble(point -> point.x));
        bottom.sort((point1, point2) -> Double.compare(point2.x, point1.x));

        // Assigner les coins triés au tableau sortedCorners
        sortedCorners[0] = top.get(0); // coin supérieur gauche
        sortedCorners[1] = top.get(1); // coin supérieur droit
        sortedCorners[2] = bottom.get(0); // coin inférieur droit
        sortedCorners[3] = bottom.get(1); // coin inférieur gauche

        return sortedCorners;
    }



    private static int minIndex(double[] values) {
        int minIdx = 0;
        for (int i = 1; i < values.length; i++) {
            if (values[i] < values[minIdx]) {
                minIdx = i;
            }
        }
        return minIdx;
    }

    public static double distance(double[] pt1, double[] pt2) {
        return Math.hypot(pt1[0] - pt2[0], pt1[1] - pt2[1]);
    }
}

