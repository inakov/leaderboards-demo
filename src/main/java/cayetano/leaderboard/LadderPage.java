package cayetano.leaderboard;

/**
 * Created by inakov on 15-9-30.
 */
public class LadderPage {
    private static final int DEFAULT_PAGE_SIZE = 10;

    private long startingOffset;
    private final long endingOffset;
    private Integer currentPage;
    private Integer pageSize;

    public LadderPage(Integer currentPage, Integer pageSize, Integer totalPages){
        if (currentPage < 1) {
            currentPage = 1;
        }

        if (currentPage > totalPages) {
            currentPage = totalPages;
        }

        if (pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        this.currentPage = currentPage;
        this.pageSize = pageSize;

        int indexForRedis = currentPage - 1;
        this.startingOffset = indexForRedis * pageSize;
        if (this.startingOffset < 0) {
            this.startingOffset = 0;
        }
        this.endingOffset = (startingOffset + pageSize) - 1;
    }

    public long getEndingOffset() {
        return endingOffset;
    }

    public long getStartingOffset() {
        return startingOffset;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }
}
