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
import com.portalstream.app.data.Portal
import com.portalstream.app.data.PortalStore
import com.portalstream.app.data.PortalType

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
        var editing by remember { mutableStateOf<Portal?>(null) }

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
                Column {
                    Text(
                        text = "Nessun portale salvato.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Torna alla Home, inserisci un URL e premi il pulsante Salva.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn {
                    items(portals, key = { it.id }) { portal ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { openPortal(portal.url) }
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Text(
                                    text = "[${portal.id}] " +
                                        if (portal.name.isBlank()) portal.url else portal.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = portal.type.label + " - " + portal.url,
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Row {
                                    TextButton(onClick = { editing = portal }) {
                                        Text("Modifica")
                                    }
                                    Spacer(Modifier.width(4.dp))
                                    TextButton(onClick = {
                                        store.delete(portal.id)
                                        portals = store.load()
                                    }) {
                                        Text(
                                            text = "Elimina",
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        editing?.let { portal ->
            PortalEditDialog(
                portal = portal,
                onDismiss = { editing = null },
                onSave = { updated ->
                    // Rileva il tipo in base all'URL aggiornato
                    store.update(updated.copy(type = PortalType.detect(updated.url)))
                    portals = store.load()
                    editing = null
                }
            )
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
