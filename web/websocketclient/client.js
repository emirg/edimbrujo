window.onload = function () {
//crea el socket para recivir estados del servidor (llama @OnOpen en servidor)
    var socket = new WebSocket("ws://localhost:8080/edimbrujo/GameWebSocket");
    //var game = document.getElementById("game");
    var terrain = document.getElementById("terrain");
    //terrain.innerHTML = "TERRAIN<br>";
    var players = document.getElementById("players");
    //players.innerHTML = "PLAYERS<br>";
    var sendAction = document.getElementById("sendAction");
    var action = document.getElementById("action");
    socket.onmessage = stateUpdate;

//actualiza la vista del juego cuando recive un nuevo estado desde el servidor
    function stateUpdate(event) {
        //console.log(event.data);
        var gameState = JSON.parse(event.data);
        //var gameState = event.data;
        /*console.log(gameState);
         console.log(gameState["0"]);
         console.log(gameState["0"]["Map"]);
         console.log(gameState["0"]["Player"]);*/
        if (typeof gameState !== "undefined") {
            //console.log(gameState);
            var i = 0;
            while (typeof gameState[i] !== "undefined") {
                //console.log(gameState[i]);
                if (typeof gameState[i]["Map"] !== "undefined") {
                    console.log(gameState[i]["Map"]);
                    var j = 0;
                    var x;
                    var xMax = 0;
                    var y;
                    var yMax = 0;
                    var cell;
                    while (typeof gameState[i]["Map"][j] !== "undefined") {
                        x = gameState[i]["Map"][j]["x"];
                        xMax = x > xMax ? x : xMax;
                        y = gameState[i]["Map"][j]["y"];
                        yMax = y > yMax ? x : yMax;
                        terrain.innerHTML += "<div id='cell" + x + "_" + y + "' class='wall'></div>";
                        cell = document.getElementById("cell" + x + "_" + y);
                        cell.style.left = x * $(".wall").width() + "px";
                        cell.style.top = y * $(".wall").height() + "px";
                        j++;
                    }
                    terrain.style.width = xMax * $(".wall").width() + $(".wall").width() + "px";
                    terrain.style.height = yMax * $(".wall").height() + $(".wall").height() + "px";
                }
                if (typeof gameState[i]["Player"] !== "undefined") {
                    console.log(gameState[i]["Player"]);
                    var id = gameState[i]["Player"]["id"];
                    var leave = gameState[i]["Player"]["leave"];
                    var x = gameState[i]["Player"]["x"];
                    var y = gameState[i]["Player"]["y"];
                    player = document.getElementById("player" + id);
                    if (player === null) {
                        players.innerHTML += "<div id='player" + id + "' class='player'></div>";
                        player = document.getElementById("player" + id);
                        player.style.background = "#" + ((1 << 24) * Math.random() | 0).toString(16);
                    }
                    player.style.left = x * $(".player").width() + "px";
                    player.style.top = y * $(".player").height() + "px";
                    if (leave) {
                        $("#player" + id).remove();
                    }
                    //players.innerHTML += gameState[i]["Player"]["id"] + "," + gameState[i]["Player"]["x"] + "," + gameState[i]["Player"]["y"] + "<br>";
                }
                i++;
            }
        }
    }

//evento al presionar el boton de Enviar Accion
    sendAction.addEventListener("click", function () {
        var actionValue = action.options[action.selectedIndex].value;
        socket.send(actionValue);
    });

    /*$('body').on('keypress', function (e) {
     if (e.which === 38) {
     socket.send("up");
     } else if (e.which === 40) {
     socket.send("down");
     } else if (e.which === 37) {
     socket.send("left");
     } else if (e.which === 39) {
     socket.send("right");
     }
     });*/

    //keyboard input with customisable repeat (set to 0 for no key repeat)
    function KeyboardController(keys, repeat) {
        //lookup of key codes to timer ID, or null for no repeat
        var timers = {};

        //when key is pressed and we don't already think it's pressed, call the
        //key action callback and set a timer to generate another one after a delay
        document.onkeydown = function (event) {
            var key = (event || window.event).keyCode;
            if (!(key in keys))
                return true;
            if (!(key in timers)) {
                timers[key] = null;
                keys[key]();
                if (repeat !== 0)
                    timers[key] = setInterval(keys[key], repeat);
            }
            return false;
        };

        //cancel timeout and mark key as released on keyup
        document.onkeyup = function (event) {
            var key = (event || window.event).keyCode;
            if (key in timers) {
                if (timers[key] !== null)
                    clearInterval(timers[key]);
                delete timers[key];
            }
        };

        //when window is unfocused we may not get key events. To prevent this
        //causing a key to 'get stuck down', cancel all held keys
        window.onblur = function () {
            for (key in timers)
                if (timers[key] !== null)
                    clearInterval(timers[key]);
            timers = {};
        };
    }
    ;
    then:
            //arrow key movement. Repeat key five times a second
            KeyboardController({
                37: function () {
                    socket.send("left");
                },
                38: function () {
                    socket.send("up");
                },
                39: function () {
                    socket.send("right");
                },
                40: function () {
                    socket.send("down");
                }
            }, 200);
}