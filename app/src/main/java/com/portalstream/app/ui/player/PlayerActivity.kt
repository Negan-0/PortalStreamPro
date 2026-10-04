package com.portalstream.app.ui.player

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.media3.common.MediaItem
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.ExoPlayer.Builder
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.portalstream.app.network.NetworkSniffer
import com.portalstream.app.streaming.AdaptiveBitrateManager
import com.portalstream.app.utils.DeviceDetector
import com.portalstream.app.utils.DeviceType
import timber.log.Timber

class PlayerActivity : ComponentActivity() {

    private lateinit var exoPlayer: ExoPlayer
    private lateinit var pipManager: PipManager
    private lateinit var adaptiveBitrate: AdaptiveBitrateManager
    private lateinit var deviceDetector: DeviceDetector
    private lateinit var networkSniffer: NetworkSniffer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            initApp()
        } catch (e: Exception) {
            Timber.e(e, "Crash in onCreate")
            showCrashScreen(e)
        }
    }

    private fun initApp() {
        deviceDetector = DeviceDetector(this)
        networkSniffer = NetworkSniffer(this)

        Timber.d("Device: ${deviceDetector.getDeviceInfo()}")

        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setDefaultRequestProperties(
                mapOf(
                    "User-Agent" to "Mozilla/5.0 (QtEmbedded; U; Linux; C) AppleWebKit/533.3 MAG200 stbapp ver: 4.3.1939"
                )
            )

        exoPlayer = Builder(this)
            .setMediaSourceFactory(DefaultMediaSourceFactory(httpDataSourceFactory))
            .build()

        pipManager = PipManager(this, exoPlayer)

        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
        adaptiveBitrate = AdaptiveBitrateManager(exoPlayer, connectivityManager)
        adaptiveBitrate.autoSelectQuality()

        // ðŸŽ¬ STREAM DI TEST HLS multi-qualita (1080p/720p/480p)
        loadTestStream()

        setupUI()
    }

    private fun loadTestStream() {
        val testStreamUrl = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"
        val mediaItem = MediaItem.fromUri(testStreamUrl)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
        Timber.d("Caricato stream di test: $testStreamUrl")
    }

    private fun showCrashScreen(e: Exception) {
        setContentView(ComposeView(this).apply {
            setContent {
                MaterialTheme {
                    CrashScreen(e)
                }
            }
        })
    }

    private fun setupUI() {
        val device = deviceDetector.detectDevice()

        setContentView(ComposeView(this).apply {
            setContent {
                MaterialTheme {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        when (device) {
                            DeviceType.PHONE -> PhonePlayerUI(exoPlayer, pipManager)
                            DeviceType.TABLET -> TabletPlayerUI(exoPlayer)
                            DeviceType.TV, DeviceType.FIRESTICK, DeviceType.BOX_ANDROID -> TVPlayerUI(exoPlayer)
                            else -> PhonePlayerUI(exoPlayer, pipManager)
                        }
                    }
                }
            }
        })
    }

    override fun onPause() {
        super.onPause()
        if (::exoPlayer.isInitialized && ::pipManager.isInitialized && !pipManager.isInPipMode()) {
            exoPlayer.pause()
        }
    }

    override fun onResume() {
        super.onResume()
        if (::exoPlayer.isInitialized) {
            exoPlayer.play()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::exoPlayer.isInitialized) {
            exoPlayer.release()
        }
    }
}
