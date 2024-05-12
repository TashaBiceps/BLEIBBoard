package com.example.bleibboard.presentation.state

data class ActiveDeviceState (
    val isDeviceConnected: Boolean = false,
    val activeDeviceServices: Map<String, List<String>> = emptyMap()
)