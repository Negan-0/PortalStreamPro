package com.portalstream.app.data

enum class PortalType(val label: String) {
    M3U("M3U"),
    XSTREAM("XStream"),
    MAG("MAG/MAC"),
    UNKNOWN("Sconosciuto");

    companion object {
        fun detect(url: String): PortalType = when {
            url.contains("player_api.php", ignoreCase = true) ||
                (url.contains("get.php", ignoreCase = true) && url.contains("username=", ignoreCase = true)) -> XSTREAM
            url.contains("stalker_portal", ignoreCase = true) ||
                url.contains("/portal.php", ignoreCase = true) ||
                url.contains("/server/load.php", ignoreCase = true) -> MAG
            url.contains(".m3u", ignoreCase = true) ||
                url.contains("playlist", ignoreCase = true) -> M3U
            else -> UNKNOWN
        }
    }
}

data class Portal(
    val id: Int,
    val name: String,
    val type: PortalType,
    val url: String
)
