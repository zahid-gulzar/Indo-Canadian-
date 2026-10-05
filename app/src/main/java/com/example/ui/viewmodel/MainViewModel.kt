package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entities.DutyLogEntity
import com.example.data.local.entities.IncidentEntity
import com.example.data.local.entities.NotificationEntity
import com.example.data.local.entities.UserEntity
import com.example.data.model.TransportDefaults
import com.example.data.repository.TransportRepository
import com.example.service.LocationHelper
import com.example.service.NotificationHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class AppTab(val title: String, val shortLabel: String) {
    MY_DUTY("My Duty", "Duty"),
    TEAM_ROSTER("Live Roster", "Roster"),
    UPDATES("Team Updates", "Updates"),
    REPORTS("Reports & Analytics", "Reports"),
    ADMIN("Admin & Access", "Admin")
}

data class ShiftReportSummary(
    val totalDutyLogs: Int,
    val activeOnDutyCount: Int,
    val totalDutyHours: Double,
    val averageShiftHours: Double,
    val totalIncidents: Int,
    val resolvedIncidents: Int,
    val stationBreakdown: Map<String, Int>,
    val roleBreakdown: Map<String, Int>
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TransportRepository
    private val locationHelper: LocationHelper = LocationHelper(application)

    init {
        val db = AppDatabase.getInstance(application)
        repository = TransportRepository(db.transportDao())
    }

    // Navigation State
    private val _currentTab = MutableStateFlow(AppTab.MY_DUTY)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    // Current Logged-in User
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    // Active Duty for Current User
    private val _currentActiveDuty = MutableStateFlow<DutyLogEntity?>(null)
    val currentActiveDuty: StateFlow<DutyLogEntity?> = _currentActiveDuty.asStateFlow()

    // Running duty duration string (e.g. "03h 45m 12s")
    private val _dutyDurationText = MutableStateFlow("00h 00m")
    val dutyDurationText: StateFlow<String> = _dutyDurationText.asStateFlow()

    // Banner message for new incident
    private val _urgentBanner = MutableStateFlow<IncidentEntity?>(null)
    val urgentBanner: StateFlow<IncidentEntity?> = _urgentBanner.asStateFlow()

    // Dialog & UI triggers
    private val _showPunchInDialog = MutableStateFlow(false)
    val showPunchInDialog: StateFlow<Boolean> = _showPunchInDialog.asStateFlow()

    private val _showPunchOutDialog = MutableStateFlow(false)
    val showPunchOutDialog: StateFlow<Boolean> = _showPunchOutDialog.asStateFlow()

    private val _showNewIncidentDialog = MutableStateFlow(false)
    val showNewIncidentDialog: StateFlow<Boolean> = _showNewIncidentDialog.asStateFlow()

    private val _showProfileSwitcher = MutableStateFlow(false)
    val showProfileSwitcher: StateFlow<Boolean> = _showProfileSwitcher.asStateFlow()

    private val _showNotificationSheet = MutableStateFlow(false)
    val showNotificationSheet: StateFlow<Boolean> = _showNotificationSheet.asStateFlow()

    // Repository Flows
    val allUsers: StateFlow<List<UserEntity>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeOnDutyLogs: StateFlow<List<DutyLogEntity>> = repository.activeOnDutyLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allDutyLogs: StateFlow<List<DutyLogEntity>> = repository.allDutyLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allIncidents: StateFlow<List<IncidentEntity>> = repository.allIncidents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<NotificationEntity>> = repository.allNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadNotificationsCount: StateFlow<Int> = repository.unreadCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        // Initialize Notification channel
        NotificationHelper.initNotificationChannel(application)

        // Set default user once user list emits
        viewModelScope.launch {
            allUsers.collect { users ->
                if (_currentUser.value == null && users.isNotEmpty()) {
                    // Try to find CRS Executive "Zahid Gulzar" or first user
                    val defaultUser = users.find { it.employeeId == "ICT-CRS-101" } ?: users.first()
                    _currentUser.value = defaultUser
                    checkUserActiveDuty(defaultUser.id)
                }
            }
        }

        // Live timer tick for active duty
        viewModelScope.launch {
            while (isActive) {
                updateDutyTimer()
                delay(1000)
            }
        }

        // Keep banner updated if new critical incident arrives
        viewModelScope.launch {
            allIncidents.collect { incidents ->
                val critical = incidents.firstOrNull { it.severity == "CRITICAL" && it.status != "RESOLVED" }
                    ?: incidents.firstOrNull { it.severity == "HIGH" && it.status == "OPEN" }
                _urgentBanner.value = critical
            }
        }
    }

    private suspend fun checkUserActiveDuty(userId: Long) {
        val active = repository.getActiveDutyForUser(userId)
        _currentActiveDuty.value = active
    }

    private fun updateDutyTimer() {
        val duty = _currentActiveDuty.value ?: return
        val elapsedMs = System.currentTimeMillis() - duty.onDutyTime
        if (elapsedMs < 0) {
            _dutyDurationText.value = "00h 00m"
            return
        }
        val totalSec = elapsedMs / 1000
        val hours = totalSec / 3600
        val mins = (totalSec % 3600) / 60
        val secs = totalSec % 60
        _dutyDurationText.value = String.format("%02dh %02dm %02ds", hours, mins, secs)
    }

    // --- Tab Selection ---
    fun selectTab(tab: AppTab) {
        _currentTab.value = tab
    }

    // --- Profile Switcher ---
    fun switchUser(user: UserEntity) {
        _currentUser.value = user
        _showProfileSwitcher.value = false
        viewModelScope.launch {
            checkUserActiveDuty(user.id)
        }
    }

    fun openProfileSwitcher() {
        _showProfileSwitcher.value = true
    }

    fun closeProfileSwitcher() {
        _showProfileSwitcher.value = false
    }

    // --- Punch On / Off Duty Dialogs ---
    fun openPunchInDialog() {
        _showPunchInDialog.value = true
    }

    fun closePunchInDialog() {
        _showPunchInDialog.value = false
    }

    fun openPunchOutDialog() {
        _showPunchOutDialog.value = true
    }

    fun closePunchOutDialog() {
        _showPunchOutDialog.value = false
    }

    fun openNewIncidentDialog() {
        _showNewIncidentDialog.value = true
    }

    fun closeNewIncidentDialog() {
        _showNewIncidentDialog.value = false
    }

    fun openNotificationsSheet() {
        _showNotificationSheet.value = true
        markNotificationsRead()
    }

    fun closeNotificationsSheet() {
        _showNotificationSheet.value = false
    }

    fun dismissUrgentBanner() {
        _urgentBanner.value = null
    }

    // --- Duty Punch Actions ---
    fun punchOnDuty(
        station: String,
        busOrDesk: String,
        shiftType: String,
        notes: String
    ) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            // Find station coordinates
            val stationInfo = TransportDefaults.STATIONS.find { it.name == station }
                ?: TransportDefaults.STATIONS.first()

            val geoResult = locationHelper.getCurrentLocation(
                fallbackStationName = station,
                fallbackLat = stationInfo.lat,
                fallbackLng = stationInfo.lng
            )

            val duty = repository.punchOnDuty(
                user = user,
                station = station,
                busOrDesk = busOrDesk,
                shiftType = shiftType,
                lat = geoResult.latitude,
                lng = geoResult.longitude,
                locationName = "${stationInfo.city} - ${geoResult.locationDescription}",
                notes = notes
            )

            _currentActiveDuty.value = duty
            _showPunchInDialog.value = false

            // Trigger system notification
            NotificationHelper.sendTeamNotification(
                getApplication(),
                title = "Indo Canadian: Duty Started",
                message = "You are ON DUTY at $station ($busOrDesk). GPS verified.",
                isUrgent = false
            )
        }
    }

    fun punchOffDuty(handoverNotes: String, offDutyLocation: String) {
        val activeDuty = _currentActiveDuty.value ?: return
        viewModelScope.launch {
            repository.completeDuty(
                dutyLog = activeDuty,
                offDutyLocation = offDutyLocation,
                notes = handoverNotes
            )
            _currentActiveDuty.value = null
            _showPunchOutDialog.value = false

            NotificationHelper.sendTeamNotification(
                getApplication(),
                title = "Indo Canadian: Duty Completed",
                message = "Off-duty marked successfully. Shift hours recorded.",
                isUrgent = false
            )
        }
    }

    // --- Incident & Broadcast Updates ---
    fun reportIncident(
        title: String,
        description: String,
        category: String,
        severity: String,
        affectedRoute: String
    ) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.createIncident(
                title = title,
                description = description,
                category = category,
                severity = severity,
                affectedRoute = affectedRoute,
                reportedByUser = user
            )
            _showNewIncidentDialog.value = false

            // System push alert
            NotificationHelper.sendTeamNotification(
                getApplication(),
                title = "🚨 [Indo Canadian Alert] $title",
                message = "$affectedRoute: $description (By ${user.name})",
                isUrgent = severity == "CRITICAL" || severity == "HIGH"
            )
        }
    }

    fun updateIncidentStatus(incidentId: Long, status: String, notes: String) {
        viewModelScope.launch {
            repository.updateIncidentStatus(incidentId, status, notes)
        }
    }

    // --- Admin Panel Actions ---
    fun addNewStaffMember(
        name: String,
        employeeId: String,
        phone: String,
        role: String,
        station: String
    ) {
        viewModelScope.launch {
            val newUser = UserEntity(
                employeeId = employeeId,
                name = name,
                role = role,
                phone = phone,
                station = station,
                isAccessActive = true,
                avatarColorHex = when (role) {
                    "ADMINISTRATOR" -> "#8E24AA"
                    "STATION_MANAGER" -> "#2E7D32"
                    "FLEET_SUPERVISOR" -> "#D48806"
                    else -> "#0E2A4E"
                }
            )
            repository.saveUser(newUser)
        }
    }

    fun toggleUserAccess(userId: Long, currentStatus: Boolean) {
        viewModelScope.launch {
            repository.updateUserAccess(userId, !currentStatus)
        }
    }

    fun updateUserRole(userId: Long, newRole: String) {
        viewModelScope.launch {
            repository.updateUserRole(userId, newRole)
        }
    }

    fun deleteUser(userId: Long) {
        viewModelScope.launch {
            repository.deleteUser(userId)
        }
    }

    fun markNotificationsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsAsRead()
        }
    }

    // --- Reports Generator Calculation ---
    fun calculateShiftReport(stationFilter: String?, shiftFilter: String?): ShiftReportSummary {
        val logs = allDutyLogs.value.filter { log ->
            (stationFilter == null || stationFilter == "All" || log.station.contains(stationFilter, ignoreCase = true)) &&
            (shiftFilter == null || shiftFilter == "All" || log.shiftType.equals(shiftFilter, ignoreCase = true))
        }

        val totalMins = logs.sumOf { it.totalDutyMinutes }
        val totalHours = totalMins / 60.0
        val avgHours = if (logs.isNotEmpty()) totalHours / logs.size else 0.0

        val stationCounts = logs.groupingBy { it.station }.eachCount()
        val roleCounts = logs.groupingBy { it.userRole }.eachCount()

        val incidentsList = allIncidents.value
        val resolved = incidentsList.count { it.status == "RESOLVED" }

        return ShiftReportSummary(
            totalDutyLogs = logs.size,
            activeOnDutyCount = activeOnDutyLogs.value.size,
            totalDutyHours = String.format("%.1f", totalHours).toDoubleOrNull() ?: totalHours,
            averageShiftHours = String.format("%.1f", avgHours).toDoubleOrNull() ?: avgHours,
            totalIncidents = incidentsList.size,
            resolvedIncidents = resolved,
            stationBreakdown = stationCounts,
            roleBreakdown = roleCounts
        )
    }
}
