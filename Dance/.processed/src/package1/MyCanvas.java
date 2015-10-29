package package1;

import java.io.IOException;
import java.util.Random;
import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.lcdui.game.LayerManager;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.lcdui.game.TiledLayer;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;

public class MyCanvas  extends GameCanvas implements Runnable{
	///---------------------------<全局常量>--------------------
	
	private final int cy=75;	//小孩图标Y坐标
	private final int oy=39;	//老人图层Y坐标
	private final int py=48;	//指示灯图层Y坐标
	protected final int sw=176;	//当前游戏宽度
	protected final int sh=220;	//当前游戏高度
	
	///------------------------------<图片>--------------
	
	private Image bg=null;	//背景
	private Image oready=null;	//老人准备图
	private Image o1=null;
	private Image o3=null;
	private Image o7=null;
	private Image o9=null;
	private Image lift=null;		//老人头
	private Image liftbg=null;		//老人头背景片段
	private Image pass=null;		//指示灯亮
	private Image notpass=null;		//错误指示
	private Image passbg=null;		//指示灯暗
	private Image passarea=null;	//指示灯背景片段1
	private Image scorearea=null;	//指示灯背景片段2
	private Image n0=null,n1=null,n2=null,n3=null,n4=null,n5=null,n6=null,n7=null,n8=null,n9=null;//数字图片
	
	///-------------------程序控件相关-------------------------------	
	private GIFDecode gifd=new GIFDecode();	//GIFDecode处理实例
	private Command cmdok=new Command("确定", Command.OK, 1);
	private final int anchor=Graphics.LEFT|Graphics.TOP;	//所有基点为左上
	private final int FrameInterval=50;	//所有帧之间的间隔时间(毫秒)
	private final int ActionInterval=200;	//所有动作之间的间隔时间
	private final int TaskWait=1000;		//任务等候时间
	
	static class Dir {	//方向"枚举"
        public static final Dir d1 = new Dir();
        public static final Dir d3 = new Dir();
        public static final Dir d7 = new Dir();
        public static final Dir d9 = new Dir();
        public static final Dir ready = new Dir();	//准备动作
	}
	static class Light{	//灯状态"枚举"
		public static final Light right=new Light();
		public static final Light error=new Light();
		public static final Light dark=new Light();
	}
	//----------------------当前游戏状态相关--------------------------------
	private boolean EnableKey=false;	//禁用键开关,默认禁用
	private Vector vector=new Vector();		//按键集合
	private int CurrentPressindex=0;	//当前按键序列
	private boolean isKeydown=false;		//键按下的状态,默认为否
	private int ActionCount=2;//动作次数
	private int ActionRepeat=6;//重复此种动作次数
	private int currentLift=3;		//当前生命数
	private int currentScore=0;		//当前分数
	private int rightcount=0;	//当前老人跳对次数
	private boolean ispress=false;	//玩家是否在小孩等待一秒后按键

	public Thread thgame=new Thread(this);	//主线程
	private boolean exit=false;//线程中止,默认不退出
	///--------------------------------------------------
	
	protected MyCanvas(boolean suppressKeyEvents) {
		super(suppressKeyEvents);
		// TODO Auto-generated constructor stub
		try {
			bg=Image.createImage("/bg.GIF");
			oready=Image.createImage("/oready.GIF");
			o1=Image.createImage("/o1.GIF");
			o3=Image.createImage("/o3.GIF");
			o7=Image.createImage("/o7.GIF");
			o9=Image.createImage("/o9.GIF");
			lift=Image.createImage("/lift.GIF");
			liftbg=Image.createImage("/liftbg.GIF");
			pass=Image.createImage("/pass.GIF");
			notpass=Image.createImage("/notpass.GIF");
			passbg=Image.createImage("/passbg.GIF");
			passarea=Image.createImage("/passarea.GIF");
			scorearea=Image.createImage("/scorearea.GIF");
			n0=Image.createImage("/0.GIF");
			n1=Image.createImage("/1.GIF");
			n2=Image.createImage("/2.GIF");
			n3=Image.createImage("/3.GIF");
			n4=Image.createImage("/4.GIF");
			n5=Image.createImage("/5.GIF");
			n6=Image.createImage("/6.GIF");
			n7=Image.createImage("/7.GIF");
			n8=Image.createImage("/8.GIF");
			n9=Image.createImage("/9.GIF");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("获取图片异常");
		}
		
		init();	//初始化游戏界面
	}
	
	/**
	 * 游戏线程
	 */
	public void run() {
		// TODO Auto-generated method stub
		try {
			Thread.sleep(1000);	//游戏启动后停顿时间
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while(true)
		{
			for(int j=ActionRepeat;j>=4;j-=(j>4?2:0))
			{
				for(int i=j;i>=1;i--)	//重复此种难度的次数最多6次,最少4次
				{
					try
					{
						//-------新一轮初始化
						ispress=false;		//恢复按键状态
						EnableKey=false;		//小孩跳舞期间禁用键盘监听
						CurrentPressindex=0;	//恢复按键次数
						rightcount=0;			//恢复老人跳对次数
						DrawOld(Dir.ready);		//恢复老人动作
						Dance(ActionCount);		//小孩开始跳
					}
					catch(InterruptedException e)
					{
						e.printStackTrace();
					}
					EnableKey=true; //启用键盘监听
					
					while(CurrentPressindex<ActionCount)
					{
						try {
							Thread.sleep(1000);//等1秒后验证玩家是否按键
							if(!ispress)
							{
								//如果没有按键则默认老人失败
								Fail();
								if(exit)
								{
									return;
								}
							}
							else
							{
								//如果用户已经按键
								ispress=false;//继续验证
							}
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					try {
						Thread.sleep(TaskWait);	//老人任务完成等待小孩开始新一轮任务
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				ActionCount+=(ActionCount<ActionRepeat?1:0);	//此动作完成难度加1
			}
		}
	}
	/**
	 * (覆盖基类按键事件)判断老人是否按对
	 */
	protected void keyPressed(int keyCode)
	{
		if(!EnableKey||CurrentPressindex>=ActionCount)
		{
			return;//禁用期间和超出动作数期间的按键不响应
		}
		isKeydown=true;
		switch(keyCode)
		{
			case KEY_NUM1:
				if(vector.elementAt(CurrentPressindex)==Dir.d1)//正确
				{
					DrawOld(Dir.d1);
					rightcount++;	//做对次数加一
					doRight(rightcount);
					currentScore++;
					ShowScore(currentScore);
				}
				else
				{
					Fail();
				}
				CurrentPressindex++;
				ispress=true;
				break;
			case KEY_NUM3:
				if(vector.elementAt(CurrentPressindex)==Dir.d3)
				{
					DrawOld(Dir.d3);
					rightcount++;	//做对次数加一
					doRight(rightcount);
					currentScore++;
					ShowScore(currentScore);
				}
				else
				{
					Fail();
				}
				CurrentPressindex++;
				ispress=true;
				break;
			case KEY_NUM7:
				if(vector.elementAt(CurrentPressindex)==Dir.d7)
				{
					DrawOld(Dir.d7);
					rightcount++;	//做对次数加一
					doRight(rightcount);
					currentScore++;
					ShowScore(currentScore);
				}
				else
				{
					Fail();
				}
				CurrentPressindex++;
				ispress=true;
				break;
			case KEY_NUM9:
				if(vector.elementAt(CurrentPressindex)==Dir.d9)
				{
					DrawOld(Dir.d9);
					rightcount++;	//做对次数加一
					doRight(rightcount);
					currentScore++;
					ShowScore(currentScore);
				}
				else
				{
					Fail();
				}
				CurrentPressindex++;
				ispress=true;
				break;
				default:
					break;
		}
	}
	/**
	 * 游戏初始化
	 */
	private void init()
	{	
		Graphics gra=getGraphics();
		gra.drawImage(bg, 0, 0, anchor);	//背景
		ShowLift(3);//画三个老人头
		ShowScore(0);
		flushGraphics();
	}
	/**
	 * 记录此次游戏记录
	 * @throws RecordStoreException 
	 * @throws RecordStoreFullException 
	 * @throws RecordStoreNotOpenException 
	 */
	private void record() throws Exception
	{
		RecordStore rs=RecordStore.openRecordStore("Dance", true);
		String score=currentScore+"";	//分数
		String actioncount=""+ActionCount;
		
		byte[] socrebyty=score.getBytes();
		rs.addRecord(socrebyty, 0, score.length());
	}
	/**
	 * 读取上次游戏记录
	 * @throws RecordStoreException 
	 * @throws RecordStoreNotFoundException 
	 * @throws RecordStoreFullException 
	 */
	private void readhistory() throws Exception
	{
		RecordStore rs=RecordStore.openRecordStore("Dance", false);
	}
	/*
	 * 参数
	 */
	int framecount=0;	//gif图像帧数
	String prepath="";	//上次图像路径
	/**
	 * 画GIF图像
	 * @param imgpath 图像路径
	 * @param x		x坐标
	 * @param y		y坐标
	 */
	private void DrawGif(String imgpath,int x,int y)
	{
		Graphics g=getGraphics();
		if(imgpath!=prepath)
		{
			prepath=imgpath;	//把当前图像路径赋予前一个
			gifd.read(this.getClass().getResourceAsStream(imgpath));//载入图片
			framecount= gifd.getFrameCount();//获取帧数
		}
		for(int i=0;i<framecount;i++)
		{
			g.drawImage(gifd.getFrame(i), x, y, anchor);
			try {
				Thread.sleep(FrameInterval);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			flushGraphics();
		}
	}
	/**
	 * 画老人头
	 * @param n 要画的个数
	 */
	private void ShowLift(int n)
	{
		int startpos=0;
		Graphics grap=getGraphics();
		if(n!=3)
		{
			//如果是画三个说明是初始化,不是三个就要清除背景
			grap.drawImage(liftbg, 0, 0, anchor);	///老人头清除背景
		}
		for(int i=1;i<=n;i++)
		{
			grap.drawImage(lift, startpos, 0, anchor);	//每次画一个右移17像素
			startpos+=17;
		}
		flushGraphics();
	}
	/**
	 * 画老人动作
	 * @param dir 方向
	 */
	private void DrawOld(Dir dir)
	{
		Graphics gra=getGraphics();
		int x=sw-o1.getWidth();
		if(dir==Dir.d1)
		{
			gra.drawImage(o1, x, oy, anchor);
		}
		else if(dir==Dir.d3)
		{
			gra.drawImage(o3, x, oy, anchor);
		}
		else if(dir==Dir.d7)
		{
			gra.drawImage(o7, x, oy, anchor);
		}
		else if(dir==Dir.d9)
		{
			gra.drawImage(o9, x, oy, anchor);
		}
		else if(dir==Dir.ready)
		{
			gra.drawImage(oready, x, oy, anchor);
		}
		flushGraphics();
	}
	/**
	 * 画小孩动作
	 * @param dir 方向
	 */
	private void DrawChild(Dir dir)
	{
		if(dir==Dir.d1)
		{
			DrawGif("/c1.GIF", 0, cy);	//小孩1键动作
		}
		else if(dir==Dir.d3)
		{
			DrawGif("/c3.GIF", 0, cy);
		}
		else if(dir==Dir.d7)
		{
			DrawGif("/c7.GIF", 0, cy);
		}
		else if(dir==Dir.d9)
		{
			DrawGif("/c9.GIF", 0, cy);
		}
		else if(dir==Dir.ready)
		{
			DrawGif("/cready.GIF", 0, cy);	//小孩准备动作
		}
	}
	/**
	 * 显示分数
	 * @param score 分数
	 */
	protected void ShowScore(int score)
	{
		Graphics gra=getGraphics();
		Image[] imgarr=new Image[]{n0,n1,n2,n3,n4,n5,n6,n7,n8,n9};
		int num0=0,num1=0,num2=0,num3=0;//个十百千
		int startposx=0,startposy;
		if(!exit)
		{
			//在游戏中显示
			startposx=40;
			startposy=0;
			gra.drawImage(scorearea, sw-startposx, 0, anchor);	//换新背景
		}
		else
		{
			startposx=68;
			startposy=71;
			//游戏结束显示
		}
		if(score<10)
		{
			//一位数
			num0=score;
			gra.drawImage(imgarr[num0], sw-startposx, startposy, anchor);
		}
		else if(score>9&&score<100)
		{
			//两位数
			num0=score%10;
			num1=(score-num0)/10;
			gra.drawImage(imgarr[num1], sw-startposx, startposy, anchor);
			gra.drawImage(imgarr[num0], sw-startposx-10, startposy, anchor);
		}
		else if(score>99&&score<1000) 
		{
			num0=score%10;
			int temp=(score-num0)/10;	//个位为0时的十分之一
			num1=temp%10;
			num2=(temp-num1)/10;
			
			gra.drawImage(imgarr[num2], sw-startposx, startposy, anchor);
			gra.drawImage(imgarr[num1], sw-startposx-10, startposy, anchor);
			gra.drawImage(imgarr[num0], sw-startposx-20, startposy, anchor);
		}
		else if(score>999&&score<10000) 
		{
			num0=score%10;	//个位,如1234,此时4
			int temp1=(score-num0)/10;	//个位为0时的十分之一,此时123
			num1=temp1%10;	//十位,3
			int temp2=(temp1-num1)/10; //12
			num2=temp2%10;	//2
			num3=(temp2-num2)/10;
			gra.drawImage(imgarr[num3], sw-startposx, startposy, anchor);
			gra.drawImage(imgarr[num2], sw-startposx-10, startposy, anchor);
			gra.drawImage(imgarr[num1], sw-startposx-20, startposy, anchor);
			gra.drawImage(imgarr[num0], sw-startposx-30, startposy, anchor);
		}
		flushGraphics();
	}
	/**
	 * 小孩做动作数
	 * @param n 动作次数(难度)
	 * @throws InterruptedException 
	 */
	private void Dance(int n) throws InterruptedException
	{
		vector.removeAllElements();	//清空原有按键信息
		Random rand=new Random();	//随机数
		int dircode=0;
		int count=1;	//计次
		ShowLightBg(Light.dark,0);	//把灯全部去掉
		for(int i=1;i<=5;i++)
		{
			DrawChild(Dir.ready);	//准备动作
			Thread.sleep(5);
		}
		Thread.sleep(400); //间隔准备动作与开始动作间隔
		while(count<=n)
		{
			ShowLightBg(Light.dark,count);	//显示暗色灯
			dircode=rand.nextInt(4);	//产生随机数 区间[0,4)
			switch(dircode)
			{
			case 0://1键
				DrawChild(Dir.d1);
				vector.addElement(Dir.d1);
				break;
			case 1://3键
				DrawChild(Dir.d3);
				vector.addElement(Dir.d3);
				break;
			case 2://7键
				DrawChild(Dir.d7);
				vector.addElement(Dir.d7);
				break;
			case 3://9键
				DrawChild(Dir.d9);
				vector.addElement(Dir.d9);
				break;
			}
			count++;
			flushGraphics();
			Thread.sleep(ActionInterval);//动作间隔
		}
	}
	/**
	 * 显示暗色的灯
	 * @param state 灯泡状态
	 * @param n		显示的数量
	 */
	private void ShowLightBg(Light state,int n)
	{
		Graphics gra=getGraphics();
		gra.drawImage(passarea, 0, py, anchor);	//换新背景
		int startpos=0;
		for(int i=1;i<=n;i++)
		{
			if(state==Light.dark)
			{
				gra.drawImage(passbg, startpos, py, anchor);
			}
			startpos+=15;	//水平偏移一个图片宽
		}
		flushGraphics();
	}
	/**
	 * 老人动作失败
	 */
	private void Fail()
	{
		ShowLightBg(Light.dark, ActionCount);//带有暗色灯背景
		Graphics gra=getGraphics();
		int startpos=0;
		for(int i=1;i<=rightcount;i++)
		{
			gra.drawImage(pass, startpos, py, anchor);
			startpos+=15;
		}
		gra.drawImage(notpass, 15*rightcount, py, anchor);	//绘制错误灯
		flushGraphics();
		currentLift--;	//生命数减1
		ShowLift(currentLift);	//显示生命数
		if(currentLift==0)
		{
			//GameOver
			Gameover();
		}
		else
		{
			CurrentPressindex=ActionCount;	//使小孩继续跳舞
		}
	}
	/**
	 * 老人动作做对的指示
	 * @param n 做对的次数
	 */
	private void doRight(int n)
	{
		int startpos=0;
		Graphics gra=getGraphics();
		for(int i=1;i<=n;i++)
		{
			gra.drawImage(pass, startpos, py, anchor);
			startpos+=15;//水平偏移一个图片宽
		}
		for(int i=1;i<=ActionCount-n;i++)
		{
			gra.drawImage(passbg, startpos, py, anchor);
		}
		flushGraphics();
	}
	private void Gameover()
	{
		exit=true;
		try
		{
			(new CanvasOver()).init(currentScore);
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}
		System.out.println("Gameover");
	}
}
