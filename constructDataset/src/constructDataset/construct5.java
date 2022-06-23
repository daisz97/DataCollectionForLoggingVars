package constructDataset;
/**
 * 构造用于BERT(text-classification)的初始数据集
 * 数据形式：var from code snippet \t one var from code snippet \t TRUE/FALSE(第一个之间是以空格分隔)
 */

import java.util.*;
import java.io.*;

public class construct5 {
	protected static List<String> logVars = new ArrayList<String>();
	protected static List<String> codeVars = new ArrayList<String>();
	protected static List<Integer> logVarCounts = new ArrayList<Integer>();
	protected static List<Integer> codeVarCounts = new ArrayList<Integer>();
	protected static List<Integer> codeVarsFlag = new ArrayList<Integer>();
	
	public static void main(String args[]) throws IOException {
		readEntryFromFile("D:\\eclipseWorkspace\\extraction\\logVar.txt",logVars,"false");
		readEntryFromFile("D:\\eclipseWorkspace\\extraction\\codeVar.txt",codeVars,"false");
		readEntryFromFile("D:\\eclipseWorkspace\\extraction\\logVarCount.txt",logVarCounts,"true");
		readEntryFromFile("D:\\eclipseWorkspace\\extraction\\codeVarCount.txt",codeVarCounts,"true");
		int preLogVarCount = 0;
		int preCodeVarCount = 0;
		for(int i = 0; i < logVarCounts.size(); i++) {
			int logVarCount = logVarCounts.get(i);
			int codeVarCount = codeVarCounts.get(i);
			List<String> subLogVars = logVars.subList(preLogVarCount,preLogVarCount+logVarCount);
			List<String> subCodeVars = codeVars.subList(preCodeVarCount, preCodeVarCount+codeVarCount);
//			for(int j = 0; j<subCodeVars.size(); j++) {
				for(String str:subCodeVars) {
					if(subLogVars.contains(str)) {
						codeVarsFlag.add(1);
					}
					else {
						codeVarsFlag.add(0);
					}
				}
//			}
			preLogVarCount += logVarCount;
			preCodeVarCount += codeVarCount;
		}
		//按照8：2：1划分数据集，每个code snippet之间用空行分隔
//		String train_path = "D:\\eclipseWorkspace\\constructDataset\\train.txt.tmp";
//		String val_path = "D:\\eclipseWorkspace\\constructDataset\\dev.txt.tmp";
//		String test_path = "D:\\eclipseWorkspace\\constructDataset\\test.txt.tmp";
		String train_path = "D:\\eclipseWorkspace\\constructDataset\\textClassifyTrain.txt.tmp";
		String val_path = "D:\\eclipseWorkspace\\constructDataset\\textClassifyDev.txt.tmp";
		String test_path = "D:\\eclipseWorkspace\\constructDataset\\textClassifyTest.txt.tmp";
		List<String> keyWord = new ArrayList<String>(Arrays.asList("abstract","boolean","break","byte","case","catch","char","class","continue","do","double","else","extends","final","finally","float","for","if","implements","import","instanceof","int","interface","long","new","package","private","protected","public","return","short","static","super","switch","this","throw","throws","try","void","while"));//这是可变的，List<String> jdks = asList("JDK6", "JDK8", "JDK10");是不可变的，即不能增删
		preCodeVarCount = 0;
		try {
			FileOutputStream fosTrain = new FileOutputStream(train_path,true);
			FileOutputStream fosVal = new FileOutputStream(val_path,true);
			FileOutputStream fosTest = new FileOutputStream(test_path,true);
			for(int i=0;i<codeVarCounts.size();i++) {
				int codeVarCount = codeVarCounts.get(i);
				StringBuilder varFromCode = new StringBuilder();
				List<Boolean> isVarFromLog = new ArrayList<Boolean>();
				//新建一个列表来存储一个code snippet的变量，因为下面有的是过滤了的
				List<String> varFromLog = new ArrayList<String>();
				for(int j=preCodeVarCount;j<preCodeVarCount+codeVarCount;j++) {
					if(keyWord.contains(codeVars.get(j))) {
						continue;
					}
					varFromCode.append(codeVars.get(j) + " ");
					
					if(codeVarsFlag.get(j) == 1) {
						isVarFromLog.add(true);
						varFromLog.add(codeVars.get(j));
					}else {
						isVarFromLog.add(false);
						varFromLog.add(codeVars.get(j));
					}
				}
				
				if (i<=codeVarCounts.size()*0.8) {
					for(int k = 0; k<isVarFromLog.size(); k++) {
						fosTrain.write((varFromCode+"\t"+varFromLog.get(k)+"\t"+isVarFromLog.get(k)+"\n").getBytes());
					}
					isVarFromLog = null;
					varFromLog = null;
				}
				if (i>codeVarCounts.size()*0.8 && i<=codeVarCounts.size()*0.9) {
					for(int k = 0; k<isVarFromLog.size(); k++) {
						fosVal.write((varFromCode+"\t"+varFromLog.get(k)+"\t"+isVarFromLog.get(k)+"\n").getBytes());
					}
					isVarFromLog = null;
					varFromLog = null;
				}
				if (i>codeVarCounts.size()*0.9) {
					for(int k = 0; k<isVarFromLog.size(); k++) {
						fosTest.write((varFromCode+"\t"+varFromLog.get(k)+"\t"+isVarFromLog.get(k)+"\n").getBytes());
					}
					isVarFromLog = null;
					varFromLog = null;
				}
				preCodeVarCount+=codeVarCount;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	public static void readEntryFromFile(String path,List list,String flag) throws IOException {//flag = true,则将String转化为Int。Integer.parserInt(str);
		FileInputStream fileInputStream = new FileInputStream(path);
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
		String str;
		switch(flag) {
			case "false":
				while((str = bufferedReader.readLine()) != null) {
					list.add(str);
				}
				break;
			case "true":
				while((str = bufferedReader.readLine()) != null) {
					list.add(Integer.parseInt(str));
				}
				break;
		}
			
		
	}
	
	public static void writeEntryToFile(String path, List<String> var, List<Integer> token) {
		try {
			FileOutputStream fos = new FileOutputStream(path,true);
			for(int i = 0; i<var.size(); i++) {
				fos.write((var.get(i)+"\t"+token.get(i)+"\n").getBytes());
			}
			fos.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
