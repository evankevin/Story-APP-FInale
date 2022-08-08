package com.example.application_story.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.example.application_story.R
import com.example.application_story.databinding.ActivityAddStoryBinding
import com.example.application_story.utilities.*
import com.example.application_story.view.viewmodel.AddStoryViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import kotlin.random.Random

@AndroidEntryPoint
class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private val addViewModel by viewModels<AddStoryViewModel>()
    private lateinit var currentPhotoPath: String
    private var getFile: File? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.addStory)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getToken()
        setupAction()
        setupPermission()

    }

    private fun setupPermission() {
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(this, PERMISSION_REQUIRED, REQUEST_CODE)
        }
    }


    private fun getToken() {
        addViewModel.getUser().observe(this) {
            if (it.token.trim() != "") {
                EXTRA_TOKEN = it.token
            }
        }
    }

    private fun setupAction() {
        if (getFile == null) {
            Glide.with(this).load(getFile).placeholder(R.drawable.ic_place_holder)
                .fallback(R.drawable.ic_place_holder).into(binding.previewImageView)
        }
        binding.apply {
            cameraXButton.setOnClickListener {
                startCameraX()
            }
            cameraButton.setOnClickListener {
                startTakePhoto()
            }
            galleryButton.setOnClickListener {
                startGallery()
            }
            uploadButton.setOnClickListener {
                uploadStory()
            }
            getMyLocation.setOnClickListener{
                getMyLocation()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (!allPermissionsGranted()) {
                Toast.makeText(this, "Unauthorized, please give the permission first", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = PERMISSION_REQUIRED.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoUri: Uri =
                FileProvider.getUriForFile(
                    this,
                    "com.example.application_story",
                    it
                )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            launcherIntentCamera.launch(intent)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            getFile = myFile

            val result = BitmapFactory.decodeFile(getFile?.path)
            binding.previewImageView.setImageBitmap(result)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri

            val myFile = uriToFile(selectedImg, this@AddStoryActivity)

            getFile = myFile

            binding.previewImageView.setImageURI(selectedImg)
        }
    }

    private fun uploadStory() {
        val description = binding.descriptionInput.text.toString()
        when {
            getFile == null -> {
                Toast.makeText(
                    this@AddStoryActivity,
                    "Please insert the picture first.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            description.trim().isEmpty() -> {
                Toast.makeText(
                    this@AddStoryActivity,
                    "Fill the Description",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {
                val file = reduceImageSize(getFile as File)
                val locationInput = binding.locationInput.text.toString()
                val location = locationSubmit(locationInput)
                addViewModel.uploadImage(EXTRA_TOKEN, file, description,location )

                addViewModel.uploadResponse.observe(this) { response ->
                    Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                    if (!response.error) {
                        val intent = Intent(this@AddStoryActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            getFile = myFile
            val result = rotateBitmap(
                BitmapFactory.decodeFile(getFile?.path),
                isBackCamera
            )

            binding.previewImageView.setImageBitmap(result)
        }
    }

    private val getPermission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
    { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> getMyLocation()
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> getMyLocation()
            else -> {}
        }
    }

    private fun checkPermission(
        permission: String): Boolean
    {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLocation() {
        if (
            checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val locationName = getLocationName(LatLng(location.latitude, location.longitude))
                    binding.locationInput.setText(locationName)
                } else {
                    Toast.makeText(this, "Can't find the location", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            getPermission.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun getLocationName(latLng: LatLng): String {
        return try {
            val geocoder = Geocoder(this)
            val location = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (location.isEmpty()) {
                getString(R.string.noAddress)
            }
            else location[0].getAddressLine(
                0
            )
        } catch (e: Exception) {
            getString(R.string.noAddress)
        }
    }

    private fun locationSubmit(locationName: String): LatLng {
        return try {
            val randomLatitude = randomLocation()
            val randomLongitude = randomLocation()

            val geocoder = Geocoder(this)
            val allLocation = geocoder.getFromLocationName(locationName, 1)
            if (allLocation.isEmpty()) {
                LatLng(randomLatitude, randomLongitude)
            } else {
                LatLng(allLocation[0].latitude, allLocation[0].longitude)
            }
        } catch (e: Exception) {
            LatLng(0.0, 0.0)
        }
    }

    private fun randomLocation(): Double {
        return Random.nextDouble(15.0, 100.0)
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }



    companion object {
        const val CAMERA_X_RESULT = 200
        private var EXTRA_TOKEN = "extra_token"

        private val PERMISSION_REQUIRED = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE = 10
        private const val TAG = "AD ACTIVITY"
    }
}
