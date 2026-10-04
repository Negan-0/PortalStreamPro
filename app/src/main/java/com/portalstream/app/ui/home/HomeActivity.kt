package com.portalstream.app.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.portalstream.app.R
import com.portalstream.app.domain.model.Channel
import com.portalstream.app.network.PlaylistDownloader
import com.portalstream.app.streaming.M3UParser
import com.portalstream.app.ui.player.PlayerActivity
import kotlinx.coroutines.launch
import timber.log.Timber

class HomeActivity : ComponentActivity() {

    private val downloader = PlaylistDownloader()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ComposeView(this).apply {
            setContent {
                MaterialTheme {
                    HomeScreen()
                }
            }
        })
    }

    @Composable
    fun HomeScreen() {
        var url by rememberSaveable { mutableStateOf("") }
        var channels by remember { mutableStateOf<List<Channel>>(emptyList()) }
        var loading by remember { mutableStateOf(false) }
        var error by remember { mutableStateOf<String?>(null) }
        val scope = rememberCoroutineScope()

        fun loadPlaylist() {
            if (url.isBlank() || loading) return
            loading = true
            error = null
            scope.launch {
                try {
                    val content = downloader.download(url.trim())
                    val parsed = M3UParser.parse(content)
                    channels = parsed
                    if (parsed.isEmpty()) error = getString(R.string.playlist_no_channels)
                } catch (e: Exception) {
                    Timber.e(e, "Download playlist fallito")
                    error = "${getString(R.string.config_import_failed)}: ${e.message}"
                } finally {
                    loading = false
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text("PortalStream Pro", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.config_url)) },
                placeholder = { Text(stringResource(R.string.config_url_hint)) },
                singleLine = true,
                enabled = !loading
            )
            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = { loadPlaylist() },
                    enabled = !loading && url.isNotBlank()
                ) {
                    Text(stringResource(R.string.home_add_portal))
                }
                Spacer(Modifier.width(8.dp))
                OutlinedButton(
                    onClick = { playStream(PlayerActivity.TEST_STREAM_URL, "Test Stream") },
                    enabled = !loading
                ) {
                    Text("ЁЯОм Test")
                }
            }

            if (loading) {
                Spacer(Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator()
                    Spacer(Modifier.width(12.dp))
                    Text(stringResource(R.string.config_importing))
                }
            }

            error?.let { err ->
                Spacer(Modifier.height(8.dp))
                Text("тЪая╕П $err", color = MaterialTheme.colorScheme.error)
            }

            if (channels.isNotEmpty()) {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.playlist_channels_count, channels.size),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(8.dp))
                LazyColumn {
                    items(channels, key = { it.url }) { channel ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { playStream(channel.url, channel.name) }
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Text(
                                    text = channel.name,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                channel.group?.let {
                                    Text(
                                        text = it,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun playStream(url: String, name: String) {
        startActivity(
            Intent(this, PlayerActivity::class.java).apply {
                putExtra(PlayerActivity.EXTRA_STREAM_URL, url)
                putExtra(PlayerActivity.EXTRA_CHANNEL_NAME, name)
            }
        )
    }
}
