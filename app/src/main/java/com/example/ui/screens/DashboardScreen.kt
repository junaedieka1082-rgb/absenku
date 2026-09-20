package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.BiometricValidationType
import com.example.data.model.Employee
import com.example.ui.MainViewModel
import com.example.ui.components.BiometricDialog
import com.example.ui.components.GpsLocationPreviewCard
import com.example.ui.components.WebBrowserViewDialog
import com.example.ui.theme.CorporateBluePrimary
import com.example.util.FormatHelper

@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    onNavigateToAttendance: () -> Unit,
    onNavigateToPayroll: () -> Unit,
    onNavigateToLeaves: () -> Unit
) {
    val selectedEmployee by viewModel.selectedEmployee.collectAsState()
    val employees by viewModel.employees.collectAsState()
    val companyPolicy by viewModel.companyPolicy.collectAsState()
    val attendances by viewModel.attendances.collectAsState()
    val announcements by viewModel.announcements.collectAsState()
    val userLat by viewModel.currentGpsLat.collectAsState()
    val userLng by viewModel.currentGpsLng.collectAsState()
    val userAddress by viewModel.currentGpsAddress.collectAsState()
    val isFieldMode by viewModel.isFieldMode.collectAsState()

    var showBiometricDialog by remember { mutableStateOf(false) }
    var activeBiometricType by remember { mutableStateOf(BiometricValidationType.FACE_SCAN) }
    var showEmployeeSelector by remember { mutableStateOf(false) }
    var showWebBrowserDialog by remember { mutableStateOf(false) }

    // Quick Metrics
    val todayDate = FormatHelper.getTodayDateString()
    val todayAttendances = attendances.filter { it.date == todayDate }
    val presentCount = todayAttendances.count { it.status == "HADIR" }
    val lateCount = todayAttendances.count { it.status == "TERLAMBAT" }
    val permitCount = todayAttendances.count { it.status in listOf("IZIN", "SAKIT", "CUTI") }
    val alphaCount = todayAttendances.count { it.status == "ALPA" }

    if (showBiometricDialog && selectedEmployee != null) {
        BiometricDialog(
            validationType = activeBiometricType,
            employeeName = selectedEmployee!!.name,
            onDismiss = { showBiometricDialog = false },
            onSuccess = {
                showBiometricDialog = false
                viewModel.performCheckIn(activeBiometricType) { _, _ -> }
            }
        )
    }

    if (showWebBrowserDialog) {
        WebBrowserViewDialog(
            companyName = companyPolicy.companyName,
            onDismiss = { showWebBrowserDialog = false }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Banner with Generated Asset
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_dashboard_hero),
                        contentDescription = "Gedung Kantor Pusat",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0x550D47A1),
                                        Color(0xEE0B192C)
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF00E5FF).copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "ENTERPRISE CLOUD GPS ATTENDANCE",
                                color = Color(0xFFE0F7FA),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = companyPolicy.companyName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Kapasitas: 1.000 Staf Aktif • Sinkron Real-Time",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFB0BEC5)
                        )
                    }

                    // Web Browser Mode Button
                    IconButton(
                        onClick = { showWebBrowserDialog = true },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            Icons.Default.Language,
                            contentDescription = "Buka Portal Web Browser",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Selected Employee Switcher Header Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = CorporateBluePrimary,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = selectedEmployee?.name ?: "Pilih Karyawan",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Text(
                            text = "${selectedEmployee?.position} • ${selectedEmployee?.department}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                        Row(
                            modifier = Modifier.padding(top = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = selectedEmployee?.employeeStatus ?: "TETAP",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CorporateBluePrimary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFE8F5E9)
                            ) {
                                Text(
                                    text = "PTKP: ${selectedEmployee?.ptkpCode ?: "TK/0"}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF2E7D32),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    OutlinedButton(
                        onClick = { showEmployeeSelector = !showEmployeeSelector },
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text("Ganti Staf", fontSize = 11.sp)
                    }
                }

                if (showEmployeeSelector) {
                    HorizontalDivider()
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Simulasikan Login sebagai Karyawan:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        employees.forEach { emp ->
                            TextButton(
                                onClick = {
                                    viewModel.selectEmployee(emp)
                                    showEmployeeSelector = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(emp.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("${emp.nik} • ${emp.statusEnum.displayName}", fontSize = 11.sp, color = Color.Gray)
                                    }
                                    if (emp.id == selectedEmployee?.id) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = CorporateBluePrimary)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Live GPS Geofence & Field Map Card
        item {
            GpsLocationPreviewCard(
                userLat = userLat,
                userLng = userLng,
                userAddress = userAddress,
                policy = companyPolicy,
                isFieldMode = isFieldMode,
                onToggleFieldMode = { viewModel.toggleFieldMode(it) }
            )
        }

        // Punch In / Punch Out Action Buttons
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Aksi Presensi Hari Ini",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                activeBiometricType = BiometricValidationType.FACE_SCAN
                                showBiometricDialog = true
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CorporateBluePrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Face, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Absen Masuk (Wajah AI)", fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                activeBiometricType = BiometricValidationType.FINGERPRINT
                                showBiometricDialog = true
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Fingerprint, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Absen Masuk (Sidik Jari)", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = {
                            viewModel.performCheckOut { _, _ -> }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color(0xFFE65100))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Catat Absen Pulang Sore", color = Color(0xFFE65100), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Daily Summary Stats
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Hadir Tepat",
                    count = presentCount.toString(),
                    color = Color(0xFF2E7D32),
                    icon = Icons.Default.CheckCircle
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Terlambat",
                    count = lateCount.toString(),
                    color = Color(0xFFED6C02),
                    icon = Icons.Default.Schedule
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Izin / Cuti",
                    count = permitCount.toString(),
                    color = Color(0xFF0288D1),
                    icon = Icons.Default.EventNote
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Alpa",
                    count = alphaCount.toString(),
                    color = Color(0xFFD32F2F),
                    icon = Icons.Default.Warning
                )
            }
        }

        // Announcements Section (Official Broadcast)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Campaign,
                        contentDescription = null,
                        tint = CorporateBluePrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Pengumuman Resmi Manajemen",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFE8F5E9)
                ) {
                    Text(
                        text = "Push Broadcast Aktif",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF2E7D32),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }

        items(announcements) { ann ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (ann.isPriority) Color(0xFFFFF8E1) else MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = ann.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (ann.isPriority) Color(0xFFE65100) else CorporateBluePrimary
                        )
                        Text(
                            text = ann.dateText,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = ann.content,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Oleh: ${ann.author}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    count: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(count, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = color)
            Text(title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
        }
    }
}
