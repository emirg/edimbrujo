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

//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% COMENTARIOS %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
// * falta barriles o monedas logicas 
// problemas con id bullet como es el mismo que la nave solo permite una bala a la vez 

var game = new Phaser.Game(config);

//coneccion
var socket;
var socketID = "";
var objetosNuevos = [];
//cursor
var cursors;

//dimensiones juego
var width = 4500;
var height = 2048;

//arreglos
var asteroides = [];
var players = [];
var neutras = [];
var coins;
var particles;
var bullets=[];


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
    this.load.spritesheet('bullet', 'assets/sprites/bullets/bullet11.png', {
        frameWidth: 64,
        frameHeight: 64
    });

    // nave neutra
    this.load.spritesheet('shipNeutra', 'assets/sprites/ship.png', {
        frameWidth: 93,
        frameHeight: 110
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
    this.progress = this.add.graphics();
}

function create() {
    //console.log(game.scene.scenes[0]==this);
    //console.log(this);
  

    //console.log("CREATE");
    //  Prepare some spritesheets and animations
    this.textures.addSpriteSheetFromAtlas('mine-sheet', {atlas: 'space', frame: 'mine', frameWidth: 64});
    this.textures.addSpriteSheetFromAtlas('asteroid1-sheet', {atlas: 'space', frame: 'asteroid1', frameWidth: 96});
    this.textures.addSpriteSheetFromAtlas('asteroid2-sheet', {atlas: 'space', frame: 'asteroid2', frameWidth: 96});
    this.textures.addSpriteSheetFromAtlas('asteroid3-sheet', {atlas: 'space', frame: 'asteroid3', frameWidth: 96});
    this.textures.addSpriteSheetFromAtlas('asteroid4-sheet', {atlas: 'space', frame: 'asteroid4', frameWidth: 64});
    //this.textures.addSpriteSheetFromAtlas('explosion-sheet', {atlas: 'space', frame: 'asteroid1', frameWidth: 96});



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

    particles = this.add.particles('space');

    // coins
    coins = this.physics.add.sprite(900, 450, 'coin');

    coins.setCollideWorldBounds(true);

    this.anims.create({
    key: "efectoMoneda",
    frames: this.anims.generateFrameNumbers("coin", { start: 0, end: 5 }),
    frameRate: 10,
    repeat: -1
    });

    //this.physics.add.overlap(ship, coins,collectCoins, null, this);

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

// Retorna un entero aleatorio entre min (incluido) y max (excluido)
// ¡Usando Math.round() te dará una distribución no-uniforme!
function getRandomInt(min, max) {
    return Math.floor(Math.random() * (max - min)) + min;
  }

function update(time, delta)
{
    coins.anims.play("efectoMoneda", true);

    for (let i = 0; i < asteroides.length; i++) {
        switch (i) {
            case 1:
                asteroides[i].anims.play('asteroid1-anim', true);
                break;
            case 2:
                asteroides[i].anims.play('asteroid2-anim', true);
                break;
            case 3:
                asteroides[i].anims.play('asteroid3-anim', true);
                break
            default:
                asteroides[i].anims.play('asteroid4-anim', true);
                break;
        }
        //var sprite=asteroides[i];
        //console.log(sprite.anims);
    }
}

function particle(ship,id){
     //particulas
     //console.log(ship);
     //console.log(players[id]);
        var emitter = particles.createEmitter({
            frame: 'blue',
            speed: 100,
            lifespan: {
                onEmit: function (particle, key, t, value)
                {
                    return Phaser.Math.Percent(ship.body.speed, 0, 300) * 2000;
                }
            },
            alpha: {
                onEmit: function (particle, key, t, value)
                {
                    return Phaser.Math.Percent(ship.body.speed, 0, 300);
                }
            },
            angle: {
                onEmit: function (particle, key, t, value)
                {
                    var v = Phaser.Math.Between(-10, 10);
                    return (ship.angle - 180) + v;
                }
            },
            scale: { start: 0.6, end: 0 },
            blendMode: 'ADD'
        });
        
        emitter.startFollow(ship);
    
}

function collectCoins(player, coins) {
    //  Add and update the score
    //score += 10;
    //var text='Score: ' + score;
    //scoreText.setText(text);
    var x =
      player.x < 400
        ? Phaser.Math.Between(100, 3000)
        : Phaser.Math.Between(0, 1000);
    var y =
      player.y < 400
        ? Phaser.Math.Between(100, 3000)
        : Phaser.Math.Between(0, 1000);
    coins.setPosition(x, y);
    //coins.setVisible(false);
}
function hitAsteroide(player,asteroide) {
    game.scene.scenes[0].add.sprite(player.x, player.y).play('explosion-anim'); 
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
            //console.log(gameState);
            if (typeof gameState[i]["Remove"] !== "undefined") {
                console.log("remove");
                var id = gameState[i]["Remove"]["id"];
                if (players[id] != null) {
                    players[id].destroy;
                    players[id] = null;
                }else{
                    if(bullets[id]!=null){
                        bullets[id].destroy;
                        bullets[id] = null;
                    }
                }
            } else if (typeof gameState[i]["NavePlayer"] !== "undefined") {
                var id = gameState[i]["NavePlayer"]["super"]['Nave']['super']["Entity"]["super"]["State"]["id"];
                //var playerId = gameState[i]["NavePlayer"]["id"];
                var destroy = gameState[i]["NavePlayer"]["super"]['Nave']['super']["Entity"]["super"]["State"]["destroy"];
                var leave = gameState[i]["NavePlayer"]["leave"];
                var dead = gameState[i]["NavePlayer"]["dead"];
                var x = gameState[i]["NavePlayer"]["super"]['Nave']['super']["Entity"]["x"];
                var y = gameState[i]["NavePlayer"]["super"]['Nave']['super']["Entity"]["y"];
                var health = gameState[i]["NavePlayer"]["health"];
                //console.log(leave);
                // Create a sphere that we will be moved by the keyboard
                if (players[id] == null) {
                    //console.log('this' + this);
                    //console.log(gameState);
                    players[id] = game.scene.scenes[0].physics.add.sprite(x, y, "ship");
                    //players[id] .setDrag(300);
                    //players[id].setDamping(true);
                    //players[id] .setAngularDrag(400);
                    //players[id] .setMaxVelocity(600);
                    //players[id] .setCollideWorldBounds(true);
                    game.scene.scenes[0].physics.add.overlap(players[id], coins,collectCoins, null, this);
                    for (let i = 0; i < asteroides.length; i++) {
                        //console.log(asteroides[i]);
                        //console.log(players[id]);
                        //console.log("moneda");
                        //console.log(coins);
                        game.scene.scenes[0].physics.add.collider(players[id], asteroides[i], hitAsteroide, null, this);
                        //console.log("colision");
                    }
                    if(particles!== undefined){
                        //particle(players[id],id);
                        console.log("particules");
                    }
                    
                    //console.log(players[id]);
                }
                players[id].y = y;
                players[id].x = x;
                players[id].z = 0;

                if (leave) {
                    players[id].destroy();  
                    //players.splice(id);
                    //console.log(players[id]);
                }
                //console.log(destroy);
                if (destroy) {
                    players[id].destroy();
                    //players[id] = null;
                }
            } else if (typeof gameState[i]['Asteroide'] !== "undefined") {
                //console.log("asteroide...................................")
                //console.log(gameState[i]["Asteroide"]['super']["Entity"]["super"]['State']);
                var id = gameState[i]["Asteroide"]['super']["Entity"]["super"]["State"]["id"];
                var x = gameState[i]["Asteroide"]['super']["Entity"]["x"];
                var y = gameState[i]["Asteroide"]['super']["Entity"]["y"];
                //console.log(x);
                //console.log(y);

                if (asteroides[id] == null) {
                    console.log("asigne imagen asteroide");
                    asteroides[id] = game.scene.scenes[0].physics.add.sprite(x, y, "asteroid1");
                    asteroides[id].setDepth(1);

                    //asteroides[id]=scene.physics.add.sprite(x, y, "asteroid1");
                }
                asteroides[id].y = y;
                asteroides[id].x = x;
                asteroides[id].z = y;

            }else if (typeof gameState[i]["NaveNeutra"] !== "undefined") {
                var id = gameState[i]["NaveNeutra"]["super"]['Nave']['super']["Entity"]["super"]["State"]["id"];
                var destroy = gameState[i]["NaveNeutra"]["super"]['Nave']['super']["Entity"]["super"]["State"]["destroy"];
                var leave = gameState[i]["NaveNeutra"]["leave"];
                var x = gameState[i]["NaveNeutra"]["super"]['Nave']['super']["Entity"]["x"];
                var y = gameState[i]["NaveNeutra"]["super"]['Nave']['super']["Entity"]["y"];
                var health = gameState[i]["NaveNeutra"]["health"];
                // Create a sphere that we will be moved by the keyboard
                if (neutras[id] == null) {
                    //console.log("asigne imagen naveNeutra");
                    //console.log(gameState);
                    neutras[id] = game.scene.scenes[0].add.sprite(x, y, "shipNeutra");
                    //neutras[id] = scene.add.sprite(x, y, "ship");
                    neutras[id].setDepth(1);
                    //console.log(players[id]);
                }
                neutras[id].y = y;
                neutras[id].x = x;
                neutras[id].z = y;
            }else if (typeof gameState[i]["Proyectil"] !== "undefined") {
                console.log(gameState);
                var id = gameState[i]["Proyectil"]['super']["Entity"]["super"]["State"]["id"];
                var x = gameState[i]["Proyectil"]['super']["Entity"]["x"];
                var y = gameState[i]["Proyectil"]['super']["Entity"]["y"];
                //var destroy =gameState[i]["Proyectil"]['super']["Entity"]["super"]["State"]["destroy"];
                
                if(bullets[id]==null){
                    bullets[id] = game.scene.scenes[0].add.sprite(x, y, 'bullet');
                }
                bullets[id].y = y;
                bullets[id].x = x;
                bullets[id].z = y;
            }
            i++;
        }
    }
};
var Bullet = new Phaser.Class({

    Extends: Phaser.Physics.Arcade.Image,

    initialize:

    function Bullet (scene)
    {
        Phaser.Physics.Arcade.Image.call(this, scene, 0, 0, 'space', 'blaster');

        this.setBlendMode(1);
        this.setDepth(1);

        this.speed = 1000;
        this.lifespan = 1000;

        this._temp = new Phaser.Math.Vector2();
    },
    
    fire: function (ship)
    {
        this.lifespan = 1000;

        this.setActive(true);
        this.setVisible(true);
        // this.setRotation(ship.rotation);
        this.setAngle(ship.body.rotation);
        this.setPosition(ship.x, ship.y);
        this.body.reset(ship.x, ship.y);

       
        var angle = Phaser.Math.DegToRad(ship.body.rotation);
    
        this.scene.physics.velocityFromRotation(angle, this.speed, this.body.velocity);

        this.body.velocity.x *= 2;
        this.body.velocity.y *= 2;
    },
    update: function (time, delta)
    {
        this.lifespan -= delta;

        if (this.lifespan <= 0)
        {
            this.setActive(false);
            this.setVisible(false);
            this.body.stop();
        }
    }

});