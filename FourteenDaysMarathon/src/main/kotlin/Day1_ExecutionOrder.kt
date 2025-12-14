open class Base(name: String) {


    init {
        println("Base init block1: name = $name")
    }

    init {
        println("Base init block2: name = $name")
    }


    val baseProp: Int = name.length.also {
        println("Base property initializer: name length = $it")
    }


    constructor(name: String, flag: String) : this(name) {
        println("Base secondary constructor: name=$name, flag=$flag")
    }
}

class Derived(name: String, tag: String, print: String = "X".also { println("default tag expression for print parameter") }) :
    Base(name.uppercase()) {

    constructor(name: String) : this(name, "DEFAULT") {
        println("Derived secondary constructor: name=$name")
    }

    init {
        println("Derived init block: name = $name tag= $tag")
    }

    val derivedProp: Int = tag.length.also {
        println("Derived property initializer: tag length = $it")
    }


}

fun main() {
    println("---- Creating Derived(\"hello\", \"k\") via primary ----")
    Derived("hello", "k")

    println("\n---- Creating Derived(\"world\") via secondary ----")
    Derived("world")


}