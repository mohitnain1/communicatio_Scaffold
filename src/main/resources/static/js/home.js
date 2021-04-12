
function enableJoin() {
	let username = document.querySelector("#username");
	let join = document.querySelector("#join");
	
  if (document.querySelector("#username").value === "") {
    join.disabled = true; //button remains disabled
    share.disabled = true;
  } else {
    join.disabled = false; //button is enabled
    share.disabled = false;
  }
}