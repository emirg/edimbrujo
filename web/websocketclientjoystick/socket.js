var socket;
var socketID = "";

window.onload = function() {
  var page = document.createElement("a");
  page.href = window.location.href;
  // Define la url del servidor como la hostname de la pagina y el puerto definido 8080 del ws
  var url = "ws://" + page.hostname + ":8080";
  socket = new WebSocket(url + "/Edimbrujo/GameWebSocket");
  socket.onmessage = stateUpdate;

  function stateUpdate(event) {
    var gameState = JSON.parse(event.data);
    if (typeof gameState !== "undefined") {
      if (gameState["id"] !== "undefined" && socketID === "") {
        socketID = gameState["id"];
        //console.log(socketID);
      }
      var i = 0;
      while (typeof gameState[i] !== "undefined") {
        //console.log(game2State[i]);
        if (typeof gameState[i]["Health"] !== "undefined") {
          var health = gameState[i]["Health"];
          updateHealth(health);
        }
      }
    }
  }
};
