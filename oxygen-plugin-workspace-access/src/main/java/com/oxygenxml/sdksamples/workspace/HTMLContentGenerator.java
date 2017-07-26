package com.oxygenxml.sdksamples.workspace;

import java.util.List;

import com.ibm.icu.impl.Differ;

import ro.sync.diff.api.Difference;
import ro.sync.diff.text.DiffEntry;
import ro.sync.diff.xml.DiffEntryType;

public class HTMLContentGenerator implements ContentListener {

	
	private StringBuilder resultedText;
	private List<Difference> differences;
	private boolean isLeft;
	private int lastIdx = 0;
	
	
	
	
	public HTMLContentGenerator(List<Difference> differences, boolean isLeft) {
		this.differences = differences;
		
		this.isLeft = isLeft;
		resultedText = new StringBuilder();
	}
	
	
	
	public String getResultedText() {
		System.out.println("local: " + local);
		return resultedText.toString();
	}

	
	
//	public void startSpan(HTMLTypes type, String content){}
//	
//	public void endSpan(HTMLTypes type, String content){}
//	
//	public void addSpaces(char character){
//		resultedText.append(character);
//	}


	@Override
	public void startNode(NodeType type, String content) {
//		System.out.println("Start node: " + type +" content ='" + content +"'");
		
		switch(type){
		case Element:
			resultedText.append( "<span class = \"Element\">" );
			break;
		case textField:
			resultedText.append("<span class = \"textField\">");;
			break;
		case attributeName:
			resultedText.append( "<span class = \"attributeName\">");
			break;
		case attributeValue:
			resultedText.append("<span class = \"attributeValue\">");
			break;
		case PI:
			resultedText.append("<span class = \"PI\">");
			break;
		case Doctype:
			resultedText.append("<span class = \"Doctype\">");
			break;
		case CDATA:
			resultedText.append("<span class = \"CDATA\">");
			break;
		case Comment:
			resultedText.append("<span class = \"Comment\">");
			
		}
		
		resultedText.append(content);
		
		
	}


	@Override
	public void endNode(NodeType type, String content) {
		//System.out.println("End node: " + type +" content ='" + content +"'");
		resultedText.append(content);
	}

	int local = 0;


	@Override
	public boolean checkDiff(int currentOffs, String buffer) {
		
		boolean foundDiff = false;
		
//		if( buffer.length() > 0)
//			System.out.println(currentOffs + "  " +buffer.charAt(buffer.length()-1));
//		else
//			System.out.println(currentOffs);
		
		if(differences != null){
			
			
			for (int i = 0; i < differences.size(); i++) {
				Difference difference = differences.get(i);
					
//				System.out.println("Offset: " + currentOffs);
				
				int start = isLeft ?  difference.getLeftIntervalStart() : difference.getRightIntervalStart();
				int end = isLeft ?  difference.getLeftIntervalEnd() : difference.getRightIntervalEnd();
					
				if (currentOffs == start + 1) {
					local++;
				//	System.out.println("Offset START: " + currentOffs+ " ");
					if( buffer.length() > 0)
						System.out.println("InStartOffset: " + currentOffs + "  " +buffer);
					else
						System.out.println(currentOffs);
					endNode(NodeType.EmptyData, buffer);
					resultedText.append("<span class=\"diffEntry\" id=" + lastIdx +" >");
					lastIdx = i;
					foundDiff = true;
					break;
				} else if (currentOffs == end + 1) {
					local--;
			//		System.out.println("Offset END: " + currentOffs + " ");
					endNode(NodeType.EmptyData, buffer);
					resultedText.append("</span>");
					foundDiff = true;
					break;
				}
				
			}
		}
		return foundDiff;
	}
	
	
}
