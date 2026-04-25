import kotlin.reflect.KFunction

data class UserInline(val name: String = "default")
class UserWithArg(val name: String) // no no-arg constructor

inline fun <reified T> isType(obj: Any?): Boolean = obj is T

inline fun <reified T : Any> createInstanceOrNull(): T? =
    try {
        T::class.java.getDeclaredConstructor().apply { isAccessible = true }.newInstance()
    } catch (e: Exception) {
        null
    }

inline fun <reified T> isListOf(obj: Any?): Boolean {
    if (obj !is List<*>) return false
    return obj.all { it is T }
}


var stored: (() -> Unit)? = null

inline fun doAndStore(noinline blockToStore: () -> Unit, inlineBlock: () -> Unit) {
    stored = blockToStore   // must be noinline
    inlineBlock()           // can be inlined; allows non-local return if used
}


inline fun foo(noinline block: () -> String, run: () -> String) {
    println("entered foo")
    val block1 = block()          // block is inlined at call site
    println("After called block returned value-> $block1")
    run()
    println("exiting foo")   // might not run if block non-local-returns
}

inline fun zoo(param: () -> Unit) {
    println("Inside Zoo")
}

fun callerNonLocal(): String {
    foo({
        println("inside lambda, about to non-local return")
        return@foo "Foo"     // NON-LOCAL return: returns from callerNonLocal()
    }) {
        println("Inside run lambda, about to not a noinline parameter")
        return "Returned Value from Run lambda"
    }
    println("this line will NOT execute because lambda returned non-locally")
    return "Empty"
}

fun bar(block: () -> Unit) {
    println("entered bar")
    block()
    println("exiting bar")
}

inline fun callLater(noinline action: () -> Unit) {
    val r = Runnable {
        action()   // action will be invoked inside another lambda (Runnable)
    }
    r.run()
}

fun testCrossInline() {
    callLater {
        println("inside")
        return@callLater    // COMPILER ERROR: "return is not allowed here"
    }
}


inline fun sayHello(name: String) {
    println("Hello, $name")
}


fun callerLocal() {
    bar {
        println("inside lambda, trying to non-local return")
        // return       // COMPILER ERROR: "return is not allowed here"
        return@bar     // Local return from the lambda (returns to bar)
    }
    println("this line WILL execute, because return was local to the lambda")
}

fun registerSam(cb: CallbackSAM) {
    println("${cb.onDone("C", "S")} Register was called")

}

fun register(cb: Callback) {
    println("${cb.onDone("C", "S")} Register was called")

}

fun printIt(s: String, s1:String) = println(s)


fun interface KHandler { fun handle(s: String) }
fun use(h: KHandler) { h.handle("x") }

fun main() {
    // isType examples
    println(isType<String>("hello"))      // true
    println(isType<Int>("hello"))         // false
    println(isType<List<Int>>(listOf(1, 2, 3))) // true

    // createInstanceOrNull
    val a: User? = createInstanceOrNull<User>()
    println(a) // User(name=default)

    val b: UserWithArg? = createInstanceOrNull<UserWithArg>()
    println(b) // null (no-arg constructor missing)

    // isListOf demo (reified advantage)
    println(isListOf<String>(listOf("a", "b"))) // true
    println(isListOf<String>(listOf("a", 1)))  // false

    // SAM interop examples (Kotlin -> Java SAM)
    val r = Runnable { println("running from Runnable lambda") }
    Thread(r).start()

    // Using Java's Comparator SAM in Kotlin
    val words = mutableListOf("kotlin", "java", "c")
    words.sortWith(Comparator { x, y -> x.length - y.length })
    println(words) // ["c","java","kotlin"]

    // Kotlin function passed to Java's Consumer (explicit SAM constructor)
    val consumer = java.util.function.Consumer<String> { println("consume: $it") }
    consumer.accept("hello")

    // Passing lambda directly to a Java overload expecting a SAM often works:
    java.util.stream.Stream.of("a", "bb", "ccc").forEach { println("stream item: $it") }

    // Vice versa: Java SAM object used in Kotlin as an interface instance:
    val javaRunnable = object : Runnable {
        override fun run() {
            println("javaRunnable.run() called")
        }
    }
    javaRunnable.run()

    doAndStore({}, {})

    val callerNonLocal = callerNonLocal()
    println("after callerNonLocal returned value-> $callerNonLocal")
    println("************")
    callerLocal()

    val kFunction1 = ::zoo
    kFunction1 {
        println("Iniside Zoo main")
    }
    zoo {
        println("Inside without reference")
    }

    val value = object : Callback {
        override fun initialize(a: Int) {
            TODO("Not yet implemented")
        }

        override fun onDone(s: String?, s1: String?) {
            TODO("Not yet implemented")
        }

    }
    registerSam() { s, s1 -> println(s) }

    register(value)
    registerSam(::printIt)

    use { println(it) }            // lambda directly accepted as KHandler
    val h: KHandler = KHandler { println(it) }  // also accepted



}
