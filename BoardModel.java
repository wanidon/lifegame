package lifegame;

import java.util.*;

public class BoardModel {

	private int cols;
	private int rows;
	private boolean[][] cells;
	
	
	//世代数:1+nextを実行した回数
	private int currentGen;
	//1つ前の状態の世代数
	private int prevGen;
	//prevGenを保存するリスト
	private List<Integer> generations;
	//盤面の状態を保存するリスト
	private List<boolean[][]> history;
	//BoardListenerを格納するリスト
	private ArrayList<BoardListener> listeners;
	//undo回数のリミッター
	private int limitUndo;
	//jump(1)を実行したフラグ
	private boolean doneJump1;
	//jump機能のリミッター
	private boolean limitJump;
	
	public BoardModel(int c,int r){
		
		cols = c;
		rows = r;
		cells = new boolean[rows+2][cols+2];
		
		history = new ArrayList<boolean[][]>();
		listeners = new ArrayList<BoardListener>();
		generations = new ArrayList<Integer>();
		
		
		currentGen = 1;
		

	}
	
	//BoardListenerを登録
	public void addListener(BoardListener listener){
		listeners.add( listener );
	}
	
	//すべてのBoardListenerに更新を通知
	private void fireUpdate(){
		this.limitJump = false;
		if(this.doneJump1) limitJump = true;
		this.doneJump1 = false;
		for(BoardListener listener: listeners){
			listener.updated(this);
		}
	}
	
	public int getCols(){
		return cols;
	}
	
	public int getRows(){
		return rows;
	}
	
	public void printForDebug(){
		for(int i=0;i<=rows+1;i++){
			for(int j=0;j<=cols+1;j++){
				if(cells[i][j] == true){
					System.out.print("*");
				}else{
					System.out.print(".");
				}
			}
			System.out.println();
		}
		System.out.println();
	}
	
	
	
	public void changeCellState(int x, int y){
		//範囲外へのアクセスならばreturn
		if( x < 0 || y < 0 || x >= cols || y >= rows) return;
		
		this.beforeSetCellState();

		
		//生存or死亡状況を変更
		if(cells[y+1][x+1] == true){
			cells[y+1][x+1] = false;
		}else{
			cells[y+1][x+1] = true;
		}
		
		this.afterSetCellState();
		
	}
	
	public void setCellState(int x,int y, boolean b){
		//範囲外へのアクセスならばreturn
		if( x < 0 || y < 0 || x >= cols || y >= rows) return;
		this.cells[y+1][x+1] = b;
	}
	public void beforeSetCellState(){
		if(this.currentGen == 1){
			//現在の状況のコピーを作成
			boolean tmpcells[][] = new boolean[rows+2][cols+2];
			for(int i=1;i<=rows;i++){
				for(int j=1;j<=cols;j++){
					tmpcells[i][j] = cells[i][j];
				}
			}
			//tmpcellsをhistoryに追加
			if(history.size() >= 32 ) history.remove(0);
			this.history.add(tmpcells);
			this.generations.add(this.prevGen);
		}
	}
	public void afterSetCellState(){
		this.prevGen = this.currentGen;
		this.currentGen = 1;
		if(this.limitUndo<32) this.limitUndo++;
		this.fireUpdate();
		
	}
	
	
	public void next(){
		if(this.currentGen==10000) return;
		
		//changeCellState後初めてnextを実行するなら現在の状態をコピー		
		if(this.currentGen==1){
			if(history.size() >= 32 ) history.remove(0);
			boolean tmpcells[][]= new boolean[rows+2][cols+2];
			for(int i=1;i<=rows;i++){
				for(int j=1;j<=cols;j++){
					tmpcells[i][j] = cells[i][j];
				}
			}
			this.history.add(tmpcells);
			this.generations.add(this.prevGen);
		}
		
		//世代更新、nextの実態
		this.alternateGen(cells);
				
		
		this.prevGen = this.currentGen++;
		if(this.limitUndo<32) this.limitUndo++;
		this.fireUpdate();


	
	}
	public void alternateGen(boolean[][] tmpcells){
		
		
	
		/*生存セル数を蓄積する方法による更新*/
		
		//tmpcells[i][j]の周囲の生存セル数をtmpint[i][j]に格納
		int tmpint[][] = new int[rows+2][cols+2];
		for(int i=1;i<=rows;i++){
			for(int j=1;j<=cols;j++){
				if(tmpcells[i][j]==true){
					tmpint[i-1][j-1]++;
					tmpint[i-1][j]++;
					tmpint[i-1][j+1]++;
					tmpint[i][j-1]++;
					tmpint[i][j+1]++;
					tmpint[i+1][j-1]++;
					tmpint[i+1][j]++;
					tmpint[i+1][j+1]++;
				}
			}
		}
		
		//周囲の生存セル数に応じて生存・誕生を決定
		for(int i=1;i<=rows;i++){
			for(int j=1;j<=cols;j++){
				if( tmpint[i][j]==3 ||
					(tmpint[i][j]==2 && tmpcells[i][j]==true)
				)tmpcells[i][j] = true;
				else tmpcells[i][j] = false;
			}
		}
		
	}
	
	public void undo(){

		
		if(this.isUndoable()==false) return;
		
		boolean[][] prev = this.history.get(history.size()-1);
		boolean[][] tmp = new boolean[rows+2][cols+2];
		
		//リストに保存されている最新のセルの情報を読み出し
		for(int i=1;i<=rows;i++){
			for(int j=1;j<=cols;j++){
				tmp[i][j] = prev[i][j];
			}
		}

		this.currentGen = this.prevGen--;
		//(1つ前の状態の世代数-1)回だけalternateGenで世代更新
		for(int i=0;i<this.prevGen;i++){
			this.alternateGen(tmp);
		}

		//参照を代入
		cells = tmp;

		//世代更新なし、すなわちchangeCellState後の状態を読み出した場合は
		//その分をhistoryから削除
		if(this.prevGen <= 0){
			this.history.remove(this.history.size()-1);
			this.prevGen = this.generations.get(this.generations.size()-1);
			this.generations.remove(this.generations.size()-1);
		}
		
		
		
		this.limitUndo--;
		this.fireUpdate();

		
	}
	
	
	
	public void jump(int n){
		if(this.isJumpable() == false) return;
			
		
		//世代更新する回数分、Undo可能回数を増やす
		if(n>this.currentGen){
			this.limitUndo += n-this.currentGen;
		}else if(n == 1){
			this.limitUndo += 1;
		}else{
			this.limitUndo += n-1;
		}
		if(this.limitUndo > 32) this.limitUndo = 32;
		
		
		//next同様changeCellState等の後初めてjumpを実行する、すなわち世代数が1なら現在の状態をコピー
		//ただしすでにjump(1)を実行していないか確認
		
		if(n == 1 ) this.doneJump1 = true;

			
	    //現在の盤面が第一世代であればリストに追加
		if(this.currentGen==1){
			
			boolean tmpcells[][] = new boolean[rows+2][cols+2];
			for(int i=1;i<=rows;i++){
				for(int j=1;j<=cols;j++){
					tmpcells[i][j] = cells[i][j];
				}
			}

			if(history.size() >= 32 ) history.remove(0);
			this.history.add(tmpcells);
			this.generations.add(this.prevGen);
		}
			

			
		//現在の世代数がジャンプ先の世代数より小さければその分更新
		if(this.currentGen < n){
			for(int i=0; i<n-this.currentGen; i++){
				this.alternateGen(this.cells);
			}
			this.prevGen = n-1;
		}else{
		
			//現在の世代数がジャンプ先の世代数より大きければリストに保存された最新セルを読み出す
			boolean his[][] = this.history.get(this.history.size()-1);
			for(int i=0;i<=rows;i++){
				for(int j=0;j<=cols;j++){
					this.cells[i][j]=his[i][j];
				}
			}
				
			//この時、盤面の世代数は1
			//ジャンプ先の世代数まで更新
			if(n!=1){
				for(int i=0; i<n-1; i++){
					this.alternateGen(this.cells);
				}
				this.prevGen = n-1;
			//ジャンプ先の世代数が1であればprevGenを読み出す
			}else{
				this.prevGen = this.generations.get(this.generations.size()-1);
			}
		}
		this.currentGen = n;	
		this.fireUpdate();
		
		
		
		
	}
	
	//ボタンの有効・無効を返すメソッド
	public boolean isUndoable(){
		if(history.size() <= 0 || this.limitUndo<=0)return false;
		else return true;
	}
	public boolean isJumpable(){
		if(history.size() <= 0 || this.limitJump){
			return false;
		}
		else return true;
	}
	public boolean isNextable(){
		if(this.currentGen >= 10000) return false;
		else return true;
	}

	
	
	public boolean isAlive(int x,int y){
		//範囲外へのアクセスならばreturn
		if( x < 0 || y < 0 || x >= cols || y >= rows) return false;
		if(cells[y+1][x+1]==true) return true;
		else return false;
	}
	
	
	public int getCurrentGen(){
		return this.currentGen;
	}
	
	
}
