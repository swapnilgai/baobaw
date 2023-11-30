package com.java.baobaw.android.feature.chat

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.java.baobaw.android.BaseView
import com.java.baobaw.feature.chat.ChatMessage
import com.java.baobaw.feature.chat.ChatDetailViewModel
import androidx.compose.material.TextField
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.rememberImagePainter
import coil.size.Scale
import com.java.baobaw.android.R
import java.util.Locale


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatScreen(
    chatDetailViewModel: ChatDetailViewModel,
    scope: CoroutineScope = rememberCoroutineScope(),
    navController: NavController,
    referenceId: String
) {
    var chatState by remember { mutableStateOf<List<ChatMessage>>(emptyList()) }
    var inputText by remember { mutableStateOf("") }

    // Function to handle the sending of the message
    fun sendMessage() {
        if (inputText.isNotBlank()) {
            // Call a function from your viewModel to send the message
            chatDetailViewModel.sendMessage(inputText, referenceId)
            inputText = "" // Clear the input field after sending
        }
    }

    fun setChatState(state: List<ChatMessage>) {
        chatState = state
    }

    BaseView(viewModel = chatDetailViewModel, navController = navController, scope = scope,
        init = { chatDetailViewModel.init(referenceId) },
        setContentT = { state -> setChatState(state) }) {

        Scaffold(
            topBar = { ChatTopBar("Swapnil", url, {}) }, // Define the top bar here.
            bottomBar = {
                ChatInputField(
                    inputText = inputText,
                    onInputTextChange = { inputText = it },
                    onSendMessage = {
                        sendMessage()
                    }
                )
            }
        ) { innerPadding ->
            LazyColumn(
                reverseLayout = true,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding) // Apply the padding provided by Scaffold to ensure content is above the bottom bar.
                    .background(MaterialTheme.colorScheme.background)
            ) {
//                val groupedMessages = chatState.groupBy { it.createdDate }
//
//                groupedMessages.forEach { (date, messages) ->
//                    items(messages) { message ->
//                        ChatMessageItem(message = message, isCurrentUser = message.isUserCreated)
//                    }
//                    stickyHeader {
//                        DateHeader(date)
//                    }
//                }

                items(chatState) { message ->
                    if(!message.isHeader)
                    ChatMessageItem(message = message, isCurrentUser = message.isUserCreated)
                    else DateHeader(message.message!!)
                }

            }
        }
    }
}

@Composable
fun ChatMessageItem(message: ChatMessage, isCurrentUser: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start
    ) {

//        if (!isCurrentUser) {
//            // Show avatar for other users
//            Avatar(message.creatorUserId)
//        }
        if(!message.isHeader) MessageBubble(message, isCurrentUser)
    }
}

@Composable
fun Avatar(userId: String) {
    // Placeholder for avatar, replace with actual image loading logic
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(Color.Gray, CircleShape)
    )
}

@Composable
fun MessageBubble(message: ChatMessage, isCurrentUser: Boolean) {
    val backgroundColor = if (isCurrentUser) MaterialTheme.colorScheme.primary else Color.LightGray
    val textColor = if (isCurrentUser) Color.White else Color.Black

    Surface(
        modifier = Modifier.padding(horizontal = 8.dp),
        shape = MaterialTheme.shapes.medium,
        color = backgroundColor, // Instead of using colors.primary directly
        elevation = 1.dp
    ) {
        Text(
            text = message.message ?: "",
            modifier = Modifier.padding(all = 8.dp),
            color = textColor // Instead of using colors directly
        )
    }
}

@Composable
fun DateHeader(dateString: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(MaterialTheme.colorScheme.primary)
    ) {
        Text(
            text = dateString.toUpperCase(Locale.ROOT),
            style = MaterialTheme.typography.labelSmall.copy(color = Color.White),
            modifier = Modifier.padding(4.dp)
        )
    }
}

@Composable
fun ChatInputField(
    inputText: String,
    onInputTextChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF0F0F0), RoundedCornerShape(24.dp)), // This padding creates the white border effect around the field
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = inputText,
                onValueChange = onInputTextChange,
                singleLine = true,
                placeholder = { Text("Type something", color = Color.Gray) },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.onSurface,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    placeholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = ContentAlpha.medium)
                ),
                modifier = Modifier
                    .weight(5f)
            )
            IconButton(
                onClick = onSendMessage,
                modifier = Modifier.size(48.dp) // Size of the IconButton
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send",
                    tint = if (inputText.isNotBlank()) MaterialTheme.colorScheme.primary else Color.LightGray
                )
            }
        }
    }
}


val url  = "https://eadxajuudpypsivdhjtu.supabase.co/storage/v1/object/public/Profile/76c1c1ef-ec48-4bcb-9081-d2c52edb8661/0.png"
@Composable
fun ChatTopBar(userName: String, userImageUrl: String = url, onBackClick: () -> Unit) {
    TopAppBar(
        backgroundColor = Color.White,
        elevation = 4.dp
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black // Set the back button icon to black
            )
        }
        UserImage(
            imageUrl = userImageUrl,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = userName,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(start = 8.dp)
        )
        Spacer(modifier = Modifier.weight(1f)) // Push everything to the left
    }
}

@Composable
fun UserImage(imageUrl: String, modifier: Modifier = Modifier) {
    Image(
        painter = rememberImagePainter(
            data = imageUrl,
            builder = {
                crossfade(true)
                scale(Scale.FILL)
            }
        ),
        contentDescription = "User Profile Picture",
        contentScale = ContentScale.Crop, // Crop the image if it's not a square
        modifier = modifier
            .size(40.dp) // Set the size of the image
            .clip(CircleShape) // Clip the image to a circle
            .border(1.5.dp, Color.Gray, CircleShape) // Add a border if needed
    )
}
