package com.example.intro_companion.backend.repositories

import android.content.Context
import androidx.room.Room
import com.example.intro_companion.backend.databases.TicketDatabase
import com.example.intro_companion.backend.entities.Ticket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.launch
import java.util.UUID

class TicketRepository private constructor(
    context: Context,
    private val coroutineScope: CoroutineScope = GlobalScope
) {

    /**
     * A companion object for the [TicketRepository] class.
     *
     * This object provides two functions and a private constant that can be used to initialize
     * and retrieve an instance of the [TicketRepository] class.
     */
    companion object {
        private const val DATABASE_NAME = "ticket-database"
        private var instance: TicketRepository? = null

        /**
         * Initializes the [instance] of the [TicketRepository] class with a new instance
         * if it hasn't already been initialized.
         *
         * This function should be called before calling [get], in order to ensure that an
         * instance of the repository exists.
         *
         * @param context The application context used to initialize the repository.
         */
        fun initialize(context: Context) {
            if (instance == null) instance = TicketRepository(context)
        }

        /**
         * Retrieves the singleton instance of the [TicketRepository] class.
         *
         * If the [instance] has not been initialized, this function throws an
         * [IllegalStateException].
         *
         * @return An instance of the [TicketRepository] class.
         * @throws IllegalStateException If the [instance] has not been initialized.
         */
        fun get(): TicketRepository {
            return instance ?: throw IllegalStateException("TicketRepository is not initialized")
        }
    }

    /**
     * Initializes a private [database] property with an instance of [TicketDatabase].
     *
     * The [TicketDatabase] instance is created using the [Room.databaseBuilder] method,
     * which creates a new instance of the database if it doesn't already exist, or opens
     * an existing database if one already exists. The database is created in the application
     * context to ensure that it persists across configuration changes and doesn't leak memory.
     *
     * @return An instance of [TicketDatabase] that can be used to access the database.
     */
    private val database: TicketDatabase = Room
        .databaseBuilder(
            context.applicationContext,
            TicketDatabase::class.java,
            DATABASE_NAME
        )
        .build()

    /* ========================== FACADE METHODS ========================== */

    /**
     * Returns a [Flow] that emits a list of [Ticket] objects from the database.
     *
     * The [Flow] will emit the current list of tickets when it is first collected,
     * and then emit any updates to the list as they are made to the database.

     *
     * @return A [Flow] that emits a list of [Ticket] objects from the database.
     */
    fun getTickets(): Flow<List<Ticket>> = database.ticketDao().getTickets()

    /**
     * Retrieves a single [Ticket] object from the database by its [id].
     *
     * This function is marked as `suspend` because it likely performs a time-consuming
     * operation, such as accessing a database or performing a network call.
     *
     * @param id The [id] of the ticket to retrieve.
     * @return The [Ticket] object with the specified [id].
     */
    suspend fun getTicket(id: UUID): Ticket = database.ticketDao().getTicket(id)

    /**
     * Updates the specified [ticket] object in the database.
     *
     * @param ticket The [Ticket] object to update in the database.
     */
    fun updateTicket(ticket: Ticket) {
        coroutineScope.launch {
            database.ticketDao().updateTicket(ticket)
        }
    }

    /**
     * Adds a new [ticket] object to the database.
     *
     * Note that this function is designed to be used with coroutines, and should be
     * called from a coroutine or another suspend function. The insert operation is
     * performed in a suspend function, which allows it to be performed on a background
     * thread without blocking the main thread.
     *
     * @param ticket The [Ticket] object to add to the database.
     */
    suspend fun addTicket(ticket: Ticket) {
        database.ticketDao().addTicket(ticket)
    }

    fun nuke() {
        database.ticketDao().nukeTable()
    }

}