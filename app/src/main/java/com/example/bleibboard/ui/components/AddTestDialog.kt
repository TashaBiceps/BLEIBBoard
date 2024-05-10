package com.example.bleibboard.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bleibboard.data.local.TestEvent
import com.example.bleibboard.ui.state.TestListState

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