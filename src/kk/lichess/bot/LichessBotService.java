package kk.lichess.bot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import kk.lichess.GameRequest;
import kk.lichess.LichessBot;
import kk.lichess.bot.player.BlueBotOne;
import spark.Spark;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class LichessBotService {

    public static void main(String[] args) throws Exception {
        String botId = "blue_bot_one";
        ObjectMapper mapper = new JsonMapper();
        JsonNode config = mapper.readTree(LichessBotService.class.getResourceAsStream("/lichess-bot.json"));
        JsonNode botConfig = config.get(botId);
        String autoToken = botConfig.get("authToken").asText();
        Set<String> friends = new HashSet<>();
        botConfig.get("whitelist").elements().forEachRemaining(node -> friends.add(node.asText()));

        Predicate<GameRequest> gameRequestAccept = gameRequest
                -> friends.contains(gameRequest.getRequesterId())
                || gameRequest.getTime() <= 10 * 60;

        LichessBot lichessBot = new LichessBot(botId, autoToken, BlueBotOne::new, gameRequestAccept);

        lichessBot.start();

        Spark.port(4567);
        Spark.get("lichess-bot/pid", (req, res) -> "" + ProcessHandle.current().pid());

        Spark.get("lichess-bot/games", (req, res) -> lichessBot.getNumberOfGamesInProgress());
        Spark.get("lichess-bot/stop/", (req, res) -> {
            lichessBot.stopAll();
            return "\"ok\"";
        });

        Spark.get("lichess-bot/kill", (req, res) -> {
            System.exit(0);
            return "";
        });

    }
}
