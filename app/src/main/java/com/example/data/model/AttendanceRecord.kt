package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class AttendanceStatus(val label: String, val colorHex: Long) {
    HADIR("Hadir Tepat Waktu", 0xFF2E7D32),
    TERLAMBAT("Datang Terlambat", 0xFFED6C02),
    IZIN("Izin", 0xFF0288D1),
    SAKIT("Sakit", 0xFF7B1FA2),
    CUTI("Cuti Disetujui", 0xFF00796B),
    ALPA("Alpa (Tanpa Keterangan)", 0xFFD32F2F)
}

enum class BiometricValidationType(val displayName: String) {
    FACE_SCAN("Pemindaian Wajah 3D"),
    FINGERPRINT("Sidik Jari Biometrik"),
    GPS_FIELD("Validasi GPS Lapangan"),
    FINGERPRINT_MACHINE_API("API Mesin Absensi Terintegrasi")
}

@Entity(tableName = "attendance_records")
data class AttendanceRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val employeeId: Long,
    val employeeName: String,
    val department: String,
    val date: String, // YYYY-MM-DD
    val checkInTime: String? = null, // HH:mm:ss
    val checkOutTime: String? = null, // HH:mm:ss
    val checkInLatitude: Double = 0.0,
    val checkInLongitude: Double = 0.0,
    val checkInAddress: String = "",
    val status: String = AttendanceStatus.HADIR.name,
    val biometricMethod: String = BiometricValidationType.FACE_SCAN.name,
    val isFieldWork: Boolean = false,
    val officeDistanceMeters: Double = 0.0,
    val withinOfficeRadius: Boolean = true,
    val cloudSyncStatus: String = "SYNCED_TO_CLOUD", // SYNCED_TO_CLOUD, PENDING_SYNC
    val warningEmailSent: Boolean = false, // Automatically generated email warning if ALPA
    val isSalaryDeducted: Boolean = false,
    val deductionAmount: Double = 0.0,
    val lateDurationMinutes: Int = 0,
    val notes: String? = null
) {
    val statusEnum: AttendanceStatus
        get() = try {
            AttendanceStatus.valueOf(status)
        } catch (e: Exception) {
            AttendanceStatus.HADIR
        }
}
