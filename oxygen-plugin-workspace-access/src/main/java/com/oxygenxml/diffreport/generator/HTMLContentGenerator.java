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
 * Lister
 * It listens to the content given by the parsers and
 * adds spans accordingly
 * @author intern3
 *
 */
public class HTMLContentGenerator implements ContentListener {

	/**
	 * String builder that keeps the generated text content.
	 */
	private StringBuilder resultedText;
	/**
	 * The list with the differnces to be added in the generated content.
	 */
	private List<Difference> differences;
	/**
	 * <code>true</code> if is the left file content generated,
	 * <code>false</code> if is the right file content generated.
	 */
	private boolean isLeft;
	/**
	 * 
	 */
	private int lastIdxForChildDiff = 0;
	private int lastIdxForParentDiff = 0;
	private int currentChildDiff = 0;
	/**
	 * The list with the parent diffs to be added in the content.
	 */
	private List<DiffEntry> parrentDiffs;
	
	private int localCount = 0;
	
	private int noDuplicates; 
	
	private int diffTypeConflict;
	private int diffTypeOutgoing;
	private int diffTypeIncoming;
	
	// TODO remove
	Comparator<Integer> comparator = new Comparator<Integer>() {
        @Override
        public int compare(Integer a, Integer b) {
            return b-a;
        }
    };
    
	
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
		
		// TODO remove
		noDuplicates = -2;
		//noDuplicates.add(Integer.MAX_VALUE);
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
	 * If the current offset indicates the beginning of a parent diff, it is returned
	 * @param currentOffs the offset that is currently analyzed
	 */
	private void checkParentStartDiff(int currentOffs){
		
		
		for(int i = lastIdxForParentDiff ; i < parrentDiffs.size(); i++){
			Difference difference = parrentDiffs.get(i);
			
			int start = isLeft ?  difference.getLeftIntervalStart() : difference.getRightIntervalStart();
			int end = isLeft ?  difference.getLeftIntervalEnd() : difference.getRightIntervalEnd();
			
			if(!(currentOffs >= start && currentOffs <= end)) {
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
	 * If the current offset indicates the ending of a parent diff, it is returned
	 * @param currentOffs the offset that is currently analyzed
	 */
	private void checkParentEndDiff(int currentOffs){
		
		for(int i = lastIdxForParentDiff ; i < parrentDiffs.size(); i++){
			Difference difference = parrentDiffs.get(i);
			
			int start = isLeft ?  difference.getLeftIntervalStart() : difference.getRightIntervalStart();
			int end = isLeft ?  difference.getLeftIntervalEnd() : difference.getRightIntervalEnd();
			
			if(!(currentOffs >= start && currentOffs <= end)) {
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
	 * 
	 * @param currentOffs
	 * @param buffer
	 * @return
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
			
			/**
			 * Time Improvement:
			 * In order to cut out meaningless operations, is checked if currentOffs is inside the current 
			 * difference interval. If it's below the start or above the end it is useless to go through the rest of
			 * the differences.   
			 */
			if(!(currentOffs >= start && currentOffs <= end)) {
				break;
			}
			String diffEntryType = getClassForParentDiffType(difference, false, currentOffs);
			
			if ((((currentOffs == start) && (start == end))) || ((currentOffs == end - 1) && (start + 1 == end))) {

				copyContent(buffer);
				resultedText.append("<span class=\"diffEntry " + diffEntryType + "\" data-diff-id=\""
						+ lastIdxForChildDiff + "\"></span   >");
				
				lastIdxForChildDiff++;
				currentChildDiff = lastIdxForChildDiff;

				foundDiff = true;

				break;
			} else {
				if (currentOffs == end - 1) {

					local--;

					copyContent(buffer);
					resultedText.append("</span>");
					foundDiff = true;
					currentChildDiff = lastIdxForChildDiff;

					break;
				}
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
	
	
}