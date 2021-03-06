package com.custardgames.coatofarms;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.custardgames.coatofarms.client.ClientMain;
import com.custardgames.coatofarms.server.ServerMain;

public class CoatOfArms
{
	public static final String name = "Coat of Arms";

	public static final int nativeWidth = 400;
	public static final int nativeHeight = 400;
	public static final double screenScale = 1.5;
	public static final int windowWidth = (int) (nativeWidth * screenScale);
	public static final int windowHeight = (int) (nativeHeight * screenScale);
	
	private JFrame frame;
	private InputHandler input;
	private WindowHandler window;
	private ClientMain client;
	private ServerMain server;
	
	public CoatOfArms()
	{
		init();
	}
	
	public void init()
	{
		
		Object[] options2 = {"Join Server", "Host Server"};
		if (JOptionPane.showOptionDialog(new JFrame(), "", "", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options2, options2[0]) == 0)
		{
			frame = new JFrame(name);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setLayout(new BorderLayout());
			frame.setMinimumSize(new Dimension(windowWidth, windowHeight));
			frame.setMaximumSize(new Dimension(windowWidth, windowHeight));
			frame.setPreferredSize(new Dimension(windowWidth, windowHeight));
			frame.pack();
			frame.setResizable(false);
			frame.setLocationRelativeTo(null);
			frame.setFocusable(true);
			frame.setVisible(true);
			
			input = new InputHandler();
			frame.addKeyListener(input);
			
			window = new WindowHandler(this);
			frame.addWindowListener(window);
			
			Canvas gameScreen = new Canvas();
			gameScreen.setBackground(Color.BLACK);
			gameScreen.setMinimumSize(new Dimension(windowWidth, windowHeight));
			gameScreen.setMaximumSize(new Dimension(windowWidth, windowHeight));
			gameScreen.setSize(new Dimension(windowWidth, windowHeight));
			gameScreen.setFocusable(false);
			frame.add(gameScreen);
			client = new ClientMain(gameScreen, input);
			client.joinGame("localhost", 25555, "Dom");
		}
		else
		{
			server = new ServerMain(25555);
			server.start();
		}
	}
	
	public void start()
	{
		
	}
	
	public void stop()
	{
		if (client != null)
			client.leaveGame();
		if (server != null)
			server.stop();
	}
	
	public void windowDeactivated()
	{
		input.releaseAll();
	}
	
	public static void main(String[] args)
	{
		new CoatOfArms().start();
	}
}
