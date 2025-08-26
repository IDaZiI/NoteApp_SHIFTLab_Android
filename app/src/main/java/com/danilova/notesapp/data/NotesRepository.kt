package com.danilova.notesapp.data

class NotesRepository(private val dao: NoteDao) {
    suspend fun getAllNotes() = dao.getAllNotes()
    suspend fun addOrUpdateNote(note: NoteEntity) = dao.insert(note)
    suspend fun deleteNote(note: NoteEntity) = dao.delete(note)
}