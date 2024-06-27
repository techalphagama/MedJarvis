package com.alpharays.mymedjarvisfma.presentation.chatscreen

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.alpharays.mymedjarvisfma.MedJarvisRouter
import com.alpharays.mymedjarvisfma.R
import com.alpharays.mymedjarvisfma.data.Response
import com.alpharays.mymedjarvisfma.data.UriCustomSaver
import com.alpharays.mymedjarvisfma.jarvischat.JarvisChatViewModel
import com.alpharays.mymedjarvisfma.model.ChatItemModel
import java.io.File
import java.io.InputStream
import java.util.UUID

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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // This ensures the background color before loading the wallpaper
    ) {
        Image(
            painter = painterResource(id = R.drawable.mymedjarvisfma_chat_wallpaper),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier.fillMaxSize()
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

            ChatBottomBar(onMessageSent = onMessageSent)
        }
    }
}

@Composable
fun ChatTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.mymedjarvisfma_bot),
            contentDescription = "Back",
            modifier = Modifier
                .size(24.dp)
                .padding(end = 8.dp)
        )
        Text(text = "Chat with Yeti", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            painter = painterResource(id = R.drawable.mymedjarvisfma_bot),
            contentDescription = "More",
            modifier = Modifier.size(24.dp)
        )
    }
}

@ExperimentalFoundationApi
@Composable
fun ChatBottomBar(onMessageSent: (String, MutableList<Uri>) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.White, RoundedCornerShape(16.dp))
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
                    .size(40.dp)
                    .padding(end = 8.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
            )
        }

        Box(
            modifier = Modifier
                .background(
                    if (promptResponse.isBot) Color(0xFFe0f7fa) else Color(0xFFffe0b2), // Adjust the colors as needed
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(8.dp)
                .clip(RoundedCornerShape(16.dp))
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
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
    ) {
        items(chatItems) { item ->
            ChatItem(promptResponse = item)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
fun UserInputPreview() {
    UserInput(onMessageSent = { it, it1 -> })
}

@ExperimentalFoundationApi
@Composable
fun UserInput(
    onMessageSent: (String, MutableList<Uri>) -> Unit,
    modifier: Modifier = Modifier,
) {
    val imageUris = rememberSaveable(saver = UriCustomSaver()) {
        mutableStateListOf()
    }
    var imageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    val pickMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { result ->
        result?.let {
            imageUris.clear()  // Clear for single image selection
            imageUris.add(it)
        }
    }

    // Activity result launcher for taking picture from camera
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { isTaken ->
        if (isTaken) {
            imageUris.add(imageUri!!)
        }
    }

    var textState by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue())
    }

    Surface(tonalElevation = 2.dp, contentColor = MaterialTheme.colorScheme.secondary) {
        Column {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .height(60.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                UserInputSelector(
                    onClickCamera = {
                        imageUri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            File(context.cacheDir, "temp_image_${UUID.randomUUID()}.jpg")
                        )
                        takePictureLauncher.launch(imageUri)
                    },
                    onClickGallery = {
                        pickMediaLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }
                )
                UserInputText(
                    textFieldValue = textState,
                    onTextChanged = { textState = it },
                    onMessageSent = {
                        onMessageSent(textState.text, imageUris)
                        textState = TextFieldValue()
                    },
                    modifier = Modifier.weight(1f) // Ensure it takes available space
                )
                SendMessage(
                    onMessageSent = {
                        onMessageSent(textState.text, imageUris)
                        textState = TextFieldValue()
                    }
                )
            }
            SelectorExpanded(imageUris = imageUris)
        }
    }
}

@ExperimentalFoundationApi
@Composable
private fun UserInputText(
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    onTextChanged: (TextFieldValue) -> Unit,
    textFieldValue: TextFieldValue,
    onMessageSent: (String) -> Unit,
) {
    Box(modifier = modifier.fillMaxWidth()) {
        UserInputTextField(
            textFieldValue,
            onTextChanged,
            onMessageSent,
            keyboardType,
        )
    }
}

@Composable
private fun UserInputTextField(
    textFieldValue: TextFieldValue,
    onTextChanged: (TextFieldValue) -> Unit,
    onMessageSent: (String) -> Unit,
    keyboardType: KeyboardType,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth() // Set the width to match parent
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .padding(12.dp) // Adjust the padding as needed
    ) {
        BasicTextField(
            value = textFieldValue,
            onValueChange = { onTextChanged(it) },
            modifier = Modifier.fillMaxWidth(), // Fill the width within the Box
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = ImeAction.Send,
            ),
            keyboardActions = KeyboardActions(
                onSend = {
                    onMessageSent(textFieldValue.text)
                }
            ),
            maxLines = 1,
            textStyle = TextStyle(color = Color.Black, fontSize = 16.sp),
        )
    }
}

@Composable
fun UserInputSelector(
    onClickCamera: () -> Unit,
    onClickGallery: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SelectorButton(
            onClick = onClickCamera,
            icon = Icons.Filled.Camera,
            description = "capture_photo_desc" // Update description for camera
        )
        SelectorButton(
            onClick = onClickGallery,
            icon = Icons.Filled.Image,
            description = "attach_photo_desc"
        )
    }
}

@Composable
fun SelectorButton(
    onClick: () -> Unit,
    icon: ImageVector,
    description: String,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick
    ) {
        Icon(
            icon,
            tint = LocalContentColor.current,
            modifier = modifier
                .padding(8.dp)
                .size(56.dp),
            contentDescription = description
        )
    }
}

@Composable
fun SendMessage(
    onMessageSent: () -> Unit
) {
    IconButton(
        onClick = onMessageSent,
        modifier = Modifier.size(48.dp) // Adjust size if needed
    ) {
        Icon(
            imageVector = Icons.Filled.Send,
            tint = Color.Blue,
            modifier = Modifier.size(24.dp), // Adjust inner icon size if needed
            contentDescription = "Send message"
        )
    }
}

@Composable
private fun SelectorExpanded(
    modifier: Modifier = Modifier,
    imageUris: MutableList<Uri>
) {
    Surface(tonalElevation = 8.dp) {
        AnimatedVisibility(
            visible = imageUris.size > 0
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                LazyRow(modifier = Modifier.padding(8.dp)) {
                    items(imageUris) { imageUri ->
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AsyncImage(
                                model = imageUri,
                                contentDescription = "",
                                modifier = Modifier
                                    .padding(4.dp)
                                    .requiredSize(50.dp)
                            )
                            TextButton(onClick = { imageUris.remove(imageUri) }) {
                                Text(text = "X")
                            }
                        }
                    }
                }
            }
        }
    }
}
