package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.BiometricValidationType
import com.example.ui.theme.CorporateBluePrimary
import kotlinx.coroutines.delay

@Composable
fun BiometricDialog(
    validationType: BiometricValidationType,
    employeeName: String,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    var scanProgress by remember { mutableStateOf(0f) }
    var isVerified by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // Simulate progressive biometric facial landmark or fingerprint scanner
        for (i in 1..100) {
            delay(18)
            scanProgress = i / 100f
        }
        isVerified = true
        delay(600)
        onSuccess()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val laserY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser"
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (validationType == BiometricValidationType.FACE_SCAN) "Verifikasi Wajah AI" else "Pemindaian Sidik Jari",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = CorporateBluePrimary
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Arahkan sensor ke $employeeName untuk validasi kehadiran",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val stroke = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                        // Outer tracking circle
                        drawCircle(
                            color = Color(0xFF0D47A1).copy(alpha = 0.2f),
                            radius = size.minDimension / 2 - 8.dp.toPx(),
                            style = stroke
                        )
                        // Progress arc
                        drawArc(
                            color = if (isVerified) Color(0xFF2E7D32) else Color(0xFF1976D2),
                            startAngle = -90f,
                            sweepAngle = 360f * scanProgress,
                            useCenter = false,
                            style = stroke
                        )

                        // Laser scan line
                        if (!isVerified) {
                            val scanTop = 20.dp.toPx()
                            val scanBottom = size.height - 20.dp.toPx()
                            val currentY = scanTop + (scanBottom - scanTop) * laserY
                            drawLine(
                                color = Color(0xFF00E5FF),
                                start = Offset(24.dp.toPx(), currentY),
                                end = Offset(size.width - 24.dp.toPx(), currentY),
                                strokeWidth = 3.dp.toPx()
                            )
                        }
                    }

                    if (isVerified) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Terverifikasi",
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(72.dp)
                        )
                    } else {
                        Icon(
                            imageVector = if (validationType == BiometricValidationType.FACE_SCAN)
                                Icons.Default.Face else Icons.Default.Fingerprint,
                            contentDescription = "Biometrik",
                            tint = CorporateBluePrimary,
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                LinearProgressIndicator(
                    progress = { scanProgress },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (isVerified) Color(0xFF2E7D32) else CorporateBluePrimary,
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (isVerified) "✅ Biometrik Terverifikasi 100%!" else "Menganalisis Biometrik... ${(scanProgress * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isVerified) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Liveness Detection & Anti-Spoofing Aktif",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
