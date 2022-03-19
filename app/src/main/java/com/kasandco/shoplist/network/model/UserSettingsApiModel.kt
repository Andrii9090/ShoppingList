package com.kasandco.shoplist.network.model

import kotlinx.serialization.Serializable

@Serializable
data class UserSettingsApiModel(
    val settings_json:HashMap<String, String>
)
