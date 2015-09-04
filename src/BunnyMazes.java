import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import java.io.*;
import java.util.*;
import javax.microedition.io.file.*;
import javax.microedition.io.*;
import javax.microedition.rms.*;
import javax.microedition.media.*;
public class BunnyMazes extends MIDlet implements CommandListener, ItemStateListener
{
	private Display display;
	private List menuList,levelList;
	private GameCanvas gamePage;
	private Form scoreForm,aboutForm,optionForm;
	private ChoiceGroup soundOn;
	private boolean onSound=false;
	
	private RecordStore recordstore,pausestore;
	
	private Command exitCommand = new Command("Exit", Command.EXIT, 2);
	private Command backCommand = new Command("Back", Command.BACK, 1);
	private Command clearCommand = new Command("Clear Scores", Command.BACK, 1);
	public BunnyMazes()
	{
		display = Display.getDisplay(this);
		menuList = new List("Game menu", Choice.IMPLICIT);

		pausestore=null;

		gamePage=new GameCanvas();
		levelList=new List("Game Levels",Choice.IMPLICIT);
		try{
		levelList.append("Level 1",Image.createImage("/1.png"));
		levelList.append("Level 2", Image.createImage("/2.png"));
		levelList.append("Level 3", Image.createImage("/3.png"));
		levelList.append("Level 4", Image.createImage("/4.png"));
		levelList.append("Level 5", Image.createImage("/5.png"));
		levelList.append("Level 6", Image.createImage("/6.png"));
		levelList.append("Level 7", Image.createImage("/7.png"));
		levelList.append("Level 8", Image.createImage("/8.png"));
		levelList.append("Level 9", Image.createImage("/9.png"));
		levelList.append("Level 10", Image.createImage("/10.png"));
		levelList.addCommand(backCommand);
		levelList.addCommand(exitCommand);

		menuList.append("Continue Game", Image.createImage("/continue.png"));
		menuList.append("New Game", Image.createImage("/new.png"));
		menuList.append("Scores", Image.createImage("/scores.png"));
		menuList.append("Options", Image.createImage("/options.png"));
		menuList.append("About", Image.createImage("/about.png"));
		menuList.append("Exit Game", Image.createImage("/exit.png"));
		
		}
		catch(IOException e)
		{
			System.out.println("Got IOException");
		}
		try
		{
			soundOn= new ChoiceGroup("Enable Sound?",Choice.MULTIPLE);
			soundOn.append("Sound ON",Image.createImage("/sound.png"));
			optionForm=new Form("Game Options");
			optionForm.append(soundOn);
			optionForm.append("GAME CONTROLS:\n 1.UP or 2:- Move Bunny Down\n 2.DOWN 0r 3:-Move Bunny Down\n 3.RIGHT or 6:-Move the Bunnt Right\n 4. LEFT or 8:-Move the Bunny Right");
			optionForm.addCommand(backCommand);
			optionForm.setItemStateListener(this);
			optionForm.setCommandListener(this);
		}
		catch(Exception e)
		{
			System.out.println("Exception gor in sound");
		}
		levelList.setCommandListener(this);
		menuList.setCommandListener(this);
	}
	public void startApp()
	{
		display.setCurrent(menuList);
	}

	public void pauseApp() { }

	public void destroyApp(boolean unconditional)
	{
		notifyDestroyed();
	}
	public void commandAction(Command c, Displayable s)
	{
		if(s==levelList)
		{
			if (c == backCommand)
			{
				display.setCurrent(menuList);
			}
			else if (c == exitCommand)
			{
				destroyApp(false);
			}
			else
			{
				int index = levelList.getSelectedIndex();
				System.out.println("index=" + index);
				gamePage.setLevel(index + 1);
				display.setCurrent(gamePage);
			}
		}
		if(s==menuList)
		{
			if (c == exitCommand)
			{
				destroyApp(false);
			}
			else
			{
				int index = menuList.getSelectedIndex();
				System.out.println("menu index=" + index);
				switch(index)
				{
					case 0:
						{
						// load game from file data

							if(pausestore==null)
							{
								break;
							}
								 Alert alert;
								   	String levelMaze,line= new String();
								   try
									{
										pausestore = RecordStore.openRecordStore("myPauseStore", true);
										try
										{
											byte[] byteInputData = new byte[1];
											int length = 0;
											for (int x = 1; x <= pausestore.getNumRecords(); x++)
											{
												byteInputData = new byte[pausestore.getRecordSize(x)];
												length = pausestore.getRecord(x, byteInputData, 0);
												line=new String(byteInputData,0,length);
												int len = ("level:").length();
												int index1 = line.indexOf("level:");
												if (index1 >= 0)
												{
													levelMaze = line.substring(index1 + len, line.indexOf("$xPos:"));
													gamePage.setLevel(Integer.parseInt(levelMaze));
													System.out.println("got level no="+levelMaze);
													int index2=line.indexOf("xPos:");
													if(index2>=0)
													{
														String xPos=line.substring(index2+("xPos:").length(),line.indexOf("$yPos"));
														System.out.println("got xPos="+xPos);
														gamePage.setX(Integer.parseInt(xPos));
														int index3=line.indexOf("yPos:");
														if(index3>=0)
														{
														 String yPos=line.substring(index3+("yPos:").length(),line.length());
														 System.out.println("got yPos="+yPos);
														 gamePage.setY(Integer.parseInt(yPos));
														 display.setCurrent(gamePage);
														}
													}
												}
											}
										}
										catch (Exception error)
										{
											alert = new Alert("Error Reading", error.toString(),null, AlertType.WARNING);
											alert.setTimeout(Alert.FOREVER);
											display.setCurrent(alert);
										}
										try
										{
											pausestore.closeRecordStore();
										}
										catch (Exception error)
										{
											alert = new Alert("Error Closing", error.toString(), null, AlertType.WARNING);
											alert.setTimeout(Alert.FOREVER);
											display.setCurrent(alert);
										}
									}
									catch (Exception error)
									{
										alert = new Alert("Error Creating", error.toString(), null, AlertType.WARNING);
										alert.setTimeout(Alert.FOREVER);
										System.out.println("score error");
										display.setCurrent(alert);
									}
									if (RecordStore.listRecordStores() != null)
									{
										try
										{
											RecordStore.deleteRecordStore("myPauseStore");
											pausestore=null;
										}
										catch (Exception error)
										{
											alert = new Alert("Error Removing", error.toString(), null, AlertType.WARNING);
											alert.setTimeout(Alert.FOREVER);
											display.setCurrent(alert);
										}
									}
		
							break;
						}
					case 1:
						{
							display.setCurrent(levelList);
							break;
						}
					case 2:
						{
						   // read scores file
						   Alert alert;
						   	String list= new String();
						   try
							{
								recordstore = RecordStore.openRecordStore("myScoreStore", true);
								try
								{
									byte[] byteInputData = new byte[1];
									int length = 0;
									for (int x = 1; x <= recordstore.getNumRecords(); x++)
									{
										byteInputData = new byte[recordstore.getRecordSize(x)];
										length = recordstore.getRecord(x, byteInputData, 0);
										list+="\n"+new String(byteInputData,0,length);
									}
								}
								catch (Exception error)
								{
									alert = new Alert("Error Reading", error.toString(),null, AlertType.WARNING);
									alert.setTimeout(Alert.FOREVER);
									display.setCurrent(alert);
								}
								try
								{
									recordstore.closeRecordStore();
								}
								catch (Exception error)
								{
									alert = new Alert("Error Closing", error.toString(), null, AlertType.WARNING);
									alert.setTimeout(Alert.FOREVER);
									display.setCurrent(alert);
								}
							}
							catch (Exception error)
							{
								alert = new Alert("Error Creating", error.toString(), null, AlertType.WARNING);
								alert.setTimeout(Alert.FOREVER);
								System.out.println("score error");
								display.setCurrent(alert);
							}
							scoreForm=new Form("Scores");
							scoreForm.append(list);
							scoreForm.addCommand(clearCommand);
							scoreForm.addCommand(backCommand);
							scoreForm.setCommandListener(this);
							display.setCurrent(scoreForm);
							break;
						}
					case 3:
						{
							 display.setCurrent(optionForm);
							break;
						}
					case 4:
						{
						aboutForm= new Form("About");
						try{
							aboutForm.append(Image.createImage("/logo.png"));
						}
						catch(IOException ioe)
						{
						System.out.println("logo not found");
						}
						aboutForm.append("\n This Game is about making the bunny to take its carrot from the end block by crossing the blocks in the maze\n\t\t\t This Application has been designed by Rajagopal.M");
						aboutForm.addCommand(backCommand);
						aboutForm.setCommandListener(this);
						display.setCurrent(aboutForm);
							break;
						}
					case 5:
						{
							destroyApp(false);
							break;
						}
				}
			}
		}
		else if (c == backCommand)
		{
				display.setCurrent(menuList);
		}
		else if(c==clearCommand)
		{
			Alert alert;
			if (RecordStore.listRecordStores() != null)
			{
				try
				{
					RecordStore.deleteRecordStore("myScoreStore");
				}
				catch (Exception error)
				{
					alert = new Alert("Error Removing", error.toString(), null, AlertType.WARNING);
					alert.setTimeout(Alert.FOREVER);
					display.setCurrent(alert);
				}
			}
		}

	}

	public void itemStateChanged(Item it)
	{
		boolean[] temp=new boolean[1];
		soundOn.getSelectedFlags(temp);
		onSound=temp[0];
	}

	class GameCanvas extends Canvas implements CommandListener
	{
		class Bunny
		{
		 private int headX,headY,headLength,headBreadth;
		 private int SIZE=1,defaultX=10,defaultY=20;
		 private int ear1X,ear1Y,ear2X,ear2Y,earLength,earBreadth;
		 private int eye1X,eye1Y,eye2X,eye2Y,eyeDia;
		 private int hand1X,hand1Y,hand2X,hand2Y,handLength,handBreadth;
		 private int mouthX,mouthY,mouthDia;
		 private int bodyX,bodyY,bodyLength,bodyBreadth;
		 private int leg1X,leg1Y,leg2X,leg2Y,legLength,legBreadth;

		 public Bunny(int down)
		 {
			ear1X=10;
			ear1Y=ear2Y=0;
			ear2X=17;
			earLength=3;
			earBreadth=8;

			headX=10;
			headY=8;
			headLength=10;
			headBreadth=10;

			eye1X=0;
			eye1Y=0;
			eye2X=0;
			eye2Y=0;
			eyeDia=0;

			mouthX=0;
			mouthY=0;
			mouthDia=0;

			bodyX=10;
			bodyY=16;
			bodyLength=10;
			bodyBreadth=14;

			hand1X=2;
			hand1Y=20;
			hand2X=18;
			hand2Y=20;
			handLength=8;
			handBreadth=3;

			leg1X=10;
			leg1Y=24;
			leg2X=17;
			leg2Y=24;
			legLength=3;
			legBreadth=8;

		 }
		 public Bunny(int right,int right1)
		 {
			ear1X=10;
			ear1Y=ear2Y=0;
			ear2X=10;
			earLength=3;
			earBreadth=8;

			headX=10;
			headY=8;
			headLength=6;
			headBreadth=10;

			eye1X=12;
			eye1Y=10;
			eye2X=12;
			eye2Y=10;
			eyeDia=0;

			mouthX=13;
			mouthY=12;
			mouthDia=0;

			bodyX=12;
			bodyY=16;
			bodyLength=6;
			bodyBreadth=14;

			hand1X=14;
			hand1Y=20;
			hand2X=14;
			hand2Y=20;
			handLength=8;
			handBreadth=3;

			leg1X=12;
			leg1Y=24;
			leg2X=12;
			leg2Y=24;
			legLength=3;
			legBreadth=8;
		 }
		 public Bunny(String left)
		 {
			ear1X=12;
			ear1Y=ear2Y=0;
			ear2X=12;
			earLength=3;
			earBreadth=8;

			headX=10;
			headY=8;
			headLength=6;
			headBreadth=10;

			eye1X=12;
			eye1Y=10;
			eye2X=12;
			eye2Y=10;
			eyeDia=0;

			mouthX=13;
			mouthY=12;
			mouthDia=0;

			bodyX=10;
			bodyY=16;
			bodyLength=6;
			bodyBreadth=14;

			hand1X=2;
			hand1Y=20;
			hand2X=4;
			hand2Y=20;
			handLength=8;
			handBreadth=3;

			leg1X=12;
			leg1Y=25;
			leg2X=12;
			leg2Y=25;
			legLength=3;
			legBreadth=8;
		 }
		 public Bunny()
		 {
			ear1X=10;
			ear1Y=ear2Y=0;
			ear2X=17;
			earLength=3;
			earBreadth=8;

			headX=10;
			headY=8;
			headLength=10;
			headBreadth=10;

			eye1X=12;
			eye1Y=10;
			eye2X=16;
			eye2Y=10;
			eyeDia=2;

			mouthX=13;
			mouthY=12;
			mouthDia=4;

			bodyX=10;
			bodyY=16;
			bodyLength=10;
			bodyBreadth=14;

			hand1X=2;
			hand1Y=20;
			hand2X=18;
			hand2Y=20;
			handLength=8;
			handBreadth=3;

			leg1X=10;
			leg1Y=24;
			leg2X=17;
			leg2Y=24;
			legLength=3;
			legBreadth=8;

		 }

		 public void drawBunny(Graphics g,int currentX,int currentY)
		 {
			g.drawArc(currentX+ear1X,currentY+ear1Y,earLength,earBreadth,0,360);
			g.drawArc(currentX+ear2X,currentY+ear2Y,earLength,earBreadth,0,360);

			g.drawArc(currentX+headX,currentY+headY,headLength,headBreadth,0,360);

			g.drawArc(currentX+eye1X,currentY+eye1Y,eyeDia,eyeDia,0,360);
			g.drawArc(currentX+eye2X,currentY+eye2Y,eyeDia,eyeDia,0,360);

			g.drawArc(currentX+mouthX,currentY+mouthY,mouthDia,mouthDia,0,360);

			g.drawArc(currentX+hand1X,currentY+hand1Y,handLength,handBreadth,0,360);
			g.drawArc(currentX+hand2X,currentY+hand2Y,handLength,handBreadth,0,360);

			g.drawArc(currentX+bodyX,currentY+bodyY,bodyLength,bodyBreadth,0,360);

			g.drawArc(currentX+leg1X,currentY+leg1Y,legLength,legBreadth,0,360);
			g.drawArc(currentX+leg2X,currentY+leg2Y,legLength,legBreadth,0,360);
		 }
		}// bunny class


		class Square
		{
		 private int squareX,squareY,squareSide;
		 private boolean isEndBox;
		 public Square(int xPos,int yPos)
		 {
			squareX=xPos;
			squareY=yPos;
			squareSide=20;
			isEndBox=false;
		 }
		 public Square(int xPos,int yPos,boolean boo)
		 {
			squareX=xPos;
			squareY=yPos;
			squareSide=20;
			isEndBox=true;
		 }
		 public void drawSquare(Graphics g)
		 {
			g.drawRect(squareX+5,squareY+5,squareSide,squareSide);
			if(!isEndBox)
			{
			 g.setColor (0, 0, 255);
			 g.fillRect(squareX+5,squareY+5,squareSide,squareSide);
			}
			if(isEndBox)
			{
			 g.drawArc(squareX,squareY,squareSide+10,squareSide+10,0,360);
			 g.setColor(0x008000);
			 g.drawArc(squareX+3,squareY+10,10,10,0,360);
			 g.fillArc(squareX + 3, squareY + 10, 10, 10,0, 360);
			 g.drawArc(squareX + 13, squareY + 10, 20, 10,0,360);
			 g.setColor(0xFF0000);
			 g.fillArc(squareX + 13, squareY + 10, 20, 10,0, 360);
			}
		 }
		 public int getX()
		 {
			return squareX;
		 }
		 public int getY()
		 {
			return squareY;
		 }
		}// square class

		private Command pauseCommand,backCommand;
		private int currentLevel;
		private Bunny curBunny,rightBunny,leftBunny,frontBunny,backBunny;
		private Square squares[];
		public Square destination;
		private winThread checkThread;
		private String levelMaze;
		public int totalMoves=0;
		private int squareCount=0;
		public int xPos=0,yPos=0;
		private int AppSideX=210,AppSideY=270;
		public GameCanvas()
		{
			pauseCommand=new Command("Pause",Command.SCREEN,1);
			backCommand=new Command("Back",Command.BACK,1);
			currentLevel=-1;

			frontBunny = new Bunny();//front
			backBunny = new Bunny(1);//back
			leftBunny = new Bunny("left");
			rightBunny = new Bunny(1, 1);//right

			curBunny = frontBunny;
			addCommand(pauseCommand);
			addCommand(backCommand);
			setCommandListener(this);
			System.out.println("in canvas constructor");
		}
		public void paint(Graphics g){
			g.setColor(0xFFFFFF);
			g.drawRect(0, 0, 300, getHeight());
			g.fillRect(0, 0, 300, getHeight());
			g.setColor(0, 0, 255);
			curBunny.drawBunny(g,xPos,yPos);
			for (int i = 0; i < squareCount; i++)
			{
				squares[i].drawSquare(g);
			}
		}
		public void setX(int x)
		{
			xPos = x;
		}
		public void setY(int y)
		{
			yPos=y;
		}
		public void setLevel(int level)
		{
			currentLevel=level;
			setX(0);
			setY(0);
			System.out.println("in set level-- got level="+level);
			if(currentLevel!=-1)
			{
				try {
					InputStream in = this.getClass().getResourceAsStream("/mymazes.maz");
				    byte[] buffer = new byte[2048];
				    while (in.read(buffer) != -1) 
					{
				        String lineMaze,line = new String(buffer);
				        //System.out.println("line read="+line);
				   		int len=("level:"+currentLevel+"$maze:").length();
				   		int index=line.indexOf("level:"+currentLevel+"$");
				   		if(index>=0)
				   		{
				   		 levelMaze=line.substring(index+len,line.length());
				   		 createMaze(levelMaze);
						 System.out.println("Maze Created ");
				   		 break;
				   		}
				    }
				   } catch (Exception e) {
					   System.out.println("Exception got in setLevel");
				    e.printStackTrace();
				  }

			}
		}
		public void createMaze(String input)
		{
			int i=0;
			squareCount=0;
			totalMoves = 0;
			System.out.println("length="+input.length());
			while(i<input.length())
			{
			 if((input.charAt(i)=='1') || (input.charAt(i)=='d') )
			 {
				squareCount++;
			 }
			if(input.charAt(i)=='e')
			{
				break;
			}
			 i++;
			}// while count
			squares=new Square[squareCount];
			System.out.println("Square Count="+squareCount);
			i=0;
			int j=0;
			while(i<input.length())
			{
			 if(input.charAt(i)=='1')
			 {
				squares[j]=new Square((i%10)*30,((i/10)*30));
				j++;
			 }
			 if(input.charAt(i)=='d')
			 {
				squares[j]=new Square((i%10)*30,((i/10)*30),true);
				destination=squares[j];
				System.out.println("Destination Number="+j);
				j++;
			 }
			if(input.charAt(i)=='e')
			{
				break;
			}
			 i++;
			}
			checkThread = new winThread(this);
			playSound("start");
		}
		public void commandAction(Command c,Displayable s)
		{
			if(c==pauseCommand)
			{
				Alert alert;
				try
				{
					pausestore = RecordStore.openRecordStore("myPauseStore", true);//"level:1$xPos:30$yPos:60"
				}
				catch (Exception error)
				{
					alert = new Alert("Error Creating", error.toString(), null, AlertType.WARNING);
					alert.setTimeout(Alert.FOREVER);
					display.setCurrent(alert);
				}
				try
				{
					String outputData = "level:"+currentLevel+"$xPos:"+xPos+"$yPos:"+yPos;
					byte[] byteOutputData = outputData.getBytes();
					pausestore.addRecord(byteOutputData, 0, byteOutputData.length);
					checkThread.stop();
				}
				catch (Exception error)
				{
					alert = new Alert("Error Writing", error.toString(), null, AlertType.WARNING);
					alert.setTimeout(Alert.FOREVER);
					display.setCurrent(alert);
				}

				try
				{
					pausestore.closeRecordStore();
				}
				catch (Exception error)
				{
					alert = new Alert("Error Closing", error.toString(), null, AlertType.WARNING);
					alert.setTimeout(Alert.FOREVER);
					display.setCurrent(alert);
				}
			}
			if(c==backCommand)
			{
				display.setCurrent(levelList);
			}
		}
		public void playSound(String soundName)
		{
		   if(onSound)
		   {
		  	try 
			{ 
				InputStream is = getClass().getResourceAsStream("/"+soundName+".wav"); 
				Player p = Manager.createPlayer(is, "audio/X-wav"); 
				p.start(); 
			}
			catch (IOException ioe) {
			System.out.println("IOException playing sound...");
			} 
			catch (MediaException me) {
			System.out.println("MeidaException playing sound...");
			}
		  }
		}
		public void keyPressed(int key)
		{
			System.out.println("Pressed Key="+key);
			switch (key)
			{
				case -1:
				case 2:
					{
						curBunny = frontBunny;
						if (yPos - 30 >= 0 && !isAnyUpSquare())
						{
							yPos -= 30;
						}
						totalMoves++;
						break;
					}
				case -2:
				case 8:
					{
						curBunny = backBunny;
						if (yPos + 30 < AppSideY && !isAnyDownSquare())
						{
							yPos += 30;
						}
						totalMoves++;
						break;
					}
				case -4:
				case 6:
					{
						curBunny = rightBunny;
						if (xPos + 30 < AppSideX + 30 && !isAnyRightSquare())
						{
							xPos += 30;
						}
						totalMoves++;
						break;
					}
				case -3:
				case 4:
					{
						curBunny = leftBunny;
						if (xPos - 30 >= 0 && !isAnyLeftSquare())
						{
							xPos -= 30;
						}
						totalMoves++;
						break;
					}
				default:
					{
						System.out.println("Some other Key Pressed");
						break;
					}
			}// switch
			repaint();
			System.out.println("Key Pressed ending");
		}
		public void keyReleased(int key)
		{
			curBunny = frontBunny;
			repaint();
			System.out.println("Key Released...");
		}
		public void keyTyped(int key)
		{
			System.out.println("Key Typing Not Allowed");
		}
	
		private boolean isAnyUpSquare()
		{
			boolean found = false;
			for (int i = 0; i < squareCount; i++)
			{
				if ((yPos - 30 >= squares[i].getY()) && (yPos <= (squares[i].getY() + 30)) && (yPos != squares[i].getY()))
				{
					if (xPos >= squares[i].getX() && xPos + 30 <= squares[i].getX() + 30)
					{
						if (squares[i] != destination)
						{
							System.out.println("got an up square dude==" + i);
							found = true;
							playSound("hit");
							break;
						}
					}
				}
			}// for
			return found;
		}
		private boolean isAnyDownSquare()
		{
			boolean found = false;
			for (int i = 0; i < squareCount; i++)
			{
				if (((yPos + 30) >= squares[i].getY()) && ((yPos + 30) <= (squares[i].getY() + 30)) && (yPos != squares[i].getY()))
				{
					if (xPos >= squares[i].getX() && xPos + 30 <= squares[i].getX() + 30)
					{
						if (squares[i] != destination)
						{
							System.out.println("got a down square dude==" + i);
							found = true;
							playSound("hit");
							break;
						}
					}
				}
			}// for
			return found;
		}

		private boolean isAnyRightSquare()
		{
			boolean found = false;
			for (int i = 0; i < squareCount; i++)
			{
				if (xPos + 30 >= squares[i].getX() && xPos + 30 <= squares[i].getX() + 30)
				{
					if (yPos >= squares[i].getY() && yPos + 30 <= squares[i].getY() + 30)
					{
						if (squares[i] != destination)
						{
							System.out.println("got a right square dude==" + i);
							found = true;
							playSound("hit");
							break;
						}
					}
				}
			}
			return found;
		}

		private boolean isAnyLeftSquare()
		{
			boolean found = false;
			for (int i = 0; i < squareCount; i++)
			{
				if (xPos - 30 >= squares[i].getX() && xPos <= squares[i].getX() + 30)
				{
					if (yPos >= squares[i].getY() && yPos + 30 <= squares[i].getY() + 30)
					{
						if (squares[i] != destination)
						{
							System.out.println("got a left square dude==" + i);
							found = true;
							playSound("hit");
							break;
						}
					}
				}
			}
			return found;
		}
	}// canvas
	class winThread extends Thread
	{
		private Form resultForm;
		private int timer;
		private boolean isRunning;
		private RecordStore recordstore;
		private GameCanvas myCaller;
		public winThread(GameCanvas cal)
		{
			super("Win Thread");
			myCaller = cal;
			resultForm = new Form("Results");
			this.start();
			System.out.println("Thread Started...");
			isRunning=true;
		}
		public void stop()
		{
			isRunning=false;
		}
		public void run()
		{
			System.out.println("inside run");
			timer = 0;
			while (isRunning)
			{
				try
				{
					sleep(1000);
					timer++;
				}
				catch (InterruptedException ie)
				{
					System.out.println("Sleep Interrupted");
				}
				if (myCaller.destination != null && myCaller.xPos == myCaller.destination.getX() && myCaller.yPos == myCaller.destination.getY())
				{
					//showStatus("You won the Game in " + timer + "seconds");
					int score;
					System.out.println("You won the Game in " + timer + "seconds with "+ myCaller.totalMoves + "moves");
					Alert alert = new Alert("Congrajulations", "You won The game", null, null);
					alert.setTimeout(Alert.FOREVER);
					alert.setType(AlertType.INFO);
					display.setCurrent(alert);
					myCaller.playSound("end");
					resultForm.append("Total Moves=" + myCaller.totalMoves + "\n");
					resultForm.append("Time Taken=" + timer + "\n");
					resultForm.append("Score=" + (score=(100-myCaller.totalMoves)*(100-timer)) + "\n");
					// write score into file
				try
				{
					recordstore = RecordStore.openRecordStore("myScoreStore", true);
				}
				catch (Exception error)
				{
					alert = new Alert("Error Creating", error.toString(), null, AlertType.WARNING);
					alert.setTimeout(Alert.FOREVER);
					display.setCurrent(alert);
				}
				try
				{
					String outputData = "score:"+score+" date:"+(new Date()).toString();
					byte[] byteOutputData = outputData.getBytes();
					recordstore.addRecord(byteOutputData, 0, byteOutputData.length);
				}
				catch (Exception error)
				{
					alert = new Alert("Error Writing", error.toString(), null, AlertType.WARNING);
					alert.setTimeout(Alert.FOREVER);
					display.setCurrent(alert);
				}

				try
				{
					recordstore.closeRecordStore();
				}
				catch (Exception error)
				{
					alert = new Alert("Error Closing", error.toString(), null, AlertType.WARNING);
					alert.setTimeout(Alert.FOREVER);
					display.setCurrent(alert);
				}
					display.setCurrent(resultForm);
					try
					{
						sleep(3000);
					}
					catch (InterruptedException e)
					{
					}
					display.setCurrent(levelList);
					break;
				}
			}
		}
	}// winThread
}// main class
