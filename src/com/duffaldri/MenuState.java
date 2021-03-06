package com.duffaldri;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.duffaldri.Audio.Audio;
import com.duffaldri.Sprites.Button;

public class MenuState implements State {
	StateManager sm;
	public int areaWidth = 1200;
	public int areaHeight = 600;
	
	private Background bg;
	private BufferedImage 	logo,
							play;
	private ArrayList<Button> buttonList = new ArrayList<Button>();
	private Audio bgMusic;
//	private JButton playButton,
//					settingButton;
	
	public MenuState(StateManager sm) {
		this.sm = sm;
		
		bg = new Background(0, 0, areaWidth * 2, areaHeight, Color.BLACK);
		try {
			logo = ImageIO.read(new File("resource/logo.png"));
			play = ImageIO.read(new File("resource/button-start.png"));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		buttonList.add(new Button(play, areaWidth/2 - (144 * 3/4) + 2, 
				areaHeight/2 + 70, 288 * 3 / 4, 135 * 3 / 4, 1));
		
		sfxInit();
	}
	
	@Override
	public void sfxInit() {
		bgMusic = new Audio("indonesia-raya.wav", 2);
		bgMusic.bgPlay();
	}
	
	@Override
	public void draw(Graphics g) {
		// TODO Auto-generated method stub
		bg.draw(g);
		g.drawImage(logo, areaWidth/2 - 150, areaHeight/2 - 260, 300, 300, null);
		for(Button b : buttonList) {
			b.draw(g);
		}
		Font font = new Font("Minecraft", Font.PLAIN, 30);
		g.setColor(Color.WHITE);
		g.setFont(font);
		g.drawString("Highscore " + sm.getHighscore(), areaWidth/2 - 80, 500);
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		bg.moveBG();
	}
	
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		int x = e.getX();
		int y = e.getY();
		
		for(Button b : buttonList) {
			int s = b.mouseClicked(x, y);
			if(s != -1) {
				bgMusic.stop();
				sm.setState(s);
			}
			
		}
				
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
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
