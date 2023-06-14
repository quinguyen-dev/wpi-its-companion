package com.example.intro_companion.frontend.models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.intro_companion.GeoFences
import com.example.intro_companion.backend.entities.Request
import com.example.intro_companion.backend.entities.Ticket
import com.example.intro_companion.backend.repositories.TicketRepository
import java.util.Date
import java.util.UUID

class RequestViewModel : ViewModel() {

    private val _request = MutableLiveData(Request("", "", "", "", "", "", false, ""))
    private val ticketRepository = TicketRepository.get()
    private var _classification = ""

    val request: LiveData<Request> = _request

    fun setRequest(request: Request) {
        _request.value = request
    }

    fun setClassification(classification: String) {
        _classification = classification
    }

    suspend fun addRequest() {
        val ticket =
            _request.value?.let {
                Ticket(
                    id = UUID.randomUUID(),
                    description = it.description,
                    submittedDate = Date(),
                    classification = _classification,
                    location = GeoFences.getDwellTimeString(),
                    appointment = Date(),
                    resolved = false
                )
            }

        if (ticket != null) {
            ticketRepository.addTicket(ticket)
        }
    }
}