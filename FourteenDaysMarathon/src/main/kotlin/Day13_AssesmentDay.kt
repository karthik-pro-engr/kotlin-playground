// Save as LazyAndDelegatesDemo.kt and run on JVM
import kotlinx.coroutines.*
import kotlin.LazyThreadSafetyMode
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlin.properties.Delegates
import java.util.concurrent.CountDownLatch

/**
 * Day 13 — Kotlin Interview Assessment (1 Crore job)
 *
 * Time rules (simulate a real interview):
 *  - MCQs: 45–60 min
 *  - Coding tasks: 45–60 min
 *  - Total: 90–120 min
 *
 * Rules:
 *  - No IDE autocomplete for MCQs
 *  - No Googling
 *  - Answer MCQs in the format: 1:B,2:D,3:A ...
 *
 * IMPORTANT:
 *  - Code blocks that were provided as part of MCQ questions are included below
 *    AS RUNNABLE (uncommented) snippets inside functions (q1Snippet(), q7Snippet(), ...).
 *  - To execute a particular question snippet, call that snippet from main() or run it in REPL.
 */

/* -------------------------
   SECTION A — 30 ULTRA-HARD MCQs
   ------------------------- */

/* Q1 (Initialization & Properties)
   The original code block is included below as a runnable snippet in q1Snippet().
   To run it: call q1Snippet() from main().
*/

// Q1 snippet: instantiate B() to observe initialization order
open class AExec {
    open val x = initX()

    init {
        println("A.init x=$x")
    }

    open fun initX(): Int {
        println("A.initX")
        return 10
    }
}

class BExec : AExec() {
    override val x = initX()

}


/* Q2
Which rule is violated here?


A. Calling non-open function during init
B. Using val instead of var
C. Open property initialized eagerly
D. Nothing — this is safe
*/
open class BaseDay13 {
    open val size = compute()
    fun compute() = 42
}


/* Q3
Which properties do NOT have backing fields?


A. a, b
B. b, d
C. b only
D. c, d
*/

class Q3 {

    val a = 10
    val b get() = a * 2
    val c by lazy { 5 }
    val d: Int = 0
        get() = field
}

/* Q4
`lateinit` cannot be used with:

A. var name: String
B. var list: MutableList<Int>
C. var count: Int
D. var ctx: Context
*/

/* Q5
val x by lazy(LazyThreadSafetyMode.NONE) { compute() }

Which is true?

A. Always thread-safe
B. Thread-safe only on JVM
C. Not thread-safe
D. Uses double-checked locking
*/

/* Q6
Which delegate intercepts both read and write?

A. lazy
B. observable
C. vetoable
D. Custom ReadOnlyProperty
*/

/* Q7 (Interfaces, Delegation, Sealed)
   The code block for Q7 is included below as a runnable snippet in q7Snippet().
   To run it: call q7Snippet() from main().
*/

interface Inter {
    fun f(): String
    fun g(): String = "g"
}

class CDay13 : Inter {
    override fun f() = "f"
}

class DDay13(private val i: Inter) : Inter by i {
    override fun g() = "D"
}

class Q7 {
    fun callQ7() {
        println("--- Q7 snippet output ---")
        println(DDay13(CDay13()).g()) // expected output per the snippet
        println("--- end Q7 snippet ---")
    }
}


/* Q8
Why are sealed classes preferred for state modeling?

A. Faster at runtime
B. JVM optimization
C. Exhaustive when
D. Easier inheritance
*/

/* Q9
Which is true about class delegation?

A. Delegate methods are copied
B. Delegate is inherited
C. Calls are forwarded at runtime
D. Reflection required
*/

/* Q10
Can a sealed class be extended outside its file?

A. Yes
B. Yes with open
C. Only in same module
D. No
*/

/* Q11
Which is NOT allowed?

A. Interface with state via delegation
B. Abstract class with constructor
C. Sealed interface
D. Sealed class with open functions
*/

/* Q12
Java interop problem with sealed classes?

A. Cannot instantiate
B. No exhaustive checking in Java
C. Cannot subclass
D. Requires annotations
*/

/* Q13 (Overrides, Visibility, JVM)
   The illustrative code is included as a runnable snippet in q13Snippet().
   To run it: call q13Snippet() from main().
*/
fun q13Snippet() {
    open class A {
        open val x = 1
    }

    class B : A() {
        // In a real top-level example this would be allowed: override var x = 2
        // Demonstrate locally:
        override var x = 2
    }

    println("--- Q13 snippet ---")
    val b = B()
    println("b.x = ${b.x}")
    println("--- end Q13 snippet ---")
}

/* Q14
Why is var → val override forbidden?

A. JVM limitation
B. Breaks LSP
C. Bytecode issue
D. Performance
*/

/* Q15
What does @JvmField do?

A. Makes field static
B. Removes getter/setter
C. Forces lateinit
D. Improves performance
*/

/* Q16
Which creates true static in bytecode?

A. companion object { val x }
B. @JvmStatic val x
C. @JvmField val x
D. Top-level val
*/

/* Q17
Why is internal dangerous for libraries?

A. Not visible in Kotlin
B. JVM has no internal
C. Compiler bug
D. Reflection bypass
*/

/* Q18
@JvmOverloads is needed because:

A. Kotlin default params aren’t JVM overloads
B. Java can’t call Kotlin
C. Constructors are private
D. JVM ignores defaults
*/

/* Q19 (Generics & Variance)
fun copy(from: List<out Number>, to: MutableList<Number>)

Why out?

A. To allow mutation
B. Producer
C. JVM requirement
D. Performance

-- The example signature is harmless; include a local wrapper to show usage if needed.
*/
fun q19Snippet() {
    fun copy(from: List<out Number>, to: MutableList<Number>) {
        for (n in from) to.add(n)
    }

    println("--- Q19 snippet ---")
    val from: List<Int> = listOf(1, 2, 3)
    val to: MutableList<Number> = mutableListOf()
    copy(from, to)
    println("to = $to")
    println("--- end Q19 snippet ---")
}

/* Q20
val x: MutableList<in Number>
x.add(10)
val y = x[0]

Type of y?

A. Number
B. Any
C. Any?
D. Nothing
*/

/* Q21
Why does this fail?

class Box<T>(var v: T)
val b: Box<*> = Box("hi")
b.v = "x"

A. Star projection
B. JVM type erasure
C. Invariance
D. Platform type

-- NOTE: this write fails to compile; therefore it's left as a commented example below.
*/
/*
class Box<T>(var v: T)
val b: Box<*> = Box("hi")
// The following line will NOT compile — left commented intentionally to keep the file compiling.
// b.v = "x"
*/

/* Q22
List<*> is equivalent to:

A. List<Any>
B. List<Any?>
C. List<out Any?>
D. List<in Any?>
*/

/* Q23
Where does variance matter?

A. Runtime
B. JVM bytecode
C. Compile-time only
D. Reflection
*/

/* Q24
Which breaks type safety?

A. as?
B. as
C. out
D. in
*/

/* Q25 (Inline, Reified, Coroutines)
Why are reified types only allowed in inline functions?

A. JVM limitation
B. Type erasure
C. Compiler design
D. Performance
*/

/* Q26
Non-local return means:

A. Return from lambda
B. Return from caller
C. Return from JVM
D. Illegal
*/

/* Q27
Which blocks non-local return?

A. inline
B. crossinline
C. noinline
D. reified
*/

/* Q28
async exception is thrown:

A. Immediately
B. On launch
C. On await
D. Never
*/

/* Q29
supervisorScope effect?

A. Parent fails children
B. Child fails parent
C. Child isolated
D. Cancels all
*/

/* Q30
Best practice for Android coroutines?

A. GlobalScope
B. Custom scope everywhere
C. Structured scopes
D. Manual Job handling
*/

/* -------------------------
   END OF MCQs
   ------------------------- */

/**
 * Timed assessment runner (unchanged).
 * It captures a single-line MCQ answer string and elapsed time.
 *
 * If you want to run any of the question snippets, call their functions from here,
 * e.g. q1Snippet(), q7Snippet(), q13Snippet(), q19Snippet()
 */
/*fun main() {
    println("Day 13 — Kotlin Interview Assessment (1 Crore job)")
    println()
    println("READ BEFORE START:")
    println("  - Paste your MCQ answers in ONE LINE (format: 1:B,2:D,3:A,...)")
    println("  - Do NOT include explanations or ask for hints here.")
    println()
    println("If you want to run a question snippet before/after the timed test, call its function from main.")
    println("Available runnable snippets: q1Snippet(), q7Snippet(), q13Snippet(), q19Snippet()")
    println("Example: uncomment a call like q1Snippet() below if you want to see that snippet's output.")
    println()
    // Example calls (commented out so they don't run during the timed assessment).
    // Uncomment to run them.
    // q1Snippet()
    // q7Snippet()
    // q13Snippet()
    // q19Snippet()

    println("When you are ready, press ENTER to start timer and submit answers.")
    print("Press ENTER to start -> ")
    readLine() // wait for user to begin; this marks the start

    val start = System.currentTimeMillis()
    println()
    println("TIMER STARTED. Paste your answers now (1:B,2:D,3:A,...). Then press ENTER.")
    print("Your answers: ")
    val answers = readLine()?.trim() ?: ""
    val end = System.currentTimeMillis()
    val elapsedSecs = (end - start) / 1000.0

    println()
    println("=== SUBMISSION RECEIVED ===")
    println("Answers (raw):")
    println(answers)
    println()
    println("Elapsed time: %.1f seconds".format(elapsedSecs))
    println()
    println("Notes:")
    println(" - Keep your answers private until you self-grade or ask for official grading.")
    println(" - This program does NOT grade your answers; it's a timed capture utility only.")
    println()
    println("Good work — treat this honestly and review mistakes after self-grading. 1 Crore job.")
}*/

open class AExecOrder {
    open val x = initX()

    init {
        println("A.init x=")
    }

    open fun initX(): Int {
        println("A.initX")
        return 10
    }

    fun printX() {
        println("A.init x= $x")
    }

}

class BExecOrder : AExecOrder() {
    override val x = initX()
    private lateinit var a: AExecOrder
    override fun initX(): Int {
        println("B.initX")
        return 20
    }
}

open class AExtension
class BExtension : AExtension()

fun AExtension.foo() = "A"
fun BExtension.foo() = "B"

fun printExtension(a: AExtension) {
    println(a.foo())
}

fun main() {
    /*  println("=== LAZY MODES DEMO ===")
      runLazyMode("SYNCHRONIZED (default)", LazyThreadSafetyMode.SYNCHRONIZED)
      runLazyMode("PUBLICATION", LazyThreadSafetyMode.PUBLICATION)
      runLazyMode("NONE", LazyThreadSafetyMode.NONE)

      println("\n=== DELEGATES DEMO ===")
      delegatesDemo()*/

    /*println(isType<String>("hello"))   // true
    println(isType<Int>("hello"))      // false*/
    //BExec()


   /* runBlocking {

            launch {
                supervisorScope {
                launch {
                    delay(100)
                    throw RuntimeException("Child failed")
                }
                delay(200)
                println("Parent finished")
            }
        }
    }*/

//    test()

//    checkAsync()
//
    printExtension(BExtension())

}

inline fun runBlock(block: () -> Unit) {
    block()
    println("after block")
}

fun test() {
    runBlock {
        return
    }
    println("after runBlock")
}

sealed interface Interface {
    object obj : Interface
}

/** ---------- Lazy demo ---------- */
fun runLazyMode(label: String, mode: LazyThreadSafetyMode) {
    println("\n--- Scenario: $label ---")
    val obj = object {
        // initializer simulates expensive work and prints when it runs
        val x by lazy(mode) {
            println("[$label] initializer running on thread=${Thread.currentThread().name}")
            Thread.sleep(100) // simulate work
            42 // computed value
        }

        fun access(startLatch: CountDownLatch) {
            startLatch.await()
            // Access property (causes initialization if not done)
            println("[$label] thread ${Thread.currentThread().name} sees x = ${x}")
        }
    }

    val start = CountDownLatch(1)
    val t1 = Thread { obj.access(start) }.apply { name = "T1-$label" }
    val t2 = Thread { obj.access(start) }.apply { name = "T2-$label" }

    t1.start()
    t2.start()

    // release both threads simultaneously
    Thread.sleep(20) // give threads time to reach await()
    start.countDown()

    t1.join()
    t2.join()
    println("--- End Scenario: $label ---")
}

/** ---------- Delegates demo ---------- */
fun delegatesDemo() {
    // 1) ReadWriteProperty (custom) -> intercepts BOTH get and set
    class ReadWriteDelegate<T>(private var value: T) : ReadWriteProperty<Any?, T> {
        override operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
            println("[ReadWriteDelegate] get ${property.name}")
            return value
        }

        override operator fun setValue(thisRef: Any?, property: KProperty<*>, v: T) {
            println("[ReadWriteDelegate] set ${property.name} = $v")
            value = v
        }
    }

    var both by ReadWriteDelegate(10)
    println("both initial read -> $both")
    both = 20
    println("both after write -> $both")

    // 2) ReadOnlyProperty -> intercepts get ONLY
    class ReadOnlyDelegate<T>(private val value: T) : ReadOnlyProperty<Any?, T> {
        override operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
            println("[ReadOnlyDelegate] get ${property.name}")
            return value
        }
    }

    val ro by ReadOnlyDelegate("hello")
    println("ro read -> $ro")

    // 3) Delegates.observable / vetoable -> intercept write (set) only
    var obs by Delegates.observable(0) { prop, old, new ->
        println("[observable] ${prop.name}: $old -> $new")
    }
    obs = 1
    obs = 2

    var vet by Delegates.vetoable(0) { prop, old, new ->
        println("[vetoable] attempt ${prop.name}: $old -> $new")
        // allow only non-negative
        new >= 0
    }
    vet = 3
    vet = -5 // will be vetoed; no change
    println(vet)

    // 4) lazy -> intercepts first read (initializer run), subsequent reads return cached value
    val lazyVal by lazy {
        println("[lazy] initializer running")
        99
    }
    println("first access lazyVal -> ${lazyVal}")
    println("second access lazyVal -> ${lazyVal}")

    println("\nDemo finished.")
}

sealed class Subject

class A_Static {
    companion object {
        val x: Int = 1
    }
}

class B_Static {
    companion object {
        @JvmStatic
        val x: Int = 1
    }
}

class C_Static {
    companion object {
        @JvmField
        val X: Int = 1
    }
}

val TOP: Int = 1

@JvmField
val PUBLIC_TOP: Int = 1


inline fun <reified T> isType(obj: Any): Boolean {
    return obj is T
}
/*
 fun <T> isTypeFail(obj: Any): Boolean {
    return obj is T
}
*/

fun checkSmartCast() {

    var x: String? = "hello"

    if (x != null) {
        x = null
        println(x?.length)
    }
}

fun checkAsync() = runBlocking {
    val d = async {
        throw RuntimeException("boom")
    }
    delay(100)
    println("done")
}











