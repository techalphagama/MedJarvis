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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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

    MainScreen(
        promptResponse = promptResponse,
        onMessageSent = { inputText, selectedItems ->
            viewModel.sendPrompt(message = inputText, pickUri = selectedItems)
            selectedItems.clear()
        }
    )
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(
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
                .verticalScroll(rememberScrollState())
        ) {
            when (promptResponse) {
                is Response.Loading -> LoadingScreen()
                is Response.Success -> SuccessScreen(promptResponse = promptResponse.data!!)
                is Response.Failure -> FailureScreen(errorMessage = promptResponse.message)
                else -> EmptyState()
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
fun SuccessScreen(promptResponse: Pair<String?, Bitmap?>) {
    Card(
        modifier = Modifier
            .padding(vertical = 16.dp)
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

