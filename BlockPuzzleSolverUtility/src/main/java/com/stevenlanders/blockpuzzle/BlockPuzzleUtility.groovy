package com.stevenlanders.blockpuzzle

import groovy.json.JsonSlurper


/**
 * Created by stevenlanders on 9/12/14.
 */
class BlockPuzzleUtility {

    static def parsePuzzleString(String puzzleStr){
        def puzzleArray = new JsonSlurper().parseText(puzzleStr)
        BlockPuzzle puzzle = new BlockPuzzle(
                puzzle: puzzleArray
        )
        try {
            puzzle.solve();
        }catch(IllegalStateException ise){
            throw new IllegalArgumentException("invalid puzzle")
        }
        puzzle.reset();
        return puzzle;
    }

}
