package com.fitlock.health

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.Instant
import java.time.ZoneId

class HealthConnectRepository(context: Context) {
    private val client = HealthConnectClient.getOrCreate(context)

    suspend fun hasExerciseReadPermission(): Boolean =
        client.permissionController.getGrantedPermissions().containsAll(PERMISSIONS)

    suspend fun readTodaySessions(): List<ExerciseSessionRecord> {
        val zone = ZoneId.systemDefault()
        val startOfDay = java.time.LocalDate.now(zone).atStartOfDay(zone).toInstant()
        val now = Instant.now()

        return client.readRecords(
            ReadRecordsRequest(
                recordType = ExerciseSessionRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startOfDay, now)
            )
        ).records
    }

    companion object {
        val PERMISSIONS = setOf(
            HealthPermission.getReadPermission(ExerciseSessionRecord::class)
        )

        fun isAvailable(context: Context): Boolean =
            HealthConnectClient.getSdkStatus(context) == HealthConnectClient.SDK_AVAILABLE
    }
}
