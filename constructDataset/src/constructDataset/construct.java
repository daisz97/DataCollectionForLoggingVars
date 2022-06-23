
package constructDataset;
/**
 * 直接提取所有的var from code snippet without partition code snippet
 */
import java.util.*;
import java.io.*;

public class construct {
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
		readEntryFromFile("D:\\eclipseWorkspace\\extraction\\codeData.txt",codeVarCounts,"false");
//		for(int i =0;i<10;i++) {
//			System.out.println(logVars.get(i)+"\t"+logVars.get(i).getClass().getSimpleName()+"\t"+codeVarCounts.get(i)+"\t"+codeVarCounts.get(i).getClass().getSimpleName());
//		}
		int preLogVarCount = 0;
		int preCodeVarCount = 0;
		for(int i = 0; i < logVarCounts.size(); i++) {
			System.out.println("log "+i);
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
		
		String path = "D:\\eclipseWorkspace\\constructDataset/dataset.txt";
		writeEntryToFile(path,codeVars,codeVarsFlag);
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
}
