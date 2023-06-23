package com.rachitbhutani.whysave.database

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@ProvidedTypeConverter
class Converters {

    @TypeConverter
    fun fromJsonToLogList(json: String?): List<Long> {
        return json?.let {
            val listType = object : TypeToken<List<Long>?>() {}.type
            Gson().fromJson(json, listType)
        } ?: emptyList()
    }

    @TypeConverter
    fun fromLogList(list: List<Long>): String {
        return Gson().toJson(list)
    }

}