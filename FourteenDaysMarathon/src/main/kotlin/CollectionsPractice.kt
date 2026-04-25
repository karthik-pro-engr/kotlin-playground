import kotlinx.coroutines.runBlocking

fun main(): Unit = runBlocking {
//    demoGroupByAndGroupingByEachCount()
//    demoFold()
//    demoGroupBy()
//    demoReduceNonAssociativeOp()
//demoWindowed()
//    findAndSoOn()
//    demoAssociateBy()
    demoDistinct()
}

fun immutableList() {
    val idx = listOf("a", "b", "c").indexOf("b")  // returns 1
    val list: List<Int> = mutableListOf(1, 2, 3)    // read-only view over a mutable list
    val l = listOf(1, 2)
    // l.add(3) // compile error - no add on List
    (l as MutableList).add(3) // may throw UnsupportedOperationException at runtime
    // Somewhere else:
    (list as MutableList).add(4) // unsafe cast — may throw at runtime if not mutable
    println(list) // unpredictable if mutated concurrently
}

fun mutableList() {
    val a: MutableList<Int> = MutableList(20, { a -> a })
    a.add(10)
    println()
}

suspend fun subList() {
    val base = mutableListOf(1, 2, 3, 4, 5)
    val subList = base.subList(1, 4)
    println(subList.joinToString(","))

    subList.add(23)
    println(
        "after adding 23 sublist will be -> ${subList.joinToString()}, main list will be-> ${
            base.joinToString(
                ","
            )
        }"
    )

    base.add(24)

    println(
        "after adding 24 in sublist and then sublist will be -> ${subList.joinToString()}, main list will be-> ${
            base.joinToString(
                ","
            )
        }"
    )

}

fun demoSequence() {

    val data = (1..10).toList()

    println("=== EAGER (List) ===")
    val eager = data
        .map { println("map1 $it"); it * 2 }   // creates a new List
        .filter { println("filter $it"); it % 3 != 0 } // creates another List
        .map { println("map2 $it"); it + 1 }  // another List
    println("eager size: ${eager.size}")

    println("\n=== LAZY (Sequence) ===")
    val lazy = data.asSequence()
        .map { println("smap1 $it"); it * 2 }   // lazy, no work yet
        .filter { println("sfilter $it"); it % 3 != 0 }
        .map { println("smap2 $it"); it + 1 }
        .take(3) // short-circuit: take 3 results only
        .toList() // terminal op triggers evaluation
    println("lazy size: ${lazy.size}")

    val seq = generateSequence(1) { if (it < 3) it + 1 else null }
    val list = seq.map { it * 2 }.toList()
    println(list)

}

fun demoGroupingBy() {
    val items = listOf("apple", "banana", "apple")
    val grouped = items.groupingBy { it }.eachCount()
    println(grouped["apple"])
}

fun demoMap() {
    data class UserDto(val name: String, val age: Int)
    data class User(val displayName: String, val age: Int)

    val dtos = listOf(UserDto("alice", 28), UserDto("bob", 34))
    val users = dtos.map { dto -> User(dto.name.capitalize(), dto.age) }
    println(users) // [User(displayName=Alice, age=28), User(displayName=Bob, age=34)]
}

fun demoFilter() {
    val numbers = listOf(1, 2, 3, 4, 5, 6)
    val evens = numbers.filter { it % 2 == 0 }
    println(evens) // [2, 4, 6]
}

fun demoNotNull() {
    val inputs = listOf("1", "a", "2", "3b")
    val ints = inputs.mapNotNull { it.toIntOrNull() }
    println(ints) // [1, 2]
    val map = inputs.map { it.toIntOrNull() }
    println(map)
    val map1 = inputs.map { value -> value + "a" }
    val onEach = inputs.onEach { println(it) }
    val onForEach = inputs.forEach { println(it) }
    println(map1)
}

data class UserData(val id: String, val age: Int, var name: String)

fun demoGroupByAndGroupingByEachCount() {
    val listOfUsers = listOf(
        UserData("u1", 20, "Alice"),
        UserData("u2", 25, "Bob"),
        UserData("u3", 20, "Carol"),
        UserData("u4", 30, "Dan")
    )

    val grouped = listOfUsers.groupBy { it.age }
    println("Printing immediately after grouped $grouped")
    listOfUsers[2].name = "Karthik"
    println("Printing after grouped updating list item -> $grouped")

    val eachCount = listOfUsers.groupingBy { it.age }.eachCount()
    println("each count-> $eachCount")
    listOfUsers.groupingBy { it.age }
    val fold = listOfUsers.groupingBy { it.age }.fold("") { acc, element ->
        if (acc.isEmpty()) {
            element.name
        } else {
            acc + "," + element.name
        }

    }
    println(fold)

}

fun demoFold() {
    data class Purchase(val category: String, val amount: Int)

    val data = listOf(
        Purchase("food", 50), Purchase("tech", 200), Purchase("food", 30)
    )
    val fold = data.groupingBy { it.category }.fold(0) { accumulator, element ->
        if (accumulator == 0) {
            element.amount
        } else {
            accumulator + element.amount
        }
    }
    data.groupingBy { it.category }
    println(fold) // {food=80, tech=200}

}
fun demoGroupBy() {
    val list1 = listOf(Int.MAX_VALUE, 1)
    data class P(val k: Int?)
    val list = listOf(P(1), P(null), P(1))
    val grouped = list.groupBy { it.k } // grouped[null] will exist
    println(grouped)
}

fun demoFoldWithParallalism() {
    val data = (1..100).toList()

    // Operation: sum, identity = 0 (associative)
    fun sumChunk(chunk: List<Int>): Int = chunk.fold(0) { acc, v -> acc + v }

    // Split into 4 chunks (could be processed in parallel)
    val chunks = data.chunked(data.size / 4)

    val partialSums = chunks.map { sumChunk(it) }   // could be computed concurrently
    val totalFromPartials = partialSums.fold(0) { acc, v -> acc + v } // combine partials

    val direct = data.fold(0) { acc, v -> acc + v } // direct single fold

    println("partialSums=$partialSums")
    println("combined=$totalFromPartials, direct=$direct")


}
fun demoReduceNonAssociativeOp(){
    val data = listOf(1,2,3,4)

    fun subChunk(chunk: List<Int>) = chunk.fold(0) { acc, v -> acc - v } // uses 0 identity
    val chunks = data.chunked(2)
    val partials = chunks.map { subChunk(it) }   // e.g., [-3, -7]
    val combined = partials.fold(0) { acc, v -> acc - v } // combine partials with '-'

    val directReduce = data.reduce { a, b -> a - b } // no initial 0, different result

    println("partials=$partials, combined=$combined, directReduce=$directReduce")
    val toMutableList = data.toMutableList()
    toMutableList.add(10)
    val toList = toMutableList.toList()
    toMutableList.add(20)
    println( toList.size)
}

fun demoWindowed() {
    // 1
    val list = listOf(1, 2, 3, 4, 5, 6)

    // 2 - default windowed (size=3, step=1)
    val windowsDefault = list.windowed(3)

    // 3 - step > 1 (sparse, non-overlapping when step == size)
    val windowsStep = list.windowed(size = 3, step = 2)

    // 4 - partial windows allowed (last windows with fewer elements included)
    val windowsPartial = list.windowed(size = 4, step = 3, partialWindows = true)

    // 5 - chunked: non-overlapping fixed-size groups
    val chunks = list.chunked(2)

    // 6 - arrays are not Iterable by Java rules; convert to iterable to use windowed
    val array = arrayOf(10, 20, 30, 40, 50)
    val arrayWindows = array.asIterable().windowed(3)

    // 7 - primitive arrays → convert to list to use these helpers (or use specific overloads)
    val intArr = intArrayOf(1, 2, 3, 4, 5)
    val intArrChunks = intArr.toList().chunked(2)

    // 8 - sequences: lazy windowed (infinite-friendly)
    val seq = generateSequence(1) { it + 1 }
    val seqWindowed = seq.windowed(size = 5).take(3).toList()

    // 9 - print results
    println("windowsDefault = $windowsDefault")
    println("windowsStep    = $windowsStep")
    println("windowsPartial = $windowsPartial")
    println("chunks         = $chunks")
    println("arrayWindows   = $arrayWindows")
    println("intArrChunks   = $intArrChunks")
    println("seqWindowed    = $seqWindowed")


}

fun findAndSoOn(): Unit {
/*    data class User(val id: Int, val name: String)
    val users = listOf(User(1, "Rita"), User(2, "Sam"), User(3, "Lee"), User(4, "Sam"),User(5, "Lee"))
    val first = users.firstOrNull()
    val sam = users.find { it.name == "Sam" }
    val notSam = users.find { it.name == "Samm" }
    val idxLee = users.indexOfFirst { it.name == "Lee" }
    val hasId2 = users.any { it.id == 2 }           // equivalent check to contains for transformed key
    println(first)   // User(1, "Rita")
    println(sam)     // User(2, "Sam")
    println(idxLee)  // 2
    println(hasId2)  // true
    println(notSam)  // null
    val map = mutableMapOf<String, Int>()
    val v = map.getOrElse("count") { "Karthik" } // compute block runs only if missing
    println(v)
    val map1 = hashMapOf("x" to mutableListOf(1) )
    val key = map1.keys.first()
    map1["x"]!!.add(2)
    val first1 = map1.keys.first()
    println("$key, $first1")

    val arr = intArrayOf(1,2,3)
    val boxList = arr.toList()
    println(boxList::class)
    println(boxList is List<Int>)
    val l = listOf(1,2,3)
    val seq = l.asSequence().map {
        println("m$it")
        it * 2 }.filter {
            println("f$it")
            it % 3 == 0 }
    println(seq.toList())

    val l2 = (1..10).asSequence().map {
        println("map:$it")
        it
    }.filter {
        println("filter:$it")
        it % 2 == 0
    }.take(2)
    println("before terminal")
    println(l2.toList())
    val map = mutableMapOf<String, Int>()
    val v1 = map.putIfAbsent("a", 1)
    val v2 = map.putIfAbsent("a", 2)

    println("$v1 | ${map["a"]} | $v2")

    val l = mutableListOf<Int>()
    repeat(1000) { l.add(it) }
    val sub = l.subList(0, 10)
    l.clear()
    println(sub.size)
    */

}

fun demoAssociateBy(){
    data class User(val id: Int, val name: String, val age: Int, val city: String?)
    val users = listOf(
        User(1, "Alice", 30, "Bengaluru"),
        User(2, "Bob",   26, "Mumbai"),
        User(3, "Cara",  28, "Bengaluru"),
        User(2, "Bobby", 27, null)  // duplicate id 2 on purpose
    )

    // --- associateBy: build Map<id, User> (last entry wins for duplicate key)
    val byId: Map<Int, User> = users.associateBy { it.id }
    println(byId) // {1=Alice, 2=Bobby, 3=Cara}

    val associateByMap = users.associateBy({ it.id }, { it.name })
    println(associateByMap)
}

fun demoDistinct(){
    data class User(val id: Int, val name: String)
    val numbers = listOf(1, 2, 2, 3, 3, 3)
    val unique = numbers.distinct()
    println(unique)
    val users = listOf(
        User(1, "A"),
        User(1, "B"),
        User(2, "C")
    )

    val uniqueById = users.distinctBy { it.id }
    println(uniqueById)

    setOf(1,2,2,3)
    listOf(2,3,4).toSet()
}

