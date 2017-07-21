package com.oxygenxml.sdksamples.workspace;

public class Constants {
	public static String pathToFirstHTML = "C:/Users/intern3/Desktop/myFiles/diffSample/htmlFile.html";
	//public static String pathToSecondHTML ="C:/Users/intern5/Desktop/myFiles/diffSample/htmlFile2.html";
	private static String firstFile ;
	private static String secondFile;
	
	public static String getFirstFile() {
		return firstFile;
	}
	public static void setFirstFile(String firstFile) {
		Constants.firstFile = firstFile;
	}
	public static String getSecondFile() {
		return secondFile;
	}
	public static void setSecondFile(String secondFile) {
		Constants.secondFile = secondFile;
	}
	
	
}
