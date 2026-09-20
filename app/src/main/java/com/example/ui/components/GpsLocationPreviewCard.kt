package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CompanyPolicy
import com.example.ui.theme.CorporateBluePrimary
import com.example.util.LocationHelper

@Composable
fun GpsLocationPreviewCard(
    userLat: Double,
    userLng: Double,
    userAddress: String,
    policy: CompanyPolicy,
    isFieldMode: Boolean,
    onToggleFieldMode: (Boolean) -> Unit
) {
    val distance = LocationHelper.calculateDistanceMeters(
        userLat, userLng,
        policy.officeLatitude, policy.officeLongitude
    )
    val isWithin = distance <= policy.officeRadiusMeters

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                    Icon(
                        imageVector = Icons.Default.GpsFixed,
                        contentDescription = null,
                        tint = CorporateBluePrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Lokasi Presensi GPS Real-Time",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isWithin) Color(0xFFE8F5E9) else if (isFieldMode) Color(0xFFE1F5FE) else Color(0xFFFFEBEE)
                ) {
                    Text(
                        text = if (isWithin) "Dalam Radius Kantor" else if (isFieldMode) "Mode Lapangan Aktif" else "Luar Radius Kantor",
                        color = if (isWithin) Color(0xFF2E7D32) else if (isFieldMode) Color(0xFF0288D1) else Color(0xFFD32F2F),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Visual Radar / Map Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0B192C)),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2, size.height / 2)

                    // Grid lines
                    val gridColor = Color(0xFF1E3E62).copy(alpha = 0.5f)
                    drawLine(gridColor, Offset(0f, center.y), Offset(size.width, center.y), 1.dp.toPx())
                    drawLine(gridColor, Offset(center.x, 0f), Offset(center.x, size.height), 1.dp.toPx())

                    // Geofence Circle (SCBD Office)
                    drawCircle(
                        color = Color(0xFF0D47A1).copy(alpha = 0.3f),
                        radius = 42.dp.toPx(),
                        center = center
                    )
                    drawCircle(
                        color = Color(0xFF29B6F6),
                        radius = 42.dp.toPx(),
                        center = center,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                            width = 2.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
                        )
                    )

                    // Office Pin Center
                    drawCircle(color = Color(0xFF00E676), radius = 6.dp.toPx(), center = center)

                    // User Pin Position
                    val userOffset = if (isWithin) {
                        Offset(center.x + 14.dp.toPx(), center.y - 12.dp.toPx())
                    } else {
                        Offset(center.x + 85.dp.toPx(), center.y + 20.dp.toPx())
                    }

                    // Pulse around user
                    drawCircle(
                        color = if (isWithin) Color(0xFF2E7D32).copy(alpha = 0.4f) else Color(0xFFFF5252).copy(alpha = 0.4f),
                        radius = 12.dp.toPx(),
                        center = userOffset
                    )
                    drawCircle(
                        color = if (isWithin) Color(0xFF2E7D32) else Color(0xFFFF1744),
                        radius = 5.dp.toPx(),
                        center = userOffset
                    )

                    // Connecting vector line
                    drawLine(
                        color = Color.White.copy(alpha = 0.4f),
                        start = center,
                        end = userOffset,
                        strokeWidth = 1.5.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f))
                    )
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color.Black.copy(alpha = 0.7f)
                    ) {
                        Text(
                            text = "Jarak: ${LocationHelper.formatDistance(distance)} (Maks: ${policy.officeRadiusMeters.toInt()}m)",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Place,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = userAddress,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Field Mode Toggle (Karyawan Lapangan)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Mode Karyawan Lapangan (Dinas Luar)",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Bebas geofence kantor dengan tagging koordinat GPS akurat",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = isFieldMode,
                    onCheckedChange = onToggleFieldMode,
                    colors = SwitchDefaults.colors(checkedThumbColor = CorporateBluePrimary)
                )
            }
        }
    }
}
