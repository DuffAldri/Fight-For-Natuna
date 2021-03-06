package com.duffaldri;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class HealthPoint {
	int value;
	String text;
	Font font;
	
	public HealthPoint() {
		font = new Font("Minecraft", Font.BOLD, 20);
		this.value = 100;
		this.text = "HP = ";
	}
	
	public int getValue() {
		return value;
	}

	public void draw (Graphics g) {
		g.setFont(font);
		g.setColor(Color.WHITE);
		g.drawString(text + value, 5, 95);
	}
	
	public void increaseHP(int value) {
		this.value += value;
		if(this.value > 100) this.value = 100;
	}
	
	public void decreaseHP(int value) {
		this.value -= value;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
