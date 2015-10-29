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

	private MyCanvas canvas=new MyCanvas(false);//�����������
	private Display display;
	private Displayable predisplay;
	private Form form;
	private Command cmd_ok=new Command("ȷ��", Command.OK, 1);
	private Command cmd_back=new Command("����", Command.BACK, 1);
	private Command cmd_reset=new Command("����", Command.OK, 1);
	private List list=new List("�����㵸", List.IMPLICIT);
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
		list.append("����Ϸ", null);
		list.append("�������", null);
		list.append("��Ϸ˵��", null);
		list.addCommand(cmd_ok);
		list.addCommand(cmd_back);
		list.setCommandListener(this);
		display.setCurrent(list);
		predisplay=display.getCurrent();	//��ʼ��ǰһ����ʾ
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
			if(selitem=="����Ϸ")
			{
				predisplay=display.getCurrent();
				canvas.setFullScreenMode(true);
				display.setCurrent(canvas);
				canvas.thgame.start();	//����1���������Ϸ�߳�
			}
			else if(selitem=="�������")
			{
				predisplay=display.getCurrent();
				form.deleteAll();
				form.setTitle("�����㵸");
				form.setCommandListener(this);
				tip.setText("����");
				form.append(tip);
				form.removeCommand(cmd_ok);
				form.addCommand(cmd_reset);
				display.setCurrent(form);
			}
			else if(selitem=="��Ϸ˵��")
			{
				predisplay=display.getCurrent();
				form.deleteAll();
				form.setTitle("��Ϸ˵��");
				form.setCommandListener(this);
				tip.setText("���˸���С���ڶ��ķ���������,ʹ�ð���1,3,7,9������.����:512882840");
				form.removeCommand(cmd_ok);
				form.removeCommand(cmd_reset);
				form.append(tip);
				display.setCurrent(form);
			}
			else if(selitem=="����")
			{}
		}
		else if(cmd==cmd_back)
		{
			//����
			if(predisplay==display.getCurrent())
			{
				//����ǿ�ʼ�˵��������˳�
				this.notifyDestroyed();
			}
			else
			{
				display.setCurrent(predisplay);
			}
		}
		else if(cmd==cmd_reset)
		{
			//����
		}
	}
	/**
	 * ���¿�ʼ
	 */
	public void restart()
	{
		display.setCurrent(predisplay);
		System.out.println("������");
	}

}
