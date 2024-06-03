package com.alpharays.mymedjarvisfma.navigation


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.alpharays.alaskagemsdk.core.register
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.alpharays.mymedjarvisfma.integration.MedJarvisDependencyProvider


@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = MedJarvisDependencyProvider.medJarvisFeature().medJarvisRoute
    ) {
        register(
            MedJarvisDependencyProvider.medJarvisFeature(),
            navController = navController,
            modifier = modifier
        )
    }

}

