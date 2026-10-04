package com.portalstream.app.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.portalstream.app.data.Portal

private data class UaPreset(val label: String, val value: String)

private val UA_PRESETS = listOf(
    UaPreset(
        "MAG200",
        "Mozilla/5.0 (QtEmbedded; U; Linux; C) AppleWebKit/533.3 MAG200 stbapp ver: 4.3.1939"
    ),
    UaPreset(
        "MAG250",
        "Mozilla/5.0 (QtEmbedded; U; Linux; C) AppleWebKit/533.3 MAG250 stbapp ver: 4.3.1939"
    ),
    UaPreset(
        "MAG322",
        "Mozilla/5.0 (QtEmbedded; U; Linux; C) AppleWebKit/533.3 MAG322 stbapp ver: 4.3.1939"
    ),
    UaPreset(
        "Browser generico",
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0 Safari/537.36"
    )
)

@Composable
fun PortalEditDialog(
    portal: Portal,
    onDismiss: () -> Unit,
    onSave: (Portal) -> Unit
) {
    var name by remember { mutableStateOf(portal.name) }
    var url by remember { mutableStateOf(portal.url) }
    var mac by remember { mutableStateOf(portal.macAddress) }
    var useVpn by remember { mutableStateOf(portal.useVpn) }
    var profile by remember { mutableStateOf(portal.profile) }
    var useCustomUa by remember { mutableStateOf(portal.useCustomUserAgent) }
    var userAgent by remember { mutableStateOf(portal.userAgent) }
    var menuExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Modifica portale #${portal.id}") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome Playlist") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("Link Portale") },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(fontSize = 13.sp),
                    maxLines = 2
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = mac,
                    onValueChange = { mac = it },
                    label = { Text("MAC Address") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = useVpn, onCheckedChange = { useVpn = it })
                    Text("Usa VPN", style = MaterialTheme.typography.bodyMedium)
                }
                OutlinedTextField(
                    value = profile,
                    onValueChange = { profile = it },
                    label = { Text("Profilo") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = useCustomUa, onCheckedChange = { useCustomUa = it })
                    Text("Usa User Agent", style = MaterialTheme.typography.bodyMedium)
                }
                if (useCustomUa) {
                    OutlinedTextField(
                        value = userAgent,
                        onValueChange = { userAgent = it },
                        label = { Text("Inserisci o seleziona User-Agent") },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(fontSize = 12.sp),
                        maxLines = 2
                    )
                    Spacer(Modifier.height(4.dp))
                    OutlinedButton(onClick = { menuExpanded = true }) {
                        Text("Seleziona preset")
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        UA_PRESETS.forEach { preset ->
                            DropdownMenuItem(
                                text = { Text(preset.label) },
                                onClick = {
                                    userAgent = preset.value
                                    menuExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSave(
                    portal.copy(
                        name = name.trim(),
                        url = url.trim(),
                        macAddress = mac.trim(),
                        useVpn = useVpn,
                        profile = profile.trim(),
                        useCustomUserAgent = useCustomUa,
                        userAgent = userAgent.trim()
                    )
                )
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annulla")
            }
        }
    )
}
