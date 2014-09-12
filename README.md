This is a quick groovy sliding block puzzle solver.

```groovy

def puzzle = BlockPuzzle.generate4x4().shuffle()
puzzle.solve()

puzzle.getMoves().each{move->
  println(move)
}

```

Examples:
```groovy
def puzzle = BlockPuzzle.generate(5).shuffle() //generate 5x5 puzzle
puzzle.printPuzzle() //prints array to system-out
puzzle.getPuzzle()  //returns 2 dimensional array of puzzle

//solve specific puzzle:
def stagedPuzzle = new BlockPuzzle(
  puzzle: [[1, 13, 9, 5], [null, 2, 14, 6], [12, 4, 3, 11], [10, 8, 7, 15]]
).solve()

stagedPuzzle.getMoves().each{move->
  println(move)
}

//solve, reset, and playback moves against unsolved puzzle
def puzzle = BlockPuzzle.generate4x4().shuffle().solve()
def moves = puzzle.getMoves()
puzzle.reset()
moves.each{move->
    puzzle.move(move)
    puzzle.printPuzzle()
}

```

Algorithm Summary:

Starting at top left corner, for each row/column pair, solve each ROW then COLUMN, until puzzle is solved at the bottom right square.  This does not find the shortest path, but runs at a reasonable complexity (solving 50x50 takes about 4 seconds)
