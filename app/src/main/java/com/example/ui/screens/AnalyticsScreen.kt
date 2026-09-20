package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.theme.CorporateBluePrimary

@Composable
fun AnalyticsScreen(viewModel: MainViewModel) {
    val attendances by viewModel.attendances.collectAsState()
    val payrolls by viewModel.payrolls.collectAsState()
    val employees by viewModel.employees.collectAsState()
    val companyPolicy by viewModel.companyPolicy.collectAsState()

    val totalRecords = attendances.size.coerceAtLeast(1)
    val onTimeCount = attendances.count { it.status == "HADIR" }
    val lateCount = attendances.count { it.status == "TERLAMBAT" }
    val alphaCount = attendances.count { it.status == "ALPA" }
    val permitCount = attendances.count { it.status in listOf("IZIN", "SAKIT", "CUTI") }

    val onTimeRate = (onTimeCount.toFloat() / totalRecords * 100).toInt()
    val lateRate = (lateCount.toFloat() / totalRecords * 100).toInt()
    val alphaRate = (alphaCount.toFloat() / totalRecords * 100).toInt()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "Dashboard Analitik & Disiplin Staf",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = CorporateBluePrimary
                )
                Text(
                    text = "Monitoring Kinerja 1.000 Staf Aktif Secara Real-Time",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Overall Discipline KPI Gauge Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Indeks Kedisiplinan Perusahaan", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text("Berdasarkan kehadiran GPS & Biometrik", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        }
                        Text(
                            text = "$onTimeRate%",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (onTimeRate >= 85) Color(0xFF2E7D32) else Color(0xFFED6C02)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    LinearProgressIndicator(
                        progress = { onTimeRate / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = Color(0xFF2E7D32),
                        trackColor = Color(0xFFE0E0E0)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MetricBadge("Tepat Waktu", "$onTimeRate%", Color(0xFF2E7D32))
                        MetricBadge("Terlambat", "$lateRate%", Color(0xFFED6C02))
                        MetricBadge("Alpa (Tanpa Izin)", "$alphaRate%", Color(0xFFD32F2F))
                        MetricBadge("Izin/Sakit", "${((permitCount.toFloat() / totalRecords) * 100).toInt()}%", Color(0xFF0288D1))
                    }
                }
            }
        }

        // Real-Time Cloud Infrastructure & Capacity Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1B2A)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFF00E676)))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Koneksi Cloud Real-Time",
                                color = Color.White,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF00E5FF).copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "ONLINE • LATENSI 38ms",
                                color = Color(0xFF80D8FF),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Kapasitas Server", style = MaterialTheme.typography.labelSmall, color = Color.LightGray)
                            Text("1.000 Staf Aktif", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Column {
                            Text("Database Storage", style = MaterialTheme.typography.labelSmall, color = Color.LightGray)
                            Text("Encrypted SQLite + Cloud Sync", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Column {
                            Text("Status Audit", style = MaterialTheme.typography.labelSmall, color = Color.LightGray)
                            Text("Siap Diekspor", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color(0xFF69F0AE))
                        }
                    }
                }
            }
        }

        // Departmental Discipline Leaderboard
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Peringkat Kedisiplinan Antar Departemen",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    DepartmentRow("Teknologi & Informasi (IT)", 98, Color(0xFF2E7D32))
                    DepartmentRow("Human Capital & Keuangan", 96, Color(0xFF2E7D32))
                    DepartmentRow("Pemasaran & Bisnis", 92, Color(0xFF1565C0))
                    DepartmentRow("Gudang & Distribusi", 89, Color(0xFFED6C02))
                    DepartmentRow("Divisi Manufaktur & Borongan", 87, Color(0xFFED6C02))
                    DepartmentRow("Operasional Lapangan (GPS)", 84, Color(0xFFE65100))
                }
            }
        }

        // Employee Status Distribution (Tetap, Harian, Borongan)
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Distribusi Status Karyawan & Skema Pajak",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    StatusDistributionRow(
                        title = "Karyawan Tetap (Gaji Pokok Bulanan)",
                        subtitle = "PPh 21 TER A/B/C + BPJS Lengkap",
                        percentage = "65% (650 Staf)",
                        color = CorporateBluePrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    StatusDistributionRow(
                        title = "Pegawai Harian Lepas (Upah Harian)",
                        subtitle = "PPh 21 TER Harian/Bulanan + JKK/JKM",
                        percentage = "25% (250 Staf)",
                        color = Color(0xFF0288D1)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    StatusDistributionRow(
                        title = "Tenaga Borongan (Upah Per Satuan Hasil)",
                        subtitle = "PPh 21 Pasal 21 Borongan + BPJS",
                        percentage = "10% (100 Staf)",
                        color = Color(0xFF26A69A)
                    )
                }
            }
        }
    }
}

@Composable
private fun MetricBadge(title: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
        Text(title, style = MaterialTheme.typography.labelSmall, color = Color.Gray, fontSize = 10.sp)
    }
}

@Composable
private fun DepartmentRow(department: String, score: Int, color: Color) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(department, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
            Text("$score%", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(3.dp))
        LinearProgressIndicator(
            progress = { score / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = Color(0xFFEEEEEE)
        )
    }
}

@Composable
private fun StatusDistributionRow(
    title: String,
    subtitle: String,
    percentage: String,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(title, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                Text(subtitle, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
        }
        Text(percentage, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = color)
    }
}
