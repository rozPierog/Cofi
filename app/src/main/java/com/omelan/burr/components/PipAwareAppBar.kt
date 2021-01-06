package com.omelan.burr.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import com.omelan.burr.MainActivityViewModel
import com.omelan.burr.R


@Composable
fun PiPAwareAppBar(isInPiP: Boolean, mainActivityViewModel: MainActivityViewModel = viewModel()) {
    if (!isInPiP) {
        val topPaddingInDp = mainActivityViewModel.statusBarHeight.observeAsState(0.dp)
        Column {
            Surface(
                elevation = 8.dp,
                modifier = Modifier.fillMaxWidth().height(topPaddingInDp.value)
                    .background(colorResource(id = R.color.navigationBar)),
            ) {}
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.app_name))
                },
                elevation = 0.dp,
                backgroundColor = colorResource(id = R.color.navigationBar),
                contentColor = colorResource(id = R.color.textPrimary),
            )
        }
    }
}

@Composable
@Preview
fun PiPAwareAppBarPreview() {
    PiPAwareAppBar(isInPiP = false)
}