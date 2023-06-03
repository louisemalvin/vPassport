package com.example.vpassport.view.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.vpassport.util.events.HistoryEvent
import com.example.vpassport.util.states.HistoryState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmationDialog(
    state: HistoryState,
    onEvent: (HistoryEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = { onEvent(HistoryEvent.HideDialog) }) {
        Column {
            Text(text = "Confirm?")
            Button(onClick = {
                onEvent(HistoryEvent.SetIsAllowed(true))
                onEvent(HistoryEvent.SetSite("test.com"))
                onEvent(HistoryEvent.AddHistory)
            }) {
                Text(text = "Yes.")
            }
        }
    }
}