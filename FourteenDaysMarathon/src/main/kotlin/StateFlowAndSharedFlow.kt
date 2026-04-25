// build.gradle.kts (dependencies you need)
// implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.Flow


/** --- Console main to collect flows --- */
fun main() = runBlocking {
    // stateFlowDemo()
//    sharedFlowDemo()
//    stopTimeoutMillisSuccess()
    stopTimeoutMillisFailure()
    /*val repo = Repo()
    val vm = ConsoleViewModel(repo)

    // Collector 1: UI state (StateFlow) - immediate snapshot and updates
    val jobState = launch {
        vm.uiState.collect { println("[UI] State = $it") }
    }

    // Collector 2: events (SharedFlow)
    val jobEvents = launch {
        vm.events.collect { println("[EVENT] $it") }
    }

    // Collector 3: numbersStateFlow (hot StateFlow converted from cold flow)
    val jobNumbersState = launch {
        vm.numbersStateFlow.collect { println("[numbersStateFlow] latest = $it") }
    }

    // Collector 4: numbersSharedFlow (hot broadcast)
    val jobNumbersShared = launch {
        vm.numbersSharedFlow.collect { println("[numbersSharedFlow] got = $it") }
    }

    // Start the ViewModel work
    vm.startWork()

    // Let it run a bit
    delay(1000)

    // Cancel and finish
    vm.stop()
    jobState.cancelAndJoin()
    jobEvents.cancelAndJoin()
    jobNumbersState.cancelAndJoin()
    jobNumbersShared.cancelAndJoin()
    println("done")*/
}

/** --- Repository: cold producer (simulates network / updates) --- */
class Repo {
    fun numbersColdFlow(): Flow<Int> = flow {
        for (i in 1..5) {
            delay(100) // simulate work
            emit(i)
        }
    }
}

/** --- ViewModel-like class for console use --- */
class ConsoleViewModel(private val repo: Repo) {
    // scope that mimics ViewModelScope
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    // UI state: expose read-only StateFlow
    private val _uiState = MutableStateFlow("Idle")
    val uiState: StateFlow<String> = _uiState.asStateFlow()

    // One-off events or broadcasts: use SharedFlow with no replay (events)
    private val _events = MutableSharedFlow<String>(
        replay = 0,
        extraBufferCapacity = 5,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val events: SharedFlow<String> = _events.asSharedFlow()

    // Turn cold repo flow into hot state flow (keeps latest value)
    val numbersStateFlow: StateFlow<Int> = repo
        .numbersColdFlow()
        .stateIn(scope, SharingStarted.Eagerly, 0) // starts immediately and holds latest

    // Turn cold repo flow into a SharedFlow broadcast (replay 1)
    val numbersSharedFlow: SharedFlow<Int> = repo
        .numbersColdFlow()
        .shareIn(scope, SharingStarted.WhileSubscribed(), replay = 1)

    fun startWork() {
        scope.launch {
            _uiState.value = "Loading"
            delay(150)
            _uiState.value = "Processing"
            _events.emit("StartedProcessing") // event
            // simulate more work
            delay(300)
            _uiState.value = "Done"
            _events.emit("Finished")
        }
    }

    fun stop() {
        scope.cancel()
    } // mimic ViewModel.onCleared
}


// ViewModel-like holder (console-friendly)
class UiViewModel {
    private val _uiState = MutableStateFlow(UiStateFlow(loading = false, counter = 0))
    val uiState: StateFlow<UiStateFlow> = _uiState.asStateFlow() // expose read-only

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    fun increment() {
        // update state atomically and immutably
        _uiState.value = _uiState.value.copy(counter = _uiState.value.counter + 1)
    }

    fun setLoading(on: Boolean) {
        _uiState.value = _uiState.value.copy(loading = on)
    }

    fun clear() {
        scope.cancel()
    }
}

data class UiStateFlow(val loading: Boolean = false, val counter: Int = 0)


// Console consumer
fun stateFlowDemo() = runBlocking {
    val vm = UiViewModel()

    // collector 1 (UI)
    val j1 = launch {
        vm.uiState.collect { println("[UI] $it") }
    }

    // update state
    vm.setLoading(true)
    delay(100)
    vm.increment()
    delay(100)

    // late subscriber sees latest snapshot immediately
    launch {
        delay(250)
        vm.uiState.collect { println("[Late UI] $it") }
    }

    delay(400)
    j1.cancel()
    vm.clear()
}


sealed class UiEventFlow {
    data class Toast(val message: String) : UiEventFlow()
    object NavigateBack : UiEventFlow()
}

class EventViewModel {
    private val _events = MutableSharedFlow<UiEventFlow>(
        replay = 0,                 // no automatic replay for events
        extraBufferCapacity = 4,    // small buffer to reduce suspending producer
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val events: SharedFlow<UiEventFlow> = _events.asSharedFlow()

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    suspend fun sendToast(msg: String) {
        _events.emit(UiEventFlow.Toast(msg)) // suspends if buffer full
    }

    fun trySendToast(msg: String) {
        _events.tryEmit(UiEventFlow.Toast(msg)) // non-suspending; may drop
    }

    fun clear() {
        scope.cancel()
    }
}

// Console consumer
fun sharedFlowDemo() = runBlocking {
    val vm = EventViewModel()

    // UI collector
    val job = launch {
        vm.events.collect { ev ->
            when (ev) {
                is UiEventFlow.Toast -> println("[UI EVENT] Toast: ${ev.message}")
                UiEventFlow.NavigateBack -> println("[UI EVENT] Navigate back")
            }
        }
    }

    // Send events
    vm.trySendToast("hi")            // fire-and-forget
    vm.trySendToast("hello again")
    vm.trySendToast("this may drop if buffer full")

    // Send events
    vm.sendToast("hi")            // fire-and-forget
    vm.sendToast("hello again")
    vm.sendToast("this may drop if buffer full")

    delay(300)
//    job.cancel()
    vm.clear()
}

fun stopTimeoutMillisSuccess() {
    val start = System.currentTimeMillis()
    runBlocking {
        val scope = this
        val shared = flow {
            println("🔥 Success Upstream started at ms:${0}")
            emit(1)
            delay(10_000)
        }.shareIn(
            scope,
            SharingStarted.WhileSubscribed(stopTimeoutMillis = 500),
            replay = 1
        )

        val job1 = launch {
            shared.collect {
                println("A got $it at ${System.currentTimeMillis() - start} ms")
            }
        }
        delay(100)
        job1.cancel()

        delay(400)

        launch {
            shared.collect { println("B got $it at ${System.currentTimeMillis() - start} ms") }
        }

    }


}
fun stopTimeoutMillisFailure() {
    val start = System.currentTimeMillis()
    runBlocking {
        val scope = this
        var counter =1
        val shared = flow {
            println("🔥 Failure Upstream started at ms:${0}")
            emit(counter++)
            delay(100)
        }.shareIn(
            scope,
            SharingStarted.WhileSubscribed(stopTimeoutMillis = 500),
            replay = 1
        )

        val job1 = launch {
            shared.collect {
                println("A got $it at ${System.currentTimeMillis() - start} ms")
            }
        }
        delay(100)
        job1.cancel()

        delay(700)

        launch {
            shared.collect { println("B got $it at ${System.currentTimeMillis() - start} ms") }
        }
    }


}

