//var Game = {};

/*Game.init = function(){
 game2.stage.disableVisibilityChange = true;
 };*/
var socket;
var socketID = "";


var config = {
    type: Phaser.AUTO,
    width: 880,
    height: 880,
    backgroundColor: '#b8b8b8',
    parent: 'jueguito',

    scene: {
        preload: preload,
        create: create,

    }
    //scene: [Credits, Instructions, MainMenu, Pause, EndGame, JuegoScene]
};

var game = new Phaser.Game(config);

function preload() {

    //game2.load.tilemap('map', 'assets/map/example_map.json', null, Phaser.Tilemap.TILED_JSON);
    //game2.load.spritesheet('tileset', 'assets/map/tilesheet.png',32,32);
    //game2.load.image('sprite','assets/sprites/sprite.png');

    var page = document.createElement('a');
    page.href = window.location.href;
    //define la url del servidor como la hostname de la pagina y el puerto definido 8080 del ws
    var url = "ws://" + page.hostname + ":8080";
    socket = new WebSocket(url + "/Edimbrujo/GameWebSocket");
    var game2 = document.getElementById("game2");
    var terrain = document.getElementById("terrain");



    //terrain.innerHTML = "TERRAIN<br>";
    var entities = document.getElementById("entities");
    //players.innerHTML = "PLAYERS<br>";
    var ready = document.getElementById("ready");
    //var sendAction = document.getElementById("sendAction");
    //var action = document.getElementById("action");
    var playerTeam = [];
    //Imagen de fondo

    //"url('')"
    //var img = document.createElement("images/mapa_grande.png");


    /*var img = document.createElement("img");
     
     img.src = "images/mapa_2018/mapa_grande.png";
     img.width = 480;
     img.height = 480;*/

    this.load.image('mapa', 'mapaSolo.jpeg');



    //https://scontent.fnqn2-1.fna.fbcdn.net/v/t1.0-9/46847853_10217410133194752_2660408218949255168_n.jpg?_nc_cat=104&_nc_ht=scontent.fnqn2-1.fna&oh=oe2a7fb3083bde0163ff1334796856d6&oe=5CA813F4
    //var src = document.getElementById("terrain");
    //src.appendChild(img);




    socket.onmessage = stateUpdate;

    //actualiza la vista del juego cuando recive un nuevo estado desde el servidor
    function stateUpdate(event) {
        //console.log(socket);
        //console.log(event.data);
        var game2State = JSON.parse(event.data);
        //var game2State = event.data;
        if (typeof game2State !== "undefined") {
            //console.log(game2State);
            if (game2State["id"] !== "undefined" && socketID === "") {
                socketID = game2State["id"];
                //console.log(socketID);
            }
            var i = 0;
            while (typeof game2State[i] !== "undefined") {
                //console.log(game2State[i]);
                if (typeof game2State[i]["Map"] !== "undefined") {
                    console.log(game2State[i]["Map"]);
                    var width = game2State[i]["Map"]["width"];
                    var height = game2State[i]["Map"]["height"];
                    var j = 0;
                    var x;
                    var y;
                    var val;
                    var cell;
                    while (typeof game2State[i]["Map"]["cells"][j] !== "undefined") {
                        x = game2State[i]["Map"]["cells"][j]["x"];
                        y = game2State[i]["Map"]["cells"][j]["y"];
                        val = game2State[i]["Map"]["cells"][j]["val"];
                        if (game2State[i]["Map"]["cells"][j]["val"] == 1) {
                            terrain.innerHTML += "<div id='cell" + x + "_" + y + "' class='cell wall" + val + "'></div>";
                            cell = document.getElementById("cell" + x + "_" + y);
                            cell.style.left = x * ($(".cell").width() + 2) + "px";
                            cell.style.top = y * ($(".cell").height() + 2) + "px";
                        }
                        j++;
                    }
                    terrain.style.width = width * ($(".cell").width + 2) + "px";
                    terrain.style.height = height * ($(".cell").height() + 2) + "px";
                    //game2.style.width = width * ($(".cell").width() + 2) + "px";

                    game2.style.width = width * ($(".cell").width() + 2) + "px";
                    game2.style.height = height * ($(".cell").height() + 2) + "px";

                    //terrain.style.height = height * ($(".cell").height() + 2) + "px";
                    //game2.style.height = height * ($(".cell").height() + 2) + "px";
                } else if (typeof game2State[i]["Player"] !== "undefined") {
                    console.log(game2State[i]["Player"]);
                    var id = game2State[i]["Player"]["id"];
                    var destroy = game2State[i]["Player"]["super"]["Entity"]["super"]["State"]["destroy"];
                    var leave = game2State[i]["Player"]["leave"];
                    var dead = game2State[i]["Player"]["dead"];
                    var health = game2State[i]["Player"]["health"];
                    var healthMax = game2State[i]["Player"]["healthMax"];
                    var x = game2State[i]["Player"]["super"]["Entity"]["x"];
                    var y = game2State[i]["Player"]["super"]["Entity"]["y"];
                    var team = game2State[i]["Player"]["team"];
                    playerTeam[id] = team;
                    var player = document.getElementById("player" + id);
                    if (player === null) {
                        var name = id.substr(0, 3);
                        entities.innerHTML += "<div id='player" + id + "' class='player'><div id='player" + id + "-healthbar' class='healthbar'><div id='player" + id + "-name' class='name'>" + name + "</div>";
                        player = document.getElementById("player" + id);
                    }
                    player.style.left = x * ($(".cell").width() + 2) + 1 + "px";
                    player.style.top = y * ($(".cell").height() + 2) + 1 + "px";
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
                } else if (typeof game2State[i]["Projectile"] !== "undefined") {
                    //console.log(game2State[i]["Projectile"]);
                    var id = game2State[i]["Projectile"]["id"];
                    var number = game2State[i]["Projectile"]["number"];
                    var destroy = game2State[i]["Projectile"]["super"]["Entity"]["super"]["State"]["destroy"];
                    var x = game2State[i]["Projectile"]["super"]["Entity"]["x"];
                    var y = game2State[i]["Projectile"]["super"]["Entity"]["y"];
                    var xVelocity = game2State[i]["Projectile"]["xVelocity"];
                    var yVelocity = game2State[i]["Projectile"]["yVelocity"];
                    var team = game2State[i]["Projectile"]["team"];
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
                    projectile.style.left = x * ($(".cell").width() + 2) + 1 + ($(".cell").width() + 2) / 2 - $(".arrow").width() / 2 + "px";
                    projectile.style.top = y * ($(".cell").height() + 2) + 1 + ($(".cell").height() + 2) / 2 - $(".arrow").height() / 2 + "px";
                    if (destroy) {
                        $("#projectile" + id + "-" + number).remove();
                    }
                    //players.innerHTML += game2State[i]["Player"]["id"] + "," + game2State[i]["Player"]["x"] + "," + game2State[i]["Player"]["y"] + "<br>";
                } else if (typeof game2State[i]["Tower"] !== "undefined") {
                    console.log(game2State[i]["Tower"]);
                    var id = game2State[i]["Tower"]["id"];
                    var destroy = game2State[i]["Tower"]["super"]["Entity"]["super"]["State"]["destroy"];
                    var dead = game2State[i]["Tower"]["dead"];
                    var team = game2State[i]["Tower"]["team"];
                    var health = game2State[i]["Tower"]["health"];
                    var healthMax = game2State[i]["Tower"]["healthMax"];
                    var width = game2State[i]["Tower"]["width"];
                    var height = game2State[i]["Tower"]["height"];
                    var x = game2State[i]["Tower"]["super"]["Entity"]["x"];
                    var y = game2State[i]["Tower"]["super"]["Entity"]["y"];
                    var team = game2State[i]["Tower"]["team"];
                    var tower = document.getElementById("tower" + id);
                    if (tower === null) {
                        entities.innerHTML += "<div id='tower" + id + "' class='tower'><div id='tower" + id + "-healthbar' class='healthbar'></div></div>";
                        tower = document.getElementById("tower" + id);
                        tower.style.backgroundColor = $(".team" + team).css("color");
                    }
                    tower.style.left = x * ($(".cell").width() + 2) + 1 - (Math.floor(width / 2) * $(".cell").width()) + "px";
                    tower.style.top = y * ($(".cell").height() + 2) + 1 - (Math.floor(height / 2) * $(".cell").width()) + "px";
                    towerHealthbar = document.getElementById("tower" + id + "-healthbar");
                    towerHealthbar.style.width = health * width * 24 / healthMax + "px";
                    towerHealthbar.style.left = -8 + "px";
                    if (dead) {
                        tower.style.zIndex = "1";
                        tower.style.backgroundImage = "url('images/rubble.png')";
                        tower.style.backgroundColor = "rgba(0,0,0,0)";
                    }
                    if (destroy) {
                        $("#tower" + id).remove();
                    }
                } else if (typeof game2State[i]["Spawn"] !== "undefined") {
                    var x = game2State[i]["Spawn"]["x"];
                    var y = game2State[i]["Spawn"]["y"];
                    terrain.innerHTML += "<div id='spawn" + x + "_" + y + "' class='cell spawn'></div>";
                    spawn = document.getElementById("spawn" + x + "_" + y);
                    spawn.style.left = x * ($(".cell").width() + 2) + "px";
                    spawn.style.top = y * ($(".cell").height() + 2) + "px";
                } else if (typeof game2State[i]["Match"] !== "undefined") {
                    //console.log(game2State[i]["Match"]);
                    var round = game2State[i]["Match"]["round"];
                    document.getElementById("round").innerHTML = round;
                    var countRounds = game2State[i]["Match"]["countRounds"];
                    document.getElementById("countRounds").innerHTML = countRounds;
                    var endGame = game2State[i]["Match"]["endGame"];
                    var endRound = game2State[i]["Match"]["endRound"];
                    var startGame = game2State[i]["Match"]["startGame"];
                    var teamAttacker = game2State[i]["Match"]["teamAttacker"];
                    var sizeTeam = game2State[i]["Match"]["sizeTeam"];
                    var players = game2State[i]["Match"]["players"];
                    var ready = game2State[i]["Match"]["ready"];
                    var playersDOM = document.getElementById("players");
                    playersDOM.innerHTML = "<tr><th>ID</th><th>Ready</th></tr>";
                    for (var p = 0; p < players.length; p++) {
                        var found = false;
                        var r = 0;
                        var playerReady = null;
                        while (!found && r < ready.length) {
                            if (ready[r] === players[p]) {
                                playerReady = ready[r];
                                found = true;
                            }
                            r++;
                        }
                        var checked = playerReady ? "checked" : "";
                        var team = playerTeam[players[p]];
                        if (team === "undefined") {
                            team = "";
                        }
                        playersDOM.innerHTML += "<tr><td class='team" + team + "'>" + players[p] + "</td><td><input id='ready" + player + "' type='checkbox' disabled " + checked + "/></td></tr>";
                    }
                    var teamPoints = game2State[i]["Match"]["teamPoints"];
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
        game2.style.visibility = "visible";
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

    game2.addEventListener('mousedown', function (e) {
        //var game2Style = window.getComputedStyle(game2);
        //var x = parseInt((e.x - game2.offsetLeft) / ($(".cell").width())) - 2;//game2Style.getPropertyValue("left");
        //var y = parseInt((e.y - game2.offsetTop) / ($(".cell").height())) - 2;//game2Style.getPropertyValue("top");
        var x = parseInt((e.x - game2.offsetLeft) / ($(".cell").width())) - 2;//game2Style.getPropertyValue("left");
        var y = parseInt((e.y - game2.offsetTop) / ($(".cell").height())) - 2;//game2Style.getPropertyValue("top");
        console.log('e ', e.x, e.y);
        console.log(game2.offsetLeft, ' ', game2.offsetTop);
        console.log($(".cell").width(), ' ', $(".cell").height());
        console.log('disparo ', x, y);
        fire(x, y);
    }, false);

}

function create() {
    background = this.add.image(440, 440, 'mapa');

}

function fire(x, y) {
    socket.send('{"name": "fire", "priority": "1","parameters": [{"name": "x", "value": "' + x + '"},{"name": "y", "value": "' + y + '"}]}');
}




/*Game.create = function(){
 Game.playerMap = {};
 var testKey = game2.input.keyboard.addKey(Phaser.Keyboard.ENTER);
 testKey.onDown.add(Client.sendTest, this);
 var map = game2.add.tilemap('map');
 map.addTilesetImage('tilesheet', 'tileset'); // tilesheet is the key of the tileset in map's JSON file
 var layer;
 for(var i = 0; i < map.layers.length; i++) {
 layer = map.createLayer(i);
 }
 layer.inputEnabled = true; // Allows clicking on the map ; it's enough to do it on the last layer
 layer.events.onInputUp.add(Game.getCoordinates, this);
 Client.askNewPlayer();
 };
 
 Game.getCoordinates = function(layer,pointer){
 Client.sendClick(pointer.worldX,pointer.worldY);
 };
 
 Game.addNewPlayer = function(id,x,y){
 Game.playerMap[id] = game2.add.sprite(x,y,'sprite');
 };
 
 Game.movePlayer = function(id,x,y){
 var player = Game.playerMap[id];
 var distance = Phaser.Math.distance(player.x,player.y,x,y);
 var tween = game2.add.tween(player);
 var duration = distance*10;
 tween.to({x:x,y:y}, duration);
 tween.start();
 };
 
 Game.removePlayer = function(id){
 Game.playerMap[id].destroy();
 delete Game.playerMap[id];
 };*/