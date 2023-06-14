package com.example.intro_companion

import android.app.Application
import com.example.intro_companion.backend.repositories.TicketRepository

class MainApplication : Application() {

    /**
     * Called when the activity is starting. This is where most initialization of the activity should go.
     *
     * This method calls the parent implementation of `onCreate()`, which performs the standard initialization of the activity.
     */
    override fun onCreate() {
        super.onCreate()
        TicketRepository.initialize(this)
    }

}