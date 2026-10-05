package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.DutyLogEntity
import com.example.data.local.entities.IncidentEntity
import com.example.data.local.entities.NotificationEntity
import com.example.data.local.entities.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransportDao {

    // --- Users & Access Control ---
    @Query("SELECT * FROM users ORDER BY role ASC, name ASC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: Long): UserEntity?

    @Query("SELECT * FROM users WHERE isAccessActive = 1")
    fun getActiveUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET isAccessActive = :isActive WHERE id = :userId")
    suspend fun updateUserAccessStatus(userId: Long, isActive: Boolean)

    @Query("UPDATE users SET role = :newRole WHERE id = :userId")
    suspend fun updateUserRole(userId: Long, newRole: String)

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUser(userId: Long)

    // --- Duty Logs & Attendance Tracking ---
    @Query("SELECT * FROM duty_logs ORDER BY onDutyTime DESC")
    fun getAllDutyLogs(): Flow<List<DutyLogEntity>>

    @Query("SELECT * FROM duty_logs WHERE isOnDuty = 1 ORDER BY onDutyTime DESC")
    fun getActiveOnDutyLogs(): Flow<List<DutyLogEntity>>

    @Query("SELECT * FROM duty_logs WHERE userId = :userId AND isOnDuty = 1 LIMIT 1")
    suspend fun getActiveDutyForUser(userId: Long): DutyLogEntity?

    @Query("SELECT * FROM duty_logs WHERE userId = :userId ORDER BY onDutyTime DESC")
    fun getDutyLogsForUser(userId: Long): Flow<List<DutyLogEntity>>

    @Query("SELECT * FROM duty_logs WHERE onDutyTime >= :startOfDay ORDER BY onDutyTime DESC")
    fun getTodayDutyLogs(startOfDay: Long): Flow<List<DutyLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDutyLog(dutyLog: DutyLogEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDutyLogs(dutyLogs: List<DutyLogEntity>)

    @Update
    suspend fun updateDutyLog(dutyLog: DutyLogEntity)

    // --- Incident & Operational Updates ---
    @Query("SELECT * FROM incident_updates ORDER BY timestamp DESC")
    fun getAllIncidents(): Flow<List<IncidentEntity>>

    @Query("SELECT * FROM incident_updates WHERE status != 'RESOLVED' ORDER BY timestamp DESC")
    fun getActiveIncidents(): Flow<List<IncidentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncident(incident: IncidentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncidents(incidents: List<IncidentEntity>)

    @Query("UPDATE incident_updates SET status = :status, resolutionNotes = :notes WHERE id = :id")
    suspend fun updateIncidentStatus(id: Long, status: String, notes: String)

    // --- Notifications & Broadcasts ---
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Query("SELECT COUNT(*) FROM notifications WHERE isRead = 0")
    fun getUnreadNotificationCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)

    @Query("UPDATE notifications SET isRead = 1 WHERE isRead = 0")
    suspend fun markAllNotificationsAsRead()

    @Query("DELETE FROM notifications")
    suspend fun clearAllNotifications()
}
