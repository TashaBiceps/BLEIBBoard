package com.example.bleibboard.ui.viewmodels

import AthleteTestsRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bleibboard.data.local.SortType
import com.example.bleibboard.data.local.TestEvent
import com.example.bleibboard.data.local.Tests
import com.example.bleibboard.data.local.TestsDao
import com.example.bleibboard.ui.state.TestListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TestListViewModel @Inject constructor(
    private val dao : TestsDao
): ViewModel() {

    private val _sortType = MutableStateFlow(SortType.FIRST_NAME)
    @OptIn(ExperimentalCoroutinesApi::class)
    private val _tests = _sortType
        .flatMapLatest { sortType ->
        when(sortType) {
            SortType.FIRST_NAME -> dao.queryTestsOrderedByFirstName()
            SortType.LAST_NAME -> dao.queryTestsOrderedByLastName()
            SortType.DATE -> dao.queryTestsOrderedByDate()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _state = MutableStateFlow(TestListState())
    val state = combine(_state, _sortType, _tests) { state, sortType, tests ->
        state.copy(
            tests = tests,
            sortType = sortType
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TestListState())

    fun onEvent(event: TestEvent) {
        when(event) {
            TestEvent.SaveTest -> {
                val firstName = state.value.firstName
                val lastName = state.value.lastName
                val date = state.value.date
                val time = state.value.time

                if(firstName.isBlank() || lastName.isBlank() || date.isBlank() || time.isBlank()) {
                    return
                }

                val test = Tests(
                    firstName = firstName,
                    lastName = lastName,
                    date = date,
                    time = time
                )
                viewModelScope.launch {
                    dao.upsertTest(test)
                }
                _state.update { it.copy(
                    firstName = "",
                    lastName = "",
                    date = "",
                    time = "",
                    isAddingContact = false
                ) }
            }
            is TestEvent.DeleteTest -> {
                viewModelScope.launch {
                    dao.deleteTest(event.test)
                }
            }
            is TestEvent.SetFirstName -> {
                _state.update { it.copy(
                    firstName = event.firstName
                ) }
            }
            is TestEvent.SetLastName -> {
                _state.update { it.copy(
                    lastName = event.lastName
                    ) }
            }
            is TestEvent.SetDate -> {
                _state.update { it.copy(
                    date = event.date
                ) }
            }
            is TestEvent.SetTime -> {
                _state.update { it.copy(
                    time = event.time
                ) }
            }
            TestEvent.ShowDialog -> {
                _state.update { it.copy(
                    isAddingContact = true
                ) }
            }
            TestEvent.HideDialog -> {
                _state.update { it.copy(
                    isAddingContact = false
                ) }
            }

            is TestEvent.SortTests -> {
                _sortType.value = event.sortType
            }

        }
    }
}