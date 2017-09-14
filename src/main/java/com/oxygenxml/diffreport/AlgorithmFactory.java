package com.oxygenxml.diffreport;

import java.io.Reader;
import java.util.List;

import ro.sync.diff.api.DiffContentTypes;
import ro.sync.diff.api.DiffException;
import ro.sync.diff.api.DiffOptions;
import ro.sync.diff.api.Difference;
import ro.sync.diff.api.DifferencePerformer;

public class AlgorithmFactory {
	
	public static List<Difference> getDifferencesWithAlgorithm(Reader reader1, Reader reader2,int algorithmID, DifferencePerformer diffPerformer) throws DiffException{
		DiffOptions diffOptions;
		String contentType;
		diffOptions = new DiffOptions();
		diffOptions.setEnableHierarchicalDiff(true);
		contentType = DiffContentTypes.XML_CONTENT_TYPE;
		
		switch(algorithmID) {
		case DiffOptions.AUTO:
//			diffOptions.setAlgorithm(DiffOptions.AUTO);
			break;
			
		case DiffOptions.XML_ACCURATE:
			diffOptions.setAlgorithm(DiffOptions.XML_ACCURATE);
			break;
			
		case DiffOptions.XML_FAST:
			diffOptions.setAlgorithm(DiffOptions.XML_FAST);
			break;
		}
		
		return  diffPerformer.performDiff(reader1, reader2, null, null, contentType, diffOptions, null);
	}
	
}
