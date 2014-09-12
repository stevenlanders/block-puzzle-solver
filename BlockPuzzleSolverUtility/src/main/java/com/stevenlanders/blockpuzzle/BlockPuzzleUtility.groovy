package com.stevenlanders.blockpuzzle

/**
 * Created by stevenlanders on 9/12/14.
 */
class BlockPuzzleUtility {

    static def parsePuzzleString(String puzzleStr){
        Object[][] bPuzzle = puzzleStringToArray(puzzleStr)
        BlockPuzzle puzzle = new BlockPuzzle(
                puzzle: bPuzzle
        )
        try {
            puzzle.solve();
        }catch(IllegalStateException ise){
            throw new IllegalArgumentException("invalid puzzle")
        }
        puzzle.reset();
        return puzzle;
    }

    private static Object[][] puzzleStringToArray(String puzzleStr) {
        String[] puzzleArray = puzzleStr.split(",")
        double edgeSize = Math.sqrt(puzzleArray.length)
        int edgeSizeInt = (int) edgeSize
        if (edgeSize > edgeSizeInt) {
            throw new IllegalArgumentException("${edgeSize} ${edgeSizeInt} Must be square in the format with X as empty space.  For example, a 3x3 puzzle: 1,2,3,4,5,6,7,8,X")
        }
        def bPuzzle = new Object[edgeSize][edgeSize]
        puzzleArray.eachWithIndex { String entry, int i ->
            int row = i % edgeSize
            int col = i / edgeSize
            bPuzzle[col][row] = (entry.toLowerCase() == 'x') ? null : Integer.parseInt(entry)
        }
        bPuzzle
    }

}
