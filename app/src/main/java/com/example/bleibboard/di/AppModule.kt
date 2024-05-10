package com.example.bleibboard.di

import AthleteTestsRepository
import android.content.Context
import androidx.room.Room
import com.example.bleibboard.data.local.TestDatabase
import com.example.bleibboard.data.local.TestsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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


    /*
    @Provides
    @Singleton
    fun provideAthleteTestsRepository(dao: TestsDao): AthleteTestsRepository {
        return AthleteTestsRepository(dao)
    }
    \
     */


}