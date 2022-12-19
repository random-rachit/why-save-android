package com.rachitbhutani.whysave.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rachitbhutani.whysave.model.ContactItem

@Database(entities = [ContactItem::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun contactDao(): ContactItemDao
}