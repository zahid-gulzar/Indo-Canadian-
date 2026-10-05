package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.DutyLogEntity
import com.example.data.local.entities.IncidentEntity
import com.example.data.local.entities.UserEntity
import com.example.data.model.TransportDefaults
import com.example.ui.theme.IndoGoldSecondary
import com.example.ui.theme.IndoGreenSuccess
import com.example.ui.theme.IndoNavyPrimary
import com.example.ui.theme.IndoRedDanger
import com.example.ui.viewmodel.ShiftReportSummary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ManagerScreen(
    currentUser: UserEntity?,
    allUsers: List<UserEntity>,
    activeOnDutyLogs: List<DutyLogEntity>,
    allDutyLogs: List<DutyLogEntity>,
    allIncidents: List<IncidentEntity>,
    onGenerateReport: (station: String?, shift: String?) -> ShiftReportSummary,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var stationFilter by remember { mutableStateOf("All") }
    var shiftFilter by remember { mutableStateOf("All") }

    val reportSummary = remember(stationFilter, shiftFilter, allDutyLogs, allIncidents) {
        onGenerateReport(stationFilter, shiftFilter)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Manager Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = IndoNavyPrimary)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Assessment,
                                contentDescription = null,
                                tint = IndoGoldSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Operations Dashboard",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = IndoGoldSecondary
                        ) {
                            Text(
                                text = "MANAGER REPORT",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = IndoNavyPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Indo Canadian Transport Co. • Fleet & Attendance Monitoring",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.75f))
                    )
                }
            }
        }

        // Live Operational KPIs Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatMetricCard(
                    title = "Staff Active",
                    value = "${activeOnDutyLogs.size}",
                    sub = "of ${allUsers.size} rostered",
                    icon = Icons.Default.People,
                    accentColor = IndoGreenSuccess,
                    modifier = Modifier.weight(1f)
                )

                StatMetricCard(
                    title = "Total Shifts",
                    value = "${reportSummary.totalDutyLogs}",
                    sub = "attendance records",
                    icon = Icons.Default.DirectionsBus,
                    accentColor = IndoNavyPrimary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatMetricCard(
                    title = "Duty Hours",
                    value = "${reportSummary.totalDutyHours}h",
                    sub = "avg ${reportSummary.averageShiftHours}h/shift",
                    icon = Icons.Default.Schedule,
                    accentColor = IndoGoldSecondary,
                    modifier = Modifier.weight(1f)
                )

                StatMetricCard(
                    title = "Open Issues",
                    value = "${allIncidents.count { it.status != "RESOLVED" }}",
                    sub = "${reportSummary.resolvedIncidents} resolved",
                    icon = Icons.Default.Warning,
                    accentColor = IndoRedDanger,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Report Filter Controls
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Generate Customized Duty Report",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    // Station Filter
                    Column {
                        Text(
                            text = "Filter by Terminal / Station:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            listOf("All", "Airport", "Jalandhar", "Amritsar", "Ludhiana", "Delhi ISBT").forEach { station ->
                                FilterChip(
                                    selected = stationFilter == station,
                                    onClick = { stationFilter = station },
                                    label = { Text(station) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = IndoNavyPrimary,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }

                    // Shift Filter
                    Column {
                        Text(
                            text = "Filter by Shift:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            listOf("All", "MORNING", "GENERAL", "EVENING", "NIGHT_AIRPORT").forEach { shift ->
                                FilterChip(
                                    selected = shiftFilter == shift,
                                    onClick = { shiftFilter = shift },
                                    label = { Text(shift.replace("_", " ")) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = IndoNavyPrimary,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Generated Report Preview & Share
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "REPORT SUMMARY",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = IndoNavyPrimary,
                                letterSpacing = 1.sp
                            )
                        )
                        Text(
                            text = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date()),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            ReportSummaryRow("Selected Station Scope:", stationFilter)
                            ReportSummaryRow("Selected Shift Scope:", shiftFilter.replace("_", " "))
                            ReportSummaryRow("Total Duty Shifts Logged:", "${reportSummary.totalDutyLogs}")
                            ReportSummaryRow("Total Working Hours:", "${reportSummary.totalDutyHours} hrs")
                            ReportSummaryRow("Active Personnel on Duty:", "${reportSummary.activeOnDutyCount}")
                            ReportSummaryRow("Total Incidents Logged:", "${reportSummary.totalIncidents}")
                            ReportSummaryRow("Resolved Incidents:", "${reportSummary.resolvedIncidents}")
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Share Report via WhatsApp / SMS / Email
                    Button(
                        onClick = {
                            val shareText = buildString {
                                appendLine("📋 *INDO CANADIAN TPT CO - OPERATIONS REPORT*")
                                appendLine("Date: ${SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault()).format(Date())}")
                                appendLine("Generated by: ${currentUser?.name ?: "Operations Manager"}")
                                appendLine("-----------------------------------")
                                appendLine("• Station Scope: $stationFilter")
                                appendLine("• Shift Scope: ${shiftFilter.replace("_", " ")}")
                                appendLine("• Total Duty Shifts: ${reportSummary.totalDutyLogs}")
                                appendLine("• Total Duty Hours: ${reportSummary.totalDutyHours} hrs")
                                appendLine("• Current Staff Active On Duty: ${reportSummary.activeOnDutyCount}")
                                appendLine("• Total Operational Incidents: ${reportSummary.totalIncidents}")
                                appendLine("• Resolved Incidents: ${reportSummary.resolvedIncidents}")
                                appendLine("-----------------------------------")
                                appendLine("Active Staff Deployment:")
                                activeOnDutyLogs.forEach { log ->
                                    appendLine(" - ${log.userName} (${log.userRole.replace("_", " ")}) @ ${log.station}")
                                }
                                appendLine("-----------------------------------")
                                appendLine("Indo Canadian Transport Central Reservation System")
                            }

                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareText)
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, "Share Operations Report")
                            context.startActivity(shareIntent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = IndoNavyPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("export_report_button")
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Export / Share Report", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun StatMetricCard(
    title: String,
    value: String,
    sub: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = accentColor
                )
            )

            Text(
                text = sub,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ReportSummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = IndoNavyPrimary
        )
    }
}
