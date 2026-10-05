package com.example

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entities.DutyLogEntity
import com.example.ui.components.IndoCanadianTopBar
import com.example.ui.components.NewIncidentDialog
import com.example.ui.components.NotificationSheet
import com.example.ui.components.ProfileSwitcherDialog
import com.example.ui.components.PunchInDialog
import com.example.ui.components.PunchOutDialog
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.DutyScreen
import com.example.ui.screens.ManagerScreen
import com.example.ui.screens.RosterScreen
import com.example.ui.screens.UpdatesScreen
import com.example.ui.theme.IndoGoldSecondary
import com.example.ui.theme.IndoGreenSuccess
import com.example.ui.theme.IndoNavyPrimary
import com.example.ui.theme.IndoRedDanger
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppScreen(viewModel: MainViewModel) {
    // Permission requests for Location & Push Notifications
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { _ ->
        // Permissions handled
    }

    LaunchedEffect(Unit) {
        val permissions = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        permissionLauncher.launch(permissions.toTypedArray())
    }

    // State observation
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val currentActiveDuty by viewModel.currentActiveDuty.collectAsStateWithLifecycle()
    val dutyDurationText by viewModel.dutyDurationText.collectAsStateWithLifecycle()
    val urgentBanner by viewModel.urgentBanner.collectAsStateWithLifecycle()

    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    val activeOnDutyLogs by viewModel.activeOnDutyLogs.collectAsStateWithLifecycle()
    val allDutyLogs by viewModel.allDutyLogs.collectAsStateWithLifecycle()
    val allIncidents by viewModel.allIncidents.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val unreadCount by viewModel.unreadNotificationsCount.collectAsStateWithLifecycle()

    // Dialogs
    val showPunchInDialog by viewModel.showPunchInDialog.collectAsStateWithLifecycle()
    val showPunchOutDialog by viewModel.showPunchOutDialog.collectAsStateWithLifecycle()
    val showNewIncidentDialog by viewModel.showNewIncidentDialog.collectAsStateWithLifecycle()
    val showProfileSwitcher by viewModel.showProfileSwitcher.collectAsStateWithLifecycle()
    val showNotificationSheet by viewModel.showNotificationSheet.collectAsStateWithLifecycle()

    // Handle back button to return to MY_DUTY if on another tab
    BackHandler(enabled = currentTab != AppTab.MY_DUTY) {
        viewModel.selectTab(AppTab.MY_DUTY)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 700.dp),
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                IndoCanadianTopBar(
                    currentUser = currentUser,
                    unreadCount = unreadCount,
                    urgentBanner = urgentBanner,
                    onProfileClick = { viewModel.openProfileSwitcher() },
                    onNotificationClick = { viewModel.openNotificationsSheet() },
                    onDismissBanner = { viewModel.dismissUrgentBanner() }
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = IndoNavyPrimary
                ) {
                    NavigationBarItem(
                        selected = currentTab == AppTab.MY_DUTY,
                        onClick = { viewModel.selectTab(AppTab.MY_DUTY) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = "My Duty"
                            )
                        },
                        label = { Text(AppTab.MY_DUTY.shortLabel, fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = IndoNavyPrimary,
                            indicatorColor = IndoNavyPrimary.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag("tab_my_duty")
                    )

                    NavigationBarItem(
                        selected = currentTab == AppTab.TEAM_ROSTER,
                        onClick = { viewModel.selectTab(AppTab.TEAM_ROSTER) },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (activeOnDutyLogs.isNotEmpty()) {
                                        Badge(containerColor = IndoGreenSuccess) {
                                            Text(activeOnDutyLogs.size.toString())
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Groups,
                                    contentDescription = "Live Roster"
                                )
                            }
                        },
                        label = { Text(AppTab.TEAM_ROSTER.shortLabel, fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = IndoNavyPrimary,
                            indicatorColor = IndoNavyPrimary.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag("tab_team_roster")
                    )

                    NavigationBarItem(
                        selected = currentTab == AppTab.UPDATES,
                        onClick = { viewModel.selectTab(AppTab.UPDATES) },
                        icon = {
                            val openAlerts = allIncidents.count { it.status != "RESOLVED" }
                            BadgedBox(
                                badge = {
                                    if (openAlerts > 0) {
                                        Badge(containerColor = IndoRedDanger) {
                                            Text(openAlerts.toString())
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Updates & Alerts"
                                )
                            }
                        },
                        label = { Text(AppTab.UPDATES.shortLabel, fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = IndoNavyPrimary,
                            indicatorColor = IndoNavyPrimary.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag("tab_updates")
                    )

                    NavigationBarItem(
                        selected = currentTab == AppTab.REPORTS,
                        onClick = { viewModel.selectTab(AppTab.REPORTS) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Assessment,
                                contentDescription = "Reports"
                            )
                        },
                        label = { Text(AppTab.REPORTS.shortLabel, fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = IndoNavyPrimary,
                            indicatorColor = IndoNavyPrimary.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag("tab_reports")
                    )

                    NavigationBarItem(
                        selected = currentTab == AppTab.ADMIN,
                        onClick = { viewModel.selectTab(AppTab.ADMIN) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = "Admin"
                            )
                        },
                        label = { Text(AppTab.ADMIN.shortLabel, fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = IndoNavyPrimary,
                            indicatorColor = IndoNavyPrimary.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag("tab_admin")
                    )
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (currentTab) {
                    AppTab.MY_DUTY -> {
                        val userHistory = allDutyLogs.filter { it.userId == (currentUser?.id ?: 0L) }
                        DutyScreen(
                            currentUser = currentUser,
                            activeDuty = currentActiveDuty,
                            dutyDurationText = dutyDurationText,
                            dutyHistory = userHistory,
                            onPunchInClick = { viewModel.openPunchInDialog() },
                            onPunchOutClick = { viewModel.openPunchOutDialog() },
                            onReportIssueClick = { viewModel.openNewIncidentDialog() },
                            onViewRosterClick = { viewModel.selectTab(AppTab.TEAM_ROSTER) }
                        )
                    }

                    AppTab.TEAM_ROSTER -> {
                        RosterScreen(
                            allUsers = allUsers,
                            activeOnDutyLogs = activeOnDutyLogs
                        )
                    }

                    AppTab.UPDATES -> {
                        UpdatesScreen(
                            incidents = allIncidents,
                            currentUser = currentUser,
                            onNewIncidentClick = { viewModel.openNewIncidentDialog() },
                            onUpdateStatus = { id, status, notes ->
                                viewModel.updateIncidentStatus(id, status, notes)
                            }
                        )
                    }

                    AppTab.REPORTS -> {
                        ManagerScreen(
                            currentUser = currentUser,
                            allUsers = allUsers,
                            activeOnDutyLogs = activeOnDutyLogs,
                            allDutyLogs = allDutyLogs,
                            allIncidents = allIncidents,
                            onGenerateReport = { station, shift ->
                                viewModel.calculateShiftReport(station, shift)
                            }
                        )
                    }

                    AppTab.ADMIN -> {
                        AdminScreen(
                            currentUser = currentUser,
                            allUsers = allUsers,
                            onToggleUserAccess = { userId, currentStatus ->
                                viewModel.toggleUserAccess(userId, currentStatus)
                            },
                            onUpdateRole = { userId, newRole ->
                                viewModel.updateUserRole(userId, newRole)
                            },
                            onAddNewUser = { name, empId, phone, role, station ->
                                viewModel.addNewStaffMember(name, empId, phone, role, station)
                            },
                            onDeleteUser = { userId ->
                                viewModel.deleteUser(userId)
                            },
                            onSwitchToAdminUser = {
                                val adminUser = allUsers.find { it.role == "ADMINISTRATOR" }
                                if (adminUser != null) {
                                    viewModel.switchUser(adminUser)
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // Modal Dialogs
    if (showPunchInDialog) {
        currentUser?.let { user ->
            PunchInDialog(
                user = user,
                onDismiss = { viewModel.closePunchInDialog() },
                onConfirm = { station, busOrDesk, shift, notes ->
                    viewModel.punchOnDuty(station, busOrDesk, shift, notes)
                }
            )
        }
    }

    if (showPunchOutDialog) {
        currentActiveDuty?.let { duty ->
            PunchOutDialog(
                activeDuty = duty,
                durationText = dutyDurationText,
                onDismiss = { viewModel.closePunchOutDialog() },
                onConfirm = { notes, loc ->
                    viewModel.punchOffDuty(notes, loc)
                }
            )
        }
    }

    if (showNewIncidentDialog) {
        currentUser?.let { user ->
            NewIncidentDialog(
                user = user,
                onDismiss = { viewModel.closeNewIncidentDialog() },
                onSubmit = { title, desc, cat, sev, route ->
                    viewModel.reportIncident(title, desc, cat, sev, route)
                }
            )
        }
    }

    if (showProfileSwitcher) {
        ProfileSwitcherDialog(
            currentUser = currentUser,
            allUsers = allUsers,
            onDismiss = { viewModel.closeProfileSwitcher() },
            onSelectUser = { selectedUser ->
                viewModel.switchUser(selectedUser)
            }
        )
    }

    if (showNotificationSheet) {
        NotificationSheet(
            notifications = notifications,
            onDismiss = { viewModel.closeNotificationsSheet() }
        )
    }
}
