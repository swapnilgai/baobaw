package com.java.baobaw.android.feature.chat

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.java.baobaw.feature.chat.LastMessage
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.java.baobaw.android.BaseView
import com.java.baobaw.feature.chat.ChatListContent
import com.java.baobaw.feature.chat.ChatListViewModel
import kotlinx.coroutines.CoroutineScope

@Composable
fun ChatListView(chatListViewModel: ChatListViewModel,
                 scope: CoroutineScope = rememberCoroutineScope(),
                 navController: NavController) {
    // Assuming messages is being collected as a state from viewModel.messages
    val listState = rememberLazyListState()
    var chatListState by remember { mutableStateOf<ChatListContent>(ChatListContent(emptyList())) }

    fun setChatState(state: ChatListContent){
        chatListState = state
    }

    BaseView(viewModel = chatListViewModel, navController = navController, scope = scope,
        init = { chatListViewModel.init()},
        setContentT = { state -> setChatState(state)}) {

        LazyColumn(state = listState) {
            itemsIndexed(chatListState.messages) { index, message ->
                ChatItem(
                    message = message,
                    onClick = { referenceId ->
                        // Call navigateToChatDetail with the specific referenceId
                        chatListViewModel.navigateToChatDetail(referenceId)
                    }
                )

                // Load more messages when near the end of the list and not already loading or at the last page
                if (index >= chatListState.messages.size - 1) {
                    //chatListViewModel.loadMoreMessages()
                }
            }
        }

        // As a side effect, check if the user is close to the end of the list and load more messages
        LaunchedEffect(listState) {
            snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
                .collect { lastIndex ->
                    if (lastIndex != null &&
                        lastIndex >= chatListState.messages.size - 10) {
                        //chatListViewModel.loadMoreMessages()
                    }
                }
        }
    }
}

@Composable
fun ChatItem(message: LastMessage, onClick: (String) -> Unit) {
    val backgroundColor = if (!message.seen) {
        MaterialTheme.colors.secondary.copy(alpha = 0.5f) // Highlight if not seen
    } else {
        MaterialTheme.colors.surface // Regular background if seen
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor) // Apply the background color based on the seen status
            .clickable { onClick(message.referenceId) }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    )  {
        // Profile picture
        Image(
            painter = rememberImagePainter(
                data = message.imageUrl,
                builder = {
                    crossfade(true)
                   // placeholder(R.drawable.avatar_placeholder) // Placeholder image
                   // error(R.drawable.avatar_placeholder) // Error image
                }
            ),
            contentDescription = "Profile picture",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape), // Circular shape
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(8.dp))

        // Message content
        Column {
            Text(text = message.name, style = MaterialTheme.typography.subtitle1)
            Text(
                text = message.message,
                style = MaterialTheme.typography.body2,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
