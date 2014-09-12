package com.stevenlanders.blockpuzzle

/**
 * Created by stevenlanders on 9/8/14.
 */
class BlockPuzzle {

    private static final int X = 0;
    private static final int Y = 1;
    def puzzle;
    def originalPuzzle;
    private def solvedPuzzle;
    private def edgeSize;
    private def emptySpot = [];
    private def lockedRows = [];
    private def lockedColumns = [];
    private def lockedSpots=[];
    def moves = [];
    public static final String EMPTY_SPOT_VALUE = "X"

    public static final String DIRECTION_UP = "up"
    public static final String DIRECTION_DOWN = "down"
    public static final String DIRECTION_LEFT = "left"
    public static final String DIRECTION_RIGHT = "right"

    static def DELTA_MAP = [
            up : [delta: [-1, 0], opposite: DIRECTION_DOWN],
            down : [delta: [1, 0], opposite: DIRECTION_UP],
            left : [delta: [0, -1], opposite: DIRECTION_RIGHT],
            right : [delta: [0, 1], opposite: DIRECTION_LEFT]
    ]


    def reset(){
        this.puzzle = copyArray(originalPuzzle)
        this.lockedRows = []
        this.lockedColumns = []
        this.lockedSpots = []
        this.moves = [];
        emptySpot = findVal(EMPTY_SPOT_VALUE)
    }

    def solve(){
        (0..(edgeSize-3)).each{cornerIndex->
            solveEdges(cornerIndex)
        }
        if(!isSolved()) crunchLastSquare()
        optimizeMoves()
        return this;
    }

    //remove left-right, right-left, up-down, down-up, as these are no-ops
    private def optimizeMoves(){
        StringBuilder sb = new StringBuilder()
        moves.each{move->
            sb.append(move).append(" ")
        }
        def moveString = sb.toString().trim()
        moveString = removeDirectOppositeMoves(moveString)

        moves.clear()
        moves.addAll(moveString.split(" "))
    }

    private static String removeDirectOppositeMoves(moveString) {
        DELTA_MAP.keySet().each { direction ->
            moveString = moveString.replaceAll("${direction} ${DELTA_MAP.get(direction).opposite}", "")
        }
        moveString = moveString.replaceAll("  +"," ").trim()
        return moveString
    }

    def isSolved(){
        return puzzle == solvedPuzzle
    }

    def down(){
        return move(DIRECTION_DOWN)
    }

    def up(){
        return move(DIRECTION_UP)
    }

    def right(){
        return move(DIRECTION_RIGHT)
    }

    def left(){
        return move(DIRECTION_LEFT)
    }

    private def crunchLastSquare(){
        gotoSpot(edgeSize-1, edgeSize-1)
        int protection = 0;
        while(!isSolved() && protection < 10){
            left()
            up()
            right()
            down()
            protection++;
        }
        if(!isSolved()){
            throw new IllegalArgumentException("Impossible puzzle ${puzzle}")
        }
    }


    private def solveEdges(def edge){
        solveRow(edge)
        solveColumn(edge)
    }

    private def solveRow(def edge){
        (edge..edgeSize-3).each{col->
            def val = solvedPuzzle[edge][col]
            moveValToSpot(val, edge, col)
            lockSpot(edge,col)
        }

        //dig last one out of there
        def lastValInRow = solvedPuzzle[edge][edgeSize - 1]
        moveValToSpot(lastValInRow, edgeSize - 1, edgeSize - 1)

        //stage second to last
        def val = solvedPuzzle[edge][edgeSize - 2]
        moveValToSpot(val, edge, edgeSize-1)
        lockSpot(edge,edgeSize-1)

        //stage last val
        moveValToSpot(lastValInRow, edge+1, edgeSize-1)
        lockSpot(edge+1,edgeSize-1)

        //sweep them in
        gotoSpot(edge, edgeSize-2)
        unlockSpot(edge,edgeSize-1)
        unlockSpot(edge+1,edgeSize-1)
        right()
        down()

        lockedRows.add(edge)
        lockedSpots.clear()
    }

    private def solveColumn(def edge){
        (edge..edgeSize-3).each{row->
            def val = solvedPuzzle[row][edge]
            moveValToSpot(val, row, edge)
            lockSpot(row,edge)
        }

        //dig last one out of there
        def lastValInColumn = solvedPuzzle[edgeSize-1][edge]
        moveValToSpot(lastValInColumn, edgeSize-1, edgeSize-1)

        //stage second to last
        def val = solvedPuzzle[edgeSize - 2][edge]
        moveValToSpot(val, edgeSize-1, edge)
        lockSpot(edgeSize-1, edge)

        //stage last val
        moveValToSpot(lastValInColumn, edgeSize-1, edge+1)
        lockSpot(edgeSize-1,edge+1)

        //sweep them in
        gotoSpot(edgeSize-2, edge)
        unlockSpot(edgeSize-1, edge)
        unlockSpot(edgeSize-1, edge+1)
        down()
        right()

        lockedColumns.add(edge)
        lockedSpots.clear()
    }

    private def moveValToSpot(def val, x, y){
        def location = findVal(val)
        def path = findNodePath(location[X], location[Y], x, y);
        path.each{node->
            moveVal(val, node.moveName)
        }
    }

    private def moveVal(def val, def direction){
        def location = findVal(val)
        def x = location[X]
        def y = location[Y]
        lockSpot(x,y)
        def delta = DELTA_MAP.get(direction)
        def possible = gotoSpot(x+delta.delta[X],y+delta.delta[Y])
        unlockSpot(x,y)
        if(possible) move(delta.opposite)
    }

    private def lockSpot(x,y){
        lockedSpots.add([x,y])
    }

    private def unlockSpot(x,y){
        lockedSpots.remove([x,y])
    }

    private def findNodePath(x1, y1, x2, y2){
        return getPath(x1, y1, x2, y2, [])
    }

    private def getPath(x1, y1, x2, y2, currentPath){
        if([x1,y1] == [x2, y2]){
            return currentPath //done!
        }

        def nextSteps = getAdjacentNodes(x1, y1).findAll{adjacentSpot->
            !currentPath.any{it.spot == adjacentSpot.spot}
        }?.sort{
            getDistance(x2, y2, it.spot[X], it.spot[Y])
        }

        for(def node : nextSteps){
            currentPath.add(node)
            def tempPath = currentPath.clone()
            def path = getPath(node.spot[X], node.spot[Y], x2, y2, tempPath)
            if(path != null){
                return path
            }else{
                currentPath.remove(currentPath.last())
            }
        }
    }

    private static def getDistance(x1, y1, x2, y2){
        Math.sqrt(Math.pow((y2-y1),2) + Math.pow((x2-x1),2))
    }

    private def getAdjacentNodes(x,y){
           [ [spot: [x, y+1], moveName: DIRECTION_RIGHT, move: {right()}],
             [spot: [x, y-1], moveName: DIRECTION_LEFT, move: {left()}],
             [spot: [x+1, y], moveName: DIRECTION_DOWN, move: {down()}],
             [spot: [x-1, y], moveName: DIRECTION_UP, move: {up()}]].findAll{
               isLegalLocation(it.spot[X], it.spot[Y])
           }
    }

    private def gotoSpot(x,y){
        if(x == emptyX() && y == emptyY()){ return true; }
        def path = findNodePath(emptyX(), emptyY(), x, y)
        if(path == null){
            return false
        }
        path.each{
            it.move()
        }
        return true;
    }

    def printPuzzle(){
        println(puzzle)
    }

    private def findVal(def val){
        for(int i=0;i<puzzle.size();i++){
            for(int j =0;j<puzzle.size();j++){
                if(("${puzzle[i][j]}".toLowerCase().equals("${val}".toLowerCase()))){
                    return [i,j]
                }
            }
        }
        throw new IllegalStateException("puzzle couldn't find ${val} when searching")
    }

    def shuffle(){
        if(puzzle == null){
            throw new IllegalStateException("puzzle hasn't been initialized")
        }
        def moveList = [DIRECTION_DOWN, DIRECTION_LEFT, DIRECTION_RIGHT, DIRECTION_UP]
        (0..(Math.max(5000, Math.pow(edgeSize, 3)))).each{
            Random r = new Random()
            move(moveList.get(r.nextInt(moveList.size())))
        }
        this.originalPuzzle = copyArray(puzzle)
        this.moves = []
        return this
    }

    def move(direction){
        def delta = DELTA_MAP.get(direction)
        if(delta == null) {
            throw new IllegalArgumentException("invalid direction: ${direction}")
        }
        def success = swapEmpty(emptyX()+delta.delta[X], emptyY()+delta.delta[Y])
        if(success){
            moves.add(direction)
        }
        return success
    }

    def setPuzzle(def newPuzzle) {
        if (newPuzzle[0].size() != newPuzzle.size()) {
            throw new IllegalArgumentException("Puzzle must be square!")
        }

        this.edgeSize = newPuzzle.size()
        this.solvedPuzzle = generateSolvedPuzzleArray(newPuzzle.size())
        this.puzzle = newPuzzle
        this.emptySpot = findVal(EMPTY_SPOT_VALUE)
        this.originalPuzzle = copyArray(puzzle)
    }

    private def isLegalLocation(x,y){
        if(lockedRows.contains(x) || lockedColumns.contains(y)){
            return false;
        }
        if(lockedSpots.contains([x,y])){
            return false;
        }
        if(x > (edgeSize-1) || x < 0){
            return false;
        }
        if(y > (edgeSize-1) || y < 0){
            return false
        }
        return true
    }

    private def swapEmpty(x,y){
        if(isLegalLocation(x,y)) {
            def currentVal = val(x, y)
            puzzle[x][y] = EMPTY_SPOT_VALUE
            puzzle[emptyX()][emptyY()] = currentVal
            emptySpot = [x,y]
            return true;
        }
        return false
    }

    private def emptyX(){
        return emptySpot[X]
    }

    private def emptyY(){
        return emptySpot[Y]
    }

    private def set(x,y,value){
        puzzle[x][y] = value
    }

    private def val(x,y){
        return puzzle[x][y]
    }


    static def generate4x4(){
        return generate(4)
    }

    static def generate(int edgeSize){
        return new BlockPuzzle(
                puzzle: generateSolvedPuzzleArray(edgeSize)
        )
    }

    private static def copyArray(originalArray){
        def edgeSize = originalArray.size()
        int total = edgeSize * edgeSize
        def bPuzzle = new Object[edgeSize][edgeSize]
        (0..<(total)).each{num ->
            int y = num % edgeSize
            int x = num / edgeSize
            bPuzzle[x][y] = originalArray[x][y]
        }
        return bPuzzle
    }

    private static def generateSolvedPuzzleArray(int edgeSize){
        int total = edgeSize * edgeSize
        def bPuzzle = new Object[edgeSize][edgeSize]
        (0..<(total-1)).each{num ->
            int y = num % edgeSize
            int x = num / edgeSize
            bPuzzle[x][y] = num+1
        }
        bPuzzle[edgeSize-1][edgeSize-1] = EMPTY_SPOT_VALUE
        return bPuzzle
    }

}
