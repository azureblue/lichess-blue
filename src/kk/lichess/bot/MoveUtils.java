package kk.lichess.bot;

import kk.chessbot.Board;
import kk.chessbot.Piece;
import kk.chessbot.wrappers.Move;
import kk.chessbot.wrappers.Position;

public class MoveUtils {
    public static String lichesMove(Move move) {
        String moveString = move.toLongNotation()
                .replace("x", "")
                .replace("ep", "")
                .replace("e.p.", "");
        if (!Character.isDigit(moveString.charAt(1)))
            moveString = moveString.substring(1);
        return moveString;
    }

    public static  Move fromLichess(String lichessMove, Board board) {
        Position from = Position.position(lichessMove.substring(0, 2));
        Position to = Position.position(lichessMove.substring(2, 4));
        String promote = "";

        if (lichessMove.length() > 4)
            promote = lichessMove.substring(4, 5).toUpperCase();

        int fromRaw = from.raw();
        Piece piece = board.piece(fromRaw);

        String capture = "";
        String ep = "";
        int toRaw = to.raw();
        if (piece == Piece.Pawn) {
            if (Position.x(toRaw) != Position.x(fromRaw)) {
                capture = "x";
                if (board.isEmpty(toRaw))
                    ep = "ep";

            }
        } else if (!board.isEmpty(toRaw))
            capture = "x";


        return Move.from("" + (piece == Piece.Pawn ? "" : piece.symbol) + from.toString() + capture + to.toString() + promote + ep);
    }

}