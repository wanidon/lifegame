package lifegame;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.BorderLayout;

public class Main implements Runnable{
	


	
	public static void main(String[] args){
		

		
		SwingUtilities.invokeLater(new Main());
	}
	
	
	public void run(){
		//行数・列数の最小値・最大値
		final int min = 10;
		final int maxRow = 90;
		final int maxCol = 160;
		
		int rows = 10;
		int cols = 10;
		
		//クエスチョンメッセージダイアログで行数・列数を決定
		String strRows = min + "以上" + maxRow + "以下の行数を入力して下さい";
		do{
			rows = inputNum(strRows,rows);
			//取り消しされた場合
			if(rows == -1) return;
		}while(rows<min||maxRow<rows);
		
		String strCols = min + "以上" + maxCol + "以下の列数を入力して下さい";
		do{
			cols = inputNum(strCols,cols);
			//取り消しされた場合
			if(cols == -1) return;
		}while(cols<min||maxCol<cols);
		
		
		
		/*
		boolean incorrectImput;
		do{
			incorrectImput = false;
			String strRows = JOptionPane.showInputDialog(
					min + "以上" + max + "以下の行数を入力して下さい",
					min);
			//入力されなければリターン
			if(strRows == null) return;
			try{
				rows = Integer.parseInt(strRows);
			}catch(NumberFormatException e){
				incorrectImput = true;
				JOptionPane.showMessageDialog(null, "不正な入力です");
			}
			if ( rows < 10 || 100 < rows){
				incorrectImput = true;
				JOptionPane.showMessageDialog(null, "The value is outside the range");
			}
		}while(incorrectImput);
		
		do{
			incorrectImput = false;
			String strCols = JOptionPane.showInputDialog(
					min + "以上" + max + "以下の列数を入力して下さい",
					min);
			//入力されなければリターン
			if(strCols == null) return;
			try{
				cols = Integer.parseInt(strCols);
			}catch(NumberFormatException e){
				incorrectImput = true;
				JOptionPane.showMessageDialog(null, "不正な入力です");
			}
			if ( cols < 10 || 100 < cols){
				incorrectImput = true;
				JOptionPane.showMessageDialog(null, "The value is outside the range");
			}
		}while(incorrectImput);
		*/
		

		
		
		
		BoardModel model = new BoardModel(cols,rows);
		model.addListener(new ModelPrinter());

		
		//ウィンドウを作成する
		JFrame frame = new JFrame("Lifegame");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		//ウィンドウ
		JPanel base = new JPanel();
		frame.setContentPane(base);
		base.setPreferredSize(new Dimension(400,300));
		frame.setMinimumSize(new Dimension(300,200));
		
		base.setLayout(new BorderLayout());
		
		
		
		
		//メニュー作成
		JMenuBar menubar = new JMenuBar();
		
		JMenu mnFile = new JMenu("File");
		JMenu mnEdit = new JMenu("Edit");
		menubar.add(mnFile);
		menubar.add(mnEdit);
		
		JMenuItem itemNew = new JMenuItem("New Game");
		itemNew.setActionCommand("Newgame");
		JMenuItem itemExport = new JMenuItem("Expot image");
		itemExport.setActionCommand("Export");
		JMenuItem itemCopy = new JMenuItem("Copy");
		itemCopy.setActionCommand("Copy");
		JMenuItem itemPaste = new JMenuItem("Paste");
		itemPaste.setActionCommand("Paste");
		mnFile.add(itemNew);
		mnFile.add(itemExport);
		mnEdit.add(itemCopy);
		mnEdit.add(itemPaste);
		

		frame.setJMenuBar(menubar);
		
		
		

		//ボタン・ラベル宣言
		JButton btNext = new JButton("Next");
		btNext.setActionCommand("Next");
		JButton btUndo = new JButton("Undo");
		btUndo.setActionCommand("Undo");
		JButton btJump = new JButton("Jump");
		btJump.setActionCommand("Jump");
		JLabel labelGen = new JLabel();
		
		
		//ボタンの参照受け渡し用クラス
		Buttons b = new Buttons(btUndo,btNext,btJump,labelGen,itemPaste);
		BoardView view = new BoardView(model,b);
		model.addListener(view);
		base.add(view,BorderLayout.CENTER);
		
		
		//ボタン等のアクションリスナーを追加
		ButtonActionListener myBtActLstnr = new ButtonActionListener(view,frame);
		btNext.addActionListener(myBtActLstnr);
		btUndo.addActionListener(myBtActLstnr);
		btJump.addActionListener(myBtActLstnr);
		itemNew.addActionListener(myBtActLstnr);
		itemExport.addActionListener(myBtActLstnr);
		itemCopy.addActionListener(myBtActLstnr);
		itemPaste.addActionListener(myBtActLstnr);
		
		//btUndo.setEnabled(false);
		view.updated(model);
		
		//画面の再描写用リスナー
		/*
		BoardListener repainter = new Repainter(gen,btUndo,view);
		model.addListener(repainter);
		*/
		
		
		JPanel buttonPanel = new JPanel();//ボタン用パネル
		JPanel labelPanel = new JPanel();//ラベル用パネル
		//baseの下端に設置
		base.add(buttonPanel, BorderLayout.SOUTH);
		base.add(labelPanel, BorderLayout.NORTH);

		buttonPanel.setLayout(new FlowLayout());//java.awt.FloyLayoutを設定
		
		
		

		
		
		
		//ボタン・ラベルをパネルに追加
		buttonPanel.add(btNext);
		buttonPanel.add(btUndo);
		buttonPanel.add(btJump);
		labelPanel.add(labelGen);
		
		frame.pack();
		frame.setVisible(true);
		
		
	}
	
	//クエスチョンダイアログメッセージで正整数の入力を受け付けるメソッド
	//取り消しボタンが押されると-1を返す
	static protected int inputNum(String message,int init){
		boolean incorrectImput = false;
		int intImput = 0;
		do{
			incorrectImput = false;
			String strImput = JOptionPane.showInputDialog(message,init);
			//入力されなければリターン
			if(strImput == null) return -1;
			try{
				 intImput = Integer.parseInt(strImput);
			}catch(NumberFormatException e){
				incorrectImput = true;
				JOptionPane.showMessageDialog(null, "不正な入力です");
			}
			if(intImput <= 0) incorrectImput = true;
		}while(incorrectImput);
		return intImput;
	}
	
	
	
	
	
	
	
	
	
}
