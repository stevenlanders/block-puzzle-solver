package com.stevenlanders.blockpuzzle.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

/**
 * Created by stevenlanders on 9/12/14.
 */

@Controller
@RequestMapping("/blockpuzzle")
class BlockPuzzleController {

    @RequestMapping("/solution")
    def String getSolution(@RequestParam String puzzle){

        throw new UnsupportedOperationException("not implemented")

    }

}
