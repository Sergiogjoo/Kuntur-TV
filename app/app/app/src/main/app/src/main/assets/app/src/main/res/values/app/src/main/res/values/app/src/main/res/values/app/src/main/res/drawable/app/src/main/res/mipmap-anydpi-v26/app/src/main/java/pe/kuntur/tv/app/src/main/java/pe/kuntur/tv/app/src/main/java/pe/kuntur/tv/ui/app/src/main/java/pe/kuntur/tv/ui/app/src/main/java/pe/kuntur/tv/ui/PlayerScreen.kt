package pe.kuntur.tv.ui

import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import pe.kuntur.tv.Channel

@Composable
fun PlayerScreen(channel: Channel, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .statusBarsPadding()
    ) {
        Box(Modifier.fillMaxWidth().padding(4.dp), contentAlignment = Alignment.CenterStart) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
            }
            Text(
                channel.name,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(start = 52.dp)
            )
        }

        LiveStream(channel)
    }
}

@OptIn(UnstableApi::class)
@Composable
private fun LiveStream(channel: Channel) {
    val context = LocalContext.current
    var sourceIndex by remember(channel.id) { mutableIntStateOf(0) }
    var loading by remember(channel.id, sourceIndex) { mutableStateOf(true) }
    var failed by remember(channel.id) { mutableStateOf(false) }

    val player = remember(channel.id, sourceIndex) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(
                MediaItem.Builder()
                    .setUri(channel.streamUrls[sourceIndex])
                    .setMimeType(MimeTypes.APPLICATION_M3U8)
                    .build()
            )
            playWhenReady = true
            prepare()
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    loading = state == Player.STATE_BUFFERING || state == Player.STATE_IDLE
                }

                override fun onPlayerError(error: PlaybackException) {
                    if (sourceIndex < channel.streamUrls.size - 1) {
                        sourceIndex += 1 // probar la siguiente señal
                    } else {
                        failed = true
                        loading = false
                    }
                }
            })
        }
    }
    DisposableEffect(player) { onDispose { player.release() } }

    Box(Modifier.fillMaxWidth().aspectRatio(16f / 9f)) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                PlayerView(ctx).apply {
                    this.player = player
                    useController = true
                }
            },
            update = { view -> view.player = player }
        )
        if (loading && !failed) {
            CircularProgressIndicator(color = PeruRed, modifier = Modifier.align(Alignment.Center))
        }
    }

    if (failed) {
        Column(Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "No se pudo reproducir la señal en este momento. Revisa tu conexión o prueba más tarde.",
                color = Color(0xFFFF8A8A),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.size(14.dp))
            Button(
                onClick = {
                    failed = false
                    sourceIndex = 0
                },
                colors = ButtonDefaults.buttonColors(containerColor = PeruRed)
            ) { Text("Reintentar") }
        }
    }
}
