var objetosNuevos = [];
var config = {
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
            gravity: {y: 0}
        }
    },
    scene: {
        preload: preload,
        create: create,
        update: update
    }
};

var game = new Phaser.Game(config);

//coneccion
var socket;
var socketID = "";

//cursor
var cursors;

//dimensiones juego
var width = 4500;
var height = 2048;

//arreglos
var asteroides = [];
var players = [];

function preload() {
    //backgroud
    this.load.image('background', 'assets/tests/space/nebula.jpg');
    //starts
    this.load.image('stars', 'assets/tests/space/stars.png');
    //space 
    this.load.atlas('space', 'assets/tests/space/space.png', 'assets/tests/space/space.json');
    // nave
    this.load.spritesheet('ship', 'assets/sprites/ship1.png', {
        frameWidth: 64,
        frameHeight: 64
    });

    //coins
    this.load.spritesheet("coin", "assets/sprites/coin.png", {
        frameWidth: 32,
        frameHeight: 32
    });
    //sprite explosion
    this.load.spritesheet("explosion", "assets/sprites/explosion.png", {
        frameWidth: 64,
        frameHeight: 64
    });
    // crear efecto de barra cargandose 
    this.fullBar = this.add.graphics();
    this.fullBar.fillStyle(0xda7a34, 1);
    this.fullBar.fillRect((this.cameras.main.width / 4) - 2, (this.cameras.main.height / 2) - 18, (this.cameras.main.width / 2) + 4, 20);
    this.progress = this.add.graphics()
}

function create() {
    //console.log("CREATE");
    //  Prepare some spritesheets and animations
    this.textures.addSpriteSheetFromAtlas('mine-sheet', {atlas: 'space', frame: 'mine', frameWidth: 64});
    this.textures.addSpriteSheetFromAtlas('asteroid1-sheet', {atlas: 'space', frame: 'asteroid1', frameWidth: 96});
    this.textures.addSpriteSheetFromAtlas('asteroid2-sheet', {atlas: 'space', frame: 'asteroid2', frameWidth: 96});
    this.textures.addSpriteSheetFromAtlas('asteroid3-sheet', {atlas: 'space', frame: 'asteroid3', frameWidth: 96});
    this.textures.addSpriteSheetFromAtlas('asteroid4-sheet', {atlas: 'space', frame: 'asteroid4', frameWidth: 64});
    this.textures.addSpriteSheetFromAtlas('explosion-sheet', {atlas: 'space', frame: 'asteroid1', frameWidth: 96});



    this.anims.create({key: 'mine-anim', frames: this.anims.generateFrameNumbers('mine-sheet', {start: 0, end: 15}), frameRate: 20, repeat: -1});
    this.anims.create({key: 'asteroid1-anim', frames: this.anims.generateFrameNumbers('asteroid1-sheet', {start: 0, end: 24}), frameRate: 20, repeat: -1});
    this.anims.create({key: 'asteroid2-anim', frames: this.anims.generateFrameNumbers('asteroid2-sheet', {start: 0, end: 24}), frameRate: 20, repeat: -1});
    this.anims.create({key: 'asteroid3-anim', frames: this.anims.generateFrameNumbers('asteroid3-sheet', {start: 0, end: 24}), frameRate: 20, repeat: -1});
    this.anims.create({key: 'asteroid4-anim', frames: this.anims.generateFrameNumbers('asteroid4-sheet', {start: 0, end: 24}), frameRate: 20, repeat: -1});
    this.anims.create({key: 'efectoMoneda', frames: this.anims.generateFrameNumbers('coin', {start: 0, end: 5}), frameRate: 10, repeat: -1});
    this.anims.create({key: 'explosion-anim', frames: this.anims.generateFrameNumbers('explosion', {start: 0, end: 23}), frameRate: 100, repeat: 1});

    //world 2048*2048
    this.physics.world.setBounds(0, 0, width, height);

    //fondo con dimesiones port encima de las dimensiones del world para que no queden partes sin fondo
    background = this.add.tileSprite(0, 0, 9000, 5000, 'background').setScrollFactor(0);

    //  agrego planetas ,etc
    this.add.image(512, 680, 'space', 'blue-planet').setOrigin(0).setScrollFactor(0.6);
    this.add.image(2048, 1024, 'space', 'sun').setOrigin(0).setScrollFactor(0.6);
    var galaxy = this.add.image(3500, 1500, 'space', 'galaxy').setBlendMode(1).setScrollFactor(0.6);

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
        frames: this.anims.generateFrameNumbers("coin", {start: 0, end: 5}),
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

    cursors = this.input.keyboard.createCursorKeys();
}

function update(time, delta)
{
    // console.log(socket);
    //acciones cursor
    for (let index = 0; index < asteroides.length; index++) {
        asteroides[i].anims.play('asteroid1-anim', true);
    }
}


window.onload = function () {
    // Crea la conexion con WebSocket
    var page = document.createElement('a');
    page.href = window.location.href;
    //define la url del servidor como la hostname de la pagina y el puerto definido 8080 del ws
    var url = "ws://" + page.hostname + ":8080";
    //servidor Edimbrujo
    //var url = "ws://" + page.hostname + ":60161";
    socket = new WebSocket(url + "/" + window.location.pathname.split('/')[1] + "/GameWebSocket");
    socket.onmessage = stateUpdate;

    //actualiza la vista del juego cuando recive un nuevo estado desde el servidor
    function stateUpdate(event) {
        //console.log(socket);
        //console.log(event.data);
        var gameState = JSON.parse(event.data);
        //console.log(gameState);

        var i = 0;
        while (typeof gameState[i] !== "undefined") {
            //console.log(gameState[i]["Entity"]+"hola")
            if (typeof gameState[i]["Remove"] !== "undefined") {
                var id = gameState[i]["Remove"]["id"];
                if (players[id] != null) {
                    players[id].dispose();
                    players[id] = null;
                }
            } else if (typeof gameState[i]["NavePlayer"] !== "undefined") {
                var id = gameState[i]["NavePlayer"]["super"]['Nave']['super']["Entity"]["super"]["State"]["id"];
                //var playerId = gameState[i]["NavePlayer"]["id"];
                var destroy = gameState[i]["NavePlayer"]["super"]['Nave']['super']["Entity"]["super"]["State"]["destroy"];
                var leave = gameState[i]["NavePlayer"]["leave"];
                var x = gameState[i]["NavePlayer"]["super"]['Nave']['super']["Entity"]["x"];
                var y = gameState[i]["NavePlayer"]["super"]['Nave']['super']["Entity"]["y"];
                // Create a sphere that we will be moved by the keyboard
                if (players[id] == null) {
                    console.log('this' + this);
                    console.log(gameState);
                    players[id] = game.scene.scenes[0].add.sprite(x, y, "ship");
                    console.log(players[id]);
                }
                players[id].y = y;
                players[id].x = x;
                players[id].z = y;

                if (leave) {
                    players[id].dispose();
                }
                if (destroy) {
                    players[id].dispose();
                    players[id] = null;
                }
            } else if (typeof gameState[i]['Entity'] !== "undefined") {
                //console.log("asteroide...................................")
                //console.log(gameState[i]["Entity"]);
                var id = gameState[i]["Entity"]["super"]["State"]["id"];
                var x = gameState[i]["Entity"]["x"];
                var y = gameState[i]["Entity"]["y"];
                //console.log(x);
                //console.log(y);

                if (asteroides[id] == null) {
                    console.log("asigne imagen asteroide");
                    asteroides[id] = game.scene.scenes[0].add.sprite(x, y, "asteroid1");
                }
                asteroides[id].y = y;
                asteroides[id].x = x;
                asteroides[id].z = 200;
            }
            i++;
        }
    }
}