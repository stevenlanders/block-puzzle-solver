<%--
  Created by IntelliJ IDEA.
  User: stevenlanders
  Date: 9/28/14
  Time: 4:26 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
    <style>
        table{
            border-spacing: 0px;
            border-collapse: separate;
            border: 2px solid #000000;
        }
        input{
            width:40px;
            border:1px solid #ffffff;
            font-size: 24px;
            padding:0px;
            margin:0px;
        }
        td{
            height: 50px;
            width: 50px;
            font-size: 24px;
            text-align: center;
            border: 2px solid #000000;
            padding: 0px;
            margin:0px;

        }
        .active{
            background: #000000;
        }
        .inactive{
            background: #ffffff;
        }
    </style>
    <script src="/resources/knockout-3.2.0.js"></script>
</head>
<body>
<span data-bind="visible: moves().length == 0">
    size: <select data-bind="value:size">
        <option>3</option>
        <option>4</option>
        <option>5</option>
        <option>6</option>
        <option>7</option>
        <option>8</option>
        <option>9</option>
        <option>10</option>
        <option>15</option>
        <option>20</option>
        <option>25</option>
    </select>
    speed: <select data-bind="value:speed">
        <option value="1">stupid fast</option>
        <option value="10">very fast</option>
        <option value="50">fast</option>
        <option value="100">medium</option>
        <option value="250">slow</option>
        <option value="500">very slow</option>
    </select>
    <button data-bind="click: update">new puzzle</button>
    <button data-bind="click: solve">solve</button>
</span>
<table cellpadding="0" cellspacing="0" data-bind="foreach: { data: puzzle, as: 'row' }">
  <tr data-bind="foreach: {data : row, as: 'val'}">
      <td data-bind="text: val, css: {active: val == 'X',inactive: val != 'X'}"></td>
  </tr>
</table>
<span data-bind="visible: moves().length == 0">
    <button data-bind="click: left">left</button>
    <button data-bind="click: right">right</button>
    <button data-bind="click: up">up</button>
    <button data-bind="click: down">down</button>
</span>
<div data-bind="text: moves"></div>
<script>

    var moves = {
        "left": [0,-1],
        "right": [0, 1],
        "up": [-1, 0],
        "down": [1, 0]
    };

    function getJson(url, handler){
        var req = new XMLHttpRequest();
        req.onreadystatechange = function(response){
            if(this.readyState == 4){
                handler(eval("("+this.responseText+")"));
            }
        };
        req.open("GET", url);
        req.send();
    }

    function PuzzleViewModel(){
        var self = this;
        self.puzzle = ko.observableArray();
        self.size = ko.observable(4);
        self.speed = ko.observable(100);

        self.update = function(){
            getJson("/rest/puzzle/"+self.size(), function(jsonObj){
                self.puzzle(jsonObj.puzzle);
            });
        };

        self.size.subscribe(function(newVal){
            model.update();
        });

        self.moves = ko.observableArray([]);

        self.moves.subscribe(function(newMoveList){
            if(newMoveList.length > 0){
                model.move(newMoveList[0]);
                setTimeout("model.incrementMove()",self.speed());
            }
        });

        self.incrementMove = function(){
            self.moves(self.moves.slice(1));
        };

        self.blankSpot = ko.computed(function(){
           for(var i=0; i<self.puzzle().length; i++){
               for(var j =0; j<self.puzzle().length; j++){
                   if(self.puzzle()[i][j] == "X"){
                       return [i,j];
                   }
               }
           }
           return ["?","?"];
        });

        self.solve = function(){
            getJson("/rest/solution?puzzle="+JSON.stringify(self.puzzle()), function(jsonObj){
                self.moves(jsonObj.moves);
            });
        };

        self.up = function(){
            self.move("up");
        };

        self.down = function(){
            self.move("down");
        };

        self.right = function(){
            self.move("right");
        };

        self.left = function(){
            self.move("left");
        };

        self.move = function(m){
            var direction = moves[m];
            var blankSpot = self.blankSpot();
            var i = blankSpot[0];
            var j = blankSpot[1];
            var newI = i + direction[0];
            var newJ =  j + direction[1];
            var thisPuzzle = self.puzzle();
            if(newJ < 0 || newJ >= thisPuzzle.length){
                return;
            }
            if(newI < 0 || newI >= thisPuzzle.length){
                return;
            }
            var val = thisPuzzle[newI][newJ];
            thisPuzzle[newI][newJ] = "X";
            thisPuzzle[i][j] = val;
            self.puzzle([]);
            self.puzzle(thisPuzzle);
        }
    }

    var model = new PuzzleViewModel();

    ko.applyBindings(model)
</script>
</body>
</html>
