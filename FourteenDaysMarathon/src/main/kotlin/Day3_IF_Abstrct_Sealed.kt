interface I { fun x(): String }
class Impl : I { override fun x() = "Impl" }

class D1(private val i: I) : I by i {
    override fun x(): String {
        return "D1->" + i.x()
    }
}

class D2(private val i: I) : I by i {
    // no override
}

fun main() {

    val d = D2(D1(Impl()))
    println(d.x()) // ?

}
