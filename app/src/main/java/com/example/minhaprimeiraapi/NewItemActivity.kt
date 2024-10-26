package com.example.minhaprimeiraapi

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.FileProvider
import com.example.minhaprimeiraapi.databinding.ActivityNewItemBinding
import com.example.minhaprimeiraapi.model.ItemLocation
import com.example.minhaprimeiraapi.model.ItemValue
import com.example.minhaprimeiraapi.service.Result
import com.example.minhaprimeiraapi.service.RetrofitClient
import com.example.minhaprimeiraapi.service.safeApiCall
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.Date

class NewItemActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityNewItemBinding

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var selectedMarker: Marker? = null

    private lateinit var imageUri: Uri
    private var imageFile: File? = null

    private val cameraLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageBitmap = BitmapFactory.decodeFile(imageFile!!.path)
            uploadImageToFirebase(imageBitmap)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setupGoogleMap()
        setupView()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        binding.mapContent.visibility = View.VISIBLE
        getDeviceLocation()
        mMap.setOnMapClickListener { latLng: LatLng ->
            // Limpar marcador anterior, se existir
            selectedMarker?.remove()

            selectedMarker = mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .draggable(true)
                    .title("Lat: ${latLng.latitude}, Long: ${latLng.longitude}")
            )
        }
    }

    private fun uploadImageToFirebase(bitmap: Bitmap) {

    }

    private fun setupGoogleMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun getDeviceLocation() {
        // Verificar permissão de Localização
        if (checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PERMISSION_GRANTED) {
            // Permissão já concedida
            loadCurrentLocation()
        } else {
            // Usuário ainda não tem permissão, vai pedir a permissão de Localização
            requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun loadCurrentLocation() {
        mMap.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location ->
            val currentLocation = LatLng(location.latitude, location.longitude)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED) {
                loadCurrentLocation()
            } else {
                showToast("Permissão de Localização negada")
            }
        } else if(requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED) {
                openCamera()
            } else {
                showToast("Permissão de Câmera Negada")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun setupView() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        binding.saveCta.setOnClickListener {
            save()
        }
        binding.takePictureCta.setOnClickListener {
            takePicture()
        }
    }

    private fun takePicture() {
        if (checkSelfPermission(this, android.Manifest.permission.CAMERA) == PERMISSION_GRANTED) {
            openCamera()
        } else {
            requestCameraPermission()
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        imageUri = createImageUri()
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        cameraLauncher.launch(intent)
    }

    private fun createImageUri(): Uri {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_${timeStamp}_"

        // Obtém o diretório de armazenamento externo para imagens
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        // Cria um arquivo de imagem
        imageFile = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",         /* suffix */
            storageDir      /* directory */
        )

        // Retorna o URI para o arquivo
        return FileProvider.getUriForFile(
            this,  // Contexto
            "com.example.minhaprimeiraapi.fileprovider", // Autoridade
            imageFile!! // O arquivo
        )
    }

    private fun requestCameraPermission() {
        requestPermissions(
            this,
            arrayOf(android.Manifest.permission.CAMERA),
            CAMERA_REQUEST_CODE
        )
    }

    private fun save() {
        if (!validateForm()) return

        val name = binding.name.text.toString()
        val itemPosition = selectedMarker?.position?.let {
            ItemLocation(
                name = name,
                it.latitude,
                it.longitude
            )
        }
        CoroutineScope(Dispatchers.IO).launch {
            val id = SecureRandom().nextInt().toString()
            val itemValue = ItemValue(
                id,
                name,
                binding.surname.text.toString(),
                binding.profession.text.toString(),
                binding.imageUrl.text.toString(),
                binding.age.text.toString().toInt(),
                location = itemPosition,
                Date()
            )
            val result = safeApiCall { RetrofitClient.apiService.addItem(itemValue) }
            withContext(Dispatchers.Main) {
                when (result) {
                    is Result.Error -> {
                        Toast.makeText(
                            this@NewItemActivity,
                            R.string.error_create,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is Result.Success -> {
                        Toast.makeText(
                            this@NewItemActivity,
                            getString(R.string.success_create, result.data.id),
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                }
            }
        }
    }

    private fun validateForm(): Boolean {
        if (binding.name.text.toString().isBlank()) {
            Toast.makeText(this, getString(R.string.error_validate_form, "Name"), Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.surname.text.toString().isBlank()) {
            Toast.makeText(this, getString(R.string.error_validate_form, "Surname"), Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.age.text.toString().isBlank()) {
            Toast.makeText(this, getString(R.string.error_validate_form, "Age"), Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.imageUrl.text.toString().isBlank()) {
            Toast.makeText(this, getString(R.string.error_validate_form, "Image Url"), Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.profession.text.toString().isBlank()) {
            Toast.makeText(this, getString(R.string.error_validate_form, "Profession"), Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    companion object {

        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
        private const val CAMERA_REQUEST_CODE = 101

        fun newIntent(context: Context) =
            Intent(context, NewItemActivity::class.java)
    }
}