package com.omelan.cofi.pages.settings

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.height
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.google.accompanist.navigation.animation.composable
import com.omelan.cofi.pages.settings.licenses.LicensesList

fun Modifier.settingsItemModifier(
    onClick: () -> Unit,
    enabled: Boolean = true,
    unlimitedHeight: Boolean = false,
) = composed {
    val modifier = this.clickable(
        onClick = onClick,
        role = Role.Button,
        interactionSource = remember { MutableInteractionSource() },
        enabled = enabled,
        indication = rememberRipple(bounded = true),
    )
    if (unlimitedHeight) {
        return@composed modifier
    }
    return@composed modifier.height(56.dp)
}


@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.settings(navController: NavController, goBack: () -> Unit) {
    navigation(startDestination = "settings_list", route = "settings") {
        composable("settings_list") {
            AppSettings(
                goBack = goBack,
                goToAbout = {
                    navController.navigate("about")
                },
                goToBackupRestore = {
                    navController.navigate("backup")
                },
                goToTimerSettings = {
                    navController.navigate("timer")
                },
            )
        }
        composable("timer") {
            TimerSettings(goBack = goBack)
        }
        composable("backup") {
            BackupRestoreSettings(
                goBack = goBack,
                goToRoot = {
                    navController.navigate("list") {
                        popUpTo("list") {
                            inclusive = true
                        }
                    }
                },
            )
        }
        composable("about") {
            AppSettingsAbout(
                goBack = goBack,
                openLicenses = {
                    navController.navigate("licenses")
                },
            )
        }
        composable("about") {
            AppSettingsAbout(
                goBack = goBack,
                openLicenses = {
                    navController.navigate("licenses")
                },
            )
        }
        composable("licenses") {
            LicensesList(goBack = goBack)
        }
    }
}
