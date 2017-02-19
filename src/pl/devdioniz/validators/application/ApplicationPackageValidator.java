package pl.devdioniz.validators.application;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ApplicationPackageValidator {
	
	private final static Logger log = Logger.getLogger(ApplicationPackageValidator.class.getName());

	private File wasPolicy;
	private File versionXml;
	private File approvedWasPolicy;
	

	public File getWasPolicy() {
		return wasPolicy;
	}

	public void setWasPolicy(File wasPolicy) throws FileNotFoundException {
		
		if (wasPolicy == null ) {
			throw new IllegalArgumentException("WAS Policy file cannot be null");
		}
		
		if (!wasPolicy.exists()) {
			throw new FileNotFoundException("WAS Policy does not exists");
		}
		
		this.wasPolicy = wasPolicy;
	}

	public File getVersionXml() {
		return versionXml;
	}

	public void setVersionXml(File versionXml) throws FileNotFoundException {
		
		if (versionXml == null ) {
			throw new IllegalArgumentException("Version XML cannot be null");
		}
		
		if (!versionXml.exists()) {
			throw new FileNotFoundException("Version XML does not exists");
		}
		
		this.versionXml = versionXml;
	}

	public File getApprovedWasPolicy() {
		return approvedWasPolicy;
	}

	public void setApprovedWasPolicy(File approvedWasPolicy) {
		
		if (approvedWasPolicy == null ) {
			throw new IllegalArgumentException("Approved WAS Policy cannot be null");
		}
		
		this.approvedWasPolicy = approvedWasPolicy;
	}

	public ApplicationPackageValidator(File wasPolicy, File approvedWasPolicy, File versionXml) throws FileNotFoundException {
		
		setApprovedWasPolicy(approvedWasPolicy);
		setWasPolicy(wasPolicy);
		setVersionXml(versionXml);
	}
	
	private boolean isWasPolicyCorrect() throws IOException {
		
		List<String> wasPolicyContent = Files.readAllLines(wasPolicy.toPath());
		
		List<String> approvedWasPolicyContent = Files.readAllLines(approvedWasPolicy.toPath());
		
		boolean isPolicyOK = approvedWasPolicyContent.containsAll(wasPolicyContent);
		
		return isPolicyOK;
	}

	public boolean validate() throws IOException {
		
		log.log(Level.INFO, "validating ...");
		return isWasPolicyCorrect();
	}

}
