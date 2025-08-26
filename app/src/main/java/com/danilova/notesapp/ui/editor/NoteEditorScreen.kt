package com.danilova.notesapp.ui.editor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.danilova.notesapp.ui.model.NoteUi
import android.content.Context
import androidx.compose.material.icons.filled.Done
import androidx.core.content.edit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorScreen(
    note: NoteUi? = null,
    onBackClick: () -> Unit,
    onSaveClick: (String, String) -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("notes_draft", Context.MODE_PRIVATE) }

    var title by remember {
        mutableStateOf(
            note?.title ?: prefs.getString("draft_title", "") ?: ""
        )
    }
    var content by remember {
        mutableStateOf(
            note?.content ?: prefs.getString("draft_content", "") ?: ""
        )
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (note != null) "Редактировать заметку" else "Новая заметка") },
                navigationIcon = {
                    IconButton(onClick = {
                        prefs.edit {
                            putString("draft_title", title)
                                .putString("draft_content", content)
                        }
                        onBackClick()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                onSaveClick(title, content)
                prefs.edit {
                    remove("draft_title")
                        .remove("draft_content")
                }
                keyboardController?.hide()
            }) {
                Icon(Icons.Default.Done, contentDescription = "Сохранить")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
        ) {
            item {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Заголовок") },
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                )
            }
            item {
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Содержание") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
    }
}