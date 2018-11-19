window.onload = function () {
//crea el socket para recivir estados del servidor (llama @OnOpen en servidor)
    var page = document.createElement('a');
    page.href = window.location.href;
    //define la url del servidor como la hostname de la pagina y el puerto definido 8080 del ws
    var url = "ws://" + page.hostname + ":8080";
    var socket = new WebSocket(url + "/Edimbrujo/GameWebSocket");
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
                        terrain.innerHTML += "<div id='cell" + x + "_" + y + "' class='wall"+gameState[i]["Map"][j]["val"]+"'></div>";
                        cell = document.getElementById("cell" + x + "_" + y);
                        cell.style.left = x * $(".wall"+gameState[i]["Map"][j]["val"]).width() + "px";
                        cell.style.top = y * $(".wall"+gameState[i]["Map"][j]["val"]).height() + "px";
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
                if (typeof gameState[i]["Arrow"] !== "undefined") {
                    console.log(gameState[i]["Arrow"]);
                    var id = gameState[i]["Arrow"]["id"];
                    var leave = gameState[i]["Arrow"]["leave"];
                    var x = gameState[i]["Arrow"]["x"];
                    var y = gameState[i]["Arrow"]["y"];
                    arrow = document.getElementById("arrow" + id);
                    if (arrow === null) {
                        players.innerHTML += "<div id='arrow" + id + "' class='arrow'></div>";
                        arrow = document.getElementById("arrow" + id);
                        arrow.style.background = "#" + ((1 << 24) * Math.random() | 0).toString(16);
                    }
                    arrow.style.left = x * $(".arrow").width() + "px";
                    arrow.style.top = y * $(".arrow").height() + "px";
                    if (leave) {
                        $("#arrow" + id).remove();
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

    var Key = {
        _pressed: {},

        LEFT: 37,
        UP: 38,
        RIGHT: 39,
        DOWN: 40,
        FIRE: 32,

        isDown: function (keyCode) {
            return this._pressed[keyCode];
        },

        onKeydown: function (event) {
            this._pressed[event.keyCode] = true;
        },

        onKeyup: function (event) {
            delete this._pressed[event.keyCode];
        }
    };

    window.addEventListener('keyup', function (event) {
        Key.onKeyup(event);
    }, false);
    window.addEventListener('keydown', function (event) {
        Key.onKeydown(event);
    }, false);

    window.setInterval(function () {
        updateKeyboard();
    }, 100);

    function updateKeyboard() {
        if (Key.isDown(Key.FIRE)) {
            socket.send("fire");
        } else if (Key.isDown(Key.UP) && Key.isDown(Key.LEFT)) {
            socket.send("upleft");
        } else if (Key.isDown(Key.UP) && Key.isDown(Key.RIGHT)) {
            socket.send("upright");
        } else if (Key.isDown(Key.DOWN) && Key.isDown(Key.LEFT)) {
            socket.send("downleft");
        } else if (Key.isDown(Key.DOWN) && Key.isDown(Key.RIGHT)) {
            socket.send("downright");
        } else if (Key.isDown(Key.UP)) {
            socket.send("up");
        } else if (Key.isDown(Key.LEFT)) {
            socket.send("left");
        } else if (Key.isDown(Key.DOWN)) {
            socket.send("down");
        } else if (Key.isDown(Key.RIGHT)) {
            socket.send("right");
        }
    }

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
}