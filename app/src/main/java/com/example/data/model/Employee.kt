package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class EmployeeStatus(val displayName: String) {
    TETAP("Karyawan Tetap"),
    HARIAN("Pegawai Harian"),
    BORONGAN("Borongan")
}

enum class MaritalStatus(val displayName: String) {
    TIDAK_KAWIN("Tidak Kawin"),
    KAWIN("Kawin")
}

@Entity(tableName = "employees")
data class Employee(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nik: String,
    val name: String,
    val email: String,
    val phone: String,
    val department: String,
    val position: String,
    val employeeStatus: String = EmployeeStatus.TETAP.name,
    val baseSalary: Double, // Gaji pokok bulanan (Tetap), Tarif harian (Harian), atau Tarif unit borongan
    val transportAllowance: Double = 500000.0,
    val mealAllowance: Double = 600000.0,
    val performanceAllowance: Double = 1000000.0,
    val maritalStatus: String = MaritalStatus.TIDAK_KAWIN.name,
    val dependentsCount: Int = 0, // 0 - 3
    val ptkpCode: String = "TK/0", // TK/0..TK/3, K/0..K/3
    val bpjsKesehatanActive: Boolean = true,
    val bpjsKetenagakerjaanActive: Boolean = true,
    val isFieldWorker: Boolean = false, // Karyawan Lapangan (GPS mobile check-in anywhere)
    val biometricFaceEnrolled: Boolean = true,
    val biometricFingerEnrolled: Boolean = true,
    val activeStatus: Boolean = true
) {
    val statusEnum: EmployeeStatus
        get() = try {
            EmployeeStatus.valueOf(employeeStatus)
        } catch (e: Exception) {
            EmployeeStatus.TETAP
        }

    val maritalEnum: MaritalStatus
        get() = try {
            MaritalStatus.valueOf(maritalStatus)
        } catch (e: Exception) {
            MaritalStatus.TIDAK_KAWIN
        }
}
