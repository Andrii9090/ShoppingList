package com.kasandco.familyfinance.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FinanceHistoryApiModel(
    val id: Long?,
    val comment: String,
    val total: Double,
    val date_create: String,
    val email: String,
    val type: Int,
    val finance_category: Long,
    val local_id: Long,
    val is_delete: Boolean,
    val date_mod: String
)
