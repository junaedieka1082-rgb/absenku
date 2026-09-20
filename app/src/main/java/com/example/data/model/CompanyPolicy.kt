package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "company_policy")
data class CompanyPolicy(
    @PrimaryKey
    val id: Int = 1,
    val companyName: String = "PT NUSANTARA PRIMA TEKNOLOGI",
    val brandColorHex: String = "#0D47A1", // Default Corporate Blue
    val officeName: String = "Kantor Pusat Jakarta (SCBD)",
    val officeLatitude: Double = -6.2241, // SCBD Jakarta coordinates
    val officeLongitude: Double = 106.8097,
    val officeRadiusMeters: Double = 150.0,
    val officeAddress: String = "Sudirman Central Business District Lot 28, Senayan, Jakarta Selatan",
    val workStartHour: String = "08:00",
    val workEndHour: String = "17:00",
    val gracePeriodMinutes: Int = 15,
    val autoSendWarningEmailOnAlpha: Boolean = true,
    val autoDeductSalaryOnAlpha: Boolean = true,
    val dailyAlphaDeductionAmount: Double = 250000.0,
    val accountingProvider: String = "Accurate Cloud & Jurnal.id",
    val fingerprintApiEndpoint: String = "https://api.presensipro.corp/v2/biometric/zkteco/sync",
    val maxActiveEmployeesCapacity: Int = 1000,
    val cloudAutoSync: Boolean = true
)
