package com.nexus.mobilestore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.nexus.mobilestore.ui.NavGraph
import com.nexus.mobilestore.ui.theme.NexusMobileStoreTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NexusMobileStoreTheme {
                NavGraph()
            }
        }
    }
}
