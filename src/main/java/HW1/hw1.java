package HW1;
import java.util.*;

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
        clearScreen();
        do {
            if (game.getMsg() != null && !game.getMsg().isEmpty()) {
                System.out.println("Info: "+game.getMsg());
                game.setMsg("");
            }
            for(Player s: players){
                System.out.println(s);
            }
            game.showAllChess();
            if (UserChoose()) {
                boolean CurrentPlayer = game.getCurrentPlayer();
                game.setCurrentPlayer(!CurrentPlayer);
            }
            clearScreen();
        }while(game.gameOver());
    }
    private static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
    private static Point getPoint(){
        System.out.print("範例輸入1,3: ");
        String input = scanner.nextLine();
        String[] inputArray = input.split(",");
        if (inputArray.length != 2) {
            System.out.println("輸入格式錯誤");
            return null;
        }
        try {
            int x = Integer.parseInt(inputArray[0].trim()) - 1;
            int y = Integer.parseInt(inputArray[1].trim()) - 1;
            if (x < 0 || x > 3 || y < 0 || y > 7) {
                System.out.println("輸入範圍錯誤，x 應在 1~" + (3 + 1) + "，y 應在 1~" + (7 + 1));
                return null;
            }
            return new Point(x, y);
        } catch (NumberFormatException e) {
            System.out.println("輸入格式錯誤，請確認輸入的是數字");
            return null;
        }
    }
    private static boolean UserChoose(){
        boolean CurrentPlayer = game.getCurrentPlayer();
        System.out.println((CurrentPlayer ? "Player A":"Player B") + " 選擇一個位置 x:(1~4), y:(1~8)");

        Point temp = getPoint();
        if (temp == null) {
            return false;
        }
        Chess chess = game.getChess(temp);
        if(chess.isOpen() && !chess.isEmpty()){
            return UserChooseAction(chess);
        }
        chess.setOpen(true);
        game.setMsg(String.format(chess+" 已翻開"));
        Player[] players = game.getPlayers();
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
class ChessGame extends AbstractGame{
    private Chess[][] board;
    private final Player[] players = new Player[2];
    private boolean currentPlayer = true;
    private int red_left = 16;
    private int black_left = 16;
    private int noProgressCount = 0;
    private Chess lastMovedChess = null;
    private String Msg = "";
    public void resetNoProgressCount() {
        noProgressCount = 0;
        lastMovedChess = null;
    }
    public void incrementNoProgressCount() {
        noProgressCount++;
    }
    public String getMsg() {
        return Msg;
    }
    public void setMsg(String msg) {
        Msg = msg;
    }
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
    public void generateChess(){
        final int x = 4;
        final int y = 8;
        board = new Chess[x][y];
        List<Chess> pieces = new ArrayList<>();
        pieces.add(new Chess(ChessType.KING, ChessType.KING.getWeight(), ChessColor.RED, null));
        pieces.add(new Chess(ChessType.KING, ChessType.KING.getWeight(), ChessColor.BLACK, null));
        for (int i = 0; i < 2; i++) {
            pieces.add(new Chess(ChessType.Mandarins, ChessType.Mandarins.getWeight(), ChessColor.RED, null));
            pieces.add(new Chess(ChessType.Mandarins, ChessType.Mandarins.getWeight(), ChessColor.BLACK, null));
        }

        for (int i = 0; i < 2; i++) {
            pieces.add(new Chess(ChessType.Elephants, ChessType.Elephants.getWeight(), ChessColor.RED, null));
            pieces.add(new Chess(ChessType.Elephants, ChessType.Elephants.getWeight(), ChessColor.BLACK, null));
        }
        for (int i = 0; i < 2; i++) {
            pieces.add(new Chess(ChessType.Rooks, ChessType.Rooks.getWeight(), ChessColor.RED, null));
            pieces.add(new Chess(ChessType.Rooks, ChessType.Rooks.getWeight(), ChessColor.BLACK, null));
        }
        for (int i = 0; i < 2; i++) {
            pieces.add(new Chess(ChessType.Knights, ChessType.Knights.getWeight(), ChessColor.RED, null));
            pieces.add(new Chess(ChessType.Knights, ChessType.Knights.getWeight(), ChessColor.BLACK, null));
        }
        for (int i = 0; i < 2; i++) {
            pieces.add(new Chess(ChessType.Cannons, ChessType.Cannons.getWeight(), ChessColor.RED, null));
            pieces.add(new Chess(ChessType.Cannons, ChessType.Cannons.getWeight(), ChessColor.BLACK, null));
        }

        for (int i = 0; i < 5; i++) {
            pieces.add(new Chess(ChessType.Pawns, ChessType.Pawns.getWeight(), ChessColor.RED, null));
            pieces.add(new Chess(ChessType.Pawns, ChessType.Pawns.getWeight(), ChessColor.BLACK, null));
        }

        Collections.shuffle(pieces, new Random());
        int index = 0;
        for(int i = 0; i< x; i++){
            for(int j = 0; j < y; j++){
                Chess temp = pieces.get(index++);
                board[i][j] = new Chess(temp.getType(), temp.getWeight(), temp.getSide(), new Point(i, j));
            }
        }
    }
    public Chess getChess(Point point){
        return board[point.x()][point.y()];
    }
    public void setChess(Point point, Chess chess){
        board[point.x()][point.y()] = chess;
    }
    public boolean getCurrentPlayer(){
        return currentPlayer;
    }
    public void setCurrentPlayer(boolean currentPlayer){
        this.currentPlayer = currentPlayer;
    }
    public Player[] getPlayers(){
        return players;
    }
    private void capture(Chess src_chess, Chess dest_chess) {
        Point srcPos = src_chess.getPosition();
        Point destPos = dest_chess.getPosition();
        board[destPos.x()][destPos.y()] = src_chess;
        board[srcPos.x()][srcPos.y()] = new Chess(null, 0, null, srcPos);
        src_chess.setPosition(destPos);
    }
    private boolean canMove(Chess src_chess, Chess dest_chess){
        int w = Math.abs(dest_chess.getPosition().x() - src_chess.getPosition().x());
        int h = Math.abs(dest_chess.getPosition().y() - src_chess.getPosition().y());
        if((w == 1) ^ (h == 1)){
            return true;
        }
        setMsg(String.format(src_chess+"只能移動一格"));
        return false;
    }
    @Override
    public void setPlayers(Player a, Player b) {
        this.players[0] = a;
        this.players[1] = b;
    }
    @Override
    public boolean gameOver() {
        Player thisPlayer = players[0].getSide() == ChessColor.RED ? players[0] : players[1];
        Player otherPlayer = thisPlayer == players[0] ? players[1] : players[0];
        if(red_left == 0 ){
            setMsg(String.format(otherPlayer.getName() + " 贏了"));
            return false;
        }
        if(black_left == 0 ){
            setMsg(String.format(thisPlayer.getName() + " 贏了"));
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
            if (dest_chess.getSide() == ChessColor.RED) {
                red_left--;
            } else {
                black_left--;
            }
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
        if (dest_chess.getSide() == ChessColor.RED) {
            red_left--;
        } else {
            black_left--;
        }
        setMsg(String.format(src_chess + " 吃掉 " + dest_chess));
        return true;
    }
}
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
enum ChessColor{
    RED,BLACK
}
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
class Chess {
    private final ChessType name;
    private final int weight;
    private final ChessColor side;
    private Point position;
    private boolean isOpen = false;
    public Chess(ChessType name,int weight, ChessColor side,Point position){
        this.name = name;
        this.weight = weight;
        this.side = side;
        this.position = position;
    }
    public int getWeight(){
        return weight;
    }
    public boolean isEmpty(){
        return name == null && side == null;
    }
    public ChessType getType() {
        return name;
    }
    public ChessColor getSide() {
        return side;
    }
    public Point getPosition() {
        return position;
    }
    public void setPosition(Point position) {
        this.position = position;
    }
    public boolean isOpen() {
        return isOpen;
    }
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
        return String.format("Player %s, side %s", name, side);
    }
}

abstract class AbstractGame{
    public abstract void setPlayers(Player a, Player b);
    public abstract boolean gameOver();
    public abstract boolean move(Chess src_chess, Chess dest_chess);
}
