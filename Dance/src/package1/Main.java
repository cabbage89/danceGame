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

	private MyCanvas canvas=new MyCanvas(false,false);//�����������
	private Display display;
	private Displayable predisplay;
	private Form form;
	private Command cmd_ok=new Command("ȷ��", Command.OK, 1);
	private Command cmd_back=new Command("����", Command.BACK, 1);
	private Command cmd_reset=new Command("����", Command.OK, 1);
	private List list=new List("�����㵸", List.IMPLICIT);
	private StringItem tip=new StringItem(null, "");
	private boolean backstate=false;	//����״̬
	Thread judgeth=new Thread(this);	//�ж��˳�״̬�߳�

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
			if(selitem=="����Ϸ"||selitem=="����")
			{
				predisplay=display.getCurrent();	
				MyCanvas.exit=false;//�ָ�
				if(selitem=="����")
				{
					canvas=new MyCanvas(false,true);	//�ٴ�ʵ����
				}
				else {
					canvas=new MyCanvas(false,false);	//�ٴ�ʵ����
				}
				canvas.setFullScreenMode(true);
				display.setCurrent(canvas);
				try
				{
					canvas.thgame.start();	//����1���������Ϸ�߳�
				}
				catch (Exception e) {
					// TODO: handle exception
					System.out.println("�����߳��쳣:"+e);
				}
				System.out.println("����Ϸ...");
				backstate=false;//�ָ�״̬
				if(!judgeth.isAlive())
				{
					System.out.println("�����µļ����߳�...");
					judgeth.start();	//�ж��߳�����
				}
			}
			else if(selitem=="�������")
			{
				predisplay=display.getCurrent();
				form.deleteAll();
				form.setTitle("�����㵸");
				form.setCommandListener(this);
				int best=0;
				try {
					best= readbestscore();
				} catch (Exception e) {
					// TODO: handle exception
					System.out.println("��ȡ��Ѽ�¼ʧ��:"+e);
				}
				tip.setText(("����                "+best));
				form.append(tip);
				form.removeCommand(cmd_ok);
				form.addCommand(cmd_reset);
				display.setCurrent(form);
				
				System.out.println("�������...");
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
				
				System.out.println("��Ϸ˵��...");
			}
			else if(selitem=="����")
			{}
		}
		else if(cmd==cmd_back)
		{
			//����
			if(predisplay==display.getCurrent())
			{
				System.out.println("�����˳�...");
				//����ǿ�ʼ�˵��������˳�
				this.notifyDestroyed();
			}
			else
			{
				display.setCurrent(predisplay);
				
				System.out.println("������...");
			}
		}
		else if(cmd==cmd_reset)
		{
			//����
			try{
				RecordStore.deleteRecordStore("DanceBest");
				System.out.println("��Ѽ�¼���óɹ�...");
				form.deleteAll();
				tip.setText(("����                  0"));
				form.append(tip);
				
			}catch (Exception e) {
				// TODO: handle exception
				System.out.println("��Ѽ�¼����ʧ��:"+e);
			}
		}
	}
	/**
	 * ���¿�ʼ
	 */
	public void restart()
	{
		display.setCurrent(predisplay);
		
		System.out.println("������...");
	}
	//�̼߳����û�����״̬
	public void run() {
		// TODO Auto-generated method stub
		while(true)
		{
			//���߳������˳�
			while(!backstate)
			{
				//�����δ������ѭ���ж��û��Ƿ��˳�
				//�û�����ʱ�ص����˵�
				if(MyCanvas.backstate)
				{
					MyCanvas.backstate=false;
					list.deleteAll();
					if(MyCanvas.iscontinue)
					{
						list.append("����", null);
						MyCanvas.iscontinue=false;	//��ԭֵ
					}
					list.append("����Ϸ", null);
					list.append("�������", null);
					list.append("��Ϸ˵��", null);
					list.addCommand(cmd_ok);
					list.addCommand(cmd_back);
					list.setCommandListener(this);
					display.setCurrent(predisplay);
					backstate=true;
					System.out.println("�������˵�...");
				}
				try {
					Thread.sleep(100);
					//System.out.println("�����ж��û��˳�״̬...");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	/**
	 * ��ȡ�����Ϸ��¼
	 * @return ��ѷ���
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
			System.out.println("��ȡ��Ѽ�¼��...");
			int temp= Integer.parseInt(new String(renum.nextRecord()));
			rsb.closeRecordStore();
			return temp;
		}
		else
		{
			System.out.println("û����Ѽ�¼...");
			rsb.closeRecordStore();
			return 0;
		}
		
	}

}
