package com.alpharays.mymedjarvisfma.presentation.chatscreen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.alpharays.mymedjarvisfma.data.UriCustomSaver
import java.io.File
import java.util.UUID

@Preview
@Composable
fun UserInputPreview() {
    UserInput(onMessageSent = { it, it1 -> })
}

@OptIn(ExperimentalFoundationApi::class)
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

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .shadow(6.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF144EF),
                        Color(0xFFFD7365)
                    ),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                ),
                shape = RoundedCornerShape(8.dp)
            )
            .clip(RoundedCornerShape(8.dp))
    ) {
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
                            File(
                                context.cacheDir,
                                "temp_image_${UUID.randomUUID()}.jpg"
                            )
                        )
                        takePictureLauncher.launch(imageUri)
                    },
                    onClickGallery = {
                        pickMediaLauncher.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    }
                )
                UserInputText(
                    textFieldValue = textState,
                    onTextChanged = { textState = it },
                    onMessageSent = {
                        onMessageSent(textState.text, imageUris)
                        textState = TextFieldValue()
                    },
                    modifier = Modifier.weight(1f)
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
            .wrapContentHeight()
            .wrapContentWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        SelectorButton(
            onClick = onClickCamera,
            icon = Icons.Filled.Camera,
            description = "capture_photo_desc"
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
            tint = Color.White,
            modifier = modifier
                .size(50.dp),
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
    ) {
        Icon(
            imageVector = Icons.Filled.Send,
            tint = Color.Blue,
            modifier = Modifier.size(50.dp), // Adjust inner icon size if needed
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