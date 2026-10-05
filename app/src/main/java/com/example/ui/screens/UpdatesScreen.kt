package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.IncidentEntity
import com.example.data.local.entities.UserEntity
import com.example.ui.theme.IndoGoldSecondary
import com.example.ui.theme.IndoGreenSuccess
import com.example.ui.theme.IndoNavyPrimary
import com.example.ui.theme.IndoRedDanger
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UpdatesScreen(
    incidents: List<IncidentEntity>,
    currentUser: UserEntity?,
    onNewIncidentClick: () -> Unit,
    onUpdateStatus: (incidentId: Long, status: String, notes: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategoryFilter by remember { mutableStateOf("All") }
    var selectedSeverityFilter by remember { mutableStateOf("All") }
    var selectedIncidentForStatus by remember { mutableStateOf<IncidentEntity?>(null) }

    val filteredIncidents = incidents.filter { item ->
        val matchesCategory = selectedCategoryFilter == "All" || item.category == selectedCategoryFilter
        val matchesSeverity = selectedSeverityFilter == "All" || item.severity == selectedSeverityFilter
        matchesCategory && matchesSeverity
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNewIncidentClick,
                containerColor = IndoNavyPrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("post_update_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Post Update", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = IndoNavyPrimary)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ReportProblem,
                                contentDescription = null,
                                tint = IndoGoldSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Operational Updates & Alerts",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Real-time broadcasts for bus delays, breakdowns, fog advisories & CRS counter notices.",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.75f))
                        )
                    }
                }
            }

            // Severity Filter Chips
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Filter by Severity",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("All", "CRITICAL", "HIGH", "WARNING", "INFO").forEach { sev ->
                            FilterChip(
                                selected = selectedSeverityFilter == sev,
                                onClick = { selectedSeverityFilter = sev },
                                label = { Text(sev) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = when (sev) {
                                        "CRITICAL" -> IndoRedDanger
                                        "HIGH" -> Color(0xFFE65100)
                                        "WARNING" -> IndoGoldSecondary
                                        else -> IndoNavyPrimary
                                    },
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Category Filter Chips
            item {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    FilterChip(
                        selected = selectedCategoryFilter == "All",
                        onClick = { selectedCategoryFilter = "All" },
                        label = { Text("All Categories") }
                    )
                    listOf(
                        "ROUTE_DELAY" to "Route Delays",
                        "BUS_BREAKDOWN" to "Breakdowns",
                        "PASSENGER_ADVISORY" to "Airport / Rush",
                        "CRS_BOOKING" to "CRS System"
                    ).forEach { (catKey, catName) ->
                        FilterChip(
                            selected = selectedCategoryFilter == catKey,
                            onClick = { selectedCategoryFilter = catKey },
                            label = { Text(catName) }
                        )
                    }
                }
            }

            // Incidents List
            if (filteredIncidents.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No operational updates for this filter.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(filteredIncidents) { incident ->
                    IncidentCard(
                        incident = incident,
                        onStatusClick = { selectedIncidentForStatus = incident }
                    )
                }
            }

            // Spacer for FAB clearance
            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }

    // Status Update Dialog
    selectedIncidentForStatus?.let { incident ->
        UpdateIncidentStatusDialog(
            incident = incident,
            onDismiss = { selectedIncidentForStatus = null },
            onConfirm = { newStatus, notes ->
                onUpdateStatus(incident.id, newStatus, notes)
                selectedIncidentForStatus = null
            }
        )
    }
}

@Composable
fun IncidentCard(
    incident: IncidentEntity,
    onStatusClick: () -> Unit
) {
    val (sevColor, sevBg) = when (incident.severity) {
        "CRITICAL" -> IndoRedDanger to IndoRedDanger.copy(alpha = 0.12f)
        "HIGH" -> Color(0xFFE65100) to Color(0xFFE65100).copy(alpha = 0.12f)
        "WARNING" -> IndoGoldSecondary to IndoGoldSecondary.copy(alpha = 0.12f)
        else -> IndoNavyPrimary to IndoNavyPrimary.copy(alpha = 0.1f)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Severity badge, Category, and Status pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = sevBg
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(sevColor)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = incident.severity,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = sevColor
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = when (incident.status) {
                        "RESOLVED" -> IndoGreenSuccess.copy(alpha = 0.15f)
                        "IN_PROGRESS" -> IndoGoldSecondary.copy(alpha = 0.15f)
                        else -> IndoRedDanger.copy(alpha = 0.15f)
                    }
                ) {
                    Text(
                        text = incident.status.replace("_", " "),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (incident.status) {
                            "RESOLVED" -> IndoGreenSuccess
                            "IN_PROGRESS" -> IndoGoldSecondary
                            else -> IndoRedDanger
                        },
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title & Route
            Text(
                text = incident.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.DirectionsBus,
                    contentDescription = null,
                    tint = IndoNavyPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = incident.affectedRoute,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    color = IndoNavyPrimary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Description
            Text(
                text = incident.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (incident.resolutionNotes.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Resolution Update:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = IndoGreenSuccess
                        )
                        Text(
                            text = incident.resolutionNotes,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Footer: Reporter info & Action button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Reported by ${incident.reportedByName} (${incident.reportedByRole.replace("_", " ")})",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(incident.timestamp)),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                TextButton(onClick = onStatusClick) {
                    Text(
                        text = if (incident.status == "RESOLVED") "View / Reopen" else "Update Status",
                        fontWeight = FontWeight.Bold,
                        color = IndoNavyPrimary
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateIncidentStatusDialog(
    incident: IncidentEntity,
    onDismiss: () -> Unit,
    onConfirm: (newStatus: String, notes: String) -> Unit
) {
    var status by remember { mutableStateOf(incident.status) }
    var notes by remember { mutableStateOf(incident.resolutionNotes) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Update Incident Status", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = incident.title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("OPEN", "IN_PROGRESS", "RESOLVED").forEach { s ->
                        FilterChip(
                            selected = status == s,
                            onClick = { status = s },
                            label = { Text(s.replace("_", " ")) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = when (s) {
                                    "RESOLVED" -> IndoGreenSuccess
                                    "IN_PROGRESS" -> IndoGoldSecondary
                                    else -> IndoRedDanger
                                },
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Action Taken / Resolution Notes") },
                    placeholder = { Text("e.g. Relief bus dispatched, passengers accommodated") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(status, notes) },
                colors = ButtonDefaults.buttonColors(containerColor = IndoNavyPrimary)
            ) {
                Text("Save Status")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
