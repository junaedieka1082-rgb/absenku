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
import com.example.data.model.PayrollRecord
import com.example.ui.MainViewModel
import com.example.ui.components.AccountingSyncDialog
import com.example.ui.components.ExportAuditDialog
import com.example.ui.components.SlipGajiDialog
import com.example.ui.theme.CorporateBluePrimary
import com.example.util.FormatHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayrollScreen(viewModel: MainViewModel) {
    val payrolls by viewModel.payrolls.collectAsState()
    val attendances by viewModel.attendances.collectAsState()
    val companyPolicy by viewModel.companyPolicy.collectAsState()
    val employees by viewModel.employees.collectAsState()

    var selectedPayrollForSlip by remember { mutableStateOf<PayrollRecord?>(null) }
    var selectedPayrollForAccounting by remember { mutableStateOf<PayrollRecord?>(null) }
    var showExportDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val totalNetPay = payrolls.sumOf { it.netTakeHomePay }
    val totalPPh21 = payrolls.sumOf { it.pph21WithheldAmount }
    val totalBpjs = payrolls.sumOf { it.bpjsJhtEmployee + it.bpjsJpEmployee + it.bpjsKesehatanEmployee }

    val filteredList = payrolls.filter {
        it.employeeName.contains(searchQuery, ignoreCase = true) ||
                it.employeeNik.contains(searchQuery, ignoreCase = true) ||
                it.department.contains(searchQuery, ignoreCase = true)
    }

    if (selectedPayrollForSlip != null) {
        SlipGajiDialog(
            payroll = selectedPayrollForSlip!!,
            companyName = companyPolicy.companyName,
            onDismiss = { selectedPayrollForSlip = null },
            onSendEmail = { p ->
                val emp = employees.find { it.id == p.employeeId }
                viewModel.sendSlipGajiNotification(p.id, p.employeeName, emp?.email ?: "karyawan@nusantaraprima.co.id")
            },
            onExportPdf = {
                viewModel.showMessage("📄 PDF Slip Gaji ${it.employeeName} siap dicetak & diunduh!")
            }
        )
    }

    if (selectedPayrollForAccounting != null) {
        AccountingSyncDialog(
            payroll = selectedPayrollForAccounting!!,
            onDismiss = { selectedPayrollForAccounting = null },
            onSyncNow = { provider ->
                viewModel.syncToAccountingSoftware(selectedPayrollForAccounting!!.id, provider)
            }
        )
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
        // Header & Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Penggajian & Pajak PPh 21 TER",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = CorporateBluePrimary
                )
                Text(
                    text = "Sesuai PP 58/2023 & BPJS Terkini",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                IconButton(
                    onClick = { viewModel.recalculatePayroll() },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Hitung Ulang", tint = CorporateBluePrimary)
                }

                FilledTonalButton(
                    onClick = { showExportDialog = true },
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.TableChart, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ekspor", fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Summary Metric Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PayrollMetricCard(
                modifier = Modifier.weight(1f),
                title = "Total Gaji Bersih",
                value = FormatHelper.formatRupiah(totalNetPay),
                color = Color(0xFF1B5E20)
            )
            PayrollMetricCard(
                modifier = Modifier.weight(1f),
                title = "Pajak PPh 21 TER",
                value = FormatHelper.formatRupiah(totalPPh21),
                color = Color(0xFFC62828)
            )
            PayrollMetricCard(
                modifier = Modifier.weight(1f),
                title = "Potongan BPJS",
                value = FormatHelper.formatRupiah(totalBpjs),
                color = Color(0xFF0288D1)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Cari staf, NIK, atau departemen...", fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(20.dp)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Payroll Cards List
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredList) { item ->
                PayrollItemCard(
                    payroll = item,
                    onOpenSlip = { selectedPayrollForSlip = item },
                    onSendEmail = {
                        val emp = employees.find { it.id == item.employeeId }
                        viewModel.sendSlipGajiNotification(item.id, item.employeeName, emp?.email ?: "karyawan@nusantaraprima.co.id")
                    },
                    onSyncAccounting = { selectedPayrollForAccounting = item }
                )
            }
        }
    }
}

@Composable
private fun PayrollMetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    color: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(title, style = MaterialTheme.typography.labelSmall, color = Color.Gray, maxLines = 1)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = color, maxLines = 1)
        }
    }
}

@Composable
private fun PayrollItemCard(
    payroll: PayrollRecord,
    onOpenSlip: () -> Unit,
    onSendEmail: () -> Unit,
    onSyncAccounting: () -> Unit
) {
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
                        text = payroll.employeeName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${payroll.employeeNik} • ${payroll.department}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = payroll.employeeStatus,
                        color = CorporateBluePrimary,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Income & Tax details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Total Bruto:", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text(FormatHelper.formatRupiah(payroll.totalGrossIncome), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                }
                Column {
                    Text("PPh 21 TER (${String.format(java.util.Locale.US, "%.2f", payroll.terEffectiveRatePercentage)}%):", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text(FormatHelper.formatRupiah(payroll.pph21WithheldAmount), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = Color(0xFFC62828))
                }
                Column {
                    Text("Take Home Pay:", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text(FormatHelper.formatRupiah(payroll.netTakeHomePay), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons: Slip Gaji, Email Notification, Accounting Sync
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = onOpenSlip,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = CorporateBluePrimary),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Lihat Slip", fontSize = 11.sp)
                }

                OutlinedButton(
                    onClick = onSendEmail,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (payroll.emailSlipSent) "Terkirim" else "Kirim Email", fontSize = 11.sp)
                }

                FilledTonalButton(
                    onClick = onSyncAccounting,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.CloudSync, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Akuntansi", fontSize = 11.sp)
                }
            }
        }
    }
}
