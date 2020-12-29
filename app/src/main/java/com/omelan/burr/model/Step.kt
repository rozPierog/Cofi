package com.omelan.burr.model

import androidx.compose.ui.graphics.Color


enum class StepType {
    ADD_COFFEE {
        override fun getColor(): Color {
            return Color.Black
        }
    },
    WATER {
        override fun getColor(): Color {
            return Color.Blue
        }
    },
    WAIT {
        override fun getColor(): Color {
            return Color.Green
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