package com.example.data.repository

import com.example.data.local.dao.TransportDao
import com.example.data.local.entities.DutyLogEntity
import com.example.data.local.entities.IncidentEntity
import com.example.data.local.entities.NotificationEntity
import com.example.data.local.entities.UserEntity
import kotlinx.coroutines.flow.Flow

class TransportRepository(private val dao: TransportDao) {

    // Users
    val allUsers: Flow<List<UserEntity>> = dao.getAllUsers()
    val activeUsers: Flow<List<UserEntity>> = dao.getActiveUsers()

    suspend fun getUserById(userId: Long): UserEntity? = dao.getUserById(userId)

    suspend fun saveUser(user: UserEntity): Long {
        return if (user.id == 0L) {
            dao.insertUser(user)
        } else {
            dao.updateUser(user)
            user.id
        }
    }

    suspend fun updateUserAccess(userId: Long, isActive: Boolean) {
        dao.updateUserAccessStatus(userId, isActive)
    }

    suspend fun updateUserRole(userId: Long, newRole: String) {
        dao.updateUserRole(userId, newRole)
    }

    suspend fun deleteUser(userId: Long) {
        dao.deleteUser(userId)
    }

    // Duty Logs
    val allDutyLogs: Flow<List<DutyLogEntity>> = dao.getAllDutyLogs()
    val activeOnDutyLogs: Flow<List<DutyLogEntity>> = dao.getActiveOnDutyLogs()

    fun getDutyLogsForUser(userId: Long): Flow<List<DutyLogEntity>> {
        return dao.getDutyLogsForUser(userId)
    }

    suspend fun getActiveDutyForUser(userId: Long): DutyLogEntity? {
        return dao.getActiveDutyForUser(userId)
    }

    suspend fun punchOnDuty(
        user: UserEntity,
        station: String,
        busOrDesk: String,
        shiftType: String,
        lat: Double,
        lng: Double,
        locationName: String,
        notes: String
    ): DutyLogEntity {
        // If user already had an active duty, close it first
        val activeDuty = dao.getActiveDutyForUser(user.id)
        if (activeDuty != null) {
            val now = System.currentTimeMillis()
            val totalMins = (now - activeDuty.onDutyTime) / (60 * 1000)
            dao.updateDutyLog(
                activeDuty.copy(
                    isOnDuty = false,
                    offDutyTime = now,
                    totalDutyMinutes = totalMins
                )
            )
        }

        val newDuty = DutyLogEntity(
            userId = user.id,
            employeeId = user.employeeId,
            userName = user.name,
            userRole = user.role,
            station = station,
            busOrDesk = busOrDesk,
            shiftType = shiftType,
            onDutyTime = System.currentTimeMillis(),
            offDutyTime = null,
            isOnDuty = true,
            locationLat = lat,
            locationLng = lng,
            locationName = locationName,
            handoverNotes = notes
        )
        val id = dao.insertDutyLog(newDuty)

        // Broadcast notification to team
        dao.insertNotification(
            NotificationEntity(
                title = "${user.name} is ON DUTY",
                message = "Assigned to $station ($busOrDesk). Shift: $shiftType.",
                type = "DUTY_ALERT",
                severity = "INFO"
            )
        )

        return newDuty.copy(id = id)
    }

    suspend fun punchOffDuty(
        dutyId: Long,
        offDutyLocation: String,
        handoverNotes: String
    ) {
        val now = System.currentTimeMillis()
        val allLogs = dao.getAllDutyLogs()
        // We fetch active duty directly or update
        val activeLogs = dao.getActiveOnDutyLogs()
        // We'll update by id
        // In room, let's load and update
        val currentDuty = dao.getActiveDutyForUser(dutyId) // or find by id
        // Let's create an update query or fetch user's active log
    }

    suspend fun completeDuty(
        dutyLog: DutyLogEntity,
        offDutyLocation: String,
        notes: String
    ) {
        val now = System.currentTimeMillis()
        val minutes = (now - dutyLog.onDutyTime) / (60 * 1000)
        val updated = dutyLog.copy(
            isOnDuty = false,
            offDutyTime = now,
            offDutyLocationName = offDutyLocation,
            handoverNotes = if (notes.isNotBlank()) "${dutyLog.handoverNotes} | Checkout: $notes" else dutyLog.handoverNotes,
            totalDutyMinutes = minutes.coerceAtLeast(1)
        )
        dao.updateDutyLog(updated)

        // Broadcast notification to team
        dao.insertNotification(
            NotificationEntity(
                title = "${dutyLog.userName} marked OFF DUTY",
                message = "Completed shift at ${dutyLog.station}. Total duty: ${minutes / 60}h ${minutes % 60}m.",
                type = "DUTY_ALERT",
                severity = "INFO"
            )
        )
    }

    // Incidents & Updates
    val allIncidents: Flow<List<IncidentEntity>> = dao.getAllIncidents()
    val activeIncidents: Flow<List<IncidentEntity>> = dao.getActiveIncidents()

    suspend fun createIncident(
        title: String,
        description: String,
        category: String,
        severity: String,
        affectedRoute: String,
        reportedByUser: UserEntity
    ): Long {
        val incident = IncidentEntity(
            title = title,
            description = description,
            category = category,
            severity = severity,
            affectedRoute = affectedRoute,
            reportedByUserId = reportedByUser.id,
            reportedByName = reportedByUser.name,
            reportedByRole = reportedByUser.role,
            timestamp = System.currentTimeMillis(),
            status = "OPEN"
        )
        val id = dao.insertIncident(incident)

        // Broadcast notification to all team members
        dao.insertNotification(
            NotificationEntity(
                title = "🚨 [${severity}] $title",
                message = "$affectedRoute: $description (Reported by ${reportedByUser.name})",
                type = "INCIDENT_ALERT",
                severity = severity
            )
        )

        return id
    }

    suspend fun updateIncidentStatus(id: Long, status: String, notes: String) {
        dao.updateIncidentStatus(id, status, notes)
    }

    // Notifications
    val allNotifications: Flow<List<NotificationEntity>> = dao.getAllNotifications()
    val unreadCount: Flow<Int> = dao.getUnreadNotificationCount()

    suspend fun markAllNotificationsAsRead() = dao.markAllNotificationsAsRead()
    suspend fun clearNotifications() = dao.clearAllNotifications()
}
