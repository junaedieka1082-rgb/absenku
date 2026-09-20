package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.PayrollRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface PayrollDao {
    @Query("SELECT * FROM payroll_records ORDER BY periodYear DESC, periodMonth DESC, id DESC")
    fun getAllPayroll(): Flow<List<PayrollRecord>>

    @Query("SELECT * FROM payroll_records WHERE periodMonth = :month AND periodYear = :year ORDER BY id DESC")
    fun getPayrollByPeriod(month: Int, year: Int): Flow<List<PayrollRecord>>

    @Query("SELECT * FROM payroll_records WHERE employeeId = :employeeId ORDER BY periodYear DESC, periodMonth DESC")
    fun getPayrollForEmployee(employeeId: Long): Flow<List<PayrollRecord>>

    @Query("SELECT SUM(netTakeHomePay) FROM payroll_records WHERE periodMonth = :month AND periodYear = :year")
    fun getTotalNetPayroll(month: Int, year: Int): Flow<Double?>

    @Query("SELECT SUM(pph21WithheldAmount) FROM payroll_records WHERE periodMonth = :month AND periodYear = :year")
    fun getTotalPPh21Withheld(month: Int, year: Int): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayroll(record: PayrollRecord): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayrolls(records: List<PayrollRecord>)

    @Update
    suspend fun updatePayroll(record: PayrollRecord)

    @Query("UPDATE payroll_records SET emailSlipSent = 1, isPublished = 1 WHERE id = :id")
    suspend fun markEmailSent(id: Long)

    @Query("UPDATE payroll_records SET thirdPartyAccountingStatus = :status WHERE id = :id")
    suspend fun updateAccountingSyncStatus(id: Long, status: String)
}
