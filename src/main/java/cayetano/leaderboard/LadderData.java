package cayetano.leaderboard;

/**
 * Created by inakov on 15-9-29.
 */
public class LadderData {

    private final Long rank;
    private final String customerId;
    private final Double score;

    public LadderData(Long rank, String customerId, Double score) {
        this.rank = rank;
        this.customerId = customerId;
        this.score = score;
    }

    public Long getRank() {
        return rank;
    }

    public String getCustomerId() {
        return customerId;
    }

    public Double getScore() {
        return score;
    }
}
