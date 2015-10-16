package cayetano.leaderboard;

/**
 * Created by inakov on 15-9-29.
 */
public class LeaderboardKeyUtils {

    private final static String REDIS_KEY_PREFIX = "leaderboard:";

    public static String resolveKey(String leaderboardId){
        return REDIS_KEY_PREFIX + leaderboardId;
    }

    public static String resolveKey(Long leaderboardId){
        return REDIS_KEY_PREFIX + leaderboardId;
    }

    public static String resolveKey(Integer leaderboardId){
        return REDIS_KEY_PREFIX + leaderboardId;
    }

}
