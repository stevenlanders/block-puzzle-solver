package com.stevenlanders.blockpuzzle

import org.junit.Test

/**
 * Created by stevenlanders on 9/8/14.
 */
class BlockPuzzleTests {

    @Test
    def void testCanary() {
        assert true
    }

    @Test
    def void testOptimizeMoves(){
        BlockPuzzle puzzle = BlockPuzzle.generate4x4()
        puzzle.moves = ["up","down","left","right","up"]
        puzzle.optimizeMoves()
        assert puzzle.moves == ["up"]

    }

    @Test
    def void stageFourTest() {
        def list = [[1, 2]]
        assert list.contains([1, 2])
    }

    @Test
    def void testReset(){
       def puzzle =  BlockPuzzle.generate4x4().shuffle()
        puzzle.solve()
        def moves = puzzle.getMoves()
        puzzle.reset()
        assert moves != puzzle.getMoves()
        puzzle.solve()
        assert moves == puzzle.getMoves()
    }

    @Test
    def void generatePuzzle(){
        BlockPuzzle puzzle = BlockPuzzle.generate4x4()
        assert puzzle.puzzle == [
                [1,2,3,4],
                [5,6,7,8],
                [9,10,11,12],
                [13,14,15,null]
        ]
        assert puzzle.emptySpot == [3,3]
    }

    @Test
    def void testSolve10x10(){
        assert BlockPuzzle.generate(10).shuffle().solve()
    }

    @Test
    def void testSolveDifficultColumn(){
        assert new BlockPuzzle(
                puzzle: [[1, 2, 3, 4], [5, 9, 6, 15], [null, 8, 10, 12], [11, 14, 7, 13]]
        ).solve().isSolved()
    }

    @Test
    def void solveAndPlayback(){
        def puzzle = BlockPuzzle.generate4x4().shuffle().solve()
        def moves = puzzle.getMoves()
        puzzle.reset()
        assert !puzzle.isSolved()
        moves.each{move->
            puzzle.move(move)
            puzzle.printPuzzle()
        }
        assert puzzle.isSolved()
    }

    @Test
    def void gotoSpotWithLockInWay(){
        BlockPuzzle puzzle = new BlockPuzzle(
                puzzle: [[1, 13, 9, 5], [null, 2, 14, 6], [12, 4, 3, 11], [10, 8, 7, 15]]
        )
        puzzle.lockSpot(0,0)
        puzzle.moveVal(2, BlockPuzzle.DIRECTION_UP)
        assert puzzle.findVal(null) == [1,1]
        assert puzzle.findVal(2) == [0,1]
    }

    @Test
    def void testGetAdjacentNodes(){
        BlockPuzzle puzzle = new BlockPuzzle(
                puzzle: [[1, 13, 9, 5], [null, 2, 14, 6], [12, 4, 3, 11], [10, 8, 7, 15]]
        )
        assert puzzle.getAdjacentNodes(0,0).collect{it.spot} == [[0,1],[1,0]]
        puzzle.lockSpot(1,0)
        assert puzzle.getAdjacentNodes(0,0).collect{it.spot} == [[0,1]]
    }

    @Test
    def void testSolveCrappyRow(){
        BlockPuzzle puzzle = new BlockPuzzle(
                puzzle: [[1, 13, 9, 5], [null, 2, 14, 6], [12, 4, 3, 11], [10, 8, 7, 15]]
        )
        assert puzzle.solve().isSolved()
        assert puzzle.originalPuzzle == [[1, 13, 9, 5], [null, 2, 14, 6], [12, 4, 3, 11], [10, 8, 7, 15]]
        puzzle.shuffle()
        assert puzzle.puzzle == puzzle.originalPuzzle
    }

    @Test
    def void testReflectiveMoveGood(){
        BlockPuzzle puzzle = new BlockPuzzle(
                puzzle: [[1, 13, 9, 5], [null, 2, 14, 6], [12, 4, 3, 11], [10, 8, 7, 15]]
        )
        puzzle.move("down")
        assert puzzle.puzzle == [[1, 13, 9, 5], [12, 2, 14, 6], [null, 4, 3, 11], [10, 8, 7, 15]]
    }

    @Test(expected = IllegalArgumentException)
    def void testReflectiveMoveBad(){
        BlockPuzzle puzzle = new BlockPuzzle(
                puzzle: [[1, 13, 9, 5], [null, 2, 14, 6], [12, 4, 3, 11], [10, 8, 7, 15]]
        )
        puzzle.move("down1")
        assert puzzle.puzzle == [[1, 13, 9, 5], [12, 2, 14, 6], [null, 4, 3, 11], [10, 8, 7, 15]]
    }

    @Test
    def void findEasyPath(){
        BlockPuzzle puzzle = new BlockPuzzle(
                puzzle: [[1, 13, 9, 5], [null, 2, 14, 6], [12, 4, 3, 11], [10, 8, 7, 15]]
        )

        def easyPath = puzzle.findNodePath(0,1,0,0);
        assert easyPath.size() == 1
        assert easyPath.first().spot == [0,0]
    }

    @Test
    def void findHardPath(){
        BlockPuzzle puzzle = new BlockPuzzle(
                puzzle: [[1, 13, 9, 5], [null, 2, 14, 6], [12, 4, 3, 11], [10, 8, 7, 15]]
        )
        puzzle.lockSpot(2,1)
        def easyPath = puzzle.findNodePath(0,1,3,1);
        assert easyPath.size() == 5
        assert easyPath.collect{it.spot} == [[1,1],[1,2],[2,2],[3,2],[3,1]]
    }

    @Test
    def void testSolve(){
        (0..100).each {
            assert BlockPuzzle.generate4x4().shuffle().solve()
        }
    }

    @Test
    def void testGenerateSolve3x3(){
        assert BlockPuzzle.generate(3).shuffle().solve().isSolved()
    }

    @Test
    def void testForMoveLeft(){
        BlockPuzzle puzzle = new BlockPuzzle(
                puzzle: [[1, 2, 3, 4, 5], [6, 19, 18, 22, null], [7, 11, 23, 24, 8], [16, 17, 15, 9, 10], [21, 14, 20, 12, 13]]
        )
        puzzle.lockedRows.add(0)
        puzzle.lockedSpots = [[0,0],[1,0]]
        puzzle.moveVal(11, BlockPuzzle.DIRECTION_LEFT)
        def locationFor11 = puzzle.findVal(11)
        assert locationFor11 == [2,0]
    }

    @Test
    def void testFindPathFor5x5(){
        BlockPuzzle puzzle = new BlockPuzzle(
                puzzle: [[1, 2, 3, 4, 5], [6, 19, 18, 22, 8], [7, 11, 23, 9, 24], [16, 17, 20, 15, 10], [21, null, 14, 12, 13]]
        )
        assert puzzle.solve().isSolved()


    }

    @Test
    def void testGenerateSolve5x5(){
        (0..10).each {
            BlockPuzzle puzzle = BlockPuzzle.generate(5)
            puzzle.shuffle()
            assert puzzle.solve().isSolved()
        }
    }

    @Test
    def void testBad5x5(){
        BlockPuzzle puzzle = new BlockPuzzle(
                puzzle: [[11, 5, null, 9, 8], [19, 23, 21, 10, 3], [4, 20, 12, 22, 7], [16, 15, 1, 6, 17], [24, 13, 14, 2, 18]]
        )
        assert puzzle.solve().isSolved()
    }

    @Test
    def void testGenerateSolve(){
        BlockPuzzle puzzle = BlockPuzzle.generate4x4().shuffle()
        assert puzzle.solve().isSolved()
        assert puzzle.puzzle == [
                [1,2,3,4],
                [5,6,7,8],
                [9,10,11,12],
                [13,14,15,null]
        ]
    }

    @Test
    def void testGotoSpot(){
        BlockPuzzle puzzle = new BlockPuzzle(
                puzzle: [
                        [null,2,3,4],
                        [1,6,7,8],
                        [5,10,11,12],
                        [9,13,14,15]
                ]
        )
        puzzle.gotoSpot(0,3)
        assert puzzle.findVal(null) == [0,3]
    }

    @Test
    def void testHuge(){
        assert BlockPuzzle.generate(20).shuffle().solve().isSolved()
    }

    @Test
    def void testSolveColumn(){
        BlockPuzzle puzzle = new BlockPuzzle(
                puzzle: [
                        [2,3,11,null],
                        [1,6,8,4],
                        [5,10,12,7],
                        [9,13,14,15]
                ]
        )
        puzzle.solveRow(0)
        assert puzzle.puzzle[0] == [1,2,3,4]
        puzzle.solveColumn(0)
        puzzle.puzzle.collect{it[0]} == [1,5,9,13]
    }

    @Test
    def void testSolveSecondRow(){
        BlockPuzzle puzzle = new BlockPuzzle(
                puzzle: [
                        [1,2,3,4],
                        [5,null,14,8],
                        [9,7,15,11],
                        [13,6,12,10]
                ]
        )
        puzzle.solveRow(1)
        assert puzzle.puzzle[0] == [1,2,3,4]
        assert puzzle.puzzle[1] == [5,6,7,8]
    }


    @Test
    def void testMoveValLeft(){
        BlockPuzzle puzzle = new BlockPuzzle(
                puzzle: [
                        [null,2,3,4],
                        [1,6,7,8],
                        [5,10,11,12],
                        [9,13,14,15]
                ]
        )
        puzzle.moveVal(7, BlockPuzzle.DIRECTION_LEFT)
        assert puzzle.puzzle == [
                [2,6,3,4],
                [1,7,null,8],
                [5,10,11,12],
                [9,13,14,15]
        ]
    }

    @Test
    def void testMoveValRight(){
        BlockPuzzle puzzle = new BlockPuzzle(
                puzzle: [
                        [null,2,3,4],
                        [1,6,7,8],
                        [5,10,11,12],
                        [9,13,14,15]
                ]
        )
        puzzle.moveVal(7, BlockPuzzle.DIRECTION_RIGHT)
        assert puzzle.puzzle == [
                [2,3,4,8],
                [1,6,null,7],
                [5,10,11,12],
                [9,13,14,15]
        ]
    }

    @Test
    def void testMoveValRightFarRight(){
        BlockPuzzle puzzle = new BlockPuzzle(
                puzzle: [
                        [2,3,4,8],
                        [1,6,7,null],
                        [5,10,11,12],
                        [9,13,14,15]
                ]
        )
        puzzle.moveVal(7, BlockPuzzle.DIRECTION_RIGHT)
        assert puzzle.puzzle == [
                [2,3,4,8],
                [1,6,null,7],
                [5,10,11,12],
                [9,13,14,15]
        ]
    }

    @Test
    def void testLeftInit(){
        BlockPuzzle puzzle = BlockPuzzle.generate4x4()
        assert puzzle.left()
        assert puzzle.puzzle == [
                [1,2,3,4],
                [5,6,7,8],
                [9,10,11,12],
                [13,14,null,15]
        ]
        assert puzzle.left()
        assert puzzle.puzzle == [
                [1,2,3,4],
                [5,6,7,8],
                [9,10,11,12],
                [13,null,14,15]
        ]
        assert puzzle.left()
        assert puzzle.puzzle == [
                [1,2,3,4],
                [5,6,7,8],
                [9,10,11,12],
                [null,13,14,15]
        ]
        assert !puzzle.left()
        assert puzzle.puzzle == [
                [1,2,3,4],
                [5,6,7,8],
                [9,10,11,12],
                [null,13,14,15]
        ]
    }

    @Test
    def void testDown() {
        BlockPuzzle puzzle = new BlockPuzzle(
                puzzle: [
                        [null,2,3,4],
                        [1,6,7,8],
                        [5,10,11,12],
                        [9,13,14,15]
                ]
        )
        assert puzzle.down()
        assert puzzle.puzzle == [
                [1,2,3,4],
                [null,6,7,8],
                [5,10,11,12],
                [9,13,14,15]
        ]
        assert puzzle.down()
        assert puzzle.puzzle == [
                [1,2,3,4],
                [5,6,7,8],
                [null,10,11,12],
                [9,13,14,15]
        ]
        assert puzzle.down()
        assert puzzle.puzzle == [
                [1,2,3,4],
                [5,6,7,8],
                [9,10,11,12],
                [null,13,14,15]
        ]
        assert !puzzle.down()
        assert puzzle.puzzle == [
                [1,2,3,4],
                [5,6,7,8],
                [9,10,11,12],
                [null,13,14,15]
        ]
    }

    @Test
    def void testUp(){
        BlockPuzzle puzzle = new BlockPuzzle(
                puzzle: [
                        [1,2,3,4],
                        [5,6,7,8],
                        [9,10,11,12],
                        [null,13,14,15]
                ]
        )
        assert puzzle.up()
        assert puzzle.puzzle == [
                [1,2,3,4],
                [5,6,7,8],
                [null,10,11,12],
                [9,13,14,15]
        ]
        assert puzzle.up()
        assert puzzle.puzzle == [
                [1,2,3,4],
                [null,6,7,8],
                [5,10,11,12],
                [9,13,14,15]
        ]
        assert puzzle.up()
        assert puzzle.puzzle == [
                [null,2,3,4],
                [1,6,7,8],
                [5,10,11,12],
                [9,13,14,15]
        ]
        assert !puzzle.up()
        assert puzzle.puzzle == [
                [null,2,3,4],
                [1,6,7,8],
                [5,10,11,12],
                [9,13,14,15]
        ]
    }

    @Test
    def void testRightInit(){
        BlockPuzzle puzzle = new BlockPuzzle(
                puzzle: [
                        [1,2,3,4],
                        [5,6,7,8],
                        [9,10,11,12],
                        [null,13,14,15]
                ]
        )

        assert puzzle.right()
        assert puzzle.puzzle == [
                [1,2,3,4],
                [5,6,7,8],
                [9,10,11,12],
                [13,null,14,15]
        ]
        assert puzzle.right()
        assert puzzle.puzzle == [
                [1,2,3,4],
                [5,6,7,8],
                [9,10,11,12],
                [13,14,null,15]
        ]
        assert puzzle.right()
        assert puzzle.puzzle == [
                [1,2,3,4],
                [5,6,7,8],
                [9,10,11,12],
                [13,14,15,null]
        ]
        assert !puzzle.right()
        assert puzzle.puzzle == [
                [1,2,3,4],
                [5,6,7,8],
                [9,10,11,12],
                [13,14,15,null]
        ]
        assert puzzle.findVal(13) == [3,0]
    }

    @Test
    def void generate5x5Puzzle(){
        BlockPuzzle puzzle = BlockPuzzle.generate(5)
        assert puzzle.puzzle == [
                [1,2,3,4,5],
                [6,7,8,9,10],
                [11,12,13,14,15],
                [16,17,18,19,20],
                [21,22,23,24,null]
        ]
        assert puzzle.emptySpot == [4,4]
        assert puzzle.puzzle[1][3] == 9
    }

}
