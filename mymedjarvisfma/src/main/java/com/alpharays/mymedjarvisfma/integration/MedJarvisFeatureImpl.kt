package com.alpharays.mymedjarvisfma.integration

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.alpharays.mymedjarvisfma.presentation.chatscreen.ChatScreen

private const val baseRoute = "jarvis_screen"

class MedJarvisFeatureImpl : MedJarvisFeatureApi {
    override val medJarvisRoute: String
        get() = baseRoute

    override fun registerGraph(
        navGraphBuilder: NavGraphBuilder,
        navController: NavHostController,
        modifier: Modifier
    ) {
        navGraphBuilder.composable(medJarvisRoute) {
            ChatScreen()
        }
    }
}