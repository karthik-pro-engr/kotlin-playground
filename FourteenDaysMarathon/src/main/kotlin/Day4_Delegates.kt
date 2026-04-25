// Day4_Delegation.kt
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


// --- Interface + Real implementation ---
interface Service {
    fun doWork(input: String): String
}

class RealService : Service {
    override fun doWork(input: String): String {
        // Simulate actual business logic
        return "Processed<$input>"
    }
}

// --- Class-level delegates (decorators) ---
class LoggingService(private val delegate: Service) : Service by delegate {
    override fun doWork(input: String): String {
        println("LOG: calling doWork(input=$input)")
        val result = delegate.doWork(input)
        println("LOG: result=$result")
        return result
    }
}

class AnalyticsService(private val delegate: Service) : Service by delegate {
    var callCount = 0
        private set

    override fun doWork(input: String): String {
        callCount++
        println("ANALYTICS: increment callCount -> $callCount (event: doWork)")
        return delegate.doWork(input)
    }
}

// --- Property delegate that tracks reads/writes ---
class ReadWriteTracker<T>(initial: T) : ReadWriteProperty<Any?, T> {
    private var value: T = initial
    var reads: Int = 0
        private set
    var writes: Int = 0
        private set

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        reads++
        println("TRACKER: get ${property.name} => $value (reads=$reads)")
        return value
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, newValue: T) {
        writes++
        println("TRACKER: set ${property.name} = $newValue (writes=$writes)")
        value = newValue
    }
}

// --- Example host class using a passed-in tracker so we can inspect it ---
class Config(tracker: ReadWriteTracker<String>) {
    var trackedProperty: String by tracker
}

interface A { fun f(): Int }
interface B { fun g(): Int }

class ImplA : A { override fun f() = 1 }
class ImplB : B { override fun g() = 1 }

class Comb(val a: A, val b: B) : A by a, B by b // Can't compile because that would be same function name


// --- Small demonstration main + simple assertions (unit-test style) ---
fun main() {
    println("=== Delegation demo ===")
    val real = RealService()
    val analytics = AnalyticsService(real)
    val logging = LoggingService(analytics)

    val out1 = logging.doWork("hello")
    println("Client received: $out1\n")

    val out2 = logging.doWork("world")
    println("Client received: $out2\n")

    // Check analytics count (should be 2)
    println("Analytics call count (should be 2): ${analytics.callCount}")

    println("\n=== Property delegate demo ===")
    val tracker = ReadWriteTracker("initial")
    val cfg = Config(tracker)

    // reads/writes
    val a = cfg.trackedProperty           // read
    cfg.trackedProperty = "first-change"  // write
    cfg.trackedProperty = "second-change" // write
    val b = cfg.trackedProperty           // read

    println("Tracker reads = ${tracker.reads} (should be 2)")
    println("Tracker writes = ${tracker.writes} (should be 2)")

}
