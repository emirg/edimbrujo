var socket;
var socketID = "";

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
    var entities = document.getElementById("entities");
    //players.innerHTML = "PLAYERS<br>";
    var ready = document.getElementById("ready");
    //var sendAction = document.getElementById("sendAction");
    //var action = document.getElementById("action");
    socket.onmessage = stateUpdate;

    //actualiza la vista del juego cuando recive un nuevo estado desde el servidor
    function stateUpdate(event) {
        //console.log(socket);
        //console.log(event.data);
        var gameState = JSON.parse(event.data);
        //var gameState = event.data;
        if (typeof gameState !== "undefined") {
            //console.log(gameState);
            if (gameState["id"] !== "undefined" && socketID === "") {
                socketID = gameState["id"];
                //console.log(socketID);
            }
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
                            terrain.innerHTML += "<div id='cell" + x + "_" + y + "' class='cell wall" + val + "'></div>";
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
                } else if (typeof gameState[i]["Player"] !== "undefined") {
                    console.log(gameState[i]["Player"]);
                    var id = gameState[i]["Player"]["id"];
                    var destroy = gameState[i]["Player"]["super"]["Entity"]["super"]["State"]["destroy"];
                    var leave = gameState[i]["Player"]["leave"];
                    var dead = gameState[i]["Player"]["dead"];
                    var health = gameState[i]["Player"]["health"];
                    var healthMax = gameState[i]["Player"]["healthMax"];
                    var x = gameState[i]["Player"]["super"]["Entity"]["x"];
                    var y = gameState[i]["Player"]["super"]["Entity"]["y"];
                    var team = gameState[i]["Player"]["team"];
                    var player = document.getElementById("player" + id);
                    if (player === null) {
                        entities.innerHTML += "<div id='player" + id + "' class='player'><div id='player" + id + "-healthbar' class='healthbar'></div>";
                        player = document.getElementById("player" + id);
                    }
                    player.style.left = x * ($(".cell").width() + 2) + 1 + "px";
                    player.style.top = y * ($(".cell").height() + 2) + 1 + "px";
                    playerHealthbar = document.getElementById("player" + id + "-healthbar");
                    playerHealthbar.style.width = health * 12 / healthMax + "px";
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
                    }
                    //players.innerHTML += gameState[i]["Player"]["id"] + "," + gameState[i]["Player"]["x"] + "," + gameState[i]["Player"]["y"] + "<br>";
                } else if (typeof gameState[i]["Projectile"] !== "undefined") {
                    //console.log(gameState[i]["Projectile"]);
                    var id = gameState[i]["Projectile"]["id"];
                    var number = gameState[i]["Projectile"]["number"];
                    var destroy = gameState[i]["Projectile"]["super"]["Entity"]["super"]["State"]["destroy"];
                    var x = gameState[i]["Projectile"]["super"]["Entity"]["x"];
                    var y = gameState[i]["Projectile"]["super"]["Entity"]["y"];
                    var xVelocity = gameState[i]["Projectile"]["xVelocity"];
                    var yVelocity = gameState[i]["Projectile"]["yVelocity"];
                    var team = gameState[i]["Projectile"]["team"];
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
                    projectile.style.left = x * ($(".cell").width() + 2) + 1 + $(".arrow").width() / 2 + "px";
                    projectile.style.top = y * ($(".cell").height() + 2) + 1 + $(".arrow").height() / 2 + "px";
                    if (destroy) {
                        $("#projectile" + id + "-" + number).remove();
                    }
                    //players.innerHTML += gameState[i]["Player"]["id"] + "," + gameState[i]["Player"]["x"] + "," + gameState[i]["Player"]["y"] + "<br>";
                } else if (typeof gameState[i]["Tower"] !== "undefined") {
                    console.log(gameState[i]["Tower"]);
                    var id = gameState[i]["Tower"]["id"];
                    var destroy = gameState[i]["Tower"]["super"]["Entity"]["super"]["State"]["destroy"];
                    var dead = gameState[i]["Tower"]["dead"];
                    var team = gameState[i]["Tower"]["team"];
                    var health = gameState[i]["Tower"]["health"];
                    var healthMax = gameState[i]["Tower"]["healthMax"];
                    var x = gameState[i]["Tower"]["super"]["Entity"]["x"];
                    var y = gameState[i]["Tower"]["super"]["Entity"]["y"];
                    var team = gameState[i]["Tower"]["team"];
                    var tower = document.getElementById("tower" + id);
                    if (tower === null) {
                        entities.innerHTML += "<div id='tower" + id + "' class='tower'><div id='tower" + id + "-healthbar' class='healthbar'></div></div>";
                        tower = document.getElementById("tower" + id);
                        tower.style.backgroundColor = $(".team" + team).css("color");
                    }
                    tower.style.left = x * ($(".cell").width() + 2) + 1 + "px";
                    tower.style.top = y * ($(".cell").height() + 2) + 1 + "px";
                    towerHealthbar = document.getElementById("tower" + id + "-healthbar");
                    towerHealthbar.style.width = health * 12 / healthMax + "px";
                    if (dead) {
                        tower.style.zIndex = "1";
                        tower.style.backgroundImage = "url('images/blood.png')";
                        tower.style.backgroundColor = "rgba(0,0,0,0)";
                    }
                    if (destroy) {
                        $("#tower" + id).remove();
                    }
                } else if (typeof gameState[i]["Spawn"] !== "undefined") {
                    var x = gameState[i]["Spawn"]["x"];
                    var y = gameState[i]["Spawn"]["y"];
                    terrain.innerHTML += "<div id='spawn" + x + "_" + y + "' class='cell spawn'></div>";
                    spawn = document.getElementById("spawn" + x + "_" + y);
                    spawn.style.left = x * ($(".cell").width() + 2) + "px";
                    spawn.style.top = y * ($(".cell").height() + 2) + "px";
                } else if (typeof gameState[i]["Match"] !== "undefined") {
                    //console.log(gameState[i]["Match"]);
                    var round = gameState[i]["Match"]["round"];
                    document.getElementById("round").innerHTML = round;
                    var countRounds = gameState[i]["Match"]["countRounds"];
                    document.getElementById("countRounds").innerHTML = countRounds;
                    var endGame = gameState[i]["Match"]["endGame"];
                    var endRound = gameState[i]["Match"]["endRound"];
                    var startGame = gameState[i]["Match"]["startGame"];
                    var teamAttacker = gameState[i]["Match"]["teamAttacker"];
                    var sizeTeam = gameState[i]["Match"]["sizeTeam"];
                    var players = gameState[i]["Match"]["players"];
                    var ready = gameState[i]["Match"]["ready"];
                    var playersDOM = document.getElementById("players");
                    playersDOM.innerHTML = "<tr><th>ID</th><th>Ready</th></tr>";
                    for (var p = 0; p < players.length; p++) {
                        var checked = ready[p] ? "checked" : "";
                        playersDOM.innerHTML += "<tr><td>" + players[p] + "</td><td><input id='ready" + player + "' type='checkbox' disabled " + checked + "/></td></tr>";
                    }
                    var teamPoints = gameState[i]["Match"]["teamPoints"];
                    var teamPointsDOM = document.getElementById("teamPoints");
                    teamPointsDOM.innerHTML = "";
                    for (var p = 0; p < teamPoints.length; p++) {
                        teamPointsDOM.innerHTML += "<span class='team" + p + "'>" + teamPoints[p] + "</span> ";
                    }
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

    game.addEventListener('mousedown', function (e) {
        //var gameStyle = window.getComputedStyle(game);
        var x = parseInt((e.x - game.offsetLeft + $("#game").width() / 2) / ($(".cell").width() + 2)) - 1;//gameStyle.getPropertyValue("left");
        var y = parseInt((e.y - game.offsetTop) / ($(".cell").height() + 2)) - 2;//gameStyle.getPropertyValue("top");
        fire(x, y);
    }, false);

}

function fire(x, y) {
    socket.send('{"name": "fire", "priority": "1","parameters": [{"name": "x", "value": "' + x + '"},{"name": "y", "value": "' + y + '"}]}');
}
