package com.example.util

import com.example.data.model.AttendanceRecord
import com.example.data.model.PayrollRecord
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FormatHelper {

    private val rupiahFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
        maximumFractionDigits = 0
    }

    fun formatRupiah(amount: Double): String {
        return try {
            rupiahFormat.format(amount)
        } catch (e: Exception) {
            "Rp " + String.format(Locale.GERMANY, "%,.0f", amount)
        }
    }

    fun formatPercentage(rate: Double): String {
        return String.format(Locale.US, "%.2f%%", rate)
    }

    fun getTodayDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    fun getCurrentTimeString(): String {
        return SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
    }

    fun formatDisplayDate(dateStr: String): String {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formatter = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
            val date = parser.parse(dateStr)
            if (date != null) formatter.format(date) else dateStr
        } catch (e: Exception) {
            dateStr
        }
    }

    /**
     * Menghasilkan Data format CSV / Excel untuk Ekspor Audit Kehadiran
     */
    fun generateAttendanceCsv(records: List<AttendanceRecord>): String {
        val sb = StringBuilder()
        sb.append("ID,Nama Karyawan,Departemen,Tanggal,Jam Masuk,Jam Pulang,Status,Metode Biometrik,Jarak Kantor (m),Lokasi GPS,Status Cloud\n")
        records.forEach { r ->
            sb.append("${r.id},")
            sb.append("\"${r.employeeName}\",")
            sb.append("\"${r.department}\",")
            sb.append("${r.date},")
            sb.append("${r.checkInTime ?: "-"},")
            sb.append("${r.checkOutTime ?: "-"},")
            sb.append("${r.status},")
            sb.append("${r.biometricMethod},")
            sb.append("${r.officeDistanceMeters.toInt()},")
            sb.append("\"${r.checkInAddress}\",")
            sb.append("${r.cloudSyncStatus}\n")
        }
        return sb.toString()
    }

    /**
     * Menghasilkan Data format CSV / Excel untuk Rekapitulasi Payroll & Pajak PPh 21
     */
    fun generatePayrollCsv(payrolls: List<PayrollRecord>): String {
        val sb = StringBuilder()
        sb.append("NIK,Nama Karyawan,Departemen,Status,Periode,Gaji Pokok,Hari Kerja,Lembur,Tunjangan,Total Bruto,Potongan Alfa,BPJS Karyawan,PPh 21 TER,Tarif TER %,Take Home Pay,Status Akuntansi\n")
        payrolls.forEach { p ->
            val totalBpjsEmp = p.bpjsJhtEmployee + p.bpjsJpEmployee + p.bpjsKesehatanEmployee
            val totalAllowances = p.allowanceTransport + p.allowanceMeal + p.allowancePerformance
            sb.append("\"${p.employeeNik}\",")
            sb.append("\"${p.employeeName}\",")
            sb.append("\"${p.department}\",")
            sb.append("\"${p.employeeStatus}\",")
            sb.append("${p.periodMonth}/${p.periodYear},")
            sb.append("${p.baseSalary.toLong()},")
            sb.append("${p.activeDaysWorked},")
            sb.append("${p.overtimePay.toLong()},")
            sb.append("${totalAllowances.toLong()},")
            sb.append("${p.totalGrossIncome.toLong()},")
            sb.append("${p.absenceDeduction.toLong()},")
            sb.append("${totalBpjsEmp.toLong()},")
            sb.append("${p.pph21WithheldAmount.toLong()},")
            sb.append("${String.format(Locale.US, "%.2f", p.terEffectiveRatePercentage)}%,")
            sb.append("${p.netTakeHomePay.toLong()},")
            sb.append("\"${p.thirdPartyAccountingStatus}\"\n")
        }
        return sb.toString()
    }
}
