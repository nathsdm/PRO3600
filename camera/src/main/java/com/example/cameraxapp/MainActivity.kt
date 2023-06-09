package com.example.cameraxapp

import android.content.ContentValues
import android.content.Context
import android.graphics.*
import org.opencv.core.CvType
import org.opencv.imgproc.Imgproc
import java.io.OutputStream
import java.util.*
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.Mat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat


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

    override fun onCreate(savedInstanceState: Bundle?) {
        System.loadLibrary("opencv_java4")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        val takePhotoButton: Button = findViewById(R.id.takePhotoButton)
        val selectPhotoButton: Button = findViewById(R.id.selectPhotoButton)

        takePhotoButton.setOnClickListener {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                photoFile = createImageFile()
                val photoURI: Uri = FileProvider.getUriForFile(this, "${BuildConfig.APPLICATION_ID}.provider", photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }

        selectPhotoButton.setOnClickListener {
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also { selectPictureIntent ->
                startActivityForResult(selectPictureIntent, SELECT_PHOTO)
            }
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

        val result = Card_Finder.Card(image, true, this)
        if (result != null) {
            // Convert the result image from RGB back to BGR
            Imgproc.cvtColor(result, result, Imgproc.COLOR_RGB2BGR)
            Core.rotate(result, result, Core.ROTATE_90_CLOCKWISE)
            val resultBitmap = Bitmap.createBitmap(result.cols(), result.rows(), Bitmap.Config.ARGB_8888)
            Utils.matToBitmap(result, resultBitmap)
            imageView.setImageBitmap(resultBitmap)

            // Save the image file
            val savedImageUri = saveImage(resultBitmap)
            if (savedImageUri != null) {
                Toast.makeText(this@MainActivity, "Image enregistré", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@MainActivity, "Erreur pour enregistré l'image", Toast.LENGTH_SHORT).show()
            }
        } else {
            imageView.setImageDrawable(null)
            Toast.makeText(this@MainActivity, "Carte non detecté", Toast.LENGTH_SHORT).show()
        }
    }


    private fun saveImage(bitmap: Bitmap): Uri? {
        val imagesDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageName = "image_${System.currentTimeMillis()}.png"
        val imageFile = File(imagesDirectory, imageName)

        return try {
            val outputStream = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            FileProvider.getUriForFile(this, "${BuildConfig.APPLICATION_ID}.provider", imageFile)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

}



