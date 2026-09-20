package com.example.data.tax

import com.example.data.model.Employee
import com.example.data.model.EmployeeStatus
import com.example.data.model.MaritalStatus
import com.example.data.model.PayrollRecord

object IndonesianTaxEngine {

    // Maximum wage caps for Indonesian BPJS
    const val BPJS_KESEHATAN_MAX_WAGE = 12000000.0 // Batas maksimal perhitungan BPJS Kesehatan
    const val BPJS_JP_MAX_WAGE = 10042300.0 // Batas maksimal upah Jaminan Pensiun

    /**
     * Menentukan Kode PTKP berdasarkan Status Kawin dan Tanggungan
     * Tanggungan maksimal diakui perpajakan Indonesia: 3 orang
     */
    fun determinePtkpCode(maritalStatus: MaritalStatus, dependentsCount: Int): String {
        val cappedDependents = dependentsCount.coerceIn(0, 3)
        return if (maritalStatus == MaritalStatus.KAWIN) {
            "K/$cappedDependents"
        } else {
            "TK/$cappedDependents"
        }
    }

    /**
     * Menentukan Kategori TER (PP 58/2023 & PMK 168/2023)
     * Kategori A: TK/0, TK/1, K/0
     * Kategori B: TK/2, TK/3, K/1, K/2
     * Kategori C: K/3
     */
    fun determineTerCategory(ptkpCode: String): String {
        return when (ptkpCode) {
            "TK/0", "TK/1", "K/0" -> "TER A"
            "TK/2", "TK/3", "K/1", "K/2" -> "TER B"
            "K/3" -> "TER C"
            else -> "TER A"
        }
    }

    /**
     * Menghitung Tarif Efektif Bulanan PPh 21 TER (dalam persentase, misal 0.0125 untuk 1.25%)
     */
    fun calculateTerRate(terCategory: String, monthlyGrossIncome: Double): Double {
        if (monthlyGrossIncome <= 0.0) return 0.0

        return when (terCategory) {
            "TER A" -> getTerRateA(monthlyGrossIncome)
            "TER B" -> getTerRateB(monthlyGrossIncome)
            "TER C" -> getTerRateC(monthlyGrossIncome)
            else -> getTerRateA(monthlyGrossIncome)
        }
    }

    private fun getTerRateA(income: Double): Double = when {
        income <= 5400000.0 -> 0.0
        income <= 5650000.0 -> 0.0025
        income <= 5950000.0 -> 0.0050
        income <= 6300000.0 -> 0.0075
        income <= 6750000.0 -> 0.0100
        income <= 7500000.0 -> 0.0125
        income <= 8550000.0 -> 0.0150
        income <= 9650000.0 -> 0.0175
        income <= 10050000.0 -> 0.0200
        income <= 10350000.0 -> 0.0225
        income <= 10700000.0 -> 0.0250
        income <= 11050000.0 -> 0.0300
        income <= 11600000.0 -> 0.0350
        income <= 12500000.0 -> 0.0400
        income <= 13750000.0 -> 0.0500
        income <= 15100000.0 -> 0.0600
        income <= 16950000.0 -> 0.0700
        income <= 19750000.0 -> 0.0800
        income <= 24150000.0 -> 0.0900
        income <= 26450000.0 -> 0.1000
        income <= 28000000.0 -> 0.1100
        income <= 30050000.0 -> 0.1200
        income <= 32400000.0 -> 0.1300
        income <= 35400000.0 -> 0.1400
        income <= 39100000.0 -> 0.1500
        income <= 43850000.0 -> 0.1600
        income <= 47800000.0 -> 0.1700
        income <= 51400000.0 -> 0.1800
        income <= 56300000.0 -> 0.1900
        income <= 62200000.0 -> 0.2000
        income <= 68600000.0 -> 0.2100
        income <= 77500000.0 -> 0.2200
        income <= 89000000.0 -> 0.2300
        income <= 103000000.0 -> 0.2400
        income <= 125000000.0 -> 0.2500
        income <= 157000000.0 -> 0.2600
        income <= 206000000.0 -> 0.2700
        income <= 337000000.0 -> 0.2800
        income <= 454000000.0 -> 0.2900
        income <= 550000000.0 -> 0.3000
        income <= 695000000.0 -> 0.3100
        income <= 910000000.0 -> 0.3200
        income <= 1400000000.0 -> 0.3300
        else -> 0.3400
    }

    private fun getTerRateB(income: Double): Double = when {
        income <= 6200000.0 -> 0.0
        income <= 6500000.0 -> 0.0025
        income <= 6850000.0 -> 0.0050
        income <= 7300000.0 -> 0.0075
        income <= 9200000.0 -> 0.0100
        income <= 10750000.0 -> 0.0150
        income <= 12300000.0 -> 0.0200
        income <= 14150000.0 -> 0.0300
        income <= 16450000.0 -> 0.0400
        income <= 19450000.0 -> 0.0500
        income <= 23600000.0 -> 0.0700
        income <= 28350000.0 -> 0.0900
        income <= 34800000.0 -> 0.1100
        income <= 42100000.0 -> 0.1300
        income <= 51000000.0 -> 0.1500
        income <= 60000000.0 -> 0.1700
        income <= 70000000.0 -> 0.1900
        income <= 82000000.0 -> 0.2100
        income <= 102000000.0 -> 0.2300
        income <= 130000000.0 -> 0.2500
        income <= 170000000.0 -> 0.2700
        income <= 260000000.0 -> 0.2800
        income <= 400000000.0 -> 0.2900
        income <= 500000000.0 -> 0.3000
        income <= 650000000.0 -> 0.3100
        income <= 850000000.0 -> 0.3200
        income <= 1300000000.0 -> 0.3300
        else -> 0.3400
    }

    private fun getTerRateC(income: Double): Double = when {
        income <= 6600000.0 -> 0.0
        income <= 6950000.0 -> 0.0025
        income <= 7350000.0 -> 0.0050
        income <= 7800000.0 -> 0.0075
        income <= 8850000.0 -> 0.0100
        income <= 9800000.0 -> 0.0125
        income <= 10950000.0 -> 0.0150
        income <= 12050000.0 -> 0.0200
        income <= 12950000.0 -> 0.0300
        income <= 14150000.0 -> 0.0400
        income <= 16000000.0 -> 0.0500
        income <= 19150000.0 -> 0.0600
        income <= 23600000.0 -> 0.0800
        income <= 28650000.0 -> 0.1000
        income <= 34800000.0 -> 0.1200
        income <= 42100000.0 -> 0.1400
        income <= 51000000.0 -> 0.1600
        income <= 60000000.0 -> 0.1800
        income <= 70000000.0 -> 0.2000
        income <= 82000000.0 -> 0.2200
        income <= 102000000.0 -> 0.2400
        income <= 130000000.0 -> 0.2600
        income <= 170000000.0 -> 0.2700
        income <= 260000000.0 -> 0.2800
        income <= 400000000.0 -> 0.2900
        income <= 500000000.0 -> 0.3000
        income <= 650000000.0 -> 0.3100
        income <= 850000000.0 -> 0.3200
        income <= 1300000000.0 -> 0.3300
        else -> 0.3400
    }

    /**
     * Menghitung Gaji Lengkap, BPJS, PPh 21 TER, dan Take Home Pay
     */
    fun calculatePayroll(
        employee: Employee,
        periodMonth: Int,
        periodYear: Int,
        activeDaysWorked: Int,
        overtimeHours: Double,
        alphaDays: Int,
        lateMinutes: Int,
        dailyAlphaDeductionPolicy: Double,
        autoDeductAlpha: Boolean
    ): PayrollRecord {
        val marital = employee.maritalEnum
        val ptkpCode = determinePtkpCode(marital, employee.dependentsCount)
        val terCategory = determineTerCategory(ptkpCode)

        // 1. Gross Earnings calculation according to Employee Status
        val (baseAmount, grossBeforeAllowances) = when (employee.statusEnum) {
            EmployeeStatus.TETAP -> {
                Pair(employee.baseSalary, employee.baseSalary)
            }
            EmployeeStatus.HARIAN -> {
                // Base salary is daily wage rate * active days
                val totalDailyWage = employee.baseSalary * activeDaysWorked.coerceAtLeast(0)
                Pair(employee.baseSalary, totalDailyWage)
            }
            EmployeeStatus.BORONGAN -> {
                // Base salary is piece-rate unit production target
                val totalPieceRate = employee.baseSalary * activeDaysWorked.coerceAtLeast(1)
                Pair(employee.baseSalary, totalPieceRate)
            }
        }

        // Overtime calculation (Depnaker standard: 1.5x of hourly rate for first hours)
        val hourlyWage = if (employee.statusEnum == EmployeeStatus.TETAP) {
            employee.baseSalary / 173.0
        } else {
            (employee.baseSalary / 8.0)
        }
        val overtimePay = overtimeHours * hourlyWage * 1.5

        val transport = if (employee.statusEnum == EmployeeStatus.TETAP) employee.transportAllowance else (employee.transportAllowance / 22.0 * activeDaysWorked)
        val meal = if (employee.statusEnum == EmployeeStatus.TETAP) employee.mealAllowance else (employee.mealAllowance / 22.0 * activeDaysWorked)
        val performance = if (alphaDays == 0 && lateMinutes < 30) employee.performanceAllowance else (employee.performanceAllowance * 0.5)

        val totalGrossIncome = grossBeforeAllowances + overtimePay + transport + meal + performance

        // 2. Disciplinary Deductions (Absensi Alfa & Terlambat)
        val absenceDeduction = if (autoDeductAlpha && alphaDays > 0) {
            dailyAlphaDeductionPolicy * alphaDays
        } else {
            0.0
        }
        val lateDeduction = if (lateMinutes > 15) {
            (lateMinutes / 15) * 25000.0
        } else {
            0.0
        }

        // 3. BPJS Ketenagakerjaan & Kesehatan Calculation
        val bpjsWageBase = if (employee.statusEnum == EmployeeStatus.TETAP) employee.baseSalary else grossBeforeAllowances
        val bpjsKesehatanBase = bpjsWageBase.coerceAtMost(BPJS_KESEHATAN_MAX_WAGE)
        val bpjsJpBase = bpjsWageBase.coerceAtMost(BPJS_JP_MAX_WAGE)

        val bpjsJhtEmployee = if (employee.bpjsKetenagakerjaanActive) bpjsWageBase * 0.02 else 0.0
        val bpjsJpEmployee = if (employee.bpjsKetenagakerjaanActive) bpjsJpBase * 0.01 else 0.0
        val bpjsKesehatanEmployee = if (employee.bpjsKesehatanActive) bpjsKesehatanBase * 0.01 else 0.0

        val bpjsJhtCompany = if (employee.bpjsKetenagakerjaanActive) bpjsWageBase * 0.037 else 0.0
        val bpjsJpCompany = if (employee.bpjsKetenagakerjaanActive) bpjsJpBase * 0.02 else 0.0
        val bpjsJkkCompany = if (employee.bpjsKetenagakerjaanActive) bpjsWageBase * 0.0024 else 0.0
        val bpjsJkmCompany = if (employee.bpjsKetenagakerjaanActive) bpjsWageBase * 0.0030 else 0.0
        val bpjsKesehatanCompany = if (employee.bpjsKesehatanActive) bpjsKesehatanBase * 0.04 else 0.0

        // 4. Pajak PPh 21 TER (Peraturan Pemerintah PP 58/2023)
        val terRate = calculateTerRate(terCategory, totalGrossIncome)
        val pph21WithheldAmount = totalGrossIncome * terRate

        // 5. Net Salary (Take Home Pay)
        val totalDeductions = absenceDeduction + lateDeduction + bpjsJhtEmployee + bpjsJpEmployee + bpjsKesehatanEmployee + pph21WithheldAmount
        val netTakeHomePay = (totalGrossIncome - totalDeductions).coerceAtLeast(0.0)

        return PayrollRecord(
            employeeId = employee.id,
            employeeNik = employee.nik,
            employeeName = employee.name,
            department = employee.department,
            employeeStatus = employee.employeeStatus,
            periodMonth = periodMonth,
            periodYear = periodYear,
            baseSalary = baseAmount,
            activeDaysWorked = activeDaysWorked,
            overtimeHours = overtimeHours,
            overtimePay = overtimePay,
            allowanceTransport = transport,
            allowanceMeal = meal,
            allowancePerformance = performance,
            totalGrossIncome = totalGrossIncome,
            alphaDays = alphaDays,
            absenceDeduction = absenceDeduction,
            lateDeduction = lateDeduction,
            bpjsJhtEmployee = bpjsJhtEmployee,
            bpjsJpEmployee = bpjsJpEmployee,
            bpjsJhtCompany = bpjsJhtCompany,
            bpjsJkkCompany = bpjsJkkCompany,
            bpjsJkmCompany = bpjsJkmCompany,
            bpjsJpCompany = bpjsJpCompany,
            bpjsKesehatanEmployee = bpjsKesehatanEmployee,
            bpjsKesehatanCompany = bpjsKesehatanCompany,
            maritalStatus = employee.maritalStatus,
            dependentsCount = employee.dependentsCount,
            ptkpCode = ptkpCode,
            terCategory = terCategory,
            terEffectiveRatePercentage = terRate * 100.0,
            pph21WithheldAmount = pph21WithheldAmount,
            totalDeductions = totalDeductions,
            netTakeHomePay = netTakeHomePay,
            isPublished = true,
            emailSlipSent = true,
            sentEmailAddress = employee.email,
            thirdPartyAccountingStatus = "SYNCED_TO_ACCURATE"
        )
    }
}
