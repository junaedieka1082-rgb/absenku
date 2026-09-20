package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.window.Dialog
import com.example.data.model.CompanyPolicy
import com.example.ui.MainViewModel
import com.example.ui.theme.CorporateBluePrimary
import com.example.util.FormatHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSettingsScreen(viewModel: MainViewModel) {
    val policy by viewModel.companyPolicy.collectAsState()

    var companyName by remember(policy) { mutableStateOf(policy.companyName) }
    var officeAddress by remember(policy) { mutableStateOf(policy.officeAddress) }
    var officeRadiusText by remember(policy) { mutableStateOf(policy.officeRadiusMeters.toInt().toString()) }
    var autoSendEmail by remember(policy) { mutableStateOf(policy.autoSendWarningEmailOnAlpha) }
    var autoDeductSalary by remember(policy) { mutableStateOf(policy.autoDeductSalaryOnAlpha) }
    var dailyDeductionText by remember(policy) { mutableStateOf(policy.dailyAlphaDeductionAmount.toLong().toString()) }
    var selectedBrandColorHex by remember(policy) { mutableStateOf(policy.brandColorHex) }
    var accountingProvider by remember(policy) { mutableStateOf(policy.accountingProvider) }
    var fingerprintEndpoint by remember(policy) { mutableStateOf(policy.fingerprintApiEndpoint) }

    var showAnnouncementDialog by remember { mutableStateOf(false) }
    var announcementTitle by remember { mutableStateOf("") }
    var announcementContent by remember { mutableStateOf("") }
    var isAnnouncementPriority by remember { mutableStateOf(true) }

    val brandColorOptions = listOf(
        "#0D47A1" to "Corporate Blue",
        "#01579B" to "Deep Ocean",
        "#1A237E" to "Navy Royal",
        "#004D40" to "Emerald Teal",
        "#212121" to "Classic Graphite"
    )

    if (showAnnouncementDialog) {
        Dialog(onDismissRequest = { showAnnouncementDialog = false }) {
            Surface(
                shape = RoundedCornerShape(18.dp),
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
                            text = "Siarkan Pengumuman Resmi",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = CorporateBluePrimary
                        )
                        IconButton(onClick = { showAnnouncementDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = null)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Pengumuman akan dikirim serentak ke 1.000 smartphone staf via Push Notification.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = announcementTitle,
                        onValueChange = { announcementTitle = it },
                        label = { Text("Judul Pengumuman") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = announcementContent,
                        onValueChange = { announcementContent = it },
                        label = { Text("Isi Pesan Siaran") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        shape = RoundedCornerShape(10.dp),
                        maxLines = 4
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = isAnnouncementPriority,
                            onCheckedChange = { isAnnouncementPriority = it }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Tandai sebagai Pengumuman Penting / Prioritas", fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (announcementTitle.isNotBlank()) {
                                viewModel.broadcastAnnouncement(
                                    announcementTitle,
                                    announcementContent,
                                    isAnnouncementPriority
                                )
                                showAnnouncementDialog = false
                                announcementTitle = ""
                                announcementContent = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = CorporateBluePrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Campaign, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Kirim Push Broadcast Sekarang")
                    }
                }
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Panel Pengaturan Administrator",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = CorporateBluePrimary
                    )
                    Text(
                        text = "Konfigurasi GPS Geofence, Email & Akuntansi",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = {
                        val newPolicy = policy.copy(
                            companyName = companyName,
                            officeAddress = officeAddress,
                            officeRadiusMeters = officeRadiusText.toDoubleOrNull() ?: 150.0,
                            autoSendWarningEmailOnAlpha = autoSendEmail,
                            autoDeductSalaryOnAlpha = autoDeductSalary,
                            dailyAlphaDeductionAmount = dailyDeductionText.toDoubleOrNull() ?: 250000.0,
                            brandColorHex = selectedBrandColorHex,
                            accountingProvider = accountingProvider,
                            fingerprintApiEndpoint = fingerprintEndpoint
                        )
                        viewModel.updateSettings(newPolicy)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CorporateBluePrimary),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Simpan", fontSize = 12.sp)
                }
            }
        }

        // Brand & Company Profile Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Identitas Perusahaan & Tema Warna", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = companyName,
                        onValueChange = { companyName = it },
                        label = { Text("Nama Perusahaan") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Warna Tema Utama (Brand Accent):", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        brandColorOptions.forEach { (hex, _) ->
                            val color = Color(android.graphics.Color.parseColor(hex))
                            val isSelected = selectedBrandColorHex == hex
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .clickable { selectedBrandColorHex = hex }
                                    .then(
                                        if (isSelected) Modifier.border(3.dp, Color.White, CircleShape)
                                        else Modifier
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Office GPS & Geofence Settings Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Lokasi Kantor Pusat & Radius Geofence", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = officeAddress,
                        onValueChange = { officeAddress = it },
                        label = { Text("Alamat Kantor SCBD") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = "${policy.officeLatitude}, ${policy.officeLongitude}",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Koordinat GPS") },
                            modifier = Modifier.weight(1.2f),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = officeRadiusText,
                            onValueChange = { officeRadiusText = it },
                            label = { Text("Radius (Meter)") },
                            modifier = Modifier.weight(0.8f),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )
                    }
                }
            }
        }

        // Disciplinary & Deduction Automation (User Requirement)
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Kebijakan Disiplin & Pemotongan Gaji Otomatis", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Text("Pengaturan otomatis ketika staf tidak memiliki keterangan absensi (Alpa)", style = MaterialTheme.typography.labelSmall, color = Color.Gray)

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Kirim Teguran Email Otomatis", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                            Text("Sistem otomatis mengirimkan surat peringatan ke email karyawan jika alpa", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        }
                        Switch(
                            checked = autoSendEmail,
                            onCheckedChange = { autoSendEmail = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = CorporateBluePrimary)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Otomatis Potong Gaji Harian", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                            Text("Potong gaji per hari saat karyawan alpa tanpa izin", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        }
                        Switch(
                            checked = autoDeductSalary,
                            onCheckedChange = { autoDeductSalary = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = CorporateBluePrimary)
                        )
                    }

                    if (autoDeductSalary) {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = dailyDeductionText,
                            onValueChange = { dailyDeductionText = it },
                            label = { Text("Nominal Pemotongan Gaji per Hari Alpa (Rp)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )
                    }
                }
            }
        }

        // Integration with Accounting & Hardware
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Integrasi Akuntansi & Mesin Biometrik", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = accountingProvider,
                        onValueChange = { accountingProvider = it },
                        label = { Text("Software Akuntansi Pihak Ketiga (Accurate/Jurnal/SAP)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = fingerprintEndpoint,
                        onValueChange = { fingerprintEndpoint = it },
                        label = { Text("Endpoint API Mesin Sidik Jari (ZKTeco/Fingerspot)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                }
            }
        }

        // Broadcast Push Announcement Action
        item {
            Button(
                onClick = { showAnnouncementDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Campaign, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Siarkan Pengumuman Push Notification ke 1.000 Karyawan", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}
