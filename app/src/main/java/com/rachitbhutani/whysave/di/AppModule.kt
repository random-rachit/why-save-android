package com.rachitbhutani.whysave.di

import android.app.Application
import androidx.room.Room
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.rachitbhutani.whysave.database.AppDatabase
import com.rachitbhutani.whysave.database.ContactItemDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun provideAppDatabase(context: Application): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "why-save-db").build()
    }

    @Provides
    fun provideContactDao(db: AppDatabase): ContactItemDao {
        return db.contactDao()
    }

    @Provides
    fun provideFirebaseAnalytics() = Firebase.analytics
}