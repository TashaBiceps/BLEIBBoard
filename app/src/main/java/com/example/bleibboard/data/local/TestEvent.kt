package com.example.bleibboard.data.local

sealed interface TestEvent {
    object SaveTest: TestEvent
    data class SetFirstName(val firstName: String): TestEvent
    data class SetLastName(val lastName: String): TestEvent
    data class SetDate(val date: String): TestEvent
    data class SetTime(val time: String): TestEvent
    object ShowDialog: TestEvent
    object HideDialog: TestEvent
    data class SortTests(val sortType: SortType): TestEvent
    data class DeleteTest(val test: Tests): TestEvent
}