package com.duffaldri;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

public class GameState extends State {

	private Player player;
	private Background bg;
	private Robot robot;
	private Button homeButton;
	private Button replayButton;
	private BufferedImage home;
	private BufferedImage replay;
	private BufferedImage gameOver;
	private boolean isLose;
	int randTimer = 20;
	int time = 0;
	
	//SFX
	private Audio bgMusic;
	
	ArrayList<PlayerBullet> playerBulletList = new ArrayList<PlayerBullet>();
	ArrayList<EnemyBullet> enemyBulletList = new ArrayList<EnemyBullet>();
	ArrayList<Enemy> enemyList = new ArrayList<Enemy>();
	ArrayList<Explosion> explosionList = new ArrayList<Explosion>();
	ArrayList<Notification> notifList = new ArrayList<Notification>();
	
	int[] level = new int[]{5, 10, 15};
	private int enemyCounter;
	private Highscore highscore;
	private Score score;
	private HealthPoint hp;
	boolean isHold;
	
	
	public GameState(StateManager sm) {
		this.sm = sm;
		this.isLose = false;
		
		int x = areaWidth/2 + 27/2;
		int y = areaHeight/2 + 27/2;
		
		player = new Player(x, y, Color.BLUE);
		bg = new Background(0,0, areaWidth, areaHeight, Color.BLACK);
		highscore = new Highscore();
		score = new Score();
		hp = new HealthPoint();	
		
		try {
			home = ImageIO.read(new File("resource/home.png"));
			replay = ImageIO.read(new File("resource/replay.png"));
			gameOver = ImageIO.read(new File("resource/game-over.png"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		homeButton = new Button(home, areaWidth - 100, 25, 50, 50, 0);
		
		this.isHold = false;	
		
		try {
			this.robot = new Robot();
			robot.mouseMove(x, y);
		} catch (AWTException e) {
			e.printStackTrace();
		}
		
		sfxInit();
		replayButton = new Button(replay, areaWidth/2 - 50, areaHeight/2 + 20, 100, 100, 0);
	}
	
	public void update() {
		if(!isLose) {
			Random rand = new Random();
//			ball.move(box);
//			try {
			for(int i = 0; i < explosionList.size(); i++) {
				if(explosionList.get(i).counter >= 7)
					explosionList.remove(i);
			}
			
			for(int i = 0; i < notifList.size(); i++) {
				if(notifList.get(i).counter >= 5)
					notifList.remove(i);
			}
			
			for(int i = 0; i < playerBulletList.size(); i++) {
				boolean flag = false;
				System.out.println("Checking bullet. Bullet: " + i);
				Bullet b = playerBulletList.get(i);
				if(b.y + 40 < 0) {
					playerBulletList.remove(i);
					System.out.println("Bullet removed");
				}
				else {
					for(int j = 0; j < enemyList.size() && flag == false; j++) {
						System.out.println("Checking hit");
						Enemy e = enemyList.get(j);
						
						if(e.bulletHit(playerBulletList.get(i))) {
							explosionList.add(new Explosion(e.x + e.width/2, e.y + e.height/2));
							enemyList.remove(j);
							playerBulletList.remove(i);
							System.out.println("Enemy Hit");
							flag = true;
							score.addValue();
							enemyCounter--;
							hp.increaseHP(2);					
							notifList.add(new Notification(player.minX, player.minY, 2));
						}
					}	
				}
				b.move();
				
			}			
		
			bg.moveBG();
				
			if(time >= randTimer && enemyCounter < level[0]) {
				enemyList.add(new Enemy(areaWidth, Color.red));
				time = 0;
				randTimer = rand.nextInt(30 + 1) + 10;
				enemyCounter++;
			}
				
			for(int i = 0; i < enemyList.size(); i++) {
				Enemy e = enemyList.get(i);
				e.move();
				e.shootTimer++;
				if(e.y > areaHeight) {
					enemyList.remove(i);
					hp.decreaseHP(20);
					notifList.add(new Notification(player.minX, player.minY, -20));
					System.out.println("Enemy removed");
					enemyCounter--;
				}	
				
				if(player.collide(e)) {
					enemyList.remove(i);
					hp.decreaseHP(10);
					notifList.add(new Notification(player.minX, player.minY, -10));
					explosionList.add(new Explosion(e.x + e.width/2, e.y + e.height/2));
					System.out.println("Collide with enemy");
					enemyCounter--;
				}	
				
				if(e.shootTimer >= 70) {
					enemyBulletList.add(new EnemyBullet(e.x + e.width/2 - 2, e.y + e.height/2 , -15));
					e.shootTimer = 0;
				}
				
			}	
			
			for(int i = 0; i < enemyBulletList.size(); i++) {
				Bullet b = enemyBulletList.get(i);
				System.out.println(enemyBulletList.size());
				if(b.y > areaHeight) {
					enemyBulletList.remove(i);
					System.out.println("Enemy bullet removed");
				}
				System.out.println(enemyBulletList.size());
				if(player.bulletHit(b)) {
					hp.decreaseHP(10);
					notifList.add(new Notification(player.minX, player.minY, -10));
					if(enemyBulletList.get(i) != null) enemyBulletList.remove(i);
				}
				b.move();
			}
	
			time++;
			if(hp.getValue() == 0) {
				this.isLose = true;
				bgMusic.stop();
				Audio lose = new Audio("lose.wav");
				lose.play();
			}
		} 
		
	}
	
	@Override
	public void sfxInit() {
		bgMusic = new Audio("gamebg.wav", 2);
		bgMusic.bgPlay();
	}
	
	@Override
	public void draw(Graphics g) {
		// TODO Auto-generated method stub
		
		bg.draw(g);
		player.draw(g);
		
		for(Bullet b : playerBulletList) {
			b.draw(g);
		}
		
		for(Bullet b : enemyBulletList) {
			b.draw(g);
		}
		
		for(Enemy e : enemyList) {
			e.draw(g);
		}
		

		
		if(!isLose) {
			homeButton.draw(g);
			for(Explosion e: explosionList) {
				e.draw(g);
			}
			

			for(Notification n: notifList) {
				n.draw(g);
			}
			
			highscore.draw(g);
			hp.draw(g);
			score.draw(g);
		}
		
		if(isLose) {
			Color c = new Color(0, 0, 0, 230);
			Font font = new Font("Minecraft", Font.PLAIN, 30);
			g.setColor(c);
			g.fillRect(0, 0, areaWidth, areaHeight);
			g.drawImage(gameOver, areaWidth/2 - 597/2, 100, 597, 103, null);
			g.setColor(Color.WHITE);
			g.setFont(font);
			g.drawString("Final score " + score.getValue(), areaWidth/2 - 100, 250);
			replayButton.draw(g);
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (!isLose) {
			int x = e.getX();
			int y = e.getY();
			player.x = x;
			player.y = y;
			player.minX = x - player.width / 2;
			player.minY = y - player.height / 2;
		}
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (!isLose) {
			// TODO Auto-generated method stub
			int x = e.getX();
			int y = e.getY();
			player.x = x;
			player.y = y;
			player.minX = x - player.width / 2;
			player.minY = y - player.height / 2;
		}
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
		int x = e.getX();
		int y = e.getY();
		this.isHold = true;
		if(!isLose) {
			this.playerBulletList.add(new PlayerBullet(x - 2, y, 25));
			int state = homeButton.mouseClicked(x, y);
			if(state != -1) {
				sm.setState(state);
				bgMusic.stop();
			}
		}
		if(isLose) {
			int state = replayButton.mouseClicked(x, y);
			if(state != -1) sm.setState(state);
		}
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		this.isHold = false;
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
	}
	
	
}
