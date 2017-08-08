package org.stevesea.matching_engine

import org.springframework.stereotype.Service
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

@Service
class MatchingEngineService(val book: Book = Book()) {
    val lock = ReentrantReadWriteLock()

    fun book(): Book {
        lock.read {
            return book.copy()
        }
    }

    fun buy(buyRequest: Order) {
        lock.write {
            var toBeBought = buyRequest.qty

            val it = book.sells.iterator()
            while (it.hasNext() && toBeBought > 0) {
                val sellOrder = it.next()
                if (sellOrder.prc > buyRequest.prc)
                    break

                val sellQty = sellOrder.qty
                if (sellQty >= toBeBought) {
                    sellOrder.qty -= toBeBought
                    toBeBought = 0
                } else {
                    sellOrder.qty = 0
                    toBeBought -= sellQty
                }
                if (sellOrder.qty == 0) {
                    it.remove()
                }
            }
            if (toBeBought > 0) {
                book.buys.add(Order(toBeBought, buyRequest.prc))
            }
        }
    }

    fun sell(sellRequest: Order)
    {
        lock.write {
            var toBeSold = sellRequest.qty

            val it = book.buys.iterator()
            while (it.hasNext() && toBeSold > 0) {
                val buyOrder = it.next()
                if (buyOrder.prc < sellRequest.prc)
                    break

                val buyQty = buyOrder.qty
                if (buyQty >= toBeSold) {
                    buyOrder.qty -= toBeSold
                    toBeSold = 0
                } else {
                    buyOrder.qty = 0
                    toBeSold -= buyQty
                }
                if (buyOrder.qty == 0) {
                    it.remove()
                }
            }
            if (toBeSold > 0) {
                book.sells.add(Order(toBeSold, sellRequest.prc))
            }
        }
    }
}