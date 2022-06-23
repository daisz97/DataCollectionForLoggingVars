package constructDataset;
/**
 * 构造展示的数据：code @@var from code @@log @@var from log
 */
import java.util.*;
import java.io.*;

public class construct2 {
	protected static List<String> logVars = new ArrayList<String>();
	protected static List<String> codeVars = new ArrayList<String>();
	protected static List<Integer> logVarCounts = new ArrayList<Integer>();
	protected static List<Integer> codeVarCounts = new ArrayList<Integer>();
	protected static List<Integer> codeVarsFlag = new ArrayList<Integer>();
	protected static List<String> codeData = new ArrayList<String>();
	protected static List<String> logStatement = new ArrayList<String>();
	
	public static void main(String args[]) throws IOException {
//		readEntryFromFile("/home/daisz/extraction/logVar.txt",logVars,"false");
//		readEntryFromFile("/home/daisz/extraction/codeVar.txt",codeVars,"false");
//		readEntryFromFile("/home/daisz/extraction/logVarCount.txt",logVarCounts,"true");
//		readEntryFromFile("/home/daisz/extraction/codeVarCount.txt",codeVarCounts,"true");
		readEntryFromFile("D:\\eclipseWorkspace\\extraction\\logVar.txt",logVars,"false");
		readEntryFromFile("D:\\eclipseWorkspace\\extraction\\codeVar.txt",codeVars,"false");
		readEntryFromFile("D:\\eclipseWorkspace\\extraction\\logVarCount.txt",logVarCounts,"true");
		readEntryFromFile("D:\\eclipseWorkspace\\extraction\\codeVarCount.txt",codeVarCounts,"true");
		readEntryFromFile("D:\\eclipseWorkspace\\extraction\\codeData.txt",codeData,"false");
		readEntryFromFile("D:\\eclipseWorkspace\\extraction\\logStatement.txt",logStatement,"false");
//		for(int i =0;i<10;i++) {
//			System.out.println(logVars.get(i)+"\t"+logVars.get(i).getClass().getSimpleName()+"\t"+codeVarCounts.get(i)+"\t"+codeVarCounts.get(i).getClass().getSimpleName());
//		}
		int preLogVarCount = 0;
		int preCodeVarCount = 0;
		String path = "D:\\eclipseWorkspace\\constructDataset\\showndataset.txt";
		for(int i = 0; i < logVarCounts.size(); i++) {
			System.out.println("log "+i);
			int logVarCount = logVarCounts.get(i);
			int codeVarCount = codeVarCounts.get(i);
			List<String> subLogVars = logVars.subList(preLogVarCount,preLogVarCount+logVarCount);
			List<String> subCodeVars = codeVars.subList(preCodeVarCount, preCodeVarCount+codeVarCount);
			
			
			try {
				FileOutputStream fos = new FileOutputStream(path,true);
				StringBuilder logVarString = new StringBuilder();
				StringBuilder codeVarString = new StringBuilder();
				for(String str:subLogVars) {
					logVarString.append(str + " ");
				}
				for(String str:subCodeVars) {
					codeVarString.append(str + " ");
				}
				
				fos.write(("code: "+codeData.get(i)+"\t@@var from code snippet: "+codeVarString+"\t@@log: "+logStatement.get(i)+"\t@@var from log statement: "+logVarString+"\n").getBytes());
				
				fos.close();
				logVarString =null;
				codeVarString = null;
			}catch (Exception e) {
				e.printStackTrace();
			}
			
			preLogVarCount += logVarCount;
			preCodeVarCount += codeVarCount;
			
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
			FileOutputStream fos = new FileOutputStream(path);
			for(int i = 0; i<var.size(); i++) {
				fos.write((var.get(i)+"\t"+token.get(i)+"\n").getBytes());
			}
			fos.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void writeShownToFile(String path, List<String> var, List<Integer> token) {
		try {
			FileOutputStream fos = new FileOutputStream(path);
			for(int i = 0; i<var.size(); i++) {
				fos.write((var.get(i)+"\t"+token.get(i)+"\n").getBytes());
			}
			fos.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
