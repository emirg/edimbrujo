const button = createAttackButton(
  document.getElementById("attackButtonWrapper")
);

// setInterval(() => console.log(joyattackButton.getPosition()), 16);

// Eventos a considerar:
//  touchstart, touchend, mousedown, mouseup, click

function createAttackButton(parent) {
  const attackButton = document.createElement("div");
  attackButton.classList.add("attackButton");

  attackButton.addEventListener("mousedown", handlePress);
  attackButton.addEventListener("touchstart", handlePress);
  // document.addEventListener("touchstart", handlePress);

  // Los handle probablemente no sean necesarios para el boton de ataque
  function handlePress(event) {
    console.log("ataque");
    socket.send("start");
  }

  function handleRelease(event) {}

  parent.appendChild(attackButton);
  return {
    getPosition: () => currentPos
  };
}
