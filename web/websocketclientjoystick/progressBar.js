function updateHealth(health) {
  var element = document.getElementById("ProgressBar");
  var width = element.style.width;
  // var identity = setInterval(scene, 10);
  function scene() {
    element.style.width = health + "%";
  }
}
