// NO DSL MARKER
open class Outer {
    fun outerFun() { println("outerFun() called on Outer instance: $this") }

    operator fun String.invoke(block: Inner.() -> Unit) {
        println("  [invoke] dispatchReceiver (Outer) = $this@Outer, extensionReceiver (String) = '$this'")
        val child = Inner(this)
        child.block()
    }
}

class Inner(val name: String) {
    fun innerFun() { println("innerFun() called on Inner instance: $this (name=$name)") }
}

// call site

fun demoNoMarker() {
    val o = Outer()
    o.run {
        "child" {
            println("inside block: this = $this (should be Inner instance)")
            outerFun()   // resolves to Outer.outerFun()
            innerFun()   // resolves to Inner.innerFun()
        }
    }
}

fun main() = demoNoMarker()


@DslMarker
annotation class MyDsl

@MyDsl
open class OuterWithMarker {
    fun outerFun() { println("outerFun() called on Outer instance: $this") }

    operator fun String.invoke(block: InnerWith.() -> Unit) {
        println("  [invoke] dispatchReceiver (Outer) = $this@Outer, extensionReceiver (String) = '$this'")
        val child = InnerWith(this)
        child.block()
    }
}

@MyDsl
class InnerWith(val name: String) {
    fun innerFun() { println("innerFun() called on Inner instance: $this (name=$name)") }
}

fun demoWithMarker() {
    val o = OuterWithMarker()
    o.run {
        "child" {
          //  outerFun()   // <-- COMPILER ERROR: "Cannot access 'outerFun' in 'Outer' from a receiver of type 'Inner'"
            innerFun()
        }
    }
}
