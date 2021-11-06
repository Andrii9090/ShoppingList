package com.kasandco.familyfinance.app.finance.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName

@Entity(tableName = "finance_history_sync")
data class FinanceHistorySync(
    @PrimaryKey(autoGenerate = true)val id:Long,
    val item: Long,
    @SerialName("date_mod")
    @ColumnInfo(name = "date_mod")
    val date_mod: String)
