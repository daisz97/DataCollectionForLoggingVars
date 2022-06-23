package constructDataset;

/**
 * 构建小数据集，用来做GNNBERT的over fit，train/dev/test都使用相同的小数据。
 * @author DSZ
 *
 */
import java.util.*;
import java.io.*;

public class smallDatasetForGNNBERT {
	protected static List<String> logVars = new ArrayList<String>();
	protected static List<String> codeTokens = new ArrayList<String>();
	protected static List<String> codeVars = new ArrayList<String>();
	protected static List<Integer> logVarsCounts = new ArrayList<Integer>();
	protected static List<Integer> codeTokensCounts = new ArrayList<Integer>();
	protected static List<Integer> codeVarsCounts = new ArrayList<Integer>();
	
	protected static List<Integer> tokenInLog = new ArrayList<Integer>();
	protected static List<Integer> tokenInCodeVar = new ArrayList<Integer>();
	
	
	public static void main(String args[]) throws IOException {
		readEntryFromFile("D:\\Documents\\Program\\Eclipse\\data\\hadoop\\logVar.txt",logVars,"false");
		readEntryFromFile("D:\\Documents\\Program\\Eclipse\\data\\hadoop\\codeToken.txt",codeTokens,"false");
		readEntryFromFile("D:\\Documents\\Program\\Eclipse\\data\\hadoop\\codeVar.txt",codeVars,"false");
		
		readEntryFromFile("D:\\Documents\\Program\\Eclipse\\data\\hadoop\\logVarCount.txt",logVarsCounts,"true");
		readEntryFromFile("D:\\Documents\\Program\\Eclipse\\data\\hadoop\\codeTokenCount.txt",codeTokensCounts,"true");
		readEntryFromFile("D:\\Documents\\Program\\Eclipse\\data\\hadoop\\codeVarCount.txt",codeVarsCounts,"true");
		
		int preLogVarCount = 0;
		int preCodeTokenCount = 0;
		int count = 0;
		System.out.println("codeVarCounts.size(): "+codeVarsCounts.size());
		System.out.println("codeTokensCounts.size: "+codeTokensCounts.size());
		System.out.println("logVarCounts.size(): "+logVarsCounts.size());
		for(int i = 0; i < logVarsCounts.size(); i++) {
			int logVarCount = logVarsCounts.get(i);
			int codeVarCount = codeTokensCounts.get(i);
			List<String> subLogVars = logVars.subList(preLogVarCount,preLogVarCount+logVarCount);
			List<String> subCodeTokens = codeTokens.subList(preCodeTokenCount, preCodeTokenCount+codeVarCount);
			for(String str:subCodeTokens) {
				if(subLogVars.contains(str)) {
					tokenInLog.add(1);
				}
				else {
					tokenInLog.add(0);
				}
			}
			preLogVarCount += logVarCount;
			preCodeTokenCount += codeVarCount;
		}
		int preCodeVarCount = 0;
		preCodeTokenCount = 0;
		for(int i = 0; i < codeVarsCounts.size(); i++) {
			if(i==codeVarsCounts.size()-1) {
				System.out.println(i);
			}
			System.out.println("log "+i);
			int codeTokenCount = codeTokensCounts.get(i);
			int codeVarCount = codeVarsCounts.get(i);
			List<String> subCodeVars = codeVars.subList(preCodeVarCount,preCodeVarCount+codeVarCount);
			List<String> subCodeTokens = codeTokens.subList(preCodeTokenCount, preCodeTokenCount+codeTokenCount);
//			for(int j = 0; j<subCodeVars.size(); j++) {
				for(String str:subCodeTokens) {
					if(subCodeVars.contains(str)) {
						tokenInCodeVar.add(1);
					}
					else {
						tokenInCodeVar.add(0);
					}
				}
//			}
			preCodeTokenCount += codeTokenCount;
			preCodeVarCount += codeVarCount;
		}
		
		//每个code snippet之间用空行分隔
		String train_path = "D:\\Documents\\Program\\Eclipse\\BertDataset\\hadoop\\smallDatasetTrain.txt";
		deleteFile(train_path);
		preCodeVarCount = 0;
		try {
			FileOutputStream fosTrain = new FileOutputStream(train_path,true);
			for(int i=0;i<codeTokensCounts.size();i++) {
				int codeVarCount = codeTokensCounts.get(i);
				if (i<codeTokensCounts.size()*0.03) {//应该改为<
					for(int j=preCodeVarCount;j<preCodeVarCount+codeVarCount;j++) {
//						if(tokenInLog.get(j) == 1 && tokenInCodeVar.get(j)==1) {
//							fosTrain.write((codeTokens.get(j)+"\tTRUE\t"+"TRUE\n").getBytes());
//						}else if(tokenInLog.get(j) == 1 && tokenInCodeVar.get(j)==0) {
//							fosTrain.write((codeTokens.get(j)+"\tTRUE\t"+"O\n").getBytes());
						if(tokenInLog.get(j) == 1) {
							fosTrain.write((codeTokens.get(j)+"\tTRUE\t"+"TRUE\n").getBytes());
						}else if(tokenInLog.get(j) == 0 && tokenInCodeVar.get(j)==1) {
							fosTrain.write((codeTokens.get(j)+"\tO\t"+"TRUE\n").getBytes());
						}else if(tokenInLog.get(j) == 0 && tokenInCodeVar.get(j)==0) {
							fosTrain.write((codeTokens.get(j)+"\tO\t"+"O\n").getBytes());
						}
					}
					fosTrain.write("\n".getBytes());
				}
				preCodeVarCount+=codeVarCount;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("snippet size of small dataset:" + Math.floor(codeTokensCounts.size()*0.03));
		
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
	public static void deleteFile(String train) {
		File trainFile = new File(train);
		if(trainFile.exists()) {
			trainFile.delete();
		}
	}
}
