package com.bastian.storyapps.ui.addstory

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.widget.addTextChangedListener
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.bastian.storyapps.R
import com.bastian.storyapps.data.preferences.UserPreferences
import com.bastian.storyapps.databinding.ActivityAddStoryBinding
import com.bastian.storyapps.ui.ViewModelFactory
import com.bastian.storyapps.ui.main.MainActivity
import com.bastian.storyapps.utils.createCustomTempFile
import com.bastian.storyapps.utils.reduceFileImage
import com.bastian.storyapps.utils.rotateImage
import com.bastian.storyapps.utils.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var currentPhotoPath: String
    private lateinit var addStoryViewModel: AddStoryViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var location: Location? = null
    private var getFile: File? = null

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this, R.string.unauthorized_perm,
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setupViewModel()
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        binding.apply {
            btnCamera.setOnClickListener { startTakePhoto() }
            btnGallery.setOnClickListener { startGallery() }
            switchLocation.setOnCheckedChangeListener { _, isChecked ->
                if(isChecked) {
                    getLastLocation()
                }
                else {
                    location = null
                }
            }
            buttonAdd.setOnClickListener { uploadImage() }
            buttonAdd.isEnabled = false

            edAddDescription.addTextChangedListener {
                val isDescriptionEmpty = it.isNullOrEmpty()
                val isImageSelected = getFile != null
                buttonAdd.isEnabled = !isDescriptionEmpty && isImageSelected
            }
        }

    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Pilih gambar")
        launcherIntentGallery.launch(chooser)
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@AddStoryActivity,
                "com.bastian.storyapps",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            myFile.let { file ->
                val bitmap = BitmapFactory.decodeFile(file.path)
                rotateImage(bitmap, currentPhotoPath).compress(
                    Bitmap.CompressFormat.JPEG,
                    100,
                    FileOutputStream(file)
                )
                addStoryViewModel.setFile(file)
            }
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri

            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this)
                addStoryViewModel.setFile(myFile)
            }
        }
    }

    private fun uploadImage() {
        if (getFile != null) {
            val description = binding.edAddDescription.text.toString()
            val file = reduceFileImage(getFile as File)

            var lat: RequestBody? = null
            var lon: RequestBody? = null
            if (location != null) {
                lat =
                    location?.latitude.toString().toRequestBody("text/plain".toMediaType())
                lon =
                    location?.longitude.toString().toRequestBody("text/plain".toMediaType())
            }
            addStoryViewModel.getUser().observe(this){user ->
                addStoryViewModel.uploadImage("Bearer ${user.token}", file, description, lat, lon)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getLastLocation() {
        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    this.location = location
                    Log.d(TAG, "getLastLocation: ${location.latitude}, ${location.longitude}")
                } else {
                    Toast.makeText(this, getString(R.string.please_activate_location_message), Toast.LENGTH_SHORT).show()
                    binding.switchLocation.isChecked = false
                }
            }
        } else {
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION))
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        Log.d(TAG, "$permissions")
        when {
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                getLastLocation()
            }
            else -> {
                Snackbar.make(findViewById(android.R.id.content), getString(R.string.location_permission_denied), Snackbar.LENGTH_SHORT)
                    .setActionTextColor(ContextCompat.getColor(this, R.color.white))
                    .setAction(getString(R.string.location_permission_denied_action)) {
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).also { intent ->
                            val uri = Uri.fromParts("package", packageName, null)
                            intent.data = uri
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }
                    .show()
                binding.switchLocation.isChecked = false
            }
        }
    }



    private fun setupViewModel(){
        addStoryViewModel = ViewModelProvider(this, ViewModelFactory(UserPreferences.getInstance(dataStore)))[AddStoryViewModel::class.java]
        addStoryViewModel.uploadResponse.observe(this) { response ->
            if (!response.error) {
                Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Error: ${response.message}", Toast.LENGTH_SHORT).show()
            }
        }
        addStoryViewModel.hasUploaded.observe(this) { file ->
            if (file != null) {
                getFile = file
                binding.ivThumbnail.setImageBitmap(BitmapFactory.decodeFile(file.path))
            } else {
                binding.ivThumbnail.setImageResource(R.drawable.ic_thumbnail_image)
            }
        }
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10

        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, AddStoryActivity::class.java)
            context.startActivity(starter)
        }
    }
}