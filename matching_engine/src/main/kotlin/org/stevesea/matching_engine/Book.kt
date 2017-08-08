package org.stevesea.matching_engine

import com.google.common.collect.ComparisonChain
import com.google.common.collect.Ordering
import com.google.common.collect.SortedMultiset
import com.google.common.collect.TreeMultiset


data class Order(var qty: Int, val prc: Double)

class OrderPriceAscendingComparator : Comparator<Order> {
    override fun compare(lhs: Order?, rhs: Order?): Int {
        return ComparisonChain.start()
                .compare(lhs?.prc, rhs?.prc, Ordering.natural<Double>().nullsLast())
                .result()
    }
}

class OrderPriceDescendingComparator : Comparator<Order> {
    override fun compare(lhs: Order?, rhs: Order?): Int {
        return ComparisonChain.start()
                .compare(rhs?.prc, lhs?.prc, Ordering.natural<Double>().nullsLast())
                .result()
    }
}

// using multiset because multiple orders of same prc/qty should be valid.
// in real-life, there'd probably be some sort of refID on each order too
data class Book(val buys: SortedMultiset<Order> = TreeMultiset.create<Order>(OrderPriceDescendingComparator()),
                val sells: SortedMultiset<Order> = TreeMultiset.create<Order>(OrderPriceAscendingComparator()))