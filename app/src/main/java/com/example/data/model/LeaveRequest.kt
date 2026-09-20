package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class LeaveCategory(val label: String) {
    CUTI_TAHUNAN("Cuti Tahunan"),
    SAKIT("Sakit (Surat Dokter)"),
    DATANG_TERLAMBAT("Izin Datang Terlambat"),
    KEPERLUAN_LAINNYA("Izin Keperluan Lainnya")
}

enum class LeaveApprovalStatus(val label: String, val colorHex: Long) {
    MENUNGGU("Menunggu Persetujuan Manajer", 0xFFED6C02),
    DISETUJUI("Disetujui Atasan", 0xFF2E7D32),
    DITOLAK("Ditolak Atasan", 0xFFD32F2F)
}

@Entity(tableName = "leave_requests")
data class LeaveRequest(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val employeeId: Long,
    val employeeName: String,
    val department: String,
    val category: String = LeaveCategory.CUTI_TAHUNAN.name,
    val startDate: String, // YYYY-MM-DD
    val endDate: String, // YYYY-MM-DD
    val durationDays: Int = 1,
    val reason: String,
    val attachmentUrl: String? = null,
    val status: String = LeaveApprovalStatus.MENUNGGU.name,
    val approvedBy: String? = null,
    val managerEmailNotified: Boolean = true,
    val responseNotes: String? = null,
    val submissionTimestamp: Long = System.currentTimeMillis()
) {
    val categoryEnum: LeaveCategory
        get() = try {
            LeaveCategory.valueOf(category)
        } catch (e: Exception) {
            LeaveCategory.CUTI_TAHUNAN
        }

    val approvalStatusEnum: LeaveApprovalStatus
        get() = try {
            LeaveApprovalStatus.valueOf(status)
        } catch (e: Exception) {
            LeaveApprovalStatus.MENUNGGU
        }
}
