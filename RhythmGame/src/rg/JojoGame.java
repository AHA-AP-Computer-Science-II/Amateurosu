package rg;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.particles.ConfigurableEmitter;
import org.newdawn.slick.particles.ParticleSystem;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.ResourceLoader;

public class JojoGame extends BasicGameState{

	/*///////////////////////////////////////////////////////////////////////
	 * 													To-Do List
	 * Make it so you can reset the game if you leave the state
	 *Is there a way to fix that weird delay?
	 *Fix the multiplier
	 *///////////////////////////////////////////////////////////////////////
	
	/*///////////////////////////////////////////////////////////////////////
	 * 												Instance Variables
	 *///////////////////////////////////////////////////////////////////////
	
	private Image background, bar, fistA, fistS, fistK, fistL, ora, count1, count2, count3;
	private Rectangle barCol;
	private static Audio bloodyStream;
	private Audio count1Sound;
	private Audio count2Sound;
	private Audio count3Sound;
	private Audio successfulHit;
	private Audio multiplierEnd;
	private Audio multiplierStreak;

	ConfigurableEmitter emitter;
	public ParticleSystem system;
	
	public float radius = 20;
	public int multiplier,score, aLoc = 0, sLoc = 0, kLoc = 0, lLoc = 0;
	
	public final static int WIDTH = 680;
	public static final int HEIGHT = 480;
	private final float MAGIC_NUMBER = 3.9f;
	
	//List for each key of falling objects. 
	
	private ArrayList<FallingObject>aFList = new ArrayList<FallingObject>();
	private ArrayList<FallingObject>sFList = new ArrayList<FallingObject>();
	private ArrayList<FallingObject>kFList = new ArrayList<FallingObject>();
	private ArrayList<FallingObject>lFList = new ArrayList<FallingObject>();
	
	//List for each of the hit times. 
	private ArrayList<Double> aList = new ArrayList<Double>();
	private ArrayList<Double> sList = new ArrayList<Double>();
	private ArrayList<Double> kList = new ArrayList<Double>();
	private ArrayList<Double> lList = new ArrayList<Double>();
	
	//These are used for the TXT file reading
	private static FileReader fileToRead;
	private static BufferedReader bf;
	
	public static String fileKey = "res/beatSheet.txt";
	public TXTRead read;
	
	public int currentBeat, tracker, quarterTracker, numSeconds, rotation = 0, rotationChange = 15;
	public double numBeats, valueToCheck,currentQuarterBeat, timerChecker;
	
	/*///////////////////////////////////////////////////////////////////////
	 * 												CODE START
	 *///////////////////////////////////////////////////////////////////////
	
	
	public JojoGame() throws SlickException {
		super();
	}
	
	/*///////////////////////////////////////////////////////////////////////
	 * 												Initialize Method
	 *///////////////////////////////////////////////////////////////////////
	@Override
	public void init(GameContainer c, StateBasedGame sbg) throws SlickException {
		currentBeat = 0;
		currentQuarterBeat = 1;
		
		timerChecker = 0;
		
		tracker = 0;
		quarterTracker=0;
		
		numBeats = 195.8;
		numSeconds = 89;
		valueToCheck = 0;
		
		//read the txt file
		try {
			read = new TXTRead(fileKey);
			transferValues(read.getArr());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		//background
		background = loadImage("jojo");
		
		//hit rectangle, image goes over
		bar = loadImage("jojoBar");
		barCol = new Rectangle(0, 380, c.getWidth(), 65);
		fistA = loadImage("fistA");
		fistS = loadImage("fistS");
		fistK = loadImage("fistK");
		fistL = loadImage("fistL");
		ora = loadImage("ORA");
		count1 = loadImage("count1");
		count2 = loadImage("count2");
		count3 = loadImage("count3");
		
		//score ints
		score = 0;
		multiplier = 1;
		
		//music is loaded here
		
		try {
			bloodyStream = loadAudio("op2");
			successfulHit = loadAudio("normal-hitclap");
			count1Sound = loadAudio("count1");
			count2Sound = loadAudio("count2");
			count3Sound = loadAudio("count3");
			multiplierEnd = loadAudio("normal-hitfinish");
			multiplierStreak = loadAudio("applause");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//end music load
	}
	
	/*///////////////////////////////////////////////////////////////////////
	 * 												Update Method
	 *///////////////////////////////////////////////////////////////////////

	@Override
	public void update(GameContainer c, StateBasedGame sbg, int d) throws SlickException {
		
		if(c.getInput().isKeyPressed(Input.KEY_ESCAPE))
			System.exit(0);
		if(c.getInput().isKeyPressed(Input.KEY_M))
			enterMenuState(sbg);
		
		//keeps track of rhythm
		currentBeat+=d;
		quarterTracker+=d;
		
		if(quarterTracker>((60/(calcBPM(numBeats,numSeconds)))*1000)/4){
			quarterTracker=0;
			currentQuarterBeat++;
		}
		
		if(currentBeat>((60/(calcBPM(numBeats,numSeconds)))*1000)){
			currentBeat=0;
			currentQuarterBeat=1;
			tracker++;
		}
		
		valueToCheck = tracker+(currentQuarterBeat/10);
		
		
		if(valueToCheck==8.1){
			count1Sound.playAsSoundEffect(1.0f, 1.0f, false);
		}
		if(valueToCheck==10.1){
			count2Sound.playAsSoundEffect(1.0f, 1.0f, false);
		}
		if(valueToCheck==12.1){
			count3Sound.playAsSoundEffect(1.0f, 1.0f, false);
		}
		
//		if(c.getInput().isKeyPressed(Input.KEY_A)){
//			System.out.println("A: "+valueToCheck);
//		}
//		if(c.getInput().isKeyPressed(Input.KEY_S)){
//			System.out.println("S: "+valueToCheck);
//		}
//		if(c.getInput().isKeyPressed(Input.KEY_K)){
//			System.out.println("K: "+valueToCheck);
//		}
//		if(c.getInput().isKeyPressed(Input.KEY_L)){
//			System.out.println("L: "+valueToCheck);
//		}
		if(valueToCheck > 196){
			System.exit(0);
		}
		//add falling objects to list
		addFallingObjects();
		
		//move every image
		moveImage(aFList);
		moveImage(sFList);
		moveImage(kFList);
		moveImage(lFList);
		
		//This is where the scoring happens
		checkScores(c);		
	}
	
	/*///////////////////////////////////////////////////////////////////////
	 * 												Render Method
	 *///////////////////////////////////////////////////////////////////////
	
	public void render(GameContainer c, StateBasedGame sbg, Graphics g) throws SlickException {
		
		//under the hood stuff
		c.setAlwaysRender(true);
		c.setUpdateOnlyWhenVisible(true);
		c.setTargetFrameRate(60);
		
		//background is drawn first
		background.draw(0, 0);
		
		//color of the bar is set to transparent, as its only used for collision detection. 
		g.setColor(Color.transparent);
		g.draw(barCol);
		
		//draws the image that goes over the rectangle barCol - just there to look pretty, it doesn't do anything else. 
		bar.draw(0,0);
		
		//render every fist
		drawFists(c, g);
		
		//sets the color of the text to black and then draws the score and multiplier off of an instance variable
		g.setColor(Color.black);
		g.drawString(Integer.toString(score),600,20);
		g.drawString("X"+Integer.toString(multiplier), 620, 400);
	
	}
	
	/*///////////////////////////////////////////////////////////////////////
	 * 												Misc Methods
	 *///////////////////////////////////////////////////////////////////////
	
	public void enterMenuState(StateBasedGame sbg){
			score = 0;
			multiplier = 0;
			currentBeat = 0;
			quarterTracker=0;
			tracker=0;
			valueToCheck=0;
			aFList.clear();
			sFList.clear();
			kFList.clear();
			lFList.clear();
			aList.clear();
			sList.clear();
			kList.clear();
			lList.clear();
			MenuState.song.playAsMusic(1.0f, 1.0f, false);
			sbg.enterState(StateBasedRunner.menuID);
	}
	
	public void moveImage(ArrayList<FallingObject> list) throws SlickException{
		for(int i = 0; i < list.size(); i++){
			if(list.get(i).getY()+list.get(i).getImg().getHeight()>HEIGHT){
				list.get(i).setY(list.get(i).getY()+1);
				list.get(i).setX(list.get(i).getX()+8);
			}
			else
				list.get(i).setY(list.get(i).getY()+MAGIC_NUMBER);
		}
	}
	
	public void moveImages() throws SlickException{
		moveImage(aFList);
		moveImage(sFList);
		moveImage(kFList);
		moveImage(lFList);
	}

	public void drawFist(GameContainer c, Graphics g, ArrayList<FallingObject> fList, int key){
		for(int i = 0; i < fList.size(); i++){
			g.drawImage(fList.get(i).getImg(), fList.get(i).getX(), fList.get(i).getY());
			if(isInRange(fList.get(i).getY())){
				g.drawImage(ora, fList.get(i).getX()+25, fList.get(i).getY()-25);
			}
		}
	}

	public void drawFists(GameContainer c, Graphics g){
		drawFist(c, g, aFList, Input.KEY_A);
		drawFist(c, g, sFList, Input.KEY_S);
		drawFist(c, g, kFList, Input.KEY_K);
		drawFist(c, g, lFList, Input.KEY_L);
	}
	
	public static Image loadImage(String key) throws SlickException{
		return new Image("res/"+key+".png");
	}

	public static Audio loadAudio(String key) throws IOException{
		return AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("res/"+key+".wav"));
	}
	
	public static int readLines(String file) throws IOException{
		fileToRead = new FileReader(file);
		bf = new BufferedReader(fileToRead);
		
		String aLine;
		int numLines = 0;
		while((aLine = bf.readLine())!=null){
			numLines++;
		}
		bf.close();
		return numLines;
	}

	public void transferValues(String[] arr) throws IOException{
		for(int i = 0; i < readLines(fileKey);i++){
			if(arr[i].contains("A"))
				aList.add(Double.parseDouble(arr[i].substring(3)));
			if(arr[i].contains("S"))
				sList.add(Double.parseDouble(arr[i].substring(3)));
			if(arr[i].contains("K"))
				kList.add(Double.parseDouble(arr[i].substring(3)));
			if(arr[i].contains("L"))
				lList.add(Double.parseDouble(arr[i].substring(3)));
		}
		Collections.sort(aList);
		Collections.sort(sList);
		Collections.sort(kList);
		Collections.sort(lList);
	}

	public void addFallingObjects(){
		if(aLoc < aList.size()&&sLoc<sList.size()&&kLoc<kList.size()&&lLoc<lList.size()){
			if(aList.get(aLoc)==(valueToCheck+4)){
				addToList(aFList, fistA, 2, 5);
				aLoc++;
			}
			if(sList.get(sLoc)==(valueToCheck+4)){
				addToList(sFList, fistS, 4, 5);
				sLoc++;
			}
			if(kList.get(kLoc)==(valueToCheck+4)){
				addToList(kFList, fistK, 6, 0);
				kLoc++;
			}
			
			if(lList.get(lLoc)==(valueToCheck+4)){
				addToList(lFList, fistL, 8, 14);
				lLoc++;
			}
			}
	}

	public double calcBPM(double d, int numSecs){
		return d*60/numSecs;
	}
	
	public boolean isInRange(float f){
		return f<barCol.getMaxY()&&f>barCol.getMinY();
	}
	
	public void checkScore(GameContainer c, ArrayList<FallingObject> list, int key){
		if(c.getInput().isKeyPressed(key)){
			for(FallingObject f: list){
				if(isInRange(f.getY())){
					f.getImg().setRotation(rotation);
					score+=10*multiplier;
					rotationChange *=-1;
					f.getImg().rotate(rotation+rotationChange);
					successfulHit.playAsSoundEffect(1.0f, 1.0f, false);
				}
			}
		}
	}
	
	public void checkScores(GameContainer c){
		checkScore(c,aFList,Input.KEY_A);
		checkScore(c,sFList,Input.KEY_S);
		checkScore(c,kFList,Input.KEY_K);
		checkScore(c,lFList,Input.KEY_L);
	}
	
	public void addToList(ArrayList<FallingObject> fList, Image img, int mult, int tweak){
		fList.add(new FallingObject(img,WIDTH/10*mult-radius-tweak,0-radius,radius));
	}
	
	public static void playMusic(){
		bloodyStream.playAsMusic(1.0f, 1.0f, false);
	}
	
	@Override
	public int getID() {
		return StateBasedRunner.gameID;
	}
	
	/*///////////////////////////////////////////////////////////////////////
	 * 											Getters and Setters
	 *///////////////////////////////////////////////////////////////////////
	
	public Image getBackground() {
		return background;
	}
	public Image getBar() {
		return bar;
	}
	public Image getFistA() {
		return fistA;
	}
	public Image getFistS() {
		return fistS;
	}
	public Image getFistK() {
		return fistK;
	}
	public Image getFistL() {
		return fistL;
	}
	public Rectangle getBarCol() {
		return barCol;
	}
	public Audio getWavEffect() {
		return bloodyStream;
	}
	public float getRadius() {
		return radius;
	}
	public int getMultiplier() {
		return multiplier;
	}
	public int getScore() {
		return score;
	}
	public void setBackground(Image background) {
		this.background = background;
	}
	public void setBar(Image bar) {
		this.bar = bar;
	}
	public void setFistA(Image fistA) {
		this.fistA = fistA;
	}
	public void setFistS(Image fistS) {
		this.fistS = fistS;
	}
	public void setFistK(Image fistK) {
		this.fistK = fistK;
	}
	public void setFistL(Image fistL) {
		this.fistL = fistL;
	}
	public void setBarCol(Rectangle barCol) {
		this.barCol = barCol;
	}
	public void setWavEffect(Audio bloodyStream) {
		this.bloodyStream = bloodyStream;
	}
	public void setRadius(float radius) {
		this.radius = radius;
	}
	public void setMultiplier(int multiplier) {
		this.multiplier = multiplier;
	}
	public void setScore(int score) {
		this.score = score;
	}

}
