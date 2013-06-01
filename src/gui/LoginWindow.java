package gui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.UIManager;

public class LoginWindow implements ActionListener{

	private JFrame frame;
	private JTextField textField;
	private JTextField textField_1;
	private JButton btnContinue,btnLogin;
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					try {
						UIManager.setLookAndFeel(UIManager
								.getSystemLookAndFeelClassName());
					} catch (Exception unused) {
						;
						// Ignore exception because we can't do anything. Will
						// use default.
					}
					LoginWindow window = new LoginWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public LoginWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new MigLayout("", "[grow][][][70px][][75px][grow][grow]", "[grow][][][][][][grow]"));
		
		JLabel lblUserName = new JLabel("User Name");
		frame.getContentPane().add(lblUserName, "cell 1 1");
		
		textField = new JTextField();
		frame.getContentPane().add(textField, "cell 3 1 4 1,growx");
		textField.setColumns(10);
		
		JLabel lblPassword = new JLabel("Password");
		frame.getContentPane().add(lblPassword, "cell 1 3");
		
		textField_1 = new JTextField();
		frame.getContentPane().add(textField_1, "cell 3 3 4 1,growx");
		textField_1.setColumns(10);
		
		btnLogin = new JButton("Login");
		frame.getContentPane().add(btnLogin, "cell 3 5,growx");
		
		btnContinue = new JButton("Continue");
		btnContinue.addActionListener(this);
		frame.getContentPane().add(btnContinue, "cell 5 5,growx");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btnContinue)
		{
			new Dashboard();
			frame.dispose();
		}
	}

}
