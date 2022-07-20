package com.example.foodemption.maps

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.TextView
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.foodemption.R
import com.example.foodemption.databinding.ActivityMapsBinding
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.maps.android.compose.*
import kotlin.random.Random

private const val TAG = "BasicMapActivity"

private val uw = LatLng(43.47302294446692, -80.54628464593662)
private val splus = LatLng(43.47276594980127, -80.53735528836131)
private val proofKitchen = LatLng(43.46382179474564, -80.52847892032257)

private val defaultCameraPosition = CameraPosition.fromLatLngZoom(uw, 11f)

private lateinit var mMap: GoogleMap
private lateinit var binding: ActivityMapsBinding
private lateinit var db : DatabaseReference
private var dataId = ""
private var defaultMaps = GoogleMap.MAP_TYPE_NORMAL

class MapsConsumerActivity  : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private val viewModel by viewModels<MapsActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getLocationPermission()
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mFirebaseInstance = FirebaseDatabase.getInstance()
        db = mFirebaseInstance.getReference("data")
        mFirebaseInstance.getReference("app_title").setValue("Save place coordinates using Firebase")

        binding.btnSave.setOnClickListener {
            savePlaces()
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setContent {
            var isMapLoaded by remember { mutableStateOf(false) }
            // Observing and controlling the camera's state can be done with a CameraPositionState
            val cameraPositionState = rememberCameraPositionState {
                position = defaultCameraPosition
            }

            Box(Modifier.fillMaxSize()) {
                GoogleMapView2(
                    modifier = Modifier.matchParentSize(),
                    cameraPositionState = cameraPositionState,
                    onMapLoaded = {
                        isMapLoaded = true
                    },
                )
                if (!isMapLoaded) {
                    AnimatedVisibility(
                        modifier = Modifier
                            .matchParentSize(),
                        visible = !isMapLoaded,
                        enter = EnterTransition.None,
                        exit = fadeOut()
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .background(MaterialTheme.colors.background)
                                .wrapContentSize()
                        )
                    }
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.mapType = defaultMaps
        mMap.moveCamera(CameraUpdateFactory.newLatLng(uw))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(uw,15.0f))
        mMap.addMarker(
            MarkerOptions()
                .position(uw)
                .title("Marker in UW")
        )
        mMap.setOnMapClickListener(this)
    }

    override fun onMapClick(latLng: LatLng) {
        if(TextUtils.isEmpty(binding.etLongitude.editText?.text.toString()) && TextUtils.isEmpty(
                binding.etLatitude.editText?.text.toString())){
            mMap.addMarker(
                MarkerOptions().position(latLng).title(getFullyAddress(latLng)).icon(BitmapDescriptorFactory.defaultMarker(
                    Random.nextDouble(0.0,360.0).toFloat())))
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15.0f))
            binding.apply {
                etLatitude.editText?.setText(latLng.latitude.toString(), TextView.BufferType.EDITABLE)
                etLongitude.editText?.setText(latLng.longitude.toString(), TextView.BufferType.EDITABLE)
            }
            mMap.uiSettings.setAllGesturesEnabled(false)
        }
    }

    private fun getFullyAddress(latLng: LatLng) : String?{
        val geocoder = Geocoder(this)
        val list = geocoder.getFromLocation(latLng.latitude, latLng.longitude,1)
        return list[0].getAddressLine(0)
    }

    private fun savePlaces() {
        val latitude = binding.etLatitude.editText?.text.toString()
        val longitude = binding.etLongitude.editText?.text.toString()
        val placesData = PlacesData(latitude,longitude)

        if(TextUtils.isEmpty(dataId)) {
            dataId = db.push().key.toString()
            createNewData(placesData)
        } else {
            updateData(placesData)
        }
        mMap.uiSettings.setAllGesturesEnabled(true)
        toggleButton()
    }
    private fun toggleButton() {
        if(TextUtils.isEmpty(dataId)) binding.btnSave.text = getString(R.string.save_places) else binding.btnSave.text = getString(
            R.string.update_places
        )
    }

    private fun updateData(placesData: PlacesData) {
        if(!TextUtils.isEmpty(placesData.latitude))
            db.child(dataId).child("latitude").setValue(placesData.latitude)

        if(!TextUtils.isEmpty(placesData.longitude))
            db.child(dataId).child("longitude").setValue(placesData.longitude)

        binding.etLatitude.editText?.setText("")
        binding.etLongitude.editText?.setText("")
    }

    private fun createNewData(placesData: PlacesData) {
        db.child(dataId).setValue(placesData).addOnCompleteListener {
            binding.etLatitude.editText?.setText("")
            binding.etLongitude.editText?.setText("")
        }
    }

    private fun getLocationPermission(){
        if (ContextCompat.checkSelfPermission(this.applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            viewModel.permissionGrand(true)
            getDeviceLocation()

        }else{
            Log.d("Exception", "Permission not granted")
        }
    }

    private  fun getDeviceLocation(){
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        try {
            if (viewModel.locationPermissionGranted.value ==true){
                val locationResult = fusedLocationProviderClient.lastLocation

                locationResult.addOnCompleteListener {
                        task ->
                    if (task.isSuccessful){
                        val lastKnownLocation = task.result

                        if (lastKnownLocation != null){
                            viewModel.currentUserGeoCOord(
                                LatLng(
                                    lastKnownLocation.altitude,
                                    lastKnownLocation.longitude
                                )
                            )
                        }
                    }else{
                        Log.d("Exception"," Current User location is null")
                    }
                }

            }

        }catch (e: SecurityException){
            Log.d("Exception", "Exception:  $e.message.toString()")
        }
    }
}

@Composable
fun GoogleMapView2(
    modifier: Modifier,
    cameraPositionState: CameraPositionState,
    onMapLoaded: () -> Unit,
    content: @Composable () -> Unit = {}
) {
    val uwState = rememberMarkerState(position = uw)
    val cfState = rememberMarkerState(position = proofKitchen)
    val fbState = rememberMarkerState(position = splus)
    var circleCenter by remember { mutableStateOf(uw) }
    if (uwState.dragState == DragState.END) {
        circleCenter = uwState.position
    }

    val uiSettings by remember { mutableStateOf(MapUiSettings(compassEnabled = false)) }
    var mapProperties by remember {
        mutableStateOf(MapProperties(mapType = MapType.NORMAL))
    }
    var mapVisible by remember { mutableStateOf(true) }


    if (mapVisible) {
        GoogleMap(
            modifier = modifier,
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            uiSettings = uiSettings,
            onMapLoaded = onMapLoaded,
            onPOIClick = {
                Log.d(TAG, "POI clicked: ${it.name}")
            }
        ) {
            // Drawing on the map is accomplished with a child-based API
            val markerClick: (Marker) -> Boolean = {
                Log.d(TAG, "${it.title} was clicked")
                cameraPositionState.projection?.let { projection ->
                    Log.d(TAG, "The current projection is: $projection")
                }
                false
            }
            MarkerInfoWindowContent(
                state = uwState,
                title = "My Location",
                onClick = markerClick,
                draggable = true,
            ) {
                Text(it.title ?: "Title", color = Color.Red)
            }
            MarkerInfoWindowContent(
                state = cfState,
                title = "Proof Kitchen Restaurant",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE),
                onClick = markerClick,
            ) {
                Text(it.title ?: "Title", color = Color.Blue)
            }
            MarkerInfoWindowContent(
                state = fbState,
                title = "Shawarma Plus Waterloo",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE),
                onClick = markerClick,
            ) {
                Text(it.title ?: "Title", color = Color.Blue)
            }
            Circle(
                center = circleCenter,
                fillColor = MaterialTheme.colors.secondary,
                strokeColor = MaterialTheme.colors.secondaryVariant,
                radius = 1000.0,
            )
            content()
        }

    }
    Column {
        MapTypeControls(onMapTypeClick = {
            Log.d("GoogleMap", "Selected map type $it")
            mapProperties = mapProperties.copy(mapType = it)
        })
        Row {
            MapButton(
                text = "Reset Map",
                onClick = {
                    mapProperties = mapProperties.copy(mapType = MapType.NORMAL)
                    cameraPositionState.position = defaultCameraPosition
                    uwState.position = uw
                    uwState.hideInfoWindow()
                }
            )
            MapButton(
                text = "Toggle Map",
                onClick = { mapVisible = !mapVisible },
                modifier = Modifier.testTag("toggleMapVisibility"),
            )
        }
    }
}

@Composable
private fun MapTypeControls(
    onMapTypeClick: (MapType) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .horizontalScroll(state = ScrollState(0)),
        horizontalArrangement = Arrangement.Center
    ) {
        MapType.values().forEach {
            MapTypeButton(type = it) { onMapTypeClick(it) }
        }
    }
}

@Composable
private fun MapTypeButton(type: MapType, onClick: () -> Unit) =
    MapButton(text = type.toString(), onClick = onClick)

@Composable
private fun MapButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        modifier = modifier.padding(4.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.onPrimary,
            contentColor = MaterialTheme.colors.primary
        ),
        onClick = onClick
    ) {
        Text(text = text, style = MaterialTheme.typography.body1)
    }
}

@Composable
private fun DebugView(
    cameraPositionState: CameraPositionState,
    markerState: MarkerState
) {
    Column(
        Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center
    ) {
        val moving =
            if (cameraPositionState.isMoving) "moving" else "not moving"
        Text(text = "Camera is $moving")
        Text(text = "Camera position is ${cameraPositionState.position}")
        Spacer(modifier = Modifier.height(4.dp))
        val dragging =
            if (markerState.dragState == DragState.DRAG) "dragging" else "not dragging"
        Text(text = "Marker is $dragging")
        Text(text = "Marker position is ${markerState.position}")
    }
}
