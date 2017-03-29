package pl.devdioniz;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class EarValidator {

	// test ear file under:
	// https://publib.boulder.ibm.com/bpcsamp/gettingStarted/helloWorld/download.html
	private static String INPUT_ZIP_FILE = "C:\\Users\\Desz\\Downloads\\HelloWorldApp.ear";
	private static String OUTPUT_DIR = "H:\\temp";
	private static String APPROVED_WAS_POLICY = "H:\\temp\\approved.policy";

	private static double expectedAppVersion;

	private final static byte[] buffer = new byte[1024];

	public static void main(String[] args) {

		double start = System.currentTimeMillis();

		if (args.length == 1) {
			try {
				expectedAppVersion = Double.parseDouble(args[0]);
			} catch (NumberFormatException e) {
				System.out.println("Wrong version argument, cannot parse to double value");
				System.exit(1);
			}
		} else {
			expectedAppVersion = 2.0;
		}

		String[] appNameTab = INPUT_ZIP_FILE.split(Pattern.quote("\\"));
		OUTPUT_DIR = OUTPUT_DIR + File.separator + retrieveApplicationName(appNameTab);

		unzipIt(INPUT_ZIP_FILE, OUTPUT_DIR, true);

		Path wasPolicyPath = findFile(OUTPUT_DIR, "application.xml");
		System.out.println("Found was policy in: " + wasPolicyPath.toAbsolutePath());
		
		boolean isPolicyOK = false;
		
		if (wasPolicyPath != null) {
			isPolicyOK = compareFiles(wasPolicyPath, APPROVED_WAS_POLICY);
		}
		
		System.out.println("Policy OK: " + isPolicyOK);

		Path versionXmlPath = findFile(OUTPUT_DIR, "web.xml");
		System.out.println("Found version.xml in: " + versionXmlPath.toAbsolutePath());

		if (versionXmlPath != null) {
			validateAppVersion(versionXmlPath, expectedAppVersion);
		}
		
		double end = System.currentTimeMillis();

		double totalTime = ((end - start) / 60);
		System.out.println("Time taken: " + totalTime + " seconds.");

	}

	private static boolean validateAppVersion(Path versionXmlPath, double expectedAppVersion2) {

		File versionFile = new File(versionXmlPath.toUri());
		try {
			Files.readAllLines(versionXmlPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}

	private static boolean compareFiles(Path wasPolicyPath, String approvedWasPolicy) {

		try {
			BufferedReader notApproved = new BufferedReader(new FileReader(new File(wasPolicyPath.toUri())));
			BufferedReader approved = new BufferedReader(new FileReader(new File(approvedWasPolicy)));

			List<String> approvedFile = new ArrayList<>();
			String line = "";

			while ((line = approved.readLine()) != null) {

				approvedFile.add(line.trim());
			}

			while ((line = notApproved.readLine()) != null) {
				line = line.trim();
				if (!approvedFile.contains(line)) {
					System.out.println("Difference in line:");
					System.out.println(line);
					return false;
				}
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;

	}

	private static Path findFile(String sourceDir, String fileName) {

		Path start = Paths.get(sourceDir);
		int maxDepth = 5;
		Path searchedFilePlace = null;

		try {
			Stream<Path> find = Files.find(start, maxDepth, (p, f) -> {
				String tempFile = p.getFileName().toString();
				return fileName.equals(tempFile);
			});

			// awful way of looking for exact path, TODO refactor it
			Iterator<Path> iterator = find.iterator();
			while (iterator.hasNext()) {

				Path next = iterator.next();
				searchedFilePlace = next;
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return searchedFilePlace;
	}

	private static String retrieveApplicationName(String[] splittedZipPath) {
		int lastArrayIndex = splittedZipPath.length - 1;
		return splittedZipPath[lastArrayIndex].substring(0, splittedZipPath[lastArrayIndex].length() - 4);
	}

	private static boolean unzipIt(final String inputZip, final String outputDir, boolean recursively) {

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

				// System.out.println(file);

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

			return true;

		} catch (FileNotFoundException e) {
			System.out.println("Cannot find file.");
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			System.out.println("Cannot perform IO operation.");
			e.printStackTrace();
			return false;
		}

		// open destination output directory in Windows explorer
		/*
		 * 
		 * Runtime.getRuntime().exec("explorer.exe /select," + OUTPUT_DIR); }
		 * catch (IOException e) { e.printStackTrace(); }
		 */
	}

}
