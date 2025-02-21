package com.techibot.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.techibot.app.data.model.ChatMessage
import com.techibot.app.ui.viewmodel.ChatState
import com.techibot.app.ui.viewmodel.ChatViewModel

@Composable
fun ChatScreen(
    navController: NavController,
    viewModel: ChatViewModel = viewModel()
) {
    var messageText by remember { mutableStateOf("") }
    val messages by viewModel.messages.collectAsState()
    val chatState by viewModel.chatState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Messages list
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            reverseLayout = true
        ) {
            items(messages.reversed()) { message ->
                ChatMessageItem(message)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Error message
        if (chatState is ChatState.Error) {
            Text(
                text = (chatState as ChatState.Error).message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // Input field and send button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                label = { Text("پیام خود را بنویسید") },
                enabled = chatState !is ChatState.Loading
            )

            Button(
                onClick = {
                    if (messageText.isNotBlank()) {
                        // TODO: Get token from SharedPreferences
                        viewModel.sendMessage(messageText, "user-token")
                        messageText = ""
                    }
                },
                enabled = messageText.isNotBlank() && chatState !is ChatState.Loading
            ) {
                Text("ارسال")
            }
        }
    }
}

@Composable
fun ChatMessageItem(message: ChatMessage) {
    val alignment = if (message.isFromUser) Alignment.End else Alignment.Start
    val backgroundColor = if (message.isFromUser)
        MaterialTheme.colorScheme.primaryContainer
    else
        MaterialTheme.colorScheme.secondaryContainer

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = backgroundColor,
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Text(
                text = message.content,
                modifier = Modifier.padding(8.dp),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}
