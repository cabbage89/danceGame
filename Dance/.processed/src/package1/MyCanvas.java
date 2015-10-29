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
	///---------------------------<ȫ�ֳ���>--------------------
	
	private final int cy=75;	//С��ͼ��Y����
	private final int oy=39;	//����ͼ��Y����
	private final int py=48;	//ָʾ��ͼ��Y����
	protected final int sw=176;	//��ǰ��Ϸ���
	protected final int sh=220;	//��ǰ��Ϸ�߶�
	
	///------------------------------<ͼƬ>--------------
	
	private Image bg=null;	//����
	private Image oready=null;	//����׼��ͼ
	private Image o1=null;
	private Image o3=null;
	private Image o7=null;
	private Image o9=null;
	private Image lift=null;		//����ͷ
	private Image liftbg=null;		//����ͷ����Ƭ��
	private Image pass=null;		//ָʾ����
	private Image notpass=null;		//����ָʾ
	private Image passbg=null;		//ָʾ�ư�
	private Image passarea=null;	//ָʾ�Ʊ���Ƭ��1
	private Image scorearea=null;	//ָʾ�Ʊ���Ƭ��2
	private Image n0=null,n1=null,n2=null,n3=null,n4=null,n5=null,n6=null,n7=null,n8=null,n9=null;//����ͼƬ
	
	///-------------------����ؼ����-------------------------------	
	private GIFDecode gifd=new GIFDecode();	//GIFDecode����ʵ��
	private Command cmdok=new Command("ȷ��", Command.OK, 1);
	private final int anchor=Graphics.LEFT|Graphics.TOP;	//���л���Ϊ����
	private final int FrameInterval=50;	//����֮֡��ļ��ʱ��(����)
	private final int ActionInterval=200;	//���ж���֮��ļ��ʱ��
	private final int TaskWait=1000;		//����Ⱥ�ʱ��
	
	static class Dir {	//����"ö��"
        public static final Dir d1 = new Dir();
        public static final Dir d3 = new Dir();
        public static final Dir d7 = new Dir();
        public static final Dir d9 = new Dir();
        public static final Dir ready = new Dir();	//׼������
	}
	static class Light{	//��״̬"ö��"
		public static final Light right=new Light();
		public static final Light error=new Light();
		public static final Light dark=new Light();
	}
	//----------------------��ǰ��Ϸ״̬���--------------------------------
	private boolean EnableKey=false;	//���ü�����,Ĭ�Ͻ���
	private Vector vector=new Vector();		//��������
	private int CurrentPressindex=0;	//��ǰ��������
	private boolean isKeydown=false;		//�����µ�״̬,Ĭ��Ϊ��
	private int ActionCount=2;//��������
	private int ActionRepeat=6;//�ظ����ֶ�������
	private int currentLift=3;		//��ǰ������
	private int currentScore=0;		//��ǰ����
	private int rightcount=0;	//��ǰ�������Դ���
	private boolean ispress=false;	//����Ƿ���С���ȴ�һ��󰴼�

	public Thread thgame=new Thread(this);	//���߳�
	private boolean exit=false;//�߳���ֹ,Ĭ�ϲ��˳�
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
			System.out.println("��ȡͼƬ�쳣");
		}
		
		init();	//��ʼ����Ϸ����
	}
	
	/**
	 * ��Ϸ�߳�
	 */
	public void run() {
		// TODO Auto-generated method stub
		try {
			Thread.sleep(1000);	//��Ϸ������ͣ��ʱ��
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while(true)
		{
			for(int j=ActionRepeat;j>=4;j-=(j>4?2:0))
			{
				for(int i=j;i>=1;i--)	//�ظ������ѶȵĴ������6��,����4��
				{
					try
					{
						//-------��һ�ֳ�ʼ��
						ispress=false;		//�ָ�����״̬
						EnableKey=false;		//С�������ڼ���ü��̼���
						CurrentPressindex=0;	//�ָ���������
						rightcount=0;			//�ָ��������Դ���
						DrawOld(Dir.ready);		//�ָ����˶���
						Dance(ActionCount);		//С����ʼ��
					}
					catch(InterruptedException e)
					{
						e.printStackTrace();
					}
					EnableKey=true; //���ü��̼���
					
					while(CurrentPressindex<ActionCount)
					{
						try {
							Thread.sleep(1000);//��1�����֤����Ƿ񰴼�
							if(!ispress)
							{
								//���û�а�����Ĭ������ʧ��
								Fail();
								if(exit)
								{
									return;
								}
							}
							else
							{
								//����û��Ѿ�����
								ispress=false;//������֤
							}
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					try {
						Thread.sleep(TaskWait);	//����������ɵȴ�С����ʼ��һ������
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				ActionCount+=(ActionCount<ActionRepeat?1:0);	//�˶�������Ѷȼ�1
			}
		}
	}
	/**
	 * (���ǻ��ఴ���¼�)�ж������Ƿ񰴶�
	 */
	protected void keyPressed(int keyCode)
	{
		if(!EnableKey||CurrentPressindex>=ActionCount)
		{
			return;//�����ڼ�ͳ����������ڼ�İ�������Ӧ
		}
		isKeydown=true;
		switch(keyCode)
		{
			case KEY_NUM1:
				if(vector.elementAt(CurrentPressindex)==Dir.d1)//��ȷ
				{
					DrawOld(Dir.d1);
					rightcount++;	//���Դ�����һ
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
					rightcount++;	//���Դ�����һ
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
					rightcount++;	//���Դ�����һ
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
					rightcount++;	//���Դ�����һ
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
	 * ��Ϸ��ʼ��
	 */
	private void init()
	{	
		Graphics gra=getGraphics();
		gra.drawImage(bg, 0, 0, anchor);	//����
		ShowLift(3);//����������ͷ
		ShowScore(0);
		flushGraphics();
	}
	/**
	 * ��¼�˴���Ϸ��¼
	 * @throws RecordStoreException 
	 * @throws RecordStoreFullException 
	 * @throws RecordStoreNotOpenException 
	 */
	private void record() throws Exception
	{
		RecordStore rs=RecordStore.openRecordStore("Dance", true);
		String score=currentScore+"";	//����
		String actioncount=""+ActionCount;
		
		byte[] socrebyty=score.getBytes();
		rs.addRecord(socrebyty, 0, score.length());
	}
	/**
	 * ��ȡ�ϴ���Ϸ��¼
	 * @throws RecordStoreException 
	 * @throws RecordStoreNotFoundException 
	 * @throws RecordStoreFullException 
	 */
	private void readhistory() throws Exception
	{
		RecordStore rs=RecordStore.openRecordStore("Dance", false);
	}
	/*
	 * ����
	 */
	int framecount=0;	//gifͼ��֡��
	String prepath="";	//�ϴ�ͼ��·��
	/**
	 * ��GIFͼ��
	 * @param imgpath ͼ��·��
	 * @param x		x����
	 * @param y		y����
	 */
	private void DrawGif(String imgpath,int x,int y)
	{
		Graphics g=getGraphics();
		if(imgpath!=prepath)
		{
			prepath=imgpath;	//�ѵ�ǰͼ��·������ǰһ��
			gifd.read(this.getClass().getResourceAsStream(imgpath));//����ͼƬ
			framecount= gifd.getFrameCount();//��ȡ֡��
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
	 * ������ͷ
	 * @param n Ҫ���ĸ���
	 */
	private void ShowLift(int n)
	{
		int startpos=0;
		Graphics grap=getGraphics();
		if(n!=3)
		{
			//����ǻ�����˵���ǳ�ʼ��,����������Ҫ�������
			grap.drawImage(liftbg, 0, 0, anchor);	///����ͷ�������
		}
		for(int i=1;i<=n;i++)
		{
			grap.drawImage(lift, startpos, 0, anchor);	//ÿ�λ�һ������17����
			startpos+=17;
		}
		flushGraphics();
	}
	/**
	 * �����˶���
	 * @param dir ����
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
	 * ��С������
	 * @param dir ����
	 */
	private void DrawChild(Dir dir)
	{
		if(dir==Dir.d1)
		{
			DrawGif("/c1.GIF", 0, cy);	//С��1������
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
			DrawGif("/cready.GIF", 0, cy);	//С��׼������
		}
	}
	/**
	 * ��ʾ����
	 * @param score ����
	 */
	protected void ShowScore(int score)
	{
		Graphics gra=getGraphics();
		Image[] imgarr=new Image[]{n0,n1,n2,n3,n4,n5,n6,n7,n8,n9};
		int num0=0,num1=0,num2=0,num3=0;//��ʮ��ǧ
		int startposx=0,startposy;
		if(!exit)
		{
			//����Ϸ����ʾ
			startposx=40;
			startposy=0;
			gra.drawImage(scorearea, sw-startposx, 0, anchor);	//���±���
		}
		else
		{
			startposx=68;
			startposy=71;
			//��Ϸ������ʾ
		}
		if(score<10)
		{
			//һλ��
			num0=score;
			gra.drawImage(imgarr[num0], sw-startposx, startposy, anchor);
		}
		else if(score>9&&score<100)
		{
			//��λ��
			num0=score%10;
			num1=(score-num0)/10;
			gra.drawImage(imgarr[num1], sw-startposx, startposy, anchor);
			gra.drawImage(imgarr[num0], sw-startposx-10, startposy, anchor);
		}
		else if(score>99&&score<1000) 
		{
			num0=score%10;
			int temp=(score-num0)/10;	//��λΪ0ʱ��ʮ��֮һ
			num1=temp%10;
			num2=(temp-num1)/10;
			
			gra.drawImage(imgarr[num2], sw-startposx, startposy, anchor);
			gra.drawImage(imgarr[num1], sw-startposx-10, startposy, anchor);
			gra.drawImage(imgarr[num0], sw-startposx-20, startposy, anchor);
		}
		else if(score>999&&score<10000) 
		{
			num0=score%10;	//��λ,��1234,��ʱ4
			int temp1=(score-num0)/10;	//��λΪ0ʱ��ʮ��֮һ,��ʱ123
			num1=temp1%10;	//ʮλ,3
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
	 * С����������
	 * @param n ��������(�Ѷ�)
	 * @throws InterruptedException 
	 */
	private void Dance(int n) throws InterruptedException
	{
		vector.removeAllElements();	//���ԭ�а�����Ϣ
		Random rand=new Random();	//�����
		int dircode=0;
		int count=1;	//�ƴ�
		ShowLightBg(Light.dark,0);	//�ѵ�ȫ��ȥ��
		for(int i=1;i<=5;i++)
		{
			DrawChild(Dir.ready);	//׼������
			Thread.sleep(5);
		}
		Thread.sleep(400); //���׼�������뿪ʼ�������
		while(count<=n)
		{
			ShowLightBg(Light.dark,count);	//��ʾ��ɫ��
			dircode=rand.nextInt(4);	//��������� ����[0,4)
			switch(dircode)
			{
			case 0://1��
				DrawChild(Dir.d1);
				vector.addElement(Dir.d1);
				break;
			case 1://3��
				DrawChild(Dir.d3);
				vector.addElement(Dir.d3);
				break;
			case 2://7��
				DrawChild(Dir.d7);
				vector.addElement(Dir.d7);
				break;
			case 3://9��
				DrawChild(Dir.d9);
				vector.addElement(Dir.d9);
				break;
			}
			count++;
			flushGraphics();
			Thread.sleep(ActionInterval);//�������
		}
	}
	/**
	 * ��ʾ��ɫ�ĵ�
	 * @param state ����״̬
	 * @param n		��ʾ������
	 */
	private void ShowLightBg(Light state,int n)
	{
		Graphics gra=getGraphics();
		gra.drawImage(passarea, 0, py, anchor);	//���±���
		int startpos=0;
		for(int i=1;i<=n;i++)
		{
			if(state==Light.dark)
			{
				gra.drawImage(passbg, startpos, py, anchor);
			}
			startpos+=15;	//ˮƽƫ��һ��ͼƬ��
		}
		flushGraphics();
	}
	/**
	 * ���˶���ʧ��
	 */
	private void Fail()
	{
		ShowLightBg(Light.dark, ActionCount);//���а�ɫ�Ʊ���
		Graphics gra=getGraphics();
		int startpos=0;
		for(int i=1;i<=rightcount;i++)
		{
			gra.drawImage(pass, startpos, py, anchor);
			startpos+=15;
		}
		gra.drawImage(notpass, 15*rightcount, py, anchor);	//���ƴ����
		flushGraphics();
		currentLift--;	//��������1
		ShowLift(currentLift);	//��ʾ������
		if(currentLift==0)
		{
			//GameOver
			Gameover();
		}
		else
		{
			CurrentPressindex=ActionCount;	//ʹС����������
		}
	}
	/**
	 * ���˶������Ե�ָʾ
	 * @param n ���ԵĴ���
	 */
	private void doRight(int n)
	{
		int startpos=0;
		Graphics gra=getGraphics();
		for(int i=1;i<=n;i++)
		{
			gra.drawImage(pass, startpos, py, anchor);
			startpos+=15;//ˮƽƫ��һ��ͼƬ��
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
