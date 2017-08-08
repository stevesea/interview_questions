package org.stevesea.matching_engine.rest

import org.springframework.web.bind.annotation.*
import org.stevesea.matching_engine.MatchingEngineService
import org.stevesea.matching_engine.Order


@RestController
class MatchingEngineController(val engineService: MatchingEngineService) {

    @GetMapping("/book")
    fun getBook() = engineService.book()

    @PostMapping("/buy")
    fun postBuy(@RequestBody order: Order) = engineService.buy(order)

    @PostMapping("/sell")
    fun postSell(@RequestBody order: Order) = engineService.sell(order)
}
