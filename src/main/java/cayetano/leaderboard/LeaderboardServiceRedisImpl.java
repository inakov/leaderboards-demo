package cayetano.leaderboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by inakov on 15-9-29.
 */
@Service
public class LeaderboardServiceRedisImpl implements LeaderboardService{

    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final List<LadderData> EMPTY_LADDER = Collections.emptyList();

    private final StringRedisTemplate redisTemplate;
    private final ZSetOperations<String, String> operations;

    @Autowired
    public LeaderboardServiceRedisImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.operations = redisTemplate.opsForZSet();
    }

    public void deleteLeaderboard(Long leaderboardId){
        final String leaderboardKey = LeaderboardKeyUtils.resolveKey(leaderboardId);
        redisTemplate.delete(leaderboardKey);
    }

    public Long getTotalCustomersIn(Long leaderboardId){
        final String leaderboardKey = LeaderboardKeyUtils.resolveKey(leaderboardId);
        return operations.zCard(leaderboardKey);
    }

    public Integer getTotalPagesIn(Long leaderboardId, Integer pageSize) {
        final Long totalCustomersCount = getTotalCustomersIn(leaderboardId);
        if(totalCustomersCount == null || pageSize == null || pageSize < 1)
            return 0;

        return (int) Math.ceil(totalCustomersCount/pageSize);
    }

    public Long getTotalCustomersInScoreRangeIn(Long leaderboardId, Double minScore, Double maxScore) {
        final String leaderboardKey = LeaderboardKeyUtils.resolveKey(leaderboardId);
        return operations.count(leaderboardKey, minScore, maxScore);
    }

    public Boolean addCustomer(Long leaderboardId, String customerId, Double initialScore) {
        final String leaderboardKey = LeaderboardKeyUtils.resolveKey(leaderboardId);
        return operations.add(leaderboardKey, customerId, initialScore);
    }

    public Double getScore(Long leaderboardId, String customerId){
        final String leaderboardKey = LeaderboardKeyUtils.resolveKey(leaderboardId);
        return operations.score(leaderboardKey, customerId);
    }

    public Double incrementScore(Long leaderboardId, String customerId, Double delta) {
        final String leaderboardKey = LeaderboardKeyUtils.resolveKey(leaderboardId);
        return operations.incrementScore(leaderboardKey, customerId, delta);
    }

    public Boolean checkMemberOf(Long leaderboardId, String customerId){
        final String leaderboardKey = LeaderboardKeyUtils.resolveKey(leaderboardId);
        return operations.score(leaderboardKey, customerId) != null;
    }

    public Long getRank(Long leaderboardId, String customerId){
        final String leaderboardKey = LeaderboardKeyUtils.resolveKey(leaderboardId);
        Long rank = null;

        final Long redisRank = operations.reverseRank(leaderboardKey, customerId);
        if(redisRank != null)
            rank = redisRank + 1;

        return rank;
    }

    public Long removeCustomersInScoreRange(Long leaderboardId, Double minScore, Double maxScore){
        final String leaderboardKey = LeaderboardKeyUtils.resolveKey(leaderboardId);
        return operations.removeRangeByScore(leaderboardKey, minScore, maxScore);
    }

    public List<LadderData> loadLadder(Long leaderboardId, Integer currentPage, Integer pageSize) {
        final String leaderboardKey = LeaderboardKeyUtils.resolveKey(leaderboardId);
        final Integer totalPages = getTotalPagesIn(leaderboardId, pageSize);

        LadderPage page = new LadderPage(currentPage, pageSize, totalPages);

        Set<ZSetOperations.TypedTuple<String>> rawLadderData =
                operations.reverseRangeWithScores(leaderboardKey, page.getStartingOffset(), page.getEndingOffset());

        List<LadderData> ladder = buildLadderFromRawData(leaderboardId, rawLadderData);
        return ladder;
    }

    public List<LadderData> ladderAroundCustomer(Long leaderboardId, String customerId, Integer pageSize){
        final Long customerRank = getRank(leaderboardId, customerId);

        if(customerRank == null)
            return EMPTY_LADDER;

        if(pageSize == null || pageSize < 1)
            pageSize = DEFAULT_PAGE_SIZE;

        int startingOffset = customerRank.intValue() - (pageSize / 2);
        if(startingOffset < 0)
            startingOffset = 0;

        int endingOffset = (startingOffset + pageSize) - 1;

        final String leaderboardKey = LeaderboardKeyUtils.resolveKey(leaderboardId);
        Set<ZSetOperations.TypedTuple<String>> rawLadderData =
                operations.reverseRangeWithScores(leaderboardKey, startingOffset, endingOffset);

        List<LadderData> ladder = buildLadderFromRawData(leaderboardId, rawLadderData);
        return ladder;
    }

    private List<LadderData> buildLadderFromRawData(Long leaderboardId,
                                                    Set<ZSetOperations.TypedTuple<String>> rawLadderData){
        List<LadderData> ladder = new ArrayList<>();
        for(ZSetOperations.TypedTuple<String> ladderRow : rawLadderData){
            final String customerId = ladderRow.getValue();
            final Double customerScore = ladderRow.getScore();
            final Long customerRank = getRank(leaderboardId, customerId);
            LadderData ladderData = new LadderData(customerRank, customerId, customerScore);
            ladder.add(ladderData);
        }

        return ladder;
    }


}
