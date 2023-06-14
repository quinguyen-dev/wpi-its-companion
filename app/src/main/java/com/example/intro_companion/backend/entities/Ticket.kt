package com.example.intro_companion.backend.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

/**
 * Entity class representing a ticket in a helpdesk system.
 * @property id The unique identifier for the ticket.
 * @property description The description of the problem reported in the ticket.
 * @property submittedDate The date the ticket was submitted.
 * @property classification The classification of the problem reported in the ticket.
 * @property location The location of the user who submitted the ticket.
 * @property appointment The date the user has scheduled an appointment.
 * @property resolved A boolean value indicating whether the problem reported in the ticket has been resolved.
 */
@Entity
data class Ticket(
    @PrimaryKey val id: UUID,
    val description: String,
    val submittedDate: Date,
    val classification: String,
    val location: String,
    val appointment: Date,
    val resolved: Boolean
)

