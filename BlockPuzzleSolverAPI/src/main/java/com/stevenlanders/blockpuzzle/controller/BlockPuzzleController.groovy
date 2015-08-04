package com.stevenlanders.blockpuzzle.controller

import com.stevenlanders.blockpuzzle.BlockPuzzle
import com.stevenlanders.blockpuzzle.BlockPuzzleUtility
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.ModelAndView

/**
 * Created by stevenlanders on 9/12/14.
 */

@Controller
class BlockPuzzleController {

    private static final int DEFAULT_PUZZLE_SIZE = 4

    @RequestMapping(value="/rest/solution", method = [RequestMethod.GET,RequestMethod.POST])
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


    @RequestMapping(["/rest/puzzle","/rest/puzzle/"])
    def @ResponseBody getPuzzleDefault(){
        BlockPuzzle blockPuzzle = BlockPuzzle.generateShuffled(DEFAULT_PUZZLE_SIZE)
        return [puzzle: blockPuzzle.getPuzzle()]
    }

    @RequestMapping("/rest/puzzle/{size}")
    def @ResponseBody getPuzzle(@PathVariable Integer size){
        if(size > 1000) return [ error: "please don't go over 1000...thanks"]
        BlockPuzzle blockPuzzle = BlockPuzzle.generate(size).shuffle()
        return [puzzle: blockPuzzle.getPuzzle()]
    }

    @RequestMapping("/puzzle")
    def String getPuzzlePage(){
        return "puzzle"
    }

}
