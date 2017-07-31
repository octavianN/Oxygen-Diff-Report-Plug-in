package com.oxygenxml.sdksamples.workspace;

import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import com.ibm.icu.impl.Differ;

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

	
	private StringBuilder resultedText;
	private List<Difference> differences;
	private boolean isLeft;
	private int lastIdx = 0;
	private List<DiffEntry> parrentDiffs;
	
	private TreeSet<Integer> noDuplicates; //checks index duplicates
	
	
	Comparator<Integer> comparator = new Comparator<Integer>() {
        @Override
        public int compare(Integer a, Integer b) {
            return b-a;
        }
    };
    
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
	
	public HTMLContentGenerator(List<Difference> differences, boolean isLeft) {
		this.differences = differences;
		
		TreeSet<DiffEntry> parentDiffsDuplicateRemover = new TreeSet<>(compareTwoDifferences);
		
		
		for (Difference difference : differences) {
			DiffEntry diff = ((DiffEntry) difference).getParentDiffEntry();
			if(diff != null){
				System.out.print("| " + diff.getLeftIntervalEnd() + "--" + diff.getLeftIntervalEnd() + " |");
				System.out.println("<---------->");
				System.out.println("| " + diff.getRightIntervalEnd() + "--" + diff.getRightIntervalEnd() + " |");
				//parentDiffsDuplicateRemover.add(((DiffEntry)difference).getParentDiffEntry());
			}else{
				System.out.println(1);
			}
		}
		
//		while(!parentDiffsDuplicateRemover.isEmpty()){
//			DiffEntry diff = parentDiffsDuplicateRemover.pollFirst();
//			parrentDiffs.add(diff);
//			
//			System.out.print("| " + diff.getLeftIntervalEnd() + "--" + diff.getLeftIntervalEnd() + " |");
//			System.out.println("<---------->");
//			System.out.println("| " + diff.getRightIntervalEnd() + "--" + diff.getRightIntervalEnd() + " |");
//		}
//		
		this.isLeft = isLeft;
		resultedText = new StringBuilder();
		
		noDuplicates = new TreeSet<Integer>(comparator);
		//noDuplicates.add(Integer.MAX_VALUE);
	}
	
	
	
	public String getResultedText() {
		System.out.println(resultedText.toString());
		return resultedText.toString();
	}

	


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

	
	private void checkParentStartDiff(int currentOffs){
		
		for(int i = 0 ; i < parrentDiffs.size(); i++){
			Difference difference = parrentDiffs.get(i);
			
			int start = isLeft ?  difference.getLeftIntervalStart() : difference.getRightIntervalStart();
			
			if(currentOffs == start){
				resultedText.append("<span class=\" ParentDiff \" id=\"" + lastIdx +"\">");
			}
			
		} 
		
	}
	
	private void checkParentEndDiff(int currentOffs){
		
		for(int i = 0 ; i < parrentDiffs.size(); i++){
			Difference difference = parrentDiffs.get(i);
			
			int end = isLeft ?  difference.getLeftIntervalEnd() : difference.getRightIntervalEnd();
			
			if(currentOffs == end){
				resultedText.append("</span>");
			}
			
		} 
		
	}

	@Override
	public boolean checkDiff(int currentOffs, String buffer) {
		
		boolean foundDiff = false;

		if(!noDuplicates.contains(new Integer(currentOffs))){
			noDuplicates.add(new Integer(currentOffs));
		
			if(differences != null){
				
				//checkParentStartDiff(currentOffs);
				
				for (int i = 0; i < differences.size(); i++) {
					Difference difference = differences.get(i);
											
					int start = isLeft ?  difference.getLeftIntervalStart() : difference.getRightIntervalStart();
					int end = isLeft ?  difference.getLeftIntervalEnd() : difference.getRightIntervalEnd();
					byte entryType = ((DiffEntry)difference).getEntryType();
					
					String diffEntryType = "diffTypeUnknown";
					switch (entryType) {
					case 1:
						diffEntryType ="diffTypeConflict";
						break;
					case 2:
						diffEntryType ="diffTypeOutgoing";
						break;
					case 3:
						diffEntryType ="diffTypeIncoming";
						break;	
					}
					
					if((currentOffs == start) && (start == end)){
						copyContent(buffer);
						resultedText.append("<span class=\"diffEntry " + diffEntryType + "\" id=\"" + lastIdx +"\"></span>");
					
						lastIdx = i+1;
						foundDiff = true;
						
						break;
					} else if (currentOffs == start) {
						local++;
						
						copyContent(buffer);
						resultedText.append("<span class=\"diffEntry " + diffEntryType + "\" id=\"" + lastIdx +"\">");
					
						foundDiff = true;

						break;
					} else if (currentOffs == end - 1) {
						local--;
						lastIdx = i+1;
						
						copyContent(buffer);
						resultedText.append("</span>");
						foundDiff = true;
						
						break;
					}
					
				}
			//	checkParentEndDiff(currentOffs);
				
			}
		}
		return foundDiff;
	}
	
	
}
