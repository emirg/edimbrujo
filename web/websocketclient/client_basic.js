var socket;
var socketID = "";

function fire(x, y) {
    socket.send('{"name": "fire", "priority": "1","parameters": [{"name": "x", "value": "' + x + '"},{"name": "y", "value": "' + y + '"}]}');
}

window.onload = function () {
    var page = document.createElement('a');
    page.href = window.location.href;
    //define la url del servidor como la hostname de la pagina y el puerto definido 8080 del ws
    var url = "ws://" + page.hostname + ":8080";
    socket = new WebSocket(url + "/Edimbrujo/GameWebSocket");
    socket.onmessage = stateUpdate;

    var scene = document.getElementById("scene");
    var terrain = document.getElementById("terrain");
    var entities = document.getElementById("entities");
    var ready = document.getElementById("ready");
    var playerTeam = [];
    //var sendAction = document.getElementById("sendAction");
    //var action = document.getElementById("action");

    //actualiza la vista del juego cuando recive un nuevo estado desde el servidor
    function stateUpdate(event) {
        //console.log(socket);
        //console.log(event.data);
        var gameState = JSON.parse(event.data);
        //var game2State = event.data;
        if (typeof gameState !== "undefined") {
            //console.log(game2State);
            if (gameState["id"] !== "undefined" && socketID === "") {
                socketID = gameState["id"];
                //console.log(socketID);
            }
            var i = 0;
            while (typeof gameState[i] !== "undefined") {
                //console.log(game2State[i]);
                if (typeof gameState[i]["Map"] !== "undefined") {
                    console.log(gameState[i]["Map"]);
                    var width = gameState[i]["Map"]["width"];
                    var height = gameState[i]["Map"]["height"];
 
                } else if (typeof gameState[i]["Player"] !== "undefined") {
                    //console.log(gameState[i]["Player"]);
                    var id = gameState[i]["Player"]["id"];
                    var destroy = gameState[i]["Player"]["super"]["Entity"]["super"]["State"]["destroy"];
                    var leave = gameState[i]["Player"]["leave"];
                    var dead = gameState[i]["Player"]["dead"];
                    var health = gameState[i]["Player"]["health"];
                    var healthMax = gameState[i]["Player"]["healthMax"];
                    var x = gameState[i]["Player"]["super"]["Entity"]["x"];
                    var y = gameState[i]["Player"]["super"]["Entity"]["y"];
                    var player = document.getElementById("player" + id);
                    if (player === null) {
                        var name = id.substr(0, 4);
                        entities.innerHTML += "<div id='player" + id + "' class='player'><div id='player" + id + "-healthbar' class='healthbar'><div id='player" + id + "-name' class='name'>" + name + "</div>";
                        player = document.getElementById("player" + id);
                    }
                    playerHealthbar = document.getElementById("player" + id + "-healthbar");
                    playerHealthbar.style.width = health * 24 / healthMax + "px";
                    if (dead) {
                        player.style.zIndex = "1";
                        player.style.backgroundImage = "url('images/blood.png')";
                        player.style.backgroundColor = "rgba(0,0,0,0)";
                    } else {
                        player.style.zIndex = "2";
                        player.style.backgroundImage = "url('images/brujo.png')";
                        if (id === socketID) {
                            player.style.backgroundColor = "green";
                        } else {
                            player.style.backgroundColor = $(".team" + team).css("color");//"#" + ((1 << 24) * Math.random() | 0).toString(16);
                        }
                    }
                    if (leave) {
                        player.style.display = "none";
                    } else {
                        player.style.display = "block";
                    }
                    if (destroy) {
                        $("#player" + id).remove();
                        playerTeam[id] = "";
                    }
                    //players.innerHTML += game2State[i]["Player"]["id"] + "," + game2State[i]["Player"]["x"] + "," + game2State[i]["Player"]["y"] + "<br>";
                } else if (typeof gameState[i]["Projectile"] !== "undefined") {
                    //console.log(game2State[i]["Projectile"]);
                    var id = gameState[i]["Projectile"]["id"];
                    var number = gameState[i]["Projectile"]["number"];
                    var destroy = gameState[i]["Projectile"]["super"]["Entity"]["super"]["State"]["destroy"];
                    var x = gameState[i]["Projectile"]["super"]["Entity"]["x"];
                    var y = gameState[i]["Projectile"]["super"]["Entity"]["y"];
                    var xVelocity = gameState[i]["Projectile"]["xVelocity"];
                    var yVelocity = gameState[i]["Projectile"]["yVelocity"];
                    var projectile = document.getElementById("projectile" + id + "-" + number);
                    if (projectile === null) {
                        entities.innerHTML += "<div id='projectile" + id + "-" + number + "' class='arrow'></div>";
                        projectile = document.getElementById("projectile" + id + "-" + number);
                        if (id === socketID) {
                            projectile.style.backgroundColor = "green";
                        } else {
                            projectile.style.backgroundColor = $(".team" + team).css("color");
                        }

                    }
                    if (destroy) {
                        $("#projectile" + id + "-" + number).remove();
                    }
                    //players.innerHTML += game2State[i]["Player"]["id"] + "," + game2State[i]["Player"]["x"] + "," + game2State[i]["Player"]["y"] + "<br>";
                } else if (typeof gameState[i]["Spawn"] !== "undefined") {
                    var x = gameState[i]["Spawn"]["x"];
                    var y = gameState[i]["Spawn"]["y"];
                    terrain.innerHTML += "<div id='spawn" + x + "_" + y + "' class='cell spawn'></div>";
                    spawn = document.getElementById("spawn" + x + "_" + y);
                    //spawn.style.left = x * ($(".cell").width() + 2) + "px";
                    //spawn.style.top = y * ($(".cell").height() + 2) + "px";
                } else if (typeof gameState[i]["Match"] !== "undefined") {
                    //console.log(game2State[i]["Match"]);
                    var round = gameState[i]["Match"]["round"];
                    document.getElementById("round").innerHTML = round;
                    var countRounds = gameState[i]["Match"]["countRounds"];
                    document.getElementById("countRounds").innerHTML = countRounds;
                    var endGame = gameState[i]["Match"]["endGame"];
                    var endRound = gameState[i]["Match"]["endRound"];
                    var startGame = gameState[i]["Match"]["startGame"];
                    var players = gameState[i]["Match"]["players"];
                    var ready = gameState[i]["Match"]["ready"];
                }
            }
        }
    }


    //evento al presionar el boton de Enviar Accion
    /*sendAction.addEventListener("click", function () {
     var actionValue = action.options[action.selectedIndex].value;
     socket.send(actionValue);
     });*/

    ready.addEventListener("click", function () {
        socket.send("ready");
    });

    var Key = {
        _pressed: {},

        LEFT: 37,
        UP: 38,
        RIGHT: 39,
        DOWN: 40,
        FIRE: 32,
        ALTLEFT: 65,
        ALTUP: 87,
        ALTRIGHT: 68,
        ALTDOWN: 83,

        areDown: function (keyCodes) {
            var pressed = false;
            var i = 0;
            while (!pressed && i < keyCodes.length) {
                pressed = this._pressed[keyCodes[i]];
                i++;
            }
            return pressed;
        },

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
        if (Key.areDown([Key.UP, Key.ALTUP]) && Key.areDown([Key.LEFT, Key.ALTLEFT])) {
            socket.send("upleft");
        } else if (Key.areDown([Key.UP, Key.ALTUP]) && Key.areDown([Key.RIGHT, Key.ALTRIGHT])) {
            socket.send("upright");
        } else if (Key.areDown([Key.DOWN, Key.ALTDOWN]) && Key.areDown([Key.LEFT, Key.ALTLEFT])) {
            socket.send("downleft");
        } else if (Key.areDown([Key.DOWN, Key.ALTDOWN]) && Key.areDown([Key.RIGHT, Key.ALTRIGHT])) {
            socket.send("downright");
        } else if (Key.areDown([Key.UP, Key.ALTUP])) {
            socket.send("up");
        } else if (Key.areDown([Key.LEFT, Key.ALTLEFT])) {
            socket.send("left");
        } else if (Key.areDown([Key.DOWN, Key.ALTDOWN])) {
            socket.send("down");
        } else if (Key.areDown([Key.RIGHT, Key.ALTRIGHT])) {
            socket.send("right");
        }
        if (Key.isDown(Key.FIRE)) {
            fire(1, 1);
        }
    }

    //evento para fire 
    $(document).ready(function () {
        $("#scene").mousedown(function (event) {
            var relX = event.pageX - $(this).offset().left;
            var relY = event.pageY - $(this).offset().top;
            relX = parseInt(relX / ($(".cell").width() + 2));
            relY = parseInt(relY / ($(".cell").height() + 2));
            //console.log("(" + relX + "," + relY + ")");
            fire(relX, relY);
        });
    });
}
