package com.alpharays.mymedjarvisfma.presentation.chatscreen

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
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
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Image(
            painter = painterResource(id = R.drawable.mymedjarvisfma_chat_wallpaper),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp)
        ) {
            ChatTopBar()

            Box(
                modifier = Modifier.weight(1f)
            ) {
                ChatListScreen(chatItems = chatItems)
                if (promptResponse is Response.Loading) {
                    LoadingScreen()
                }
            }

            ChatBottomBar(
                onMessageSent = onMessageSent,
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding() // Ensure the bottom bar adjusts for the keyboard
            )
        }
    }
}


@Composable
fun ChatTopBar() {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.mymedjarvis_ic_back),
            contentDescription = "Back",
            modifier = Modifier
                .size(30.dp)
                .padding(end = 10.dp)
                .clickable { (context as? Activity)?.finish() },
            tint = Color.White,

            )
        Text(
            text = "Chat with Medris",
            style = MaterialTheme.typography.headlineLarge,
            color = Color.White
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            painter = painterResource(id = R.drawable.mymedjarvisfma_bot),
            contentDescription = "More",
            modifier = Modifier
                .size(24.dp)
                .clickable {
                    Toast
                        .makeText(context, "Coming soon, Sabar kro ji", Toast.LENGTH_SHORT)
                        .show()
                },
            tint = Color.White // This sets the tint color to white
        )

    }
}

@ExperimentalFoundationApi
@Composable
fun ChatBottomBar(
    onMessageSent: (String, MutableList<Uri>) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.White, RoundedCornerShape(16.dp))
            .imePadding() // Ensure the bottom bar adjusts for the keyboard
    ) {
        UserInput(
            onMessageSent = onMessageSent,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
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
                painter = painterResource(id = R.drawable.mymedjarvisfma_bot), // Replace with your bot image resource
                contentDescription = "Bot Image",
                modifier = Modifier
                    .size(50.dp)
                    .padding(1.dp)
                    .background(Color.White, CircleShape)
                    .padding(5.dp)
                    .clip(CircleShape)
            )
        }

        Box(
            modifier = Modifier
                .padding(
                    start = if (promptResponse.isBot) 10.dp else 0.dp,
                    end = if (promptResponse.isBot) 0.dp else 10.dp
                )
                .background(
                    if (promptResponse.isBot) Color(0xFFe0f7fa) else Color(0xFFffe0b2), // Adjust the colors as needed
                    shape = RoundedCornerShape(16.dp)
                )
                .clip(RoundedCornerShape(16.dp))
                .wrapContentWidth(Alignment.Start)
                .weight(1f, false) // Allow Box to take up only the required space
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.padding(8.dp)
            ) {
                promptResponse.image?.let { bitmap ->
                    AsyncImage(
                        model = bitmap,
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .padding(4.dp)
                            .requiredSize(200.dp) // Adjust size as needed
                    )
                }
                Text(
                    text = promptResponse.message ?: "",
                    modifier = Modifier.padding(4.dp)
                )
            }
        }

        if (!promptResponse.isBot) {
            Image(
                painter = painterResource(id = R.drawable.mymedjarvisfma_user), // Replace with your user image resource
                contentDescription = "User Image",
                modifier = Modifier
                    .size(50.dp)
                    .padding(1.dp)
                    .background(Color.White, CircleShape)
                    .padding(5.dp)
                    .clip(CircleShape)
            )
        }
    }
}





@Composable
fun ChatListScreen(chatItems: List<ChatItemModel>) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
    ) {
        items(chatItems) { item ->
            ChatItem(promptResponse = item)
        }
    }
}

