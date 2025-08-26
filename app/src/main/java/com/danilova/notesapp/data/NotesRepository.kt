package com.danilova.notesapp.data

class NotesRepository(private val dao: NoteDao) {
    suspend fun getAllNotes() = dao.getAllNotes()

    suspend fun addNote(note: NoteEntity): Long = dao.insert(note)

    suspend fun updateNote(note: NoteEntity) = dao.update(note)

    suspend fun deleteNote(note: NoteEntity) = dao.delete(note)


}
