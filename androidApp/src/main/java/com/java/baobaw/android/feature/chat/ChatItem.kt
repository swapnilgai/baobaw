package com.java.baobaw.android.feature.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberImagePainter
import coil.size.Scale
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatScreen(
    chatDetailViewModel: ChatDetailViewModel,
    scope: CoroutineScope = rememberCoroutineScope(),
    navController: NavController,
    referenceId: String
) {
    var chatState by remember { mutableStateOf<Map<String, List<ChatMessage>>>(emptyMap()) }
    var inputText by remember { mutableStateOf("") }

    // Function to handle the sending of the message
    fun sendMessage() {
        if (inputText.isNotBlank()) {
            // Call a function from your viewModel to send the message
            chatDetailViewModel.sendMessage(inputText, referenceId)
            inputText = "" // Clear the input field after sending
        }
    }

    fun setChatState(state: Map<String, List<ChatMessage>>) {
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
                chatState.forEach { (date, messages) ->
                    items(messages) { message ->
                        ChatMessageItem(message = message, isCurrentUser = message.isUserCreated)
                    }
                    item(date) {
                        DateHeader(date)
                    }
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
            .padding(vertical = 0.dp),
        horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start
    ) {
        if(!message.isHeader) MessageBubble(message, isCurrentUser)
    }
}

@Composable
fun DateHeader(dateString: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
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

@Composable
fun MessageBubble(message: ChatMessage, isCurrentUser: Boolean) {
    var showTime by remember { mutableStateOf(false) }

    val bubbleColor = if (isCurrentUser) Color(0xFF6B38FB) else Color(0xFFE7E7E8)
    val textColor = if (isCurrentUser) Color.White else Color.Black
    val timeTextColor = Color(0xFF9E9E9E)
    val paddingHorizontal = 8.dp

    Column(
        horizontalAlignment = if (isCurrentUser) Alignment.End else Alignment.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 1.dp)
    ) {
        Surface(
            modifier = Modifier
                .clickable { showTime = !showTime }
                .padding(horizontal = paddingHorizontal, vertical = 2.dp)
                .background(color = bubbleColor, shape = RoundedCornerShape(8.dp)),
            shape = RoundedCornerShape(8.dp),
            color = bubbleColor
        ) {
            Text(
                text = message.message!!,
                color = textColor,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp)
            )
        }

        AnimatedVisibility(
            visible = showTime,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            Text(
                text = message.createdTime,
                color = timeTextColor,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(
                    start = if (isCurrentUser) paddingHorizontal else 0.dp,
                    end = if (isCurrentUser) 0.dp else paddingHorizontal,
                    top = 4.dp
                )
            )
        }
    }
}


