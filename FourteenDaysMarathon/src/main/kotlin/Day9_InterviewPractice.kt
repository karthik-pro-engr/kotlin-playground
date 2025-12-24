import kotlinx.coroutines.*

fun main() = runBlocking {
    val handler = CoroutineExceptionHandler { _, e ->
        println("[handler] ${e::class.simpleName} - ${e.message}")
    }

    // long-lived scope with SupervisorJob + handler
    val scope = CoroutineScope(SupervisorJob() + handler)

    // Orchestrator launched in the supervisor scope
    val orchestrator = scope.async {
        println("orchestrator: start")
        try {
            supervisorScope {
                val a = async {
                    println("a: start")
                    delay(100)
                    println("a: about to throw")
                    throw RuntimeException("a-failed")
                }

                val b = async {
                    println("b: start")
                    delay(300)
                    println("b: done")
                    "b-result"
                }

                println("orchestrator: before awaitAll(a,b)")
                val results = awaitAll(a, b)   // <-- important interaction point
                println("orchestrator: awaitAll returned $results")
            }
        } catch (e: Throwable) {
            println("orchestrator: caught ${e::class.simpleName} - ${e.message}")
        }
        println("orchestrator: end")
    }

    try {
        val orchestorResult =
            orchestrator.await()
        println("orchestrator await returned $orchestorResult")
    } catch (e: Throwable) {
        println(" orchestrator : caught ${e::class.simpleName} - ${e.message}")
    }
    // An async started in the same top-level scope that will fail and is never awaited
    val d1 = scope.async {
        println("d1: start")
        delay(250)
        println("d1: about to throw")
        throw IllegalStateException("d1-broke")
    }

    // A simple launch that throws (uncaught) — should go to handler
    val l3 = scope.launch {
        delay(150)
        println("l3: about to throw")
        throw IllegalArgumentException("l3-broke")
    }

    // Let things run for a while
    delay(1000)
    println("main: done")
}
