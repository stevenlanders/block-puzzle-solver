package com.stevenlanders.blockpuzzle.controller

import com.stevenlanders.blockpuzzle.BlockPuzzle
import com.stevenlanders.blockpuzzle.BlockPuzzleUtility
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Created by stevenlanders on 9/12/14.
 */

@RestController
@RequestMapping("/blockpuzzle")
class BlockPuzzleController {

    private static final int DEFAULT_PUZZLE_SIZE = 4

    @RequestMapping("/solution")
    def getSolution(@RequestParam String puzzle){
        try{
            BlockPuzzle blockPuzzle = BlockPuzzleUtility.parsePuzzleString(puzzle)
            blockPuzzle.solve()
            def moves = blockPuzzle.getMoves()
            [
                    moves: moves
            ]
        }catch(IllegalArgumentException iae){
            return "invalid"
        }
    }

    @RequestMapping(["/",""])
    def getPuzzleDefault(){
        return getPuzzle(DEFAULT_PUZZLE_SIZE)
    }

    @RequestMapping("/{size}")
    def getPuzzle(@PathVariable Integer size){
        BlockPuzzle blockPuzzle = BlockPuzzle.generate(size).shuffle()
        return [puzzle: blockPuzzle.toString()]
    }

}
