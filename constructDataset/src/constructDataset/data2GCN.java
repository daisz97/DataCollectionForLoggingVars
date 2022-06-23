package constructDataset;
/**
 * 构建用于GCN的数据，每一个代码段作为一个字符串，按8：1：1划分train/dev/test。
 * code var snippet存入codeVarSnippets.txt
 * 所有token对应的label放入tokensLabel.txt
 * 所有snippet属于train/val/test的那个数据集的label放入snippetsLabel.txt
 * 
 */
import java.util.*;
import java.io.*;

public class data2GCN {
	protected static List<String> logVars = new ArrayList<String>();
	protected static List<String> codeTokens = new ArrayList<String>();
	protected static List<String> codeVars = new ArrayList<String>();
	protected static List<Integer> logVarsCounts = new ArrayList<Integer>();
	protected static List<Integer> codeTokensCounts = new ArrayList<Integer>();
	protected static List<Integer> codeVarsCounts = new ArrayList<Integer>();
	
	protected static List<Integer> tokenInLog = new ArrayList<Integer>();
	protected static List<Integer> tokenInCodeVar = new ArrayList<Integer>();
	
	public static void main(String args[]) throws IOException {
		readEntryFromFile("D:\\Documents\\Program\\Eclipse\\data\\activeMQ\\logVar.txt",logVars,"false");
		readEntryFromFile("D:\\Documents\\Program\\Eclipse\\data\\activeMQ\\codeToken.txt",codeTokens,"false");
		readEntryFromFile("D:\\Documents\\Program\\Eclipse\\data\\activeMQ\\codeVar.txt",codeVars,"false");
		
		readEntryFromFile("D:\\Documents\\Program\\Eclipse\\data\\activeMQ\\logVarCount.txt",logVarsCounts,"true");
		readEntryFromFile("D:\\Documents\\Program\\Eclipse\\data\\activeMQ\\codeTokenCount.txt",codeTokensCounts,"true");
		readEntryFromFile("D:\\Documents\\Program\\Eclipse\\data\\activeMQ\\codeVarCount.txt",codeVarsCounts,"true");
//		for(int i =0;i<10;i++) {
//			System.out.println(logVars.get(i)+"\t"+logVars.get(i).getClass().getSimpleName()+"\t"+codeVarCounts.get(i)+"\t"+codeVarCounts.get(i).getClass().getSimpleName());
//		}
		int preLogVarCount = 0;
		int preCodeTokenCount = 0;
		int count = 0;
		System.out.println("codeVarCounts.size(): "+codeVarsCounts.size());
		System.out.println("codeTokensCounts.size: "+codeTokensCounts.size());
		System.out.println("logVarCounts.size(): "+logVarsCounts.size());
		for(int i = 0; i < logVarsCounts.size(); i++) {
//			System.out.println("log "+i);
			int logVarCount = logVarsCounts.get(i);
			int codeVarCount = codeTokensCounts.get(i);
			List<String> subLogVars = logVars.subList(preLogVarCount,preLogVarCount+logVarCount);
			List<String> subCodeTokens = codeTokens.subList(preCodeTokenCount, preCodeTokenCount+codeVarCount);
//			for(int j = 0; j<subCodeVars.size(); j++) {
				for(String str:subCodeTokens) {
					if(subLogVars.contains(str)) {
						tokenInLog.add(1);
					}
					else {
						tokenInLog.add(0);
					}
				}
//			}
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
		
		//按照8：2：1划分数据集
		// 每一个code snippet的code var生成一个String
		String codeVarSnippetPath = "D:\\Documents\\Program\\Eclipse\\gcnData\\activeMQ\\codeVarSnippets.txt";
		String tokenLabelPath = "D:\\Documents\\Program\\Eclipse\\gcnData\\activeMQ\\tokensLabel.txt";  //相同的token，只要有一个代码段中出现true,就认为它对应的label是true
		String snippetLabelPath = "D:\\Documents\\Program\\Eclipse\\gcnData\\activeMQ\\snippetsLabel.txt";
		deleteFile(codeVarSnippetPath);
		deleteFile(tokenLabelPath);
		preCodeVarCount = 0;
		try {
			FileOutputStream fosSnippet = new FileOutputStream(codeVarSnippetPath,true);
			FileOutputStream fosLabel = new FileOutputStream(tokenLabelPath,true);
			FileOutputStream fosSnippetLabel = new FileOutputStream(snippetLabelPath,true);
			for(int i=0;i<codeTokensCounts.size();i++) {
				List<String> snippet = new ArrayList<String>();
				int codeVarCount = codeTokensCounts.get(i);
				for(int j=preCodeVarCount;j<preCodeVarCount+codeVarCount;j++) {
					if(i < codeTokensCounts.size()*0.8) {
						if(tokenInLog.get(j) == 1 && tokenInCodeVar.get(j)==1) {
							fosLabel.write((("train_"+codeTokens.get(j))+"\tyes\t"+"yes\n").getBytes());
						}else if(tokenInLog.get(j) == 1 && tokenInCodeVar.get(j)==0) {
							fosLabel.write((("train_"+codeTokens.get(j))+"\tyes\t"+"no\n").getBytes());
						}else if(tokenInLog.get(j) == 0 && tokenInCodeVar.get(j)==1) {
							fosLabel.write((("train_"+codeTokens.get(j))+"\tno\t"+"yes\n").getBytes());
						}else if(tokenInLog.get(j) == 0 && tokenInCodeVar.get(j)==0) {
							fosLabel.write((("train_"+codeTokens.get(j))+"\tno\t"+"no\n").getBytes());
						}
					}
					else if(i >= codeTokensCounts.size()*0.8 && i<codeTokensCounts.size()*0.9) {
						if(tokenInLog.get(j) == 1 && tokenInCodeVar.get(j)==1) {
							fosLabel.write((("val_"+codeTokens.get(j))+"\tyes\t"+"yes\n").getBytes());
						}else if(tokenInLog.get(j) == 1 && tokenInCodeVar.get(j)==0) {
							fosLabel.write((("val_"+codeTokens.get(j))+"\tyes\t"+"no\n").getBytes());
						}else if(tokenInLog.get(j) == 0 && tokenInCodeVar.get(j)==1) {
							fosLabel.write((("val_"+codeTokens.get(j))+"\tno\t"+"yes\n").getBytes());
						}else if(tokenInLog.get(j) == 0 && tokenInCodeVar.get(j)==0) {
							fosLabel.write((("val_"+codeTokens.get(j))+"\tno\t"+"no\n").getBytes());
						}
					}
					else if(i >= codeTokensCounts.size()*0.9) {
						if(tokenInLog.get(j) == 1 && tokenInCodeVar.get(j)==1) {
							fosLabel.write((("test_"+codeTokens.get(j))+"\tyes\t"+"yes\n").getBytes());
						}else if(tokenInLog.get(j) == 1 && tokenInCodeVar.get(j)==0) {
							fosLabel.write((("test_"+codeTokens.get(j))+"\tyes\t"+"no\n").getBytes());
						}else if(tokenInLog.get(j) == 0 && tokenInCodeVar.get(j)==1) {
							fosLabel.write((("test_"+codeTokens.get(j))+"\tno\t"+"yes\n").getBytes());
						}else if(tokenInLog.get(j) == 0 && tokenInCodeVar.get(j)==0) {
							fosLabel.write((("test_"+codeTokens.get(j))+"\tno\t"+"no\n").getBytes());
						}
					}
					if(tokenInCodeVar.get(j)==1) {
						snippet.add(codeTokens.get(j));
					}
				}
				fosLabel.write("\n".getBytes());
				if(snippet.size()>0) {
					String snippetString = "";
					if(i < codeTokensCounts.size()*0.8) {
						for(String var : snippet) {
							var = "train_"+var;
							snippetString = snippetString + " " + var;
						}
						fosSnippetLabel.write("train\n".getBytes());
					}
					else if(i >= codeTokensCounts.size()*0.8 && i<codeTokensCounts.size()*0.9) {
						for(String var : snippet) {
							var = "val_"+var;
							snippetString = snippetString + " " + var;
						}
						fosSnippetLabel.write("val\n".getBytes());
					}
					else if(i >= codeTokensCounts.size()*0.9) {
						for(String var : snippet) {
							var = "test_"+var;
							snippetString = snippetString + " " + var;
						}
						fosSnippetLabel.write("test\n".getBytes());
					}
					fosSnippet.write((snippetString.trim()+"\n").getBytes());
				}
				preCodeVarCount+=codeVarCount;
			}
			fosSnippet.close();
			fosLabel.close();
			fosSnippetLabel.close();
			System.out.println("train size: "+ codeTokensCounts.size()*0.8);
			System.out.println("val size: "+ (codeTokensCounts.size()*0.9-codeTokensCounts.size()*0.8));
			System.out.println("test size: "+ (codeTokensCounts.size()-codeTokensCounts.size()*0.9));
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
		bufferedReader.close();	
		
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
