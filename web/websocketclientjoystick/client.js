var socket;
var socketID = "";

window.onload = function() {
  var page = document.createElement("a");
  page.href = window.location.href;
  //define la url del servidor como la hostname de la pagina y el puerto definido 8080 del ws
  var url = "ws://" + page.hostname + ":8080";
  socket = new WebSocket(url + "/Edimbrujo/GameWebSocket");

  // Este cliente no recibe mensajes, solo envia
  // socket.onmessage = stateUpdate;

  var joystick = document.getElementById("joystick");
  var botonAtaque = document.getElementById("attackButton");

  //evento al presionar el boton de Enviar Accion
  /*sendAction.addEventListener("click", function () {
     var actionValue = action.options[action.selectedIndex].value;
     socket.send(actionValue);
     });*/

  // Accion al presionar el "attackButton"
  botonAtaque.addEventListener("mousedown", function() {
    socket.send("ataque");
  });

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

    areDown: function(keyCodes) {
      var pressed = false;
      var i = 0;
      while (!pressed && i < keyCodes.length) {
        pressed = this._pressed[keyCodes[i]];
        i++;
      }
      return pressed;
    },

    isDown: function(keyCode) {
      return this._pressed[keyCode];
    },

    onKeydown: function(event) {
      this._pressed[event.keyCode] = true;
    },

    onKeyup: function(event) {
      delete this._pressed[event.keyCode];
    }
  };

  window.addEventListener(
    "keyup",
    function(event) {
      Key.onKeyup(event);
    },
    false
  );
  window.addEventListener(
    "keydown",
    function(event) {
      Key.onKeydown(event);
    },
    false
  );

  window.setInterval(function() {
    updateKeyboard();
  }, 100);

  function updateKeyboard() {
    if (
      Key.areDown([Key.UP, Key.ALTUP]) &&
      Key.areDown([Key.LEFT, Key.ALTLEFT])
    ) {
      socket.send("upleft");
    } else if (
      Key.areDown([Key.UP, Key.ALTUP]) &&
      Key.areDown([Key.RIGHT, Key.ALTRIGHT])
    ) {
      socket.send("upright");
    } else if (
      Key.areDown([Key.DOWN, Key.ALTDOWN]) &&
      Key.areDown([Key.LEFT, Key.ALTLEFT])
    ) {
      socket.send("downleft");
    } else if (
      Key.areDown([Key.DOWN, Key.ALTDOWN]) &&
      Key.areDown([Key.RIGHT, Key.ALTRIGHT])
    ) {
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

  //touch click helper
  /*
  (function($) {
    $.fn.tclick = function(onclick) {
      this.bind("touchstart", function(e) {
        onclick.call(this, e);
        e.stopPropagation();
        e.preventDefault();
      });

      this.bind("click", function(e) {
        onclick.call(this, e); //substitute mousedown event for exact same result as touchstart
      });

      return this;
    };
  })(jQuery);*/

  //evento para fire
  $(document).ready(function() {
    $("#scene").mousedown(function(event) {
      var relX = event.pageX - $(this).offset().left;
      var relY = event.pageY - $(this).offset().top;
      relX = parseInt(relX / ($(".cell").width() + 2));
      relY = parseInt(relY / ($(".cell").height() + 2));
      //console.log("(" + relX + "," + relY + ")");
      fire(relX, relY);
    });
  });
};
