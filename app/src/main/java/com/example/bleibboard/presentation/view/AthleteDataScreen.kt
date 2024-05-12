package com.example.bleibboard.presentation.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bleibboard.data.local.SortType
import com.example.bleibboard.data.local.TestEvent
import com.example.bleibboard.presentation.viewmodel.AthleteDataViewmodel
import com.example.bleibboard.presentation.state.TestListState

@Composable
fun AthleteDataScreen(
) {

    val viewmodel : AthleteDataViewmodel = hiltViewModel()
    val state by viewmodel.state.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewmodel.onEvent(TestEvent.ShowDialog)
            }) {
                Icon(
                    imageVector =  Icons.Default.Add,
                    contentDescription = "Add Test"
                )
            }

        }
    ) { padding ->
        if(state.isAddingContact) {
            AddTestDialog(
                state = state,
                onEvent = viewmodel::onEvent
            )
        }
        LazyColumn (
            contentPadding = padding,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SortType.values().forEach { sortType ->
                        Row(
                            modifier = Modifier
                                .clickable {
                                    viewmodel.onEvent(TestEvent.SortTests(sortType))
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = state.sortType == sortType,
                                onClick = {
                                    viewmodel.onEvent(TestEvent.SortTests(sortType))
                                }
                            )
                            Text(
                                text = sortType.name)
                        }
                    }
                }
            }
            items(state.tests) { test ->
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "${test.firstName} ${test.lastName}",
                            fontSize = 20.sp
                        )
                        Text(
                            text = "${test.date} ${test.time}",
                            fontSize = 14.sp
                        )
                    }
                    IconButton(onClick = {
                        viewmodel.onEvent(TestEvent.DeleteTest(test))
                    }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Test"
                        )
                    }
                }
            }
        }

    }
}

@Composable
fun AddTestDialog(
    state: TestListState,
    onEvent: (TestEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = {
            onEvent(TestEvent.HideDialog)
        },
        title = { Text(text = "Add Test") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextField(
                    value = state.firstName,
                    onValueChange = {
                        onEvent(TestEvent.SetFirstName(it))
                    },
                    placeholder = {
                        Text(text = "First Name")
                    }
                )
                TextField(
                    value = state.lastName,
                    onValueChange = {
                        onEvent(TestEvent.SetLastName(it))
                    },
                    placeholder = {
                        Text(text = "Last Name")
                    }
                )
                TextField(
                    value = state.date,
                    onValueChange = {
                        onEvent(TestEvent.SetDate(it))
                    },
                    placeholder = {
                        Text(text = "Date")
                    }
                )
                TextField(
                    value = state.time,
                    onValueChange = {
                        onEvent(TestEvent.SetTime(it))
                    },
                    placeholder = {
                        Text(text = "Time")
                    }
                )
            }
        },
        confirmButton = {
            Box(modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd) {
                Button(
                    onClick = {
                        onEvent(TestEvent.SaveTest)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Save")
                }
            }
        }
    )
}