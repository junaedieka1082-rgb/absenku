package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payroll_records")
data class PayrollRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val employeeId: Long,
    val employeeNik: String,
    val employeeName: String,
    val department: String,
    val employeeStatus: String, // TETAP, HARIAN, BORONGAN
    val periodMonth: Int, // 1 - 12
    val periodYear: Int, // e.g. 2024 / 2026
    
    // Earnings
    val baseSalary: Double,
    val activeDaysWorked: Int = 22,
    val overtimeHours: Double = 0.0,
    val overtimePay: Double = 0.0,
    val allowanceTransport: Double = 0.0,
    val allowanceMeal: Double = 0.0,
    val allowancePerformance: Double = 0.0,
    val totalGrossIncome: Double,

    // Deductions
    val alphaDays: Int = 0,
    val absenceDeduction: Double = 0.0,
    val lateDeduction: Double = 0.0,
    
    // BPJS Ketenagakerjaan (Beban Karyawan)
    val bpjsJhtEmployee: Double = 0.0, // 2%
    val bpjsJpEmployee: Double = 0.0,  // 1%
    
    // BPJS Ketenagakerjaan (Beban Perusahaan / Info)
    val bpjsJhtCompany: Double = 0.0,  // 3.7%
    val bpjsJkkCompany: Double = 0.0,  // 0.24%
    val bpjsJkmCompany: Double = 0.0,  // 0.30%
    val bpjsJpCompany: Double = 0.0,   // 2.0%

    // BPJS Kesehatan
    val bpjsKesehatanEmployee: Double = 0.0, // 1%
    val bpjsKesehatanCompany: Double = 0.0,  // 4%

    // Perpajakan PPh 21 (PP 58/2023 & PMK 168/2023 - TER)
    val maritalStatus: String,
    val dependentsCount: Int,
    val ptkpCode: String, // TK/0..TK/3, K/0..K/3
    val terCategory: String, // Kategori TER A, B, atau C
    val terEffectiveRatePercentage: Double, // e.g. 1.25%
    val pph21WithheldAmount: Double,

    // Net Take Home Pay
    val totalDeductions: Double,
    val netTakeHomePay: Double,

    // Status & Integrations
    val isPublished: Boolean = true,
    val emailSlipSent: Boolean = true,
    val sentEmailAddress: String = "",
    val thirdPartyAccountingStatus: String = "SYNCED_TO_ACCURATE", // SYNCED_TO_ACCURATE, SYNCED_TO_JURNAL, PENDING
    val generatedTimestamp: Long = System.currentTimeMillis()
)
