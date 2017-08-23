	
	//get the parents off the differences
	var parentsList = document.getElementsByClassName("diffParentEntry");
	//get the child differences
	var childrenList = document.getElementsByClassName("diffEntry");
	// get the canvas element using the DOM
	var canvas = document.getElementById('myCanvas');
	var context = canvas.getContext('2d');
	context.canvas.height = document.getElementById('tr1').offsetHeight;
	// get the row where the Next and Previous Buttons are going to be implemented
	var i;
	var currentIDParent = -1;
	var currentIDChild = -1;

	var childrenListHalf;
	if(childrenList.length % 2 == 1)	
		childrenListHalf = (childrenList.length+1)/2;
	else
		childrenListHalf = childrenList.length/2;


/*
	Adding to every span that has the "data-diff-parent-id" attribute
	- indentify1 -> checks if the current diff is selected
	- onmouseover -> goes on mouseOverParent(id)
	- onmouseout
	- onclick
*/

	for(i = 0 ; i < parentsList.length; i++){
		var indexOfParent = parseInt(parentsList[i].getAttribute("data-diff-parent-id"));

		parentsList[i].setAttribute("identify1", "0");

		var string1 = "mouseOverParent(";
	   	string1 = string1.concat(indexOfParent);
		string1 = string1.concat(")");
		parentsList[i].setAttribute("onmouseover", string1);

		var string2 = "mouseOutParent(";
	   	string2 = string2.concat(indexOfParent);
		string2 = string2.concat(")");
		parentsList[i].setAttribute("onmouseout", string2);

		var string3 = "onClickEventParent(";
		string3 = string3.concat(indexOfParent);
		string3 = string3.concat(")");
		parentsList[i].setAttribute("onclick", string3);
	}


/*
	Adding to every span that has the "data-diff-id" attribute
	- indentify1 -> checks if the current diff is selected
	- onmouseover -> goes on mouseOverParent(id)
	- onmouseout
	- onclick
*/	
	
	for(i = 0 ; i < childrenList.length; i++){
		var indexOfChild = parseInt(childrenList[i].getAttribute("data-diff-id"));

		childrenList[i].setAttribute("identify", "0");

		var string1 = "mouseOverChild(";
	   	string1 = string1.concat(indexOfChild);
		string1 = string1.concat(")");
		childrenList[i].setAttribute("onmouseover", string1);

		var string2 = "mouseOutChild(";
	   	string2 = string2.concat(indexOfChild);
		string2 = string2.concat(")");
		childrenList[i].setAttribute("onmouseout", string2);

		var string3 = "onClickEventChild(";
		string3 = string3.concat(indexOfChild);
		string3 = string3.concat(")");
		childrenList[i].setAttribute("onclick", string3);
	}

/*
	I have to build the canvas before any of the events (onmouseover, onclick, etc)
	could change it
*/
	fillCanvas(null,null);



	/*
		-------------------------------FUNCTIONS-------------------------------------------
	*/
	

	/*
		@param e - > element
		Returns the color of the element by comparing the types of the classes:
		Conflict, Outgoing and Incoming
	*/
    function getColor(e){
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


    /*
    	@param e -> id of the element
    	-HOOVER effect-
		Changes the color of the background when the mouse is on the certain element.
		Also changes the color of the matched neighbour of the element 

    */
	function mouseOverParent(e) {

	    var i;
	    for(i = 0; i < parentsList.length; i++){  
	    	var indexOfParent = parseInt(parentsList[i].getAttribute("data-diff-parent-id"));
	    	if(indexOfParent == e){
	    		var classList = parentsList[i].className.split(' ');
	    		var colorForBackgrund = classList[1];
	    		var color;


	    		if(colorForBackgrund == "diffParentTypeConflict"){
	    			color = 'rgb('+255+',' +210+','+ 210+')';
	    		}else if(colorForBackgrund == "diffParentTypeOutgoing"){
	    			color = 'rgb('+209+',' +224+',' +224+')';
	    		}else if(colorForBackgrund == "diffParentTypeIncoming"){
	    			color = 'rgb('+179+',' +236+','+255+')';
	    		}

	    		parentsList[i].style.background = color;
 				fillCanvas(parentsList[i], color);

	    		// fillCanvas(x[i], color);
	    		parentsList[i].style.opacity = "0.9";
	    	}
	    }
	}


	/*
    	@param e -> id of the element
    	-HOOVER effect-
		Changes the color of the background when the mouse is off the certain element.
		Also changes the color of the matched neighbour of the element 

    */
	function mouseOutParent(e) {

	    var i;
	    for(i = 0; i < parentsList.length; i++){
	    	var indexOfParent = parseInt(parentsList[i].getAttribute("data-diff-parent-id"));
	    	if(indexOfParent == e){

	    		var classList = parentsList[i].className.split(' ');
	    		var colorForBackgrund = classList[1];
	    		var color;

	    		if(colorForBackgrund == "diffParentTypeConflict"){
	    			color= 'rgb('+255+',' +230+','+230+')';
	    		}else if(colorForBackgrund == "diffParentTypeOutgoing"){
	    			color = 'rgb('+240+',' +245+',' +245+')';
	    		}else if(colorForBackgrund == "diffParentTypeIncoming"){
	    			color = 'rgb('+204+',' +242+','+255+')';
	    		}

	    		parentsList[i].style.background = color;
	    		fillCanvas(parentsList[i], color);
	    		// fillCanvas(x[i], color);
	    		parentsList[i].style.opacity = "0.81";
	    	}
	    }
	}



    /*
    	@param e -> id of the element
    	-HOOVER effect-
		Changes the color of the background when the mouse is on the certain element.
		Also changes the color of the matched neighbour of the element 

    */

	function mouseOverChild(e) {
	    var list = document.getElementsByClassName("diffEntry");
	    var i;
	    for(i = 0; i < list.length; i++){
	    	var indexOfChild = parseInt(childrenList[i].getAttribute("data-diff-id"));
	    	if(indexOfChild == e){
	    		var classList = childrenList[i].className.split(' ');
	    		var colorForBackgrund = classList[1];

	    		if(colorForBackgrund == "diffTypeConflict"){
	    			childrenList[i].style.background = 'rgb('+255+',' +133+','+133+')';
	    		}else if(colorForBackgrund == "diffTypeOutgoing"){
	    			childrenList[i].style.background = 'rgb('+204+',' +204+','+204+')';
	    		}else if(colorForBackgrund == "diffTypeIncoming"){
	    			childrenList[i].style.background = 'rgb('+128+',' +206+','+255+')';
	    		}
	    	// 	y[i].style.background = 'rgb('+255+',' +255+','+0+')';
	    	// 	y[i].style.opacity = "0.8";
	    	}
	    }
	}

    /*
    	@param e -> id of the element
    	-HOOVER effect-
		Changes the color of the background when the mouse is off the certain element.
		Also changes the color of the matched neighbour of the element 

    */	
	function mouseOutChild(e) {
	    var list = document.getElementsByClassName("diffEntry");
	    var i;
	    for(i = 0; i < list.length; i++){
	    	var indexOfChild = parseInt(childrenList[i].getAttribute("data-diff-id"));
	    	if(indexOfChild == e){

	    		var classList = childrenList[i].className.split(' ');
	    		var colorForBackgrund = classList[1];

	    		if(colorForBackgrund == "diffTypeConflict"){
	    			childrenList[i].style.background = 'rgb('+255+',' +179+','+179+')';
	    		}else if(colorForBackgrund == "diffTypeOutgoing"){
	    			childrenList[i].style.background = 'rgb('+221+',' +221+',' +221+')';
	    		}else if(colorForBackgrund == "diffTypeIncoming"){
	    			childrenList[i].style.background = 'rgb('+209+',' +237+','+255+')';
	    		}

	    		childrenList[i].style.opacity = "1.0";
	    	}
	    }
	}

	/*
		Depending on the class of the given element, the function returns the
		name of that class concatenated with "_sel"
	*/	
	function getConcatForParent(i){
		var classList = parentsList[i].className.split(' ');
	   	var colorForBackgrund = classList[1];
	    var concat;
	    if(colorForBackgrund == "diffParentTypeConflict"){
	    	concat = "diffParentTypeConflict_sel";
	    }else if(colorForBackgrund == "diffParentTypeOutgoing"){
	    	concat = "diffParentTypeOutgoing_sel";
	    }else if(colorForBackgrund == "diffParentTypeIncoming"){
	    	concat = "diffParentTypeIncoming_sel";
	    }
	    return concat;
	}

	/*
		When clicked, a Parent element should change its borders so that it looks selected
		This function is used with the buttons as weel as by click.
		If an element is clicked and the next or previous button is pressed, it will go
		from thap poin on, without reseting the border.
		Also when pressed twice or when a "highlited" element is pressed, the border is
		reseted and the index of the selected elements is reseted.
	*/
	function onClickEventParent(e){
		var i;
		var id1,id2;

		/*Upon finding the element with the value "e" both in the right and the left
		the clas (diff..)_sel should remain, otherwise be removed if exists */
		for(i = 0; i < parentsList.length/2; i++){
			var value1 = parseInt(parentsList[i].getAttribute("data-diff-parent-id"));

			var concat = getConcatForParent(i);

	    	if(parentsList[i].classList.contains(concat)){
				parentsList[i].classList.remove(concat); 
	    	}

			if(value1 == e){
				id1 = i;
				
			}else{
				parentsList[i].removeAttribute("identify1");
				parentsList[i].setAttribute("identify1", "0");
			}
		}

		for(i = parentsList.length/2; i < parentsList.length; i++){
			var value2 = parseInt(parentsList[i].getAttribute("data-diff-parent-id"));
			
			var concat = getConcatForParent(i);

			if(parentsList[i].classList.contains(concat)){
				parentsList[i].classList.remove(concat);
	    	}


			if(value2 == e){
				id2 = i;
				
			}else{
				parentsList[i].removeAttribute("identify1");
				parentsList[i].setAttribute("identify1", "0");
			}
		}


		
	    var concat = getConcatForParent(id1);
   
    	
    	parentsList[id1].classList.add(concat); 
    	parentsList[id2].classList.add(concat);
    	currentIDParent = id1;

    	/*After finding the two matched element, if they are selected
    	"val == 1" then they should be deselected, otherwise selected*/
	    var val = parseInt(parentsList[id1].getAttribute("identify1"));
	    if(val == 0){
			parentsList[id1].removeAttribute("identify1");
			parentsList[id1].setAttribute("identify1", "1");
			parentsList[id2].removeAttribute("identify1");
			parentsList[id2].setAttribute("identify1", "1");
		} else {
	    	parentsList[id1].classList.remove(concat); 
	    	parentsList[id2].classList.remove(concat);

			parentsList[id1].removeAttribute("identify1");
			parentsList[id1].setAttribute("identify1", "0");
			parentsList[id2].removeAttribute("identify1");
			parentsList[id2].setAttribute("identify1", "0");
			currentIDParent = -1;
		}

		
		
	}

	/*
		Depending on the class of the given element, the function returns the
		name of that class concatenated with "_sel"
	*/
	function getConcatForChild(i){
		var classList = childrenList[i].className.split(' ');
	   	 var colorForBackgrund = classList[1];
	    var concat;
	    if(colorForBackgrund == "diffTypeConflict"){
	    	concat = "diffTypeConflict_sel";
	    }else if(colorForBackgrund == "diffTypeOutgoing"){
	    	concat = "diffTypeOutgoing_sel";
	    }else if(colorForBackgrund == "diffTypeIncoming"){
	    	concat = "diffTypeIncoming_sel";
	    }
	    return concat;
	}

	/*
		When clicked, a Child element should change its borders so that it looks selected
		This function is used with the buttons as weel as by click.
		If an element is clicked and the next or previous button is pressed, it will go
		from thap poin on, without reseting the border.
		Also when pressed twice or when a "highlited" element is pressed, the border is
		reseted and the index of the selected elements is reseted.
	*/
	function onClickEventChild(e){
		var i;
		var id1,id2;

		/*Upon finding the element with the value "e" both in the right and the left
		the clas (diff..)_sel should remain, otherwise be removed if exists */
		for(i = 0; i < childrenListHalf; i++){
			var value1 = parseInt(childrenList[i].getAttribute("data-diff-id"));
			
	    	var concat=getConcatForChild(i);
	    	
			if(childrenList[i].classList.contains(concat)){
				childrenList[i].classList.remove(concat); 
	    	} else {
				childrenList[i].removeAttribute("identify");
				childrenList[i].setAttribute("identify", "0");
			}

			if(value1 == e){
				id1 = i;
				
			}
		}

		for(i = childrenListHalf; i < childrenList.length; i++){
			console.log("half: ",childrenListHalf, "whole: ", childrenList.length);
			var value2 = parseInt(childrenList[i].getAttribute("data-diff-id"));
			
			var concat=getConcatForChild(i);

			if(childrenList[i].classList.contains(concat)){
				childrenList[i].classList.remove(concat); 
	    	} else {
				childrenList[i].removeAttribute("identify");
				childrenList[i].setAttribute("identify", "0");
			}

			if(value2 == e){
				id2 = i;
				
			}
		}


	   var concat = getConcatForChild(id1);
   

    	childrenList[id1].classList.add(concat); 
    	childrenList[id2].classList.add(concat);
    	currentIDChild = id1;

    	/*After finding the two matched element, if they are selected
    	"val == 1" then they should be deselected, otherwise selected*/
	    var val = parseInt(childrenList[id1].getAttribute("identify"));
	    if(val == 0){
			childrenList[id1].removeAttribute("identify");
			childrenList[id1].setAttribute("identify", "1");
			childrenList[id2].removeAttribute("identify");
			childrenList[id2].setAttribute("identify", "1");
		} else {
	    	childrenList[id1].classList.remove(concat); 
	    	childrenList[id2].classList.remove(concat);

			childrenList[id1].removeAttribute("identify");
			childrenList[id1].setAttribute("identify", "0");
			childrenList[id2].removeAttribute("identify");
			childrenList[id2].setAttribute("identify", "0");
			currentIDChild = -1;
		}

	}



	function nextDiff(){
		if(currentIDParent == -1 || currentIDParent == parentsList.length/2 - 1){
			onClickEventParent(0);
		} else {
			onClickEventParent(currentIDParent + 1);
		}
	}

	function previousDiff(){
		if(currentIDParent == -1 || currentIDParent == 0){
			onClickEventParent(parentsList.length/2 - 1);
		} else {
			onClickEventParent(currentIDParent - 1);
		}
	}

	function nextChildDiff(){
		if(currentIDChild == -1 || currentIDChild == childrenListHalf - 1){
			onClickEventChild(0);
		} else {
			onClickEventChild(currentIDChild + 1);
		}
	}

	function previousChildDiff(){
		if(currentIDChild == -1 || currentIDChild == 0){
			console.log(currentIDChild);
			onClickEventChild((childrenListHalf-1));
		} else {
			console.log((childrenList.length), " ", currentIDChild);
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
	        var length = parentsList.length;
	       	var i;
	       	context.clearRect(0, 0, canvas.width, canvas.height);


	       	console.log("coordinates: ",parentsList[0].getBoundingClientRect(), parentsList[0].offsetTop, parentsList[0].offsetBottom);
	       	for(i = 0 ; i < parentsList.length/2 ; i++){
		        var coordinatesLeft = parentsList[i].getBoundingClientRect();
		        var coordinatesRight = parentsList[parentsList.length/2 + i].getBoundingClientRect();

		        context.beginPath();
		        context.lineWidth = 4;

				context.moveTo(0,parentsList[i].offsetTop + coordinatesLeft.height);
		        context.lineTo(0, parentsList[i].offsetTop);
		        context.strokeStyle = '#ffffff';
				context.stroke();
		        context.lineTo(40, parentsList[parentsList.length/2 + i].offsetTop);
		        context.strokeStyle = 'none';
				context.stroke();
				context.lineTo(40, parentsList[parentsList.length/2 + i].offsetTop + coordinatesRight.height);
				context.strokeStyle = '#ffffff';
				context.stroke();
		        context.lineTo(0, parentsList[i].offsetBottom);
		         context.strokeStyle = 'none';
				context.stroke();
				context.closePath();
				

				context.fillStyle = getColor(parentsList[i]);
		        if(parentsList[i] == e1 || parentsList[i + parentsList.length] == e1){
		        	
		      		context.fillStyle = color;
		        }
		     	context.fill();
	        }
	           
	    } else {
	           alert('Your browser does NOT support the update!');
	    }
	}