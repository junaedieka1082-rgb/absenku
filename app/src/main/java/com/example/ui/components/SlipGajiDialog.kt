package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.PayrollRecord
import com.example.ui.theme.CorporateBluePrimary
import com.example.util.FormatHelper

@Composable
fun SlipGajiDialog(
    payroll: PayrollRecord,
    companyName: String,
    onDismiss: () -> Unit,
    onSendEmail: (PayrollRecord) -> Unit,
    onExportPdf: (PayrollRecord) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .padding(vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "SLIP GAJI KARYAWAN",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = CorporateBluePrimary
                        )
                        Text(
                            text = companyName,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Employee Info Box
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Nama Karyawan:", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                Text(payroll.employeeName, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("NIK / Departemen:", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                Text("${payroll.employeeNik} • ${payroll.department}", style = MaterialTheme.typography.bodySmall)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Status Karyawan:", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                Text(payroll.employeeStatus, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = CorporateBluePrimary)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Status Pajak (PTKP):", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                Text("${payroll.ptkpCode} (${payroll.maritalStatus}, ${payroll.dependentsCount} Tanggungan) - ${payroll.terCategory}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Periode Gaji:", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                Text("${payroll.periodMonth} / ${payroll.periodYear}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Penghasilan (Earnings)
                    Text(
                        text = "KOMPONEN PENGHASILAN",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = CorporateBluePrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    PayRow("Gaji Pokok / Upah Dasar (${payroll.activeDaysWorked} Hari)", FormatHelper.formatRupiah(payroll.baseSalary))
                    if (payroll.overtimePay > 0) {
                        PayRow("Upah Lembur (${payroll.overtimeHours} Jam)", FormatHelper.formatRupiah(payroll.overtimePay))
                    }
                    if (payroll.allowanceTransport > 0) {
                        PayRow("Tunjangan Transportasi", FormatHelper.formatRupiah(payroll.allowanceTransport))
                    }
                    if (payroll.allowanceMeal > 0) {
                        PayRow("Tunjangan Makan", FormatHelper.formatRupiah(payroll.allowanceMeal))
                    }
                    if (payroll.allowancePerformance > 0) {
                        PayRow("Tunjangan Kinerja / Insentif", FormatHelper.formatRupiah(payroll.allowancePerformance))
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Penghasilan Bruto", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Text(FormatHelper.formatRupiah(payroll.totalGrossIncome), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color(0xFF1565C0))
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                    // Potongan (Deductions)
                    Text(
                        text = "POTONGAN & PAJAK TERBARU",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFC62828)
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    if (payroll.absenceDeduction > 0) {
                        PayRow("Potongan Alpa (${payroll.alphaDays} Hari)", "- " + FormatHelper.formatRupiah(payroll.absenceDeduction), isDeduction = true)
                    }
                    if (payroll.lateDeduction > 0) {
                        PayRow("Potongan Keterlambatan", "- " + FormatHelper.formatRupiah(payroll.lateDeduction), isDeduction = true)
                    }

                    // BPJS Ketenagakerjaan
                    PayRow("BPJS JHT Karyawan (2%)", "- " + FormatHelper.formatRupiah(payroll.bpjsJhtEmployee), isDeduction = true)
                    PayRow("BPJS JP Karyawan (1%)", "- " + FormatHelper.formatRupiah(payroll.bpjsJpEmployee), isDeduction = true)
                    PayRow("BPJS Kesehatan Karyawan (1%)", "- " + FormatHelper.formatRupiah(payroll.bpjsKesehatanEmployee), isDeduction = true)

                    // PPh 21 TER
                    PayRow(
                        "PPh 21 TER (${String.format(java.util.Locale.US, "%.2f", payroll.terEffectiveRatePercentage)}% ${payroll.terCategory})",
                        "- " + FormatHelper.formatRupiah(payroll.pph21WithheldAmount),
                        isDeduction = true
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Potongan", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Text("- " + FormatHelper.formatRupiah(payroll.totalDeductions), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color(0xFFC62828))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Take Home Pay Box
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFE8F5E9),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF81C784)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("GAJI BERSIH (TAKE HOME PAY)", style = MaterialTheme.typography.labelMedium, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                                Text("Ditransfer ke Rekening", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            }
                            Text(
                                text = FormatHelper.formatRupiah(payroll.netTakeHomePay),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF1B5E20)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Info BPJS Perusahaan
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Manfaat Dibayar Perusahaan (Non-Potongan):", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            Text("• BPJS JKK (0.24%): ${FormatHelper.formatRupiah(payroll.bpjsJkkCompany)}", style = MaterialTheme.typography.labelSmall)
                            Text("• BPJS JKM (0.30%): ${FormatHelper.formatRupiah(payroll.bpjsJkmCompany)}", style = MaterialTheme.typography.labelSmall)
                            Text("• BPJS JHT (3.70%): ${FormatHelper.formatRupiah(payroll.bpjsJhtCompany)}", style = MaterialTheme.typography.labelSmall)
                            Text("• BPJS JP (2.00%): ${FormatHelper.formatRupiah(payroll.bpjsJpCompany)}", style = MaterialTheme.typography.labelSmall)
                            Text("• BPJS Kesehatan (4.00%): ${FormatHelper.formatRupiah(payroll.bpjsKesehatanCompany)}", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Actions: Email Dispatch & PDF Export
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { onSendEmail(payroll) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Kirim Email", fontSize = 12.sp)
                    }

                    Button(
                        onClick = { onExportPdf(payroll) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = CorporateBluePrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Cetak / PDF", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun PayRow(label: String, value: String, isDeduction: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
        Text(
            value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = if (isDeduction) Color(0xFFC62828) else MaterialTheme.colorScheme.onSurface
        )
    }
}
