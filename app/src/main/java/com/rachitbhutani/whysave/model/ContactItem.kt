package com.rachitbhutani.whysave.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class ContactItem(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val phone: String?,
    val timestamp: Long?,
    val logList: List<Long>?,
    val note: String? = null
)