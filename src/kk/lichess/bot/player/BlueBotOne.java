package kk.lichess.bot.player;

import kk.chessbot.Board;
import kk.chessbot.Fen;
import kk.chessbot.Side;
import kk.chessbot.player.AlphaBetaPlayer;
import kk.lichess.bot.MoveUtils;
import kk.lichess.bots.api.ChessPlayer;

public class BlueBotOne implements ChessPlayer {

    private AlphaBetaPlayer player;
    private Board tmpBoard = new Board();

    @Override
    public void gameStarts(String fen, boolean playerIsWhite, int remainingTime) {
        player = new AlphaBetaPlayer(Fen.fen(fen).createBoard(), playerIsWhite ? Side.White : Side.Black);
    }

    @Override
    public String makeMove(int remainingTime) {
        return MoveUtils.lichesMove(player.makeMove(remainingTime));
    }

    @Override
    public void applyMove(String move) {
        player.getBoard(tmpBoard);
        player.applyMove(MoveUtils.fromLichess(move, tmpBoard));
    }
}
