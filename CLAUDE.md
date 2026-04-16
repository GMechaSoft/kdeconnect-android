# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build and Test Commands

The project uses Gradle for building and testing.

- **Build debug APK**: `./gradlew assembleDebug`
- **Build release APK**: `./gradlew assembleRelease`
- **Run all tests**: `./gradlew test`
- **Run a single test**: `./gradlew test --tests <package.ClassName.methodName>`
- **Run lint checks**: `./gradlew lint`

## Architecture Overview

This repository is the Android client for KDE Connect, a decentralized P2P synchronization system.

### Core Components

- **Network Backends** (`org.kde.kdeconnect.backends`): Handles communication layers.
    - **LAN/mDNS** (`lan`): Discovery and data transfer over local networks.
    - **Bluetooth** (`bluetooth`): Pairing and communication via Bluetooth.
    - **Loopback** (`loopback`): Used for local testing purposes.
- **Plugin System** (`org.kde.kdeconnect.plugins`): An extensible architecture where specific features are implemented as independent plugins (e.g., Battery, Clipboard, SFTP, SMS, System Volume).
- **Core Engine** (`org.kde.kdeconnect`): Manages device lifecycle, pairing, and background service orchestration.
- **UI Layer** (`org.kde.kdeconnect.ui`): A mix of traditional Android Views and modern Jetpack Compose components (found in `ui.compose`).
- **Helpers** (`org.kde.kdeconnect.helpers`): Android-specific utility classes for files, network, and security.

### Key Technologies

- **Language**: Kotlin (primary) and Java.
- **UI Framework**: Jetpack Compose & Android Views.
- **Async Processing**: Kotlin Coroutines and Flow.
- **Discovery**: mDNS (Multicast DNS).
- **Security**: Implementation of SSL and RSA helpers for secure pairing and communication.
