package com.omelan.cofi.components

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun WebView(urlToRender: String) {
    AndroidView(viewBlock = ::WebView) { webView ->
        with(webView) {
            webViewClient = WebViewClient()
            loadUrl(urlToRender)
        }
    }
}

@Preview
@Composable
fun WebViewPreview() {
    WebView(urlToRender = "file:///android_asset/open_source_licenses.html")
}