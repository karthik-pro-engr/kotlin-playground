import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import java.time.Duration
import java.time.Instant
import java.time.Instant.now


fun main() = runBlocking {
//    replayWithZero()
//    demoReplyWithOne()
    demoSuspendBehavior()
    demoSuspendBehaviourWithConsumerReady()
}

suspend fun replayWithZero(): Unit = coroutineScope {
    println("Replay with Zero")
    val events = MutableSharedFlow<String>(replay = 0)
    launch {
        listOf("A", "B", "C", "D", "E").forEach { a ->
            println("producer: emit $a at ${now()}")
            events.emit(a)
            println("producer: emitted $a at ${now()}")
            delay(50)
        }
    }
    delay(170)
    launch {
        println("collector: subscribing late at ${now()}")
        events.collect {
            println("collector received: $it at ${now()}")
        }
    }

    delay(300)
    coroutineContext.cancelChildren() // stop collectors
    println("**********************************************")

}

suspend fun demoReplyWithOne() = coroutineScope {
    println("Replay with One")
    val events = MutableSharedFlow<String>(1)
    launch {
        listOf("A", "B", "C", "D").forEach { a ->
            println("producer: emit $a at ${now()}")
            events.emit(a)
            println("producer: emitted $a at ${now()}")
            delay(50)
        }
    }

    delay(500)

    launch {
        println("collector: subscribing late at ${now()}")
        events.collect {
            println("collector received: $it at ${now()}")
        }
    }

    delay(300)
    coroutineContext.cancelChildren() // stop collectors

    println("*************************")
}

suspend fun demoSuspendBehavior() = coroutineScope {
    println("[SUSPEND behavior] backpressure demo at ${now()}")

    // replay=0, extraBufferCapacity=0, onBufferOverflow = SUSPEND (default)
    val flow = MutableSharedFlow<Int>(replay = 0, extraBufferCapacity = 0, onBufferOverflow = BufferOverflow.SUSPEND)

    // Slow consumer: consumes with 300ms delay
    launch {
        delay(120) // start a little later so producer can try to fill
        println("slow consumer: subscribing at ${now()}")
        flow.collect { value ->
            println("slow consumer: got $value at ${now()} (processing...)")
            delay(300) // slow processing -> causes backpressure
        }
    }

    // Fast producer: attempts to emit 1..5 very quickly using emit (suspending)
    launch {
        for (i in 1..5) {
            val tBefore = now()
            println("producer: trying to emit $i at $tBefore")
            flow.emit(i) // will suspend when buffer full until consumer frees space
            val tAfter = now()
            val delta = Duration.between(tBefore, tAfter).toMillis()
            println("producer: finished emit $i at $tAfter (delta=$delta ms)")
            delay(20) // small pause between emits
        }
    }

    delay(2000)
    coroutineContext.cancelChildren()

    println("*************************************************")
}

fun nowIso() = now().toString()
suspend fun demoSuspendBehaviourWithConsumerReady() = coroutineScope {
    println("[SUSPEND demo] start at ${nowIso()}\n")

    // 0-capacity shared flow (replay=0, extraBufferCapacity=0) with SUSPEND on overflow
    val flow = MutableSharedFlow<Int>(
        replay = 0,
        extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.SUSPEND
    )

    // Use a handshake so producer only starts after collector is ready
    val collectorReady = CompletableDeferred<Unit>()

    // Consumer (slow): subscribes first and intentionally processes slowly
    val consumer = launch {
        println("consumer: subscribing at ${nowIso()}")
        collectorReady.complete(Unit) // tell producer it's ok to start emitting
        flow.collect { value ->
            println("consumer: got $value at ${nowIso()} (processing 300ms)")
            delay(300) // slow processing -> causes emitter backpressure for subsequent emits
            println("consumer: finished processing $value at ${nowIso()}")
        }
    }

    // Producer: waits for collectorReady, then emits quickly (20ms between attempts)
    val producer = launch {
        collectorReady.await() // ensure consumer is already subscribed
        delay(10) // tiny extra gap to make the timeline clearer
        for (i in 1..5) {
            val tBefore = now()
            println("producer: trying to emit $i at ${tBefore}")
            flow.emit(i) // <-- SUSPEND may occur here if buffer full & consumer busy
            val tAfter = now()
            val delta = Duration.between(tBefore, tAfter).toMillis()
            println("producer: finished emit $i at $tAfter (delta=${delta} ms)")
            delay(20)
        }
    }

    // Let the demo run for a while then cancel
    delay(2000)
    producer.cancelAndJoin()
    consumer.cancelAndJoin()

    println("\n[SUSPEND demo] done at ${nowIso()}")
}