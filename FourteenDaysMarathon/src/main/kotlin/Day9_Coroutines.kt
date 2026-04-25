import kotlinx.coroutines.*
import java.time.LocalTime
import java.time.format.DateTimeFormatter

// --------------------- Timestamped logger ---------------------
class DemoLogger(private val demoName: String) {
    private val demoStartMillis = System.currentTimeMillis()
    private val timeFmt = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")

    private fun nowTs(): String = LocalTime.now().format(timeFmt)
    private fun elapsedMs(): Long = System.currentTimeMillis() - demoStartMillis

    fun log(actor: String, msg: String) {
        println("[${demoName}] [${nowTs()}] [+${elapsedMs()}ms] [${Thread.currentThread().name}] ${actor.padEnd(10)} ┆ $msg")
    }
}

// --------------------- Demo runner ---------------------
fun main() = runBlocking {
    println("=== Day 9: Coroutines basics & failure modes (timestamped) ===\n")

      println("Demo A: coroutineScope + launch (one child fails) --- expect siblings cancelled")
      demoCoroutineScopeLaunch()

    /*
      println("\nDemo B: supervisorScope + launch (one child fails) --- siblings survive")
      demoSupervisorScopeLaunch()
 */

//    println("\nDemo C: async + await differences inside coroutineScope vs supervisorScope")
//    demoAsyncAwaitBehavior()

   /* println("\nDemo D: withTimeout -> cancellation propagation")
    demoWithTimeout()*/
}

// --------------------- Demos (refactored with timestamps) ---------------------
suspend fun demoCoroutineScopeLaunch() {
    val L = DemoLogger("DemoA-coroutineScope")
    try {
        L.log("parent", "entering coroutineScope")
        coroutineScope {
            // Child 1: long-running
            val j1 = launch {
                L.log("child1", "started")
                try {
                    delay(1000)
                    L.log("child1", "completed normally")
                } catch (e: CancellationException) {
                    L.log("child1", "cancelled (caught CancellationException): ${e.message ?: "<no msg>"}")
                    throw e
                }
            }

            // Child 2: fails quickly (we log exact throw time then rethrow)
            val j2 = launch {
                L.log("child2", "started and will fail soon")
                try {
                    delay(200)
                    val ex = RuntimeException("child2 failed")
                    L.log("child2", "about to throw RuntimeException now")
                    throw ex
                } catch (e: Throwable) {
                    // log then rethrow to preserve original structured-concurrency behavior
                    L.log("child2", "threw exception: ${e::class.simpleName} - ${e.message}")
                    throw e
                }
            }

            // Child 3: also long-running
            val j3 = launch {
                L.log("child3", "started")
                try {
                    delay(1200)
                    L.log("child3", "completed normally")
                } catch (e: CancellationException) {
                    L.log("child3", "cancelled (caught CancellationException): ${e.message ?: "<no msg>"}")
                    throw e
                }
            }

            // optional: show join attempt so you see when parent waits/returns
            try {
                L.log("parent", "joining children (this will complete when scope completes or is cancelled)")
                j2.join()
//                joinAll(j1, j2, j3)
                L.log("parent", "joinAll returned (scope completed normally)")
            } catch (e: Throwable) {
                L.log("parent", "joinAll threw: ${e::class.simpleName} - ${e.message}")
                throw e
            }
        }
    } catch (e: Throwable) {
        L.log("parent", "caught at top-level: ${e::class.simpleName} - ${e.message}")
    } finally {
        L.log("parent", "exiting demoCoroutineScopeLaunch")
    }
}

suspend fun demoSupervisorScopeLaunch() {
    val L = DemoLogger("DemoB-supervisor")
    try {
        L.log("parent", "entering supervisorScope")
        supervisorScope {
            // Child 1: long-running
            val j1 = launch {
                L.log("s-child1", "started")
                try {
                    delay(1000)
                    L.log("s-child1", "completed normally")
                } catch (e: CancellationException) {
                    L.log("s-child1", "cancelled: ${e.message ?: "<no msg>"}")
                    throw e
                }
            }

            // Child 2: fails quickly
            val j2 = launch {
                L.log("s-child2", "started and will fail soon")
                try {
                    delay(200)
                    val ex = RuntimeException("s-child2 failed")
                    L.log("s-child2", "about to throw RuntimeException now")
                    throw ex
                } catch (e: Throwable) {
                    L.log("s-child2", "threw exception: ${e::class.simpleName} - ${e.message}")
                    throw e
                }
            }

            // Child 3: should continue despite s-child2 failure
            val j3 = launch {
                L.log("s-child3", "started")
                try {
                    delay(1200)
                    L.log("s-child3", "completed normally")
                } catch (e: CancellationException) {
                    L.log("s-child3", "cancelled: ${e.message ?: "<no msg>"}")
                    throw e
                }
            }

            // joinAll - exceptions from a supervised child won't cancel siblings automatically
            try {
                L.log("supervisor", "joining children (supervisor doesn't cancel siblings on child failure)")
                joinAll(j1, j2, j3)
                L.log("supervisor", "joinAll returned")
            } catch (e: Throwable) {
                L.log("supervisor", "joinAll caught throwable: ${e::class.simpleName} - ${e.message}")
            }
        }
    } catch (e: Throwable) {
        L.log("parent", "caught at top-level: ${e::class.simpleName} - ${e.message}")
    } finally {
        L.log("parent", "exiting demoSupervisorScopeLaunch")
    }
}

suspend fun demoAsyncAwaitBehavior() {
    val L = DemoLogger("DemoC-asyncAwait")
    // 1) coroutineScope with async children
    try {
        L.log("parent", "entering coroutineScope (async section)")
        coroutineScope {

            // 1) default async (may not start body before next parent logs)
            val sd1 = async { L.log("sd1", "started (default)"); delay(1000); "r1" }
            L.log("parent", "after creating sd1 - may run before sd1 body runs")

            // 2) force immediate start up to first suspension
            val sdImmediate = async(start = CoroutineStart.UNDISPATCHED) {
                L.log("sdImmediate", "started (UNDISPATCHED)")
                delay(1000)
                "r2"
            }

            val d1 = async {
                L.log("d1", "started")
                try {
                    delay(1000)
                    L.log("d1", "completed normally and will return result1")
                    "result1"
                } catch (e: CancellationException) {
                    L.log("d1", "cancelled during work")
                    throw e
                }
            }

            val d2 = async {
                L.log("d2", "started and will fail soon")
                try {
                    delay(200)
                    val ex = RuntimeException("d2 failed")
                    L.log("d2", "about to throw RuntimeException now")
                    throw ex
                } catch (e: Throwable) {
                    L.log("d2", "threw exception: ${e::class.simpleName} - ${e.message}")
                    throw e
                }
            }

            // If one async fails (and awaited), exception bubbles out and cancels the scope
            try {
                L.log("parent", "awaiting d2 (this will rethrow the async exception)")
                val r2 = d2.join() // will throw
                L.log("parent", "d2.await returned: $r2 (unexpected)")
                val r1 = d1.await()
                L.log("parent", "d1.await returned: $r1")
            } catch (e: Throwable) {
                L.log("parent", "caught while awaiting in coroutineScope: ${e::class.simpleName} - ${e.message}")
            }
        }
    } catch (e: Throwable) {
        L.log("outer", "caught outer coroutineScope exception: ${e::class.simpleName} - ${e.message}")
    } finally {
        L.log("parent", "leaving coroutineScope (async section)")
    }

    // 2) supervisorScope with async children
/*
    try {
        L.log("parent", "entering supervisorScope (async section)")
        supervisorScope {
            val sd1 = async {
                L.log("sd1", "started")
                try {
                    delay(1000)
                    L.log("sd1", "completed normally and will return s-result1")
                    "s-result1"
                } catch (e: CancellationException) {
                    L.log("sd1", "cancelled")
                    throw e
                }
            }

            val sd2 = async {
                L.log("sd2", "started and will fail soon")
                try {
                    delay(200)
                    val ex = RuntimeException("sd2 failed")
                    L.log("sd2", "about to throw RuntimeException now")
                    throw ex
                } catch (e: Throwable) {
                    L.log("sd2", "threw exception: ${e::class.simpleName} - ${e.message}")
                    throw e
                }
            }

            // awaiting sd2 will throw, but sd1 is not cancelled by sd2's failure because of supervisor
           */
/* try {
                L.log("super-parent", "awaiting sd2 (will rethrow)")
                val r2 = sd2.await()
                L.log("super-parent", "sd2.await returned (unexpected): $r2")
            } catch (e: Throwable) {
                L.log("super-parent", "caught sd2 failure on await: ${e::class.simpleName} - ${e.message}")
            }*//*


            // now await sd1
            try {
                L.log("super-parent", "awaiting sd1 (should complete normally despite sd2 failure)")
                val r1 = sd1.await()
                L.log("super-parent", "sd1.await returned: $r1")
            } catch (e: Throwable) {
                L.log("super-parent", "sd1.await threw: ${e::class.simpleName} - ${e.message}")
            }
        }
    } catch (e: Throwable) {
        L.log("outer", "caught outer supervisorScope exception: ${e::class.simpleName} - ${e.message}")
    } finally {
        L.log("parent", "exiting supervisorScope (async section)")
    }
*/
}

suspend fun demoWithTimeout() {
    val L = DemoLogger("DemoD-timeout")
    try {
        L.log("parent", "entering withTimeout(500)")
        try {
            withTimeout(500) {
                coroutineScope() {
                    val j1 = launch {
                        L.log("t-child1", "started - will run longer than timeout")
                        try {
                            delay(1000)
                            L.log("t-child1", "completed normally (unexpected, timeout should have fired)")
                        } catch (e: CancellationException) {
                            L.log("t-child1", "cancelled due to timeout: ${e::class.simpleName}")
                            throw e
                        }
                    }

                    val j2 = launch {
                        L.log("t-child2", "started - short")
                        try {
                            delay(200)
                            L.log("t-child2", "completed normally")
                        } catch (e: CancellationException) {
                            L.log("t-child2", "cancelled: ${e::class.simpleName}")
                            throw e
                        }
                    }

                    L.log("parent", "delaying inside scoped block to trigger timeout (delay 1000)")
                    delay(1000) // this ensures withTimeout will fire before children finish
                }
            }
        } catch (e: TimeoutCancellationException) {
            L.log("withTimeout", "timed out: ${e::class.simpleName} - ${e.message ?: "<no msg>"}")
        }
    } catch (e: Throwable) {
        L.log("parent", "caught unexpected: ${e::class.simpleName} - ${e.message}")
    } finally {
        L.log("parent", "exiting demoWithTimeout")
    }
}
