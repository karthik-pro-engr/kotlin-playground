import kotlinx.coroutines.*

fun main() = runBlocking {
    // 1) set up a CoroutineExceptionHandler
    val handler = CoroutineExceptionHandler { ctx, thr ->
        println("[handler] caught: ${thr::class.simpleName} - ${thr.message}")
    }

    // 2) supervisor scope (long-lived scope example)
    val scope = CoroutineScope(Dispatchers.Default + SupervisorJob() + handler)

    // 3) launch two children: one fails, one keeps working
    val j1 = scope.launch {
        println("[j1] started")
        repeat(5) {
            delay(200)
            println("[j1] running $it")
        }
        println("[j1] completed")
    }

    val j2 = scope.launch {
        println("[j2] started and will throw")
        delay(300)
        throw IllegalStateException("j2-broken")
    }

    val j3 = scope.async {
        println("[j3] started and will throw")
        delay(100)
        throw IllegalStateException("j3-broken")
    }

    // Wait a bit to observe behavior, then cancel the scope
    delay(1000)
    println("[main] cancelling scope")
    scope.cancel()
    // wait to let children finish cancellation
    j1.join()
    j2.join()
    j3.join()
    println("[main] done, before await")

    try {
        j3.await()
    } catch (e: Throwable) {
        println("[main] await caught: ${e::class.simpleName} - ${e.message}")
    }

    // DEFAULT (may or may not run immediately on same thread)
    val dDefault = async { println("default started"); delay(100); "r" }
    println("after created default")

    // LAZY
    val dLazy = async(start = CoroutineStart.LAZY) {
        println("lazy started")
        delay(100)
        "r-lazy"
    }
    println("after created lazy (not started yet)")
    dLazy.start() // or call await() to start
    println("started lazy via start()")

    // UNDISPATCHED
    val dUn = async(start = CoroutineStart.UNDISPATCHED) {
        println("undispatched: runs immediately on caller thread until first suspend")
        delay(50)
        "done-un"
    }
    println("after created und (body executed up to delay already)")

    // ATOMIC (difficult to observe with prints but ensures it cannot be cancelled before it starts)
    val dAtomic = async(start = CoroutineStart.ATOMIC) {
        println("atomic started (cannot be cancelled before first suspension)")
        delay(50)
        "atomic"
    }
    println("created all")
    // await to keep program alive
    println(dDefault.await())
    println(dLazy.await())
    println(dUn.await())
    println(dAtomic.await())

    println("InvokeOnCompletion")
    println("*******************")

    val job = launch {
        println("[child] working")
        delay(100)
        println("[child] throwing now")
        // throw RuntimeException("boom") // try both success and failure
    }

    job.invokeOnCompletion { cause ->
        println("[invokeOnCompletion] cause = ${cause?.let { it::class.simpleName + " - ${it.message}" } ?: "null (success)"}")
    }

    println("[main] before join()")
    try {
        job.join()
        println("[main] after join() (resumed)")
    } catch (e: Throwable) {
        println("[main] join threw: ${e::class.simpleName} - ${e.message}")
    }

}
