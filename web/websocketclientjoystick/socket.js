var socket;
var socketID = "";

window.onload = function() {
  var page = document.createElement("a");
  page.href = window.location.href;
  // Define la url del servidor como la hostname de la pagina y el puerto definido 8080 del ws
  var url = "ws://" + page.hostname + ":8080";
  socket = new WebSocket(url + "/StateEngine/GameWebSocket");
  socket.onmessage = stateUpdate;
  //socket.send("start");

  function stateUpdate(event) {
   //console.log(socket);
        //console.log(event.data);
        var gameState = JSON.parse(event.data);
        console.log(gameState);

        var i = 0;
        while (typeof gameState[i] !== "undefined") {
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
                    console.log('this'+this);
                    console.log(game);
                    players[id] =  game.scene.scenes[0].add.sprite(x, y, "ship");
                    console.log(players[id]);
                }


                if (leave) {
                    players[id].dispose();
                }
                if (destroy) {
                    players[id].dispose();
                    players[id] = null;
                }
            }
            i++;
        }
  }
};
