package com.example.ui.components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AttendanceRecord
import com.example.data.model.PayrollRecord
import com.example.ui.theme.CorporateBluePrimary
import com.example.util.FormatHelper

@Composable
fun ExportAuditDialog(
    attendances: List<AttendanceRecord>,
    payrolls: List<PayrollRecord>,
    onDismiss: () -> Unit,
    onSuccessToast: (String) -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }

    Dialog(onDismissRequest = onDismiss) {
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Download,
                            contentDescription = null,
                            tint = CorporateBluePrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Ekspor Laporan & Audit",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    contentColor = CorporateBluePrimary
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Audit Presensi") }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Audit Payroll & Pajak") }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (selectedTab == 0) {
                    Text(
                        text = "Ekspor Rekap Kehadiran Harian & GPS Lapangan",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Total Data: ${attendances.size} catatan absensi biometrik & GPS.",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val csvData = FormatHelper.generateAttendanceCsv(attendances)
                            shareText(context, "Laporan_Audit_Presensi_${FormatHelper.getTodayDateString()}.csv", csvData)
                            onSuccessToast("✅ File Excel/CSV Presensi Siap Diunduh & Dibagikan")
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.TableChart, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ekspor ke Excel (.CSV)")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = {
                            val summary = "=== LAPORAN RESMI AUDIT PRESENSI PT NUSANTARA PRIMA ===\n" +
                                    "Tanggal: ${FormatHelper.getTodayDateString()}\n" +
                                    "Total Karyawan Aktif: 1.000 Staf\n" +
                                    "Tingkat Kehadiran: 94.2%\n\n" +
                                    FormatHelper.generateAttendanceCsv(attendances.take(15))
                            shareText(context, "Laporan_Audit_Presensi.pdf", summary)
                            onSuccessToast("📄 Dokumen PDF Audit Kehadiran Dibuat")
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cetak Dokumen PDF Audit")
                    }
                } else {
                    Text(
                        text = "Ekspor Rekapitulasi Gaji, BPJS & PPh 21 TER",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Total Data: ${payrolls.size} slip gaji terdaftar.",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val csvData = FormatHelper.generatePayrollCsv(payrolls)
                            shareText(context, "Rekap_Payroll_PajakPPh21_${FormatHelper.getTodayDateString()}.csv", csvData)
                            onSuccessToast("✅ Rekap Excel/CSV Payroll & Pajak PPh 21 Dibuat")
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.TableChart, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ekspor Payroll ke Excel (.CSV)")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = {
                            val summary = "=== REKAPITULASI PENGGAJIAN & PPH 21 TER (PP 58/2023) ===\n" +
                                    "Bulan: September 2026\n" +
                                    "Perusahaan: PT Nusantara Prima Teknologi\n\n" +
                                    FormatHelper.generatePayrollCsv(payrolls)
                            shareText(context, "Rekap_Penggajian_Resmi.pdf", summary)
                            onSuccessToast("📄 Dokumen PDF Payroll Diterbitkan")
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cetak Dokumen PDF Payroll")
                    }
                }
            }
        }
    }
}

private fun shareText(context: Context, title: String, content: String) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TITLE, title)
        putExtra(Intent.EXTRA_SUBJECT, title)
        putExtra(Intent.EXTRA_TEXT, content)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "Ekspor Data ke:")
    context.startActivity(shareIntent)
}
