package br.org.scadabr.vo.exporter.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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

	public static File createTxtTempFile(String nome, String text) {
		File f = new File(nome);
		try {
			File.createTempFile("temp", "");
			FileOutputStream fi = new FileOutputStream(f);
			fi.write(text.getBytes());
			fi.flush();
			fi.close();
			f.deleteOnExit();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return f;
	}

	public static List<File> getFilesOnDirectory(String directoryName) {
		List<File> files = new ArrayList<File>();
		try {
			File directory = new File(directoryName);
			if (directory.exists()) {

				if (directory.isDirectory()) {
					String[] filesOnDirectory = directory.list();

					for (String fileName : filesOnDirectory) {
						files.addAll(getFilesOnDirectory(directory
								.getAbsolutePath() + FILE_SEPARATOR + fileName));
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
