package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AttendanceRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {
    @Query("SELECT * FROM attendance_records ORDER BY date DESC, id DESC")
    fun getAllAttendance(): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance_records WHERE date = :date ORDER BY id DESC")
    fun getAttendanceByDate(date: String): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance_records WHERE employeeId = :employeeId ORDER BY date DESC")
    fun getAttendanceForEmployee(employeeId: Long): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance_records WHERE employeeId = :employeeId AND date = :date LIMIT 1")
    suspend fun getTodayAttendance(employeeId: Long, date: String): AttendanceRecord?

    @Query("SELECT COUNT(*) FROM attendance_records WHERE date = :date AND status = 'HADIR'")
    fun getPresentCount(date: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM attendance_records WHERE date = :date AND status = 'TERLAMBAT'")
    fun getLateCount(date: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM attendance_records WHERE date = :date AND status = 'ALPA'")
    fun getAbsentCount(date: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM attendance_records WHERE date = :date AND (status = 'IZIN' OR status = 'SAKIT' OR status = 'CUTI')")
    fun getPermitCount(date: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(record: AttendanceRecord): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendances(records: List<AttendanceRecord>)

    @Update
    suspend fun updateAttendance(record: AttendanceRecord)

    @Query("UPDATE attendance_records SET warningEmailSent = 1 WHERE id = :id")
    suspend fun markWarningEmailSent(id: Long)

    @Query("UPDATE attendance_records SET isSalaryDeducted = 1, deductionAmount = :deduction WHERE id = :id")
    suspend fun applySalaryDeduction(id: Long, deduction: Double)
}
