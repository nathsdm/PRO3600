package com.example.testtest

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.room.Room.databaseBuilder
import com.example.testtest.Nom.*
import com.example.testtest.OCR.TextRecognitionCallback
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc
import java.io.File
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*


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

class MainActivity4 : AppCompatActivity() {

    private var db: AppDatabase? = null

    fun getDb(): AppDatabase? {
        return db
    }

    val REQUEST_IMAGE_CAPTURE = 1
    val SELECT_PHOTO = 2
    lateinit var imageView: ImageView
    lateinit var photoFile: File
    lateinit var cards: MutableList<String>
    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 100
        private const val REQUEST_STORAGE_PERMISSION = 101
    }

    fun addCarteToMain(code: String){
        val insertDataTask2 = InsertDataTask2(this, code)
        insertDataTask2.execute()
        Thread.sleep(100)
        val sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        with (sharedPref.edit()) {
            putString("card_code", code)
            commit()
        }
        onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)

    }


    fun showPopUpNon(): String {
        var userInput = ""
        val editText = EditText(this)
        val dialog = AlertDialog.Builder(this)
            .setTitle("Carte non détectée.")
            .setMessage("Veuillez entrer le code de votre cate :")
            .setView(editText)
            .setPositiveButton("Submit") { dialog, _ ->
                userInput = editText.text.toString()
                CheckGoodCodeOrBadCodeTask(this).execute(userInput)


            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()
        return userInput
    }

    fun showPopUpNon2(): String {
        var userInput = ""
        val editText = EditText(this)
        val dialog = AlertDialog.Builder(this)
            .setTitle("Dans ce cas, veuillez entrer le bon code ici :")
            .setView(editText)
            .setPositiveButton("Submit") { dialog, _ ->
                userInput = editText.text.toString()
                CheckGoodCodeOrBadCodeTask(this).execute(userInput)


            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()
        return userInput
    }

    fun showPopUpConfirm(code: String) {
        AlertDialog.Builder(this)
            .setTitle("Confirmation")
            .setMessage("Is \"$code\" your card's code?")
            .setPositiveButton("Yes") { _, _ ->
                CheckGoodCodeOrBadCodeTask(this).execute(code)
            }
            .setNegativeButton("No") { dialog, _ ->
                showPopUpNon2()
                dialog.dismiss()
            }
            .create()
            .show()
    }



    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        System.loadLibrary("opencv_java4")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main4)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        cards = getCardNames(this@MainActivity4)


        db = databaseBuilder<AppDatabase>(
            applicationContext,
            AppDatabase::class.java, "carte-database"
        ).build()

        imageView = findViewById(R.id.imageView)
        val takePhotoButton: Button = findViewById(R.id.takePhotoButton)
        val selectPhotoButton: Button = findViewById(R.id.selectPhotoButton)


        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.mainMenu -> {
                    // Switch to MainActivity only if current activity is not MainActivity
                    onBackPressed();
                    true
                }
                R.id.cameraMenu -> {
                    // Do nothing if the current activity is MainActivity4
                    true
                }
                else -> false
            }
        }

        // Set the navigation listener to your BottomNavigationView
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)


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
            val photoURI: Uri = FileProvider.getUriForFile(this, "${BuildConfig.APPLICATION_ID}.provider", photoFile)
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
                        //Toast.makeText(this@MainActivity4, "Texte reconnu : $text", Toast.LENGTH_SHORT).show()
                        recognizedTexts.add(text)

                        // Check if recognizedTexts has at least 2 elements
                        if (recognizedTexts.size >= 2) {
                            val closestName = findClosestName(recognizedTexts[1], cards)
                            //Toast.makeText(this@MainActivity4, "Le nom le plus proche est : $closestName", Toast.LENGTH_SHORT).show()
                            val CodesName = findCodesForName(closestName,this@MainActivity4)
                            if (CodesName.isEmpty()) {
                                println("La liste cardNames est vide.")
                            } else {
                                println("La liste cardNames n'est pas vide. Elle contient ${CodesName.size} éléments.")
                            }
                            val CodeName = findClosestName(recognizedTexts[0], CodesName)
                            //Toast.makeText(this@MainActivity4, "Le code le plus proche est : $CodeName", Toast.LENGTH_SHORT).show()
                            showPopUpConfirm(CodeName)

                        }
                        else{
                            //showPopUpNon()
                        }
                    }

                    override fun onFailure(e: Exception) {
                        Toast.makeText(this@MainActivity4, "Erreur de détection pour l'image $i", Toast.LENGTH_SHORT).show()
                    }
                })

                // Afficher la première image uniquement
                if (i == 0) {
                    imageView.setImageBitmap(resultBitmap)
                }

                // Save the image file
                val savedImageUri = saveImage(resultBitmap)
                if (savedImageUri != null) {
                   // Toast.makeText(this@MainActivity4, "Image $i enregistrée", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity4, "Erreur pour enregistrer l'image $i", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            imageView.setImageDrawable(null)
            Toast.makeText(this@MainActivity4, "Carte non détectée", Toast.LENGTH_SHORT).show()
            showPopUpNon()
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



