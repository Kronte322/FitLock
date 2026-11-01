# FitLock

Android app that will gate selected apps based on a configurable daily workout goal.

## Current MVP

- Kotlin + Jetpack Compose.
- Health Connect integration.
- Requests read access to exercise sessions.
- Reads and displays exercise sessions recorded today.

## Requirements

- Android 10 (API 29) or newer.
- Health Connect installed and available on the device.
- A source app (for example, a fitness watch companion app) that writes exercise sessions to Health Connect.
- Android Studio with JDK 17 and Android SDK 36.

## Build

Open this repository in Android Studio and sync the Gradle project. The project currently does not include the Gradle wrapper; use a locally installed compatible Gradle distribution or generate the wrapper from Android Studio.

## Roadmap

1. Read today's exercise sessions from Health Connect. (in progress)
2. Define configurable workout rules (types, duration, rest days, daily deadline).
3. Evaluate whether the daily goal has been met.
4. Add Android app blocking with an explicitly enabled AccessibilityService.
5. Add optional Mac companion integration.

## Important limitations

- FitLock does not block apps yet.
- Health Connect only exposes data written by apps the user has connected or authorized.
- Workout records can be delayed, incomplete, or manually entered. The app must not treat source metadata as proof that a workout genuinely happened.
- Health data stays on-device in this MVP; no server is required.
