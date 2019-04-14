package lifegame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

public class BoardView extends JPanel
	implements BoardListener, MouseListener, MouseMotionListener, KeyListener{
	
	BoardModel model;
	

	protected int s;//マス1辺の長さ
	protected int cols;//列数
	protected int rows;//行数
	
	//マウスカーソルの一回前のクリック・ドラッグ時の座標
	protected int prevX;
	protected int prevY;
	
	//シフトキーが押下されているか
	private boolean isShiftDown;
	
	//シフトキーが押されるとマウスドラッグによるセルの状態変化をロック
	private boolean isRocked;
	
	//シフトキーを押しながら初めてクリックした場所
	private int shiftX;
	private int shiftY;
	
	//シフトキー押下時の範囲
	private int top;
	private int bottom;
	private int right;
	private int left;
	
	//盤面コピー時のクリップボード
	private boolean[][] clipBoard;
	
	//有効・無効を切り替えるためのボタンの参照
	private JButton btUndo;
	private JButton btNext;
	private JButton btJump;
	private JMenuItem itemPaste;
	
	//世代数の表示の更新を行うためのラベルの参照
	private JLabel labelGen;
	
	
	
	
	
	public BoardView(BoardModel m, Buttons b){
		this.model = m;
		this.cols = m.getCols();//列数
		this.rows = m.getRows();//行数
		
		this.prevX = -1;
		this.prevY = -1;
		
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		
		this.btUndo = b.btUndo;
		this.btNext = b.btNext;
		this.btJump = b.btJump;
		this.labelGen = b.labelGen;
		this.itemPaste = b.itemPaste;
		
		this.bottom = this.right = -1;
		this.top = this.left = 99999;
		
		addKeyListener(this);
	}
	
	
	@Override
	public void paint(Graphics g){
		
		super.paint(g);//JPanelの描画処理（背景塗りつぶし）
		
		s = Math.min(
				(int) (this.getWidth() * (cols-1) / (cols*cols)),
				(int) (this.getHeight() * (rows-1) / (rows*rows)) 
				);//1辺の長さをウィンドウサイズに合わせて取得
		
		
		
		
		//生きているマスを塗りつぶす
		g.setColor(Color.BLACK);
		for(int i=0;i<rows;i++){
			for(int j=0;j<cols;j++){	
				if(model.isAlive(j, i) == true) g.fillRect(cnvX(j), cnvY(i), s, s);

			}
		}
		
		
		
		//シフト押下時の選択範囲の表示
		if(this.top<=this.bottom && this.left<=this.right){
			for(int i=this.top;i<=this.bottom;i++){
				for(int j=this.left;j<=this.right;j++){
					
					if(model.isAlive(j, i) == true){
						g.setColor(Color.BLUE);
						g.fillRect(cnvX(j), cnvY(i), s, s);
					}else{
						g.setColor(Color.CYAN);
						g.fillRect(cnvX(j), cnvY(i), s, s);
					}
					
				}
			}
		}
				
				
		
		
		
		//縦線の描画
		g.setColor(Color.BLACK);
		for(int i=0;i<=cols;i++){
			g.drawLine(cnvX(i), cnvY(0), cnvX(i), cnvY(rows));
		}
		//横線の描画
		for(int i=0;i<=rows;i++){
			g.drawLine(cnvX(0), cnvY(i), cnvX(cols), cnvY(i));
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		//キー入力のフォーカスを要求
		requestFocusInWindow();

	}
	
	public void repaint(Graphics g){
		paint(g);
		
	}
	
	
	private int cnvX(int x){
		
		return (int) (s/2 +  (x)*s);
		
	}
	
	private int cnvY(int y){
		
		return (int) (s/2 +  (y)*s);
		
	}
	
	private int invX(int x){
		if(x < s/2 ) return -1;  
		return (int)( x -((int)s/2) )/s;
	}
	
	private int invY(int y){
		if(y <s /2 ) return -1;  
		return (int)( y -((int)s/2) )/s;
	}
	
	protected void resetPrevX(){
		this.prevX = 0;
	}
	protected void resetPrevY(){
		this.prevY = 0;
	}
	
	//マウス操作イベントに対応するメソッド
	@Override
	public void mouseDragged(MouseEvent e) {
		int x = invX(e.getX());
		int y = invY(e.getY());
		//範囲外へのアクセスならばreturn
		if( x < 0 || y < 0 || x >= cols || y >= rows) return;
		//シフト非押下時
		if(this.isShiftDown == false && this.isRocked == false){
			
			if(x!=this.prevX || y!= this.prevY)
				this.model.changeCellState(x, y);
			
			this.prevX = x;
			this.prevY = y;
			
		//シフト押下時
		}else if(this.isShiftDown == true){
			//マウスクリック時に範囲外へのアクセスしていればreturn
			if( this.shiftX < 0 || this.shiftY < 0 || this.shiftX >= cols || this.shiftY >= rows) return;
				
			this.right = Math.max(this.shiftX,x);
			this.left = Math.min(this.shiftX, x);
			this.bottom = Math.max(this.shiftY, y);
			this.top = Math.min(this.shiftY, y);
			this.updated(this.model);
			
			
		}
	}


	@Override
	public void mouseMoved(MouseEvent e) {

		
	}


	@Override
	public void mouseClicked(MouseEvent e) {

		
	}


	@Override
	public void mousePressed(MouseEvent e) {
		int x = invX(e.getX());
		int y = invY(e.getY());
		//クリックを検出＝クリックした地点がBoardView上ならシフトキーによる範囲選択をリセット
		
		this.bottom = this.right = -1;
		this.top = this.left = 99999;
		this.updated(this.model);
		//シフト非押下時
		if(this.isShiftDown == false){
			this.shiftX = -1;
			this.shiftY = -1;
			this.model.changeCellState(x,y);
			this.prevX = x;
			this.prevY = y;
		//シフト押下時
		}else{
			//範囲外へのアクセスならば座標として-1を設定
			if( x < 0 || y < 0 || x >= cols || y >= rows){
				this.shiftX = -1;
				this.shiftY = -1;
			}else{
				this.shiftX = x;
				this.shiftY = y;

				this.right = x;
				this.left = x;
				this.bottom = y;
				this.top = y;
				this.updated(this.model);

			}
			
			
		}
		
		
	}


	@Override
	public void mouseReleased(MouseEvent e) {
		this.isRocked = false;
		
		
	}


	@Override
	public void mouseEntered(MouseEvent e) {
		
	}


	@Override
	public void mouseExited(MouseEvent e) {
		
	}


	@Override
	public void updated(BoardModel m) {
		this.btUndo.setEnabled(m.isUndoable());
		this.btNext.setEnabled(m.isNextable());
		this.btJump.setEnabled(m.isJumpable());
		this.itemPaste.setEnabled(this.isPastable());
		String strgen = "generation: " + String.valueOf(m.getCurrentGen());
		this.labelGen.setText(strgen);
		this.repaint();
		//TODO 世代数10000の時のnextボタンの無効化、盤面起動直後のjumpボタンの無効化
	}


	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		if(e.isShiftDown()){
			this.isShiftDown = true;
			this.isRocked = true;
		}
		
	}


	@Override
	public void keyReleased(KeyEvent e) {
		this.isShiftDown = false;
		
	}
	
	public BoardModel getModel(){
		return this.model;
	}
	
	
	public void copy(){
		//選択範囲がなければリターン
		if(this.top>this.bottom || this.left>this.right) return;
		//選択範囲のサイズで配列を生成
		this.clipBoard 
		= new boolean[this.bottom-this.top+1][this.right-this.left+1];

		//選択範囲を配列にコピー
		for(int i=0;i<this.bottom-this.top+1;i++){
			for(int j=0;j<this.right-this.left+1;j++){
				this.clipBoard[i][j] 
						= this.model.isAlive(j+this.left, i+this.top); 
			}
		}
	}
	
	protected void paste(){
		if(this.clipBoard == null) return;
		//セルをはみ出さないように貼り付け範囲を設定
		int limitY = Math.min(this.clipBoard.length, this.bottom-this.top+1);
		int limitX = Math.min(this.clipBoard[0].length, this.right-this.left+1);
				
		//選択範囲の左上から貼り付け
		this.model.beforeSetCellState();
		for(int i=0; i<limitY;i++){
			for(int j=0; j<limitX;j++){
				this.model.setCellState(j+this.left, i+this.top,this.clipBoard[i][j]);
			}
		}
		this.model.afterSetCellState();
		
	}
	public boolean isPastable(){
		if (this.clipBoard == null) return false;
		else return true;
	}
	
	
	
	
	

}
