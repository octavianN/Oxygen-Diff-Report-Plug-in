package com.oxygenxml.diffreport.generator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import com.oxygenxml.diffreport.parser.NodeType;

import ro.sync.diff.api.Difference;
import ro.sync.diff.text.DiffEntry;
import ro.sync.diff.xml.DiffEntryType;



/**
 * Lister.
 * It listens to the content given by the parsers and
 * adds spans accordingly
 * @author Dina_Andrei
 *
 */
public class HTMLContentGenerator implements ContentListener {

	/**
	 * String builder that keeps the generated text content.
	 */
	private StringBuilder resultedText;
	/**
	 * The list with the differences to be added in the generated content.
	 */
	private List<Difference> differences;
	/**
	 * <code>true</code> if is the left file content generated,
	 * <code>false</code> if is the right file content generated.
	 */
	private boolean isLeft;
	/**
	 * the value of diff-entry-type id
	 */
	private int lastIdxForChildDiff = 0;
	/**
	 * the value of diff-parent-entry-type id
	 * also the current ParentDiff
	 */
	private int lastIdxForParentDiff = 0;
	/**
	 * current ChildDiff
	 */
	private int currentChildDiff = 0;
	/**
	 * The list with the parent diffs to be added in the content.
	 */
	private List<DiffEntry> parrentDiffs;
	/**
	 * Counts the spans to see if they are closed.
	 * If the value is bigger than 0, then the value represents the number of unclosed spans.
	 * If the value is smaller than 0, then the value represents how many spans are closed in excess.
	 */
	private int localCount = 0;
	/**
	 * An error may occur and an offset may be analyzed twice.
	 * This variable does not allow checking an offset twice
	 */
	private int noDuplicates; 
	
	private int diffTypeConflict;
	private int diffTypeOutgoing;
	private int diffTypeIncoming;

    
	
    /**
     * Constructor.
     * @param differences The list with the diff entries to be rendered in the output html.
     * @param isLeft <code>true</code> if is the left file content generated,
	 *                <code>false</code> if is the right file content generated.
     */
	public HTMLContentGenerator(List<Difference> differences, boolean isLeft) {
		this.differences = differences;
		this.isLeft = isLeft;
		parrentDiffs = new ArrayList<>();
		
		diffTypeConflict = 0;
		diffTypeOutgoing = 0;
		diffTypeIncoming = 0;
		
		// Compuute the list with the parent diff entries.
		TreeSet<DiffEntry> parentDiffsDuplicateRemover = new TreeSet<DiffEntry>();
		
		for (Difference difference : differences) {
			DiffEntry diff = ((DiffEntry) difference).getParentDiffEntry();
			parentDiffsDuplicateRemover.add(diff);
			
		}
		
		while(!parentDiffsDuplicateRemover.isEmpty()){
			DiffEntry diff = parentDiffsDuplicateRemover.pollFirst();
			parrentDiffs.add(diff);
			
		}
		
		
		// Initialize the result string builder.
		resultedText = new StringBuilder();
		
		//initial value, could be any value, but not a positive one, nor 0 or -1
		noDuplicates = -2;
	}
	
	
	/**
	 * @return The resulted HTML generated from the given content.
	 */
	public String getResultedText() {
		if(localCount == 1){
			resultedText.append("</div>");
		}
		return resultedText.toString();
	}

	public int getDiffTypeConflict() {
		return diffTypeConflict;
	}

	public int getDiffTypeOutgoing() {
		return diffTypeOutgoing;
	}

	public int getDiffTypeIncoming() {
		return diffTypeIncoming;
	}


	/**
	 * Depending on the type of the Node, a span is created
	 * with a class of that Node Type
	 */
	@Override
	public void startNode(NodeType type) {
		if(type != null)
		switch(type){
		case ELEMENT:
			resultedText.append( "<span class = \"Element\">" );
			break;
		case ELEMENT_CLOSE:
			resultedText.append( "<span class = \"Element\">" );
			break;
		case TEXTFIELD:
			resultedText.append("<span class = \"textField\">");;
			break;
		case ATTRIBUTENAME:
			resultedText.append( "<span class = \"attributeName\">");
			break;
		case ATTRIBUTEVALUE:
			resultedText.append("<span class = \"attributeValue\">");
			break;
		case PI:
			resultedText.append("<span class = \"PI\">");
			break;
		case DOCTYPE:
			resultedText.append("<span class = \"Doctype\">");
			break;
		case CDATA:
			resultedText.append("<span class = \"CDATA\">");
			break;
		case COMMENT:
			resultedText.append("<span class = \"Comment\">");
		default:
			break;
			
		}
		
		
		
	}

	@Override
	public void copyContent(String content){
		resultedText.append(content);
	}
	
	@Override
	public void endNode(String content) {
		copyContent(content);
		resultedText.append("</span>");
	}

	int local = 0;

	/**
	 * Depending on witch type of difference the node has, this function returns 
	 * the class that defines that 
	 * @param difference the difference that is analyzed
	 * @param isParent a boolean that sais if a parent or a child is analized
	 * @return
	 */
	private String getClassForParentDiffType(Difference difference, boolean isParent, int currentOffs) {

		byte entryType = ((DiffEntry) difference).getEntryType();

		String diffEntryType = "diffTypeUnknown";
		if (isParent) { 
			//analyze parent
			switch (entryType) {
			case DiffEntryType.DIFF_MODIFIED:
				diffEntryType = "diffParentTypeConflict";
				break;
			case DiffEntryType.DIFF_INSERTED:
				diffEntryType = "diffParentTypeOutgoing";
				break;
			case DiffEntryType.DIFF_REMOVED:
				diffEntryType = "diffParentTypeIncoming";
				break;
			}
		} else {
			//analyze child
			int start = isLeft ?  difference.getLeftIntervalStart() : difference.getRightIntervalStart();
			switch (entryType) {
			case DiffEntryType.DIFF_MODIFIED:
				diffEntryType = "diffTypeConflict";
				if(start == currentOffs) {
					diffTypeConflict++;
				}
				break;
			case DiffEntryType.DIFF_INSERTED:
				diffEntryType = "diffTypeOutgoing";
				if(start == currentOffs) {
					diffTypeOutgoing++;
				}
				break;
			case DiffEntryType.DIFF_REMOVED:
				diffEntryType = "diffTypeIncoming";
				if(start == currentOffs) {
					diffTypeIncoming++;
				}
				break;
			}
		}

		return diffEntryType;
	}
	
	/**
	 * If the current offset indicates the beginning of a ParentDiff, it is marked with a <b>div</b> tag
	 * @param currentOffs the offset that is currently analyzed
	 */
	private void checkParentStartDiff(int currentOffs){
		
		
		for(int i = lastIdxForParentDiff ; i < parrentDiffs.size(); i++){
			Difference difference = parrentDiffs.get(i);
			
			int start = isLeft ?  difference.getLeftIntervalStart() : difference.getRightIntervalStart();
			int end = isLeft ?  difference.getLeftIntervalEnd() : difference.getRightIntervalEnd();
			
			if(timeImproovementChecker(start, end, currentOffs)) {
				break;
			}
			
			if(currentOffs == start){
				localCount++;
				resultedText.append("<div class=\"diffParentEntry " + getClassForParentDiffType(difference, true, currentOffs)+ "\" data-diff-parent-id=\"" + lastIdxForParentDiff +"\">");
				break;
			}
			
		} 
		
	}
	


	/**
	 * If the current offset indicates the ending of a ParentDiff, it is marked with a <b>div</b> tag
	 * @param currentOffs the offset that is currently analyzed
	 */
	private void checkParentEndDiff(int currentOffs){
		
		for(int i = lastIdxForParentDiff ; i < parrentDiffs.size(); i++){
			Difference difference = parrentDiffs.get(i);
			
			int start = isLeft ?  difference.getLeftIntervalStart() : difference.getRightIntervalStart();
			int end = isLeft ?  difference.getLeftIntervalEnd() : difference.getRightIntervalEnd();
			
			if(timeImproovementChecker(start, end, currentOffs)) {
				break;
			}
			
			if(currentOffs == end){
				localCount--;
				resultedText.append("</div>");
				lastIdxForParentDiff ++;
				break;
			}
			
		} 
		
	}

	@Override
	public boolean checkDiff(int currentOffs, String buffer) {
		//the returned value. If a Child Diff is found, it returns <code>true</code> else <code>false</code>
		boolean foundDiff = false;
		
		
		if(currentOffs > noDuplicates){ //does not let any duplicates offsets be rechecked
			noDuplicates = currentOffs;
			
			if(differences != null){
				/**
				 * Check the ParentDiff Start Tag
				 */
				checkParentStartDiff(currentOffs); 
				/**
				 * Check ChildDiff
				 */
				foundDiff = checkChild(currentOffs, buffer);
				
				/**
				 * Check ParentDiff end tag
				 */
				checkParentEndDiff(currentOffs);
			}
		}
		return foundDiff;
	}
	
	/**
	 * If the current offset indicates the end or the beginning of a ChildDiff, it is marked with a <b>span</b> tag
	 * @param currentOffs
	 * @param buffer
	 * @return <code> true </code> if the current offset is either the beginning or the end of a ChildDiff
	 */
	private boolean checkChild( int currentOffs, String buffer) {
		//Beginning of a ChildDiff interval
		int start;
		//ending of a ChildDiff interval
		int end;
		//initially they are 0
		start = end = 0;
		
		boolean foundDiff = false;
		for (int i = currentChildDiff; i < differences.size(); i++) {
			Difference difference = differences.get(i);
			
			start = isLeft ?  difference.getLeftIntervalStart() : difference.getRightIntervalStart();
			end = isLeft ?  difference.getLeftIntervalEnd() : difference.getRightIntervalEnd();
						

			//Time Improvement:
			if(timeImproovementChecker(start, end, currentOffs)) {
				break;
			}
			
			String diffEntryType = getClassForParentDiffType(difference, false, currentOffs);
			
			//if the offset is start/end and the difference between beginning end ending of a difference is either 0 or 1
			if ((((currentOffs == start) && (start == end))) 
					|| ((currentOffs == end - 1) && (start + 1 == end))) {

				copyContent(buffer);
				resultedText.append("<span class=\"diffEntry " + diffEntryType + "\" data-diff-id=\""
						+ lastIdxForChildDiff + "\"></span   >");
				
				lastIdxForChildDiff++;
				currentChildDiff = lastIdxForChildDiff;

				foundDiff = true;

				break;
			} else {
				//the algorithm adds a 1, so the current offset has to be compared to the ending - 1 offset
				if (currentOffs == end - 1) {

					local--;

					copyContent(buffer);
					resultedText.append("</span>");
					foundDiff = true;
					currentChildDiff = lastIdxForChildDiff;

					break;
				}
				//checks if there is a start offset
				if (currentOffs == start) {
					local++;

					copyContent(buffer);
					resultedText.append("<span class=\"diffEntry " + diffEntryType + "\" data-diff-id=\""
							+ lastIdxForChildDiff + "\">");
					lastIdxForChildDiff++;

					foundDiff = true;

					break;
				}
			}
			
			

		}

		return foundDiff;
	}
	
	/**
	 * Time Improvement:
	 * In order to cut out meaningless operations, is checked if currentOffs is inside the current 
	 * difference interval. If it's below the start or above the end it is useless to go through the rest of
	 * the differences. 
	 * @param start - interval Begin Offset
	 * @param end - interval End Offset
	 * @param currentOffs - current Offset
	 * @return <code>true</code> if the offset is NOT inside the interval.
	 */
	public boolean timeImproovementChecker(int start, int end, int currentOffs) {
		return !(currentOffs >= start && currentOffs <= end);
	}
	
	
}