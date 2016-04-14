import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;


public class ApplicationJCL {
	
	private static final String folderName = "MVS_test";
	
	private static final String oldApplicationName = "KHQ";
	private static final String newApplicationName = "GCV";
	
	private static final Map<String,String> fileNamesList = new HashMap<String,String>();
	
	static File jclFiles = new File("JCLFileList.txt");
	static File cobolFiles = new File("CobolFileList.txt");
	
	public static void main(String[] args) throws IOException 
	{
		System.out.println("-------------- Execution Started for JCL files ----------------- ");
		
		System.out.print("1. Reset Files >>>>>>> In Progress");
		resetFilesTest();
		System.out.println("  -------------> Completed");
		
		System.out.print("2. Remove Comments >>>>>>> In Progress");
		removeComments();
		System.out.println("  -------------> Completed");
		
		System.out.print("3. Renaming Files >>>>>>> In Progress");
		renameFiles();
		System.out.println("  -------------> Completed");
		
		System.out.print("4. Replace File Names used inside file contents >>>>>>> In Progress");
		replaceAllContents();
		System.out.println("  -------------> Completed");
		
		System.out.print("5. Renaming All KHQ to GCV >>>>>>> In Progress");
		renameApplicationName();
		System.out.println("  -------------> Completed");
	}

	private static void renameApplicationName() throws IOException
	{
		File folder = new File(folderName);
		File[] list = folder.listFiles();
		
		for(File file : list)
		{
			if(!file.isDirectory())
			{
				String content = FileUtils.readFileToString(file,"UTF-8");
				
				if(content.contains(oldApplicationName))
					content = content.replaceAll(oldApplicationName,newApplicationName);
				
				FileUtils.writeStringToFile(file, content,"UTF-8");
			}
		}
		
	}

	//REMOVE COMMENTS FROM ALL THE FILES
	public static void removeComments() throws IOException
	{
		File folder = new File(folderName);
		File[] list = folder.listFiles();

		Scanner readFile;
		
		for(File file : list)
		{
			if(!file.isDirectory())
			{
				File tempFileName = new File(folderName+"/temp.txt");
				FileWriter tempFile = new FileWriter(tempFileName);
	
				readFile = new Scanner(file);
				while(readFile.hasNextLine()) 
				{
					String currLine = readFile.nextLine();
					//System.out.println(file.getName() + " >>>> " + currLine);
					if(currLine.length() > 3 && currLine.charAt(2) != '*')
						tempFile.write(currLine+"\n");
				}
				readFile.close();
				tempFile.close();
				file.delete();
				tempFileName.renameTo(file);
			}
		}
	}
	
	private static void renameFiles() throws IOException
	{
		File folder = new File(folderName);
		File[] list = folder.listFiles();
		String newFileName;
		FileWriter fw = new FileWriter(jclFiles);
		for(File file : list)
		{
			if(!file.isDirectory())
			{
				newFileName = newApplicationName.substring(0,2) + file.getName().substring(2);
				
				file.renameTo(new File(folderName+"/" + newFileName));
				if(!fileNamesList.containsValue(newFileName.substring(0,newFileName.length()-4)))
				{
					fileNamesList.put(file.getName().substring(0,newFileName.length()-4),newFileName.substring(0,newFileName.length()-4));
					fw.write(file.getName().substring(0,newFileName.length()-4)+","+newFileName.substring(0,newFileName.length()-4)+"\n");
					
				}
			}
		}
		fw.close();
	}
	
	private static void replaceAllContents() throws IOException
	{
		File folder = new File(folderName);
		File[] list = folder.listFiles();
		
		for(File file : list)
		{
			if(!file.isDirectory())
			{
				//Checking JCL Files
				renameFileNamesInContent(file,jclFiles);
				
				//Checking COBOL and CPY files
				renameFileNamesInContent(file,cobolFiles);
			}
		}
	}
	
	private static void renameFileNamesInContent(File searchFile,File file) throws IOException
	{
		Scanner sc = new Scanner(file);
		String content = FileUtils.readFileToString(searchFile,"UTF-8");
		while(sc.hasNextLine())
		{
			String names[] = sc.nextLine().split(",");
			if(names.length<2)
				continue;
			String oldFileName = names[0];
			String newFileName = names[1];
			if(content.contains(oldFileName))
				content = content.replaceAll("\\b"+oldFileName+"\\b",newFileName);
		}
		FileUtils.writeStringToFile(searchFile, content,"UTF-8");
		sc.close();
	}

	// INIT STEP TO COPY FILES FROM "MVS" to WORKING DIRECTORY (folderName)
	private static void resetFilesTest() throws IOException 
	{
		File oldLoc = new File("MVS");
		File newLoc = new File(folderName);
		FileUtils.cleanDirectory(newLoc);
		FileUtils.copyDirectory(oldLoc, newLoc);
	}
}
