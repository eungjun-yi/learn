package com.npcode.learning.kotlin

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.lang.ref.PhantomReference
import java.lang.ref.ReferenceQueue
import java.lang.ref.SoftReference
import java.lang.ref.WeakReference


class ReferencesTest {

    @Test
    fun test() {
        var weak: String? = "hello"
        var soft: String? = "hello"
        var phantom: String? = "hello"

        val queue = ReferenceQueue<String?>()
        val weakRef = WeakReference(weak)
        val softRef = SoftReference(soft)
        val phantomRef = PhantomReference(phantom, queue)

        // WeakReference는 참조하는 object가 finalized 되는 것을 막지 못하는 참조이다.

        // SoftReference는 메모리가 부족한 경우 clear된다.
        // 그럼 결국 WeakReference가 SoftReference보다 더 약한건가? 적어도 SoftReference는 메모리가 부족한
        // 경우에만 finalized 되는걸 방치하는거니

        // PhantomReference는 get() 하면 항상 null이다. 따라서 참조 자체는 있지만 그 참조의 주체는 없다...
        // PhantomReference로 참조된 object는 그냥 finalize 되지 않는다는 것 같은데? reference queue를 순회해서 처리해줘야하나?
        // enqueue 되는 건 이제 finalize 해도 된다는 의미인건가?
        //
        // JDK8 문서에는 그냥 reachability changes가 detected되면 append 되는 queue라고 한다:
        //
        // Reference queues, to which registered reference objects are appended by the garbage
        // collector after the appropriate reachability changes are detected.

        assertThat(weakRef.get()).isEqualTo(weak)
        assertThat(softRef.get()).isEqualTo(soft)
        assertThat(phantomRef.get()).isEqualTo(null)
    }
}
