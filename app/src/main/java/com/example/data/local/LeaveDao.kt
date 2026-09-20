package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.LeaveRequest
import kotlinx.coroutines.flow.Flow

@Dao
interface LeaveDao {
    @Query("SELECT * FROM leave_requests ORDER BY submissionTimestamp DESC")
    fun getAllLeaves(): Flow<List<LeaveRequest>>

    @Query("SELECT * FROM leave_requests WHERE employeeId = :employeeId ORDER BY submissionTimestamp DESC")
    fun getLeavesForEmployee(employeeId: Long): Flow<List<LeaveRequest>>

    @Query("SELECT * FROM leave_requests WHERE status = 'MENUNGGU' ORDER BY submissionTimestamp DESC")
    fun getPendingLeaves(): Flow<List<LeaveRequest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeave(request: LeaveRequest): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeaves(requests: List<LeaveRequest>)

    @Update
    suspend fun updateLeave(request: LeaveRequest)

    @Query("UPDATE leave_requests SET status = :status, approvedBy = :approvedBy, responseNotes = :notes WHERE id = :id")
    suspend fun updateApproval(id: Long, status: String, approvedBy: String, notes: String?)
}
