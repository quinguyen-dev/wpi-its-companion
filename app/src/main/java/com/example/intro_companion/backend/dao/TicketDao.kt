package com.example.intro_companion.backend.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.intro_companion.backend.entities.Ticket
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface TicketDao {

    @Query("SELECT * FROM ticket")
    fun getTickets(): Flow<List<Ticket>>

    @Query("SELECT * FROM ticket WHERE id=(:id)")
    suspend fun getTicket(id: UUID): Ticket

    @Update
    suspend fun updateTicket(ticket: Ticket)

    @Insert
    suspend fun addTicket(ticket: Ticket)

    @Query("DELETE FROM ticket")
    fun nukeTable()
}