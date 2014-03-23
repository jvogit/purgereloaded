package com.gmail.mcraftworldmc.thepurge.utilities;

import java.util.Random;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;

public class FireworkEffects {
	public FireworkEffect randomFireworkEffect(){
		Color co = null;
		/* Type type = null; */
		Random ra = new Random();
		int i = ra.nextInt(17) + 1;
		/* colors */
		switch (i){
		default:
		case 1:
			co = Color.AQUA;
			break;
		case 2:
			co = Color.BLACK;
			break;
		case 3:
			co = Color.BLUE;
			break;
		case 4:
			co = Color.FUCHSIA;
			break;
		case 5:
			co = Color.GRAY;
			break;
		case 6:
			co = Color.GREEN;
			break;
		case 7:
			co = Color.LIME;
			break;
		case 8:
			co = Color.MAROON;
			break;
		case 9:
			co = Color.NAVY;
			break;
		case 10:
			co = Color.OLIVE;
			break;
		case 11:
			co = Color.ORANGE;
			break;
		case 12:
			co = Color.PURPLE;
			break;
		case 13:
			co = Color.RED;
			break;
		case 14:
			co = Color.SILVER;
			break;
		case 15:
			co = Color.TEAL;
			break;
		case 16:
			co = Color.WHITE;
			break;
		case 17:
			co = Color.YELLOW;
			break;
		}
		/* Type of explosion */
		/* i = ra.nextInt(5) + 1;
		switch(i){
		case 1: 
			type = Type.BALL_LARGE;
			break;
		case 2:
		    type = Type.BALL;
		    break;
		case 3:
			type = Type.BURST;
			break;
		case 4:
			type = Type.CREEPER;
			break;
		case 5:
			type = Type.STAR;
			break;
		}
		*/
		return FireworkEffect.builder().with(Type.STAR).withColor(co).build();
	}
	public FireworkEffect deathEffect(){
		return FireworkEffect.builder().with(Type.BALL_LARGE).withColor(Color.RED).withTrail().build();
	}
}
