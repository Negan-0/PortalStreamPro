package com.portalstream.app.data

import android.content.Context
import java.io.File
import org.json.JSONArray
import org.json.JSONObject

// Salva i portali in un file JSON interno: numerazione progressiva garantita dall'id
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
                        url = o.getString("url")
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
                }
            )
        }
        file.writeText(arr.toString())
    }
}
