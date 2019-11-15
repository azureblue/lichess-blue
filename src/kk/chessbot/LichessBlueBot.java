package kk.chessbot;

import kk.lichess.LichessBot;

public class LichessBlueBot {
    public static void main(String[] args) throws Exception {
        LichessBot.main(new String[]{"kk.chessbot.player.LichessNotSoRandomPlayer"});
    }
}
