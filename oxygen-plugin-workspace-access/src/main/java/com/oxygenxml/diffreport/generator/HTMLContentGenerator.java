package com.oxygenxml.diffreport.generator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import com.ibm.icu.impl.Differ;
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
	/**
	 * The list with the parent diffs to be added in the content.
	 */
	private List<DiffEntry> parrentDiffs;
	
	private TreeSet<Integer> noDuplicates; //checks index duplicates // TODO remove
	
	// TODO remove
	Comparator<Integer> comparator = new Comparator<Integer>() {
        @Override
        public int compare(Integer a, Integer b) {
            return b-a;
        }
    };
    
    // TODO remove
    Comparator<DiffEntry> compareTwoDifferences = new Comparator<DiffEntry>() {
        @Override
        public int compare(DiffEntry diff1, DiffEntry diff2) {
            if(diff1.getLeftIntervalStart() == diff2.getLeftIntervalStart()){
            	if(diff1.getLeftIntervalStart() == diff2.getLeftIntervalStart()){
            		if(diff1.getRightIntervalStart() == diff2.getRightIntervalStart()){
            			if(diff1.getRightIntervalEnd() == diff2.getRightIntervalEnd()){
            				return 0;
            			}else{
            				return diff1.getRightIntervalEnd() - diff2.getRightIntervalEnd();
            			}
            		}else{
            			return diff1.getRightIntervalStart() - diff2.getRightIntervalStart();
            		}
            	}else{
            		return diff1.getLeftIntervalStart() - diff2.getLeftIntervalStart();
            	}
            }else{
            	return diff1.getLeftIntervalStart() - diff2.getLeftIntervalStart();
            }
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
		noDuplicates = new TreeSet<Integer>(comparator);
		//noDuplicates.add(Integer.MAX_VALUE);
	}
	
	
	/**
	 * @return The resulted HTML generated from the given content.
	 */
	public String getResultedText() {
		System.out.println(resultedText.toString());
		return resultedText.toString();
	}

	


	/**
	 * 
	 */
	@Override
	public void startNode(NodeType type) {
		
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
	 * 
	 * @param difference
	 * @param isParent
	 * @return
	 */
	private String getClassForParentDiffType(Difference difference, boolean isParent) {

		byte entryType = ((DiffEntry) difference).getEntryType();

		String diffEntryType = "diffTypeUnknown";
		if (isParent) {
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
			switch (entryType) {
			case DiffEntryType.DIFF_MODIFIED:
				diffEntryType = "diffTypeConflict";
				break;
			case DiffEntryType.DIFF_INSERTED:
				diffEntryType = "diffTypeOutgoing";
				break;
			case DiffEntryType.DIFF_REMOVED:
				diffEntryType = "diffTypeIncoming";
				break;
			}
		}

		return diffEntryType;
	}
	
	/**
	 * 
	 * @param currentOffs
	 */
	private void checkParentStartDiff(int currentOffs){
		
		for(int i = 0 ; i < parrentDiffs.size(); i++){
			Difference difference = parrentDiffs.get(i);
			
			int start = isLeft ?  difference.getLeftIntervalStart() : difference.getRightIntervalStart();
			
			if(currentOffs == start){
				
				resultedText.append("<span class=\"diffParentEntry " + getClassForParentDiffType(difference, true)+ "\" id=\"" + lastIdxForParentDiff +"\">");
				break;
			}
			
		} 
		
	}
	
	

	
	private void checkParentEndDiff(int currentOffs){
		
		for(int i = 0 ; i < parrentDiffs.size(); i++){
			Difference difference = parrentDiffs.get(i);
			
			int end = isLeft ?  difference.getLeftIntervalEnd() : difference.getRightIntervalEnd();
			
			if(currentOffs == end){
				resultedText.append("</span>");
				parrentDiffs.remove(i);
				lastIdxForParentDiff ++;
				break;
			}
			
		} 
		
	}

	@Override
	public boolean checkDiff(int currentOffs, String buffer) {
		
		boolean foundDiff = false;
		int start = 0;
		int end = 0;
		//System.out.println("currentOffs: " + currentOffs);
		if(!noDuplicates.contains(new Integer(currentOffs))){
			noDuplicates.add(new Integer(currentOffs));
		
			if(differences != null){
				
				checkParentStartDiff(currentOffs);
				
				for (int i = 0; i < differences.size(); i++) {
					Difference difference = differences.get(i);
//					System.out.println("difference: " + difference);
											
					start = isLeft ?  difference.getLeftIntervalStart() : difference.getRightIntervalStart();
					end = isLeft ?  difference.getLeftIntervalEnd() : difference.getRightIntervalEnd();
					
					String diffEntryType = getClassForParentDiffType(difference, false);
					
					
					if( (((currentOffs == start) && (start == end) )) || (((currentOffs == end - 1) && (start == end) )) || ((currentOffs == end - 1 ) && (start + 1 == end)) ){
						copyContent(buffer);
						resultedText.append("<span class=\"diffEntry " + diffEntryType + "\" id=\"" + lastIdxForChildDiff +"\"></span>");
						lastIdxForChildDiff ++;
					
						foundDiff = true;
						
						break;
					} else if (currentOffs == start) {
						local++;
						
						copyContent(buffer);
						resultedText.append("<span class=\"diffEntry " + diffEntryType + "\" id=\"" + lastIdxForChildDiff +"\">");
						lastIdxForChildDiff ++;
					
						foundDiff = true;

						break;
					} else if (currentOffs == end - 1) {
						
						//System.out.println("end " + end + " start: "+ start);
						
						local--;
						
						copyContent(buffer);
						resultedText.append("</span>");
						foundDiff = true;
						
						break;
					}
					
				}
				if (currentOffs == end - 1) {
					checkParentEndDiff(currentOffs);
				} else {
					checkParentEndDiff(currentOffs);
				}
				
			}
		}
		return foundDiff;
	}
	
	
}
