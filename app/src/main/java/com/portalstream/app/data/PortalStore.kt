package com.portalstream.app.data

import android.content.Context
import java.io.File
import org.json.JSONArray
import org.json.JSONObject

// Portali salvati in JSON interno. I campi nuovi usano opt* con default:
// i portali salvati con versioni vecce dell'app continuano a funzionare.
class PortalStore(context: Context) {

    private val file = File(context.filesDir, "portals.json")

    fun load(): List<Portal> {
        if (!file.exists()) return emptyList()
        return try {
            val arr = JSONArray(file.readText())
            val list = mutableListOf<Portal>()
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                list.add(
                    Portal(
                        id = o.getInt("id"),
                        name = o.optString("name"),
                        type = runCatching { PortalType.valueOf(o.getString("type")) }
                            .getOrDefault(PortalType.UNKNOWN),
                        url = o.optString("url"),
                        macAddress = o.optString("mac", ""),
                        useVpn = o.optBoolean("vpn", false),
                        profile = o.optString("profile", ""),
                        useCustomUserAgent = o.optBoolean("customUa", false),
                        userAgent = o.optString("ua", "")
                    )
                )
            }
            list.sortedBy { it.id }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun add(url: String): Portal {
        val portals = load().toMutableList()
        val nextId = (portals.maxOfOrNull { it.id } ?: 0) + 1
        val portal = Portal(
            id = nextId,
            name = url,
            type = PortalType.detect(url),
            url = url
        )
        portals.add(portal)
        write(portals)
        return portal
    }

    fun update(portal: Portal) {
        write(load().map { if (it.id == portal.id) portal else it })
    }

    fun delete(id: Int) {
        write(load().filterNot { it.id == id })
    }

    private fun write(portals: List<Portal>) {
        val arr = JSONArray()
        portals.forEach { p ->
            arr.put(
                JSONObject().apply {
                    put("id", p.id)
                    put("name", p.name)
                    put("type", p.type.name)
                    put("url", p.url)
                    put("mac", p.macAddress)
                    put("vpn", p.useVpn)
                    put("profile", p.profile)
                    put("customUa", p.useCustomUserAgent)
                    put("ua", p.userAgent)
                }
            )
        }
        file.writeText(arr.toString())
    }
}
