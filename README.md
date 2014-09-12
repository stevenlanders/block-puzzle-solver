This is a sliding block puzzle solver API

Example:

````
//create 4x4 puzzle (GET)
http://blockpuzzle-stevenlanders.rhcloud.com/puzzle/4

//solve puzzle (GET or POST)
http://blockpuzzle-stevenlanders.rhcloud.com/solution?puzzle=[[8,12,3,14],[10,9,11,5],[1,"X",4,6],[2,7,13,15]]
````

Solution Response Example:
````json
{
    "duration": "48ms",
    "puzzle": [
        [8,12,3,14],
        [10,9,11,5],
        [1,"X",4,6],
        [2,7,13,15]
    ],
    "moves": [
        "up",
        "left",
        "down",
        "right",
        "up",
        "up",
        "left",
        "down",
        "down",
        "down",
        "right",
        "up",
        "up",
        "left",
        "down",
        "right",
        "up",
        "left",
        "down",
        "right",
        "right",
        "up",
        "up",
        "left",
        "down",
        "right",
        "down",
        "left",
        "down",
        "right",
        "right",
        "up",
        "left",
        "down",
        "right",
        "up",
        "left",
        "up",
        "left",
        "down",
        "right",
        "right",
        "up",
        "left",
        "up",
        "right",
        "down",
        "down",
        "down",
        "left",
        "up",
        "up",
        "right",
        "down",
        "left",
        "up",
        "up",
        "right",
        "down",
        "left",
        "left",
        "down",
        "right",
        "down",
        "left",
        "left",
        "up",
        "right",
        "up",
        "left",
        "down",
        "right",
        "right",
        "right",
        "down",
        "up",
        "left",
        "left",
        "down",
        "right",
        "up",
        "left",
        "left",
        "down",
        "right",
        "right",
        "right",
        "up",
        "left",
        "down",
        "left",
        "up",
        "right",
        "up",
        "left",
        "down",
        "right",
        "up",
        "right",
        "down",
        "down",
        "left",
        "up",
        "up",
        "right",
        "down",
        "left",
        "down",
        "right",
        "up",
        "left",
        "down",
        "left",
        "up",
        "right",
        "down",
        "right",
        "up",
        "left",
        "left",
        "down",
        "right",
        "up",
        "right",
        "down"
    ]
}
````



Solve examples

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
