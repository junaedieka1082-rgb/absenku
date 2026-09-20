package com.example.data.sample

import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.data.tax.IndonesianTaxEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object SampleDataInitializer {

    suspend fun initializeIfEmpty(database: AppDatabase) = withContext(Dispatchers.IO) {
        val existingPolicy = database.companyDao().getCompanyPolicySync()
        if (existingPolicy != null) {
            return@withContext // Already seeded
        }

        // 1. Seed Company Policy
        val defaultPolicy = CompanyPolicy(
            id = 1,
            companyName = "PT NUSANTARA TEKNOLOGI PRIMA",
            brandColorHex = "#0D47A1",
            officeName = "Menara Sudirman SCBD (Head Office)",
            officeLatitude = -6.2241,
            officeLongitude = 106.8097,
            officeRadiusMeters = 150.0,
            officeAddress = "Sudirman Central Business District Lot 28, Senayan, Jakarta Selatan",
            workStartHour = "08:00",
            workEndHour = "17:00",
            gracePeriodMinutes = 15,
            autoSendWarningEmailOnAlpha = true,
            autoDeductSalaryOnAlpha = true,
            dailyAlphaDeductionAmount = 250000.0,
            accountingProvider = "Accurate Cloud & Jurnal.id",
            fingerprintApiEndpoint = "https://api.presensipro.corp/v2/biometric/zkteco/sync",
            maxActiveEmployeesCapacity = 1000,
            cloudAutoSync = true
        )
        database.companyDao().savePolicy(defaultPolicy)

        // 2. Seed Employees (Representing 1000 active staff ecosystem)
        val initialEmployees = listOf(
            Employee(
                id = 1,
                nik = "EMP-00101",
                name = "Raden Surya Adiputra",
                email = "surya.adiputra@nusantaraprima.co.id",
                phone = "081234567890",
                department = "Teknologi & Informasi",
                position = "Lead Mobile Software Engineer",
                employeeStatus = EmployeeStatus.TETAP.name,
                baseSalary = 18500000.0,
                transportAllowance = 1500000.0,
                mealAllowance = 1200000.0,
                performanceAllowance = 2500000.0,
                maritalStatus = MaritalStatus.KAWIN.name,
                dependentsCount = 2,
                ptkpCode = "K/2",
                bpjsKesehatanActive = true,
                bpjsKetenagakerjaanActive = true,
                isFieldWorker = false
            ),
            Employee(
                id = 2,
                nik = "EMP-00102",
                name = "Aisyah Putri Rahmadani",
                email = "aisyah.putri@nusantaraprima.co.id",
                phone = "081298765432",
                department = "Human Capital & Keuangan",
                position = "HR & Payroll Specialist",
                employeeStatus = EmployeeStatus.TETAP.name,
                baseSalary = 12500000.0,
                transportAllowance = 1000000.0,
                mealAllowance = 900000.0,
                performanceAllowance = 1500000.0,
                maritalStatus = MaritalStatus.TIDAK_KAWIN.name,
                dependentsCount = 0,
                ptkpCode = "TK/0",
                bpjsKesehatanActive = true,
                bpjsKetenagakerjaanActive = true,
                isFieldWorker = false
            ),
            Employee(
                id = 3,
                nik = "EMP-00103",
                name = "Bambang Kurniawan",
                email = "bambang.k@nusantaraprima.co.id",
                phone = "085611223344",
                department = "Operasional Lapangan",
                position = "Supervisor Logistik & Field Project",
                employeeStatus = EmployeeStatus.TETAP.name,
                baseSalary = 9500000.0,
                transportAllowance = 2000000.0,
                mealAllowance = 1000000.0,
                performanceAllowance = 1000000.0,
                maritalStatus = MaritalStatus.KAWIN.name,
                dependentsCount = 3,
                ptkpCode = "K/3",
                bpjsKesehatanActive = true,
                bpjsKetenagakerjaanActive = true,
                isFieldWorker = true // Karyawan Lapangan GPS
            ),
            Employee(
                id = 4,
                nik = "EMP-00201",
                name = "Joko Wicaksono",
                email = "joko.w@nusantaraprima.co.id",
                phone = "087755667788",
                department = "Gudang & Distribusi",
                position = "Staff Pergudangan Harian",
                employeeStatus = EmployeeStatus.HARIAN.name,
                baseSalary = 230000.0, // Tarif Harian Rp 230.000 / hari
                transportAllowance = 300000.0,
                mealAllowance = 400000.0,
                performanceAllowance = 300000.0,
                maritalStatus = MaritalStatus.KAWIN.name,
                dependentsCount = 1,
                ptkpCode = "K/1",
                bpjsKesehatanActive = true,
                bpjsKetenagakerjaanActive = true,
                isFieldWorker = false
            ),
            Employee(
                id = 5,
                nik = "EMP-00301",
                name = "Slamet Santoso",
                email = "slamet.s@nusantaraprima.co.id",
                phone = "081344332211",
                department = "Divisi Manufaktur & Fabrikasi",
                position = "Teknisi Borongan Instalasi Presisi",
                employeeStatus = EmployeeStatus.BORONGAN.name,
                baseSalary = 350000.0, // Upah Borongan per Satuan Unit
                transportAllowance = 200000.0,
                mealAllowance = 300000.0,
                performanceAllowance = 500000.0,
                maritalStatus = MaritalStatus.TIDAK_KAWIN.name,
                dependentsCount = 1,
                ptkpCode = "TK/1",
                bpjsKesehatanActive = true,
                bpjsKetenagakerjaanActive = true,
                isFieldWorker = true
            ),
            Employee(
                id = 6,
                nik = "EMP-00104",
                name = "Nadia Citra Lestari",
                email = "nadia.citra@nusantaraprima.co.id",
                phone = "082199887766",
                department = "Pemasaran & Bisnis",
                position = "Corporate Account Manager",
                employeeStatus = EmployeeStatus.TETAP.name,
                baseSalary = 14000000.0,
                transportAllowance = 1500000.0,
                mealAllowance = 1000000.0,
                performanceAllowance = 3000000.0,
                maritalStatus = MaritalStatus.TIDAK_KAWIN.name,
                dependentsCount = 0,
                ptkpCode = "TK/0",
                bpjsKesehatanActive = true,
                bpjsKetenagakerjaanActive = true,
                isFieldWorker = true
            )
        )
        database.employeeDao().insertEmployees(initialEmployees)

        // 3. Seed Today & Recent Attendance Records
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val initialAttendance = listOf(
            AttendanceRecord(
                id = 1,
                employeeId = 1,
                employeeName = "Raden Surya Adiputra",
                department = "Teknologi & Informasi",
                date = today,
                checkInTime = "07:52:14",
                checkOutTime = "17:15:30",
                checkInLatitude = -6.2242,
                checkInLongitude = 106.8096,
                checkInAddress = "Menara SCBD Lot 28, Jakarta Selatan",
                status = AttendanceStatus.HADIR.name,
                biometricMethod = BiometricValidationType.FACE_SCAN.name,
                isFieldWork = false,
                officeDistanceMeters = 35.0,
                withinOfficeRadius = true,
                cloudSyncStatus = "SYNCED_TO_CLOUD"
            ),
            AttendanceRecord(
                id = 2,
                employeeId = 2,
                employeeName = "Aisyah Putri Rahmadani",
                department = "Human Capital & Keuangan",
                date = today,
                checkInTime = "07:58:02",
                checkOutTime = null,
                checkInLatitude = -6.2240,
                checkInLongitude = 106.8098,
                checkInAddress = "Menara SCBD Lot 28, Jakarta Selatan",
                status = AttendanceStatus.HADIR.name,
                biometricMethod = BiometricValidationType.FINGERPRINT.name,
                isFieldWork = false,
                officeDistanceMeters = 24.0,
                withinOfficeRadius = true,
                cloudSyncStatus = "SYNCED_TO_CLOUD"
            ),
            AttendanceRecord(
                id = 3,
                employeeId = 3,
                employeeName = "Bambang Kurniawan",
                department = "Operasional Lapangan",
                date = today,
                checkInTime = "08:24:45",
                checkOutTime = null,
                checkInLatitude = -6.2941,
                checkInLongitude = 106.8790,
                checkInAddress = "Lokasi Proyek Infrastruktur Tol Simatupang",
                status = AttendanceStatus.TERLAMBAT.name,
                biometricMethod = BiometricValidationType.GPS_FIELD.name,
                isFieldWork = true,
                officeDistanceMeters = 8400.0,
                withinOfficeRadius = false,
                lateDurationMinutes = 24,
                cloudSyncStatus = "SYNCED_TO_CLOUD"
            ),
            AttendanceRecord(
                id = 4,
                employeeId = 4,
                employeeName = "Joko Wicaksono",
                department = "Gudang & Distribusi",
                date = today,
                checkInTime = "07:45:10",
                checkOutTime = null,
                checkInLatitude = -6.2243,
                checkInLongitude = 106.8095,
                checkInAddress = "Pusat Pergudangan SCBD",
                status = AttendanceStatus.HADIR.name,
                biometricMethod = BiometricValidationType.FINGERPRINT_MACHINE_API.name,
                isFieldWork = false,
                officeDistanceMeters = 40.0,
                withinOfficeRadius = true,
                cloudSyncStatus = "SYNCED_TO_CLOUD"
            ),
            AttendanceRecord(
                id = 5,
                employeeId = 5,
                employeeName = "Slamet Santoso",
                department = "Divisi Manufaktur & Fabrikasi",
                date = today,
                checkInTime = null,
                checkOutTime = null,
                status = AttendanceStatus.ALPA.name,
                biometricMethod = "NONE",
                isFieldWork = false,
                officeDistanceMeters = 0.0,
                withinOfficeRadius = false,
                cloudSyncStatus = "SYNCED_TO_CLOUD",
                warningEmailSent = true, // Auto sent reprimand email
                isSalaryDeducted = true,
                deductionAmount = 250000.0,
                notes = "Tidak ada keterangan kehadiran sampai jam 10:00. Surat Peringatan & Teguran otomatis terkirim via email."
            ),
            AttendanceRecord(
                id = 6,
                employeeId = 6,
                employeeName = "Nadia Citra Lestari",
                department = "Pemasaran & Bisnis",
                date = today,
                checkInTime = "08:10:05",
                checkOutTime = null,
                checkInLatitude = -6.1950,
                checkInLongitude = 106.8220,
                checkInAddress = "Meeting Client - Hotel Indonesia Kempinski Jakarta",
                status = AttendanceStatus.HADIR.name,
                biometricMethod = BiometricValidationType.GPS_FIELD.name,
                isFieldWork = true,
                officeDistanceMeters = 3800.0,
                withinOfficeRadius = false,
                cloudSyncStatus = "SYNCED_TO_CLOUD"
            )
        )
        database.attendanceDao().insertAttendances(initialAttendance)

        // 4. Seed Leave Requests
        val initialLeaves = listOf(
            LeaveRequest(
                id = 1,
                employeeId = 1,
                employeeName = "Raden Surya Adiputra",
                department = "Teknologi & Informasi",
                category = LeaveCategory.CUTI_TAHUNAN.name,
                startDate = "2026-10-05",
                endDate = "2026-10-07",
                durationDays = 3,
                reason = "Acara keluarga tahunan di Yogyakarta",
                status = LeaveApprovalStatus.MENUNGGU.name,
                managerEmailNotified = true
            ),
            LeaveRequest(
                id = 2,
                employeeId = 2,
                employeeName = "Aisyah Putri Rahmadani",
                department = "Human Capital & Keuangan",
                category = LeaveCategory.SAKIT.name,
                startDate = "2026-09-15",
                endDate = "2026-09-16",
                durationDays = 2,
                reason = "Demam tinggi & radang tenggorokan (Surat dr. RS Siloam)",
                attachmentUrl = "https://internal.presensipro.corp/surat_dokter_aisyah.pdf",
                status = LeaveApprovalStatus.DISETUJUI.name,
                approvedBy = "Dr. Hendra Gunawan (VP HR)",
                responseNotes = "Disetujui. Istirahat yang cukup."
            ),
            LeaveRequest(
                id = 3,
                employeeId = 3,
                employeeName = "Bambang Kurniawan",
                department = "Operasional Lapangan",
                category = LeaveCategory.DATANG_TERLAMBAT.name,
                startDate = today,
                endDate = today,
                durationDays = 1,
                reason = "Mobil operasional mogok ban bocor di tol Lingkar Luar",
                status = LeaveApprovalStatus.DISETUJUI.name,
                approvedBy = "Ir. Wijaya (Manajer Operasional)",
                responseNotes = "Diterima dan dispensasi keterlambatan."
            )
        )
        database.leaveDao().insertLeaves(initialLeaves)

        // 5. Seed Payroll Calculation Records (Current Month - Indonesian Tax TER & BPJS)
        val payrollRecords = initialEmployees.map { emp ->
            val alphaCount = if (emp.id == 5L) 1 else 0
            val overtime = if (emp.id == 1L) 12.0 else if (emp.id == 4L) 16.0 else 4.0
            val activeDays = if (emp.statusEnum == EmployeeStatus.HARIAN) 21 else if (emp.statusEnum == EmployeeStatus.BORONGAN) 24 else 22

            IndonesianTaxEngine.calculatePayroll(
                employee = emp,
                periodMonth = 9,
                periodYear = 2026,
                activeDaysWorked = activeDays,
                overtimeHours = overtime,
                alphaDays = alphaCount,
                lateMinutes = if (emp.id == 3L) 24 else 0,
                dailyAlphaDeductionPolicy = defaultPolicy.dailyAlphaDeductionAmount,
                autoDeductAlpha = defaultPolicy.autoDeductSalaryOnAlpha
            )
        }
        database.payrollDao().insertPayrolls(payrollRecords)

        // 6. Seed Announcements
        val initialAnnouncements = listOf(
            Announcement(
                id = 1,
                title = "Pengumuman Cut-Off Penggajian & Sinkronisasi Pajak PPh 21 TER",
                content = "Diberitahukan kepada seluruh 1.000 karyawan bahwa cut-off absensi GPS dan klaim lembur bulan September berakhir tanggal 25. Perhitungan PPh 21 menggunakan regulasi TER PP 58/2023.",
                author = "HR & Finance Directorate",
                dateText = "20 September 2026",
                isPriority = true,
                isPushBroadcasted = true
            ),
            Announcement(
                id = 2,
                title = "Kebijakan Absensi GPS Karyawan Lapangan & Mesin Sidik Jari",
                content = "Karyawan dinas luar diwajibkan melakukan swafoto biometrik wajah dan menyalakan akurasi GPS tinggi. Mesin sidik jari di SCBD kini telah tersinkronisasi otomatis setiap 15 detik ke cloud.",
                author = "Operational & Security Team",
                dateText = "18 September 2026",
                isPriority = false,
                isPushBroadcasted = true
            )
        )
        database.announcementDao().insertAnnouncements(initialAnnouncements)
    }
}
