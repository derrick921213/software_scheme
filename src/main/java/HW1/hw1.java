package HW1;
import java.util.*;

/**
 * 象棋進入點
 */
public class hw1 {
    static Scanner scanner = new Scanner(System.in);
    static ChessGame game = new ChessGame();
    static boolean isInit = true;
    public static void main(String[] args) {
        Player[] players = new Player[2];
        players[0] = new Player("A", null);
        players[1] = new Player("B", null);
        game.setPlayers(players[0], players[1]);
        game.generateChess();
        // 遊戲第一次開始，先清空畫面
        clearScreen();
        while(true) {
            if (!game.gameOver()){
                System.out.println("Info: "+game.getMsg());
                break;
            }
            // 檢查是否有訊息要顯示
            if (game.getMsg() != null && !game.getMsg().isEmpty()) {
                System.out.println("Info: "+game.getMsg());
                game.setMsg("");
            }
            // 顯示玩家資訊
            for(Player s: players){
                System.out.println(s);
            }
            // 顯示棋盤
            game.showAllChess();
            // 玩家開始操作
            if (UserChoose()) {
                // 切換玩家
                boolean CurrentPlayer = game.getCurrentPlayer();
                game.setCurrentPlayer(!CurrentPlayer);
            }
            clearScreen();
        }
    }

    /**
     * 清空畫面
     * @return void
     */
    private static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /**
     * 取得使用者輸入的座標
     * @return Point
     */
    private static Point getPoint(){
        System.out.print("範例輸入1,3: ");
        String input = scanner.nextLine();
        // 輸入格式為 x,y
        String[] inputArray = input.split(",");
        // 確認輸入格式
        if (inputArray.length != 2) {
            game.setMsg("輸入格式錯誤");
            return null;
        }
        // 確認輸入的是數字
        try {
            int x = Integer.parseInt(inputArray[0].trim()) - 1;
            int y = Integer.parseInt(inputArray[1].trim()) - 1;
            if (x < 0 || x > 3 || y < 0 || y > 7) {
                game.setMsg(String.format("輸入範圍錯誤，x 應在 1~" + (3 + 1) + "，y 應在 1~" + (7 + 1)));
                return null;
            }
            return new Point(x, y);
        } catch (NumberFormatException e) {
            game.setMsg("輸入格式錯誤，請確認輸入的是數字");
            return null;
        }
    }
    /**
     * 使用者選擇
     * @return boolean
     */
    private static boolean UserChoose(){
        // 獲取當前玩家
        boolean CurrentPlayer = game.getCurrentPlayer();
        // 獲取所有玩家
        Player[] players = game.getPlayers();
        System.out.println("Player "+(CurrentPlayer ? players[0].getName():players[1].getName()) + " 選擇一個位置 x:(1~4), y:(1~8)");
        Point temp = getPoint();
        if (temp == null) {
            return false;
        }
        // 將使用者選取的位置中棋子拿起來
        Chess chess = game.getChess(temp);
        // 如果棋子已經翻開且不是空的，則進行下一步
        if(chess.isOpen() && !chess.isEmpty()){
            return UserChooseAction(chess);
        }
        chess.setOpen(true);
        game.setMsg(String.format(chess+" 已翻開"));

        // 如果玩家還沒選擇陣營，則進行選擇
        if(players[CurrentPlayer?0:1].getSide() == null && isInit){
            players[0].setSide(chess.getSide());
            if (players[0].getSide() == ChessColor.RED){
                players[1].setSide(ChessColor.BLACK);
            }
            else{
                players[1].setSide(ChessColor.RED);
            }
            isInit = false;
        }
        return true;
    }

    /**
     * 玩家將指定以翻開的棋子動作
     * @param src_chess
     * @return boolean
     */
    private static boolean UserChooseAction(Chess src_chess){
        System.out.println("請輸入目的位置 x:(1~4), y:(1~8)");
        Point temp = getPoint();
        if (temp == null) {
            return false;
        }
        Chess dest_chess = game.getChess(temp);
        return game.move(src_chess, dest_chess);
    }
}

/**
 * 象棋棋盤
 */
class ChessGame extends AbstractGame{
    private Chess[][] board;
    private final Player[] players = new Player[2];
    private boolean currentPlayer = true;
    private int red_left = 16;
    private int black_left = 16;
    private int noProgressCount = 0;
    private Chess lastMovedChess = null;
    private String Msg = "";

    /**
     * 重置沒有進展的次數
     * @return void
     */
    public void resetNoProgressCount() {
        noProgressCount = 0;
        lastMovedChess = null;
    }

    /**
     * 增加沒有進展的次數
     * @return void
     */
    public void incrementNoProgressCount() {
        noProgressCount++;
    }
    /**
     * 取得訊息
     * @return String
     */
    public String getMsg() {
        return Msg;
    }
    /**
     * 設定訊息
     * @param msg
     * @return void
     */
    public void setMsg(String msg) {
        Msg = msg;
    }
    /**
     * 顯示所有棋子
     * @return void
     */
    public void showAllChess() {
        int maxLen = 0;
        for (Chess[] chess : board) {
            for (Chess value : chess) {
                int len = value.toString().length();
                if (len > maxLen) {
                    maxLen = len;
                }
            }
        }
        String format = "%-" + (maxLen + 2) + "s";
        for (Chess[] chess : board) {
            for (Chess value : chess) {
                System.out.printf(format, value.toString());
            }
            System.out.println();
        }
    }
    /**
     * 生成象棋
     * @return void
     */
    public void generateChess(){
        final int x = 4;
        final int y = 8;
        board = new Chess[x][y];
        List<Chess> pieces = new ArrayList<>();
        // 生成將、帥
        pieces.add(new Chess(ChessType.KING, ChessType.KING.getWeight(), ChessColor.RED, null));
        pieces.add(new Chess(ChessType.KING, ChessType.KING.getWeight(), ChessColor.BLACK, null));
        // 生成士、仕
        for (int i = 0; i < 2; i++) {
            pieces.add(new Chess(ChessType.Mandarins, ChessType.Mandarins.getWeight(), ChessColor.RED, null));
            pieces.add(new Chess(ChessType.Mandarins, ChessType.Mandarins.getWeight(), ChessColor.BLACK, null));
        }
        // 生成象、相
        for (int i = 0; i < 2; i++) {
            pieces.add(new Chess(ChessType.Elephants, ChessType.Elephants.getWeight(), ChessColor.RED, null));
            pieces.add(new Chess(ChessType.Elephants, ChessType.Elephants.getWeight(), ChessColor.BLACK, null));
        }
        // 生成車、俥
        for (int i = 0; i < 2; i++) {
            pieces.add(new Chess(ChessType.Rooks, ChessType.Rooks.getWeight(), ChessColor.RED, null));
            pieces.add(new Chess(ChessType.Rooks, ChessType.Rooks.getWeight(), ChessColor.BLACK, null));
        }
        // 生成馬、傌
        for (int i = 0; i < 2; i++) {
            pieces.add(new Chess(ChessType.Knights, ChessType.Knights.getWeight(), ChessColor.RED, null));
            pieces.add(new Chess(ChessType.Knights, ChessType.Knights.getWeight(), ChessColor.BLACK, null));
        }
        // 生成砲、炮
        for (int i = 0; i < 2; i++) {
            pieces.add(new Chess(ChessType.Cannons, ChessType.Cannons.getWeight(), ChessColor.RED, null));
            pieces.add(new Chess(ChessType.Cannons, ChessType.Cannons.getWeight(), ChessColor.BLACK, null));
        }
        // 生成卒、兵
        for (int i = 0; i < 5; i++) {
            pieces.add(new Chess(ChessType.Pawns, ChessType.Pawns.getWeight(), ChessColor.RED, null));
            pieces.add(new Chess(ChessType.Pawns, ChessType.Pawns.getWeight(), ChessColor.BLACK, null));
        }
        // 洗牌
        Collections.shuffle(pieces, new Random());
        // 放置棋子
        int index = 0;
        for(int i = 0; i< x; i++){
            for(int j = 0; j < y; j++){
                Chess temp = pieces.get(index++);
                board[i][j] = new Chess(temp.getType(), temp.getWeight(), temp.getSide(), new Point(i, j));
            }
        }
    }
    /**
     * 取得棋子
     * @param point
     * @return Chess
     */
    public Chess getChess(Point point){
        return board[point.x()][point.y()];
    }
    /**
     * 設定棋子
     * @param point
     * @param chess
     * @return void
     */
    public void setChess(Point point, Chess chess){
        board[point.x()][point.y()] = chess;
    }
    /**
     * 取得當前玩家
     * @return boolean
     */
    public boolean getCurrentPlayer(){
        return currentPlayer;
    }
    /**
     * 設定當前玩家
     * @param currentPlayer
     * @return void
     */
    public void setCurrentPlayer(boolean currentPlayer){
        this.currentPlayer = currentPlayer;
    }
    /**
     * 取得所有玩家
     * @return Player[]
     */
    public Player[] getPlayers(){
        return players;
    }
    /**
     * 吃掉對方的棋子
     * @param src_chess
     * @param dest_chess
     * @return void
     */
    private void capture(Chess src_chess, Chess dest_chess) {
        Point srcPos = src_chess.getPosition();
        Point destPos = dest_chess.getPosition();
        if (dest_chess.getSide() == ChessColor.RED){
            red_left--;
        }
        if (dest_chess.getSide() == ChessColor.BLACK){
            black_left--;
        }
        board[destPos.x()][destPos.y()] = src_chess;
        board[srcPos.x()][srcPos.y()] = new Chess(null, 0, null, srcPos);
        src_chess.setPosition(destPos);
    }
    /**
     * 檢查是否可以移動
     * @param src_chess
     * @param dest_chess
     * @return boolean
     */
    private boolean canMove(Chess src_chess, Chess dest_chess){
        int w = Math.abs(dest_chess.getPosition().x() - src_chess.getPosition().x());
        int h = Math.abs(dest_chess.getPosition().y() - src_chess.getPosition().y());
        if((w == 1) ^ (h == 1)){
            return true;
        }
        setMsg(String.format(src_chess+"只能移動一格"));
        return false;
    }
    /**
     * 設置玩家
     * @return void
     */
    @Override
    public void setPlayers(Player a, Player b) {
        this.players[0] = a;
        this.players[1] = b;
    }
    /**
     * 判斷遊戲是否結束
     * @return boolean
     */
    @Override
    public boolean gameOver() {
        Player thisPlayer = players[0].getSide() == ChessColor.RED ? players[0] : players[1];
        Player otherPlayer = thisPlayer == players[0] ? players[1] : players[0];
        if(red_left == 0 ){
            setMsg(String.format("Player "+otherPlayer.getName() + " 贏了"));
            return false;
        }
        if(black_left == 0 ){
            setMsg(String.format("Player " +thisPlayer.getName() + " 贏了"));
            return false;
        }
        int pieceCount = 0;
        int redSum = 0;
        int blackSum = 0;
        for (Chess[] chess : board) {
            for (Chess piece : chess) {
                if (piece.getType() != null) {
                    pieceCount++;
                    if (piece.getSide() == ChessColor.RED) {
                        redSum += piece.getWeight();
                    } else if (piece.getSide() == ChessColor.BLACK) {
                        blackSum += piece.getWeight();
                    }
                }
            }
        }
        if (pieceCount == 3) {
            setMsg("只剩下三隻棋子，依棋子等級總和決定勝負");
            if (redSum > blackSum) {
                setMsg(String.format(thisPlayer.getName() + " 贏了"));
            } else if (blackSum > redSum) {
                setMsg(String.format(otherPlayer.getName() + " 贏了"));
            } else {
                setMsg("和局");
            }
            return false;
        }

        if (noProgressCount >= 50) {
            setMsg("連續50步沒有翻子或吃子，和局");
            return false;
        }
        return true;
    }
    /**
     * 移動棋子
     * @param src_chess
     * @param dest_chess
     * @return boolean
     */
    @Override
    public boolean move(Chess src_chess, Chess dest_chess) {
        if (src_chess.getSide() == null && src_chess.getType() == null) {
            setMsg("輸入位置錯誤");
            return false;
        }
        if (src_chess.getSide() != players[currentPlayer?0:1].getSide()) {
            // 假設 src_chess 不是當前玩家的棋子
            setMsg("不是你的陣營");
            return false;
        }
        int srcX = src_chess.getPosition().x();
        int srcY = src_chess.getPosition().y();
        int destX = dest_chess.getPosition().x();
        int destY = dest_chess.getPosition().y();
        if (srcX != destX && srcY != destY) {
            setMsg(String.format(src_chess+"只能直線移動"));
            return false;
        }
        if(dest_chess.getSide() == null && dest_chess.getType() == null){
            // 假設 dest_chess 是空的, 直接移動
            if(!canMove(src_chess, dest_chess)){
                return false;
            }
            capture(src_chess, dest_chess);
            setMsg(String.format(src_chess + " 移動到 " + dest_chess));
            if (lastMovedChess != src_chess) {
                lastMovedChess = src_chess;
            }
            incrementNoProgressCount();
            return true;
        }
        if (src_chess.getSide() == dest_chess.getSide()) {
            // 假設 src_chess 與 dest_chess 是同一方的棋子
            setMsg("不能吃自己的棋子");
            return false;
        }

        if (src_chess.getType() == ChessType.Cannons) {
            int count = 0;
            if (srcX == destX) {
                int minY = Math.min(srcY, destY);
                int maxY = Math.max(srcY, destY);
                for (int i = minY + 1; i < maxY; i++) {
                    if (board[srcX][i].getType() != null) {
                        count++;
                    }
                }
            } else {
                int minX = Math.min(srcX, destX);
                int maxX = Math.max(srcX, destX);
                for (int i = minX + 1; i < maxX; i++) {
                    if (board[i][srcY].getType() != null) {
                        count++;
                    }
                }
            }
            if (count != 1) {
                setMsg(String.format(src_chess+"吃棋時中間必須隔一個棋子，目前隔了 " + count + " 個"));
                return false;
            }
            resetNoProgressCount();
            capture(src_chess, dest_chess);
            setMsg(String.format(src_chess + " 吃掉 " + dest_chess));
            return true;
        }
        if (src_chess.getWeight() < dest_chess.getWeight()) {
            // 假設 src_chess 的權重小於 dest_chess 的權重
            if (src_chess.getType() == ChessType.Pawns && dest_chess.getType() == ChessType.KING) {
                resetNoProgressCount();
                capture(src_chess, dest_chess);
                setMsg(String.format(src_chess + " 吃掉 " + dest_chess));
                return true;
            }
            setMsg(String.format(src_chess + " 無法吃掉 " + dest_chess));
            return false;
        }
        if(src_chess.getType() == ChessType.KING && dest_chess.getType() == ChessType.Pawns){
            // 假設 src_chess 是將，且 dest_chess 是卒
            setMsg(String.format(src_chess + " 無法吃掉 " + dest_chess));
            return false;
        }
        if(!canMove(src_chess, dest_chess)){
            return false;
        }
        resetNoProgressCount();
        capture(src_chess, dest_chess);
        setMsg(String.format(src_chess + " 吃掉 " + dest_chess));
        return true;
    }
}
/**
 * 棋子種類
 */
enum ChessType {
    KING(7),
    Mandarins(6),
    Elephants(5),
    Rooks(4),
    Knights(3),
    Cannons(2),
    Pawns(1);

    private final int weight;

    ChessType(int weight) {
        this.weight = weight;
    }
    public int getWeight() {
        return weight;
    }

}
/**
 * 棋子顏色
 */
enum ChessColor{
    RED,BLACK
}
/**
 * 座標
 */
record Point(int x, int y){
    public Point add(Point point){
        return new Point(this.x + point.x, this.y + point.y);
    }
    public Point add(int z){
        return new Point(this.x + z, this.y + z);
    }
    public Point substract(Point point){
        return new Point(this.x - point.x, this.y - point.y);
    }
    public Point abs(Point point){
        if (this.x == point.x || this.y == point.y){
            return new Point(Math.abs(this.x - point.x), Math.abs(this.y - point.y));
        }
        return new Point(0, 0);
    }
}
/**
 * 棋子類別
 */
class Chess {
    private final ChessType name;
    private final int weight;
    private final ChessColor side;
    private Point position;
    private boolean isOpen = false;
    /**
     * 棋子建構子
     * @param name
     * @param weight
     * @param side
     * @param position
     */
    public Chess(ChessType name,int weight, ChessColor side,Point position){
        this.name = name;
        this.weight = weight;
        this.side = side;
        this.position = position;
    }
    /**
     * 取得權重
     * @return int
     */
    public int getWeight(){
        return weight;
    }
    /**
     * 檢查是否為空
     * @return boolean
     */
    public boolean isEmpty(){
        return name == null && side == null;
    }
    /**
     * 取得棋子種類
     * @return ChessType
     */
    public ChessType getType() {
        return name;
    }
    /**
     * 取得棋子顏色
     * @return ChessColor
     **/
    public ChessColor getSide() {
        return side;
    }
    /**
     * 獲取棋子位置
     * @return void
     */
    public Point getPosition() {
        return position;
    }
    /**
     * 設置棋子位置
     * @param position
     * @return void
     */
    public void setPosition(Point position) {
        this.position = position;
    }
    /**
     * 檢查是否翻開
     * @return boolean
     */
    public boolean isOpen() {
        return isOpen;
    }
    /**
     * 設置是否翻開
     * @param open
     * @return void
     */
    public void setOpen(boolean open) {
        isOpen = open;
    }
    @Override
    public String toString(){
        boolean isBlack = side == ChessColor.BLACK;
        if (name == null && side == null){
            return "＿";
        }
        if (!isOpen){
            return "Ｘ";
        }
        assert name != null;
        return switch (name) {
            case KING -> isBlack ? "將" : "帥";
            case Mandarins -> isBlack ? "士" : "仕";
            case Elephants -> isBlack ? "象" : "相";
            case Rooks -> isBlack ? "車" : "俥";
            case Knights -> isBlack ? "馬" : "傌";
            case Cannons -> isBlack ? "砲" : "炮";
            case Pawns -> isBlack ? "卒" : "兵";
        };
    }
}

class Player{
    private final String name;
    private ChessColor side;
    public Player(String name, ChessColor side){
        this.name = name;
        this.side = side;
    }
    public String getName(){
        return name;
    }
    public ChessColor getSide(){
        return side;
    }
    public void setSide(ChessColor side){
        this.side = side;
    }
    @Override
    public String toString(){
        return String.format("Player %s, side %s", name, side==null?"未選定":side);
    }
}

abstract class AbstractGame{
    public abstract void setPlayers(Player a, Player b);
    public abstract boolean gameOver();
    public abstract boolean move(Chess src_chess, Chess dest_chess);
}
