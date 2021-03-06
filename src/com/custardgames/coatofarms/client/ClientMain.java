package com.custardgames.coatofarms.client;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;

import com.custardgames.coatofarms.InputHandler;
import com.custardgames.coatofarms.client.net.ClientSocket;
import com.custardgames.coatofarms.shared.entitysystem.entities.World;

public class ClientMain
{
	final private int tickrate = 30;
	final private int framerate = 30;

	private boolean running;
	private boolean rendering;

	private ClientSocket socket;

	private Canvas gameScreen;
	private InputHandler input;
	
	private World world;

	public ClientMain(Canvas gameScreen, InputHandler input)
	{
		this.gameScreen = gameScreen;
		this.input = input;
		
		init();
	}

	public void init()
	{
		running = false;
		rendering = false;
		
		world = new World(input, gameScreen.getWidth(), gameScreen.getHeight());
	}

	public void joinGame(String ipAddress, int port, String username)
	{
		socket = new ClientSocket(ipAddress, port, username);
		socket.start();
		socket.login();
		start();
	}

	public void leaveGame()
	{
		socket.disconnect();
		stop();
	}

	public synchronized void start()
	{
		running = true;
		rendering = true;

		Thread runner = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				loopTick();
			}
		});
		runner.start();

		Thread renderer = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				loopRender();
			}
		});
		renderer.start();
	}

	public void stop()
	{
		running = false;
		rendering = false;
	}

	private void loopTick()
	{
		long currentFrameTime = System.currentTimeMillis();
		long lastFrameTime = currentFrameTime;

		while (running)
		{
			lastFrameTime = currentFrameTime;
			currentFrameTime = System.currentTimeMillis();
			
			tick(currentFrameTime - lastFrameTime);

			if (1000 / tickrate - (currentFrameTime - lastFrameTime) > 0)
			{
				try
				{
					Thread.sleep(1000 / tickrate - (currentFrameTime - lastFrameTime));
				}
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private void loopRender()
	{
		long currentFrameTime = System.currentTimeMillis();
		long lastFrameTime = currentFrameTime;

		while (rendering)
		{
			lastFrameTime = currentFrameTime;
			currentFrameTime = System.currentTimeMillis();
	
			render(currentFrameTime - lastFrameTime);
			
			if (1000 / framerate - (currentFrameTime - lastFrameTime) > 0)
			{
				try
				{
					Thread.sleep(1000 / framerate - (currentFrameTime - lastFrameTime));
				}
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private void tick(long delta)
	{
		world.tick(delta);
	}

	private void render(long delta)
	{
		BufferStrategy bs = gameScreen.getBufferStrategy();
		if (bs == null)
		{
			gameScreen.createBufferStrategy(3);
			return;
		}

		Graphics g = bs.getDrawGraphics();
		g.drawImage(world.render(delta), 0, 0, gameScreen.getWidth(), gameScreen.getHeight(), null);
		g.dispose();
		bs.show();
	}
}
