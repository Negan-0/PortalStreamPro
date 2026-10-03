package com.portalstream.app.ui.player

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.ExoPlayer.Builder
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
        
        // Inizializza gli strumenti
        deviceDetector = DeviceDetector(this)
        networkSniffer = NetworkSniffer(this)
        
        Timber.d("Device: ${deviceDetector.getDeviceInfo()}")
        
        // ExoPlayer con sniffer di rete
        exoPlayer = Builder(this)
            .setHttpDataSourceFactory { dataSourceFactory ->
                dataSourceFactory.setDefaultRequestProperties(
                    mapOf(
                        "User-Agent" to "Mozilla/5.0 (QtEmbedded; U; Linux; C) AppleWebKit/533.3 MAG200 stbapp ver: 4.3.1939"
                    )
                )
            }
            .build()
        
        pipManager = PipManager(this, exoPlayer)
        adaptiveBitrate = AdaptiveBitrateManager(this, getSystemService(CONNECTIVITY_SERVICE) as android.net.ConnectivityManager)
        
        // Auto-seleziona qualità in base alla velocità di rete
        adaptiveBitrate.autoSelectQuality()
        
        // Setup UI in base al device
        setupUI()
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
        if (!pipManager.isInPipMode()) {
            exoPlayer.pause()
        }
    }
    
    override fun onResume() {
        super.onResume()
        exoPlayer.play()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.release()
    }
}
