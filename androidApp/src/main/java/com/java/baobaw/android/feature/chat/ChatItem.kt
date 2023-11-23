package com.java.baobaw.android.feature.chat

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.java.baobaw.android.BaseView
import com.java.baobaw.feature.chatt_detail.ChatMessage
import com.java.baobaw.feature.chatt_detail.ChatViewModel
import kotlinx.coroutines.CoroutineScope
import androidx.compose.material.TextField
import androidx.compose.material.Button
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChatScreen(
    chatViewModel: ChatViewModel,
    scope: CoroutineScope = rememberCoroutineScope(),
    navController: NavController
){

    var chatState by remember { mutableStateOf<List<ChatMessage>>(emptyList()) }

    var inputText by remember { mutableStateOf("") }

    // Function to handle the sending of the message
    fun sendMessage() {
        if (inputText.isNotBlank()) {
            // Call a function from your viewModel to send the message
            chatViewModel.sendMessage(inputText)
            inputText = "" // Clear the input field after sending
        }
    }

    fun setChatState(state: List<ChatMessage>){
        chatState = state
    }

    BaseView(viewModel = chatViewModel, navController = navController, scope = scope, setContentT = { state -> setChatState(state)}) {
        LazyColumn(
            reverseLayout = true, // This ensures the latest messages are at the bottom.
            modifier = Modifier.fillMaxHeight()
        ) {
            items(count = chatState.size) { index ->
                // The latest message is at the end of the list, so we reverse the index.
                val message = chatState[chatState.size - index - 1]
                ChatMessageItem(message = message, isCurrentUser = message.isUserCreated)
            }
        }
        ChatInputField(
            inputText = inputText,
            onInputTextChange = { inputText = it },
            onSendMessage = { sendMessage() }
        )
    }
}


@Composable
fun ChatInputField(
    inputText: String,
    onInputTextChange: (String) -> Unit,
    onSendMessage: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = inputText,
            onValueChange = onInputTextChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Type your message here") }
        )
        Button(
            onClick = onSendMessage,
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Text("Send")
        }
    }
}

@Composable
fun ChatMessageItem(message: ChatMessage, isCurrentUser: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.padding(4.dp),
            shadowElevation = 2.dp,
            shape = MaterialTheme.shapes.medium,
            color = if (isCurrentUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = message.message ?: "No message",
                    style = MaterialTheme.typography.bodyMedium,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 10,
                    color = if (isCurrentUser) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = message.createdDate.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(top = 4.dp),
                    color = if (isCurrentUser) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (message.seen) {
                    Text(
                        text = "Seen",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(top = 2.dp),
                        color = if (isCurrentUser) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
