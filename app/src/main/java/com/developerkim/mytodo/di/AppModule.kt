package com.developerkim.mytodo.di

import android.content.Context
import com.developerkim.mytodo.NotesApp
import com.developerkim.mytodo.data.database.NoteCategoriesDao
import com.developerkim.mytodo.data.database.NoteDatabase
import com.developerkim.mytodo.data.repository.NotesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context):NoteDatabase{
        return NoteDatabase.getInstance(context)
    }

    @Singleton
    @Provides
    fun provideDao(noteDatabase: NoteDatabase):NoteCategoriesDao{
        return noteDatabase.notesCategoriesDao
    }

    @Singleton
    @Provides
    fun provideRepository(noteDatabase: NoteDatabase):NotesRepository{
        return NotesRepository(noteDatabase.notesCategoriesDao)
    }
}