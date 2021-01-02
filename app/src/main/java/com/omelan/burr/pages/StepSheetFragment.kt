package com.omelan.burr.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.res.colorResource
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.omelan.burr.R
import com.omelan.burr.components.StepListItem
import com.omelan.burr.components.StepProgress
import com.omelan.burr.model.Step

class StepSheetFragment: BottomSheetDialogFragment() {
    companion object{
        fun newInstance(step: Step?): StepSheetFragment{
            val args = Bundle()

            val fragment = StepSheetFragment()
            fragment.arguments = args
            return fragment
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        container?.setContent {
            val (recipeName, setRecipeName) = remember { mutableStateOf("") }
            val (recipeDescription, setRecipeDescription) = remember { mutableStateOf("") }
            TextField(
                value = recipeName,
                onValueChange = { setRecipeName(it) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text(text = "Name") },
                backgroundColor = colorResource(id = R.color.navigationBar)
            )
            TextField(
                value = recipeDescription,
                onValueChange = { setRecipeDescription(it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Description") },
                backgroundColor = colorResource(id = R.color.navigationBar)
            )
        }
        return container
    }
}