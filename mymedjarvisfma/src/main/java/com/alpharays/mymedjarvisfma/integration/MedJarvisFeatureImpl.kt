package com.alpharays.mymedjarvisfma.integration

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

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
            //  CommunityScreen(navController = navController)
        }
    }
}