package com.example.vpassport.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Icon(
    val small: Dp = 30.dp,
    val medium: Dp = 40.dp,
    val large:  Dp = 60.dp
)

val LocalIcon = compositionLocalOf { Icon() }
val MaterialTheme.icon: Icon
    @Composable
    @ReadOnlyComposable
    get() = LocalIcon.current