package com.portalstream.app.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.media3.exoplayer.ExoPlayer

@Composable
fun PhonePlayerUI(exoPlayer: ExoPlayer, pipManager: PipManager) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Text("📱 Phone Player UI", color = Color.White)
    }
}

@Composable
fun TabletPlayerUI(exoPlayer: ExoPlayer) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Text("📱 Tablet Player UI", color = Color.White)
    }
}

@Composable
fun TVPlayerUI(exoPlayer: ExoPlayer) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Text("📺 TV/Firestick Player UI", color = Color.White)
    }
}
