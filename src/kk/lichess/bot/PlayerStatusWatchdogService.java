package kk.lichess.bot;

import kk.lichess.Log;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

public class PlayerStatusWatchdogService {
    private static final Pattern PLAYER_OFFLINE_REGEX
            = Pattern.compile("class\\s*=\\s*\"[^\"]*((offline)|(user-link))[^\"]*((offline)|(user-link))[^\"]*\"");

    private static final Pattern PLAYER_ONLINE_REGEX
            = Pattern.compile("class\\s*=\\s*\"[^\"]*((online)|(user-link))[^\"]*((online)|(user-link))[^\"]*\"");
    private final Set<String> watchedPlayers = new HashSet<>();
    private Timer timer = new Timer();

    public synchronized void watchPlayerStatus(String playerId, long intervalInMillis, BiConsumer<Boolean, Throwable> playerOnlineStatusConsumer) {
        if (watchedPlayers.contains(playerId)) {
            throw new IllegalStateException("player " + playerId + " is already watched");
        }

        watchedPlayers.add(playerId);

        timer.scheduleAtFixedRate(new PlayerWatchTimerTask(playerId, playerOnlineStatusConsumer),
                intervalInMillis, intervalInMillis);
    }

    private class PlayerWatchTimerTask extends TimerTask {
        final String userName;
        final BiConsumer<Boolean, Throwable> playerOnlineStatusConsumer;

        private PlayerWatchTimerTask(String userName, BiConsumer<Boolean, Throwable> playerOnlineStatusConsumer) {
            this.userName = userName;
            this.playerOnlineStatusConsumer = playerOnlineStatusConsumer;
        }

        @Override
        public void run() {
            Log.v("checking users status: " + userName);
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL("https://lichess.org/@/" + userName).openConnection();
                String response = new String(connection.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                if (PLAYER_OFFLINE_REGEX.matcher(response).find()) {
                    Log.v("user " + userName + " offline");
                    playerOnlineStatusConsumer.accept(false, null);
                    return;
                }

                if (PLAYER_ONLINE_REGEX.matcher(response).find()) {
                    Log.v("user " + userName + " online");
                    playerOnlineStatusConsumer.accept(true, null);
                    return;
                }

                Log.e("user status error");
                playerOnlineStatusConsumer.accept(null, new IllegalStateException("unble to check user status"));

            } catch (java.io.IOException e) {
                Log.e("user status error", e);
                playerOnlineStatusConsumer.accept(null, e);
            }
        }
    }

}
