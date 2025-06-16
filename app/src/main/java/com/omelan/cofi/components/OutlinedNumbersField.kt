package com.omelan.cofi.components

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
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
            if (newValue.isEmpty()) {
                onValueChange("")
                return@OutlinedTextField
            }

            // --- Integer Handling (if not allowing float) ---
            if (!allowFloat) {
                if (newValue.all { it.isDigit() }) {
                    val processed = newValue.trimStart('0')
                    if (processed.isEmpty() && newValue.contains("0")) {
                        onValueChange("0")
                        return@OutlinedTextField
                    }
                    onValueChange(processed)
                }
                return@OutlinedTextField
            }

            // --- Float Handling ---
            var newText = newValue
            val originalHadDecimal = value.contains('.')
            val newHasNoDecimal = !newText.contains('.')
            if (originalHadDecimal && newHasNoDecimal) {
                val parts = value.split('.')
                if (parts.isNotEmpty()) {
                    val integer = parts[0]
                    val decimal = if (parts.size > 1) parts[1] else ""
                    if (newText == integer || (parts.size > 1 && newText == "${integer}${decimal}")) {
                        val potentialNewValue = newText.trimStart('0')
                        if (potentialNewValue.isEmpty()) {
                            onValueChange("0")
                            return@OutlinedTextField
                        }
                        if (potentialNewValue.all { it.isDigit() }) {
                            onValueChange(potentialNewValue)
                            return@OutlinedTextField
                        }
                    }
                }
            }

            if (!newText.all { it.isDigit() || it == '.' }) {
                return@OutlinedTextField
            }

            // Ensure only one decimal point
            // TODO: Move to TextFieldValue transformation and determine where new "." is
            if (newText.count { it == '.' } > 1) {
                if (newText.count { it == '.' } > value.count { it == '.' }) {
                    onValueChange(newText.replaceFirst(".", ""))
                    return@OutlinedTextField
                }
            }


            // Limit decimal places
            val decimalIndex = newText.indexOf('.')
            if (decimalIndex != -1) {
                val decimalPart = newText.substring(decimalIndex + 1)
                if (decimalPart.length > 2) {
                    newText = newText.substring(0, decimalIndex + 1 + 2)
                }
            }


            // Handle ".5" -> "0.5"
            if (newText.startsWith(".")) {
                newText = "0$newText"
            }

            // Handle "00.5" -> "0.5" and similar cases
            if (newText.startsWith("0") && newText.length > 1 && !newText.startsWith("0.")) {
                newText = newText.trimStart('0')
                if (newText.startsWith(".")) { // If "00.5" became ".5"
                    newText = "0$newText"
                } else if (newText.isEmpty()) { // If "000" became ""
                    newText = "0"
                }
            }


            // Final check: if after all this, the number is somehow invalid (e.g. just "."),
            // it might be better to revert or set to a sensible default like "0".
            // However, allowing intermediate states like "0." is good.
            if (newText == "." && value.isNotEmpty()) {
                onValueChange("0.")
                return@OutlinedTextField
            }

            onValueChange(newText)
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal,
            imeAction = imeAction,
        ),
        keyboardActions = keyboardActions,
        modifier = modifier,
    )
}
