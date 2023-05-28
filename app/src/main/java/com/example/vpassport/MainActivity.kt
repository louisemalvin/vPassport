@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.vpassport

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.core.view.WindowCompat
import com.example.vpassport.ui.screens.DefaultApp
import com.example.vpassport.ui.theme.VPassportTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            VPassportTheme {
                DefaultApp()
            }
        }
    }
}
