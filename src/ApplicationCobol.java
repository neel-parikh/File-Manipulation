import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;


public class ApplicationCobol {

	private static final String oldApplicationName = "KHQ";
	private static final String newApplicationName = "GCV";
	
	private static final String folderName = "cobol_and_copy_test";
	
	private static final Map<String,String> fileNamesList = new HashMap<String,String>();
	private static final Map<String,String> dataItems = new HashMap<String,String>();
	
	static File fileNames = new File("CobolFileList.txt");
	static File dataItemsFile = new File("DataItems.txt");
	
	public static void main(String[] args) throws IOException 
	{
		System.out.println("-------------- Execution Started for COBOL and COPY BOOK files ----------------- ");
		
		//resetFilesMain();
		
		System.out.print("1. Initializing files >>>>>>> In Progress");
		resetFilesTest();
		System.out.println("  -------------> Completed");
		
		System.out.print("2. Removing Comments from the file >>>>>>> In Progress");
		removeComments();
		System.out.println("  -------------> Completed");
		
		System.out.print("3. Renaming of File Names and File Names used in other files >>>>>>> In Progress");
		renameFiles();
		System.out.println("  -------------> Completed");
		
		System.out.print("4. Renaming Missing file names >>>>>>> In Progress");
		renameMissingFiles();
		System.out.println("  -------------> Completed");

		System.out.print("5. Renaming Database Names >>>>>>> In Progress");
		renameDatabase();
		System.out.println("  -------------> Completed");
		
		System.out.print("6. Renaming All Strings >>>>>>> In Progress");
		findAllStrings();
		System.out.println("  -------------> Completed");
		
		System.out.print("7. Renaming Paragraph Names >>>>>>> In Progress");
		changeParagraphNames();
		System.out.println("  -------------> Completed");
		
		System.out.print("8. Renaming Data Items and Contents inside the files >>>>>>> In Progress");
		renameDataItems();
		System.out.println("  -------------> Completed");
		
		System.out.print("9. Renaming All KHQ to GCV >>>>>>> In Progress");
		replaceAllContent(oldApplicationName+".TMP", newApplicationName + ".TMP");
		System.out.println("  -------------> Completed");
		
		//ApplicationJCL.main(args);
		//System.out.println(dataItems.toString());
	}

	
	// RENAME ALL THE DATA ITEMS IN THE FILES
	private static void renameDataItems() throws IOException
	{
		File folder = new File(folderName);
		File[] list = folder.listFiles();
		Scanner readFile;
		String currLine;
		FileWriter dataItemsWrite = new FileWriter(dataItemsFile);
		
		for(File file : list)
		{
			if(!file.isDirectory())
			{
				File tempFileName = new File(folderName+"/temp.txt");
				FileWriter tempFile = new FileWriter(tempFileName);
				
				readFile = new Scanner(file);
				//System.out.println(file.getName());
				while(readFile.hasNextLine()) 
				{
					currLine = readFile.nextLine();
					if(currLine.contains("DATA DIVISION.") && !file.getName().endsWith("CPY"))
					{
						tempFile.write(currLine+"\n");
						currLine = readFile.nextLine();
						while(!currLine.contains("PROCEDURE DIVISION."))
						{
							//System.out.println(currLine);
							if(currLine.length()>8)
							{
								String currLineSubStr = currLine.substring(7).trim();
								currLineSubStr = currLineSubStr.replaceAll("\\s+", " ");
								
								if(currLineSubStr.length()>3 && currLineSubStr.contains(" ") && currLineSubStr.charAt(0)>=48 && currLineSubStr.charAt(0)<=57 && currLineSubStr.charAt(1)>=48 && currLineSubStr.charAt(1)<=57 && !currLineSubStr.contains("FILLER"))
								{
									String splitItems[] = currLineSubStr.split(" ");
									String items = splitItems[1].toUpperCase();
									String randomString;
									
									if(items.endsWith("."))
										items=items.substring(0,items.length()-1);
									
									if(!dataItems.containsKey(items.trim()))
									{	
										if(!fileNamesList.containsValue(items.trim()) && !items.equals("PIC") && items.length()>2 && !items.equals("POS"))
											randomString = generateRandomString(items);
										else
											randomString = items;
										
										dataItems.put(items, randomString);
										dataItemsWrite.write(items+","+randomString+"\n");
									}
									else
									{
										randomString = dataItems.get(items);
									}
									//System.out.println(file.getName() + ">>>>>"+currLineSubStr + ">>>>"+items+">>>>>"+randomString);
									if(currLine.contains(items))
										currLine = currLine.replaceFirst(items, randomString);
									
									tempFile.write(currLine+"\n");
								}
								else
								{
									tempFile.write(currLine+"\n");
								}
								
							}
							else
							{
								tempFile.write(currLine+"\n");
							}
							currLine = readFile.nextLine();
						}
						tempFile.write(currLine+"\n");
					}
					else if(file.getName().endsWith("CPY"))
					{
						if(currLine.length()>8)
						{
							String currLineSubStr = currLine.substring(7).trim();
							currLineSubStr = currLineSubStr.replaceAll("\\s+", " ");
							
							if(currLineSubStr.length()>3 && currLineSubStr.contains(" ") && currLineSubStr.charAt(0)>=48 && currLineSubStr.charAt(0)<=57 && currLineSubStr.charAt(1)>=48 && currLineSubStr.charAt(1)<=57 && !currLineSubStr.contains("FILLER"))
							{
								String splitItems[] = currLineSubStr.split(" ");
								String items = splitItems[1].toUpperCase();
								String randomString;
								
								if(items.endsWith("."))
									items=items.substring(0,items.length()-1);
								
								if(!dataItems.containsKey(items.trim()))
								{
									
									if(!fileNamesList.containsValue(items.trim()) && !items.equals("PIC") && items.length()>2)
										randomString = generateRandomString(items);
									else
										randomString = items;
									
									dataItems.put(items, randomString);
									dataItemsWrite.write(items+","+randomString+"\n");
								}
								else
								{
									randomString = dataItems.get(items);
								}
								if(currLine.contains(items))
									currLine = currLine.replaceFirst(items, randomString);
								//System.out.println(file.getName() + " ----------- " + items + "  >>>>>   "+randomString);
								tempFile.write(currLine+"\n");
							}
							else
							{
								tempFile.write(currLine+"\n");
							}
							
						}
						else
						{
							tempFile.write(currLine+"\n");
						}
					}
					else
					{
						tempFile.write(currLine+"\n");
						//System.out.println(currLine);
					}
				}
			readFile.close();
			tempFile.close();
			file.delete();
			tempFileName.renameTo(file);
			}
		}
		dataItemsWrite.close();
		//System.out.println(dataItems.toString());
		Map<String, String> treeMap = new TreeMap<String, String>(
			    new Comparator<String>() {
			        @Override
			        public int compare(String s1, String s2) {
			            if (s1.length() > s2.length()) {
			                return -1;
			            } else if (s1.length() < s2.length()) {
			                return 1;
			            } else {
			                return s1.compareTo(s2);
			            }
			        }
			});
		treeMap.putAll(dataItems);
		//System.out.println(treeMap.toString());
		updateDataItemsContent(treeMap);
	}

	// UPDATE DATA ITEMS IN THE CONTENTS OF ALL THE FILES
	private static void updateDataItemsContent(Map<String,String> treeMap) throws IOException
	{
		
		File folder = new File(folderName);
		File[] list = folder.listFiles();
		
		for(File file : list)
		{
			if(!file.isDirectory())
			{
				String content = FileUtils.readFileToString(file,"UTF-8");
				for(Map.Entry<String, String> entry : treeMap.entrySet())
				{
					String key = entry.getKey().trim();
					if(!fileNamesList.containsValue(key) && key.length()>2 && content.contains(key))
					{
						//key = checkLongerKey(key);
						String value = treeMap.get(key);
						//System.out.println(key + " >>>> " + value);
						content = content.replaceAll("\\b"+key+"\\b",value);
						if(content.contains(key.toLowerCase()))
							content = content.replaceAll("\\b"+key.toLowerCase()+"\\b",value);
					}
				}
				FileUtils.writeStringToFile(file, content,"UTF-8");
				//System.out.println("DONE for : " + file.getName());
			}
		}
	}

	
	// CHECK THE LONGER KEY TO REPLACE BEFORE THE SUBSTRING SMALLER KEY
	private static String checkLongerKey(String smallKey) 
	{
		for(Map.Entry<String, String> entry : dataItems.entrySet())
		{
			String key = entry.getKey().trim();
			if(key.startsWith(smallKey) && key.length()>smallKey.length())
			{
				smallKey=key;
				break;
			}
		}
		return smallKey;
	}

	// CHANGE PARAGRAPH NAMES WITH SOME RANDOM CHARACTERS AND STORE IN HASHMAP TO UPDATE CORRESPONDING 'PERFORM'
	private static void changeParagraphNames() throws IOException
	{
		File folder = new File(folderName);
		File[] list = folder.listFiles();
		Scanner readFile;
		String currLine;
		Map<String,String> performMap;
		
		//Pattern p = Pattern.compile("([A-Z]+([A-Z0-9][-|\\s]?)+).");
		Pattern p = Pattern.compile("(([A-Z]+[A-Z0-9][-\\s]*)+).");
		
		for(File file : list)
		{
			if(!file.isDirectory())
			{
				File tempFileName = new File(folderName+"/temp.txt");
				FileWriter tempFile = new FileWriter(tempFileName);
				
				readFile = new Scanner(file);
				performMap = new HashMap<String,String>();
				while(readFile.hasNextLine()) 
				{
					currLine = readFile.nextLine();
					if(currLine.length() > 8 && currLine.charAt(7)>=65 && currLine.charAt(7)<=90 && !currLine.contains("FD  ")) 
					{
						Matcher m = p.matcher(currLine);
						if (m.find() && m.group(1).length()>1)
						{
							if(!StringUtils.containsAny(m.group(1), new String[] {"LINKAGE SECTION","ENTREE SECTION","IDENTIFICATION","PROGRAM-ID","ENVIRONMENT DIVISION","CONFIGURATION SECTION","SOURCE-COMPUTER","OBJECT-COMPUTER","SPECIAL-NAMES","INPUT-OUTPUT","FILE-CONTROL","DATA DIVISION","FILE SECTION","FD ","WORKING-STORAGE","PROCEDURE DIVISION","OBJECT-COMPUTEQ"})) 
							{
								if(!performMap.containsKey(m.group(1)))
								{
									String randomString = generateRandomString(m.group(1));
									
									if(currLine.contains(m.group(1)))
										currLine = currLine.replace(m.group(1), randomString);
									
									performMap.put(m.group(1), randomString);
									//System.out.println(m.group(1) + ">>>>>"+ randomString);
								}
								else
								{
									if(currLine.contains(m.group(1)))
										currLine = currLine.replace(m.group(1), performMap.get(m.group(1)));
								}
							}
						}
						tempFile.write(currLine+"\n");
					}
					else
					{
						tempFile.write(currLine+"\n");
					}
				}
				readFile.close();
				tempFile.close();
				file.delete();
				tempFileName.renameTo(file);
				updateParagraphContents(file,performMap);
			}
		}
	}


	// UPDATE PARAGRAPH CALLS FROM 'PERFORM' & 'GO TO'
	private static void updateParagraphContents(File file, Map<String, String> performMap) throws IOException
	{	
		for(Map.Entry<String, String> entry : performMap.entrySet())
		{
			//System.out.println("File name : "+ file.getName() + " >>>>>>>> " +entry.getKey() + "     >     "+entry.getValue());
			String key = entry.getKey();
			String value = entry.getValue();
			String content = FileUtils.readFileToString(file,"UTF-8");
			
			if(content.contains("PERFORM "+key)) 
				content = content.replaceAll("PERFORM "+key,"PERFORM "+value);
			if(content.contains("GO TO "+key))
			content = content.replaceAll("GO TO "+key,"GO TO "+value);
			
			//For all others
			if(key.trim().length()>2 && content.contains("-"+key))
				content = content.replaceAll("-"+key,"-"+value);
			
			FileUtils.writeStringToFile(file, content,"UTF-8");
		}
	}


	// FIND STRINGS BETWEEN "" and ' ' AND REPLACE WITH RANDOM CHARACTERS WITH SOME EXCEPTIONS
	private static void findAllStrings() throws IOException
	{
		File folder = new File(folderName);
		File[] list = folder.listFiles();
		Scanner readFile;
		
		Pattern patternWithoutMoveTo = Pattern.compile("[\"|\']([^\"]*)[\"|\']?");
		//Pattern patternWithMoveTo = Pattern.compile("[\"|\']([^\"]*)?[\"|\']");
		
		Pattern patternWithMoveTo = Pattern.compile("[\'|\"]([^\'^\"]*)[\'|\"]");
		Pattern basicPattern = Pattern.compile("\"([^\"]*)\"");
		String currLine;
		
		for(File file : list)
		{
			if(!file.isDirectory())
			{
				File tempFileName = new File(folderName+"/temp.txt");
				FileWriter tempFile = new FileWriter(tempFileName);
				
				readFile = new Scanner(file);
				while(readFile.hasNextLine()) 
				{
					currLine = readFile.nextLine();
					if(currLine.trim().contains("STRING"))
					{
						tempFile.write(currLine+"\n");
						currLine = readFile.nextLine();
						if(currLine.trim().contains("SELECT") || currLine.trim().contains("INSERT ") || currLine.trim().contains("UPDATE ") || currLine.trim().contains("DELETE ") || currLine.trim().contains("OUTFIL INCLUDE"))
						{
							tempFile.write(currLine+"\n");
							while(!currLine.trim().contains("END-STRING"))
							{
								currLine = readFile.nextLine();
								tempFile.write(currLine+"\n");
							}
						}
						else
						{
							Matcher m = patternWithoutMoveTo.matcher(currLine);
							Matcher m2 = patternWithMoveTo.matcher(currLine);
							Matcher base = basicPattern.matcher(currLine);
							//System.out.println(currLine + "  " + base.find() + "  " + m2.groupCount() + "  "+m.groupCount());
							if(base.find())
							{
								base.reset();
								while(base.find())
								{
										if(base.group(1).trim().length()>2 && currLine.contains(base.group(1)) && !base.group(1).trim().equals("YYYYMMDD") && !base.group(1).trim().equals(oldApplicationName) && !fileNamesList.containsValue(base.group(1).trim()))
										{
											String randomString = generateRandomString(base.group(1));
											currLine = currLine.replace(base.group(1), randomString);
										}
								}
							}
							else if(m2.find())
							{
								m2.reset();
								while(m2.find())
								{
										if(m2.group(1).trim().length()>2 && currLine.contains(m2.group(1)) && !m2.group(1).trim().equals("YYYYMMDD") && !m2.group(1).trim().equals(oldApplicationName) && !fileNamesList.containsValue(m2.group(1).trim()))
										{
											String randomString = generateRandomString(m2.group(1));
											currLine = currLine.replace(m2.group(1), randomString);
										}
								//System.out.println(file.getName() + " >>> " + m2.group(1) + " --------- " + randomString);
								}
							}
							else if(m.find())
							{	
								m.reset();
								while(m.find())
								{
										if(m.group(1).trim().length()>2 && currLine.contains(m.group(1)) && !m.group(1).trim().equals("YYYYMMDD") && !m.group(1).trim().equals(oldApplicationName) && !fileNamesList.containsValue(m.group(1).trim()))
										{
											String randomString = generateRandomString(m.group(1));
											currLine = currLine.replace(m.group(1), randomString);
										}
								//System.out.println(file.getName() + " >>> " + m.group(1) + " --------- " + randomString);
								}
							}
							tempFile.write(currLine+"\n");
						}
					}
					else if(currLine.trim().contains("CALL ") || currLine.trim().equals(oldApplicationName) || currLine.trim().contains("MAPSET"))
					{
						tempFile.write(currLine+"\n");
					}
					else if(currLine.trim().length()>2)
					{
						Matcher m = patternWithoutMoveTo.matcher(currLine);
						Matcher m2 = patternWithMoveTo.matcher(currLine);
						Matcher base = basicPattern.matcher(currLine);
						//System.out.println(currLine + "  " + base.find() + "  " + m2.groupCount() + "  "+m.groupCount());
						if(base.find())
						{
							base.reset();
							while(base.find())
							{
									if(base.group(1).trim().length()>2 && currLine.contains(base.group(1)) && !base.group(1).trim().equals("YYYYMMDD") && !base.group(1).trim().equals(oldApplicationName) && !fileNamesList.containsValue(base.group(1).trim()))
									{
										String randomString = generateRandomString(base.group(1));
										currLine = currLine.replace(base.group(1), randomString);
									}
							}
						}
						else if(m2.find())
						{
							m2.reset();
							while(m2.find())
							{
									if(m2.group(1).trim().length()>2 && currLine.contains(m2.group(1)) && !m2.group(1).trim().equals("YYYYMMDD") && !m2.group(1).trim().equals(oldApplicationName) && !fileNamesList.containsValue(m2.group(1).trim()))
									{
										String randomString = generateRandomString(m2.group(1));
										currLine = currLine.replace(m2.group(1), randomString);
									}
							//System.out.println(file.getName() + " >>> " + m2.group(1) + " --------- " + randomString);
							}
						}
						else if(m.find())
						{	
							m.reset();
							while(m.find())
							{
									if(m.group(1).trim().length()>2 && currLine.contains(m.group(1)) && !m.group(1).trim().equals("YYYYMMDD") && !m.group(1).trim().equals(oldApplicationName) && !fileNamesList.containsValue(m.group(1).trim()))
									{
										String randomString = generateRandomString(m.group(1));
										currLine = currLine.replace(m.group(1), randomString);
									}
							//System.out.println(file.getName() + " >>> " + m.group(1) + " --------- " + randomString);
							}
						}
						tempFile.write(currLine+"\n");
					}
				}
				readFile.close();
				tempFile.close();
				file.delete();
				tempFileName.renameTo(file);
			}
		}
	}

	// GENERATING RANDOM STRING
	private static String generateRandomString(String str) 
	{
		Random rn = new Random();
		String newStr = "";
		int start=0;
		if(str.startsWith("W-")) {
			start=str.indexOf("-");
			newStr+=str.substring(0,start);
		}
		else if(str.startsWith(":W:-")) {
			start=str.indexOf("-");
			newStr+=str.substring(0,start);
		}
		
		//Generate Random Numbers
		for(int i=start;i<str.length();i++)
		{
			if(str.charAt(i)>=48 && str.charAt(i)<=57)
			{
				newStr+=(char)(rn.nextInt(10)+48);
			}
			else if(str.charAt(i)>=97 && str.charAt(i)<=122)
			{
				newStr+=(char)(rn.nextInt(26)+97);
			}
			else if(str.charAt(i)>=65 && str.charAt(i)<=90)
			{
				newStr+=(char)(rn.nextInt(26)+65);
			}
			else
			{
				newStr+=str.charAt(i);
			}
			
		}
		return newStr;
	}

	//RENAMING CONTENT FOR THE MISSING DB NAMES FROM "missing_db.txt"
	private static void renameDatabase() throws IOException
	{
		FileWriter fw = new FileWriter(fileNames,true);
		File file = new File("missing_db.txt");
		Scanner sc = new Scanner(file);
		String newName=null;
		while(sc.hasNextLine())
		{
			String currName = sc.next().trim();
			if(currName.startsWith("\""))
			{
				newName = currName.replace("RES", newApplicationName);
			}
			else
			{
				newName = newApplicationName + currName.substring(newApplicationName.length());
			}
			//System.out.println(currName + ">>>>>" + newName);
			if(!fileNamesList.containsValue(newName))
			{
				fileNamesList.put(currName,newName);
				fw.write(currName+","+newName+"\n");
			}
			replaceAllContent(currName+".DBA", newName+".DBA");
		}
		fw.close();
		sc.close();
	}


	//RENAMING FILE NAMES CONTENT FOR THE MISSING FILES FROM "missing.txt"
	private static void renameMissingFiles() throws IOException 
	{
		FileWriter fw = new FileWriter(fileNames,true);
		File file = new File("missing.txt");
		Scanner sc = new Scanner(file);
		String newName=null;
		while(sc.hasNextLine())
		{
			String currName = sc.next().trim();
			if(currName.startsWith("SQL"))
			{
				if(currName.length() == 5)
					newName = "SQL"+"TX";
				else if(currName.length() == 6)
					newName = "SQL"+"TXD";
			}
			else if(currName.startsWith("S-"+oldApplicationName))
			{
				newName = "S-"+ newApplicationName +currName.substring(newApplicationName.length()+2);
			}
			else if(currName.startsWith("ANO"))
			{
				newName = "EPI"+currName.substring(newApplicationName.length());
			}
			else if(currName.startsWith("CMC"))
			{
				newName = "BCT"+currName.substring(newApplicationName.length());
			}
			else if(currName.startsWith("PRB"))
			{
				newName = "ERT"+currName.substring(newApplicationName.length());
			}
			else if(currName.startsWith("DA-I"))
			{
				newName = "GH-T"+currName.substring(newApplicationName.length()+1);
			}
			else if(currName.startsWith("MOD"))
			{
				newName = "RDC"+currName.substring(newApplicationName.length());
			}
			else if(currName.endsWith(oldApplicationName))
			{
				newName = currName.substring(0,currName.length()-2) + newApplicationName;
			}
			else if(currName.startsWith("SPACE") || currName.startsWith("ENTRE"))
			{
				newName = currName;
			}
			else
			{
				newName = newApplicationName + currName.substring(newApplicationName.length());
			}
			if(!fileNamesList.containsValue(newName))
			{
				fileNamesList.put(currName,newName);
				fw.write(currName+","+newName+"\n");
			}
			//System.out.println(currName + "  >>>>> " +newName);
			replaceAllContent(currName+".TMP", newName+".TMP");
		}
		fw.close();
		sc.close();
	}


	// RENAME FILE NAMES USED IN OTHER FILES --- NEW
	private static void replaceAllContent(String fileNameToSearch, String newFileName) throws IOException 
	{
		File folder = new File(folderName);
		File[] list = folder.listFiles();

		String checkName = fileNameToSearch.substring(0,fileNameToSearch.length()-4);
		newFileName = newFileName.substring(0,fileNameToSearch.length()-4);
		
		for(File file : list)
		{
			if(!file.isDirectory())
			{
				String content = FileUtils.readFileToString(file,"UTF-8");
				newFileName = newFileName.substring(0,fileNameToSearch.length()-4);
				if(fileNameToSearch.endsWith("DBA") && content.contains(checkName))
				{
					if(content.contains("FROM "+checkName))
						content = content.replaceAll("FROM "+checkName,"FROM "+newFileName);
					
					if(content.contains("INTO "+checkName))
						content = content.replaceAll("INTO "+checkName,"INTO "+newFileName);
					
					if(content.contains("TABLE "+checkName))
						content = content.replaceAll("TABLE "+checkName,"TABLE "+newFileName);
					
					if(content.contains("UPDATE "+checkName))
						content = content.replaceAll("UPDATE "+checkName,"UPDATE "+newFileName);
					
					if(content.contains("DELETE "+checkName))
						content = content.replaceAll("DELETE "+checkName,"DELETE "+newFileName);
				}
				else if(content.contains(checkName))
				{
					content = content.replaceAll("\\b"+checkName+"\\b",newFileName);
				}
				FileUtils.writeStringToFile(file, content,"UTF-8");
			}
		}
	}

	
	
	//RENAMING FILE NAMES
	private static void renameFiles() throws IOException
	{
		File folder = new File(folderName);
		File[] list = folder.listFiles();
		String newFileName;
		
		for(File file : list)
		{
			if(!file.isDirectory())
			{
				newFileName = newApplicationName + file.getName().substring(newApplicationName.length());
				//searchFileName(file.getName(),newFileName);
				replaceAllContent(file.getName(),newFileName);
			}
		}
		
		FileWriter fw = new FileWriter(fileNames);
		for(File file : list)
		{
			if(!file.isDirectory())
			{
				newFileName = newApplicationName + file.getName().substring(newApplicationName.length());
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
	
	
	
	// RENAME FILE NAMES USED IN OTHER FILES --- OLD
	public static void searchFileName(String fileNameToSearch, String newFileName) throws IOException
	{
		
		File folder = new File(folderName);
		File[] list = folder.listFiles();
		Scanner readFile;
		
		String checkName = fileNameToSearch.substring(0,fileNameToSearch.length()-4);
		newFileName = newFileName.substring(0,fileNameToSearch.length()-4);
		
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
					
					if(currLine.matches("(.*)" + checkName +"(\\s*|\\**|(A-Z)*)" + "(\"|\')" + "(.*)"))
					{
						currLine = currLine.replaceAll(checkName, newFileName);
					}
					
					else if(currLine.contains("PROGRAM-ID. "+ checkName))
					{
						currLine = currLine.replaceAll(checkName, newFileName);
					}
					
					else if(currLine.endsWith(checkName))
					{
						currLine = currLine.replaceAll(checkName, newFileName);
					}
					
					tempFile.write(currLine+"\n");
				}
				readFile.close();
				tempFile.close();
				file.delete();
				tempFileName.renameTo(file);
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
					if(currLine.length() >= 7 && currLine.charAt(0) != '*' && currLine.charAt(6) != '*')
						tempFile.write(currLine+"\n");
				}
				readFile.close();
				tempFile.close();
				file.delete();
				tempFileName.renameTo(file);
			}
		}
	}
	
	
	// INIT STEP TO COPY FILES FROM "bkp_test" to WORKING DIRECTORY (folderName)
	private static void resetFilesTest() throws IOException 
	{
		File oldLoc = new File("bkp_test");
		File newLoc = new File(folderName);
		FileUtils.cleanDirectory(newLoc);
		FileUtils.copyDirectory(oldLoc, newLoc);
	}
	
	private static void resetFilesMain() throws IOException 
	{
		File oldLoc = new File("cobol_and_copy");
		File newLoc = new File("bkp_test");
		FileUtils.cleanDirectory(newLoc);
		FileUtils.copyDirectory(oldLoc, newLoc);
	}
}
