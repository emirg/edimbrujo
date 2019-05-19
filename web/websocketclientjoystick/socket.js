var socket;
var socketID = "";

window.onload = function() {
  var page = document.createElement("a");
  page.href = window.location.href;
  // Define la url del servidor como la hostname de la pagina y el puerto definido 8080 del ws
  var url = "ws://" + page.hostname + ":8080";
  socket = new WebSocket(url + "/Edimbrujo/GameWebSocket");

  // Este cliente no recibe mensajes, solo envia
  // socket.onmessage = stateUpdate;
};
