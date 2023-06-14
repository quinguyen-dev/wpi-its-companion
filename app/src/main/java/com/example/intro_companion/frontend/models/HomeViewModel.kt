package com.example.intro_companion.frontend.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.intro_companion.backend.entities.Ticket
import com.example.intro_companion.backend.repositories.TicketRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val ticketRepository = TicketRepository.get()
    private val _tickets: MutableStateFlow<List<Ticket>> = MutableStateFlow(emptyList())

    val tickets: StateFlow<List<Ticket>>
        get() = _tickets.asStateFlow()

    fun nuke() {
        ticketRepository.nuke()
    }

    init {
        viewModelScope.launch {
            ticketRepository.getTickets().collect {
                _tickets.value = it
            }
        }
    }
}