package com.omelan.cofi.components

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun OutlinedNumbersField(
    modifier: Modifier = Modifier,
    allowFloat: Boolean = true,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    value: String,
    onValueChange: (String) -> Unit,
    imeAction: ImeAction = ImeAction.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    val pattern = remember { Regex(if (allowFloat) "^\\d+(?:\\.\\d{0,2})?\$" else "^\\d+\$") }
    OutlinedTextField(
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        prefix = prefix,
        suffix = suffix,
        supportingText = supportingText,
        singleLine = true,
        value = value,
        onValueChange = { newValue ->
            if (newValue.matches(pattern)) {
                if (value == "0.0" && newValue != "0.0" && newValue.endsWith("0.0")) {
                    onValueChange(newValue.removeSuffix("0.0"))
                } else if (value == "0" && newValue != "0" && newValue.endsWith("0")) {
                    onValueChange(newValue.removeSuffix("0"))
                } else {
                    onValueChange(newValue.removePrefix("0"))
                }
            } else if (newValue.startsWith(".")) {
                onValueChange("0$newValue")
            } else if (newValue.isBlank()) {
                onValueChange("0")
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal,
            imeAction = imeAction,
        ),
        keyboardActions = keyboardActions,
        modifier = modifier,
    )
}
