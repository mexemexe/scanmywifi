package com.wifiscanner.model

import android.net.wifi.ScanResult
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class WifiNetworkDetailsTest {

    @Mock
    private lateinit var mockScanResult: ScanResult

    @Test
    fun `test fromScanResult with WPA2 network`() {
        // Arrange
        `when`(mockScanResult.SSID).thenReturn("TestNetwork")
        `when`(mockScanResult.BSSID).thenReturn("00:11:22:33:44:55")
        `when`(mockScanResult.level).thenReturn(-65)
        `when`(mockScanResult.frequency).thenReturn(2437)
        `when`(mockScanResult.capabilities).thenReturn("[WPA2-PSK-CCMP][ESS]")
        `when`(mockScanResult.channelWidth).thenReturn(1)

        // Act
        val networkDetails = WifiNetworkDetails.fromScanResult(mockScanResult)

        // Assert
        assertEquals("TestNetwork", networkDetails.ssid)
        assertEquals("00:11:22:33:44:55", networkDetails.bssid)
        assertEquals(-65, networkDetails.signalStrength)
        assertEquals(2437, networkDetails.frequency)
        assertEquals(WifiNetworkDetails.SecurityType.WPA2, networkDetails.securityType)
        assertEquals(1, networkDetails.channelWidth)
    }

    @Test
    fun `test fromScanResult with open network`() {
        // Arrange
        `when`(mockScanResult.SSID).thenReturn("OpenNetwork")
        `when`(mockScanResult.BSSID).thenReturn("66:77:88:99:AA:BB")
        `when`(mockScanResult.level).thenReturn(-80)
        `when`(mockScanResult.frequency).thenReturn(5180)
        `when`(mockScanResult.capabilities).thenReturn("[ESS]")
        `when`(mockScanResult.channelWidth).thenReturn(2)

        // Act
        val networkDetails = WifiNetworkDetails.fromScanResult(mockScanResult)

        // Assert
        assertEquals("OpenNetwork", networkDetails.ssid)
        assertEquals("66:77:88:99:AA:BB", networkDetails.bssid)
        assertEquals(-80, networkDetails.signalStrength)
        assertEquals(5180, networkDetails.frequency)
        assertEquals(WifiNetworkDetails.SecurityType.OPEN, networkDetails.securityType)
        assertEquals(2, networkDetails.channelWidth)
    }

    @Test
    fun `test isSecure method`() {
        // Arrange
        val secureNetwork = WifiNetworkDetails(
            ssid = "SecureNetwork",
            bssid = "00:11:22:33:44:55",
            signalStrength = -55,
            frequency = 2437,
            securityType = WifiNetworkDetails.SecurityType.WPA2,
            channelWidth = 1,
            capabilities = "[WPA2-PSK-CCMP][ESS]"
        )

        val openNetwork = WifiNetworkDetails(
            ssid = "OpenNetwork",
            bssid = "66:77:88:99:AA:BB",
            signalStrength = -80,
            frequency = 5180,
            securityType = WifiNetworkDetails.SecurityType.OPEN,
            channelWidth = 2,
            capabilities = "[ESS]"
        )

        // Assert
        assertTrue(secureNetwork.isSecure())
        assertFalse(openNetwork.isSecure())
    }

    @Test
    fun `test security type determination`() {
        // Test various security type scenarios
        val scenarios = listOf(
            "[WPA3-SAE-CCMP]" to WifiNetworkDetails.SecurityType.WPA3,
            "[WPA2-PSK-CCMP][ESS]" to WifiNetworkDetails.SecurityType.WPA2,
            "[WPA-PSK-TKIP][ESS]" to WifiNetworkDetails.SecurityType.WPA,
            "[WEP][ESS]" to WifiNetworkDetails.SecurityType.WEP,
            "[ESS]" to WifiNetworkDetails.SecurityType.OPEN,
            "" to WifiNetworkDetails.SecurityType.UNKNOWN
        )

        scenarios.forEach { (capabilities, expectedType) ->
            val mockResult = ScanResult().apply {
                this.capabilities = capabilities
            }
            val details = WifiNetworkDetails.fromScanResult(mockResult)
            assertEquals(expectedType, details.securityType)
        }
    }
}