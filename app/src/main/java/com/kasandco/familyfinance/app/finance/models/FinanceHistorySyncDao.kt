package com.kasandco.familyfinance.app.finance.models

import androidx.room.Dao
import androidx.room.Query
import com.kasandco.familyfinance.dao.BaseDao

@Dao
interface FinanceHistorySyncDao : BaseDao<FinanceHistorySync> {
    @Query("SELECT * FROM finance_history_sync")
    fun getAll():List<FinanceHistorySync>
    @Query("DELETE FROM finance_history_sync")
    fun clear()
}