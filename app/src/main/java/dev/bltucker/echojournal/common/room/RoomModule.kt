package dev.bltucker.echojournal.common.room

import android.app.Application
import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Provides
    @Singleton
    fun provideEchoJournalDatabase(@ApplicationContext context: Context): EchoJournalDatabase {
        return Room.databaseBuilder(
            context,
            EchoJournalDatabase::class.java,
            EchoJournalDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideJournalEntryDao(database: EchoJournalDatabase): JournalEntryDao {
        return database.journalEntryDao()
    }

    @Provides
    @Singleton
    fun provideTopicDao(database: EchoJournalDatabase): TopicDao {
        return database.topicDao()
    }
}