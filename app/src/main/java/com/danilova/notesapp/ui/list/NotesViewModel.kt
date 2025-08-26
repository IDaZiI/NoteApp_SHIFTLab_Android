package com.danilova.notesapp.ui.list

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danilova.notesapp.ui.model.NoteUi
import com.danilova.notesapp.data.NoteEntity
import com.danilova.notesapp.data.NotesRepository
import kotlinx.coroutines.launch

class NotesViewModel(
    private val repository: NotesRepository
) : ViewModel() {

    private val _notes = mutableStateListOf<NoteUi>()
    val notes: List<NoteUi> = _notes

    init {
        viewModelScope.launch {
            repository.getAllNotes().forEach { note ->
                _notes.add(NoteUi(note.id, note.title, note.content))
            }
        }
    }

    fun addNote(title: String, content: String) {
        val newNote = NoteUi(System.currentTimeMillis(), title, content)
        _notes.add(0, newNote)
        viewModelScope.launch {
            repository.addOrUpdateNote(
                NoteEntity(newNote.id, title, content)
            )
        }
    }

    fun updateNote(id: Long, title: String, content: String) {
        val index = _notes.indexOfFirst { it.id == id }
        if (index != -1) {
            _notes[index] = _notes[index].copy(title = title, content = content)
            viewModelScope.launch {
                repository.addOrUpdateNote(
                    NoteEntity(id, title, content)
                )
            }
        }
    }

    fun deleteNote(id: Long) {
        val index = _notes.indexOfFirst { it.id == id }
        if (index != -1) {
            val note = _notes.removeAt(index)
            viewModelScope.launch {
                repository.deleteNote(
                    NoteEntity(note.id, note.title, note.content)
                )
            }
        }
    }
}
