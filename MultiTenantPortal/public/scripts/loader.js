function getLoader() {
	var getLo = document.getElementById('spinner-container');
	getLo.style.visibility = "visible";
	setTimeout(function() {
		getLo.style.visibility = "hidden";
	}, 7000);
}
