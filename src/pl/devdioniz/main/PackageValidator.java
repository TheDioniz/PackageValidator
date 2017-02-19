package pl.devdioniz.main;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import pl.devdioniz.gui.ValidatorGui;
import pl.devdioniz.validators.application.ApplicationPackageValidator;

public class PackageValidator {

	private final static Logger log = Logger.getLogger(PackageValidator.class.getName());

	public static void main(String[] args) throws Exception {

		log.log(Level.INFO, "Starting validation ...");

		consoleRunner();
		// graphicalRunner();
		
	}

	public static void graphicalRunner() {

		ValidatorGui.run();
		log.log(Level.INFO, "Done.");
	}

	public static void consoleRunner() throws Exception {

		// setup
		ApplicationPackageValidator validator = new ApplicationPackageValidator(new File("examples/was.policy"),
				new File("examples/approved.was.policy"), new File("examples/version.xml"));

		// validate
		System.out.println("was policy validation status: " + validator.validate());
		log.log(Level.INFO, "Done.");
	}

}
