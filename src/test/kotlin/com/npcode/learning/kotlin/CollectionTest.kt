package com.npcode.learning.kotlin

import io.kotest.matchers.shouldBe
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.util.LinkedList
import kotlin.system.measureTimeMillis

class CollectionTest {

    data class Field(val a: Int, val b: Int)

    @Test
    fun sortedByBoolean() {
        // false가 true보다 우선순위가 높다
        assertThat(listOf(1, 2, 3).sortedBy { it == 3 }).isEqualTo(listOf(1, 2, 3))
        assertThat(listOf(1, 2, 3).sortedBy { it != 3 }).isEqualTo(listOf(3, 1, 2))
    }

    @Test
    fun sortedByOrdering() {
        val list = listOf(Field(2, 2), Field(1, 2), Field(2, 1))
        val sortedByA = listOf(Field(1, 2), Field(2, 2), Field(2, 1))
        val sortedByB = listOf(Field(2, 1), Field(2, 2), Field(1, 2))
        val sortedByAAndB = listOf(Field(1, 2), Field(2, 1), Field(2, 2))
        val sortedByBAndA = listOf(Field(2, 1), Field(1, 2), Field(2, 2))

        assertThat(list.sortedBy { it.a }).isEqualTo(sortedByA)
        assertThat(list.sortedBy { it.b }).isEqualTo(sortedByB)
        assertThat(list.sortedBy { it.b }.sortedBy { it.a }).isEqualTo(sortedByAAndB)
        assertThat(list.sortedBy { it.a }.sortedBy { it.b }).isEqualTo(sortedByBAndA) // 나중에 나온 것이 우선순위가 높다
    }

    @Test
    fun chainByFold() {
        listOf(1, 2, 3).fold(10) { acc, i ->
            acc + i
        } shouldBe 16
    }

    @Test
    fun toMap() {
        listOf(
            "a" to "b",
            "a" to "c"
        ).groupBy(
            { it.first },
            { it.second }
        )
        .toMap() shouldBe mapOf( "a" to listOf("b", "c") )
    }

    @Test
    fun testTimeComplexity() {
        val seq1 = (1..10000000).asSequence()
        val seq2 = (1..10000000).asSequence()
        val seq3 = (1..10000000).asSequence()
        val list1 = seq1.toList()
        val list2 = seq2.toList()
        val linkedList1 = LinkedList(list1)
        val linkedList2 = LinkedList(list2)
        val persistenceList1 = seq1.toPersistentList()
        val persistenceList2 = seq2.toPersistentList()

        measureTimeMillis {
            list1 + list2
        }.let { println("list1 + list2: $it") }

        // 의외로 제일 느리다 immutable이라서 그런지
        measureTimeMillis {
            linkedList1 + linkedList2
        }.let { println("linkedList1 + linkedList2: $it") }

        measureTimeMillis {
            listOf(1) + linkedList2
        }.let { println("lisOf(1) + linkedList2: $it") }

        measureTimeMillis {
            linkedList2.addFirst(1)
        }.let { println("linkedList2.addFirst(1): $it") }

        // O(1)이긴한데 초기화 오버헤드가 20ms나 된다.
        measureTimeMillis {
            seq1 + seq2
        }.let { println("seq1 + seq2: $it") }

        // O(1) 인 것 같아보인다
        measureTimeMillis {
            sequenceOf(seq1, seq2).flatten()
        }.let { println("sequenceOf(seq1, seq2).flatten(): $it") }

        // 초기화 오버헤드가 있는게 아닌지 궁금해서 한번 더 해본다.
        measureTimeMillis {
            seq1 + seq3
        }.let { println("seq1 + seq3: $it") }

        measureTimeMillis {
            persistenceList1 + persistenceList2
        }.let { println("persistenceList1 + persistenceList2: $it") }

        measureTimeMillis {
            list1.last()
        }.let { println("list1.last(): $it") }

        measureTimeMillis {
            seq1.last()
        }.let { println("seq1.last(): $it") }

        /*
        measureTimeMillis {
            seq1.last()
        }.let { println("시퀀스 last() 한번 더: $it") }
         */

        measureTimeMillis {
            linkedList1.first()
        }.let { println("linkedList1.first(): $it") }

        measureTimeMillis {
            linkedList1.last()
        }.let { println("linkedList1.last(): $it") }

        measureTimeMillis {
            seq1.first()
        }.let { println("seq1.first(): $it") }

        measureTimeMillis {
            seq1.drop(1)
        }.let { println("seq1.drop(1): $it") }

        measureTimeMillis {
            (seq1 + sequenceOf(1)).last()
        }.let { println("(seq1 + sequenceOf(1)).last(): $it") }

        measureTimeMillis {
            persistenceList1.add(1)
        }.let { println("persistenceList1.add(1): $it") }

        measureTimeMillis {
            persistenceList1.first()
        }.let { println("persistenceList1.first(): $it") }
    }

    @Test
    fun testSequenceFirst() {
        val seq1 = (1..10000000).asSequence()
        measureTimeMillis {
            seq1.drop(1).first()
        }.let { println("seq1.first(): $it") }
    }

    @Test
    fun testSequenceToList() {
        val seq1 = (1..10000000).asSequence()
        measureTimeMillis {
            seq1.toList()
        }
    }

    @Test
    fun testFoldSequenceAndToList() {
        // 이런 형태로 만들어진 sequence를 나중에 toList 하는 것은 굉장히 느리다.
        // 한 O(n^3) 쯤 되는 느낌
        val seq1 = (1..1000).fold(emptySequence<Int>()) { acc, i ->
            sequenceOf(i) + acc
        }

        // 빠르지만 아주 빠르진 않고 stack overflow가 발생한다
        val seq2 = (1..100000).fold(emptySequence<Int>()) { acc, i ->
            sequence {
                yield(i)
                yieldAll(acc)
            }
        }

        val seq3 = (1..1000).fold(emptySequence<Int>()) { acc, i ->
            sequenceOf(sequenceOf(i), acc).flatten()
        }

        // stackoverflow
        val flow1 = (1..10000).fold(emptyFlow<Int>()) { acc, i ->
            flow {
                emit(i)
                emitAll(acc)
            }
        }

        // 느리다. O(n^3)
        lateinit var ls1: List<Int>
        measureTimeMillis {
            ls1 = seq1.toList()
        }.let { println("size=1인 sequence 1천개를 concat으로 이은 다음 toList: $it") }

        // 오히려 mutable이 빠르다던가?
        lateinit var ls2: List<Int>
        measureTimeMillis {
            ls2 = seq1.toMutableList()
        }.let { println("size=1인 sequence 1천개를 concat으로 이은 다음 toMutableList: $it") }

        /*
        lateinit var ls2: List<Int>
        measureTimeMillis {
            ls2 = seq2.toList()
        }.let { println("size=1인 sequence 10만개를 yield와 yieldAll로 출력하는 sequence를 toList: $it") }
         */

        // concat으로 잇는것과 동일하게 느리다. O(n^3) 같음
        lateinit var ls3: List<Int>
        measureTimeMillis {
            ls3 = seq3.toList()
        }.let { println("size=1인 sequence 1천개를 연결해서 flatten 후 toList: $it") }

        // O(n^2). 스택오버플로우 없음
        measureTimeMillis {
            (1..10000).fold(emptyList<Int>()) { acc, i ->
                listOf(i) + acc
            }
        }.let { println("size=1인 list 1만개를 연결: $it") }

        measureTimeMillis {
            (1..100000).fold(persistentListOf<Int>()) { acc, i ->
                acc.add(i)
            }.toList()
        }.let { println("persistenceList에 item 10만개를 insert 한 뒤 toList: $it") }

        measureTimeMillis {
            (1..100000).fold(mutableListOf<Int>()) { acc, i ->
                acc.add(i)
                acc
            }.toList()
        }.let { println("mutableList에 item 10만개를 insert 한 뒤 toList: $it") }

        /*
        lateinit var ls4: List<Int>
        measureTimeMillis {
            runBlocking {
                ls4 = flow1.toList()
            }
        }.let { println("size=1인 flow 1만개를 emit과 emitAll로 출력한느 flow를 toList: $it") }
         */

        // ls1 shouldBe ls2
    }

    @Test
    fun customLinkedList() {
        data class Node(
            val value: Int,
            val next: Node?
        ) {
            fun toList(): List<Int> {
                val result = mutableListOf<Int>()
                var cursor: Node? = this

                while (cursor != null) {
                    result.add(cursor.value)
                    cursor = cursor.next
                }

                return result
            }
        }

        measureTimeMillis {
            val head = (1..10000).fold(Node(0, null)) { head, item ->
                Node(item, head)
            }

            head.toList()
        }.let { println("커스텀 데이터타입 $it") }
    }

    /*
    @Test
    fun testPersistentList() {
        val ls = persistentListOf<Int>()
        val ls2 = ls.add(1)
        ls.size shouldBe 0
        ls2.size shouldBe 1
        val ls = (1..1000000).toList().toPersistentList()
    }
     */
}
