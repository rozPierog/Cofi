package com.omelan.cofi.pages.settings

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.omelan.cofi.R
import com.omelan.cofi.components.PiPAwareAppBar
import com.omelan.cofi.components.WebView

@Composable
fun Licenses(goBack: () -> Unit) {
    Scaffold(
        topBar = {
            PiPAwareAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.settings_licenses_title),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = goBack) {
                        Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) {
        WebView(urlToRender = "file:///android_asset/open_source_licenses.html")
    }
}

@Preview
@Composable
fun LicensesPreview() {
    Licenses(goBack = {})
}