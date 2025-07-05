@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package com.omelan.cofi.pages.settings

import android.os.Build
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.omelan.cofi.R
import com.omelan.cofi.components.CofiSwitch
import com.omelan.cofi.components.PiPAwareAppBar
import com.omelan.cofi.components.createAppBarBehavior
import com.omelan.cofi.model.DataStore
import com.omelan.cofi.model.NEXT_STEP_ENABLED_DEFAULT_VALUE
import com.omelan.cofi.utils.getDefaultPadding
import kotlinx.coroutines.launch

@Composable
fun AppearanceSettings(goBack: () -> Unit) {
    val context = LocalContext.current
    val dataStore = DataStore(context)
    val coroutineScope = rememberCoroutineScope()
    val appBarBehavior = createAppBarBehavior()
    val isDynamicThemeEnabled by dataStore.getDynamicThemeSetting().collectAsState(
        NEXT_STEP_ENABLED_DEFAULT_VALUE,
    )
    val toggleDynamicTheme: () -> Unit = {
        coroutineScope.launch {
            dataStore.setDynamicTheme(!isDynamicThemeEnabled)
        }
    }
    Scaffold(
        topBar = {
            PiPAwareAppBar(
                title = {
                    Text(
                        text = "Appearance Settings",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = goBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = null,
                        )
                    }
                },
                scrollBehavior = appBarBehavior,
            )
        },
    ) {
        LazyColumn(
            modifier = Modifier
                .nestedScroll(appBarBehavior.nestedScrollConnection)
                .fillMaxSize(),
            contentPadding = getDefaultPadding(
                paddingValues = it,
                additionalStartPadding = 0.dp,
                additionalEndPadding = 0.dp,
            ),
        ) {
            item {
                ListItem(
                    headlineContent = {
                        Text(text = "Dynamic Theme")
                    },
                    leadingContent = {
                        Icon(
                            painterResource(id = R.drawable.ic_picture_in_picture),
                            contentDescription = null,
                        )
                    },
                    modifier = Modifier
                        .settingsItemModifier(
                            onClick = toggleDynamicTheme,
                            enabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S,
                        ),
                    trailingContent = {
                        CofiSwitch(
                            modifier = Modifier.testTag("settings_timer_switch_pip"),
                            checked = isDynamicThemeEnabled,
                            onCheckedChange = { toggleDynamicTheme() },
                            enabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S,
                        )
                    },
                )
            }
        }
    }
}
