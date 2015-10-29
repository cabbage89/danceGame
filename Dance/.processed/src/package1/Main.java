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

import com.sun.perseus.model.Set;

public class Main extends MIDlet implements CommandListener{

	private MyCanvas canvas=new MyCanvas(false);//不禁用特殊键
	private Display display;
	private Displayable predisplay;
	private Form form;
	private Command cmd_ok=new Command("确定", Command.OK, 1);
	private Command cmd_back=new Command("返回", Command.BACK, 1);
	private Command cmd_reset=new Command("重置", Command.OK, 1);
	private List list=new List("手舞足蹈", List.IMPLICIT);
	private StringItem tip=new StringItem(null, "");

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
			if(selitem=="新游戏")
			{
				predisplay=display.getCurrent();
				canvas.setFullScreenMode(true);
				display.setCurrent(canvas);
				canvas.thgame.start();	//休眠1秒后启动游戏线程
			}
			else if(selitem=="最佳排行")
			{
				predisplay=display.getCurrent();
				form.deleteAll();
				form.setTitle("手舞足蹈");
				form.setCommandListener(this);
				tip.setText("分数");
				form.append(tip);
				form.removeCommand(cmd_ok);
				form.addCommand(cmd_reset);
				display.setCurrent(form);
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
			}
			else if(selitem=="继续")
			{}
		}
		else if(cmd==cmd_back)
		{
			//返回
			if(predisplay==display.getCurrent())
			{
				//如果是开始菜单返回则退出
				this.notifyDestroyed();
			}
			else
			{
				display.setCurrent(predisplay);
			}
		}
		else if(cmd==cmd_reset)
		{
			//重置
		}
	}
	/**
	 * 重新开始
	 */
	public void restart()
	{
		display.setCurrent(predisplay);
		System.out.println("重启了");
	}

}
