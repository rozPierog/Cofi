package com.omelan.cofi.pages.settings.licenses

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.omelan.cofi.share.utils.parseJsonToDependencyList
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

const val testJson = """
[ 
  {
    "project": "Accompanist FlowLayout library",
    "description": "Utilities for Jetpack Compose",
    "version": "0.20.2",
    "developers": [
      "Google"
    ],
    "url": "https://github.com/google/accompanist/",
    "year": null,
    "licenses": [
      {
        "license": "The Apache Software License, Version 2.0",
        "license_url": "http://www.apache.org/licenses/LICENSE-2.0.txt"
      }
    ],
    "dependency": "com.google.accompanist:accompanist-flowlayout:0.20.2"
  },
  {
    "project": "Accompanist Insets library",
    "description": "Utilities for Jetpack Compose",
    "version": "0.20.2",
    "developers": [
      "Google"
    ],
    "url": "https://github.com/google/accompanist/",
    "year": 2077,
    "licenses": [
      {
        "license": "The Apache Software License, Version 2.0",
        "license_url": "http://www.apache.org/licenses/LICENSE-2.0.txt"
      }
    ],
    "dependency": "com.google.accompanist:accompanist-insets:0.20.2"
  }
]
"""

@RunWith(JUnit4::class)
class LicenseTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testDependency() {
        val dependencies = testJson.parseJsonToDependencyList()
        composeTestRule.setContent {
            DependencyItem(dependency = dependencies.first())
        }

        composeTestRule.onNodeWithTag("dependency_column").assertIsEnabled()
            .assertHasClickAction()
            .assertTextContains("Utilities for Jetpack Compose")
            .assertTextContains("Copyright © 20XX Google")
            .assertTextContains("The Apache Software License, Version 2.0")
    }
}
