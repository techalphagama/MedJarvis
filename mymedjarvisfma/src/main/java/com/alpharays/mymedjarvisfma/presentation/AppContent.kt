package com.alpharays.mymedjarvisfma.presentation

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.alpharays.mymedjarvisfma.navigation.AppNavGraph
import com.alpharays.mymedjarvisfma.ui.theme.MedJarvisTheme

@Composable
fun AppContent() {
    MedJarvisTheme {
        val navController = rememberNavController()
        Scaffold(modifier = Modifier.navigationBarsPadding()) { innerPaddingModifier ->
            AppNavGraph(
                navController = navController,
                modifier = Modifier.padding(innerPaddingModifier)
            )
        }
    }
}