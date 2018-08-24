// Name - Tejaswi Singh , Student Id - 1001387430 


// Importing the required packages 
import java.awt.TextArea;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;

public class FileServer extends Thread {


	private ServerSocket ss; // Declaring socket variable
	
	// Declaring static variables
	static int i=0;
	static private final String newline = "\n";
	static TextArea area;   
	
	// Declaring default constructor which is called in main method to construct the GUI
	FileServer(){  
		JFrame f = new JFrame(); // Declaring the frame object and designing the GUI
	    area=new TextArea();  
	    area.setBounds(20,100,300,300);  
	    f.add(area); 
	    f.setSize(400,450);  
	    f.setLayout(null);  
	    f.setVisible(true);  
	}  
	public FileServer(int port) {
		try {
			ss = new ServerSocket(port); // Defining the port on which the file server will run
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// run method to establish connection with the client and calling saveFile method to upload the files to the server
	public void run() {
		while (true) {
			try {
				Socket clientSock = ss.accept();
				i++; 
				area.append("Client "+ i + "  Connected"+ newline);
				saveFile(clientSock);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// Upload method to upload files to the server
	
	private void saveFile(Socket clientSock) throws IOException {  
		// Declaring the input and Data Input, File Output streams
		InputStream in = clientSock.getInputStream();
		DataInputStream dis = new DataInputStream(in);
		String fileName = dis.readUTF();
		FileOutputStream fos = new FileOutputStream("C:/Users/tejas/Desktop/Server/"+fileName); // Static directory where the files are  uploaded
		byte[] buffer = new byte[100000]; // Declaring the size of the buffer

		int filesize = 151230; // So that if we want we can send the file size as well
		int read = 0;
		int totalRead = 0;
		int remaining = filesize;
		// loop to write files to the server static directory
		while((read = dis.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
			totalRead += read;
			remaining -= read;
			System.out.println("read " + totalRead + " bytes.");
			fos.write(buffer, 0, read);
			break;
		}

		// Closing all the I/O file/Data Streams
		fos.close();
		dis.close();
		
	}

	//  Main method declares the port number on which the connection will be made and calls a thread using .start() method and also calls default constructor for making the GUI
	public static void main(String[] args) {
		FileServer fs = new FileServer(1988);
		fs.start();
		new FileServer();
	}

}