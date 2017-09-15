Diff Generator Plug-in

   - This project is a plug-in for Oxygen XML Editor 19.1 Beta. It's purpose is to realize the differences between two XML files and build a HTML file
where the differences are highlighted. Both the parents of the differences and the differences themselves are shown, the user being able to iterate 
through them by buttons or by click. Each difference will have a hover effect on them when mouse is over, and will be surrounded by specific borders
when clicked.
   - The user may try any XML files up to 10MB each, maybe more, though it is not suggested because it will result in a HTML that may reach almost
three times the size of the biggest XML files and so it will take a long time to be opened. The canvas may not work for big HTML's, depending on
the browser it has a number of pixels.
   - The time it takes the program to realize the differences is linear, so it takes a small amount of time for most of the XML files to be parsed and
made into a HTML. 
   - In the Dialog there is a ComboBox for every path given by the user. Each has a history of 10 files, after introducing more than the limit number, the
first file that was added is deleted and so on. 
   - The algorithm that calculates the differences may be chosen.
