package br.org.scadabr.db.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.serotonin.mango.Common;

public class ConfigurationDB {

public static void copy(File src, File dst) throws IOException {
    InputStream in = new FileInputStream(src);
    try {
        OutputStream out = new FileOutputStream(dst);
        try {
            // Transfer bytes from in to out
            byte[] buf = new byte[2048];  // Increased buffer size for performance
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } finally {
            out.close();  // Ensures out stream closes even if write fails
        }
    } finally {
        in.close();  // Ensures in stream closes even if output stream creation fails
    }
}


	public static void useDerbyDB() {

		String fileSeparator = System.getProperty("file.separator");

		String path = Common.ctx.getServletContext().getRealPath("");

		boolean win = false;

		File envFile = null;

		if (fileSeparator.equals("\\")) {
			path = path + "\\" + "WEB-INF" + "\\" + "classes" + "\\";
			envFile = new File(path + "\\" + "env.properties");
			win = true;
		}

		if (fileSeparator.equals("/")) {
			path = path + "/" + "WEB-INF" + "/" + "classes" + "/";
			envFile = new File(path + "/" + "env.properties");
		}

		System.out.println("envFile exists: " + envFile.exists());

		if (envFile.exists()) {

			if (win) {
				File derbyFile = new File(path + "\\" + "env.properties.derby");

				if (derbyFile.exists()) {
					try {
						ConfigurationDB.copy(derbyFile, envFile);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			} else {
				File derbyFile = new File(path + "/" + "env.properties.derby");

				if (derbyFile.exists()) {
					try {
						ConfigurationDB.copy(derbyFile, envFile);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

		}

	}

	public static void useMysqlDB() {

		String fileSeparator = System.getProperty("file.separator");

		String path = Common.ctx.getServletContext().getRealPath("");

		boolean win = false;

		File envFile = null;

		if (fileSeparator.equals("\\")) {
			path = path + "\\" + "WEB-INF" + "\\" + "classes" + "\\";
			envFile = new File(path + "\\" + "env.properties");
			win = true;
		}

		if (fileSeparator.equals("/")) {
			path = path + "/" + "WEB-INF" + "/" + "classes" + "/";
			envFile = new File(path + "/" + "env.properties");
		}

		if (envFile.exists()) {

			if (win) {
				File mysqlFile = new File(path + "\\" + "env.properties.mysql");

				if (mysqlFile.exists()) {
					try {
						ConfigurationDB.copy(mysqlFile, envFile);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			} else {
				File mysqlFile = new File(path + "/" + "env.properties.mysql");

				if (mysqlFile.exists()) {
					try {
						ConfigurationDB.copy(mysqlFile, envFile);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

		}
	}

	public static void useMssqlDB() {

	}

	public static void useOracle11gDB() {

		String fileSeparator = System.getProperty("file.separator");

		String path = Common.ctx.getServletContext().getRealPath("");

		path = path + fileSeparator + "WEB-INF" + fileSeparator + "classes"
				+ fileSeparator;
		File envFile = new File(path + fileSeparator + "env.properties");

		// TODO VANIA
		System.out.println("envFile: " + path + fileSeparator
				+ "env.properties");

		if (envFile.exists()) {
			File oracleFile = new File(path + fileSeparator
					+ "env.properties.oracle11g");
			// TODO VANIA
			System.out.println("oracleFile: " + path + fileSeparator
					+ "env.properties.oracle11g");

			if (oracleFile.exists()) {
				try {
					ConfigurationDB.copy(oracleFile, envFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
