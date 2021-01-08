package com.omelan.burr.pages.settings

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.omelan.burr.components.PiPAwareAppBar
import com.omelan.burr.components.WebView

@Composable
fun Licenses(goBack: () -> Unit) {
    Scaffold(topBar = {
        PiPAwareAppBar(
            title = { Text(text = "Open source licenses", maxLines = 1, overflow = TextOverflow.Ellipsis) },
            navigationIcon = {
                IconButton(onClick = goBack) {
                    Icon(imageVector = Icons.Rounded.ArrowBack)
                }
            })
    }) {
        WebView(urlToRender = "file:///android_asset/open_source_licenses.html")
    }
}

@Preview
@Composable
fun LicensesPreview() {
    Licenses(goBack = {})
}