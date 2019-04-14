package lifegame;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class ButtonActionListener 
implements java.awt.event.ActionListener {
		
	
	

	private BoardModel m;
	private BoardView v;
	private JFrame frame;
	

	
	ButtonActionListener(BoardView view,JFrame frame){
		this.v = view;
		this.m = v.getModel();
		this.frame = frame;
		
	}
	
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command == null) return;
		switch(command){
		case "Next":
			m.next();
			break;
		case "Undo":
			m.undo();
			break;
		case "Jump":
			this.jump();
			break;
		case "Newgame":
			SwingUtilities.invokeLater(new Main());
			break;
		case "Export":
			this.export();
			break;
		case "Copy":
			this.v.copy();
			break;
		case "Paste":
			this.v.paste();
			break;
		default:
			System.out.println("incorrect command");
			break;
		}


	}
	
	
	private void jump(){
		//ジャンプ先の世代数の入力を受け取る
		int jump = 1;
		String strJump = "1以上10000以下の世代数を指定して下さい";
		do{
			jump = Main.inputNum(strJump,m.getCurrentGen());
			
		}while(jump<1||10000<jump);	
		
		if(jump == -1) return;
		
		m.jump(jump);
		
	}
	
	
	
	
	private void export(){
		
		Point pnt = frame.getLocation();
		int w = frame.getWidth();
		int h = frame.getHeight();
		Rectangle rect = new Rectangle((int)pnt.getX(),(int)pnt.getY(),w,h);
		try{
			
			
			
			
			JFileChooser filechooser = new JFileChooser();

		    int selected = filechooser.showSaveDialog(null);
		    //キャンセル処理
		    if(selected == JFileChooser.CANCEL_OPTION){
		    	return;
		    //エラー処理
		    }else if(selected == JFileChooser.ERROR_OPTION){
		    	JOptionPane.showMessageDialog(this.frame, null);
		    	return;
		    }else if (selected == JFileChooser.APPROVE_OPTION){
		    	File file = filechooser.getSelectedFile();
				Robot robot = new Robot();
				BufferedImage image = robot.createScreenCapture(rect);
				ImageIO.write(image, "png", file);
		    }
			
				
			
		}catch(AWTException e){
			e.printStackTrace();
			this.errorMessage(null);
		}catch(IOException e){
			e.printStackTrace();
			this.errorMessage(null);
		}
		
		
		
		
	}
	
	
	
	private void errorMessage(String str){
		if(str == null) str = "An error has occurred.";
		JOptionPane.showMessageDialog(this.frame, str);
	}

}
