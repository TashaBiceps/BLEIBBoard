package com.example.bleibboard.ui.state

import com.example.bleibboard.data.local.SortType
import com.example.bleibboard.data.local.Tests

data class TestListState(
    val tests: List<Tests> = emptyList(),
    val firstName: String = "",
    val lastName: String = "",
    val date: String = "",
    val time: String = "",
    val isAddingContact: Boolean = false,
    val sortType: SortType = SortType.FIRST_NAME
)
