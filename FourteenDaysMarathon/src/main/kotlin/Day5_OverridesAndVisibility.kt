class X {
    var regular: Int = 1

    @JvmField
    var direct: Int = 2

    companion object {
        const val C = "C"

        @JvmField
        val SF = "SF"
        fun foo() {}

        @JvmStatic
        fun bar() {
        }
    }
}

class Api {

    @get:JvmSynthetic
    @set:JvmSynthetic
    var a =10
    fun visibleToAll(): String = "visible"

    @JvmSynthetic
    fun onlyForKotlin(): String = "Kotlin-only"
}

class InternalExample {
    @JvmField
    var a: Int = 1
    internal val hidden = "secret"
}

class F {
    fun foo(a: Int = 1) = a
    @kotlin.jvm.JvmSynthetic
    fun bar(a: Int = 2) = a
}


fun main() {
    X.C
}

