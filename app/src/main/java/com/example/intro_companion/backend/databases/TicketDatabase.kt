package com.example.intro_companion.backend.databases

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.intro_companion.backend.converters.TypeConverter
import com.example.intro_companion.backend.dao.TicketDao
import com.example.intro_companion.backend.entities.Ticket

@Database(entities = [Ticket::class], version = 1)
@TypeConverters(TypeConverter::class)
abstract class TicketDatabase : RoomDatabase() {
    abstract fun ticketDao(): TicketDao
}