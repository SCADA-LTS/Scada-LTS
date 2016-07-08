/**
	This is a simple class that can load, save, and remove 
	files from the native file system. It is needed by Safari and Opera
	for the dojo.storage.browser.FileStorageProvider, since both of
	these platforms have no native way to talk to the file system
	for file:// URLs. Safari supports LiveConnect, but only for talking
	to an applet, not for generic scripting by JavaScript, so we must
	have an applet.

	@author Brad Neuberg, bkn3@columbia.edu
*/

import java.io.*;
import java.util.*;

public class DojoFileStorageProvider{
	public String load(String filePath) 
			throws IOException, FileNotFoundException{
		StringBuffer results = new StringBuffer();
		BufferedReader reader = new BufferedReader(
					new FileReader(filePath));	
		String line = null;
		while((line = reader.readLine()) != null){
			results.append(line);
		}

		reader.close();

		return results.toString();
	}

	public void save(String filePath, String content) 
			throws IOException, FileNotFoundException{
		PrintWriter writer = new PrintWriter(
					new BufferedWriter(
						new FileWriter(filePath, false)));
		writer.print(content);

		writer.close();
	}

	public void remove(String filePath)
			throws IOException, FileNotFoundException{
		File f = new File(filePath);

		if(f.exists() == false || f.isDirectory()){
			return;
		}

		if(f.exists() && f.isFile()){
			f.delete();
		}
	}
}
