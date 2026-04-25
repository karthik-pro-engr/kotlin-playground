// build.gradle.kts (if you need deps hint)
// implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ExampleViewModel {
    private val _ui = MutableStateFlow(0)

    // ---- CASE 1: LEAK (accidental) ----
    // no explicit type -> public type is MutableStateFlow<Int>
    // callers can do vm.uiLeaky.value = ...
    val uiLeaky = _ui

    // ---- CASE 2: INTENDED READ-ONLY ----
    // explicit read-only view (idiomatic)
    val uiSafe: StateFlow<Int> = _ui.asStateFlow()

    // internal mutation method - only this class should mutate
    fun incrementInternal() {
        _ui.value = _ui.value + 1
    }
}

fun main() = runBlocking {
    val vm = ExampleViewModel()

    // start collectors to show emissions
    val jobSafe = launch {
        vm.uiSafe.collect { println("uiSafe collected: $it") }
    }
    val jobLeaky = launch {
        vm.uiLeaky.collect { println("uiLeaky collected: $it") }
    }

    delay(100)

    // 1) Mutate via the leaked public MutableStateFlow (this should be impossible if it was exposed as StateFlow)
    println("\n-- Mutating vm.uiLeaky.value = 10 (accidental leak) --")
    vm.uiLeaky.value = 10
    delay(150)

    // 2) Mutate by unsafe cast from StateFlow back to MutableStateFlow
    println("\n-- Mutating (vm.uiSafe as MutableStateFlow<Int>).value = 20 (unsafe cast) --")
    @Suppress("UNCHECKED_CAST")
    (vm.uiSafe as MutableStateFlow<Int>).value = 20
    delay(150)

    // 3) Proper internal mutation (how it should be done)
    println("\n-- Proper internal mutation via method --")
    vm.incrementInternal()
    delay(150)

    jobSafe.cancel()
    jobLeaky.cancel()
}
