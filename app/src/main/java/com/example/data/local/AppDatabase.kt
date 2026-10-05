package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.TransportDao
import com.example.data.local.entities.DutyLogEntity
import com.example.data.local.entities.IncidentEntity
import com.example.data.local.entities.NotificationEntity
import com.example.data.local.entities.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        DutyLogEntity::class,
        IncidentEntity::class,
        NotificationEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transportDao(): TransportDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "indo_canadian_transport.db"
                )
                    .addCallback(DatabaseCallback())
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    seedInitialData(database.transportDao())
                }
            }
        }

        private suspend fun seedInitialData(dao: TransportDao) {
            val currentTime = System.currentTimeMillis()
            val twoHoursAgo = currentTime - (2 * 3600 * 1000)
            val fourHoursAgo = currentTime - (4 * 3600 * 1000)
            val oneDayAgo = currentTime - (24 * 3600 * 1000)

            // Seed Users (Indo Canadian Transport Co Team)
            val users = listOf(
                UserEntity(
                    id = 1,
                    employeeId = "ICT-CRS-101",
                    name = "Zahid Gulzar",
                    role = "CRS_EXECUTIVE",
                    phone = "+91 98765 43210",
                    station = "IGI Airport Terminal 3 Counter",
                    isAccessActive = true,
                    avatarColorHex = "#0E2A4E"
                ),
                UserEntity(
                    id = 2,
                    employeeId = "ICT-CRS-104",
                    name = "Harpreet Singh",
                    role = "CRS_EXECUTIVE",
                    phone = "+91 98140 11223",
                    station = "Indo Canadian Lounge, Jalandhar",
                    isAccessActive = true,
                    avatarColorHex = "#1A538C"
                ),
                UserEntity(
                    id = 3,
                    employeeId = "ICT-OPS-202",
                    name = "Raman Sharma",
                    role = "FLEET_SUPERVISOR",
                    phone = "+91 94170 55667",
                    station = "Kashmere Gate ISBT Counter 14",
                    isAccessActive = true,
                    avatarColorHex = "#D48806"
                ),
                UserEntity(
                    id = 4,
                    employeeId = "ICT-MGR-301",
                    name = "Maninder Kaur",
                    role = "STATION_MANAGER",
                    phone = "+91 98888 77665",
                    station = "Near Golden Temple Terminal, Amritsar",
                    isAccessActive = true,
                    avatarColorHex = "#2E7D32"
                ),
                UserEntity(
                    id = 5,
                    employeeId = "ICT-ADM-001",
                    name = "Rajesh Verma",
                    role = "ADMINISTRATOR",
                    phone = "+91 99999 12345",
                    station = "Central Operations HQ, Delhi",
                    isAccessActive = true,
                    avatarColorHex = "#8E24AA"
                ),
                UserEntity(
                    id = 6,
                    employeeId = "ICT-CRS-109",
                    name = "Gurpreet Sandhu",
                    role = "CRS_EXECUTIVE",
                    phone = "+91 98720 99887",
                    station = "Sherpur Chowk Terminal, Ludhiana",
                    isAccessActive = true,
                    avatarColorHex = "#00838F"
                )
            )
            dao.insertUsers(users)

            // Seed Active & Historical Duty Logs
            val dutyLogs = listOf(
                // Harpreet Singh currently ON DUTY in Jalandhar
                DutyLogEntity(
                    id = 1,
                    userId = 2,
                    employeeId = "ICT-CRS-104",
                    userName = "Harpreet Singh",
                    userRole = "CRS_EXECUTIVE",
                    station = "Indo Canadian Lounge, Jalandhar",
                    busOrDesk = "CRS Terminal Counter Desk 1",
                    shiftType = "MORNING",
                    onDutyTime = fourHoursAgo,
                    offDutyTime = null,
                    isOnDuty = true,
                    locationLat = 31.3260,
                    locationLng = 75.5762,
                    locationName = "GT Road Lounge, Jalandhar",
                    handoverNotes = "Morning booking peak managed smoothly. 18 Delhi passengers booked.",
                    totalDutyMinutes = 240
                ),
                // Raman Sharma currently ON DUTY in Delhi
                DutyLogEntity(
                    id = 2,
                    userId = 3,
                    employeeId = "ICT-OPS-202",
                    userName = "Raman Sharma",
                    userRole = "FLEET_SUPERVISOR",
                    station = "Kashmere Gate ISBT Counter 14",
                    busOrDesk = "PB-08-CX-9090 (Volvo B11R Luxury Sleeper)",
                    shiftType = "GENERAL",
                    onDutyTime = twoHoursAgo,
                    offDutyTime = null,
                    isOnDuty = true,
                    locationLat = 28.6672,
                    locationLng = 77.2285,
                    locationName = "ISBT Kashmere Gate Platform 3",
                    handoverNotes = "Pre-departure checklist verified. Volvo departing at 14:30.",
                    totalDutyMinutes = 120
                ),
                // Completed Duty Log yesterday
                DutyLogEntity(
                    id = 3,
                    userId = 1,
                    employeeId = "ICT-CRS-101",
                    userName = "Zahid Gulzar",
                    userRole = "CRS_EXECUTIVE",
                    station = "IGI Airport Terminal 3 Counter",
                    busOrDesk = "Terminal 3 Reservation Desk",
                    shiftType = "NIGHT_AIRPORT",
                    onDutyTime = oneDayAgo,
                    offDutyTime = oneDayAgo + (8 * 3600 * 1000),
                    isOnDuty = false,
                    locationLat = 28.5562,
                    locationLng = 77.1000,
                    locationName = "IGI Airport T3 Arrivals Deck",
                    offDutyLocationName = "Staff Lounge Aerocity",
                    handoverNotes = "Shift completed. All 4 international arrival buses dispatched on time.",
                    totalDutyMinutes = 480
                )
            )
            dao.insertDutyLogs(dutyLogs)

            // Seed Live Incidents & Team Updates
            val incidents = listOf(
                IncidentEntity(
                    id = 1,
                    title = "Fog & Slow Traffic on NH-44 Murthal Stretch",
                    description = "Heavy morning fog between Panipat and Murthal. Buses running approx 25-35 mins delayed for passenger safety. CRS team informed for passenger queries.",
                    category = "ROUTE_DELAY",
                    severity = "WARNING",
                    affectedRoute = "Delhi IGI Airport <-> Jalandhar / Amritsar",
                    reportedByUserId = 3,
                    reportedByName = "Raman Sharma",
                    reportedByRole = "FLEET_SUPERVISOR",
                    timestamp = twoHoursAgo + (30 * 60 * 1000),
                    status = "IN_PROGRESS",
                    resolutionNotes = "Drivers instructed to maintain 50kmph safe spacing. GPS active."
                ),
                IncidentEntity(
                    id = 2,
                    title = "Flight Influx at IGI T3 - High Demand for Punjab Routes",
                    description = "Three international flights landed simultaneously. High passenger rush at T3 Counter. Keep standby Volvo PB-10-DF-4400 ready.",
                    category = "PASSENGER_ADVISORY",
                    severity = "HIGH",
                    affectedRoute = "Delhi IGI T3 -> Ludhiana / Jalandhar",
                    reportedByUserId = 1,
                    reportedByName = "Zahid Gulzar",
                    reportedByRole = "CRS_EXECUTIVE",
                    timestamp = twoHoursAgo + (50 * 60 * 1000),
                    status = "OPEN",
                    resolutionNotes = ""
                )
            )
            dao.insertIncidents(incidents)

            // Seed Initial Notifications
            val notifications = listOf(
                NotificationEntity(
                    id = 1,
                    title = "Operational Notice: Fog Advisory NH-44",
                    message = "Murthal to Panipat sector has reduced visibility. Expect 25-30 min delay.",
                    type = "INCIDENT_ALERT",
                    severity = "WARNING",
                    timestamp = twoHoursAgo + (31 * 60 * 1000),
                    isRead = false
                ),
                NotificationEntity(
                    id = 2,
                    title = "CRS Passenger Rush Alert - IGI T3",
                    message = "Zahid Gulzar reported heavy flight arrival rush at Delhi T3 counter.",
                    type = "BROADCAST",
                    severity = "HIGH",
                    timestamp = twoHoursAgo + (51 * 60 * 1000),
                    isRead = false
                )
            )
            dao.insertNotifications(notifications)
        }
    }
}
