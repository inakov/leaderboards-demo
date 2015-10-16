package cayetano.leaderboard;

import java.util.List;

/**
 * Created by inakov on 15-9-30.
 */
public interface LeaderboardService {

    void deleteLeaderboard(Long leaderboardId);

    Boolean addCustomer(Long leaderboardId, String customerId, Double initialScore);

    Double getScore(Long leaderboardId, String customerId);

    Double incrementScore(Long leaderboardId, String customerId, Double delta);

    Boolean checkMemberOf(Long leaderboardId, String customerId);

    List<LadderData> loadLadder(Long leaderboardId, Integer currentPage, Integer pageSize);

    List<LadderData> ladderAroundCustomer(Long leaderboardId, String customerId, Integer pageSize);
}
