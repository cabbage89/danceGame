package package1;

import java.io.IOException;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class CanvasOver extends MyCanvas implements CommandListener{

	private Command cmd_ok=new Command("ȷ��", Command.OK, 1);
	private Image gameovertip=null;
	private Image endtip=null;
	private Image scorebg=null;
	private Image gameoverbg=null;
	
	protected CanvasOver() {
		super(true);
		// TODO Auto-generated constructor stub
		this.addCommand(cmd_ok);
		setCommandListener(this);
	}
	/**
	 * ���������ʼ��
	 * @param score ����
	 * @throws IOException
	 */
	public void init(int score) throws IOException
	{
		/*��Ϸ����:42,23
		gameover:42,38
		������:38,58
		����ͼƬ:45,107*/
		gameovertip=Image.createImage("/gameovertip.GIF");
		endtip=Image.createImage("/endtip.GIF");
		scorebg=Image.createImage("/scorebg.GIF");
		gameoverbg=Image.createImage("/gameoverbg.GIF");
		
		Graphics gra=super.getGraphics();
		gra.setColor(255, 255, 255);
		gra.fillRect(0, 0, sw, sh);
		gra.drawImage(endtip, 42, 23,Graphics.LEFT|Graphics.TOP);
		gra.drawImage(gameovertip, 42, 38,Graphics.LEFT|Graphics.TOP);
		gra.drawImage(scorebg, 38, 58,Graphics.LEFT|Graphics.TOP);
		gra.drawImage(gameoverbg, 45, 107,Graphics.LEFT|Graphics.TOP);
		flushGraphics();
		repaint();
		super.ShowScore(score);
		System.out.println("��������������");
	}
	
	public void commandAction(Command cmd, Displayable arg1) {
		// TODO Auto-generated method stub
		if(cmd==cmd_ok)
		{
			//������Ϸ
		}
	}

}
