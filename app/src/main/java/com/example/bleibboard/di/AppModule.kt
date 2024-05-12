package com.example.bleibboard.di

import AthleteTestsRepository
import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.bleibboard.data.local.TestDatabase
import com.example.bleibboard.data.local.TestsDao
import com.example.bleibboard.data.remote.ble.BLEDeviceConnection
import com.example.bleibboard.data.remote.ble.BLEScanner
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideTestDatabase(@ApplicationContext appContext: Context): TestDatabase {
        return Room.databaseBuilder(
            appContext,
            TestDatabase::class.java,
            "test_database"
        ).build(
        )
    }

    @Provides
    @Singleton
    fun provideTestsDao(database: TestDatabase) : TestsDao {
        return database.dao
    }

    @Provides
    @Singleton
    fun provideBLEScanner(@ApplicationContext appContext: Context) : BLEScanner {
        return BLEScanner(appContext)
    }

    @Provides
    @Singleton
    fun provideActiveConnection(): MutableStateFlow<BLEDeviceConnection?> {
        return MutableStateFlow(null)
    }


}