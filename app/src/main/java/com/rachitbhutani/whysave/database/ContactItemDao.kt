package com.rachitbhutani.whysave.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rachitbhutani.whysave.model.ContactItem

@Dao
interface ContactItemDao {

    @Query("SELECT * FROM contacts")
    fun getAllContacts(): List<ContactItem>

    @Query("SELECT * FROM contacts where phone LIKE :phone")
    fun findContact(phone: String): ContactItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertContact(contactItem: ContactItem)

    @Delete
    fun delete(contact: ContactItem)
}