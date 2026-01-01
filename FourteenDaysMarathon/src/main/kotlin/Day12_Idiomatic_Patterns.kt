// ---- Model types ----

// File: root.kt
sealed class Root

// File: child.kt
class Child : Root()

fun whenHandle(r: Root?) {
    when (r) {
        is Child -> TODO()
        null -> TODO()
        is Summa -> {}
    }
}


// Item shown in UI
data class Item(val id: Int, val title: String)

// UI State: closed set of possibilities
sealed class UiState {
    object Idle : UiState()
    object Loading : UiState()
    data class Success(val items: List<Item>) : UiState()
    object Empty : UiState()           // when no items available
    data class Error(val message: String) : UiState()
}

// Events that drive the state machine
sealed class UiEvent {
    object Load : UiEvent()                    // start loading
    object Refresh : UiEvent()                 // user requested refresh
    data class ItemClicked(val itemId: Int) : UiEvent()
    data class LoadResult(val result: Result<List<Item>>) : UiEvent()
}

// ---- State machine ----
class UiStateMachine(
    initial: UiState = UiState.Idle
) {
    var state: UiState = initial
        private set

    // pure function: compute next state, then assign.
    fun handle(event: UiEvent) {
        state = reduce(state, event)
    }

    // Reducer returns new state based on current state + event
    private fun reduce(current: UiState, event: UiEvent): UiState {
        return when (event) {
            is UiEvent.Load -> UiState.Loading
            is UiEvent.Refresh -> UiState.Loading
            is UiEvent.ItemClicked -> when (current) {
                is UiState.Success -> {
                    // no state change, but we could trigger navigation as a side effect in caller
                    current
                }

                else -> current
            }

            is UiEvent.LoadResult -> when (val r = event.result) {
                is Result.Success -> {
                    val items = r.value
                    if (items.isEmpty()) UiState.Empty else UiState.Success(items)
                }

                is Result.Failure -> UiState.Error(r.exception.message ?: "Unknown error")
            }
        }
    }
}

// ---- Small Result wrapper for example (replace with your own or kotlin.Result) ----
sealed class Result<out T> {
    data class Success<T>(val value: T) : Result<T>()
    data class Failure(val exception: Throwable) : Result<Nothing>()
}

// ---- Example usage ----
fun main() {
    /* val sm = UiStateMachine()

     println(sm.state) // Idle

     sm.handle(UiEvent.Load)
     println(sm.state) // Loading

     // Simulate load success
     val loadedItems = listOf(Item(1, "A"), Item(2, "B"))
     sm.handle(UiEvent.LoadResult(Result.Success(loadedItems)))
     println(sm.state) // Success(items=[Item(1,...), Item(2,...)])

     // Click item - state remains same (events can produce side-effects outside reducer)
     sm.handle(UiEvent.ItemClicked(1))
     println(sm.state) // Success(...)

     // Simulate error on refresh
     sm.handle(UiEvent.Refresh)
     sm.handle(UiEvent.LoadResult(Result.Failure(RuntimeException("network"))))
     println(sm.state) // Error(message="network")
     */
    print()
    handleDataList()
}

sealed class S {
    object A : S()
    data class B(val x: Int) : S()
}

fun handle(s: S?) {
    when (s) {
        is S.A -> println("A")
        is S.B -> println("B ${s.x}")
        null -> {} // or else-> {}
    }
}

open class BaseSealed(val id: Int)

data class D(val idD: Int) : BaseSealed(idD)

val a = D(1)
val d = D(1)
val b = BaseSealed(1)
val bb = BaseSealed(1)
fun print() {
    println(a==d)
    println(a===d)
    println(a == b)
    println(b == bb)
    println(b === bb)
}


sealed class St {
    data class OK(val v: Int) : St()
    object Err : St()
}

var state: St = St.Err

fun example() {
    if (state is St.OK) {
        // (1)
        println((state as St.OK).v)
    }
}
data class DataMutableList(val items: MutableList<String>)
data class DataList(val items: List<String>)

fun handleDataList() {
    val s  = DataMutableList(mutableListOf("a"))
    val s2 = s.copy()            // shallow copy: s2.items points to the same MutableList
    s.items.add("b")             // mutates the single shared list
    println("s.items = ${s.items}")   // [a, b]
    println("s2.items = ${s2.items}") // [a, b]  <-- visible change
    println("s2.items.size = ${s2.items.size}") // 2
    s2.items.add("c")
    println("After adding C")
    println("s.items = ${s.items}")   // [a, b]
    println("s2.items = ${s2.items}") // [a, b]  <-- visible change
    println("s2.items.size = ${s2.items.size}") // 2

    val originalMutable = mutableListOf("c")
    val dataList = DataList(originalMutable.toList())
    originalMutable.add("d")

    val copy = dataList.copy(items = dataList.items.toList())
    println( "copy.items = ${copy.items}")




}




