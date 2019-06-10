var socket;
var socketID = "";
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%5
//%%%%%%%%%%%%%%%% esta de adorno %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%5
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

function empezar() {
    socket.send("start");
}

window.onload = function () {
    var page = document.createElement("a");
    page.href = window.location.href;
    // Define la url del servidor como la hostname de la pagina y el puerto definido 8080 del ws
    //var url = "ws://" + page.hostname + ":8080";
    //servidor Edimbrujo
    var url = "ws://" + page.hostname + ":60161";
    socket = new WebSocket(url + "/" + window.location.pathname.split('/')[1] + "/GameWebSocket");
    socket.onmessage = stateUpdate;
    socket.onopen = empezar;
    function stateUpdate(event) {
        //console.log(socket);
        //console.log(event.data);
        var gameState = JSON.parse(event.data);
        console.log(gameState);

        if (typeof gameState !== "undefined") {
            //console.log(game2State);
            if (gameState["id"] !== "undefined" && socketID === "") {
                socketID = gameState["id"];
                //console.log(socketID);
            }
            var i = 0;
            while (typeof gameState[i] !== "undefined") {
                if (typeof gameState[i]["NavePlayer"] !== "undefined") {
                    var id = gameState[i]["NavePlayer"]["super"]['Nave']['super']["Entity"]["super"]["State"]["id"];
                    var dead = gameState[i]["NavePlayer"]["dead"];
                    if (id == socketID){                        
                        var health = gameState[i]["NavePlayer"]["health"];
                        console.log(health);
                        if (health != null) {
                            updateHealth(health);
                        }
                        if(dead){
                            socket.send("died");
                        }
                    }
                }
                i++;
            }
        }
    }
};
