package com.danilova.notesapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.danilova.notesapp.ui.editor.NoteEditorScreen
import com.danilova.notesapp.ui.list.NotesListScreen
import com.danilova.notesapp.ui.list.NotesViewModel
import com.danilova.notesapp.ui.list.NotesViewModelFactory
import com.danilova.notesapp.data.AppDatabase
import com.danilova.notesapp.data.NotesRepository
import com.danilova.notesapp.ui.detail.NoteDetailScreen
import com.danilova.notesapp.ui.theme.NotesAppTheme
import androidx.compose.runtime.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dao = AppDatabase.getDatabase(applicationContext).noteDao()
        val repository = NotesRepository(dao)
        setContent {
            NotesAppTheme(dynamicColor = false){
                NotesApp(repository = repository)
            }
        }
    }
}

@Composable
fun NotesApp(repository: NotesRepository) {
    val viewModel: NotesViewModel = viewModel(
        factory = NotesViewModelFactory(repository)
    )

    val navController = rememberNavController()
    var isDarkTheme by remember { mutableStateOf(false) }

    NotesAppTheme(darkTheme = isDarkTheme, dynamicColor = false) {
        NavHost(navController = navController, startDestination = "list") {
            composable("list") {
                NotesListScreen(
                    notes = viewModel.notes,
                    onAddClick = { navController.navigate("editor/-1") },
                    onNoteClick = { note -> navController.navigate("detail/${note.id}") },
                    onDeleteClick = { note -> viewModel.deleteNote(note.id) },
                    isDarkTheme = isDarkTheme,
                    onThemeToggle = { isDarkTheme = it }
                )
            }

            composable(
                "detail/{noteId}",
                arguments = listOf(navArgument("noteId") { defaultValue = -1L })
            ) { backStackEntry ->
                val noteId = backStackEntry.arguments?.getLong("noteId") ?: -1L
                val note = viewModel.notes.find { it.id == noteId } ?: return@composable
                NoteDetailScreen(
                    note = note,
                    onEditClick = { navController.navigate("editor/${note.id}") },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                "editor/{noteId}",
                arguments = listOf(navArgument("noteId") { defaultValue = -1L })
            ) { backStackEntry ->
                val noteId = backStackEntry.arguments?.getLong("noteId") ?: -1L
                val note = viewModel.notes.find { it.id == noteId }
                NoteEditorScreen(
                    note = note,
                    onBackClick = { navController.popBackStack() },
                    onSaveClick = { title, content ->
                        if (note != null) {
                            viewModel.updateNote(note.id, title, content)
                        } else {
                            viewModel.addNote(title, content)
                        }
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}