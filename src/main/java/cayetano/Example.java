package cayetano;

import cayetano.leaderboard.LadderData;
import cayetano.leaderboard.LeaderboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by inakov on 15-9-28.
 */
@RestController
public class Example {

    @Autowired
    LeaderboardService leaderboardService;

    @RequestMapping("/")
    String home(){
        return "Hello world!";
    }

    @RequestMapping(value = "/rank", method = RequestMethod.POST)
    public void rankCustomer(@RequestParam Long leaderboardId, @RequestParam String customerId){
        leaderboardService.addCustomer(leaderboardId, customerId, 0d);
    }

    @RequestMapping(value = "/updateScore", method = RequestMethod.POST)
    public @ResponseBody Double updateScore(@RequestParam Long leaderboardId, @RequestParam String customerId){
        return leaderboardService.incrementScore(leaderboardId, customerId, 1.3);
    }

    @RequestMapping(value = "/leaderboard", method = RequestMethod.GET)
    public @ResponseBody List<LadderData> updateScore(@RequestParam Long leaderboardId){
        return leaderboardService.loadLadder(leaderboardId, 0, 10);
    }

}
