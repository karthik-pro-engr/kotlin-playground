fun main() {
    /*  foundFrequentWordsWithK(listOf("apple","banana","apple","orange","banana","apple"),2)
      foundFrequentWordsWithK(listOf("dog","cat","cat","dog","bird"),2)
      foundFrequentWordsWithK(listOf("a","b","a"),5)
      foundFrequentWordsWithK(listOf(),3)
      foundFrequentWordsWithK(listOf("x","y"),0)*/
//    sortedAndRelated()
    println(groupContinous(listOf(1, 1, 2, 2, 2, 3, 1, 1, 4, 5, 6, 7, 3, 3, 3, 6,1,1)))
}

/*
Goal (one line):
Given a list of words, return the top k most frequent words. If two words have the same frequency,
 the word with the smaller lexicographic (dictionary) order comes first.
 Inputs

words: List<String> — a list of strings (words). Words may repeat.

k: Int — number of top results to return. k >= 0.

Output

List<String> of length min(k, distinctWords) containing the top k words sorted:

primarily by frequency descending (higher frequency first)

secondarily by lexicographic ascending when frequencies tie

Examples

Simple

Input: ["apple","banana","apple","orange","banana","apple"], k = 2

Frequencies: apple:3, banana:2, orange:1

Output: ["apple", "banana"]

Tie broken by lexicographic order

Input: ["dog","cat","cat","dog","bird"], k = 2

Frequencies: dog:2, cat:2, bird:1

cat vs dog tie → lexicographic: "cat" < "dog"

Output: ["cat", "dog"]

k larger than distinct words

Input: ["a","b","a"], k = 5

Distinct words = 2 → return both: ["a","b"]

Empty input / zero k

Input: [], k = 3 → []

Input: ["x","y"], k = 0 → []
* */
fun foundFrequentWordsWithK(input: List<String>, k: Int): List<String> {

    val partOutput = input.groupingBy { it }.eachCount().filter { it.value >= 2 }
    val output = partOutput.keys.sorted()
    return output

}

fun topKFrequent(words: List<String>, k: Int): List<String> {
    if (k <= 0) return emptyList()
    val freq = words.groupingBy { it }.eachCount()
    return freq.entries
        .sortedWith(compareByDescending<Map.Entry<String, Int>> { it.value }
            .thenBy { it.key })
        .take(k)
        .map { it.key }
}

fun sortedAndRelated() {

    val nums = listOf(2, 1)
    val original = mutableListOf(2, 1)
    println(original)
    val sort = original.sort()
    println(original)
    val sorted = nums.sorted()
    println(nums)
    println(sorted)
    println(nums == sorted)
    println(nums === sorted)


}

/*
*
* Problem B (Hardish): Implement groupContinous:
* given a List<Int>, return List<List<Int>> of maximal contiguous runs of equal elements.
*  Example: [1,1,2,2,2,3,1,1] → [[1,1],[2,2,2],[3],[1,1]]. Do not use chunked or windowed.
* Write idiomatic Kotlin and discuss memory.
* */
fun groupContinous(input: List<Int>): List<List<Int>> {
    var start = 0
    val output = mutableListOf<List<Int>>()

    for (i in input.indices) {

        val cv = input[i]
        while (cv != input[start]) {
            output.add(input.subList(start, i))
            start = i
        }
    }

    /*val groupBy = input.groupBy { it }
    groupBy.values.map {
        output.add(it)
    }*/
    output.add(input.subList(start, input.size).toList())
    return output


}