package dev.bltucker.echojournal.common

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Provides
    @Named("AudioDirectory")
    fun provideAudioDirectory(@ApplicationContext context: Context): File {
        return File(context.filesDir, "audio")
    }
}