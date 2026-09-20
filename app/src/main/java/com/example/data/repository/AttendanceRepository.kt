package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.data.tax.IndonesianTaxEngine
import kotlinx.coroutines.flow.Flow

class AttendanceRepository(private val db: AppDatabase) {

    val allEmployees: Flow<List<Employee>> = db.employeeDao().getAllEmployees()
    val allAttendance: Flow<List<AttendanceRecord>> = db.attendanceDao().getAllAttendance()
    val allLeaves: Flow<List<LeaveRequest>> = db.leaveDao().getAllLeaves()
    val pendingLeaves: Flow<List<LeaveRequest>> = db.leaveDao().getPendingLeaves()
    val allPayroll: Flow<List<PayrollRecord>> = db.payrollDao().getAllPayroll()
    val companyPolicy: Flow<CompanyPolicy?> = db.companyDao().getCompanyPolicy()
    val announcements: Flow<List<Announcement>> = db.announcementDao().getAllAnnouncements()

    fun getAttendanceByDate(date: String): Flow<List<AttendanceRecord>> =
        db.attendanceDao().getAttendanceByDate(date)

    suspend fun getTodayAttendance(employeeId: Long, date: String): AttendanceRecord? =
        db.attendanceDao().getTodayAttendance(employeeId, date)

    suspend fun recordAttendance(record: AttendanceRecord): Long =
        db.attendanceDao().insertAttendance(record)

    suspend fun updateAttendance(record: AttendanceRecord) =
        db.attendanceDao().updateAttendance(record)

    suspend fun markWarningEmailSent(attendanceId: Long) =
        db.attendanceDao().markWarningEmailSent(attendanceId)

    suspend fun applySalaryDeduction(attendanceId: Long, deduction: Double) =
        db.attendanceDao().applySalaryDeduction(attendanceId, deduction)

    suspend fun submitLeave(leave: LeaveRequest): Long =
        db.leaveDao().insertLeave(leave)

    suspend fun updateLeaveStatus(id: Long, status: String, approvedBy: String, notes: String?) =
        db.leaveDao().updateApproval(id, status, approvedBy, notes)

    suspend fun updatePayroll(record: PayrollRecord) =
        db.payrollDao().updatePayroll(record)

    suspend fun markSlipEmailSent(id: Long) =
        db.payrollDao().markEmailSent(id)

    suspend fun updateAccountingSyncStatus(id: Long, status: String) =
        db.payrollDao().updateAccountingSyncStatus(id, status)

    suspend fun updateCompanyPolicy(policy: CompanyPolicy) =
        db.companyDao().savePolicy(policy)

    suspend fun postAnnouncement(announcement: Announcement): Long =
        db.announcementDao().insertAnnouncement(announcement)

    suspend fun addEmployee(employee: Employee): Long =
        db.employeeDao().insertEmployee(employee)

    suspend fun recalculateAllPayroll(
        month: Int,
        year: Int,
        policy: CompanyPolicy,
        employees: List<Employee>
    ) {
        val newPayrolls = employees.map { emp ->
            val activeDays = if (emp.statusEnum == EmployeeStatus.HARIAN) 21 else 22
            val overtime = if (emp.statusEnum == EmployeeStatus.TETAP) 8.0 else 4.0
            IndonesianTaxEngine.calculatePayroll(
                employee = emp,
                periodMonth = month,
                periodYear = year,
                activeDaysWorked = activeDays,
                overtimeHours = overtime,
                alphaDays = 0,
                lateMinutes = 0,
                dailyAlphaDeductionPolicy = policy.dailyAlphaDeductionAmount,
                autoDeductAlpha = policy.autoDeductSalaryOnAlpha
            )
        }
        db.payrollDao().insertPayrolls(newPayrolls)
    }
}
