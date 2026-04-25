package com

import kotlinx.coroutines.*
import java.lang.Exception
import java.lang.IllegalStateException
import java.util.concurrent.Executors
import kotlin.coroutines.cancellation.CancellationException

fun main() {
    /* blockingExample()
     println("Suspend")
     println("************")
     suspendExample()

     learnRunBlocking()
     learnIOBlockingGrowth()
    asyncVsLaunchException()
    cancellableWithContext()

  suspension()
    blocking()

    defaultStart()
    lazyStart()


    mainImmediate()
    startAwait()
    asyncWithContext()
    sequential()
    unConfined()
    unConfinedWithIO()
       */
//    nonCancellableWithContext()

//    asyncAndLaunchWithContext()
//    learnIOBlockingGrowth()
//    asyncVsLaunchException()
//    threadSleepWithDispatchersIO()
//    noSuspension()
    unDispatchedStart()
//    unConfinedWithIOUpdated()
}

fun learnRunBlocking() {
    runBlocking {
        val scope = this
        scope.launch {
            launch {
                delay(200)
                println("Inner finished")
            }
            delay(100)
            println("Outer finished")
        }
        delay(120)
        println("runBlocking end")
    }
}

fun learnIOBlockingGrowth() {
    runBlocking {

        val jobList = (1..6).map { i ->
            launch(Dispatchers.IO) {
                println("Start $i on ${Thread.currentThread().name}")
                Thread.sleep(1000) // blocking
                println("End $i on ${Thread.currentThread().name}")
            }
        }
        jobList.forEachIndexed { index, job ->
            if (index == 2) job.cancel()
            else
                job.join()
        }

    }
}

fun asyncVsLaunchException() {
    runBlocking {
        /*val a = launch {
            throw RuntimeException("boom-launch")
        }*/
        val d = async {
            throw RuntimeException("boom-async")
        }

        // Wait a little
//        delay(100)
//        println("After delay")

        try {
            d.await()
        } catch (e: java.lang.RuntimeException) {
            println("caught await: ${e.message}")
        }

//        delay(100)
        println("End of runBlocking")

    }
}

fun nonCancellableWithContext() {
    println("NonCancellable")

    runBlocking {
        val job = launch {
            try {
                repeat(10) {
                    delay(200)
                    println("working $it")
                }
            } finally {
                println("in finally, trying to cleanup (suspend)")
                withContext(NonCancellable) {
                    delay(300) // simulate cleanup
                    notASuspendFunc()
                    println("cleanup done")
                }
            }
        }
        delay(700)
        job.cancelAndJoin()
        println("after cancelAndJoin")
        println("******************")
    }
}

fun notASuspendFunc() {
    println("notASuspendFunc")
}

fun cancellableWithContext() {
    println("Cancellable")
    println("******************")
    runBlocking {
        val job = launch {
            try {
                repeat(10) {
                    delay(200)
                    println("working $it")
                }
            } finally {
                println("in finally, trying to cleanup (suspend)")
                delay(300) // simulate cleanup
                println("cleanup done")
            }
        }
        delay(700)
        job.cancelAndJoin()
        println("after cancelAndJoin")
    }
}

fun blockingExample() {
    runBlocking {
        val dispatcher = Executors.newFixedThreadPool(2).asCoroutineDispatcher()

        val start = System.currentTimeMillis()
        val jobs = (1..6).map { id ->
            launch(dispatcher) {
                val diff = System.currentTimeMillis() - start
                println("START #$id at ${diff}ms on ${Thread.currentThread().name}")
                Thread.sleep(1000)

                val t2 = System.currentTimeMillis() - start
                println("FINISH  #$id at ${t2}ms on ${Thread.currentThread().name}")

            }
        }

        jobs.forEach { it.join() }

        println("All done in ${System.currentTimeMillis() - start}ms")
        dispatcher.close() // cleanup

    }
}

fun suspendExample() {
    runBlocking {
        val dispatcher = Executors.newFixedThreadPool(2).asCoroutineDispatcher()

        val start = System.currentTimeMillis()
        val jobs = (1..6).map { id ->
            launch(dispatcher) {
                val diff = System.currentTimeMillis() - start
                println("START #$id at ${diff}ms on ${Thread.currentThread().name}")
                delay(1000)

                val t2 = System.currentTimeMillis() - start
                println("FINISH  #$id at ${t2}ms on ${Thread.currentThread().name}")

            }
        }

        jobs.forEach { it.join() }

        println("All done in ${System.currentTimeMillis() - start}ms")
        dispatcher.close() // cleanup

    }
}

fun simpleLaunchAndAsync() {
    runBlocking {
        println("Main start - thread: ${Thread.currentThread().name}")

        val deferred = async {
            println("Async started on ${Thread.currentThread().name}")

            withContext(Dispatchers.IO) {
                Thread.sleep(1000)
                println("After thread sleep")
            }
            "result"

        }


        println("Awaiting result: ${deferred.await()}")

        val job = launch {
            println("Launch started on ${Thread.currentThread().name}")

            delay(2000)

            println("Launch finished")
        }

        job.join()
        println("Main end")
    }
}

@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
fun suspension() {
    runBlocking {
        val disp = newSingleThreadContext("S")

        val a = launch(disp) {
            log("A", "start")
            delay(700)
            log("A", "end")
        }
        val b = launch(disp) {
            log("B", "start")
            delay(300)
            log("B", "end")
        }
        a.join(); b.join()
        disp.close()
    }

}

@OptIn(ExperimentalCoroutinesApi::class)
fun blocking() {
    runBlocking {
        val disp = newSingleThreadContext("S")

        val a = launch(disp) {
            log("A", "start")
            Thread.sleep(500)
            log("A", "end")
        }

        val async = async(disp) {
            log("Async", "start")
            Thread.sleep(500)
            log("Async", "end")
        }

        val b = launch(disp) {
            log("B", "start")
            delay(200)
            log("B", "end")
        }
        async.await()
        a.join(); b.join()
        disp.close()
    }
}

fun log(tag: String, msg: String) =
    println("${System.currentTimeMillis()} | $tag | ${Thread.currentThread().name} | $msg")

fun defaultStart() {
    runBlocking {
        launch {
            println("DEFAULT Start: ${Thread.currentThread().name}")
        }
        println("runBlocking continues")
    }
}

fun lazyStart() {
    runBlocking {
        val job = launch(start = CoroutineStart.LAZY) {
            println("launch LAZY Start: ${Thread.currentThread().name}")
        }
        println("Before join")
        job.join()
        println("After join")

        val async = async(start = CoroutineStart.LAZY) {
            println("async LAZY Start: ${Thread.currentThread().name}")
        }
        println("Before await")
        async.await()
        println("After await")

    }
}

fun unDispatchedStart() {
    runBlocking {
        launch(Dispatchers.Default, start = CoroutineStart.UNDISPATCHED) {
            println("Start on: ${Thread.currentThread().name}")
            delay(100)
            println("Resume on: ${Thread.currentThread().name}")
        }
    }
}

fun mainImmediate() {
    runBlocking {
        println("main? ${Thread.currentThread().name}")
        withContext(Dispatchers.Main.immediate) { // in desktop REPL this won't exist; run this on Android
            println("before delay on ${Thread.currentThread().name}")
            delay(10)
            println("after delay on ${Thread.currentThread().name}")
        }
    }
}

fun startAwait() {

    val d = GlobalScope.async(start = CoroutineStart.LAZY) {
        println("work")
        5
    }
    runBlocking {
        val j = launch { delay(100); d.start() }
        d.await()
    }

}

fun asyncAndLaunchWithContext() {
    runBlocking {
        var start = System.currentTimeMillis()
        println("main start on ${Thread.currentThread().name}")

        /*val a = async {
            println("async body START on ${Thread.currentThread().name}")
            var result: String? = null
            try {
                withContext(Dispatchers.IO) {
                    println("INSIDE withContext on ${Thread.currentThread().name} -> about to block")
                    blockingWork(500) // blocks IO thread
                    println("INSIDE withContext after blocking on ${Thread.currentThread().name}")
                }
                println("async body END on ${Thread.currentThread().name}")
            } catch (exception: CancellationException) {
                println("a's exception ${exception.message}")
            } finally {
                println("Finally")
                result = withContext(NonCancellable) {
                    blockingWork(500);
                    println("A clean up inside non cancellable block")
                    return@withContext "Cancelled"
                }
            }

            result

        }
        val b = async {
            val withContext = withContext(Dispatchers.IO) {
                blockingWork(500); try {
                throw IllegalStateException("Error")
            } catch (x: Exception) {
                println("Exception -> ${x.message}")
            } finally {
            }
                return@withContext "Error"
            }

            withContext
        }


        try {
            println("Got a await-> ${a.await()} at ${System.currentTimeMillis() - start}ms")
        } catch (x: Exception) {
            println("await catch-> ${x.message}")
        }
        println("Got a cancel-> ${a.cancel()} at ${System.currentTimeMillis() - start}ms")


        println("Got b-> ${b.await()} at ${System.currentTimeMillis() - start}ms")*/

        withContext(Dispatchers.Default) {
            val aa = async { delay(500) }
            val ba = async { delay(500) }
            awaitAll(aa, ba)
            println("Got awaitAll->  ${awaitAll(aa, ba)} at ${System.currentTimeMillis() - start}ms")
        }

        val ass = async { delay(500) }
        delay(500)
        println("After ass at ${System.currentTimeMillis() - start}ms")


        /* start = System.currentTimeMillis()

         val aLaunch = launch { withContext(Dispatchers.IO) { blockingWork(500); println("A Launch") } }
         val bLaunch = launch { withContext(Dispatchers.IO) { blockingWork(500); println("B Launch") } }

         println("Got ${aLaunch.join()} and ${bLaunch.join()} at ${System.currentTimeMillis() - start}ms")*/
    }
}

fun blockingWork(ms: Long) {
    Thread.sleep(ms)
}

fun sequential() {
    runBlocking {
        val start = System.currentTimeMillis()

        // sequential work using withContext twice
        withContext(Dispatchers.Default) {
            delay(500)  // simulate work
            println("A done at ${System.currentTimeMillis() - start}ms on ${Thread.currentThread().name}")
        }
        withContext(Dispatchers.Default) {
            delay(500)
            println("B done at ${System.currentTimeMillis() - start}ms on ${Thread.currentThread().name}")
        }

        println("Total ${System.currentTimeMillis() - start}ms (sequential)")
    }
}

fun unConfined() {
    runBlocking {
        println("runBlocking thread: ${Thread.currentThread().name}")

        launch(Dispatchers.Unconfined) {
            println("Unconfined start: ${Thread.currentThread().name}")
            delay(100)
            println("Unconfined after delay: ${Thread.currentThread().name}")
        }.join()
    }
}

fun unConfinedWithIO() {
    runBlocking {
        println("runBlocking thread: ${Thread.currentThread().name}")

        launch(Dispatchers.Unconfined) {
            println("Unconfined start: ${Thread.currentThread().name}")
            withContext(Dispatchers.IO) {
                delay(100)
                println("Unconfined after delay: ${Thread.currentThread().name}")
            }
        }.join()
    }
}

fun threadSleepWithDispatchersIO() {
    runBlocking {
        val d = async(Dispatchers.IO) {
            try {
                println("blocking start on ${Thread.currentThread().name} at ${System.currentTimeMillis()}")
                Thread.sleep(1000) // blocks the IO thread
                println("blocking finished on ${Thread.currentThread().name} at ${System.currentTimeMillis()}")
            } finally {
                println("finally executed on ${Thread.currentThread().name} at ${System.currentTimeMillis()}")
            }
        }

        delay(100) // let coroutine enter sleep
        println("cancelling at ${System.currentTimeMillis()}")
        d.cancel() // requests cancellation
        try {
            d.await()
        } catch (e: Exception) {
            println("await threw: ${e::class.simpleName} ${e.message} at ${System.currentTimeMillis()}")
        }
    }
}

fun noSuspension() {
    runBlocking {
        val async = async {
            repeat(100000) {
                if (isActive) {

                    println("$it")
                }
                if (it == 200) {
                    cancel()
//                    delay(100)
                }
            }
        }
        async.start()
        try {
            async.await()
        } catch (e: Exception) {
            println("****************************************************${e.message}")
        }
    }
}

fun unConfinedWithIOUpdated() {
    runBlocking {
        launch(Dispatchers.Unconfined) {
            println("Start (Unconfined) on ${Thread.currentThread().name}")
            // withContext is a suspension that switches to Dispatcher.IO for block execution
            withContext(Dispatchers.IO) {
                println("Inside withContext(IO) on ${Thread.currentThread().name}")
            }
            // after withContext finishes, continuation resumes according to context/resumer
            println("After withContext on ${Thread.currentThread().name}")
        }.join()
    }
}