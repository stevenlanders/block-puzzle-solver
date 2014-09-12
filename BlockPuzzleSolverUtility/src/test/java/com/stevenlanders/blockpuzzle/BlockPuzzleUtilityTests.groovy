package com.stevenlanders.blockpuzzle

import org.junit.Test

/**
 * Created by stevenlanders on 9/12/14.
 */
class BlockPuzzleUtilityTests {

    @Test
    def void testUtilityParse(){
        BlockPuzzle puzzle = BlockPuzzleUtility.parsePuzzleString("1,2,3,4,5,6,7,8,X")
        assert puzzle.solve().isSolved()
    }

    @Test(expected = IllegalArgumentException)
    def void testUtilityParseDups(){
        BlockPuzzle puzzle = BlockPuzzleUtility.parsePuzzleString("1,1,3,4,5,6,7,8,X")
        assert puzzle.solve().isSolved()
    }

    @Test(expected = IllegalArgumentException)
    def void testUtilityInvalidPuzzle(){
        BlockPuzzle puzzle = BlockPuzzleUtility.parsePuzzleString("2,1,3,4,5,6,7,8,X")
        assert puzzle.solve().isSolved()
    }
}
