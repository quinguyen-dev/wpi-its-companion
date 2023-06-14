package com.example.intro_companion.frontend.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.intro_companion.backend.entities.Ticket
import com.example.intro_companion.backend.repositories.TicketRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class TicketViewModel(id: UUID) : ViewModel() {
    private val ticketRepository = TicketRepository.get()

    private val _ticket: MutableStateFlow<Ticket?> = MutableStateFlow(null)
    val ticket: StateFlow<Ticket?> = _ticket.asStateFlow()

    init {
        viewModelScope.launch {
            _ticket.value = ticketRepository.getTicket(id)
        }
    }
}

class TicketViewModelFactory(
    private val id: UUID
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TicketViewModel(id) as T
    }
}