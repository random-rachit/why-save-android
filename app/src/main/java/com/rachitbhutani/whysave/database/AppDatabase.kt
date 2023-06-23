package com.rachitbhutani.whysave.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rachitbhutani.whysave.model.ContactItem

@Database(
    entities = [ContactItem::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactItemDao
}