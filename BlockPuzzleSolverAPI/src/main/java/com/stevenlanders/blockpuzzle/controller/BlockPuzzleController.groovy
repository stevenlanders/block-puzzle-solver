package com.stevenlanders.blockpuzzle.controller

import com.stevenlanders.blockpuzzle.BlockPuzzle
import com.stevenlanders.blockpuzzle.BlockPuzzleUtility
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

/**
 * Created by stevenlanders on 9/12/14.
 */

@RestController
class BlockPuzzleController {

    private static final int DEFAULT_PUZZLE_SIZE = 4

    @RequestMapping(value="/solution", method = [RequestMethod.GET,RequestMethod.POST])
    def @ResponseBody getSolution(@RequestParam String puzzle){
        try{
            BlockPuzzle blockPuzzle = BlockPuzzleUtility.parsePuzzleString(puzzle)
            long start = System.currentTimeMillis()
            blockPuzzle.solve()
            long duration = System.currentTimeMillis()-start
            def moves = blockPuzzle.getMoves()
            [
                    puzzle: blockPuzzle.getOriginalPuzzle(),
                    moves: moves,
                    duration: duration+"ms"
            ]
        }catch(IllegalArgumentException iae){
            return "invalid puzzle"
        }
    }

    @RequestMapping(["/puzzle","/puzzle/"])
    def @ResponseBody getPuzzleDefault(){
        BlockPuzzle blockPuzzle = BlockPuzzle.generate(DEFAULT_PUZZLE_SIZE).shuffle()
        return [puzzle: blockPuzzle.getPuzzle()]
    }

    @RequestMapping("/puzzle/{size}")
    def @ResponseBody getPuzzle(@PathVariable Integer size){
        if(size > 1000) return [ error: "please don't go over 1000...thanks"]
        BlockPuzzle blockPuzzle = BlockPuzzle.generate(size).shuffle()
        return [puzzle: blockPuzzle.getPuzzle()]
    }

}
