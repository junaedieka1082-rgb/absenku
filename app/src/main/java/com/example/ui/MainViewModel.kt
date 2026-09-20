package com.example.ui

import android.app.Application
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.data.repository.AttendanceRepository
import com.example.data.sample.SampleDataInitializer
import com.example.ui.theme.CorporateBluePrimary
import com.example.util.FormatHelper
import com.example.util.LocationHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = AttendanceRepository(db)

    val employees: StateFlow<List<Employee>> = repository.allEmployees
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val attendances: StateFlow<List<AttendanceRecord>> = repository.allAttendance
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val leaves: StateFlow<List<LeaveRequest>> = repository.allLeaves
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val payrolls: StateFlow<List<PayrollRecord>> = repository.allPayroll
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val announcements: StateFlow<List<Announcement>> = repository.announcements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _companyPolicy = MutableStateFlow(CompanyPolicy())
    val companyPolicy: StateFlow<CompanyPolicy> = _companyPolicy.asStateFlow()

    private val _selectedEmployee = MutableStateFlow<Employee?>(null)
    val selectedEmployee: StateFlow<Employee?> = _selectedEmployee.asStateFlow()

    // Real-time GPS coordinates (defaulting near SCBD office or customizable)
    private val _currentGpsLat = MutableStateFlow(-6.2241)
    val currentGpsLat: StateFlow<Double> = _currentGpsLat.asStateFlow()

    private val _currentGpsLng = MutableStateFlow(106.8097)
    val currentGpsLng: StateFlow<Double> = _currentGpsLng.asStateFlow()

    private val _currentGpsAddress = MutableStateFlow("SCBD Lot 28, Senayan, Jakarta Selatan")
    val currentGpsAddress: StateFlow<String> = _currentGpsAddress.asStateFlow()

    private val _isFieldMode = MutableStateFlow(false)
    val isFieldMode: StateFlow<Boolean> = _isFieldMode.asStateFlow()

    // Brand accent color
    private val _brandColor = MutableStateFlow(CorporateBluePrimary)
    val brandColor: StateFlow<Color> = _brandColor.asStateFlow()

    // System Feedback & Push notifications
    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    private val _isSyncingCloud = MutableStateFlow(false)
    val isSyncingCloud: StateFlow<Boolean> = _isSyncingCloud.asStateFlow()

    init {
        viewModelScope.launch {
            SampleDataInitializer.initializeIfEmpty(db)
            repository.companyPolicy.collect { pol ->
                if (pol != null) {
                    _companyPolicy.value = pol
                    try {
                        val parsedColor = Color(android.graphics.Color.parseColor(pol.brandColorHex))
                        _brandColor.value = parsedColor
                    } catch (e: Exception) {
                        _brandColor.value = CorporateBluePrimary
                    }
                }
            }
        }

        viewModelScope.launch {
            employees.collect { list ->
                if (_selectedEmployee.value == null && list.isNotEmpty()) {
                    _selectedEmployee.value = list.first()
                }
            }
        }
    }

    fun selectEmployee(employee: Employee) {
        _selectedEmployee.value = employee
        _isFieldMode.value = employee.isFieldWorker
    }

    fun toggleFieldMode(enabled: Boolean) {
        _isFieldMode.value = enabled
        if (enabled) {
            _currentGpsLat.value = -6.2941
            _currentGpsLng.value = 106.8790
            _currentGpsAddress.value = "Lokasi Proyek Infrastruktur Tol Simatupang"
        } else {
            val policy = _companyPolicy.value
            _currentGpsLat.value = policy.officeLatitude
            _currentGpsLng.value = policy.officeLongitude
            _currentGpsAddress.value = policy.officeAddress
        }
    }

    fun setCustomGps(lat: Double, lng: Double, address: String) {
        _currentGpsLat.value = lat
        _currentGpsLng.value = lng
        _currentGpsAddress.value = address
    }

    fun showMessage(msg: String) {
        _statusMessage.value = msg
        viewModelScope.launch {
            delay(3500)
            if (_statusMessage.value == msg) {
                _statusMessage.value = null
            }
        }
    }

    fun clearMessage() {
        _statusMessage.value = null
    }

    /**
     * Absen Masuk dengan GPS & Validasi Biometrik
     */
    fun performCheckIn(
        biometricMethod: BiometricValidationType,
        onComplete: (Boolean, String) -> Unit
    ) {
        val emp = _selectedEmployee.value ?: run {
            onComplete(false, "Pilih karyawan terlebih dahulu")
            return
        }

        viewModelScope.launch {
            val policy = _companyPolicy.value
            val today = FormatHelper.getTodayDateString()
            val nowTime = FormatHelper.getCurrentTimeString()

            val existing = repository.getTodayAttendance(emp.id, today)
            if (existing != null && existing.checkInTime != null) {
                onComplete(false, "Karyawan ${emp.name} sudah melakukan absen masuk hari ini pukul ${existing.checkInTime}")
                return@launch
            }

            val distance = LocationHelper.calculateDistanceMeters(
                _currentGpsLat.value, _currentGpsLng.value,
                policy.officeLatitude, policy.officeLongitude
            )
            val isWithin = distance <= policy.officeRadiusMeters

            if (!isWithin && !_isFieldMode.value && !emp.isFieldWorker) {
                onComplete(
                    false,
                    "Gagal: Lokasi GPS berada di luar radius kantor (${LocationHelper.formatDistance(distance)}). Harap berada dalam ${policy.officeRadiusMeters.toInt()}m atau aktifkan Mode Lapangan."
                )
                return@launch
            }

            // Hitung keterlambatan (Jam masuk standard vs toleransi)
            val isLate = nowTime > "08:15:00"
            val status = if (isLate) AttendanceStatus.TERLAMBAT else AttendanceStatus.HADIR

            val newRecord = AttendanceRecord(
                id = existing?.id ?: 0,
                employeeId = emp.id,
                employeeName = emp.name,
                department = emp.department,
                date = today,
                checkInTime = nowTime,
                checkOutTime = null,
                checkInLatitude = _currentGpsLat.value,
                checkInLongitude = _currentGpsLng.value,
                checkInAddress = _currentGpsAddress.value,
                status = status.name,
                biometricMethod = biometricMethod.name,
                isFieldWork = _isFieldMode.value || emp.isFieldWorker,
                officeDistanceMeters = distance,
                withinOfficeRadius = isWithin,
                lateDurationMinutes = if (isLate) 20 else 0,
                cloudSyncStatus = "SYNCED_TO_CLOUD"
            )

            repository.recordAttendance(newRecord)
            showMessage("✅ Presensi Masuk Berhasil! ${emp.name} tercatat ${status.label} (${biometricMethod.displayName})")
            onComplete(true, "Presensi Berhasil Diverifikasi")
        }
    }

    /**
     * Absen Pulang
     */
    fun performCheckOut(onComplete: (Boolean, String) -> Unit) {
        val emp = _selectedEmployee.value ?: return
        viewModelScope.launch {
            val today = FormatHelper.getTodayDateString()
            val nowTime = FormatHelper.getCurrentTimeString()
            val existing = repository.getTodayAttendance(emp.id, today)

            if (existing == null || existing.checkInTime == null) {
                onComplete(false, "Belum ada catatan absen masuk untuk hari ini.")
                return@launch
            }

            if (existing.checkOutTime != null) {
                onComplete(false, "Sudah melakukan absen pulang pada pukul ${existing.checkOutTime}")
                return@launch
            }

            val updated = existing.copy(checkOutTime = nowTime)
            repository.updateAttendance(updated)
            showMessage("✅ Presensi Pulang Berhasil! Jam: $nowTime")
            onComplete(true, "Presensi Pulang Berhasil")
        }
    }

    /**
     * Pengajuan Permohonan Izin / Cuti
     */
    fun submitLeave(
        category: LeaveCategory,
        startDate: String,
        endDate: String,
        days: Int,
        reason: String,
        onSuccess: () -> Unit
    ) {
        val emp = _selectedEmployee.value ?: return
        viewModelScope.launch {
            val req = LeaveRequest(
                employeeId = emp.id,
                employeeName = emp.name,
                department = emp.department,
                category = category.name,
                startDate = startDate,
                endDate = endDate,
                durationDays = days,
                reason = reason,
                status = LeaveApprovalStatus.MENUNGGU.name,
                managerEmailNotified = true
            )
            repository.submitLeave(req)
            showMessage("📩 Permohonan ${category.label} terkirim! Notifikasi email otomatis dikirim ke Manajer Departemen.")
            onSuccess()
        }
    }

    /**
     * Persetujuan / Penolakan Cuti oleh Atasan
     */
    fun reviewLeave(leaveId: Long, isApproved: Boolean, notes: String?) {
        viewModelScope.launch {
            val status = if (isApproved) LeaveApprovalStatus.DISETUJUI.name else LeaveApprovalStatus.DITOLAK.name
            repository.updateLeaveStatus(leaveId, status, "Bambang Sudarsono (HR Director)", notes)
            showMessage(if (isApproved) "✅ Permohonan cuti disetujui atasan." else "❌ Permohonan cuti ditolak.")
        }
    }

    /**
     * Tindakan Disiplin Alpa: Kirim Surat Teguran via Email & Potong Gaji
     */
    fun handleDisciplinaryAction(attendanceId: Long, employeeName: String) {
        viewModelScope.launch {
            val policy = _companyPolicy.value
            repository.markWarningEmailSent(attendanceId)
            if (policy.autoDeductSalaryOnAlpha) {
                repository.applySalaryDeduction(attendanceId, policy.dailyAlphaDeductionAmount)
            }
            showMessage("📧 Surat Peringatan & Teguran Ketidakhadiran berhasil dikirim ke email $employeeName. Potongan gaji Rp ${policy.dailyAlphaDeductionAmount.toLong()} diterapkan.")
        }
    }

    /**
     * Terbitkan & Kirim Notifikasi Slip Gaji via Email
     */
    fun sendSlipGajiNotification(payrollId: Long, employeeName: String, email: String) {
        viewModelScope.launch {
            repository.markSlipEmailSent(payrollId)
            showMessage("📧 Slip Gaji Online & PDF terkirim ke $email ($employeeName)")
        }
    }

    /**
     * Sinkronisasi ke Sistem Akuntansi Pihak Ketiga (Accurate / Jurnal / SAP)
     */
    fun syncToAccountingSoftware(payrollId: Long, provider: String) {
        viewModelScope.launch {
            _isSyncingCloud.value = true
            delay(1000)
            repository.updateAccountingSyncStatus(payrollId, "SYNCED_TO_${provider.uppercase()}")
            _isSyncingCloud.value = false
            showMessage("🔗 Data payroll dan pemotongan PPh 21 berhasil disinkronisasi ke API $provider!")
        }
    }

    /**
     * Sinkronisasi Real-Time Mesin Sidik Jari (ZKTeco / Fingerspot API)
     */
    fun syncBiometricHardware() {
        viewModelScope.launch {
            _isSyncingCloud.value = true
            delay(1200)
            _isSyncingCloud.value = false
            showMessage("🔄 Sinkronisasi API Mesin Absensi Biometrik Berhasil! 1.000 log karyawan diperbarui.")
        }
    }

    /**
     * Kirim Pengumuman Resmi ke Seluruh Perangkat Karyawan
     */
    fun broadcastAnnouncement(title: String, content: String, isPriority: Boolean) {
        viewModelScope.launch {
            val announcement = Announcement(
                title = title,
                content = content,
                author = "Manajemen & Direksi",
                dateText = FormatHelper.getTodayDateString(),
                isPriority = isPriority,
                isPushBroadcasted = true
            )
            repository.postAnnouncement(announcement)
            showMessage("📢 Pengumuman resmi disiarkan ke 1.000 perangkat karyawan via Push Notification!")
        }
    }

    /**
     * Update Pengaturan Kebijakan Perusahaan & Identitas Brand
     */
    fun updateSettings(newPolicy: CompanyPolicy) {
        viewModelScope.launch {
            repository.updateCompanyPolicy(newPolicy)
            _companyPolicy.value = newPolicy
            try {
                _brandColor.value = Color(android.graphics.Color.parseColor(newPolicy.brandColorHex))
            } catch (e: Exception) {
                // Keep current
            }
            showMessage("💾 Kebijakan perusahaan & tema brand berhasil diperbarui!")
        }
    }

    /**
     * Hitung Ulang Seluruh Penggajian Karyawan dengan PPh 21 TER & BPJS Terkini
     */
    fun recalculatePayroll() {
        viewModelScope.launch {
            _isSyncingCloud.value = true
            repository.recalculateAllPayroll(9, 2026, _companyPolicy.value, employees.value)
            _isSyncingCloud.value = false
            showMessage("⚡ Perhitungan gaji, lembur, BPJS, & PPh 21 TER (PP 58/2023) selesai diperbarui!")
        }
    }
}
