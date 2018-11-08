var socket = new WebSocket("ws://localhost:8080/Edimbrujo/serverWSocket");
socket.onmessage = onMessage;

function onMessage(event) {
    var lblAccion = document.getElementById("lblAccion");
    var data = JSON.parse(event.data);
    lblAccion.innerHTML = data.cell.x + " " + data.cell.y + " " + data.cell.jugador;
}

function sendAccion() {
    var accion = document.getElementById("txtAccion");
    console.log("presiona");
    socket.send("{'accion':'" + accion.value + "'}");
    console.log("envia accion");
}


