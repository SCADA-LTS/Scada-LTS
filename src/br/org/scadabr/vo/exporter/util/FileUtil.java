package br.org.scadabr.vo.exporter.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileUtil {
	private static final int BUFFER_SIZE = 2048;
	private static final String FILE_SEPARATOR = System
			.getProperty("file.separator");

	public static void compactFiles(OutputStream os, FileToPack[] files) {
		int cont;
		byte[] data = new byte[BUFFER_SIZE];
		BufferedInputStream origin = null;
		FileInputStream inputStream = null;
		ZipOutputStream output = null;
		ZipEntry entry = null;
		try {
			output = new ZipOutputStream(new BufferedOutputStream(os));
			for (FileToPack file : files) {
				if (file.getFile().isFile()) {
					inputStream = new FileInputStream(file.getFile());
					origin = new BufferedInputStream(inputStream, BUFFER_SIZE);
					entry = new ZipEntry(file.getFileNameWhenPacked());
					try {
						output.putNextEntry(entry);
					} catch (Exception e) {
						continue;
					}
					while ((cont = origin.read(data, 0, BUFFER_SIZE)) != -1) {
						output.write(data, 0, cont);
					}
					origin.close();
				}
			}
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static File createTxtTempFile(String text) {
		File file = new File("temp");
		try {
			file = File.createTempFile("temp", "");
			try (FileOutputStream fi = new FileOutputStream(file)) {
				fi.write(text.getBytes());
				fi.flush();
			}
			file.deleteOnExit();
			return file;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}

	public static List<File> getFilesOnDirectory(Path directoryName) {
		List<File> files = new ArrayList<>();
		try {
			File directory = directoryName.toFile();
			if (directory.exists()) {

				if (directory.isDirectory()) {
					String[] filesOnDirectory = directory.list();

					if(filesOnDirectory != null) {
						for (String fileName : filesOnDirectory) {
							files.addAll(getFilesOnDirectory(Paths.get(directory
									.getAbsolutePath() + FILE_SEPARATOR + fileName)));
						}
					}
				} else if (directory.isFile()) {
					files.add(directory);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return files;
	}
}
