package pe.kuntur.tv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import pe.kuntur.tv.ui.HomeScreen
import pe.kuntur.tv.ui.PeruTheme
import pe.kuntur.tv.ui.PlayerScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        val channels = ChannelRepository.load(this)

        setContent {
            PeruTheme {
                var selected by remember { mutableStateOf<Channel?>(null) }
                val current = selected

                if (current == null) {
                    HomeScreen(channels = channels, onChannelClick = { selected = it })
                } else {
                    BackHandler { selected = null }
                    PlayerScreen(
                        channel = current,
                        onBack = { selected = null }
                    )
                }
            }
        }
    }
}
