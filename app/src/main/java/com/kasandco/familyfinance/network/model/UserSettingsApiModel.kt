package com.kasandco.familyfinance.network.model

import kotlinx.serialization.Serializable

@Serializable
data class UserSettingsApiModel(
    val settings_json:HashMap<String, String>
)
