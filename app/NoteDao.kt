package com.example.notesapp
import androidx.room.*
@Dao
interface NoteDao {

    @Query("SECELT * FROM Note")
    fun getAll(): List<Note>

    @Insert
    fun insert(note: Note)

    @Delete
    fun delete(note: Note)
}