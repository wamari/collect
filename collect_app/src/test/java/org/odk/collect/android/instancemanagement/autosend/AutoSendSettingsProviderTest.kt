package org.odk.collect.android.instancemanagement.autosend

import android.net.ConnectivityManager
import android.net.NetworkInfo
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.odk.collect.androidshared.network.NetworkStateProvider
import org.odk.collect.projects.Project
import org.odk.collect.settings.InMemSettingsProvider
import org.odk.collect.settings.keys.ProjectKeys

class AutoSendSettingsProviderTest {
    private val networkStateProvider: NetworkStateProvider = mock()
    private val settingsProvider = InMemSettingsProvider()

    private val projectId = Project.DEMO_PROJECT_NAME

    @Test
    fun `return false when networkInfo is null`() {
        val autoSendSettingsProvider = setupAutoSendSettingProvider(networkInfo = null)

        assertFalse(autoSendSettingsProvider.isAutoSendEnabledInSettings(projectId))
    }

    @Test
    fun `return false when autosend is disabled in settings`() {
        val autoSendSettingsProvider = setupAutoSendSettingProvider(
            autoSendOption = "off",
            networkType = ConnectivityManager.TYPE_WIFI
        )

        assertFalse(autoSendSettingsProvider.isAutoSendEnabledInSettings(projectId))
    }

    @Test
    fun `return false when autosend is enabled for wifi only but network type is cellular`() {
        val autoSendSettingsProvider = setupAutoSendSettingProvider(
            autoSendOption = "wifi_only",
            networkType = ConnectivityManager.TYPE_MOBILE
        )

        assertFalse(autoSendSettingsProvider.isAutoSendEnabledInSettings(projectId))
    }

    @Test
    fun `return true when autosend is enabled for wifi only and network type is wifi`() {
        val autoSendSettingsProvider = setupAutoSendSettingProvider(
            autoSendOption = "wifi_only",
            networkType = ConnectivityManager.TYPE_WIFI
        )

        assertTrue(autoSendSettingsProvider.isAutoSendEnabledInSettings(projectId))
    }

    @Test
    fun `return false when autosend is enabled for cellular only but network type is wifi`() {
        val autoSendSettingsProvider = setupAutoSendSettingProvider(
            autoSendOption = "cellular_only",
            networkType = ConnectivityManager.TYPE_WIFI
        )

        assertFalse(autoSendSettingsProvider.isAutoSendEnabledInSettings(projectId))
    }

    @Test
    fun `return true when autosend is enabled for cellular only and network type is cellular`() {
        val autoSendSettingsProvider = setupAutoSendSettingProvider(
            autoSendOption = "cellular_only",
            networkType = ConnectivityManager.TYPE_MOBILE
        )

        assertTrue(autoSendSettingsProvider.isAutoSendEnabledInSettings(projectId))
    }

    @Test
    fun `return true when autosend is enabled for wifi and cellular and network type is wifi`() {
        val autoSendSettingsProvider = setupAutoSendSettingProvider(
            autoSendOption = "wifi_and_cellular",
            networkType = ConnectivityManager.TYPE_WIFI
        )

        assertTrue(autoSendSettingsProvider.isAutoSendEnabledInSettings(projectId))
    }

    @Test
    fun `return true when autosend is enabled for wifi and cellular and network type is cellular`() {
        val autoSendSettingsProvider = setupAutoSendSettingProvider(
            autoSendOption = "wifi_and_cellular",
            networkType = ConnectivityManager.TYPE_MOBILE
        )

        assertTrue(autoSendSettingsProvider.isAutoSendEnabledInSettings(projectId))
    }

    private fun setupAutoSendSettingProvider(
        autoSendOption: String? = null,
        networkType: Int? = null,
        networkInfo: NetworkInfo? = mock()
    ): AutoSendSettingsProvider {
        networkType?.let {
            whenever(networkInfo?.type).thenReturn(networkType)
        }
        whenever(networkStateProvider.networkInfo).thenReturn(networkInfo)
        settingsProvider.getUnprotectedSettings(projectId).save(ProjectKeys.KEY_AUTOSEND, autoSendOption)

        return AutoSendSettingsProvider(networkStateProvider, settingsProvider)
    }
}
