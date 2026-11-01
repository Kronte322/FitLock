package com.fitlock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.health.connect.client.PermissionController
import androidx.lifecycle.lifecycleScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fitlock.health.HealthConnectRepository
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    private lateinit var healthConnect: HealthConnectRepository
    private var status by mutableStateOf("Checking Health Connect…")
    private var sessions by mutableStateOf<List<String>>(emptyList())

    private val permissionLauncher = registerForActivityResult(
        PermissionController.createRequestPermissionResultContract()
    ) { granted ->
        lifecycleScope.launch {
            if (granted.containsAll(HealthConnectRepository.PERMISSIONS)) {
                loadTodaySessions()
            } else {
                status = "Exercise access was not granted."
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (HealthConnectRepository.isAvailable(this)) {
            healthConnect = HealthConnectRepository(this)
            status = "Health Connect is available."
        } else {
            status = "Health Connect is not available. Install or update Health Connect."
        }

        setContent {
            MaterialTheme {
                FitLockHome(
                    status = status,
                    sessions = sessions,
                    onConnect = {
                        if (HealthConnectRepository.isAvailable(this)) {
                            permissionLauncher.launch(HealthConnectRepository.PERMISSIONS)
                        }
                    },
                    onRefresh = {
                        lifecycleScope.launch { loadTodaySessions() }
                    }
                )
            }
        }
    }

    private suspend fun loadTodaySessions() {
        try {
            if (!healthConnect.hasExerciseReadPermission()) {
                status = "Grant permission to read exercise sessions."
                return
            }
            val records = healthConnect.readTodaySessions()
            sessions = records.map { record ->
                val minutes = Duration.between(record.startTime, record.endTime).toMinutes()
                val type = record.exerciseType.toString()
                "$type · $minutes min"
            }
            status = if (records.isEmpty()) {
                "No exercise sessions recorded today."
            } else {
                "Today's exercise sessions: ${records.size}"
            }
        } catch (error: Exception) {
            status = "Couldn't read Health Connect: ${error.localizedMessage ?: "unknown error"}"
        }
    }
}

@Composable
private fun FitLockHome(
    status: String,
    sessions: List<String>,
    onConnect: () -> Unit,
    onRefresh: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("FitLock", style = MaterialTheme.typography.headlineLarge)
            Text(
                text = "Daily activity unlocks your apps.",
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp),
                style = MaterialTheme.typography.bodyLarge
            )
            Text(status)
            Button(onClick = onConnect, modifier = Modifier.padding(top = 16.dp)) {
                Text("Connect Health Connect")
            }
            Button(onClick = onRefresh, modifier = Modifier.padding(top = 8.dp)) {
                Text("Refresh today's workouts")
            }
            sessions.forEach { session ->
                Text(session, modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}
