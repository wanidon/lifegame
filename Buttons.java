package lifegame;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;

public class Buttons {
	protected JButton btUndo;
	protected JButton btNext;
	protected JButton btJump;
	protected JLabel labelGen;
	protected JMenuItem itemPaste;
	protected Buttons(JButton btUndo,JButton btNext,JButton btJump,
			JLabel labelGen,
			JMenuItem itemPaste){
		this.btUndo = btUndo;
		this.btNext = btNext;
		this.btJump = btJump;
		this.labelGen = labelGen;
		this.itemPaste = itemPaste;
		
	}

}
