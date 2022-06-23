package constructDataset;
/**
 * 构造用于BERT(text-classification)的初始数据集
 * 数据形式：code tokens from code snippet \t one var from code snippet \t TRUE/FALSE(第一个之间是以空格分隔)
 */

import java.util.*;
import java.io.*;

public class tokensTextDataset {
	protected static List<String> logVars = new ArrayList<String>();
	protected static List<String> codeVars = new ArrayList<String>();
	protected static List<Integer> logVarCounts = new ArrayList<Integer>();
	protected static List<Integer> codeVarCounts = new ArrayList<Integer>();
//	protected static List<Integer> codeVarsFlag = new ArrayList<Integer>(); // code vars in log vars
//	protected static List<Integer> codeTokensFlag = new ArrayList<Integer>(); // code tokens in log vars
	protected static List<Boolean> twoSeqFlag = new ArrayList<Boolean>();  //[CLS]seq1[SEP]seq2[SEP] flag，即flag
	protected static List<String> codeTokens = new ArrayList<String>();
	protected static List<Integer> codeTokensCounts = new ArrayList<Integer>();
	
	protected static List<StringBuilder> firstSeqs = new ArrayList<StringBuilder>();
	protected static List<String> secondSeqs = new ArrayList<String>();
	protected static List<Integer> secondSeqsCount = new ArrayList<Integer>();
	
	public static void main(String args[]) throws IOException {
		readEntryFromFile("D:\\eclipseWorkspace\\dataset\\activeMQ\\logVar.txt",logVars,"false");
		readEntryFromFile("D:\\eclipseWorkspace\\dataset\\activeMQ\\codeVar.txt",codeVars,"false");
		readEntryFromFile("D:\\eclipseWorkspace\\dataset\\activeMQ\\logVarCount.txt",logVarCounts,"true");
		readEntryFromFile("D:\\eclipseWorkspace\\dataset\\activeMQ\\codeVarCount.txt",codeVarCounts,"true");
		readEntryFromFile("D:\\eclipseWorkspace\\dataset\\activeMQ\\codeToken.txt",codeTokens,"false");
		readEntryFromFile("D:\\eclipseWorkspace\\dataset\\activeMQ\\codeTokenCount.txt",codeTokensCounts,"true");
		
		int preLogVarCount = 0;
		int preCodeTokenCount = 0;
		int preCodeVarCount = 0;
		List<String> keyWord = new ArrayList<String>(Arrays.asList("abstract","boolean","break","byte","case","catch","char","class","continue","do","double","else","extends","final","finally","float","for","if","implements","import","instanceof","int","interface","long","new","package","private","protected","public","return","short","static","super","switch","this","throw","throws","try","void","while"));//这是可变的，List<String> jdks = asList("JDK6", "JDK8", "JDK10");是不可变的，即不能增删
		System.out.print("Creating text dataset……\t");
		for(int i = 0; i < logVarCounts.size(); i++) {
			StringBuilder tmpFirstSeq = new StringBuilder();
			int tmpSecondSeqCount = 0;
			int logVarCount = logVarCounts.get(i);
			int codeTokenCount = codeTokensCounts.get(i);
			int codeVarCount = codeVarCounts.get(i);
			List<String> subLogVars = logVars.subList(preLogVarCount,preLogVarCount+logVarCount);
			List<String> subCodeTokens = codeTokens.subList(preCodeTokenCount, preCodeTokenCount+codeTokenCount);
			List<String> subCodeVars = codeVars.subList(preCodeVarCount, preCodeVarCount+codeVarCount);
			
//			for(int j = 0; j<subCodeVars.size(); j++) {
			for(String str:subCodeTokens) {
				if(subLogVars.contains(str)) {
					secondSeqs.add(str);
					tmpSecondSeqCount++;
					twoSeqFlag.add(true);
				}
				else {
					if(subCodeVars.contains(str) && !keyWord.contains(str)) {
						secondSeqs.add(str);
						tmpSecondSeqCount++;
						twoSeqFlag.add(false);
					}
						
					
				}
				tmpFirstSeq.append(str + " ");
			}
//			}
			preLogVarCount += logVarCount;
			preCodeVarCount += codeVarCount;
			preCodeTokenCount += codeTokenCount;
			firstSeqs.add(tmpFirstSeq);
			secondSeqsCount.add(tmpSecondSeqCount);
		}
		System.out.println("Done!\nSplit dataset to train/dev/test set……");
		//按照8：2：1划分数据集，每个code snippet之间用空行分隔
//		String train_path = "D:\\eclipseWorkspace\\constructDataset\\train.txt.tmp";
//		String val_path = "D:\\eclipseWorkspace\\constructDataset\\dev.txt.tmp";
//		String test_path = "D:\\eclipseWorkspace\\constructDataset\\test.txt.tmp";
		String train_path = "D:\\eclipseWorkspace\\bertDataset\\activeMQ\\textTrain.txt";
		String val_path = "D:\\eclipseWorkspace\\bertDataset\\activeMQ\\textDev.txt";
		String test_path = "D:\\eclipseWorkspace\\bertDataset\\activeMQ\\textTest.txt";
		deleteFile(train_path, val_path, test_path);
		preCodeVarCount = 0;
		try {
			FileOutputStream fosTrain = new FileOutputStream(train_path,true);
			FileOutputStream fosVal = new FileOutputStream(val_path,true);
			FileOutputStream fosTest = new FileOutputStream(test_path,true);
			int firstSeqSize = firstSeqs.size();
			int trainBoundary = (int)(firstSeqSize*0.8); //不加1是因为下标从0开始
			System.out.println("trainBoundary:"+trainBoundary);
			int devBoundary = (int)(firstSeqSize*0.9);
			System.out.println("devBoundary:"+devBoundary);
			System.out.println("firstSeq for dev:"+(devBoundary-trainBoundary));
			System.out.println("firstSeq for test:"+(firstSeqSize-devBoundary));
			writeEntryToFile("D:\\eclipseWorkspace\\bertDataset\\activeMQ\\secondSeqCountTrain.txt", secondSeqsCount.subList(0,trainBoundary));
			writeEntryToFile("D:\\eclipseWorkspace\\bertDataset\\activeMQ\\secondSeqCountDev.txt", secondSeqsCount.subList(trainBoundary,devBoundary));
			writeEntryToFile("D:\\eclipseWorkspace\\bertDataset\\activeMQ\\secondSeqCountTest.txt", secondSeqsCount.subList(devBoundary,firstSeqSize));
			List<Integer> trainSeq = secondSeqsCount.subList(0,trainBoundary);
			int sumTrainTrue = 0;
			for(int num : trainSeq) {
				sumTrainTrue += num;
			}
			System.out.println("sumTrainTrue:"+sumTrainTrue);
			int preCorSecondSeqCount = 0;
			int sumTrain = 0;
			int sumDev = 0;
			int sumTest = 0;
			for(int i=0;i<firstSeqSize;i++) {
				System.out.print("firstSeq:"+i+"\t");
				String firstSeq = firstSeqs.get(i).toString().trim();
				int corSecondSeqCount = secondSeqsCount.get(i);
				if (i<trainBoundary) {
					sumTrain += corSecondSeqCount;
					for(int j = preCorSecondSeqCount; j<preCorSecondSeqCount+corSecondSeqCount; j++) {
						fosTrain.write((firstSeq+"\t"+secondSeqs.get(j)+"\t"+twoSeqFlag.get(j)+"\n").getBytes());
					}
				}
				if (i>=trainBoundary && i<devBoundary) {
					sumDev += corSecondSeqCount;
					for(int j = preCorSecondSeqCount; j<preCorSecondSeqCount+corSecondSeqCount; j++) {
						fosVal.write((firstSeq+"\t"+secondSeqs.get(j)+"\t"+twoSeqFlag.get(j)+"\n").getBytes());
					}
				}
				if (i>=devBoundary) {
					sumTest += corSecondSeqCount;
					for(int j = preCorSecondSeqCount; j<preCorSecondSeqCount+corSecondSeqCount; j++) {
						fosTest.write((firstSeq+"\t"+secondSeqs.get(j)+"\t"+twoSeqFlag.get(j)+"\n").getBytes());
					}
				}
				preCorSecondSeqCount += corSecondSeqCount;
			}
			System.out.println("\nsumTrain:"+sumTrain+"\tsumDev:"+sumDev+"\tsumTest:"+sumTest);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("\nDone!");
	}
	public static void deleteFile(String train, String dev, String test) {
		File trainFile = new File(train);
		File devFile = new File(dev);
		File testFile = new File(test);
		if(trainFile.exists()) {
			trainFile.delete();
		}
		if(devFile.exists()) {
			devFile.delete();
		}
		if(testFile.exists()) {
			testFile.delete();
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
	
	public static void writeEntryToFile(String path, List<Integer> counts) {
		try {
			FileOutputStream fos = new FileOutputStream(path);
			for(int i = 0; i<counts.size(); i++) {
				fos.write((counts.get(i)+"\n").getBytes());
			}
			fos.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}

