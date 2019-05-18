const button = createAttackButton(
  document.getElementById("attackButtonWrapper")
);

// setInterval(() => console.log(joyattackButton.getPosition()), 16);

function createAttackButton(parent) {
  const attackButton = document.createElement("div");
  attackButton.classList.add("attackButton");

  attackButton.addEventListener("mousedown", handlePress);
  attackButton.addEventListener("touchstart", handlePress);
  document.addEventListener("mouseup", handleRelease);
  document.addEventListener("touchend", handleRelease);

  function handlePress(event) {}

  function handleRelease(event) {}

  parent.appendChild(attackButton);
  return {
    getPosition: () => currentPos
  };
}
