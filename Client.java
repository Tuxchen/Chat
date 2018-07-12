package de.tuxchan;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.*;
import java.io.*;

public class Client extends JFrame {
	private Container c;
	private JMenuBar menuBar;
	private JMenu menu, menuConnect;
	private JMenuItem quit, connect;
	private JTextField name;
	private JTextArea chat;
	private JScrollPane sp;
	private JTextField input;
	private final Font font = new Font("Comic Sans", Font.BOLD + Font.ITALIC, 15);
	
	private Socket sock;
	private BufferedReader in;
	private PrintWriter out;
	private Thread t, tKey;
	private String key;
	
	private class ChatThread extends Thread {
		
		public void run() {
			try {
				key = in.readLine();
				String text;
				boolean run = true;
				
				while(run) {
					if(t.isInterrupted()) {
						run = false;
					}
					text = in.readLine();
					if(text != null) {
						if(text.contains("***")) {
							chat.append(text + "\n");
							chat.setCaretPosition(chat.getText().length());
							if(text.equals("*** Chat partner has end the connection. ***")) {
								t.interrupt();
								in.close();
								out.close();
								sock.close();
							}
						}
						else {
							chat.append(AES.decrypt(text, key) + "\n");
							chat.setCaretPosition(chat.getText().length());
						}
					}
				}
			}
			catch(IOException e) {
			}
		}
	}
	
	private class CloseWindow extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			
			if(sock != null) {
				out.println("<!#close#!>");
				
				t.interrupt();
			
				try {
					in.close();
					out.close();
					sock.close();
				}
				catch(IOException exp) {
					JOptionPane.showMessageDialog(c, exp.getMessage());
				}
			}
			
			System.exit(0);
		}
	}
	
	private class PressEnter implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == input) {
				String msg = "<" + name.getText() + "> " + input.getText();
				out.println(AES.encrypt(msg, key));
				chat.append(msg + "\n");
				chat.setCaretPosition(chat.getText().length());
				input.setText("");
			}
			else if(e.getSource() == connect) {
				try {
					
					sock = new Socket("www.tuxchan.de", 3000);
					in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
					out = new PrintWriter(sock.getOutputStream(), true);
					
					chat.setText("*** Wait for chat partner ***\n");
					chat.setCaretPosition(chat.getText().length());
					
					t = new ChatThread();
					t.start();
				}
				catch(IOException exp) {
					JOptionPane.showMessageDialog(c, exp.getMessage());
				}
			}
			else if(e.getSource() == quit) {
				if(sock != null) {
					if(out != null) {
						out.println("<!#close#!>");
					}
					
					t.interrupt();
					
					try {
						in.close();
						out.close();
						sock.close();
					}
					catch(IOException exp) {
						JOptionPane.showMessageDialog(c, exp.getMessage());
					}
				}
			}
		}
	}
	
	public Client() {
		c = getContentPane();
		
		addWindowListener(new CloseWindow());
		
		menuBar = new JMenuBar();
		
		menu = new JMenu("Quit");
		menu.setMnemonic(KeyEvent.VK_Q);
		
		menuConnect = new JMenu("Connect");
		menuConnect.setMnemonic(KeyEvent.VK_C);
		
		connect = new JMenuItem("Connect");
		connect.setMnemonic(KeyEvent.VK_C);
		connect.addActionListener(new PressEnter());
		
		quit = new JMenuItem("Quit");
		quit.setMnemonic(KeyEvent.VK_Q);
		quit.addActionListener(new PressEnter());
		
		menu.add(quit);
		menuConnect.add(connect);
		menuBar.add(menuConnect);
		menuBar.add(Box.createGlue());
		menuBar.add(menu);
		
		setJMenuBar(menuBar);
		
		name = new JTextField("Anonymous");
		name.setFont(font);
		
		chat = new JTextArea();
		chat.setEditable(false);
		chat.setLineWrap(true);
		chat.setWrapStyleWord(true);
		chat.setFont(font);
		sp = new JScrollPane(chat);
		
		input = new JTextField();
		input.setFont(font);
		input.addActionListener(new PressEnter());
		
		c.add(BorderLayout.NORTH, name);
		c.add(BorderLayout.CENTER, sp);
		c.add(BorderLayout.SOUTH, input);
	}
	
	public static void main (String[] args) {
		Client window = new Client();
		window.setTitle("Client 1.0");
		window.setSize(500, 700);
		window.setVisible(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}