package com.wifiscanner.model

import android.net.wifi.ScanResult
import android.net.wifi.WifiManager

/**
 * Data class representing detailed information about a WiFi network.
 *
 * @property ssid The network's SSID (Service Set Identifier)
 * @property bssid The network's BSSID (Basic Service Set Identifier)
 * @property signalStrength Strength of the WiFi signal in dBm
 * @property frequency Network frequency in MHz
 * @property securityType Type of security for the network
 * @property channelWidth Width of the WiFi channel
 * @property capabilities Raw capabilities string from the scan result
 */
data class WifiNetworkDetails(
    val ssid: String,
    val bssid: String,
    val signalStrength: Int,
    val frequency: Int,
    val securityType: SecurityType,
    val channelWidth: Int,
    val capabilities: String
) {
    /**
     * Enum representing different WiFi security types
     */
    enum class SecurityType {
        OPEN,
        WEP,
        WPA,
        WPA2,
        WPA3,
        UNKNOWN
    }

    companion object {
        /**
         * Extracts WiFi network details from a ScanResult
         *
         * @param scanResult The WiFi scan result to extract details from
         * @return A WifiNetworkDetails object with extracted information
         */
        fun fromScanResult(scanResult: ScanResult): WifiNetworkDetails {
            return WifiNetworkDetails(
                ssid = scanResult.SSID ?: "Unknown",
                bssid = scanResult.BSSID ?: "Unknown",
                signalStrength = scanResult.level,
                frequency = scanResult.frequency,
                securityType = determineSecurityType(scanResult.capabilities),
                channelWidth = scanResult.channelWidth,
                capabilities = scanResult.capabilities
            )
        }

        /**
         * Determines the security type of a WiFi network based on its capabilities
         *
         * @param capabilities Raw capabilities string from the scan result
         * @return The determined SecurityType
         */
        private fun determineSecurityType(capabilities: String): SecurityType {
            return when {
                capabilities.contains("WPA3") -> SecurityType.WPA3
                capabilities.contains("WPA2") -> SecurityType.WPA2
                capabilities.contains("WPA") -> SecurityType.WPA
                capabilities.contains("WEP") -> SecurityType.WEP
                capabilities.contains("Open") -> SecurityType.OPEN
                else -> SecurityType.UNKNOWN
            }
        }

        /**
         * Checks if a WiFi network is considered secure
         *
         * @return Boolean indicating if the network is secure
         */
        fun WifiNetworkDetails.isSecure(): Boolean {
            return securityType != SecurityType.OPEN && 
                   securityType != SecurityType.UNKNOWN
        }
    }
}