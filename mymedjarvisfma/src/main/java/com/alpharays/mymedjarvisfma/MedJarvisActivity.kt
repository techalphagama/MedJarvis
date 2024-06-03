package com.alpharays.mymedjarvisfma

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.alpharays.mymedjarvisfma.integration.MedJarvisDependencyProvider
import com.alpharays.mymedjarvisfma.integration.MedJarvisFeatureImpl
import com.alpharays.mymedjarvisfma.ui.theme.MedJarvisTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MedJarvisActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MedJarvisDependencyProvider.provideImpl(medJarvisFeatureApi = MedJarvisFeatureImpl())
        enableEdgeToEdge()
        setContent {
            MedJarvisTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    //   AppContent()
                }
            }
        }
    }
}
