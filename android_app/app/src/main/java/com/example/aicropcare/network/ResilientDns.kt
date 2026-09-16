package com.example.aicropcare.network

import okhttp3.Dns
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.InetAddress
import java.net.URL
import java.net.UnknownHostException

/**
 * Resilient DNS Resolver for Android OkHttp.
 *
 * Resolves hostnames using standard System DNS first. If system DNS fails
 * (e.g., due to local ISP IPv6 CNAME lookup timeouts or router DNS issues),
 * it queries Google Public DNS and Cloudflare DNS over HTTPS (DoH) over port 443,
 * matching Google Chrome's DNS resolution resilience while preserving full SSL/TLS
 * certificate and hostname verification.
 */
object ResilientDns : Dns {

    private val RENDER_FALLBACK_IPS = listOf("216.24.57.15", "216.24.57.7")

    override fun lookup(hostname: String): List<InetAddress> {
        // 1. Primary: Standard Android System DNS (POSIX getaddrinfo)
        try {
            val systemAddresses = Dns.SYSTEM.lookup(hostname)
            if (systemAddresses.isNotEmpty()) {
                return systemAddresses
            }
        } catch (_: UnknownHostException) {
            // System DNS failed on this network; proceed to resilient fallbacks
        } catch (_: Exception) {
            // Proceed to resilient fallbacks
        }

        // 2. Fallback: Google DNS-over-HTTPS (DoH)
        val googleAddresses = queryGoogleDoH(hostname)
        if (googleAddresses.isNotEmpty()) {
            return googleAddresses
        }

        // 3. Fallback: Cloudflare DNS-over-HTTPS (DoH)
        val cloudflareAddresses = queryCloudflareDoH(hostname)
        if (cloudflareAddresses.isNotEmpty()) {
            return cloudflareAddresses
        }

        // 4. Fallback: For Render backend host, use verified Render CDN origin IPs
        if (hostname.contains("onrender.com", ignoreCase = true)) {
            val fallbackAddresses = RENDER_FALLBACK_IPS.mapNotNull { ip ->
                try {
                    InetAddress.getByAddress(hostname, InetAddress.getByName(ip).address)
                } catch (_: Exception) {
                    null
                }
            }
            if (fallbackAddresses.isNotEmpty()) {
                return fallbackAddresses
            }
        }

        throw UnknownHostException("Unable to resolve host \"$hostname\": No address associated with hostname")
    }

    private fun queryGoogleDoH(hostname: String): List<InetAddress> {
        return try {
            val url = URL("https://dns.google/resolve?name=$hostname&type=A")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 6000
                readTimeout = 6000
                requestMethod = "GET"
                setRequestProperty("Accept", "application/json")
            }
            if (conn.responseCode == 200) {
                val responseText = conn.inputStream.bufferedReader().use { it.readText() }
                parseDnsJson(hostname, responseText)
            } else {
                emptyList()
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun queryCloudflareDoH(hostname: String): List<InetAddress> {
        return try {
            val url = URL("https://cloudflare-dns.com/dns-query?name=$hostname&type=A")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 6000
                readTimeout = 6000
                requestMethod = "GET"
                setRequestProperty("Accept", "application/dns-json")
            }
            if (conn.responseCode == 200) {
                val responseText = conn.inputStream.bufferedReader().use { it.readText() }
                parseDnsJson(hostname, responseText)
            } else {
                emptyList()
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun parseDnsJson(hostname: String, jsonStr: String): List<InetAddress> {
        val addresses = mutableListOf<InetAddress>()
        try {
            val root = JSONObject(jsonStr)
            val answers = root.optJSONArray("Answer")
            if (answers != null) {
                for (i in 0 until answers.length()) {
                    val ans = answers.getJSONObject(i)
                    // type 1 is 'A' record (IPv4)
                    val type = ans.optInt("type")
                    val data = ans.optString("data")
                    if (type == 1 && data.isNotBlank()) {
                        try {
                            val rawIpBytes = InetAddress.getByName(data.trim()).address
                            addresses.add(InetAddress.getByAddress(hostname, rawIpBytes))
                        } catch (_: Exception) {}
                    }
                }
            }
        } catch (_: Exception) {}
        return addresses
    }
}
