package com.example.cameraxapp



import android.Manifest
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ColorSpace
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.cameraxapp.databinding.ActivityMainBinding
import org.opencv.android.Utils
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


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
    val rgbaMat = Mat(mat.rows(), mat.cols(), CvType.CV_8UC4)
    Imgproc.cvtColor(mat, rgbaMat, Imgproc.COLOR_BGR2RGBA)

    val bitmap = Bitmap.createBitmap(rgbaMat.cols(), rgbaMat.rows(), Bitmap.Config.ARGB_8888)
    Utils.matToBitmap(rgbaMat, bitmap)
    return bitmap
}
//Transform an uri to a Mat
fun uriToMat(contentResolver: ContentResolver, uri: Uri): Mat {
    val source: ImageDecoder.Source = ImageDecoder.createSource(contentResolver, uri)
    val bitmap = ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
        decoder.setTargetColorSpace(ColorSpace.get(ColorSpace.Named.SRGB))
        decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
    }

    val rgbaMat = Mat(bitmap.height, bitmap.width, CvType.CV_8UC4)
    Utils.bitmapToMat(bitmap, rgbaMat)

    val bgrMat = Mat(rgbaMat.rows(), rgbaMat.cols(), CvType.CV_8UC3)
    Imgproc.cvtColor(rgbaMat, bgrMat, Imgproc.COLOR_RGBA2BGR)

    return bgrMat
}

// Function to save a Mat as a new image file in the MediaStore
fun saveMatToMediaStore(context: Context, mat: Mat): Uri? {
    // Create a ContentValues object to store metadata about the image
    val values = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "IMG_${System.currentTimeMillis()}.jpg") // Set the display name for the image
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg") // Set the MIME type of the image
        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image") // Set the relative path for the image
    }

    // Get the content resolver and insert the new image into the MediaStore
    val resolver = context.contentResolver
    val uri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

    // Open an output stream to write the image data to the MediaStore
    uri?.let {
        val outputStream: OutputStream = resolver.openOutputStream(uri) ?: return null

        // Convert the Mat to a Bitmap and write it to the output stream
        val bitmap = matToBitmap(mat)

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

        outputStream.flush()
        outputStream.close()
    }

    return uri
}

class MainActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityMainBinding

    private var imageCapture: ImageCapture? = null

    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        System.loadLibrary("opencv_java4")
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        // Set up the listeners for take photo and video capture buttons
        viewBinding.imageCaptureButton.setOnClickListener { takePhoto() }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }


    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time stamped name and MediaStore entry.
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name) //nom
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg") //type
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image") //Path
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
            .build()


        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    // Convertir la photo en un objet Mat puis la découper et la sauvgarder dans la gallerie
                    val mat = uriToMat(contentResolver, outputFileResults.savedUri!!)
                    val matcrop = Card_Finder.Card(mat,true,this@MainActivity)
                    matcrop?.let {
                        // Perform operations with the non-null matCrop value
                    } ?: run {
                        Log.e("YourTag", "No contours found.")
                    }
                    val uri = matcrop?.let { saveMatToMediaStore(this@MainActivity, it) }
                    Log.d(TAG, "Photo capture succeeded: $uri")

                    // Afficher un message lorsque la photo est enregistrée
                    val message = "Photo enregistrée: $uri"
                    Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()

                }
            }
        )

    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))


    }


        private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }


    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
            }.toTypedArray()
    }
}
