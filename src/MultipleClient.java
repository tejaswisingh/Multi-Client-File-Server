// Name - Tejaswi Singh , Student Id - 1001387430 

// Importing the required packages 
import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.Timer;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.commons.io.FileUtils;

public class MultipleClient extends JPanel implements ActionListener {
	static private final String newline = "\n";

	JButton openButton, saveButton, downloadButton, listButton, disconnectButton; // Declaring Button variables
	DataInputStream inFromServer; // Declaring Data I/O Stream variables
	DataOutputStream outToServer;
	JTextArea log; // Declaring textarea variable
	Socket clSocket; // Declaring socket variable
	JFileChooser fc; // Declaring filechooser variable
	JComboBox<String> cb; // Declaring combobox variable
	FileInputStream in;  // Declaring File I/O Stream variables
	int i;
	
	//date format for http
    static String date=java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now(ZoneOffset.systemDefault())).toString();

	public MultipleClient() {
		super(new BorderLayout());

		//Create the log object 

		log = new JTextArea(5, 20);
		log.setMargin(new Insets(5, 5, 5, 5));
		log.setEditable(false);
		JScrollPane logScrollPane = new JScrollPane(log);
		
		//Create a file chooser
		fc = new JFileChooser();

		// Declaring buttons and adding listeners to them
		openButton = new JButton("Browse");
		openButton.addActionListener(this);

		saveButton = new JButton("Upload");
		saveButton.addActionListener(this);

		listButton = new JButton("List");
		listButton.addActionListener(this);
		
		
		downloadButton = new JButton("Download");
		downloadButton.addActionListener(this);
		
		disconnectButton = new JButton("Disconnect");
		disconnectButton.addActionListener(this);
		
		cb = new JComboBox<String>(); // Declaring object of combobox class
		
		//For layout purposes, put the buttons in a separate panel
		JPanel buttonPanel = new JPanel(); //use FlowLayout
		buttonPanel.add(openButton);
		buttonPanel.add(saveButton);
		buttonPanel.add(listButton);
		buttonPanel.add(cb);
		buttonPanel.add(downloadButton);
		buttonPanel.add(disconnectButton);
		
		//Add the buttons and the log(textarea) to this panel.
		add(buttonPanel, BorderLayout.PAGE_START);
		add(logScrollPane, BorderLayout.CENTER);
	}

	// Defining methods for the actions of the button declared above
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == openButton) {
			int returnVal = fc.showOpenDialog(MultipleClient.this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				log.append("Opening: " + file.getName() + "." + newline);
			} 
		} else if (e.getSource() == saveButton) {
			try {
				send();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}else if (e.getSource() == listButton) {
			try {
				listOfFiles();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}else if (e.getSource() == downloadButton) {
			try {
				downloadFile();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}else if (e.getSource() == disconnectButton) {
			try {
				exitClient();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		
	}
	
	public void exitClient()throws Exception{				// method to exit the server operation
		// --	
		System.exit(0);										// close the connection to the server
	}	   
	
	
// Function to upload files to the server
	public void send() throws IOException {
		File file = fc.getSelectedFile();  // getting the selected file to upload to server
		Socket socket = new Socket("localhost", 1988); // socket connection

		DataOutputStream dos = new DataOutputStream(socket.getOutputStream()); // Initializing data I/O streams
		FileInputStream fis = new FileInputStream(file);
		byte[] buffer = new byte[4096]; // Defining the size of the buffer 
		dos.writeUTF(file.getName());
		while (fis.read(buffer) > 0) {
			dos.write(buffer);
		}
		log.append("File " +file.getName()+" uploaded successfully"+ newline); // Message to client that the  file is uploaded successfully
		String msg= "File " +file.getName()+" uploaded successfully"+ newline; // Taking message into a variable to calculate length of the message
		int res=msg.length(); // Calculating length of the message
		// http string passed to the client
		String httpMsg="\nPOST HTTP/1.1\n"+"Host: http://127.0.0.1\n"+"Date:"+date+"\n"+"Content-Type:application/x-www-form-urlencoded\n"+ 
				"Content-Length:"+res+"\nUser-Agent: Multi-Client File Server Application\n";
		log.append(httpMsg + newline);
		dos.flush();
	}

// Function to list files uploaded to the server
	public void listOfFiles() throws IOException {
		File folder = new File("C:/Users/tejas/Desktop/Server/"); // Static directory where the files are  uploaded
		File[] listOfFiles = folder.listFiles();
		cb.removeAllItems();
		// loop over all the files uploaded to the server
		for (int i = 0; i < listOfFiles.length; i++) { 
			if (listOfFiles[i].isFile()) {
				log.append(listOfFiles[i].getName()+ newline); // List of files displayed to client
				String msg = listOfFiles[i].getName()+ newline; // Taking message into a variable to calculate length of the message
				int res=msg.length(); // Calculating length of the message
				// http string passed to the client
		    	String httpMsg="\nPOST HTTP/1.1\n"+"Host: http://127.0.0.1\n"+"Date:"+date+"\n"+"Content-Type:application/x-www-form-urlencoded\n"+ 
						"Content-Length:"+res+"\nUser-Agent: Multi-Client File Server Application\n";
				log.append(httpMsg + newline);
				cb.addItem(listOfFiles[i].getName());
			} 
		}
		
	}
	
// Function to download files to the server	
	public void downloadFile() throws IOException {
	    String selectedBook = (String) cb.getSelectedItem();
	    
	    InputStream is = null;
	    OutputStream os = null;
	    try {
	    	File source = new File("C:/Users/tejas/Desktop/Server/"+selectedBook); // Static directory where the files are  uploaded
	    	File dest = new File("C:/Users/tejas/Desktop/Download/"+selectedBook); // Static directory where the files are  downloaded
	    	FileUtils.copyFile(source, dest);
	    	log.append("File " +selectedBook+" downloaded successfully"+ newline); // Message to client that the  file is downloaded successfully
	    	String msg= "File " +selectedBook+" downloaded successfully"+ newline; // Taking message into a variable to calculate length of the message
	    	int res=msg.length(); // Calculating length of the message
			// http string passed to the client
	    	String httpMsg="\nPOST HTTP/1.1\n"+"Host: http://127.0.0.1\n"+"Date:"+date+"\n"+"Content-Type:application/x-www-form-urlencoded\n"+ 
					"Content-Length:"+res+"\nUser-Agent: Multi-Client File Server Application\n";
			log.append(httpMsg + newline);
	    	
	    }catch(Exception e){
	    	
	    } finally {
			
			// closing I/O streams
	        is.close(); 
	        os.close();
	    }		
	}
	    
	    
	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event-dispatching thread.
	 */
	private static void createAndShowGUI() {
		//Make sure we have nice window decorations.
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);

		//Create and set up the window.
		JFrame frame = new JFrame("MultipleClient");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Create and set up the content pane.
		JComponent newContentPane = new MultipleClient();
		newContentPane.setOpaque(true); //content panes must be opaque
		frame.setContentPane(newContentPane);

		//Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		//Schedule a job for the event-dispatching thread:
		//creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
}
