import java.lang.Exception
import kotlin.math.sqrt
import kotlin.properties.Delegates
import kotlin.reflect.KProperty
import kotlin.system.measureNanoTime

class MyClass {
    val b: Int = 10

    companion object {
        var DEFAULT = 0

        init {
            println("Companion init ${DEFAULT++}")
        }   // runs on first companion access

        fun createDefault() = MyClass()
    }


    val a = 10

    init {
        println("Instance init a=$a")
    }
}

class C {
    val x: Int

    constructor(a: Int) {
        x = a
    } // ok
}

class Person(var name: String) {
    var age: Int = -1
        get() =
            if (field == -1) {
                field
            } else field
        set(value) {
            field = if (value < 0) 0 else value
        }
}

class ValidatedStringDelegate(val minLen: Int) {
    private var stored: String = ""
    operator fun getValue(thisRef: Any?, prop: KProperty<*>): String = stored

    operator fun setValue(thisRef: Any?, prop: KProperty<*>, value: String) {
        stored = if (value.length < minLen) {
            println("Assigned too short -> storing empty")
            ""
        } else {
            value
        }
    }
}

class User {
    var name: String by ValidatedStringDelegate(3)
}

class WithJvmField {
    @JvmField
    val z = 5
}

class CircleComputed(private val r: Double) {
    val area: Double
        get() {
            var s = 0.0
            for (i in 1..10) {
                s += sqrt(r * i)
            }
            return s
        }
}

class CircleCached(private val r: Double) {
    val area: Double = run {
        var s = 0.0
        for (i in 1..10) {
            s += sqrt(r * i)
        }
        s
    }
}

class CircleLazy(private val r: Double) {
    private var cicumbrance by Delegates.notNull<Double>()
    fun accessLateInit(i: Int) {
        try {
            println("$i time->")
            println(cicumbrance)
        } catch (x: Exception) {
            println(x.message)
            cicumbrance = 2 * 3.14 * r
        }
    }

    val area: Double by lazy {
        var s = 0.0
        for (i in 1..10) {
            s += sqrt(r * i)
        }
        s
    }
}

// helper to measure repeated reads
fun measureAccess(label: String, supplier: () -> Double, warmupIters: Int = 5_000, measuredIters: Int = 1_000_00) {
    // warmup
    repeat(warmupIters) { supplier() }
    // measured
    val t = measureNanoTime {
        var sum = 0.0
        repeat(measuredIters) { sum += supplier() }
        // use sum to prevent dead code elimination
        if (sum == Double.MIN_VALUE) println("impossible")
    }
    println("%-20s %10d ns for %d reads (avg %d ns/read)".format(label, t, measuredIters, t / measuredIters))
}

fun main() {
    println("Before any companion access")
    // no companion init yet
    val m = MyClass.createDefault() // this accesses companion -> companion init runs before createDefault
    println(m.b)
    val x = MyClass.DEFAULT
    println(x)

    println("*******************************")
    println()
    println("Custom getter and setter for backing field")
    println("-----------------------------------------------")

    val person = Person("Karthik")
    println(person.age)
    person.age = 25
    println(person.age)
    person.age = -5
    println(person.age)

    println("*******************************")
    println()

    println("Delegate Workout")
    println("---------------------")

    val user = User()
    user.name = "Ap"
    user.name = "APJ"
    println(user.name)

    println("*******************************")
    println()

    println("JvmField Example")
    println("---------------------")
    val jvmField = WithJvmField()
    println("Accessing field with JvmField ${jvmField.z}")
    println("*******************************")
    println()
    println("Normal kotlin field Example")
    println("---------------------")
    val apjPerson = Person("APJ")
    apjPerson.name

    println("*******************************")
    println()

    println("Warmup JVM + JIT; results are illustrative; for production use JMH")
    println("--------------------------------------------------------------------------")

    // create objects
    val cComputed = CircleComputed(2.0)
    val cCached = CircleCached(2.0)
    val cLazy = CircleLazy(2.0)
    cLazy.accessLateInit(1)
    cLazy.accessLateInit(2)

    // First measure cold read for lazy (includes initialization)
    val tColdLazy = measureNanoTime { cLazy.area } // first access initializes
    println("Lazy first access (includes init): $tColdLazy ns")

    // Now measure repeated reads
    measureAccess("Computed getter", { cComputed.area })
    measureAccess("Cached field", { cCached.area })
    measureAccess("Lazy (subsequent)", { cLazy.area })

}
