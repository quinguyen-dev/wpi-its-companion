package com.example.intro_companion

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import java.util.*

class GeoFences private constructor() {

    /* The primary activity */
    private var mainActivity: MainActivity? = null;

    /* Boolean for if the app has location permissions */
    private var hasLocationPermission = false

    /* Client for location handling */
    private lateinit var locationClient: FusedLocationProviderClient

    /* Current location of the user */
    private lateinit var currentLocation: Location



    /**
     * Setup the fences and configure the attributes
     */
    @SuppressLint("MissingPermission")
    fun initializeFences(client: GeofencingClient, activity: MainActivity) {
        /* Assign attributes */
        mainActivity = activity;
        locationClient = LocationServices.getFusedLocationProviderClient(mainActivity!!)

        /* Request location permissions */
        getLocationPermission()

        /* Determine the current location of the user */
        determineLocation()

        /* Create a pending intent for the geofences */
        getGeoFencePendingIntent(activity)

        val geoRequest = GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofences(getGeofenceList())
        }.build()

        if (hasLocationPermission) {
            client.addGeofences(geoRequest, getGeoFencePendingIntent(activity)).run {
                addOnSuccessListener {
                    Toast.makeText(activity, "Successful geoFence", Toast.LENGTH_LONG)
                        .show()
                }
                addOnFailureListener {
                    Toast.makeText(activity, "Failed to geoFence", Toast.LENGTH_LONG)
                        .show()
                    it.message?.let { it1 -> Log.e("Failed GeoFence", it1) }
                }
            }
        }
    }

    /**
     * Generate the pending intent for the geofences
     */
    private fun getGeoFencePendingIntent(activity: MainActivity): PendingIntent {
        val intent = Intent(activity, GeoFencingBroadcastReceiver::class.java)
        return PendingIntent.getBroadcast(activity, 123, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    /**
     * Generate the list of geofences for both campus building and campus as a whole
     */
    private fun getGeofenceList(): List<Geofence> {
        val geofenceList = mutableListOf<Geofence>()

        val listOfBuildings = listOf(
            listOf("fuller_labs", 42.27505615486682, -71.80635424133574, 33f),
            listOf("gordon_library", 42.274242, -71.806351, 29f),
            listOf("salisbury_lab", 42.274638, -71.807051, 33f),
            listOf("atwater_kent", 42.275305, -71.807008, 36f),
            listOf("inno_studio", 42.27432069253115, -71.80881742975608, 14f),
            listOf("olin_hall", 42.275188, -71.807893, 15f),
            listOf("olin_hall1", 42.274980, -71.807939, 15f),
            listOf("olin_hall2", 42.274802, -71.807970, 15f),
            listOf("townhouses", 42.279650, -71.807255, 132.39f),
            listOf("global_project_center", 42.274240, -71.807558, 15.19f),
            listOf("higgins_lab_north", 42.274296, -71.808194, 23.59f),
            listOf("higgins_lab_south", 42.274037, -71.808266, 20.06f),
        )

        listOfBuildings.forEach {
            geofenceList.add(
                Geofence.Builder()
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setRequestId(it[0] as String)
                    .setCircularRegion(it[1] as Double, it[2] as Double, it[3] as Float)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_DWELL or Geofence.GEOFENCE_TRANSITION_EXIT)
                    .setLoiteringDelay(1000)
                    .build()
            )
        }

        val listOfCampusBounds = listOf(
            listOf("campus_central", 42.274855, -71.808135, 370.99f),
            listOf("campus_sw", 42.272608, -71.811611, 141.23f),
            listOf("campus_e", 42.273394, -71.804959, 167.74f),
            listOf("campus_n", 42.279650, -71.807255, 132.39f)
        )

        listOfCampusBounds.forEach {
            geofenceList.add(
                Geofence.Builder()
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setRequestId(it[0] as String)
                    .setCircularRegion(it[1] as Double, it[2] as Double, it[3] as Float)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build()
            )
        }
        return geofenceList;
    }


    /**
     * Checks if the app has permissions to access the device's location.
     */
    private fun checkContextPermissions(): Boolean {
        return mainActivity?.applicationContext?.let {
            ContextCompat.checkSelfPermission(
                it, Manifest.permission.ACCESS_FINE_LOCATION
            )
        } == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            mainActivity!!.applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Ensure the location permissions are valid
     */
    private fun getLocationPermission() {
        if (checkContextPermissions()) {
            hasLocationPermission = true
        } else {
            /* Prompt the user for permissions */
            mainActivity?.let {
                ActivityCompat.requestPermissions(
                    it,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
                )
            }
        }
    }


    /**
     * Periodically calls for locations so the system will call the geofence
     */
    private fun determineLocation() {
        try {
            /* If the user has granted location permissions */
            if (hasLocationPermission) {
                mainActivity?.let {
                    locationClient.lastLocation.addOnCompleteListener(it) { location ->

                        if (location.isSuccessful) {
                            currentLocation = location.result

                            /* Create a location request system that handles updating the user location */
                            val locationRequest =
                                LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
                                    .setWaitForAccurateLocation(false)
                                    .setMinUpdateIntervalMillis(500)
                                    .setMaxUpdateDelayMillis(1000).build()

                            /* Callback function that sets the current location to the last location updated */
                            val locationCallback = object : LocationCallback() {
                                override fun onLocationResult(locationResult: LocationResult) {
                                    currentLocation = locationResult.lastLocation!!
                                }
                            }

                            /* Add location updates to the location client */
                            locationClient.requestLocationUpdates(
                                locationRequest, locationCallback, null
                            )
                        }
                    }.addOnFailureListener { Log.e("LOCATION FAILED", "Failed") }
                }
            }
        } catch (e: SecurityException) {
            Log.e("[SECURITY EXCEPTION]: %s", e.message, e)
        }
    }


    companion object {
        var onCampusStart:Double = 0.0
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1

        private var _instance: GeoFences = GeoFences();

        //singleton
        val instance: GeoFences
            get() = synchronized(this) {
                _instance
            }

        //
        var dwellInitTime:Double = 0.0
        //empty string is not in a building, otherwise its a campus building
        var currentBuilding: String = ""

        //Returns formatted string of campus/building dwell time for ticket tracking
        fun getDwellTimeString():String{
            var durationSec:Double =0.0
            if(currentBuilding != ""){
                durationSec = ((System.currentTimeMillis()- dwellInitTime)/1000)
                return "You were at ${currentBuilding} for ${(durationSec/60).toInt()}:${((durationSec%60)/60).toInt()}"
            }
            else{
                if(campusRegion != ""){
                    durationSec = ((System.currentTimeMillis()- onCampusStart)/1000)
                    return "You were on campus for ${(durationSec/60).toInt()} minutes and ${((durationSec%60)/60).toInt()} seconds"
                }
                else{
                   return "Not on campus at time of submission"
                }
            }
        }


        //empty string represents off campus, otherwise on campus
        var campusRegion: String = ""
    }
}