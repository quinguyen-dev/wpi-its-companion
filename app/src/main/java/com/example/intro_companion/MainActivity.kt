package com.example.intro_companion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import com.example.intro_companion.databinding.ActivityMainBinding
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {

    /**
     * Called when the activity is starting. This is where most initialization of the activity should go.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut
     * down then this Bundle contains the data it most recently supplied in `onSaveInstanceState(Bundle)`.
     */
    private lateinit var binding: ActivityMainBinding
    private lateinit var geofence: GeofencingClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* Remove the app header bar */
        supportActionBar?.hide()

        /* Create geofence monitors */
        geofence = LocationServices.getGeofencingClient(this@MainActivity)
        GeoFences.instance.initializeFences(geofence, this)

        /* Inflate the view */
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}
