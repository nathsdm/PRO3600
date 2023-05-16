package com.example.cameraxapp

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.cameraxapp.OCR.TextRecognitionCallback
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc
import java.io.File
import com.example.cameraxapp.Nom.getCardNames
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import android.Manifest
import com.example.cameraxapp.Nom.findClosestName
import com.example.cameraxapp.Nom.findCodesForName


//debug
fun saveDebugImageToMediaStore(context: Context,debugImageName: String, mat: Mat): Uri? {
    // Create a ContentValues object to store metadata about the image
    val values = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "${debugImageName}_${System.currentTimeMillis()}.png")
        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/DebugImages")
    }

    // Get the content resolver and insert the new image into the MediaStore
    val resolver = context.contentResolver
    val uri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

    // Open an output stream to write the image data to the MediaStore
    uri?.let {
        val outputStream: OutputStream = resolver.openOutputStream(uri) ?: return null

        // Convert the Mat to a Bitmap and write it to the output stream
        val bitmap = matToBitmap(mat)

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)

        outputStream.flush()
        outputStream.close()
    }

    return uri
}
//mat to bitmap
fun matToBitmap(mat: Mat): Bitmap {
    val rgbMat = Mat(mat.rows(), mat.cols(), CvType.CV_8UC3)
    Imgproc.cvtColor(mat, rgbMat, Imgproc.COLOR_BGR2RGB)

    val rotatedMat = Mat()
    Core.rotate(rgbMat, rotatedMat, Core.ROTATE_90_CLOCKWISE)

    val bitmap = Bitmap.createBitmap(rotatedMat.cols(), rotatedMat.rows(), Bitmap.Config.RGB_565)
    Utils.matToBitmap(rotatedMat, bitmap)
    return bitmap
}

class MainActivity : AppCompatActivity() {
    val REQUEST_IMAGE_CAPTURE = 1
    val SELECT_PHOTO = 2
    lateinit var imageView: ImageView
    lateinit var photoFile: File
    lateinit var cards: MutableList<String>
    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 100
        private const val REQUEST_STORAGE_PERMISSION = 101
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        System.loadLibrary("opencv_java4")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        cards = getCardNames(this@MainActivity)

        imageView = findViewById(R.id.imageView)
        val takePhotoButton: Button = findViewById(R.id.takePhotoButton)
        val selectPhotoButton: Button = findViewById(R.id.selectPhotoButton)

        // Check for camera permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Request camera permissions
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
        }

        // Check for write storage permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Request write storage permissions
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_STORAGE_PERMISSION)
        }

        takePhotoButton.setOnClickListener {
            // Check for camera permissions again in case they were not granted initially
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                // Camera permissions already granted, proceed with camera intent
                startCameraIntent()
            } else {
                Toast.makeText(this, "Permission de la caméra requise", Toast.LENGTH_LONG).show()
            }
        }

        selectPhotoButton.setOnClickListener {
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also { selectPictureIntent ->
                startActivityForResult(selectPictureIntent, SELECT_PHOTO)
            }
        }
    }


    private fun startCameraIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            photoFile = createImageFile()
            val photoURI: Uri = FileProvider.getUriForFile(this, "${this.packageName}.provider", photoFile)
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when(requestCode){
                REQUEST_IMAGE_CAPTURE -> {
                    val imgBitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                    val imgMat = Mat()
                    Utils.bitmapToMat(imgBitmap, imgMat)
                    processImage(imgMat)
                }
                SELECT_PHOTO -> {
                    data?.data?.let {
                        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, it)
                        val imgMat = Mat()
                        Utils.bitmapToMat(bitmap, imgMat)
                        processImage(imgMat)
                    }
                }
            }
        }
    }


    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }


    private fun processImage(image: Mat) {
        // Convert the image from BGR to RGB
        Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2RGB)

        val results = Card_Finder.Card(image, true, this)
        val recognizedTexts = mutableListOf<String>()

        if (results != null && results.isNotEmpty()) {
            // Process each result
            for (i in results.indices) {
                // Convert the result image from RGB back to BGR
                Imgproc.cvtColor(results[i], results[i], Imgproc.COLOR_RGB2BGR)
                Core.rotate(results[i], results[i], Core.ROTATE_90_CLOCKWISE)
                val resultBitmap = Bitmap.createBitmap(results[i].cols(), results[i].rows(), Bitmap.Config.ARGB_8888)
                Utils.matToBitmap(results[i], resultBitmap)

                OCR.recognizeText(resultBitmap, object : TextRecognitionCallback {
                    override fun onSuccess(text: String) {
                        Toast.makeText(this@MainActivity, "Texte reconnu : $text", Toast.LENGTH_SHORT).show()
                        recognizedTexts.add(text)

                        // Check if recognizedTexts has at least 2 elements
                        if (recognizedTexts.size >= 2) {
                            val closestName = findClosestName(recognizedTexts[1], cards)
                            Toast.makeText(this@MainActivity, "Le nom le plus proche est : $closestName", Toast.LENGTH_SHORT).show()
                            val CodesName = findCodesForName(closestName,this@MainActivity)
                            if (CodesName.isEmpty()) {
                                println("La liste cardNames est vide.")
                            } else {
                                println("La liste cardNames n'est pas vide. Elle contient ${CodesName.size} éléments.")
                            }
                            val CodeName = findClosestName(recognizedTexts[0], CodesName)
                            Toast.makeText(this@MainActivity, "Le code le plus proche est : $CodeName", Toast.LENGTH_SHORT).show()

                        }
                    }

                    override fun onFailure(e: Exception) {
                        Toast.makeText(this@MainActivity, "Erreur de détection pour l'image $i", Toast.LENGTH_SHORT).show()
                    }
                })

                // Afficher la première image uniquement
                if (i == 0) {
                    imageView.setImageBitmap(resultBitmap)
                }

                // Save the image file
                val savedImageUri = saveImage(resultBitmap)
                if (savedImageUri != null) {
                    Toast.makeText(this@MainActivity, "Image $i enregistrée", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity, "Erreur pour enregistrer l'image $i", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            imageView.setImageDrawable(null)
            Toast.makeText(this@MainActivity, "Carte non détectée", Toast.LENGTH_SHORT).show()
        }
    }



    private fun saveImage(bitmap: Bitmap): Uri? {

        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "image_${System.currentTimeMillis()}.png")
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }

        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        uri?.let {
            val outputStream = contentResolver.openOutputStream(it)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream?.close()
        }

        return uri
    }


}



