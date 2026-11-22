package com.example.ui.screens

import androidx.compose.runtime.Composable

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    isFirebaseReady: Boolean = true,
    onFirebaseInitialized: () -> Unit = {}
) {
    LoginScreen(
        onAuthSuccess = onAuthSuccess,
        isFirebaseReady = isFirebaseReady,
        onFirebaseInitialized = onFirebaseInitialized
    )
}
