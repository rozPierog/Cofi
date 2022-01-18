package com.omelan.cofi.components

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebSettingsCompat.FORCE_DARK_OFF
import androidx.webkit.WebSettingsCompat.FORCE_DARK_ON
import androidx.webkit.WebViewFeature

@Composable
fun WebView(urlToRender: String) {
    val darkTheme = isSystemInDarkTheme()
    AndroidView(
        factory = { context ->
            val webView = WebView(context)
            webView.webViewClient = WebViewClient()
            webView.loadUrl(urlToRender)
            return@AndroidView webView
        },
        update = { webView ->
            if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                if (darkTheme) {
                    WebSettingsCompat.setForceDark(webView.settings, FORCE_DARK_ON)
                } else {
                    WebSettingsCompat.setForceDark(webView.settings, FORCE_DARK_OFF)
                }
            }
        }
    )
}

@Preview
@Composable
fun WebViewPreview() {
    WebView(urlToRender = "file:///android_asset/open_source_licenses.html")
}