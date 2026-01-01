package com.interop

import Child
import Root
import Summa

class Drill_1{
    companion object{
        fun normal() = "N"
        @JvmStatic fun stat() = "S"
    }
}

class Drill_2{
    companion object {
        @JvmField val F = "F"

        val G = "G"

        @JvmStatic val S = "S"
    }
}

class Drill_3 {
    companion object {
        @get:JvmStatic val V get() = "V"
    }
}

fun handleRoot(r:Root) {
    when (r) {
        is Child -> TODO()
        is Summa -> TODO()
    }
}
// class DrillSealed:Root() which is not accessible, because sealed class is not accessible in other packages

