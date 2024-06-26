package com.alpharays.mymedjarvisfma.presentation.chatscreen

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.alpharays.mymedjarvisfma.data.Response
import com.alpharays.mymedjarvisfma.jarvischat.JarvisChatViewModel

@Composable
fun ChatScreen(
    viewModel: JarvisChatViewModel = hiltViewModel()
) {
    val promptResponse by viewModel.promptResponse.collectAsState()
    val chatItems = remember { mutableStateListOf<Pair<String?, Bitmap?>>() }

    // Update chatItems when promptResponse changes
    if (promptResponse is Response.Success) {
        (promptResponse as Response.Success<Pair<String?, Bitmap?>>).data?.let { chatItems.add(it) }
    }

    MainScreen(
        chatItems = chatItems,
        promptResponse = promptResponse,
        onMessageSent = { inputText, selectedItems ->
            viewModel.sendPrompt(message = inputText, pickUri = selectedItems)
            selectedItems.clear()
            chatItems.add(Pair(inputText, null))
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(
    chatItems: List<Pair<String?, Bitmap?>>,
    promptResponse: Response<Pair<String?, Bitmap?>>?,
    onMessageSent: (String, MutableList<Uri>) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = { AppBars() },
        bottomBar = {
            UserInput(
                onMessageSent = onMessageSent
            )
        }
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(it)
        ) {
            ChatListScreen(chatItems = chatItems)
            if (promptResponse is Response.Loading) {
                LoadingScreen()
            }
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ChatItem(promptResponse: Pair<String?, Bitmap?>) {
    Card(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            promptResponse.second?.let { bitmap ->
                AsyncImage(
                    model = bitmap,
                    contentDescription = "",
                    modifier = Modifier
                        .padding(4.dp)
                        .requiredSize(300.dp)
                )
            }
            Text(
                text = promptResponse.first ?: "",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun ChatListScreen(chatItems: List<Pair<String?, Bitmap?>>) {
    LazyColumn {
        items(chatItems) { item ->
            ChatItem(promptResponse = item)
        }
    }
}

@Composable
fun FailureScreen(errorMessage: String) {
    Card(
        modifier = Modifier
            .padding(vertical = 16.dp)
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
    ) {
        Text(
            text = errorMessage,
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Composable
fun EmptyState() {
    Box(modifier = Modifier.fillMaxSize())
}
