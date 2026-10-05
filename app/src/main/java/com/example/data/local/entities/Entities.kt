package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val employeeId: String,
    val name: String,
    val role: String, // CRS_EXECUTIVE, FLEET_SUPERVISOR, STATION_MANAGER, ADMINISTRATOR
    val phone: String,
    val station: String,
    val isAccessActive: Boolean = true,
    val avatarColorHex: String = "#0E2A4E",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "duty_logs")
data class DutyLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val employeeId: String,
    val userName: String,
    val userRole: String,
    val station: String,
    val busOrDesk: String,
    val shiftType: String,
    val onDutyTime: Long,
    val offDutyTime: Long? = null,
    val isOnDuty: Boolean = true,
    val locationLat: Double = 0.0,
    val locationLng: Double = 0.0,
    val locationName: String = "",
    val offDutyLocationName: String? = null,
    val handoverNotes: String = "",
    val totalDutyMinutes: Long = 0
)

@Entity(tableName = "incident_updates")
data class IncidentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val category: String, // BUS_BREAKDOWN, ROUTE_DELAY, etc.
    val severity: String, // INFO, WARNING, HIGH, CRITICAL
    val affectedRoute: String,
    val reportedByUserId: Long,
    val reportedByName: String,
    val reportedByRole: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "OPEN", // OPEN, IN_PROGRESS, RESOLVED
    val resolutionNotes: String = ""
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val message: String,
    val type: String, // BROADCAST, DUTY_ALERT, INCIDENT_ALERT, SYSTEM
    val severity: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
