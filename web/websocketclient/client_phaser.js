var socket;
var socketID = "";

var config = {
    type: Phaser.AUTO,
    parent: 'map',
    width: window.innerWidth,
    height: window.innerHeight,
    scale: {
        mode: Phaser.Scale.FIT,
        autoCenter: Phaser.Scale.CENTER_BOTH,
        width: 2048,
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

var width= 2048;
var height= 2048;
var naveAgente;
var cursors;
var maxVelocity = 150;
var maxVelocity1 = 800;
var coins;
var score = 0;
var scoreText;
var listaNaves=[];
      

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
    //this.cameras.main.setBackgroundColor(0x2a0503);
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

    var EnemyBullet = new Phaser.Class({
  
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
          this.setAngle(listaNaves[0].body.rotation);
          this.setPosition(listaNaves[0].x, listaNaves[0].y);
          this.body.reset(listaNaves[0].x, listaNaves[0].y);
  
         
          var angle = Phaser.Math.DegToRad(listaNaves[0].body.rotation);
      
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

background = this.add.tileSprite(0, 0, 4000, 4000, 'background').setScrollFactor(0);

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

enemyBullets = this.physics.add.group({
    classType: Bullet,
    maxSize: 1,
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

for(var i=0;i<1;i++){
    var x =
      ship.x < 2000
        ? Phaser.Math.Between(0, 2000)
        : Phaser.Math.Between(0, 400);
     var y =
      ship.y < 2000
        ? Phaser.Math.Between(0, 2000)
        : Phaser.Math.Between(0, 400);

    var nave=this.physics.add.sprite(x, y,"ship");
    nave.setDamping(true);
    nave.setDrag(0.99);
    nave.setMaxVelocity(maxVelocity1);
    nave.setCollideWorldBounds(true);
    nave.body.mass=400;
    nave.alive=true;
    nave.id=i;
    listaNaves [i]=nave;
}
//naveAgente=listaNaves[0];
//console.log(naveAgente.alive);




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

this.physics.add.collider(enemyBullets, ship, hitByEnemy, null, this);

for(var i=0;i<5;i++){
    this.physics.add.collider(ship, listaNaves[i], hitShip, null, this);
}


for(var i=0;i<5;i++){
    this.physics.add.collider(bullets, listaNaves[i], hitEnemy, null, this);
}



//camara
//this.cameras.main.setBounds(0,0,width,height);
//this.cameras.main.startFollow(ship);

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
    
  
        if(listaNaves[0].alive){
        seek(listaNaves[0],ship);
        if(time > lastFired && Phaser.Math.Distance.Between(listaNaves[0].x,listaNaves[0].y,ship.x,ship.y)<500){

            shoot(time);
        }
        }
/*
        if(listaNaves[1].alive){
            seek(listaNaves[1],ship);
            }
            if(listaNaves[2].alive){
                seek(listaNaves[2],ship);
                }
                if(listaNaves[3].alive){
                    seek(listaNaves[3],ship);
                    }
                    if(listaNaves[4].alive){
                        seek(listaNaves[4],ship);
                        }
    */
   
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

  function hitByEnemy(bullet, ship) {
    this.physics.pause();
    ship.setTint(0xff0000);
    var gameOver = this.add.text(ship.x-100, ship.y-100, "GAME OVER", {
      fontSize: "64px",
      fill: "red"
    });
  }
  function shoot(time){
 
    var bullet = enemyBullets.get();

    if (bullet)
    {
        bullet.fire(listaNaves[0]);

        lastFired = time + 100;
    }
  }

  function hitEnemy(enemy,bullet){
    this.add.sprite(enemy.x, enemy.y).play('explosion-anim'); 
    enemy.destroy();
    bullet.destroy();
    enemy.alive=false;
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

 

