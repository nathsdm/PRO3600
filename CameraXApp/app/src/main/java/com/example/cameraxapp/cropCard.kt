package com.example.cameraxapp
import org.opencv.core.*
import org.opencv.imgproc.Imgproc


fun cropCard(image: Mat): Mat

{
    // Convertir l'image en niveaux de gris
    val grayImage = Mat()
    Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY)

    // Appliquer un flou Gaussien pour réduire le bruit
    val blurredImage = Mat()
    Imgproc.GaussianBlur(grayImage, blurredImage, Size(5.0, 5.0), 0.0)

    // Détecter les contours de l'image
    val cannyEdges = Mat()
    Imgproc.Canny(blurredImage, cannyEdges, 50.0, 150.0)

    // Trouver les contours
    val contours: MutableList<MatOfPoint> = ArrayList()
    val hierarchy = Mat()
    Imgproc.findContours(cannyEdges, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE)

    // Trier les contours par aire, en ordre décroissant
    contours.sortByDescending { Imgproc.contourArea(it) }

    // Rechercher le premier contour qui correspond aux dimensions et aux coins arrondis d'une carte Yu-Gi-Oh
    var cardContour: MatOfPoint? = null
    for (contour in contours) {
        val rect = Imgproc.boundingRect(contour)
        val aspectRatio = rect.width.toDouble() / rect.height.toDouble()
        val yuGiOhAspectRatio = 59.0 / 86.0

        if (Math.abs(aspectRatio - yuGiOhAspectRatio) < 0.1 && rect.width > 100 && rect.height > 100) {
            val epsilon = 0.02 * Imgproc.arcLength(MatOfPoint2f(*contour.toArray()), true)
            val approxCurve = MatOfPoint2f()
            val contour2f = MatOfPoint2f(*contour.toArray())
            Imgproc.approxPolyDP(contour2f, approxCurve, epsilon, true)

            if (approxCurve.rows() >= 4) {
                cardContour = MatOfPoint(*approxCurve.toArray())
                break
            }
        }
    }

    // Si aucun contour approprié n'est trouvé, retournez l'image originale
    if (cardContour == null) {
        return image
    }

    // Obtenir les points du rectangle englobant autour du contour de la carte
    val rect = Imgproc.boundingRect(cardContour)

    // Découper la carte à partir de l'image d'entrée en utilisant le rectangle englobant
    return Mat(image, rect)
}
