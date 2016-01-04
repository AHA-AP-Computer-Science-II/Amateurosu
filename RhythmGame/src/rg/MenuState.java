package rg;

import java.awt.Font;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.SecureRandom;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class MenuState extends BasicGameState{

	public Image img, sakura;
	public String[] nygrenIsms, lineArray;
	public String fileKey = "res/nigrenSama.png",str, ls, line, temp;
	public StringBuilder stringBuilder;
	public SecureRandom rand;
	public int numToCheck, lineNum,x,y;
	public static Audio song;
    BufferedReader reader;
    public SpriteSheet flowers;
    public Animation flowerAnimation1, flowerAnimation2, flowerAnimation3;
    
    public int timer;
    public float sakuraNum, sakuraInc;
    
    Font font = new Font("Times New Roman", Font.BOLD, 40);
    UnicodeFont uFont = new UnicodeFont(font, font.getSize(), font.isBold(), font.isItalic());
    
	public MenuState() {
	}
	
	@Override
	public void init(GameContainer container, StateBasedGame arg1) throws SlickException {
		sakuraNum = 1;
		timer = 0;
		sakuraInc = 1;
		flowers = new SpriteSheet("res/fallingPetalSpriteSheet.png",64,64);
		flowerAnimation1 = new Animation(flowers,100);
		flowerAnimation2 = flowerAnimation1.copy();
		flowerAnimation3 = flowerAnimation1.copy();
		rand = new SecureRandom();
		temp = "";
		try {
			lineArray = new String[JojoGame.readLines("res/nigrenIsms.txt")];
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		lineNum = 0;
		try {
			song =JojoGame.loadAudio("jin");
			song.playAsMusic(1.0f, 1.0f, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	    line = null;
	    stringBuilder = new StringBuilder();
	    ls = System.getProperty("line.separator");

		try {
			reader = new BufferedReader( new FileReader ("res/nigrenIsms.txt"));
			while( ( line = reader.readLine() ) != null ) {
		        stringBuilder.append( line );
		        lineArray[lineNum]=stringBuilder.toString();
		        stringBuilder.replace(0, stringBuilder.length(), "");
		        lineNum++;
		    }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		sakura = JojoGame.loadImage("sakura");
		img = JojoGame.loadImage("nigrenSama");
		numToCheck = rand.nextInt(lineArray.length);
		str = lineArray[numToCheck];
		flowerAnimation1.setPingPong(true);
		flowerAnimation2.setPingPong(true);
		flowerAnimation3.setPingPong(true);
	}

	@Override
	public void render(GameContainer container, StateBasedGame arg1, Graphics g) throws SlickException {
		container.setTargetFrameRate(60);
		img.draw(0, 0);
		g.setColor(Color.black);
		g.drawString(str.replaceAll("@ ", ls), 20, 380);
		flowerAnimation1.draw(x+sakuraNum, y);
		flowerAnimation2.draw(x-sakuraNum, y+sakuraNum);
		flowerAnimation3.draw(x+sakuraNum, y-sakuraNum);
	}
	@Override
	public void update(GameContainer container, StateBasedGame game, int d) throws SlickException {
		if(!(x>container.getWidth()&&y>container.getHeight())){
			flowerAnimation1.update(d);
			timer+=d;
			if(timer > 10){
				x+=rand.nextInt(5);
				y++;
				sakuraNum+=sakuraInc;
				if(sakuraNum >30||sakuraNum <-30)
					sakuraInc*=-1;
				timer=0;
			}
		}else{
			x=0;
			y=0;
		}
		if (container.getInput().isKeyPressed(Input.KEY_ENTER)) {
			song.stop();
			JojoGame.playMusic();
			container.getInput().clearKeyPressedRecord();
			game.enterState(StateBasedRunner.gameID);
		}
		if(container.getInput().isKeyPressed(Input.KEY_R)){
			numToCheck = rand.nextInt(lineArray.length);
			str = lineArray[numToCheck];
		}
		if (container.getInput().isKeyPressed(Input.KEY_ESCAPE)) {
			System.exit(0);
		}
	}

	@Override
	public int getID() {
		return StateBasedRunner.menuID;
	}

}
