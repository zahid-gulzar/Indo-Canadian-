package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.ReportProblem
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.example.data.local.entities.DutyLogEntity
import com.example.data.local.entities.NotificationEntity
import com.example.data.local.entities.UserEntity
import com.example.data.model.IncidentCategory
import com.example.data.model.IncidentSeverity
import com.example.data.model.ShiftType
import com.example.data.model.TransportDefaults
import com.example.ui.theme.IndoGoldSecondary
import com.example.ui.theme.IndoGreenSuccess
import com.example.ui.theme.IndoNavyPrimary
import com.example.ui.theme.IndoRedDanger
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PunchInDialog(
    user: UserEntity,
    onDismiss: () -> Unit,
    onConfirm: (station: String, busOrDesk: String, shift: String, notes: String) -> Unit
) {
    var selectedStation by remember { mutableStateOf(user.station.ifBlank { TransportDefaults.STATIONS.first().name }) }
    var stationExpanded by remember { mutableStateOf(false) }

    var selectedBusOrDesk by remember { mutableStateOf(TransportDefaults.FLEET_BUSES.first()) }
    var busExpanded by remember { mutableStateOf(false) }

    var selectedShift by remember { mutableStateOf(ShiftType.GENERAL.name) }
    var handoverNotes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(IndoGreenSuccess.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = IndoGreenSuccess,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Mark ON DUTY",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "${user.name} (${user.employeeId})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Station Dropdown
                ExposedDropdownMenuBox(
                    expanded = stationExpanded,
                    onExpandedChange = { stationExpanded = !stationExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedStation,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Station / Terminal Location") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = stationExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        colors = OutlinedTextFieldDefaults.colors()
                    )
                    ExposedDropdownMenu(
                        expanded = stationExpanded,
                        onDismissRequest = { stationExpanded = false }
                    ) {
                        TransportDefaults.STATIONS.forEach { station ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(text = station.name, fontWeight = FontWeight.SemiBold)
                                        Text(
                                            text = station.city,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                },
                                onClick = {
                                    selectedStation = station.name
                                    stationExpanded = false
                                }
                            )
                        }
                    }
                }

                // Bus / Counter Desk Dropdown
                ExposedDropdownMenuBox(
                    expanded = busExpanded,
                    onExpandedChange = { busExpanded = !busExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedBusOrDesk,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Assigned Fleet / Counter Desk") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = busExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = busExpanded,
                        onDismissRequest = { busExpanded = false }
                    ) {
                        TransportDefaults.FLEET_BUSES.forEach { bus ->
                            DropdownMenuItem(
                                text = { Text(text = bus) },
                                onClick = {
                                    selectedBusOrDesk = bus
                                    busExpanded = false
                                }
                            )
                        }
                    }
                }

                // Shift Type Chips
                Column {
                    Text(
                        text = "Shift Schedule",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        ShiftType.values().forEach { shift ->
                            FilterChip(
                                selected = selectedShift == shift.name,
                                onClick = { selectedShift = shift.name },
                                label = { Text(text = shift.label, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = IndoNavyPrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }

                // GPS Telemetry Banner
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = IndoNavyPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Real-time GPS will be recorded with timestamp upon punch-in.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Notes / Pre-duty Checklist
                OutlinedTextField(
                    value = handoverNotes,
                    onValueChange = { handoverNotes = it },
                    label = { Text("Pre-duty Remarks (Optional)") },
                    placeholder = { Text("e.g. Counter float received, Morning roster checked") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(selectedStation, selectedBusOrDesk, selectedShift, handoverNotes)
                },
                colors = ButtonDefaults.buttonColors(containerColor = IndoGreenSuccess),
                modifier = Modifier.testTag("confirm_punch_in_button")
            ) {
                Text("Confirm ON DUTY", fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun PunchOutDialog(
    activeDuty: DutyLogEntity,
    durationText: String,
    onDismiss: () -> Unit,
    onConfirm: (notes: String, checkoutLocation: String) -> Unit
) {
    var checkoutLocation by remember { mutableStateOf(activeDuty.station) }
    var checkoutNotes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(IndoRedDanger.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = IndoRedDanger,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Mark OFF DUTY",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = activeDuty.userName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Time summary card
                Card(
                    colors = CardDefaults.cardColors(containerColor = IndoNavyPrimary.copy(alpha = 0.08f)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Shift Duration: $durationText",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = IndoNavyPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Started at: ${SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(activeDuty.onDutyTime))}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Station: ${activeDuty.station} (${activeDuty.busOrDesk})",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                OutlinedTextField(
                    value = checkoutLocation,
                    onValueChange = { checkoutLocation = it },
                    label = { Text("Checkout Terminal / Station") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = checkoutNotes,
                    onValueChange = { checkoutNotes = it },
                    label = { Text("Handover / Shift Closing Summary") },
                    placeholder = { Text("e.g. Next executive Arvinder briefed, cash tallied") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(checkoutNotes, checkoutLocation) },
                colors = ButtonDefaults.buttonColors(containerColor = IndoRedDanger),
                modifier = Modifier.testTag("confirm_punch_out_button")
            ) {
                Text("Punch OFF DUTY", fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun NewIncidentDialog(
    user: UserEntity,
    onDismiss: () -> Unit,
    onSubmit: (title: String, desc: String, category: String, severity: String, route: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(IncidentCategory.ROUTE_DELAY.name) }
    var selectedSeverity by remember { mutableStateOf(IncidentSeverity.WARNING.name) }
    var affectedRoute by remember { mutableStateOf("Delhi IGI Airport <-> Jalandhar") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(IndoRedDanger.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ReportProblem,
                        contentDescription = null,
                        tint = IndoRedDanger,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Broadcast Update / Issue",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Will notify all active team members",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Update Title *") },
                    placeholder = { Text("e.g. Fog delay on GT Road / Bus breakdown") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("incident_title_input"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = affectedRoute,
                    onValueChange = { affectedRoute = it },
                    label = { Text("Affected Route / Station *") },
                    placeholder = { Text("e.g. Delhi IGI T3 -> Amritsar") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Severity Selection
                Column {
                    Text(
                        text = "Severity / Priority",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IncidentSeverity.values().forEach { sev ->
                            val isSelected = selectedSeverity == sev.name
                            val chipColor = when (sev) {
                                IncidentSeverity.CRITICAL -> IndoRedDanger
                                IncidentSeverity.HIGH -> Color(0xFFE65100)
                                IncidentSeverity.WARNING -> IndoGoldSecondary
                                IncidentSeverity.INFO -> IndoNavyPrimary
                            }
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedSeverity = sev.name },
                                label = { Text(text = sev.name, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = chipColor,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }

                // Category Chips
                Column {
                    Text(
                        text = "Category",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IncidentCategory.values().forEach { cat ->
                            FilterChip(
                                selected = selectedCategory == cat.name,
                                onClick = { selectedCategory = cat.name },
                                label = { Text(text = cat.label, fontSize = 11.sp) }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Details & Action Required *") },
                    placeholder = { Text("Explain situation, expected delay, instructions for CRS...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && description.isNotBlank()) {
                        onSubmit(title, description, selectedCategory, selectedSeverity, affectedRoute)
                    }
                },
                enabled = title.isNotBlank() && description.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = IndoNavyPrimary),
                modifier = Modifier.testTag("submit_incident_button")
            ) {
                Text("Broadcast to Team", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ProfileSwitcherDialog(
    currentUser: UserEntity?,
    allUsers: List<UserEntity>,
    onDismiss: () -> Unit,
    onSelectUser: (UserEntity) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(IndoNavyPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsBus,
                        contentDescription = null,
                        tint = IndoGoldSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Switch Staff Profile",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Test CRS Exec, Manager, Supervisor or Admin",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(allUsers) { user ->
                    val isSelected = user.id == currentUser?.id
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectUser(user) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) IndoNavyPrimary.copy(alpha = 0.12f)
                            else MaterialTheme.colorScheme.surface
                        ),
                        border = if (isSelected) CardDefaults.outlinedCardBorder().copy(width = 2.dp)
                        else null
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(IndoNavyPrimary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = (user.name.firstOrNull() ?: 'U').toString(),
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = user.name,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "${user.employeeId} • ${user.role.replace("_", " ")}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = IndoGoldSecondary
                                    )
                                    Text(
                                        text = user.station,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = IndoGreenSuccess
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSheet(
    notifications: List<NotificationEntity>,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = null,
                        tint = IndoNavyPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Team Broadcasts & Alerts",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
                Text(
                    text = "${notifications.size} updates",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (notifications.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No broadcast notifications yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(notifications) { item ->
                        val chipColor = when (item.severity) {
                            "CRITICAL" -> IndoRedDanger
                            "HIGH" -> Color(0xFFE65100)
                            "WARNING" -> IndoGoldSecondary
                            else -> IndoNavyPrimary
                        }

                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = chipColor
                                    )
                                    Text(
                                        text = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(item.timestamp)),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = item.message,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
