package com.example.bleibboard.ui.screens

import android.graphics.drawable.Icon
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bleibboard.data.local.SortType
import com.example.bleibboard.data.local.TestEvent
import com.example.bleibboard.ui.components.AddTestDialog
import com.example.bleibboard.ui.state.TestListState
import com.example.bleibboard.ui.viewmodels.TestListViewModel

@Composable
fun AthleteDataScreen(
    viewModel: TestListViewModel,
    onEvent: (TestEvent) -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                onEvent(TestEvent.ShowDialog)
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
                onEvent = onEvent
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
                                    onEvent(TestEvent.SortTests(sortType))
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = state.sortType == sortType,
                                onClick = {
                                    onEvent(TestEvent.SortTests(sortType))
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
                        onEvent(TestEvent.DeleteTest(test))
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