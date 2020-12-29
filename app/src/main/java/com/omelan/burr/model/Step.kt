package com.omelan.burr.model

import androidx.compose.ui.graphics.Color
import com.omelan.burr.ui.brown
import com.omelan.burr.ui.green


enum class StepType {
    ADD_COFFEE {
        override fun getColor(): Color {
            return brown
        }
    },
    WATER {
        override fun getColor(): Color {
            return Color.Blue
        }
    },
    WAIT {
        override fun getColor(): Color {
            return green
        }
    },
    OTHER {
        override fun getColor(): Color {
            return Color.Gray
        }
    };

    abstract fun getColor(): Color
}

data class Step(val id: Int, val name: String, val time: Int, val type: StepType, val value: Int? = null)