package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SwapHoriz
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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.data.local.entities.UserEntity
import com.example.data.model.TransportDefaults
import com.example.data.model.UserRole
import com.example.ui.theme.IndoGoldSecondary
import com.example.ui.theme.IndoGreenSuccess
import com.example.ui.theme.IndoNavyPrimary
import com.example.ui.theme.IndoRedDanger

@Composable
fun AdminScreen(
    currentUser: UserEntity?,
    allUsers: List<UserEntity>,
    onToggleUserAccess: (userId: Long, currentStatus: Boolean) -> Unit,
    onUpdateRole: (userId: Long, newRole: String) -> Unit,
    onAddNewUser: (name: String, empId: String, phone: String, role: String, station: String) -> Unit,
    onDeleteUser: (userId: Long) -> Unit,
    onSwitchToAdminUser: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddUserDialog by remember { mutableStateOf(false) }
    var selectedUserForRoleChange by remember { mutableStateOf<UserEntity?>(null) }
    var userToDelete by remember { mutableStateOf<UserEntity?>(null) }

    val isAdmin = currentUser?.role == "ADMINISTRATOR"

    Scaffold(
        floatingActionButton = {
            if (isAdmin) {
                FloatingActionButton(
                    onClick = { showAddUserDialog = true },
                    containerColor = IndoNavyPrimary,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("add_user_fab")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.PersonAdd, contentDescription = "Add")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add Staff", fontWeight = FontWeight.Bold)
                    }
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Admin Panel Header
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
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = null,
                                    tint = IndoGoldSecondary,
                                    modifier = Modifier.size(26.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "User Access Control",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isAdmin) IndoGreenSuccess else IndoGoldSecondary
                            ) {
                                Text(
                                    text = if (isAdmin) "FULL ACCESS" else "VIEW ONLY",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Manage CRS executives, supervisors, role permissions, and access status.",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.75f))
                        )
                    }
                }
            }

            // Elevated Permission Alert if not Admin
            if (!isAdmin) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = IndoGoldSecondary.copy(alpha = 0.15f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = IndoGoldSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Viewing as ${currentUser?.role?.replace("_", " ")}",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "To modify staff roles or revoke access, switch to the Administrator profile.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Button(
                                onClick = onSwitchToAdminUser,
                                colors = ButtonDefaults.buttonColors(containerColor = IndoNavyPrimary),
                                modifier = Modifier.testTag("switch_to_admin_button")
                            ) {
                                Text("Be Admin", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Security Overview Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        SecurityStat(
                            title = "Active Staff",
                            value = "${allUsers.count { it.isAccessActive }}",
                            color = IndoGreenSuccess
                        )
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(32.dp)
                                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                        )
                        SecurityStat(
                            title = "Suspended",
                            value = "${allUsers.count { !it.isAccessActive }}",
                            color = IndoRedDanger
                        )
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(32.dp)
                                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                        )
                        SecurityStat(
                            title = "Admins",
                            value = "${allUsers.count { it.role == "ADMINISTRATOR" }}",
                            color = IndoNavyPrimary
                        )
                    }
                }
            }

            // User Access List
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Registered Personnel (${allUsers.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            items(allUsers) { user ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (user.isAccessActive) MaterialTheme.colorScheme.surface
                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(if (user.isAccessActive) IndoNavyPrimary else Color.Gray),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = (user.name.firstOrNull() ?: 'U').toString(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = user.name,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    if (user.id == currentUser?.id) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = IndoNavyPrimary.copy(alpha = 0.1f)
                                        ) {
                                            Text(
                                                text = "YOU",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = IndoNavyPrimary,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                }

                                Text(
                                    text = "${user.employeeId} • ${user.role.replace("_", " ")}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = IndoGoldSecondary,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Text(
                                    text = "${user.station} • ${user.phone}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }

                            // Active / Suspended Access Toggle
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = if (user.isAccessActive) "Active" else "Suspended",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (user.isAccessActive) IndoGreenSuccess else IndoRedDanger
                                )
                                Switch(
                                    checked = user.isAccessActive,
                                    onCheckedChange = { onToggleUserAccess(user.id, user.isAccessActive) },
                                    enabled = isAdmin,
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = IndoGreenSuccess,
                                        checkedTrackColor = IndoGreenSuccess.copy(alpha = 0.3f)
                                    ),
                                    modifier = Modifier.testTag("toggle_user_${user.id}")
                                )
                            }
                        }

                        if (isAdmin) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(
                                    onClick = { selectedUserForRoleChange = user }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SwapHoriz,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Change Role", fontSize = 12.sp)
                                }

                                if (user.id != currentUser?.id) {
                                    TextButton(
                                        onClick = { userToDelete = user }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = null,
                                            tint = IndoRedDanger,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Delete", color = IndoRedDanger, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }

    // Add New Staff Dialog
    if (showAddUserDialog) {
        AddNewStaffDialog(
            onDismiss = { showAddUserDialog = false },
            onConfirm = { name, empId, phone, role, station ->
                onAddNewUser(name, empId, phone, role, station)
                showAddUserDialog = false
            }
        )
    }

    // Change Role Dialog
    selectedUserForRoleChange?.let { user ->
        ChangeRoleDialog(
            user = user,
            onDismiss = { selectedUserForRoleChange = null },
            onConfirm = { newRole ->
                onUpdateRole(user.id, newRole)
                selectedUserForRoleChange = null
            }
        )
    }

    // Delete Confirmation Dialog
    userToDelete?.let { user ->
        AlertDialog(
            onDismissRequest = { userToDelete = null },
            title = { Text("Delete Staff Member?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to remove ${user.name} (${user.employeeId}) from Indo Canadian Transport?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteUser(user.id)
                        userToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IndoRedDanger)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { userToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SecurityStat(title: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
            color = color
        )
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewStaffDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, empId: String, phone: String, role: String, station: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var employeeId by remember { mutableStateOf("ICT-CRS-${(100..999).random()}") }
    var phone by remember { mutableStateOf("+91 ") }
    var role by remember { mutableStateOf(UserRole.CRS_EXECUTIVE.name) }
    var station by remember { mutableStateOf(TransportDefaults.STATIONS.first().name) }
    var stationExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Team Member", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name *") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = employeeId,
                    onValueChange = { employeeId = it },
                    label = { Text("Employee ID *") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Mobile Phone *") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Role Selector
                Column {
                    Text("Role Assignment:", style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(
                            UserRole.CRS_EXECUTIVE.name to "CRS Exec",
                            UserRole.FLEET_SUPERVISOR.name to "Supervisor",
                            UserRole.STATION_MANAGER.name to "Manager",
                            UserRole.ADMINISTRATOR.name to "Admin"
                        ).forEach { (rKey, rLabel) ->
                            FilterChip(
                                selected = role == rKey,
                                onClick = { role = rKey },
                                label = { Text(rLabel, fontSize = 11.sp) }
                            )
                        }
                    }
                }

                // Station Dropdown
                ExposedDropdownMenuBox(
                    expanded = stationExpanded,
                    onExpandedChange = { stationExpanded = !stationExpanded }
                ) {
                    OutlinedTextField(
                        value = station,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Assigned Station") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = stationExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = stationExpanded,
                        onDismissRequest = { stationExpanded = false }
                    ) {
                        TransportDefaults.STATIONS.forEach { s ->
                            DropdownMenuItem(
                                text = { Text(s.name) },
                                onClick = {
                                    station = s.name
                                    stationExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && employeeId.isNotBlank()) {
                        onConfirm(name, employeeId, phone, role, station)
                    }
                },
                enabled = name.isNotBlank() && employeeId.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = IndoNavyPrimary)
            ) {
                Text("Create User")
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
fun ChangeRoleDialog(
    user: UserEntity,
    onDismiss: () -> Unit,
    onConfirm: (newRole: String) -> Unit
) {
    var selectedRole by remember { mutableStateOf(user.role) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Change Role for ${user.name}", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    UserRole.CRS_EXECUTIVE.name to "CRS Executive (Counter Reservation)",
                    UserRole.FLEET_SUPERVISOR.name to "Fleet Supervisor (Bus Deployment)",
                    UserRole.STATION_MANAGER.name to "Station Manager (Station Reports & Oversight)",
                    UserRole.ADMINISTRATOR.name to "Administrator (Full Access Control)"
                ).forEach { (rKey, rLabel) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedRole == rKey) IndoNavyPrimary.copy(alpha = 0.12f)
                            else MaterialTheme.colorScheme.surface
                        ),
                        onClick = { selectedRole = rKey }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = rLabel,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (selectedRole == rKey) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedRole) },
                colors = ButtonDefaults.buttonColors(containerColor = IndoNavyPrimary)
            ) {
                Text("Update Role")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
