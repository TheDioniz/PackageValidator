package pl.devdioniz;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class EarValidator {

	// test ear file under: https://publib.boulder.ibm.com/bpcsamp/gettingStarted/helloWorld/download.html
	private static String INPUT_ZIP_FILE = "C:\\Users\\Desz\\Downloads\\HelloWorldApp.ear";
	private static String OUTPUT_DIR = "H:\\temp\\HelloWorldApp";

	private final static byte[] buffer = new byte[1024];

	public static void main(String[] args) {

		double start = System.currentTimeMillis();
		
		String[] appNameTab = INPUT_ZIP_FILE.split(Pattern.quote("\\"));
		System.out.println(retrieveApplicationName(appNameTab));

		//unzipIt(INPUT_ZIP_FILE, OUTPUT_DIR, true);

		double end = System.currentTimeMillis();

		System.out.println("Time taken: " + ((end - start) / 60) + " seconds.");

	}
	
	private static String retrieveApplicationName(String[] splittedZipPath) {
		return splittedZipPath[splittedZipPath.length - 1].substring(0, splittedZipPath[splittedZipPath.length - 1].length() - 4);
	}

	private static void unzipIt(final String inputZip, final String outputDir, boolean recursively) {

		// make sure input zip exists
		File input = new File(inputZip);
		if (!input.exists()) {
			System.out.println("Input zip does not exists.");
			System.exit(1);
		}

		// make sure output folder exists
		File output = new File(outputDir);
		if (!output.exists()) {
			output.mkdirs();
		}

		// initialize input and output of the program
		try (ZipInputStream in = new ZipInputStream(new FileInputStream(input))) {

			ZipEntry entry;

			// iterate over zip entries
			while ((entry = in.getNextEntry()) != null) {

				// get the name
				String fileName = entry.getName();

				// create an file object
				File file = new File(output + File.separator + fileName);

				// create parent directories to avoid FileNotFoundException
				new File(file.getParent()).mkdirs();

				System.out.println(file);

				if (entry.isDirectory()) {
					continue;
				}

				// save the content of the input file in output file
				try (FileOutputStream out = new FileOutputStream(file)) {
					int len;
					while ((len = in.read(buffer)) > 0) {
						out.write(buffer, 0, len);
					}
				}

				if (recursively) {

					// if another .war file, unpack again and delete zipped .war
					if (file.getName().matches(".*\\.war$")) {

						unzipIt(file.getAbsolutePath(), file.getAbsolutePath() + "_unzipped", true);

						Files.delete(file.toPath());
					}
				}
			}

		} catch (FileNotFoundException e) {
			System.out.println("Cannot find file.");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Cannot perform IO operation.");
			e.printStackTrace();
		}

		// open destination output directory in Windows explorer
		/*
		 * 
		 * Runtime.getRuntime().exec("explorer.exe /select," + OUTPUT_DIR); }
		 * catch (IOException e) { e.printStackTrace(); }
		 */
	}

}
