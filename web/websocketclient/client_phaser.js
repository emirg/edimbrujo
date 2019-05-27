
    type: Phaser.AUTO,
    parent: 'map',
    width: window.innerWidth,
    height: window.innerHeight,
    scale: {
        mode: Phaser.Scale.FIT,
        autoCenter: Phaser.Scale.CENTER_BOTH,
        width: 4500,
        height: 2048,
    },
    physics: {
      default: "arcade",
      arcade: {
        fps: 60,
        gravity: { y: 0 }
      }
    },
    scene: {
      preload: preload,
      create: create,
      update: update,
    }
  };

var game = new Phaser.Game(config);
var socket;
var socketID = "";
//dimenciones juego
var width= 4500;
var height= 2048;

function preload() {
    //backgroud
    this.load.image('background', 'assets/tests/space/nebula.jpg');
    //starts
    this.load.image('stars', 'assets/tests/space/stars.png');
    //space 
    this.load.atlas('space', 'assets/tests/space/space.png', 'assets/tests/space/space.json');
    // nave
    this.load.spritesheet('ship', 'assets/games/asteroids/ship.png', {
      frameWidth: 32,
      frameHeight: 32
    });
    //coins
    this.load.spritesheet("coin", "assets/sprites/coin.png", {
      frameWidth: 32,
      frameHeight: 32
    });
    //sprite explosion
    this.load.spritesheet("explosion","assets/sprites/explosion.png",{
        frameWidth: 64,
        frameHeight: 64
    });  
    // crear efecto de barra cargandose 
    this.fullBar = this.add.graphics();
    this.fullBar.fillStyle(0xda7a34, 1);
    this.fullBar.fillRect((this.cameras.main.width / 4)-2,(this.cameras.main.height /2) - 18, (this.cameras.main.width / 2) + 4, 20);
    this.progress = this.add.graphics();
  }

  function create() {
    //  Prepare some spritesheets and animations
    this.textures.addSpriteSheetFromAtlas('mine-sheet', { atlas: 'space', frame: 'mine', frameWidth: 64 });
    this.textures.addSpriteSheetFromAtlas('asteroid1-sheet', { atlas: 'space', frame: 'asteroid1', frameWidth: 96 });
    this.textures.addSpriteSheetFromAtlas('asteroid2-sheet', { atlas: 'space', frame: 'asteroid2', frameWidth: 96 });
    this.textures.addSpriteSheetFromAtlas('asteroid3-sheet', { atlas: 'space', frame: 'asteroid3', frameWidth: 96 });
    this.textures.addSpriteSheetFromAtlas('asteroid4-sheet', { atlas: 'space', frame: 'asteroid4', frameWidth: 64 });
    this.textures.addSpriteSheetFromAtlas('explosion-sheet', { atlas: 'space', frame: 'asteroid1', frameWidth: 96 });



    this.anims.create({ key: 'mine-anim', frames: this.anims.generateFrameNumbers('mine-sheet', { start: 0, end: 15 }), frameRate: 20, repeat: -1 });
    this.anims.create({ key: 'asteroid1-anim', frames: this.anims.generateFrameNumbers('asteroid1-sheet', { start: 0, end: 24 }), frameRate: 20, repeat: -1 });
    this.anims.create({ key: 'asteroid2-anim', frames: this.anims.generateFrameNumbers('asteroid2-sheet', { start: 0, end: 24 }), frameRate: 20, repeat: -1 });
    this.anims.create({ key: 'asteroid3-anim', frames: this.anims.generateFrameNumbers('asteroid3-sheet', { start: 0, end: 24 }), frameRate: 20, repeat: -1 });
    this.anims.create({ key: 'asteroid4-anim', frames: this.anims.generateFrameNumbers('asteroid4-sheet', { start: 0, end: 24 }), frameRate: 20, repeat: -1 });
    this.anims.create({key: 'efectoMoneda',frames: this.anims.generateFrameNumbers('coin', { start: 0, end: 5 }),frameRate: 10,repeat: -1});
    this.anims.create({ key: 'explosion-anim', frames: this.anims.generateFrameNumbers('explosion', { start: 0, end: 23 }), frameRate: 100, repeat: 1 });
    
    //world 2048*2048
    this.physics.world.setBounds(0,0,width,height);

    //fondo con dimesiones port encima de las dimensiones del world para que no queden partes sin fondo
    background = this.add.tileSprite(0, 0, 9000, 5000, 'background').setScrollFactor(0);

    //  agrego planetas ,etc
    this.add.image(512, 680, 'space', 'blue-planet').setOrigin(0).setScrollFactor(0.6);
    this.add.image(2048, 1024, 'space', 'sun').setOrigin(0).setScrollFactor(0.6);
    var galaxy = this.add.image(3500 ,1500, 'space', 'galaxy').setBlendMode(1).setScrollFactor(0.6);

    //efecto estres de luz
    for (var i = 0; i < 6; i++)
    {
        this.add.image(Phaser.Math.Between(0, width), Phaser.Math.Between(0, height), 'space', 'eyes').setBlendMode(1).setScrollFactor(0.8);
    }
    //estrellas
    stars = this.add.tileSprite(400, 300, 2000, 2000, 'stars').setScrollFactor(0);

    // coins
    //coins = this.physics.add.sprite(900, 450, 'coin');

    //animacion moneda
    this.anims.create({
        key: "moneda-anim",
        frames: this.anims.generateFrameNumbers("coin", { start: 0, end: 5 }),
        frameRate: 10,
        repeat: -1
    });

    //animacion galaxia
    this.tweens.add({
        targets: galaxy,
        angle: 360,
        duration: 100000,
        ease: 'Linear',
        loop: -1
    });

  }

  function update (time, delta)
    {

    }

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

    //mapa 
    var map = document.getElementById("map");
    //escena
    var scene = document.getElementById("scene");
    //
    //var terrain = document.getElementById("terrain");
    //
    var entities = document.getElementById("entities");
    //
    var ready = document.getElementById("ready");
    //
    var restart = document.getElementById("restart");
    //
    //var playerTeam = [];
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
                            cell.style.visibility = "hidden";
                        }
                        j++;
                    }
                    terrain.style.width = width * ($(".cell").width + 2) + "px";
                    terrain.style.height = height * ($(".cell").height() + 2) + "px";
                    //game2.style.width = width * ($(".cell").width() + 2) + "px";

                    scene.style.width = width * ($(".cell").width() + 2) + "px";
                    scene.style.height = height * ($(".cell").height() + 2) + "px";

                    //terrain.style.height = height * ($(".cell").height() + 2) + "px";
                    //game2.style.height = height * ($(".cell").height() + 2) + "px";
                    
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
                   // var team = gameState[i]["Player"]["team"];
                    //playerTeam[id] = team;
                    var player = document.getElementById("player" + id);
                    if (player === null) {
                        var name = id.substr(0, 4);
                        entities.innerHTML += "<div id='player" + id + "' class='player'><div id='player" + id + "-healthbar' class='healthbar'><div id='player" + id + "-name' class='name'>" + name + "</div>";
                        player = document.getElementById("player" + id);
                    }
                    //player.style.left = x * ($(".cell").width() + 2) + 1 + "px";
                    //player.style.top = y * ($(".cell").height() + 2) + 1 + "px";
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
                    //var team = gameState[i]["Projectile"]["team"];
                    var projectile = document.getElementById("projectile" + id + "-" + number);
                    if (projectile === null) {
                        entities.innerHTML += "<div id='projectile" + id + "-" + number + "' class='arrow'><div id='projectileTeam" + id + "-" + number + "'class='arrowteam'></div></div>";
                        projectile = document.getElementById("projectile" + id + "-" + number);
                        var projectileTeam = document.getElementById("projectileTeam" + id + "-" + number);
                        if (id === socketID) {
                            projectileTeam.style.backgroundColor = "green";
                        } else {
                            projectileTeam.style.backgroundColor = $(".team" + team).css("color");
                        }

                    }
                    //projectile.style.left = x * ($(".cell").width() + 2) + 1 + ($(".cell").width() + 2) / 2 - $(".arrow").width() / 2 + "px";
                    //projectile.style.top = y * ($(".cell").height() + 2) + 1 + ($(".cell").height() + 2) / 2 - $(".arrow").height() / 2 + "px";
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
                    //var teamAttacker = gameState[i]["Match"]["teamAttacker"];
                    //var sizeTeam = gameState[i]["Match"]["sizeTeam"];
                    var players = gameState[i]["Match"]["players"];
                    var ready = gameState[i]["Match"]["ready"];
                    var playersDOM = document.getElementById("players");
                    playersDOM.innerHTML = "<tr><th>ID</th><th>Ready</th></tr>";
                    /*
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
                    }*/
                    
                    //var teamPoints = gameState[i]["Match"]["teamPoints"];
                    //var teamPointsDOM = document.getElementById("teamPoints");
                    //teamPointsDOM.innerHTML = "";
                    for (var p = 0; p < teamPoints.length; p++) {
                        teamPointsDOM.innerHTML += "<span class='team" + p + "'>" + teamPoints[p] + "</span> ";
                    }
                }
                i++;
            }
        }
    }
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

}
