package com.example.notesapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class NoteViewModel(
    private val noteDao: NoteDao
) : ViewModel() {

    val notes: Flow<List<Note>> = noteDao.getAllNotes()

    fun addNote(title: String, content: String) {
        if (title.isBlank() && content.isBlank()) return

        viewModelScope.launch {
            noteDao.insertNote(
                Note(
                    title = title,
                    content = content
                )
            )
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            noteDao.deleteNote(note)
        }
    }
    fun updateNote(note: Note) {
        viewModelScope.launch {
            noteDao.updateNote(note)
        }
    }
}
class NoteViewModelFactory(
    private val noteDao: NoteDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
         if
          (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
        @Suppress("UNCHECKED_CAST")
        return NoteViewModel(noteDao) as T
    }

    throw IllegalArgumentException("Unknown ViewModel class")
}
}



