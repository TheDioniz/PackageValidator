package pl.devdioniz.gui;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import pl.devdioniz.validators.application.ApplicationPackageValidator;

public class ValidatorGui extends JFrame {

	private static final long serialVersionUID = 1L;
	
	JButton wasPolictBtn;
	JLabel validationStatusLabel;
	JFileChooser files;
	JButton approvedWasPolicyBtn;
	ImageIcon icon;
	JButton validateBtn;
	private File wasPolicy;
	private File approvedWasPolicy;
	
	private ApplicationPackageValidator validator;

	public ValidatorGui() {
		initUI();
		addListenersToComponents();
	}

	public void initUI() {

		// setup frame icon
		icon = new ImageIcon("src/pictures/Numbers-1-Black-icon.png");
		Container container = this.getContentPane();

		// setup the JFrame look and behavior
		this.setIconImage(icon.getImage());
		this.setTitle("My Custom App");
		this.setVisible(true);
		this.setSize(320, 480);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(new FlowLayout());
		this.setMaximumSize(new Dimension(320, 480));

		// initialize components
		wasPolictBtn = new JButton("Add was.policy");
		wasPolictBtn.setToolTipText("insert was.policy of the package");

		validationStatusLabel = new JLabel("");
		validationStatusLabel.setToolTipText("package validation status");

		files = new JFileChooser();
		approvedWasPolicyBtn = new JButton("Add approved was.policy file");
		
		validateBtn = new JButton("Validate policies");

		// add components to root container so they are all visible
		container.add(wasPolictBtn);
		container.add(approvedWasPolicyBtn);
		container.add(validateBtn);
		container.add(validationStatusLabel);

	}

	private void addListenersToComponents() {

		wasPolictBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				files.showOpenDialog(null);
				File file = files.getSelectedFile();

				if (file != null) {
					wasPolicy = file;
				}
			}
		});

		approvedWasPolicyBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				files.showOpenDialog(null);
				File file = files.getSelectedFile();

				if (file != null) {
					approvedWasPolicy = file;
				}

			}
		});
		
		validateBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					validator = new ApplicationPackageValidator(wasPolicy, approvedWasPolicy, new File("src/version.xml"));
					boolean isOK = validator.validate();
					validationStatusLabel.setText("Validation status: " + isOK);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
	}

	public static void run() {
		
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				ValidatorGui gui = new ValidatorGui();
			}

		});
	}

}
