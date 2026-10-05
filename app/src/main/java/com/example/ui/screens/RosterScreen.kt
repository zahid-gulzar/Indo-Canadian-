package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.DutyLogEntity
import com.example.data.local.entities.UserEntity
import com.example.ui.theme.IndoGoldSecondary
import com.example.ui.theme.IndoGreenSuccess
import com.example.ui.theme.IndoNavyPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RosterScreen(
    allUsers: List<UserEntity>,
    activeOnDutyLogs: List<DutyLogEntity>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedStationFilter by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var filterOnlyActiveOnDuty by remember { mutableStateOf(false) }

    val activeUserIds = remember(activeOnDutyLogs) {
        activeOnDutyLogs.map { it.userId }.toSet()
    }

    val activeDutyByUserId = remember(activeOnDutyLogs) {
        activeOnDutyLogs.associateBy { it.userId }
    }

    val filteredUsers = allUsers.filter { user ->
        val matchesStation = selectedStationFilter == "All" ||
                user.station.contains(selectedStationFilter, ignoreCase = true)
        val matchesSearch = searchQuery.isBlank() ||
                user.name.contains(searchQuery, ignoreCase = true) ||
                user.employeeId.contains(searchQuery, ignoreCase = true) ||
                user.station.contains(searchQuery, ignoreCase = true)
        val matchesDutyState = !filterOnlyActiveOnDuty || activeUserIds.contains(user.id)
        matchesStation && matchesSearch && matchesDutyState
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header & Live Summary KPI
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = IndoNavyPrimary)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.People,
                                contentDescription = null,
                                tint = IndoGoldSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Live Team Roster",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                        Text(
                            text = "Real-time attendance & station deployment",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.7f))
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = IndoGreenSuccess
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "${activeOnDutyLogs.size} ACTIVE",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "of ${allUsers.size} Staff",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search by name, employee ID, station...") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("roster_search_field"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
        }

        // Station & State Filters
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    FilterChip(
                        selected = filterOnlyActiveOnDuty,
                        onClick = { filterOnlyActiveOnDuty = !filterOnlyActiveOnDuty },
                        label = { Text("● On Duty Only (${activeOnDutyLogs.size})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = IndoGreenSuccess,
                            selectedLabelColor = Color.White
                        )
                    )

                    listOf("All", "IGI Airport", "Jalandhar", "Amritsar", "Ludhiana", "Delhi ISBT").forEach { station ->
                        FilterChip(
                            selected = selectedStationFilter == station,
                            onClick = { selectedStationFilter = station },
                            label = { Text(station) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = IndoNavyPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // Staff List
        if (filteredUsers.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No staff members matching criteria.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(filteredUsers) { user ->
                val activeDuty = activeDutyByUserId[user.id]
                val isOnDuty = activeDuty != null

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // User Avatar
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(IndoNavyPrimary),
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

                        // User details
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = user.name,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                // Active Indicator
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (isOnDuty) IndoGreenSuccess else Color.LightGray)
                                )
                            }

                            Text(
                                text = "${user.employeeId} • ${user.role.replace("_", " ")}",
                                style = MaterialTheme.typography.bodySmall,
                                color = IndoGoldSecondary,
                                fontWeight = FontWeight.SemiBold
                            )

                            if (isOnDuty && activeDuty != null) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "On Duty: ${activeDuty.station} (${activeDuty.busOrDesk})",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = IndoGreenSuccess,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Since ${SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(activeDuty.onDutyTime))} • GPS Tracked",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                Text(
                                    text = "Base: ${user.station}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // Call Action
                        IconButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:${user.phone}")
                                }
                                context.startActivity(intent)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = "Call Staff",
                                tint = IndoNavyPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}
