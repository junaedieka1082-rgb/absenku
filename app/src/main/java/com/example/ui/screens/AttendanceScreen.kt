package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AttendanceRecord
import com.example.data.model.AttendanceStatus
import com.example.ui.MainViewModel
import com.example.ui.components.ExportAuditDialog
import com.example.ui.theme.CorporateBluePrimary
import com.example.util.FormatHelper
import com.example.util.LocationHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(viewModel: MainViewModel) {
    val attendances by viewModel.attendances.collectAsState()
    val payrolls by viewModel.payrolls.collectAsState()
    val companyPolicy by viewModel.companyPolicy.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("SEMUA") }
    var showExportDialog by remember { mutableStateOf(false) }

    val filteredList = attendances.filter { record ->
        val matchesSearch = record.employeeName.contains(searchQuery, ignoreCase = true) ||
                record.department.contains(searchQuery, ignoreCase = true)
        val matchesStatus = when (selectedFilter) {
            "SEMUA" -> true
            "HADIR" -> record.status == "HADIR"
            "TERLAMBAT" -> record.status == "TERLAMBAT"
            "IZIN" -> record.status in listOf("IZIN", "SAKIT", "CUTI")
            "ALPA" -> record.status == "ALPA"
            else -> true
        }
        matchesSearch && matchesStatus
    }

    if (showExportDialog) {
        ExportAuditDialog(
            attendances = attendances,
            payrolls = payrolls,
            onDismiss = { showExportDialog = false },
            onSuccessToast = { viewModel.showMessage(it) }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Top Toolbar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Laporan Presensi Harian",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = CorporateBluePrimary
                )
                Text(
                    text = "Verifikasi GPS & Biometrik 1.000 Karyawan",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = { viewModel.syncBiometricHardware() },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Sync,
                        contentDescription = "Sinkron Mesin Sidik Jari",
                        tint = CorporateBluePrimary
                    )
                }

                FilledTonalButton(
                    onClick = { showExportDialog = true },
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ekspor", fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Cari nama atau departemen...", fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(20.dp)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Filter Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("SEMUA", "HADIR", "TERLAMBAT", "IZIN", "ALPA").forEach { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter },
                    label = { Text(filter, fontSize = 11.sp) },
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Attendance List
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredList) { record ->
                AttendanceItemCard(
                    record = record,
                    policy = companyPolicy,
                    onDisciplinaryAction = {
                        viewModel.handleDisciplinaryAction(record.id, record.employeeName)
                    }
                )
            }
        }
    }
}

@Composable
private fun AttendanceItemCard(
    record: AttendanceRecord,
    policy: com.example.data.model.CompanyPolicy,
    onDisciplinaryAction: () -> Unit
) {
    val statusColor = when (record.status) {
        "HADIR" -> Color(0xFF2E7D32)
        "TERLAMBAT" -> Color(0xFFED6C02)
        "ALPA" -> Color(0xFFD32F2F)
        else -> Color(0xFF0288D1)
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = record.employeeName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${record.department} • ${record.date}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = record.statusEnum.label,
                        color = statusColor,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Time & Biometric Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Jam Masuk:", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text(
                        text = record.checkInTime ?: "Tidak Absen",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Column {
                    Text("Jam Pulang:", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text(
                        text = record.checkOutTime ?: "-",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Column {
                    Text("Metode Validasi:", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text(
                        text = record.biometricMethod,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = CorporateBluePrimary
                    )
                }
            }

            // GPS Location Details
            if (record.checkInAddress.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Place, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${record.checkInAddress} (${LocationHelper.formatDistance(record.officeDistanceMeters)})",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }

            // Disciplinary Sanction / Alpa Action
            if (record.status == "ALPA") {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFFFEBEE),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Color(0xFFC62828), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Ketidakhadiran Tanpa Keterangan (Alpa)",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFC62828)
                            )
                        }

                        if (record.warningEmailSent) {
                            Text(
                                text = "✅ Surat Teguran telah dikirim ke email karyawan. Pemotongan gaji Rp ${FormatHelper.formatRupiah(record.deductionAmount)} aktif.",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFB71C1C),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        } else {
                            Text(
                                text = "Belum ada tindakan disiplin. Klik tombol di bawah untuk memproses teguran resmi & pemotongan gaji.",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.DarkGray,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = onDisciplinaryAction,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.MailOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Kirim Teguran Email & Potong Gaji", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
