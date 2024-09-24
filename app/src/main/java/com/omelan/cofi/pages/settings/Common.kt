package com.omelan.cofi.pages.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.omelan.cofi.pages.settings.licenses.LicensesList
import com.omelan.cofi.share.pages.Destinations

fun Modifier.settingsItemModifier(
    onClick: () -> Unit,
    enabled: Boolean = true,
    unlimitedHeight: Boolean = false,
) = composed {
    val modifier = this.clickable(
        onClick = onClick,
        role = Role.Button,
        enabled = enabled,
    )
    if (unlimitedHeight) {
        return@composed modifier
    }
    return@composed modifier.height(56.dp)
}

fun NavGraphBuilder.settings(
    navController: NavController,
    goBack: () -> Unit = {
        navController.popBackStack()
    },
) {
    navigation(startDestination = Destinations.SETTINGS_LIST, route = Destinations.SETTINGS) {
        composable(Destinations.SETTINGS_LIST) {
            AppSettings(
                goBack = goBack,
                goToAbout = {
                    navController.navigate(Destinations.SETTINGS_ABOUT)
                },
                goToBackupRestore = {
                    navController.navigate(Destinations.SETTINGS_BACKUP)
                },
                goToTimerSettings = {
                    navController.navigate(Destinations.SETTINGS_TIMER)
                },
            )
        }
        composable(Destinations.SETTINGS_TIMER) {
            TimerSettings(goBack = goBack)
        }
        composable(Destinations.SETTINGS_BACKUP) {
            BackupRestoreSettings(
                goBack = goBack,
                goToRoot = {
                    navController.navigate(Destinations.SETTINGS_LIST) {
                        popUpTo(Destinations.SETTINGS_LIST) {
                            inclusive = true
                        }
                    }
                },
            )
        }
        composable(Destinations.SETTINGS_ABOUT) {
            AppSettingsAbout(
                goBack = goBack,
                openLicenses = {
                    navController.navigate(Destinations.SETTINGS_LICENSES)
                },
            )
        }
        composable(Destinations.SETTINGS_LICENSES) {
            LicensesList(goBack = goBack)
        }
    }
}
