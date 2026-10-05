package com.example.data.model

enum class UserRole(val displayName: String, val level: Int) {
    CRS_EXECUTIVE("CRS Executive", 1),
    FLEET_SUPERVISOR("Fleet Supervisor", 2),
    STATION_MANAGER("Station Manager", 3),
    ADMINISTRATOR("Administrator", 4)
}

enum class ShiftType(val label: String, val hours: String) {
    MORNING("Morning Shift", "06:00 - 14:00"),
    GENERAL("General Shift", "09:00 - 18:00"),
    EVENING("Evening Shift", "14:00 - 22:00"),
    NIGHT_AIRPORT("Night Express (IGI)", "22:00 - 06:00")
}

enum class IncidentSeverity(val label: String) {
    INFO("General Notice"),
    WARNING("Operational Warning"),
    HIGH("Major Delay / Reroute"),
    CRITICAL("Emergency / Breakdown")
}

enum class IncidentCategory(val label: String) {
    BUS_BREAKDOWN("Vehicle / Breakdown"),
    ROUTE_DELAY("Traffic & Fog Delay"),
    CRS_BOOKING("CRS & Ticket System"),
    PASSENGER_ADVISORY("Passenger Rush / IGI Airport"),
    ROAD_BLOCK("Highway Block / Diversion"),
    STAFF_NOTICE("Staff Duty Notice")
}

data class StationInfo(
    val code: String,
    val name: String,
    val city: String,
    val lat: Double,
    val lng: Double
)

object TransportDefaults {
    val STATIONS = listOf(
        StationInfo("DEL-IGI", "IGI Airport Terminal 3 Counter", "New Delhi", 28.5562, 77.1000),
        StationInfo("DEL-ISBT", "Kashmere Gate ISBT Counter 14", "Delhi", 28.6672, 77.2285),
        StationInfo("JAL-CT", "Indo Canadian Lounge & Office, GT Road", "Jalandhar", 31.3260, 75.5762),
        StationInfo("ASR-GT", "Near Golden Temple Terminal", "Amritsar", 31.6200, 74.8765),
        StationInfo("LDH-BS", "Sherpur Chowk Indo Canadian Terminal", "Ludhiana", 30.9010, 75.8573),
        StationInfo("IXC-43", "Sector 43 Inter-State Terminal", "Chandigarh", 30.7180, 76.7450),
        StationInfo("PGW-GT", "Phagwara Bypass Counter", "Phagwara", 31.2240, 75.7708)
    )

    val FLEET_BUSES = listOf(
        "PB-08-CX-9090 (Volvo B11R Luxury Sleeper)",
        "PB-02-EE-1122 (Scania Multi-Axle Metrolink)",
        "PB-10-DF-4400 (Mercedes Multi-Axle Superfast)",
        "PB-08-AL-7800 (Volvo 9600 IGI Airport Shuttle)",
        "CRS Terminal Counter Desk 1",
        "CRS Terminal Counter Desk 2",
        "Central Dispatch & Reservations Deck"
    )
}
