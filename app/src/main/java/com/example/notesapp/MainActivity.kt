package com.example.notesapp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
class MainActivity :
        ComponentActivity() {

    override fun
            onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database =
            NoteDatabase.getDatabase(this)
        setContent {
            val noteViewModel:
                    NoteViewModel = viewModel(
                factory =
                    NoteViewModelFactory(database.noteDao())
            )
            NotesScreen(noteViewModel)
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(viewModel:
NoteViewModel) {
    val notes by
            viewModel.notes.collectAsState(initial = emptyList())

    var searchText by remember {
        mutableStateOf("")
    }
    var showDialog by remember {
        mutableStateOf(false)
    }
    var selectedNote by remember {
        mutableStateOf<Note?>(null)
    }
    val filteredNotes = notes.filter()
    {
        it.title.contains(searchText,
            true) ||
                it.content.contains(searchText, true)
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Notes",
                        fontWeight =
                            FontWeight.Bold
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    selectedNote =
                        null
                    showDialog = true
                }
            ) {
                Icon(
                    imageVector =
                        Icons.Default.Add,
                    contentDescription = "Add Note"
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = {
                    searchText = it
                },
                modifier =
                    Modifier.fillMaxWidth(),
                placeholder = {
                    Text("Search notes...")
                },
                leadingIcon = {
                    Icon(
                        imageVector =
                            Icons.Default.Search,
                        contentDescription = "Search"
                    )
                },
                singleLine = true,
                shape =
                    RoundedCornerShape(14.dp)
            )
            Spacer(
                modifier =
                    Modifier.height(16.dp)
            )
            if
                    (filteredNotes.isEmpty()) {
                        EmptyNotes()
            } else {
                LazyColumn(
                    verticalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items =
                            filteredNotes,
                        key =
                            { it.id }
                    ) { note ->
                        NoteCard(
                            note =
                                note,
                          onEdit =
                            {
                                selectedNote = note
                                showDialog = true
                            },
                            onDelete =
                                {
                                    viewModel.deleteNote(note)
                                }
                        )
                    }
                }
            }
        }
    }
    if (showDialog) {
        NoteEditorDialog(
            note = selectedNote,
            onDismiss = {
                showDialog = false
            },
            onSave = { title, content
            ->
                if (selectedNote ==
                    null) {
                    viewModel.addNote(title, content)
                } else {
                    viewModel.updateNote(
                        selectedNote!!.copy(
                            title =
                                title,
                            content =
                                content
                        )
                    )
                }
                showDialog = false
            }
        )
    }
}
@Composable
fun NoteCard(
    note: Note,
    onEdit:() -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onEdit()
            },
        shape =
            RoundedCornerShape(18.dp),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 4.dp
            )
    ) {
        Column(
            modifier =
                Modifier.padding(16.dp)
        ) {
            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
Text(
    text = if
                   (note.title.isBlank())
    "Untitled"
    else
    note.title,
    modifier =
        Modifier.weight(1f),
    fontSize = 18.sp,
    fontWeight =
        FontWeight.Bold
)
                IconButton(
                    onClick = onEdit
                ) {
                    Icon(
                        imageVector =
                            Icons.Default.Edit,
                        contentDescription = "Edit"
                    )
                }
                IconButton(
                    onClick =
                        onDelete
                ) {
                    Icon(
                        imageVector =
                            Icons.Default.Delete,
                        contentDescription = "Delete"
                    )
                }
            }
            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )
            Text(
                text = note.content,
                fontSize = 15.sp,
                maxLines = 4
            )
        }
    }
}
@Composable
fun EmptyNotes() {
    Box(
        modifier =
            Modifier.fillMaxSize(),
        contentAlignment =
            Alignment.Center
    ) {
        Column(
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {
            Text(
                text = "No Notes Yet",
                fontSize = 22.sp,
                fontWeight =
                    FontWeight.Bold
            )
            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )
            Text(
                text = "Tap + to create your first note.",
                fontSize = 15.sp
            )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorDialog(
    note: Note?,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var title by remember {
        mutableStateOf(note?.title ?:"")
    }
    var content by remember {
        mutableStateOf(note?.content ?:"")
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (note ==
                    null)
                "Create Note"
                else
                "Edit Note"
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                    },
                    modifier =
                        Modifier.fillMaxWidth(),
                    label = {
                        Text("Title")
                    },
                    singleLine = true
                )
                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )
                OutlinedTextField(
                    value = content,
                    onValueChange = {
                        content = it
                    },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                    label = {
                        Text("Write your note...")
                    }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(title,
                        content)
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}



