package com.alpharays.mymedjarvisfma.presentation.chatscreen

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.alpharays.mymedjarvisfma.MedJarvisRouter
import com.alpharays.mymedjarvisfma.R
import com.alpharays.mymedjarvisfma.data.Response
import com.alpharays.mymedjarvisfma.jarvischat.JarvisChatViewModel
import com.alpharays.mymedjarvisfma.model.ChatItemModel
import java.io.InputStream

@Composable
fun ChatScreen(
    viewModel: JarvisChatViewModel = hiltViewModel()
) {
    val promptResponse by viewModel.promptResponse.collectAsState()
    val chatItems = remember { mutableStateListOf<ChatItemModel>() }

    // Update chatItems when promptResponse changes
    if (promptResponse is Response.Success) {
        (promptResponse as Response.Success<ChatItemModel>).data?.let { chatItems.add(it) }
    }

    MainScreen(
        chatItems = chatItems,
        promptResponse = promptResponse,
        onMessageSent = { inputText, selectedItems ->
            val selectedImageUri = selectedItems.firstOrNull()

            if (selectedImageUri != null) {
                viewModel.sendPrompt(message = inputText, pickUri = selectedItems)
                selectedItems.clear()
                val bitmap = getBitmapFromUri(selectedImageUri)
                val chatItemModel =
                    ChatItemModel(message = inputText, isBot = false, image = bitmap)
                chatItems.add(chatItemModel)
            } else {
                viewModel.sendPrompt(message = inputText, pickUri = selectedItems)
                val chatItemModel = ChatItemModel(message = inputText, isBot = false, image = null)
                chatItems.add(chatItemModel)
            }
        }
    )
}

private fun getBitmapFromUri(uri: Uri): Bitmap? {
    val context: Context? = MedJarvisRouter.context

    return try {
        val contentResolver: ContentResolver? = context?.contentResolver
        val inputStream: InputStream? = contentResolver?.openInputStream(uri)
        BitmapFactory.decodeStream(inputStream)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(
    chatItems: List<ChatItemModel>,
    promptResponse: Response<ChatItemModel>?,
    onMessageSent: (String, MutableList<Uri>) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = { AppBars() },
        bottomBar = {
            UserInput(onMessageSent = onMessageSent)
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .padding(innerPadding)
                .padding(10.dp)
                .fillMaxSize()
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
fun ChatItem(promptResponse: ChatItemModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = if (promptResponse.isBot) Arrangement.Start else Arrangement.End
    ) {
        if (promptResponse.isBot) {
            Image(
                painter = painterResource(id = R.drawable.close_bottom_sheet_icon), // Replace with your bot image resource
                contentDescription = "Bot Image",
                modifier = Modifier
                    .size(40.dp)
                    .padding(end = 8.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
            )
        }

        Card(
            modifier = Modifier
                .background(if (promptResponse.isBot) Color.Red else Color.Yellow)
                .padding(8.dp),
            shape = MaterialTheme.shapes.large,
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(8.dp)
            ) {
                promptResponse.image?.let { bitmap ->
                    AsyncImage(
                        model = bitmap,
                        contentDescription = "",
                        modifier = Modifier
                            .padding(4.dp)
                            .requiredSize(300.dp)
                    )
                }
                Text(
                    text = promptResponse.message ?: "",
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        if (!promptResponse.isBot) {
            Image(
                painter = painterResource(id = R.drawable.close_bottom_sheet_icon), // Replace with your user image resource
                contentDescription = "User Image",
                modifier = Modifier
                    .size(40.dp)
                    .padding(start = 8.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
            )
        }
    }
}




@Composable
fun ChatListScreen(chatItems: List<ChatItemModel>) {
    LazyColumn(
        contentPadding = PaddingValues(vertical = 2.dp)
    ) {
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
