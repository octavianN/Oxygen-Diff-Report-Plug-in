package com.oxygenxml.sdksamples.workspace;

import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import ro.sync.diff.api.Difference;



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
	
	
	private TreeSet<Integer> noDuplicates; //checks index duplicates
	
	Comparator<Integer> comparator = new Comparator<Integer>() {
        @Override
        public int compare(Integer a, Integer b) {
            return b-a;
        }
    };
	
	public HTMLContentGenerator(List<Difference> differences, boolean isLeft) {
		this.differences = differences;
		
		this.isLeft = isLeft;
		resultedText = new StringBuilder();
		
		noDuplicates = new TreeSet<Integer>(comparator);
		noDuplicates.add(Integer.MAX_VALUE);
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


	@Override
	public boolean checkDiff(int currentOffs, String buffer) {
		
		boolean foundDiff = false;

		if(!noDuplicates.contains(new Integer(currentOffs))){
			noDuplicates.add(new Integer(currentOffs));
		
			if(differences != null){
				
				
				for (int i = 0; i < differences.size(); i++) {
					Difference difference = differences.get(i);
											
					int start = isLeft ?  difference.getLeftIntervalStart() : difference.getRightIntervalStart();
					int end = isLeft ?  difference.getLeftIntervalEnd() : difference.getRightIntervalEnd();
						
					if (currentOffs == start) {
						local++;
						
						copyContent(buffer);
						resultedText.append("<span class=\"diffEntry\" id=\"" + lastIdx +"\">");
					
						lastIdx = i;
						foundDiff = true;

						break;
					} else if (currentOffs == end - 1) {
						local--;
						
						copyContent(buffer);
						resultedText.append("</span>");
						foundDiff = true;
						
						break;
					}
					
				}
			}
		}
		return foundDiff;
	}
	
	
}
