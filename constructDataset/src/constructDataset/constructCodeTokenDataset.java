package constructDataset;
/**20200926
*ʹ��code tokens�����µ�����token-classification�����ݼ�@@code token @@token in log? @@ token in codeVar?
*/
import java.util.*;
import java.io.*;

public class constructCodeTokenDataset {
	protected static List<String> logVars = new ArrayList<String>();
	protected static List<String> codeTokens = new ArrayList<String>();
	protected static List<String> codeVars = new ArrayList<String>();
	protected static List<Integer> logVarsCounts = new ArrayList<Integer>();
	protected static List<Integer> codeTokensCounts = new ArrayList<Integer>();
	protected static List<Integer> codeVarsCounts = new ArrayList<Integer>();
	
	protected static List<Integer> tokenInLog = new ArrayList<Integer>();
	protected static List<Integer> tokenInCodeVar = new ArrayList<Integer>();
	
//	public static List<String> keyWord = new ArrayList<String>(Arrays.asList("abstract","boolean","break","byte","case","catch","char","class","continue","do","double","else","extends","final","finally","float","for","if","implements","import","instanceof","int","interface","long","new","package","private","protected","public","return","short","static","String","super","switch","this","throw","throws","try","void","while"));//���ǿɱ�ģ�List<String> jdks = asList("JDK6", "JDK8", "JDK10");�ǲ��ɱ�ģ���������ɾ

	
	
	public static void main(String args[]) throws IOException {
//		readEntryFromFile("/home/daisz/extraction/logVar.txt",logVars,"false");
//		readEntryFromFile("/home/daisz/extraction/codeVar.txt",codeVars,"false");
//		readEntryFromFile("/home/daisz/extraction/logVarCount.txt",logVarCounts,"true");
//		readEntryFromFile("/home/daisz/extraction/codeVarCount.txt",codeVarCounts,"true");
		readEntryFromFile("D:\\Documents\\Program\\Eclipse\\Dataset\\extractionV3based\\extracted\\hadoop\\logVar.txt",logVars,"false");
		readEntryFromFile("D:\\Documents\\Program\\Eclipse\\Dataset\\extractionV3based\\extracted\\hadoop\\codeToken.txt",codeTokens,"false");
		readEntryFromFile("D:\\Documents\\Program\\Eclipse\\Dataset\\extractionV3based\\extracted\\hadoop\\codeVar.txt",codeVars,"false");
		
		readEntryFromFile("D:\\Documents\\Program\\Eclipse\\Dataset\\extractionV3based\\extracted\\hadoop\\logVarCount.txt",logVarsCounts,"true");
		readEntryFromFile("D:\\Documents\\Program\\Eclipse\\Dataset\\extractionV3based\\extracted\\hadoop\\codeTokenCount.txt",codeTokensCounts,"true");
		readEntryFromFile("D:\\Documents\\Program\\Eclipse\\Dataset\\extractionV3based\\extracted\\hadoop\\codeVarCount.txt",codeVarsCounts,"true");
//		for(int i =0;i<10;i++) {
//			System.out.println(logVars.get(i)+"\t"+logVars.get(i).getClass().getSimpleName()+"\t"+codeVarCounts.get(i)+"\t"+codeVarCounts.get(i).getClass().getSimpleName());
//		}
		
		//�ж�codetokens������codetokensCounts��¼�ĸ����Ƿ����
		int codeTokensCountsSum = 0;
		for(int i = 0; i< codeTokensCounts.size(); i++) {
			codeTokensCountsSum += codeTokensCounts.get(i);
		}
		System.out.println("codeTokensCountsSum:"+codeTokensCountsSum+",codeTokensSum:"+codeTokens.size());
		
		int preLogVarCount = 0;
		int preCodeTokenCount = 0;
		int count = 0;
		System.out.println("codeVarCounts.size(): "+codeVarsCounts.size());
		System.out.println("codeTokensCounts.size: "+codeTokensCounts.size());
		System.out.println("logVarCounts.size(): "+logVarsCounts.size());
		for(int i = 0; i < logVarsCounts.size(); i++) {
//			System.out.println("log "+i);
			int logVarCount = logVarsCounts.get(i);
			int codeTokenCount = codeTokensCounts.get(i);
			List<String> subLogVars = logVars.subList(preLogVarCount,preLogVarCount+logVarCount);
			List<String> subCodeTokens = codeTokens.subList(preCodeTokenCount, preCodeTokenCount+codeTokenCount);
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
			preCodeTokenCount += codeTokenCount;
		}
		int preCodeVarCount = 0;//����һ���ֺ���һ���ֿ��Ժϲ�
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
		
		//����8��2��1�������ݼ���ÿ��code snippet֮���ÿ��зָ�
//		String train_path = "D:\\eclipseWorkspace\\constructDataset\\train.txt.tmp";
//		String val_path = "D:\\eclipseWorkspace\\constructDataset\\dev.txt.tmp";
//		String test_path = "D:\\eclipseWorkspace\\constructDataset\\test.txt.tmp";
		String train_path = "D:\\Documents\\Program\\Eclipse\\Dataset\\extractionV3based\\constructed\\hadoop\\codeTokensTrain.txt";
		String val_path = "D:\\Documents\\Program\\Eclipse\\Dataset\\extractionV3based\\constructed\\hadoop\\codeTokensDev.txt";
		String test_path = "D:\\Documents\\Program\\Eclipse\\Dataset\\extractionV3based\\constructed\\hadoop\\codeTokensTest.txt";
		preCodeTokenCount = 0;
		try {
			FileOutputStream fosTrain = new FileOutputStream(train_path,true);
			FileOutputStream fosVal = new FileOutputStream(val_path,true);
			FileOutputStream fosTest = new FileOutputStream(test_path,true);
			
			for(int i=0;i<codeTokensCounts.size();i++) {
				int codeTokenCount = codeTokensCounts.get(i);
				if (i<=codeTokensCounts.size()*0.8) {//Ӧ�ø�Ϊ<
					for(int j=preCodeTokenCount;j<preCodeVarCount+codeTokenCount;j++) {
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
//						fosTrain.write((codeVars.get(j)+"\t"+codeVarsFlag.get(j)+"\n").getBytes());
					}
					fosTrain.write("\n".getBytes());
				}
				if (i>codeTokensCounts.size()*0.8 && i<=codeTokensCounts.size()*0.9) {
					for(int j=preCodeTokenCount;j<preCodeVarCount+codeTokenCount;j++) {
//						if(tokenInLog.get(j) == 1 && tokenInCodeVar.get(j)==1) {
//							fosVal.write((codeTokens.get(j)+"\tTRUE\t"+"TRUE\n").getBytes());
//						}else if(tokenInLog.get(j) == 1 && tokenInCodeVar.get(j)==0) {
//							fosVal.write((codeTokens.get(j)+"\tTRUE\t"+"O\n").getBytes());
						if(tokenInLog.get(j) == 1) {
							fosVal.write((codeTokens.get(j)+"\tTRUE\t"+"TRUE\n").getBytes());
						}else if(tokenInLog.get(j) == 0 && tokenInCodeVar.get(j)==1) {
							fosVal.write((codeTokens.get(j)+"\tO\t"+"TRUE\n").getBytes());
						}else if(tokenInLog.get(j) == 0 && tokenInCodeVar.get(j)==0) {
							fosVal.write((codeTokens.get(j)+"\tO\t"+"O\n").getBytes());
						}
//						fosTrain.write((codeVars.get(j)+"\t"+codeVarsFlag.get(j)+"\n").getBytes());
					}
					fosVal.write("\n".getBytes());
				}
				if (i>codeTokensCounts.size()*0.9) {
					for(int j=preCodeTokenCount;j<preCodeVarCount+codeTokenCount;j++) {
//						if(tokenInLog.get(j) == 1 && tokenInCodeVar.get(j)==1) {
//							fosTest.write((codeTokens.get(j)+"\tTRUE\t"+"TRUE\n").getBytes());
//						}else if(tokenInLog.get(j) == 1 && tokenInCodeVar.get(j)==0) {
//							fosTest.write((codeTokens.get(j)+"\tTRUE\t"+"O\n").getBytes());
						if(tokenInLog.get(j) == 1) {
							fosTest.write((codeTokens.get(j)+"\tTRUE\t"+"TRUE\n").getBytes());
						}else if(tokenInLog.get(j) == 0 && tokenInCodeVar.get(j)==1) {
							fosTest.write((codeTokens.get(j)+"\tO\t"+"TRUE\n").getBytes());
						}else if(tokenInLog.get(j) == 0 && tokenInCodeVar.get(j)==0) {
							fosTest.write((codeTokens.get(j)+"\tO\t"+"O\n").getBytes());
						}
//						fosTrain.write((codeVars.get(j)+"\t"+codeVarsFlag.get(j)+"\n").getBytes());
					}
					fosTest.write("\n".getBytes());
				}
				preCodeTokenCount+=codeTokenCount;
			}
			fosTrain.close();
			fosVal.close();
			fosTest.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	public static void readEntryFromFile(String path,List list,String flag) throws IOException {//flag = true,��Stringת��ΪInt��Integer.parserInt(str);
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
