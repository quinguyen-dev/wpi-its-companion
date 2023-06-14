package com.example.intro_companion

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.*
import kotlin.collections.ArrayList

class GeoFencingBroadcastReceiver : BroadcastReceiver() {
    private val TAG = "GeoFence"

    override fun onReceive(context: Context, intent: Intent) {
        Log.e("Received", "onReceive was called")
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent?.hasError() == true) {
            val errorMessage = GeofenceStatusCodes
                .getStatusCodeString(geofencingEvent.errorCode)
            Log.e(TAG, errorMessage)
            return
        }

        // Get the transition type.
        val geofenceTransition = geofencingEvent?.geofenceTransition

        //If the device has dwelled at a location
        //This is only applied to campus buildings
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {
            val triggeringGeofences = geofencingEvent.triggeringGeofences

            triggeringGeofences?.forEach {
                Toast.makeText(context, it.requestId, Toast.LENGTH_LONG)
                    .show()
                Log.e("Dwell", it.requestId)
                //Update the current building that the device is in
                GeoFences.currentBuilding = it.requestId
                GeoFences.dwellInitTime = System.currentTimeMillis().toDouble()
            }

            // Get the transition details as a String.
            val geofenceTransitionDetails = getGeofenceTransitionDetails(geofencingEvent)
            if (geofenceTransitionDetails != null) {
                Log.i(TAG, geofenceTransitionDetails)
            }
        }
        //If the phone enters a fenced area
        //Only applied to the campus area
        else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            //this determines that the phone is on campus but may not be in a particular building

            Log.i(TAG, "Entered \n" + GeofencingEvent.fromIntent(intent)
                ?.let { getGeofenceTransitionDetails(it) })
            //Sets the campus time tracker and the current campus region
            geofencingEvent.triggeringGeofences?.forEach {
                if(GeoFences.campusRegion == "")
                    GeoFences.onCampusStart = System.currentTimeMillis().toDouble()
                if(it.requestId.contains("campus"))
                    GeoFences.campusRegion = it.requestId
                else if(GeoFences.currentBuilding == "")
                    GeoFences.currentBuilding = it.requestId
            }
            //if you leave a building or a campus region
        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            Log.e(TAG, "Entered \n" + GeofencingEvent.fromIntent(intent)
                ?.let { getGeofenceTransitionDetails(it) })

            geofencingEvent.triggeringGeofences?.forEach {
                if (it.requestId == GeoFences.currentBuilding) {
                    GeoFences.currentBuilding = ""
                } else if (it.requestId == GeoFences.campusRegion) {
                    GeoFences.campusRegion = ""
                }
            }
        } else {
            // Log the error.
            Log.e(TAG, geofenceTransition.toString())
        }
    }

    /**
     * Generates a string describing a geofence transition event and the IDs of the triggering geofences.
     *
     * @param event The GeofencingEvent object representing the transition event.
     * @return A string describing the transition and the IDs of the triggering geofences.
     */
    private fun getGeofenceTransitionDetails(event: GeofencingEvent): String? {
        val transitionString: String
        val c: Calendar = Calendar.getInstance()
        val df = SimpleDateFormat("mm-ss")
        val formattedDate: String = df.format(c.getTime())
        val geofenceTransition = event.geofenceTransition
        transitionString = if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            "IN-$formattedDate"
        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            "OUT-$formattedDate"
        } else {
            "OTHER-$formattedDate"
        }
        val triggeringIDs: MutableList<String?>
        triggeringIDs = ArrayList()
        for (geofence in event.triggeringGeofences!!) {
            triggeringIDs.add(geofence.requestId)
        }
        return String.format("%s: %s", transitionString, TextUtils.join(", ", triggeringIDs))
    }
}