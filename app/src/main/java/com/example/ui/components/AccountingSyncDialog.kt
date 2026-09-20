package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.PayrollRecord
import com.example.ui.theme.CorporateBluePrimary

@Composable
fun AccountingSyncDialog(
    payroll: PayrollRecord,
    onDismiss: () -> Unit,
    onSyncNow: (String) -> Unit
) {
    var selectedSoftware by remember { mutableStateOf("Accurate") }

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
                            Icons.Default.CloudSync,
                            contentDescription = null,
                            tint = CorporateBluePrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Integrasi Sistem Akuntansi",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Sinkronisasi otomatis jurnal penggajian & hutang pajak PPh 21 ke GL Akuntansi pihak ketiga.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Accurate", "Jurnal.id", "SAP").forEach { software ->
                        FilterChip(
                            selected = selectedSoftware == software,
                            onClick = { selectedSoftware = software },
                            label = { Text(software) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Payload Journal API ($selectedSoftware REST API v2):",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Code/JSON payload preview
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(12.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        val json = """
{
  "integration": "$selectedSoftware Cloud ERP",
  "journal_date": "2026-09-25",
  "memo": "Gaji & PPh 21 - ${payroll.employeeName} (${payroll.employeeNik})",
  "debit": [
    { "account": "5101-Beban Gaji Pokok", "amount": ${payroll.baseSalary.toLong()} },
    { "account": "5102-Beban Lembur", "amount": ${payroll.overtimePay.toLong()} },
    { "account": "5103-Beban Tunjangan", "amount": ${(payroll.allowanceTransport + payroll.allowanceMeal + payroll.allowancePerformance).toLong()} }
  ],
  "credit": [
    { "account": "2104-Hutang PPh 21 TER", "amount": ${payroll.pph21WithheldAmount.toLong()} },
    { "account": "2105-Hutang BPJS Ketenagakerjaan", "amount": ${(payroll.bpjsJhtEmployee + payroll.bpjsJpEmployee).toLong()} },
    { "account": "2106-Hutang BPJS Kesehatan", "amount": ${payroll.bpjsKesehatanEmployee.toLong()} },
    { "account": "1101-Kas & Bank (Net Pay)", "amount": ${payroll.netTakeHomePay.toLong()} }
  ],
  "status": "APPROVED_AUTO_POST"
}
                        """.trimIndent()
                        Text(
                            text = json,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = Color(0xFF38BDF8)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        onSyncNow(selectedSoftware)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = CorporateBluePrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Send, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sinkronkan ke $selectedSoftware Sekarang")
                }
            }
        }
    }
}
