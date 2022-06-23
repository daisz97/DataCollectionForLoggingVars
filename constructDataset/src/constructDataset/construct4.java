package constructDataset;

/**
 * 构建小数据集，用来做overfit
 * @author DSZ
 *
 */
import java.util.*;
import java.io.*;

public class construct4 {
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

		String train_path = "D:\\eclipseWorkspace\\constructDataset\\smallDataset.txt.tmp";

		preCodeVarCount = 0;
		try {
			FileOutputStream fosTrain = new FileOutputStream(train_path,true);
			for(int i=0;i<codeVarCounts.size();i++) {
				int codeVarCount = codeVarCounts.get(i);
				if (i<=codeVarCounts.size()*0.01) {
					for(int j=preCodeVarCount;j<preCodeVarCount+codeVarCount;j++) {
						if(codeVarsFlag.get(j) == 1) {
							fosTrain.write((codeVars.get(j)+"\t"+"TRUE\n").getBytes());
						}else {
							fosTrain.write((codeVars.get(j)+"\t"+"O\n").getBytes());
						}
//						fosTrain.write((codeVars.get(j)+"\t"+codeVarsFlag.get(j)+"\n").getBytes());
					}
					fosTrain.write("\n".getBytes());
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
	
}
