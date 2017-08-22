	
	//get the parents off the differences
	var x = document.getElementsByClassName("diffParentEntry");
	//get the child differences
	var y = document.getElementsByClassName("diffEntry");
	// get the canvas element using the DOM
	var canvas = document.getElementById('myCanvas');
	var ctx = canvas.getContext('2d');
	ctx.canvas.height = document.getElementById('tr1').offsetHeight;
	// get the row where the Next and Previous Buttons are going to be implemented
	var i;
	var currentIDParent = -1;
	var currentIDChild = -1;

	var yHalf;
	if(y.length % 2 == 1)	
		yHalf = (y.length+1)/2;
	else
		yHalf = y.length/2;

	for(i = 0 ; i < x.length; i++){
		var value = parseInt(x[i].getAttribute("data-diff-parent-id"));

		x[i].setAttribute("identify1", "0");

		var string1 = "mouseOverParent(";
	   	string1 = string1.concat(value);
		string1 = string1.concat(")");
		x[i].setAttribute("onmouseover", string1);

		var string2 = "mouseOutParent(";
	   	string2 = string2.concat(value);
		string2 = string2.concat(")");
		x[i].setAttribute("onmouseout", string2);

		var string3 = "onClickEventParent(";
		string3 = string3.concat(value);
		string3 = string3.concat(")");
		x[i].setAttribute("onclick", string3);
	}

	
	
	for(i = 0 ; i < y.length; i++){
		var value = parseInt(y[i].getAttribute("data-diff-id"));

		y[i].setAttribute("identify", "0");

		var string1 = "mouseOverChild(";
	   	string1 = string1.concat(value);
		string1 = string1.concat(")");
		y[i].setAttribute("onmouseover", string1);

		var string2 = "mouseOutChild(";
	   	string2 = string2.concat(value);
		string2 = string2.concat(")");
		y[i].setAttribute("onmouseout", string2);

		var string3 = "onClickEventChild(";
		string3 = string3.concat(value);
		string3 = string3.concat(")");
		y[i].setAttribute("onclick", string3);
	}

	fillCanvas(null,null);
	
    function getColor(e){
    		var value = parseInt(e.getAttribute("data-diff-parent-id"));
	    	var classList = e.className.split(' ');
	    	var colorForBackgrund = classList[1];
	    	var color;
	    	if(colorForBackgrund == "diffParentTypeConflict"){
	    			color = 'rgb('+255+',' +230+','+230+')';
	    	}else if(colorForBackgrund == "diffParentTypeOutgoing"){
	    			color = 'rgb('+240+',' +245+',' +245+')';
	    	}else if(colorForBackgrund == "diffParentTypeIncoming"){
	    			color = 'rgb('+204+',' +242+','+255+')';
	    	}

	    	return color;
    }

	function mouseOverParent(e) {

	    var i;
	    for(i = 0; i < x.length; i++){  
	    	var value = parseInt(x[i].getAttribute("data-diff-parent-id"));
	    	if(value == e){
	    		var classList = x[i].className.split(' ');
	    		var colorForBackgrund = classList[1];
	    		var color;


	    		if(colorForBackgrund == "diffParentTypeConflict"){
	    			color = 'rgb('+255+',' +210+','+ 210+')';
	    		}else if(colorForBackgrund == "diffParentTypeOutgoing"){
	    			color = 'rgb('+209+',' +224+',' +224+')';
	    		}else if(colorForBackgrund == "diffParentTypeIncoming"){
	    			color = 'rgb('+179+',' +236+','+255+')';
	    		}

	    		x[i].style.background = color;
 				fillCanvas(x[i], color);

	    		// fillCanvas(x[i], color);
	    		x[i].style.opacity = "0.9";
	    	}
	    }
	}

	function mouseOutParent(e) {

	    var i;
	    for(i = 0; i < x.length; i++){
	    	var value = parseInt(x[i].getAttribute("data-diff-parent-id"));
	    	if(value == e){

	    		var classList = x[i].className.split(' ');
	    		var colorForBackgrund = classList[1];
	    		var color;

	    		if(colorForBackgrund == "diffParentTypeConflict"){
	    			color= 'rgb('+255+',' +230+','+230+')';
	    		}else if(colorForBackgrund == "diffParentTypeOutgoing"){
	    			color = 'rgb('+240+',' +245+',' +245+')';
	    		}else if(colorForBackgrund == "diffParentTypeIncoming"){
	    			color = 'rgb('+204+',' +242+','+255+')';
	    		}

	    		x[i].style.background = color;
	    		fillCanvas(x[i], color);
	    		// fillCanvas(x[i], color);
	    		x[i].style.opacity = "0.81";
	    	}
	    }
	}




	function mouseOverChild(e) {
	    var list = document.getElementsByClassName("diffEntry");
	    var i;
	    for(i = 0; i < list.length; i++){
	    	var value = parseInt(y[i].getAttribute("data-diff-id"));
	    	if(value == e){
	    		var classList = y[i].className.split(' ');
	    		var colorForBackgrund = classList[1];

	    		if(colorForBackgrund == "diffTypeConflict"){
	    			y[i].style.background = 'rgb('+255+',' +133+','+133+')';
	    		}else if(colorForBackgrund == "diffTypeOutgoing"){
	    			y[i].style.background = 'rgb('+204+',' +204+','+204+')';
	    		}else if(colorForBackgrund == "diffTypeIncoming"){
	    			y[i].style.background = 'rgb('+128+',' +206+','+255+')';
	    		}
	    	// 	y[i].style.background = 'rgb('+255+',' +255+','+0+')';
	    	// 	y[i].style.opacity = "0.8";
	    	}
	    }
	}


	function mouseOutChild(e) {
	    var list = document.getElementsByClassName("diffEntry");
	    var i;
	    for(i = 0; i < list.length; i++){
	    	var value = parseInt(y[i].getAttribute("data-diff-id"));
	    	if(value == e){

	    		var classList = y[i].className.split(' ');
	    		var colorForBackgrund = classList[1];

	    		if(colorForBackgrund == "diffTypeConflict"){
	    			y[i].style.background = 'rgb('+255+',' +179+','+179+')';
	    		}else if(colorForBackgrund == "diffTypeOutgoing"){
	    			y[i].style.background = 'rgb('+221+',' +221+',' +221+')';
	    		}else if(colorForBackgrund == "diffTypeIncoming"){
	    			y[i].style.background = 'rgb('+209+',' +237+','+255+')';
	    		}

	    		y[i].style.opacity = "1.0";
	    	}
	    }
	}


	function onClickEventParent(e){
		var i;
		var id1,id2;
		for(i = 0; i < x.length/2; i++){
			var value1 = parseInt(x[i].getAttribute("data-diff-parent-id"));

			var classList = x[i].className.split(' ');
	   	 	var colorForBackgrund = classList[1];
	    	var concat;
	    	if(colorForBackgrund == "diffParentTypeConflict"){
	    		concat = "diffParentTypeConflict_sel";
	    	}else if(colorForBackgrund == "diffParentTypeOutgoing"){
	    		concat = "diffParentTypeOutgoing_sel";
	    	}else if(colorForBackgrund == "diffParentTypeIncoming"){
	    		concat = "diffParentTypeIncoming_sel";
	    	}

	    	if(x[i].classList.contains(concat)){
				x[i].classList.remove(concat); 
	    	}

			if(value1 == e){
				id1 = i;
				
			}else{
				x[i].removeAttribute("identify1");
				x[i].setAttribute("identify1", "0");
			}
		}

		for(i = x.length/2; i < x.length; i++){
			var value2 = parseInt(x[i].getAttribute("data-diff-parent-id"));
			
			var classList = x[i].className.split(' ');
	   	 	var colorForBackgrund = classList[1];
	    	var concat;
	    	if(colorForBackgrund == "diffParentTypeConflict"){
	    		concat = "diffParentTypeConflict_sel";
	    	}else if(colorForBackgrund == "diffParentTypeOutgoing"){
	    		concat = "diffParentTypeOutgoing_sel";
	    	}else if(colorForBackgrund == "diffParentTypeIncoming"){
	    		concat = "diffParentTypeIncoming_sel";
	    	}

			if(x[i].classList.contains(concat)){
				x[i].classList.remove(concat);
	    	}


			if(value2 == e){
				id2 = i;
				
			}else{
				x[i].removeAttribute("identify1");
				x[i].setAttribute("identify1", "0");
			}
		}


	    var classList = x[id1].className.split(' ');
	    var colorForBackgrund = classList[1];
	    var concat;
	    if(colorForBackgrund == "diffParentTypeConflict"){
	    		concat = "diffParentTypeConflict_sel";
	    }else if(colorForBackgrund == "diffParentTypeOutgoing"){
	    		concat = "diffParentTypeOutgoing_sel";
	    }else if(colorForBackgrund == "diffParentTypeIncoming"){
	    		concat = "diffParentTypeIncoming_sel";
	    }
   
	    // let computedStyle = getComputedStyle(x[id1]);
    	// alert( computedStyle.marginTop ); 
    	// alert( computedStyle.color ); 
    	
    	x[id1].classList.add(concat); 
    	x[id2].classList.add(concat);
    	currentIDParent = id1;


	    var val = parseInt(x[id1].getAttribute("identify1"));
	    if(val == 0){
			x[id1].removeAttribute("identify1");
			x[id1].setAttribute("identify1", "1");
			x[id2].removeAttribute("identify1");
			x[id2].setAttribute("identify1", "1");
		} else {
	    	x[id1].classList.remove(concat); 
	    	x[id2].classList.remove(concat);

			x[id1].removeAttribute("identify1");
			x[id1].setAttribute("identify1", "0");
			x[id2].removeAttribute("identify1");
			x[id2].setAttribute("identify1", "0");
			currentIDParent = -1;
		}

		
		
	}

	function onClickEventChild(e){
		var i;
		var id1,id2;
		for(i = 0; i < yHalf; i++){
			var value1 = parseInt(y[i].getAttribute("data-diff-id"));

			var classList = y[i].className.split(' ');
	   	 	var colorForBackgrund = classList[1];
	    	var concat;
	    	if(colorForBackgrund == "diffTypeConflict"){
	    		concat = "diffTypeConflict_sel";
	    	}else if(colorForBackgrund == "diffTypeOutgoing"){
	    		concat = "diffTypeOutgoing_sel";
	    	}else if(colorForBackgrund == "diffTypeIncoming"){
	    		concat = "diffTypeIncoming_sel";
	    	}

			if(y[i].classList.contains(concat)){
				y[i].classList.remove(concat); 
	    	} else {
				y[i].removeAttribute("identify");
				y[i].setAttribute("identify", "0");
			}

			if(value1 == e){
				id1 = i;
				
			}
		}

		for(i = yHalf; i < y.length; i++){
			console.log("half: ",yHalf, "whole: ", y.length);
			var value2 = parseInt(y[i].getAttribute("data-diff-id"));
			
			var classList = y[i].className.split(' ');
	   	 	var colorForBackgrund = classList[1];
	    	var concat;
	    	if(colorForBackgrund == "diffTypeConflict"){
	    		concat = "diffTypeConflict_sel";
	    	}else if(colorForBackgrund == "diffTypeOutgoing"){
	    		concat = "diffTypeOutgoing_sel";
	    	}else if(colorForBackgrund == "diffTypeIncoming"){
	    		concat = "diffTypeIncoming_sel";
	    	}

			if(y[i].classList.contains(concat)){
				y[i].classList.remove(concat); 
	    	} else {
				y[i].removeAttribute("identify");
				y[i].setAttribute("identify", "0");
			}

			if(value2 == e){
				id2 = i;
				
			}
		}


	    var classList = y[id1].className.split(' ');
	    var colorForBackgrund = classList[1];
	    var concat;
	    if(colorForBackgrund == "diffTypeConflict"){
	    		concat = "diffTypeConflict_sel";
	    }else if(colorForBackgrund == "diffTypeOutgoing"){
	    		concat = "diffTypeOutgoing_sel";
	    }else if(colorForBackgrund == "diffTypeIncoming"){
	    		concat = "diffTypeIncoming_sel";
	    }
   
	    // let computedStyle = getComputedStyle(x[id1]);
    	// alert( computedStyle.marginTop ); 
    	// alert( computedStyle.color ); 

    	y[id1].classList.add(concat); 
    	y[id2].classList.add(concat);
    	currentIDChild = id1;


	    var val = parseInt(y[id1].getAttribute("identify"));
	    if(val == 0){
			y[id1].removeAttribute("identify");
			y[id1].setAttribute("identify", "1");
			y[id2].removeAttribute("identify");
			y[id2].setAttribute("identify", "1");
		} else {
	    	y[id1].classList.remove(concat); 
	    	y[id2].classList.remove(concat);

			y[id1].removeAttribute("identify");
			y[id1].setAttribute("identify", "0");
			y[id2].removeAttribute("identify");
			y[id2].setAttribute("identify", "0");
			currentIDChild = -1;
		}

	}



	function nextDiff(){
		if(currentIDParent == -1 || currentIDParent == x.length/2 - 1){
			onClickEventParent(0);
		} else {
			onClickEventParent(currentIDParent + 1);
		}
	}

	function previousDiff(){
		if(currentIDParent == -1 || currentIDParent == 0){
			onClickEventParent(x.length/2 - 1);
		} else {
			onClickEventParent(currentIDParent - 1);
		}
	}

	function nextChildDiff(){
		if(currentIDChild == -1 || currentIDChild == yHalf - 1){
			onClickEventChild(0);
		} else {
			onClickEventChild(currentIDChild + 1);
		}
	}

	function previousChildDiff(){
		if(currentIDChild == -1 || currentIDChild == 0){
			console.log(currentIDChild);
			onClickEventChild((yHalf-1));
		} else {
			console.log((y.length), " ", currentIDChild);
			onClickEventChild(currentIDChild - 1);
		}
	}

	function swapTexts(){
		var firstDoc = document.getElementById('b1');
		var secondDoc = document.getElementById('b2');
		var aux = firstDoc.innerHTML;
		firstDoc.innerHTML = secondDoc.innerHTML;
		secondDoc.innerHTML = aux;
		fillCanvas(null, null);
	}

	function fillCanvas(e1,color){

	     if (canvas.getContext){
	        var length = x.length;
	       	var i;
	       	ctx.clearRect(0, 0, canvas.width, canvas.height);


	       	console.log("coordinates: ",x[0].getBoundingClientRect(), x[0].offsetTop, x[0].offsetBottom);
	       	for(i = 0 ; i < x.length/2 ; i++){
		        var coordinatesLeft = x[i].getBoundingClientRect();
		        var coordinatesRight = x[x.length/2 + i].getBoundingClientRect();

		        ctx.beginPath();
		        ctx.lineWidth = 4;

				ctx.moveTo(0,x[i].offsetTop + coordinatesLeft.height);
		        ctx.lineTo(0, x[i].offsetTop);
		        ctx.strokeStyle = '#ffffff';
				ctx.stroke();
		        ctx.lineTo(40, x[x.length/2 + i].offsetTop);
		        ctx.strokeStyle = 'none';
				ctx.stroke();
				ctx.lineTo(40, x[x.length/2 + i].offsetTop + coordinatesRight.height);
				ctx.strokeStyle = '#ffffff';
				ctx.stroke();
		        ctx.lineTo(0, x[i].offsetBottom);
		         ctx.strokeStyle = 'none';
				ctx.stroke();
				ctx.closePath();
				
				//console.log(document.getElementsByClassName("diffParentEntry")[2].getClientRects());
				//console.log(coordinatesLeft.bottom,coordinatesLeft.top,coordinatesLeft.left,coordinatesLeft.right);
				// console.log("Left: ", coordinatesLeft);
				// console.log("Right: ", coordinatesRight);

				ctx.fillStyle = getColor(x[i]);
		        // if(x[i] == e1 || x[i + x.length] == e1){
		        	
		      		// ctx.fillStyle = color;
		        // }
		     	ctx.fill();
	        }
	           
	    } else {
	           alert('Your browser does NOT support the update!');
	    }
	}