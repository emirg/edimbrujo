var socket;

window.onload = function () {
//crea el socket para recivir estados del servidor (llama @OnOpen en servidor)
    var page = document.createElement('a');
    page.href = window.location.href;
    //define la url del servidor como la hostname de la pagina y el puerto definido 8080 del ws
    var url = "ws://" + page.hostname + ":8080";
    socket = new WebSocket(url + "/Edimbrujo/GameWebSocket");
    var game = document.getElementById("game");
    var terrain = document.getElementById("terrain");
    //terrain.innerHTML = "TERRAIN<br>";
    var players = document.getElementById("players");
    //players.innerHTML = "PLAYERS<br>";
    //var sendAction = document.getElementById("sendAction");
    var action = document.getElementById("action");
    socket.onmessage = stateUpdate;

    function fire(x, y) {
        if (mouseDown == 1) {
            socket.send('{"name": "fire", "priority": "1","parameters": [{"name": "x", "value": "' + x + '"},{"name": "y", "value": "' + y + '"}]}');
        }
    }

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
                    var width = gameState[i]["Map"]["width"];
                    var height = gameState[i]["Map"]["height"];
                    var j = 0;
                    var x;
                    var y;
                    var val;
                    var cell;
                    while (typeof gameState[i]["Map"]["cells"][j] !== "undefined") {
                        x = gameState[i]["Map"]["cells"][j]["x"];
                        y = gameState[i]["Map"]["cells"][j]["y"];
                        val = gameState[i]["Map"]["cells"][j]["val"];
                        if (gameState[i]["Map"]["cells"][j]["val"] == 1) {
                            terrain.innerHTML += "<div id='cell" + x + "_" + y + "' onmousedown='fire(" + x + "," + y + ")' class='cell wall" + val + "'></div>";
                            cell = document.getElementById("cell" + x + "_" + y);
                            cell.style.left = x * ($(".cell").width() + 2) + "px";
                            cell.style.top = y * ($(".cell").height() + 2) + "px";
                        }
                        j++;
                    }
                    terrain.style.width = width * ($(".cell").width + 2) + "px";
                    game.style.width = width * ($(".cell").width() + 2) + "px";
                    terrain.style.height = height * ($(".cell").height() + 2) + "px";
                    game.style.height = height * ($(".cell").height() + 2) + "px";
                }
                if (typeof gameState[i]["Player"] !== "undefined") {
                    console.log(gameState[i]["Player"]);
                    var id = gameState[i]["Player"]["id"];
                    var leave = gameState[i]["Player"]["leave"];
                    var dead = gameState[i]["Player"]["dead"];
                    var x = gameState[i]["Player"]["super"]["Entity"]["x"];
                    var y = gameState[i]["Player"]["super"]["Entity"]["y"];
                    player = document.getElementById("player" + id);
                    if (player === null) {
                        players.innerHTML += "<div id='player" + id + "' class='player'></div>";
                        player = document.getElementById("player" + id);
                        player.style.backgroundColor = "#" + ((1 << 24) * Math.random() | 0).toString(16);
                    }
                    player.style.left = x * ($(".cell").width() + 2) + 1 + "px";
                    player.style.top = y * ($(".cell").height() + 2) + 1 + "px";
                    if (dead) {
                        player.style.zIndex = "1";
                        player.style.backgroundImage = "url('images/blood.png')";
                        player.style.backgroundColor = "rgba(0,0,0,0)";
                    }
                    if (leave) {
                        player.style.display = "none";
                    } else {
                        player.style.display = "block";
                    }
                    //players.innerHTML += gameState[i]["Player"]["id"] + "," + gameState[i]["Player"]["x"] + "," + gameState[i]["Player"]["y"] + "<br>";
                }
                if (typeof gameState[i]["Projectile"] !== "undefined") {
                    console.log(gameState[i]["Projectile"]);
                    var id = gameState[i]["Projectile"]["id"];
                    var destroy = gameState[i]["Projectile"]["super"]["Entity"]["super"]["State"]["destroy"];
                    var x = gameState[i]["Projectile"]["super"]["Entity"]["x"];
                    var y = gameState[i]["Projectile"]["super"]["Entity"]["y"];
                    var xVelocity = gameState[i]["Projectile"]["xVelocity"];
                    var yVelocity = gameState[i]["Projectile"]["yVelocity"];
                    arrow = document.getElementById("arrow" + id);
                    if (arrow === null) {
                        players.innerHTML += "<div id='arrow" + id + "' class='arrow'></div>";
                        arrow = document.getElementById("arrow" + id);
                        //arrow.style.background = "#" + ((1 << 24) * Math.random() | 0).toString(16);
                    }
                    arrow.style.left = x * ($(".cell").width() + 2) + 1 + $(".arrow").width() / 2 + "px";
                    arrow.style.top = y * ($(".cell").height() + 2) + 1 + $(".arrow").height() / 2 + "px";
                    if (destroy) {
                        $("#arrow" + id).remove();
                    }
                    //players.innerHTML += gameState[i]["Player"]["id"] + "," + gameState[i]["Player"]["x"] + "," + gameState[i]["Player"]["y"] + "<br>";
                }
                i++;
            }
        }
    }

    //evento al presionar el boton de Enviar Accion
    /*sendAction.addEventListener("click", function () {
     var actionValue = action.options[action.selectedIndex].value;
     socket.send(actionValue);
     });*/

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
        if (Key.isDown(Key.UP) && Key.isDown(Key.LEFT)) {
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
        if (Key.isDown(Key.FIRE)) {
            fire(1, 1);
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

function fire(x, y) {
    socket.send('{"name": "fire", "priority": "1","parameters": [{"name": "x", "value": "' + x + '"},{"name": "y", "value": "' + y + '"}]}');
}
