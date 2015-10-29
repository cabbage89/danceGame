package package1;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.StringItem;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;

import com.sun.perseus.model.Set;

public class Main extends MIDlet implements CommandListener,Runnable{

	private MyCanvas canvas=new MyCanvas(false,false);//不禁用特殊键
	private Display display;
	private Displayable predisplay;
	private Form form;
	private Command cmd_ok=new Command("确定", Command.OK, 1);
	private Command cmd_back=new Command("返回", Command.BACK, 1);
	private Command cmd_reset=new Command("重置", Command.OK, 1);
	private List list=new List("手舞足蹈", List.IMPLICIT);
	private StringItem tip=new StringItem(null, "");
	private boolean backstate=false;	//返回状态
	Thread judgeth=new Thread(this);	//判断退出状态线程

	public Main() {
		// TODO Auto-generated constructor stub
	}

	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
		// TODO Auto-generated method stub

	}

	protected void pauseApp() {
		// TODO Auto-generated method stub
	}

	protected void startApp() throws MIDletStateChangeException {
		// TODO Auto-generated method stub
		display=Display.getDisplay(this);
		list.append("新游戏", null);
		list.append("最佳排行", null);
		list.append("游戏说明", null);
		list.addCommand(cmd_ok);
		list.addCommand(cmd_back);
		list.setCommandListener(this);
		display.setCurrent(list);
		predisplay=display.getCurrent();	//初始化前一个显示
		form=new Form("");
		form.addCommand(cmd_back);
		form.addCommand(cmd_ok);
	}

	public void commandAction(Command cmd, Displayable dis) {
		// TODO Auto-generated method stub
		if(cmd==List.SELECT_COMMAND||cmd==cmd_ok)
		{
			int selindex=list.getSelectedIndex();
			String selitem= list.getString(selindex);
			if(selitem=="新游戏"||selitem=="继续")
			{
				predisplay=display.getCurrent();	
				MyCanvas.exit=false;//恢复
				if(selitem=="继续")
				{
					canvas=new MyCanvas(false,true);	//再次实例化
				}
				else {
					canvas=new MyCanvas(false,false);	//再次实例化
				}
				canvas.setFullScreenMode(true);
				display.setCurrent(canvas);
				try
				{
					canvas.thgame.start();	//休眠1秒后启动游戏线程
				}
				catch (Exception e) {
					// TODO: handle exception
					System.out.println("启动线程异常:"+e);
				}
				System.out.println("新游戏...");
				backstate=false;//恢复状态
				if(!judgeth.isAlive())
				{
					System.out.println("启动新的监视线程...");
					judgeth.start();	//判断线程启动
				}
			}
			else if(selitem=="最佳排行")
			{
				predisplay=display.getCurrent();
				form.deleteAll();
				form.setTitle("手舞足蹈");
				form.setCommandListener(this);
				int best=0;
				try {
					best= readbestscore();
				} catch (Exception e) {
					// TODO: handle exception
					System.out.println("读取最佳记录失败:"+e);
				}
				tip.setText(("分数                "+best));
				form.append(tip);
				form.removeCommand(cmd_ok);
				form.addCommand(cmd_reset);
				display.setCurrent(form);
				
				System.out.println("最佳排行...");
			}
			else if(selitem=="游戏说明")
			{
				predisplay=display.getCurrent();
				form.deleteAll();
				form.setTitle("游戏说明");
				form.setCommandListener(this);
				tip.setText("老人跟着小孩摆动的方向做动作,使用按键1,3,7,9来操作.作者:512882840");
				form.removeCommand(cmd_ok);
				form.removeCommand(cmd_reset);
				form.append(tip);
				display.setCurrent(form);
				
				System.out.println("游戏说明...");
			}
			else if(selitem=="继续")
			{}
		}
		else if(cmd==cmd_back)
		{
			//返回
			if(predisplay==display.getCurrent())
			{
				System.out.println("正在退出...");
				//如果是开始菜单返回则退出
				this.notifyDestroyed();
			}
			else
			{
				display.setCurrent(predisplay);
				
				System.out.println("返回中...");
			}
		}
		else if(cmd==cmd_reset)
		{
			//重置
			try{
				RecordStore.deleteRecordStore("DanceBest");
				System.out.println("最佳记录重置成功...");
				form.deleteAll();
				tip.setText(("分数                  0"));
				form.append(tip);
				
			}catch (Exception e) {
				// TODO: handle exception
				System.out.println("最佳记录重置失败:"+e);
			}
		}
	}
	/**
	 * 重新开始
	 */
	public void restart()
	{
		display.setCurrent(predisplay);
		
		System.out.println("重启了...");
	}
	//线程监听用户返回状态
	public void run() {
		// TODO Auto-generated method stub
		while(true)
		{
			//此线程永不退出
			while(!backstate)
			{
				//如果还未返回死循环判断用户是否退出
				//用户返回时回到主菜单
				if(MyCanvas.backstate)
				{
					MyCanvas.backstate=false;
					list.deleteAll();
					if(MyCanvas.iscontinue)
					{
						list.append("继续", null);
						MyCanvas.iscontinue=false;	//还原值
					}
					list.append("新游戏", null);
					list.append("最佳排行", null);
					list.append("游戏说明", null);
					list.addCommand(cmd_ok);
					list.addCommand(cmd_back);
					list.setCommandListener(this);
					display.setCurrent(predisplay);
					backstate=true;
					System.out.println("返回主菜单...");
				}
				try {
					Thread.sleep(100);
					//System.out.println("正在判断用户退出状态...");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	/**
	 * 读取最佳游戏记录
	 * @return 最佳分数
	 * @throws RecordStoreException 
	 * @throws  
	 * @throws Exception 
	 */
	private int readbestscore() throws Exception
	{
		RecordStore rsb=RecordStore.openRecordStore("DanceBest", true);
		RecordEnumeration renum= rsb.enumerateRecords(null, null, false);
		if(renum.hasNextElement())
		{
			System.out.println("读取最佳记录中...");
			int temp= Integer.parseInt(new String(renum.nextRecord()));
			rsb.closeRecordStore();
			return temp;
		}
		else
		{
			System.out.println("没有最佳记录...");
			rsb.closeRecordStore();
			return 0;
		}
		
	}

}
