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
import androidx.compose.ui.window.Dialog
import com.example.data.model.LeaveCategory
import com.example.data.model.LeaveRequest
import com.example.ui.MainViewModel
import com.example.ui.theme.CorporateBluePrimary
import com.example.util.FormatHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaveScreen(viewModel: MainViewModel) {
    val leaves by viewModel.leaves.collectAsState()
    val selectedEmployee by viewModel.selectedEmployee.collectAsState()

    var showApplyDialog by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf(LeaveCategory.CUTI_TAHUNAN) }
    var startDateText by remember { mutableStateOf(FormatHelper.getTodayDateString()) }
    var endDateText by remember { mutableStateOf(FormatHelper.getTodayDateString()) }
    var durationDaysText by remember { mutableStateOf("1") }
    var reasonText by remember { mutableStateOf("") }

    val pendingCount = leaves.count { it.status == "MENUNGGU" }
    val approvedCount = leaves.count { it.status == "DISETUJUI" }
    val rejectedCount = leaves.count { it.status == "DITOLAK" }

    if (showApplyDialog) {
        Dialog(onDismissRequest = { showApplyDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Pengajuan Izin / Cuti Staf",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = CorporateBluePrimary
                        )
                        IconButton(onClick = { showApplyDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = null)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Pemohon: ${selectedEmployee?.name ?: "-"}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Pilih Kategori:", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            LeaveCategory.CUTI_TAHUNAN,
                            LeaveCategory.SAKIT,
                            LeaveCategory.DATANG_TERLAMBAT,
                            LeaveCategory.KEPERLUAN_LAINNYA
                        ).forEach { cat ->
                            FilterChip(
                                selected = selectedCategory == cat,
                                onClick = { selectedCategory = cat },
                                label = { Text(cat.label, fontSize = 10.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = startDateText,
                            onValueChange = { startDateText = it },
                            label = { Text("Tgl Mulai", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = endDateText,
                            onValueChange = { endDateText = it },
                            label = { Text("Tgl Selesai", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = reasonText,
                        onValueChange = { reasonText = it },
                        label = { Text("Alasan Lengkap & Keterangan", fontSize = 11.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp),
                        shape = RoundedCornerShape(10.dp),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            val days = durationDaysText.toIntOrNull() ?: 1
                            viewModel.submitLeave(
                                category = selectedCategory,
                                startDate = startDateText,
                                endDate = endDateText,
                                days = days,
                                reason = reasonText.ifEmpty { "Keperluan Pribadi" },
                                onSuccess = { showApplyDialog = false }
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = CorporateBluePrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Kirim ke Atasan & Notifikasi Email")
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Manajemen Cuti & Izin",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = CorporateBluePrimary
                )
                Text(
                    text = "Alur Persetujuan Atasan & Notifikasi Otomatis",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = { showApplyDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = CorporateBluePrimary),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Ajukan Izin", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Summary Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LeaveStatusCard(modifier = Modifier.weight(1f), title = "Menunggu", count = pendingCount, color = Color(0xFFED6C02))
            LeaveStatusCard(modifier = Modifier.weight(1f), title = "Disetujui", count = approvedCount, color = Color(0xFF2E7D32))
            LeaveStatusCard(modifier = Modifier.weight(1f), title = "Ditolak", count = rejectedCount, color = Color(0xFFD32F2F))
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Daftar Permohonan Cuti Masuk:",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Leaves List
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(leaves) { item ->
                LeaveItemCard(
                    leave = item,
                    onApprove = { viewModel.reviewLeave(item.id, true, "Disetujui oleh Kepala Divisi") },
                    onReject = { viewModel.reviewLeave(item.id, false, "Kapasitas proyek padat pada tanggal tersebut") }
                )
            }
        }
    }
}

@Composable
private fun LeaveStatusCard(modifier: Modifier = Modifier, title: String, count: Int, color: Color) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(count.toString(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = color)
            Text(title, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        }
    }
}

@Composable
private fun LeaveItemCard(
    leave: LeaveRequest,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    val statusColor = when (leave.status) {
        "DISETUJUI" -> Color(0xFF2E7D32)
        "DITOLAK" -> Color(0xFFD32F2F)
        else -> Color(0xFFED6C02)
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
                    Text(leave.employeeName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Text("${leave.department} • ${leave.categoryEnum.label}", style = MaterialTheme.typography.bodySmall, color = CorporateBluePrimary, fontWeight = FontWeight.SemiBold)
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = leave.approvalStatusEnum.label,
                        color = statusColor,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Periode: ${leave.startDate} s/d ${leave.endDate} (${leave.durationDays} hari kerja)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Alasan: \"${leave.reason}\"",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp)
            )

            if (leave.approvedBy != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Diverifikasi: ${leave.approvedBy} (${leave.responseNotes ?: "-"})",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }

            // Approval Buttons for Pending status
            if (leave.status == "MENUNGGU") {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onApprove,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Setujui Cuti", fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = onReject,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFFD32F2F))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Tolak", fontSize = 11.sp, color = Color(0xFFD32F2F))
                    }
                }
            }
        }
    }
}
