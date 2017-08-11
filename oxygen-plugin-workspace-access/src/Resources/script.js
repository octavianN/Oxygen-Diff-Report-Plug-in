	

	var x = document.getElementsByClassName("diffParentEntry");
	var i;

	for(i = 0 ; i < x.length; i++){
		var value = parseInt(x[i].getAttribute("data-diff-parent-id"));

		var string1 = "mouseOverParent(";
	   	string1 = string1.concat(value);
		string1 = string1.concat(")");
		x[i].setAttribute("onmouseover", string1);

		var string2 = "mouseOutParent(";
	   	string2 = string2.concat(value);
		string2 = string2.concat(")");
		x[i].setAttribute("onmouseout", string2);
	}

	var y = document.getElementsByClassName("diffEntry");
	
	for(i = 0 ; i < y.length; i++){
		var value = parseInt(y[i].getAttribute("data-diff-id"));

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


	function mouseOverParent(e) {
	    var list = document.getElementsByClassName("diffParentEntry");
	    var i;
	    for(i = 0; i < list.length; i++){
	    	var value = parseInt(x[i].getAttribute("data-diff-parent-id"));
	    	if(value == e){
	    		var classList = x[i].className.split(' ');
	    		var colorForBackgrund = classList[1];

	    		if(colorForBackgrund == "diffParentTypeConflict"){
	    			x[i].style.background = 'rgb('+255+',' +179+','+ 179+')';
	    		}else if(colorForBackgrund == "diffParentTypeOutgoing"){
	    			x[i].style.background = 'rgb('+209+',' +224+',' +224+')';
	    		}else if(colorForBackgrund == "diffParentTypeIncoming"){
	    			x[i].style.background = 'rgb('+153+',' +230+','+255+')';
	    		}

	    		x[i].style.opacity = "1.0";
	    	}
	    }
	}

	function mouseOutParent(e) {
	    var list = document.getElementsByClassName("diffParentEntry");
	    var i;
	    for(i = 0; i < list.length; i++){
	    	var value = parseInt(x[i].getAttribute("data-diff-parent-id"));
	    	if(value == e){

	    		var classList = x[i].className.split(' ');
	    		var colorForBackgrund = classList[1];

	    		if(colorForBackgrund == "diffParentTypeConflict"){
	    			x[i].style.background = 'rgb('+255+',' +230+','+230+')';
	    		}else if(colorForBackgrund == "diffParentTypeOutgoing"){
	    			x[i].style.background = 'rgb('+240+',' +245+',' +245+')';
	    		}else if(colorForBackgrund == "diffParentTypeIncoming"){
	    			x[i].style.background = 'rgb('+204+',' +242+','+255+')';
	    		}

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

	    		// if(colorForBackgrund == "diffTypeConflict"){
	    		// 	y[i].style.background = 'rgb('+255+',' +153+','+ 153+')';
	    		// }else if(colorForBackgrund == "diffTypeOutgoing"){
	    		// 	y[i].style.background = 'rgb('+204+',' +204+',' +204+')';
	    		// }else if(colorForBackgrund == "diffTypeIncoming"){
	    		// 	y[i].style.background = 'rgb('+179+',' +226+','+255+')';
	    		// }
	    		y[i].style.background = 'rgb('+255+',' +255+','+0+')';
	    		y[i].style.opacity = "1.0";
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
	    			y[i].style.background = 'rgb('+255+',' +209+','+209+')';
	    		}else if(colorForBackgrund == "diffTypeOutgoing"){
	    			y[i].style.background = 'rgb('+221+',' +221+',' +221+')';
	    		}else if(colorForBackgrund == "diffTypeIncoming"){
	    			y[i].style.background = 'rgb('+209+',' +237+','+255+')';
	    		}

	    		y[i].style.opacity = "0.74";
	    	}
	    }
	}

	function onClickEventChild(e){
		//var list = document.getElementsByClassName("diffEntry");
		var i;


		console.log(y);
		for(i = 0; i < y.length/2; i++){
			var value1 = parseInt(y[i].getAttribute("data-diff-id"));

			if(value1 == e){
				value1 = i;
				break;
			}
		}

		console.log("===========================");
		console.log(y);
		for(i = (y.length - 1)/2; i < y.length; i++){
			console.log("i: " + i + ", y[i]: " + y[i]);
			var value2 = parseInt(y[i].getAttribute("data-diff-id"));

			if(value2 == e){
				value2 = i;
				break;
			}
		}

		var auxiliary = y[value1].innerHTML;
		y[value1].innerHTML = y[value2].innerHTML;
		y[value2].innerHTML = auxiliary;

		

	}


