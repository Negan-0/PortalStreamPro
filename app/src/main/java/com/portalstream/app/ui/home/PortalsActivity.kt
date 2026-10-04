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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.portalstream.app.data.PortalStore

class PortalsActivity : ComponentActivity() {

    private lateinit var store: PortalStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        store = PortalStore(this)
        setContentView(ComposeView(this).apply {
            setContent {
                MaterialTheme {
                    PortalsScreen()
                }
            }
        })
    }

    @Composable
    fun PortalsScreen() {
        var portals by remember { mutableStateOf(store.load()) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text("I miei portali", style = MaterialTheme.typography.headlineSmall)
            Text(
                text = "${portals.size} salvati",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))

            if (portals.isEmpty()) {
                Text(
                    "Nessun portale salvato.
Torna alla Home, inserisci un URL e premi \u201CSalva\u201D.",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                LazyColumn {
                    items(portals, key = { it.id }) { portal ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { openPortal(portal.url) }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Text(
                                        text = "[${portal.id}] ${portal.type.label}",
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    Text(
                                        text = portal.url,
                                        style = MaterialTheme.typography.bodySmall,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                TextButton(onClick = {
                                    store.delete(portal.id)
                                    portals = store.load()
                                }) {
                                    Text("Elimina", color = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun openPortal(url: String) {
        startActivity(
            Intent(this, HomeActivity::class.java).apply {
                putExtra(HomeActivity.EXTRA_PORTAL_URL, url)
            }
        )
    }
}
