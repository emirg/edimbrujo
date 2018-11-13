window.onload = function () {
//crea el socket para recivir estados del servidor (llama @OnOpen en servidor)
    var socket = new WebSocket("ws://localhost:8080/Edimbrujo/GameWebSocket");
    //var game = document.getElementById("game");
    var terrain = document.getElementById("terrain");
    //terrain.innerHTML = "TERRAIN<br>";
    var players = document.getElementById("players");
    //players.innerHTML = "PLAYERS<br>";
    var sendAction = document.getElementById("sendAction");
    var action = document.getElementById("action");
    socket.onmessage = stateUpdate;

//actualiza la vista del juego cuando recive un nuevo estado desde el servidor
    function stateUpdate(event) {
        //console.log(event.data);
        var gameState = JSON.parse(event.data);
        //var gameState = event.data;
        /*console.log(gameState);
         console.log(gameState["0"]);
         console.log(gameState["0"]["Map"]);
         console.log(gameState["0"]["Player"]);*/
        if (typeof gameState !== "undefined") {
            //console.log(gameState);
            var i = 0;
            while (typeof gameState[i] !== "undefined") {
                //console.log(gameState[i]);
                if (typeof gameState[i]["Map"] !== "undefined") {
                    console.log(gameState[i]["Map"]);
                    var j = 0;
                    var x;
                    var xMax = 0;
                    var y;
                    var yMax = 0;
                    var cell;
                    while (typeof gameState[i]["Map"][j] !== "undefined") {
                        x = gameState[i]["Map"][j]["x"];
                        xMax = x > xMax ? x : xMax;
                        y = gameState[i]["Map"][j]["y"];
                        yMax = y > yMax ? x : yMax;
                        terrain.innerHTML += "<div id='cell" + x + "_" + y + "' class='wall'></div>";
                        cell = document.getElementById("cell" + x + "_" + y);
                        cell.style.left = x * $(".wall").width() + "px";
                        cell.style.top = y * $(".wall").height() + "px";
                        j++;
                    }
                    terrain.style.width = xMax * $(".wall").width() + $(".wall").width() + "px";
                    terrain.style.height = yMax * $(".wall").height() + $(".wall").height() + "px";
                }
                if (typeof gameState[i]["Player"] !== "undefined") {
                    console.log(gameState[i]["Player"]);
                    var id = gameState[i]["Player"]["id"];
                    var leave = gameState[i]["Player"]["leave"];
                    var x = gameState[i]["Player"]["x"];
                    var y = gameState[i]["Player"]["y"];
                    player = document.getElementById("player" + id);
                    if (player === null) {
                        players.innerHTML += "<div id='player" + id + "' class='player'></div>";
                        player = document.getElementById("player" + id);
                        player.style.background = "#" + ((1 << 24) * Math.random() | 0).toString(16);
                    }
                    player.style.left = x * $(".player").width() + "px";
                    player.style.top = y * $(".player").height() + "px";
                    if (leave) {
                        $("#player" + id).remove();
                    }
                    //players.innerHTML += gameState[i]["Player"]["id"] + "," + gameState[i]["Player"]["x"] + "," + gameState[i]["Player"]["y"] + "<br>";
                }
                i++;
            }
        }
    }

//evento al presionar el boton de Enviar Accion
    sendAction.addEventListener("click", function () {
        var actionValue = action.options[action.selectedIndex].value;
        socket.send(actionValue);
    });

    $('body').on('keydown', function (e) {
        console.log("APRETO");
        if (e.which === 38) {
            socket.send("up");
        } else if(e.which === 40) {
            socket.send("down");
        } else if(e.which === 37) {
            socket.send("left");
        } else if(e.which === 39) {
            socket.send("right");
        }
    });
}