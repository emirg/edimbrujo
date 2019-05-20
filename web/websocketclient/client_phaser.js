var socket;
var socketID = "";

var config = {
    type: Phaser.AUTO,
    parent: 'map',
    width: 1248,
    height: 600,
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
      extend: {
        player: null,
        healthpoints: null,
        reticle: null,
        moveKeys: null,
        playerBullets: null,
        enemyBullets: null,
        time: 0,
    }
    }
  };

var game = new Phaser.Game(config);
var background;
var stars;
var ship;
var bullets;
var lastFired = 0;
var cursors;
var fire;

var height=2048;
var width=2048;
var naveAgente;
var cursors;
var maxVelocity = 150;
var maxVelocity1 = 800;
var coins;
var score = 0;
var scoreText;
      

function preload() {
  this.load.image('background', 'assets/tests/space/nebula.jpg');
  this.load.image('stars', 'assets/tests/space/stars.png');
  this.load.atlas('space', 'assets/tests/space/space.png', 'assets/tests/space/space.json');
  this.load.spritesheet('ship', 'assets/games/asteroids/ship.png', {
    frameWidth: 32,
    frameHeight: 32
  });
  this.load.spritesheet("coin", "assets/sprites/coin.png", {
    frameWidth: 32,
    frameHeight: 32
  });
  this.load.spritesheet("explosion","assets/sprites/explosion.png",{frameWidth: 64,frameHeight: 64});
  
    
    //create a background and prepare loading bar

    // crear efecto de barra cargandose 
    this.cameras.main.setBackgroundColor(0x2a0503);
    this.fullBar = this.add.graphics();
    this.fullBar.fillStyle(0xda7a34, 1);
    this.fullBar.fillRect((this.cameras.main.width / 4)-2,(this.cameras.main.height /2) - 18, (this.cameras.main.width / 2) + 4, 20);
    this.progress = this.add.graphics();
}

function create() {
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

background = this.add.tileSprite(400, 300, 3000, 3000, 'background').setScrollFactor(0);

//  Add our planets, etc

this.add.image(512, 680, 'space', 'blue-planet').setOrigin(0).setScrollFactor(0.6);
//this.add.image(1024, 1246, 'space', 'brown-planet').setOrigin(0).setScrollFactor(0.6);
this.add.image(2048, 1024, 'space', 'sun').setOrigin(0).setScrollFactor(0.6);
var galaxy = this.add.image(100 ,200, 'space', 'galaxy').setBlendMode(1).setScrollFactor(0.6);
//this.add.image(0, 0, 'space', 'gas-giant').setOrigin(0).setScrollFactor(0.6);
//this.add.image(1600, 1000, 'space', 'brown-planet').setOrigin(0).setScrollFactor(0.6).setScale(0.8).setTint(0x882d2d);
//this.add.image(1700, 200, 'space', 'purple-planet').setOrigin(0).setScrollFactor(0.6);

for (var i = 0; i < 6; i++)
{
    this.add.image(Phaser.Math.Between(0, width), Phaser.Math.Between(0, height), 'space', 'eyes').setBlendMode(1).setScrollFactor(0.8);
}


stars = this.add.tileSprite(400, 300, 2000, 2000, 'stars').setScrollFactor(0);

var particles = this.add.particles('space');

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

bullets = this.physics.add.group({
    classType: Bullet,
    maxSize: 30,
    runChildUpdate: true
});

// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% naves %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

ship = this.physics.add.image(800, 600, 'space', 'ship').setDepth(2);
ship.setDrag(300);
ship.setAngularDrag(400);
ship.setMaxVelocity(600);
ship.setCollideWorldBounds(true);
//ship.setFrictionAir(0.15);
ship.setMass(30);
//ship.setFixedRotation();

naveAgente = this.physics.add.sprite(600, 300,"ship");
naveAgente.setDamping(true);
naveAgente.setDrag(0.99);
naveAgente.setMaxVelocity(maxVelocity1);
naveAgente.setCollideWorldBounds(true);
naveAgente.body.mass=400;




emitter.startFollow(ship);

// coins
coins = this.physics.add.sprite(900, 450, 'coin');

coins.setCollideWorldBounds(true);

this.anims.create({
  key: "efectoMoneda",
  frames: this.anims.generateFrameNumbers("coin", { start: 0, end: 5 }),
  frameRate: 10,
  repeat: -1
});

this.physics.add.overlap(ship, coins,collectCoins, null, this);

this.physics.add.collider(ship, naveAgente, hitShip, null, this);

this.physics.add.collider(bullets, naveAgente, hitEnemy, null, this);


//camara
this.cameras.main.setBounds(0,0,width,height);
this.cameras.main.startFollow(ship);

cursors = this.input.keyboard.createCursorKeys();
fire = this.input.keyboard.addKey(Phaser.Input.Keyboard.KeyCodes.SPACE);

//this.add.sprite(900, 300).play('asteroid1-anim');

asteroide1 = this.physics.add.sprite(300, 200, "asteroid1");
asteroide1.setVelocity(-100, 0);

asteroide2 = this.physics.add.sprite(200, 400, "asteroid2");
asteroide2.setVelocity(-100, 0);

asteroide3 = this.physics.add.sprite(500, 600, "asteroid3");
asteroide3.setVelocity(-100, 0);

asteroide4 = this.physics.add.sprite(600, 800, "asteroid4");
asteroide4.setVelocity(-100, 0);

asteroide5 = this.physics.add.sprite(700, 1000, "asteroid1");
asteroide5.setVelocity(-100, 0);

asteroide6 = this.physics.add.sprite(400, 1200, "asteroid2");
asteroide6.setVelocity(-100, 0);

asteroide7 = this.physics.add.sprite(800, 300, "asteroid3");
asteroide7.setVelocity(-100, 0);

asteroide8 = this.physics.add.sprite(900, 500, "asteroid4");
asteroide8.setVelocity(-100, 0);

asteroide1.body.immovable = true;
asteroide2.body.immovable = true;
asteroide3.body.immovable = true;
asteroide4.body.immovable = true;
asteroide5.body.immovable = true;
asteroide6.body.immovable = true;
asteroide7.body.immovable = true;
asteroide8.body.immovable = true;



this.physics.add.collider(ship, asteroide1, null, null, this);
this.physics.add.collider(ship, asteroide2, null, null, this);
this.physics.add.collider(ship, asteroide3, null, null, this);
this.physics.add.collider(ship, asteroide4, null, null, this);
this.physics.add.collider(ship, asteroide5, null, null, this);
this.physics.add.collider(ship, asteroide6, null, null, this);
this.physics.add.collider(ship, asteroide7, null, null, this);
this.physics.add.collider(ship, asteroide8, null, null, this);

this.tweens.add({
    targets: galaxy,
    angle: 360,
    duration: 100000,
    ease: 'Linear',
    loop: -1
});

    //  The score 
    scoreText = this.add.text(16, 16,"Score: 0").setScrollFactor(0).setFontSize(32).setColor('yellow');
    
}

function update (time, delta)
{
  this.physics.world.wrap(asteroide1, 0);
  this.physics.world.wrap(asteroide2, 0);
  this.physics.world.wrap(asteroide3, 0);
  this.physics.world.wrap(asteroide4, 0);
  this.physics.world.wrap(asteroide5, 0);
  this.physics.world.wrap(asteroide6, 0);
  this.physics.world.wrap(asteroide7, 0);
  this.physics.world.wrap(asteroide8, 0);

  asteroide1.anims.play('asteroid1-anim',true);
  asteroide2.anims.play('asteroid2-anim',true);
  asteroide3.anims.play('asteroid3-anim',true);
  asteroide4.anims.play('asteroid4-anim',true);
  asteroide5.anims.play('asteroid1-anim',true);
  asteroide6.anims.play('asteroid2-anim',true);
  asteroide7.anims.play('asteroid3-anim',true);
  asteroide8.anims.play('asteroid4-anim',true);


  coins.anims.play("efectoMoneda", true);

  //acciones cursor
    if (cursors.left.isDown)
    {
        ship.setAngularVelocity(-150);
    }
    else if (cursors.right.isDown)
    {
        ship.setAngularVelocity(150);
    }
    else
    {
        ship.setAngularVelocity(0);
    }

    if (cursors.up.isDown)
    {
        this.physics.velocityFromRotation(ship.rotation, maxVelocity, ship.body.acceleration);
    }
    else
    {
        ship.setAcceleration(0);
    }

    if (fire.isDown && time > lastFired)
    {
        var bullet = bullets.get();

        if (bullet)
        {
            bullet.fire(ship);

            lastFired = time + 100;
        }
    }

    background.tilePositionX += ship.body.deltaX() * 0.5;
    background.tilePositionY += ship.body.deltaY() * 0.5;

    stars.tilePositionX += ship.body.deltaX() * 2;
    stars.tilePositionY += ship.body.deltaY() * 2;

    // acccion naves agente
    seek(naveAgente,ship);
   
  }


//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
function collectCoins(player, coins) {
    //  Add and update the score
    score += 10;
    var text='Score: ' + score;
    scoreText.setText(text);
    var x =
      player.x < 400
        ? Phaser.Math.Between(400, 800)
        : Phaser.Math.Between(0, 400);
    var y =
      player.y < 400
        ? Phaser.Math.Between(400, 800)
        : Phaser.Math.Between(0, 400);
    coins.setPosition(x, y);
    //coins.setVisible(false);
  }

  function hitShip(player, agente) {
    this.physics.pause();
    player.setTint(0xff0000);
    var gameOver = this.add.text(ship.x-100, ship.y-100, "GAME OVER", {
      fontSize: "64px",
      fill: "red"
    });
  }

  function hitEnemy(bullet, naveAgente){
    this.add.sprite(naveAgente.x, naveAgente.y).play('explosion-anim');
    naveAgente.kill();
  }

  function seek(pVehicle, pTarget) {
    var vectorDesired;
    // 1. vector(desired velocity) = (target position) - (vehicle position)
    vectorDesired = pTarget.body.position.subtract(pVehicle.body.position);
    // 2. normalize vector(desired velocity)
    vectorDesired.normalize();
    // 3. scale vector(desired velocity) to maximum speed
    vectorDesired.scale(maxVelocity1);
    // 4. vector(steering force) = vector(desired velocity) - vector(current velocity)
    var vectorSteering = vectorDesired.subtract(pVehicle.body.velocity);
    // 5. limit the magnitude of vector(steering force) to maximum force
    //vectorSteering.scale(200);
    // 6. vector(new velocity) = vector(current velocity) + vector(steering force)
    vectorSteering.x=vectorSteering.x/pVehicle.body.mass ; 
    vectorSteering.y=vectorSteering.y/pVehicle.body.mass;
    truncate(vectorSteering.add(pVehicle.body.velocity), maxVelocity1);
    pVehicle.setVelocity(vectorSteering.x, vectorSteering.y);
    // 7. limit the magnitude of vector(new velocity) to maximum speed
    //pVehicle.body.velocity.scale(200);
    // 8. update vehicle rotation according to the angle of the vehicle velocity

    var angle = pVehicle.body.velocity.angle();
    //const angle = Phaser.Math.DegToRad(pVehicle.body.rotation - 90);

    pVehicle.rotation = angle;
    pVehicle.body.world.scene.physics.velocityFromRotation(
      angle,
      pVehicle.body.velocity.length(),
      pVehicle.body.velocity
    );
  }

  function setAngle(vector, value) {
    var len = vector.length();
    vector.x = Math.cos(value) * len;
    vector.y = Math.sin(value) * len;
  }

  function truncate(vector, max) {
    var i;
    i = max / vector.length();
    if (i > 1.0) {
      i = 1.0;
    }
    vector.scale(i);
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

    var map = document.getElementById("map");
    var scene = document.getElementById("scene");
    var terrain = document.getElementById("terrain");
    var entities = document.getElementById("entities");
    var ready = document.getElementById("ready");
    var restart = document.getElementById("restart");
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
                    var team = gameState[i]["Player"]["team"];
                    playerTeam[id] = team;
                    var player = document.getElementById("player" + id);
                    if (player === null) {
                        var name = id.substr(0, 4);
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
                } else if (typeof gameState[i]["Projectile"] !== "undefined") {
                    //console.log(game2State[i]["Projectile"]);
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
                        entities.innerHTML += "<div id='projectile" + id + "-" + number + "' class='arrow'><div id='projectileTeam" + id + "-" + number + "'class='arrowteam'></div></div>";
                        projectile = document.getElementById("projectile" + id + "-" + number);
                        var projectileTeam = document.getElementById("projectileTeam" + id + "-" + number);
                        if (id === socketID) {
                            projectileTeam.style.backgroundColor = "green";
                        } else {
                            projectileTeam.style.backgroundColor = $(".team" + team).css("color");
                        }

                    }
                    projectile.style.left = x * ($(".cell").width() + 2) + 1 + ($(".cell").width() + 2) / 2 - $(".arrow").width() / 2 + "px";
                    projectile.style.top = y * ($(".cell").height() + 2) + 1 + ($(".cell").height() + 2) / 2 - $(".arrow").height() / 2 + "px";
                    if (destroy) {
                        $("#projectile" + id + "-" + number).remove();
                    }
                    //players.innerHTML += game2State[i]["Player"]["id"] + "," + game2State[i]["Player"]["x"] + "," + game2State[i]["Player"]["y"] + "<br>";
                } else if (typeof gameState[i]["Tower"] !== "undefined") {
                    //console.log(gameState[i]["Tower"]);
                    var id = gameState[i]["Tower"]["id"];
                    var destroy = gameState[i]["Tower"]["super"]["Entity"]["super"]["State"]["destroy"];
                    var dead = gameState[i]["Tower"]["dead"];
                    var team = gameState[i]["Tower"]["team"];
                    var health = gameState[i]["Tower"]["health"];
                    var healthMax = gameState[i]["Tower"]["healthMax"];
                    var width = gameState[i]["Tower"]["width"];
                    var height = gameState[i]["Tower"]["height"];
                    var x = gameState[i]["Tower"]["super"]["Entity"]["x"];
                    var y = gameState[i]["Tower"]["super"]["Entity"]["y"];
                    var team = gameState[i]["Tower"]["team"];
                    var tower = document.getElementById("tower" + id);
                    if (tower === null) {
                        entities.innerHTML += "<div id='tower" + id + "' class='tower'><div id='tower" + id + "-healthbar' class='healthbar'></div></div>";
                        tower = document.getElementById("tower" + id);
                        //tower.style.backgroundColor = $(".team" + team).css("color");
                    }
                    tower.style.left = x * ($(".cell").width() + 2) + 1 - (Math.floor(width / 2) * $(".cell").width()) + "px";
                    tower.style.top = -60 + y * ($(".cell").height() + 2) + 1 - (Math.floor(height / 2) * $(".cell").width()) + "px";
                    towerHealthbar = document.getElementById("tower" + id + "-healthbar");
                    towerHealthbar.style.width = health * width * 24 / healthMax + "px";
                    towerHealthbar.style.left = -8 + "px";
                    if (dead) {
                        //tower.style.zIndex = "1";
                        //tower.style.removeProperty('backgroundImage');
                        tower.style.backgroundImage = "url('images/mapa_2018/torre_rota.png')";
                        tower.style.backgroundSize ="100% 50%";
                        tower.style.marginTop = "45px";
                        //tower.style.backgroundSize ="100% 50%";
                        //tower.style.marginTop = "45px";
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
                    //console.log(game2State[i]["Match"]);
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
    
    restart.addEventListener("click", function () {
        socket.send("restart");
    });
/*
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
    */
/*
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
    */

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