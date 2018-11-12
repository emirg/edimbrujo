window.onload = function () {
//crea el socket para recivir estados del servidor (llama @OnOpen en servidor)
    var socket = new WebSocket("ws://localhost:8080/Edimbrujo/GameWebSocket");

    var game = document.getElementById("game");
    var sendAction = document.getElementById("sendAction");
    var action = document.getElementById("action");

    socket.onmessage = stateUpdate;

//actualiza la vista del juego cuando recive un nuevo estado desde el servidor
    function stateUpdate(event) {
        console.log(event.data);
        //var gameState = JSON.parse(event.data);
        gameState = event.data;
        game.innerHTML = gameState;//gameState.cell.x + " " + gameState.cell.y + " " + gameState.cell.jugador;
    }

//evento al presionar el boton de Enviar Accion
    sendAction.addEventListener("click", function () {
        var actionValue = action.options[action.selectedIndex].value;
        socket.send(actionValue);
    });
}
