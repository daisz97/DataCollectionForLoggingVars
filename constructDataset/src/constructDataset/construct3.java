package constructDataset;
/**
 * 构造用于BERT(token-classification)的初始数据集
 */

import java.util.*;
import java.io.*;

public class construct3 {
	protected static List<String> logVars = new ArrayList<String>();
	protected static List<String> codeVars = new ArrayList<String>();
	protected static List<Integer> logVarCounts = new ArrayList<Integer>();
	protected static List<Integer> codeVarCounts = new ArrayList<Integer>();
	protected static List<Integer> codeVarsFlag = new ArrayList<Integer>();
	
	public static void main(String args[]) throws IOException {
//		readEntryFromFile("/home/daisz/extraction/logVar.txt",logVars,"false");
//		readEntryFromFile("/home/daisz/extraction/codeVar.txt",codeVars,"false");
//		readEntryFromFile("/home/daisz/extraction/logVarCount.txt",logVarCounts,"true");
//		readEntryFromFile("/home/daisz/extraction/codeVarCount.txt",codeVarCounts,"true");
		readEntryFromFile("D:\\eclipseWorkspace\\extraction\\logVar.txt",logVars,"false");
		readEntryFromFile("D:\\eclipseWorkspace\\extraction\\codeVar.txt",codeVars,"false");
		readEntryFromFile("D:\\eclipseWorkspace\\extraction\\logVarCount.txt",logVarCounts,"true");
		readEntryFromFile("D:\\eclipseWorkspace\\extraction\\codeVarCount.txt",codeVarCounts,"true");
//		for(int i =0;i<10;i++) {
//			System.out.println(logVars.get(i)+"\t"+logVars.get(i).getClass().getSimpleName()+"\t"+codeVarCounts.get(i)+"\t"+codeVarCounts.get(i).getClass().getSimpleName());
//		}
		int preLogVarCount = 0;
		int preCodeVarCount = 0;
		int count = 0;
		for(int i = 0; i < logVarCounts.size(); i++) {
//			System.out.println("log "+i);
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
		String train_path = "D:\\eclipseWorkspace\\constructDataset\\trainFilted.txt.tmp";
		String val_path = "D:\\eclipseWorkspace\\constructDataset\\devFilted.txt.tmp";
		String test_path = "D:\\eclipseWorkspace\\constructDataset\\testFilted.txt.tmp";
		List<String> keyWord = new ArrayList<String>(Arrays.asList("abstract","boolean","break","byte","case","catch","char","class","continue","do","double","else","extends","final","finally","float","for","if","implements","import","instanceof","int","interface","long","new","package","private","protected","public","return","short","static","String","super","switch","this","throw","throws","try","void","while"));//这是可变的，List<String> jdks = asList("JDK6", "JDK8", "JDK10");是不可变的，即不能增删
		System.out.println(keyWord);
		preCodeVarCount = 0;
		try {
			FileOutputStream fosTrain = new FileOutputStream(train_path,true);
			FileOutputStream fosVal = new FileOutputStream(val_path,true);
			FileOutputStream fosTest = new FileOutputStream(test_path,true);
			for(int i=0;i<codeVarCounts.size();i++) {
				int codeVarCount = codeVarCounts.get(i);
				if (i<=codeVarCounts.size()*0.8) {
					for(int j=preCodeVarCount;j<preCodeVarCount+codeVarCount;j++) {
						if(keyWord.contains(codeVars.get(j))) {
							continue;
						}
						if(codeVarsFlag.get(j) == 1) {
							fosTrain.write((codeVars.get(j)+"\t"+"TRUE\n").getBytes());
						}else {
							fosTrain.write((codeVars.get(j)+"\t"+"O\n").getBytes());
						}
//						fosTrain.write((codeVars.get(j)+"\t"+codeVarsFlag.get(j)+"\n").getBytes());
					}
					fosTrain.write("\n".getBytes());
				}
				if (i>codeVarCounts.size()*0.8 && i<=codeVarCounts.size()*0.9) {
					for(int j=preCodeVarCount;j<preCodeVarCount+codeVarCount;j++) {
						if(keyWord.contains(codeVars.get(j))) {
							continue;
						}
						if(codeVarsFlag.get(j)==1) {
							fosVal.write((codeVars.get(j)+"\t"+"TRUE\n").getBytes());
						}else {
							fosVal.write((codeVars.get(j)+"\t"+"O\n").getBytes());
						}
//						fosVal.write((codeVars.get(j)+"\t"+codeVarsFlag.get(j)+"\n").getBytes());
					}
					fosVal.write("\n".getBytes());
				}
				if (i>codeVarCounts.size()*0.9) {
					for(int j=preCodeVarCount;j<preCodeVarCount+codeVarCount;j++) {
						if(keyWord.contains(codeVars.get(j))) {
							continue;
						}
						if(codeVarsFlag.get(j)==1) {
							fosTest.write((codeVars.get(j)+"\t"+"TRUE\n").getBytes());
						}else {
							fosTest.write((codeVars.get(j)+"\t"+"O\n").getBytes());
						}
//						fosTest.write((codeVars.get(j)+"\t"+codeVarsFlag.get(j)+"\n").getBytes());
					}
					fosTest.write("\n".getBytes());
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
