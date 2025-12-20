// Declaration-site variance (safe producer)
// Box can only expose T (as return), not accept T as parameter
class Box<out T>(private val value: T) {
    fun get(): T = value

    // fun set(v: T) { ... } // NOT allowed — T in "in" position disallowed
}

interface Consumer<in T> {
    fun consume(item: T)
}

class Holder<T>(var value: T)

fun <T> write(h: Holder<T>, v: T) { h.value = v }



interface Producer<out T> {
    fun produce(): T
}

fun <T> copyList(from: List<out T>, to: MutableList<in T>) {
    for (item in from) to.add(item)
}

fun main() {

    val ints = listOf(1, 2, 3)
    val numbers = mutableListOf<Int>()

    copyList(ints, numbers)
    println(numbers)

    val producer = object : Producer<Int> {
        override fun produce() = 34
    }

    val producerAsNumber: Producer<Number> = producer
    val producerAsInt: Producer<Int> = producer
    // val producerAsLong: Producer<Long> = producer which ask int or number only, not long

    val consumer = object : Consumer<Number> {
        override fun consume(item: Number) = println(item)
    }

    val consumerAsIntConsumer: Consumer<Int> = consumer
    val consumerAsNumberConsumer: Consumer<Number> = consumer

//    val h: Holder<*> = Holder("hi")
//    write(h, "x")   // call A
//    write(h, null)  // call B

    val real: MutableList<Any> = mutableListOf("hello")
    val lst: MutableList<in Number> = real
    val a: Any? = lst[0]        // OK
// val n: Number = lst[0]   // ❌ compile error

    class Holder<T>(var value: T)
    fun <T> write(h: Holder<T>, v: T) { h.value = v }

    val h1: Holder<*> = Holder("hi")
// write(h, "x")            // ❌ compile error
// write(h, null)           // ❌ compile error

    val h2: Holder<Any?> = Holder("hi")
    write(h2, "x")             // OK
    write(h2, null)            // OK



}
