package com.danilova.notesapp.data

class NotesRepository(private val dao: NoteDao) {
    suspend fun getAllNotes() = dao.getAllNotes()
    suspend fun addOrUpdateNote(note: NoteEntity): Long? {
        return if (note.id == 0L) dao.insert(note) else { dao.insert(note); note.id }
    }
    suspend fun deleteNote(note: NoteEntity) = dao.delete(note)
}
