package tv.logisch.oneBlockRace.scoreboard;

import org.bukkit.entity.Player;
import tv.logisch.oneBlockRace.enums.GameState;
import tv.logisch.oneBlockRace.manager.GameManager;
import tv.logisch.oneBlockRace.team.Team;

import java.util.ArrayList;
import java.util.List;

public class Scoreboard extends ScoreboardManager {

    public static List<Scoreboard> scoreboards = new ArrayList<>();

    public Scoreboard(Player player) {
        super(player, "    ①    ");
    }

    @Override
    public void createScoreboard() {
        if(GameManager.get().state().equals(GameState.RUNNING)) {
            this.update();
            return;
        }
        setScore("§0", 12);
        setScore("§3§l» Top 3", 11);
        setScore("§f1§8. §fN/A", 10);
        setScore("§f2§8. §fN/A", 9);
        setScore("§f3§8. §fN/A", 8);
        setScore("§2", 7);
        setScore("§3§l» Rank", 6);
        setScore("§f0§8. §fN/A", 5);
        setScore("§f0§8. §fN/A", 4);
        setScore("§f0§8. §fN/A", 3);
        setScore("§3", 2);
        setScore("§3§l» Server", 1);
        setScore("Logisch.tv", 0);
    }

    @Override
    public void update() {

        if(player == null || !player.isOnline()) return;
        List<Team> top = GameManager.get().teamManager().getTop(3);
        List<String> topNames = new ArrayList<>();
        for(int i = 0; i < 3; i++) {
            if(i < top.size()) {
                Team team = top.get(i);
                String name = team.players().getFirst().getName();
                if(name.length() > 13) {
                    name = name.substring(0, 13);
                }
                topNames.add(i+1 + "§8. §f" + name + " §8(§f" + team.getScore() + "§8)");
            } else {
                topNames.add(i+1 + "§8. §fN/A");
            }
        }
        Team team = GameManager.get().teamManager().getTeam(player);
        if(team == null) {
            return;
        }
        int place = GameManager.get().teamManager().getPlace(team);

        List<String> aroundNames = new ArrayList<>();
        List<Team> around = GameManager.get().teamManager().getAround(team);
        for(int i = 0; i < 3; i++) {
            Team t = around.get(i);
            if(t == null) {
                continue;
            }
            String name = t.players().getFirst().getName();
            if(name.length() > 13) {
                name = name.substring(0, 13);
            }
            aroundNames.add((place-1+i) + "§8. §f" + name + " §8(§f" + t.getScore() + "§8)");
        }

        for(int i = 0; i < 13; i++) {
            removeScore(i);
        }

        int score = 0;
        setScore("Logisch.tv", score++);
        setScore("§3§l» Server", score++);
        setScore("§3", score++);
        for(String name : aroundNames.reversed()) {
            setScore(name, score++);
        }
        setScore("§3§l» Rank", score++);
        setScore("§2", score++);
        for(String name : topNames.reversed()) {
            setScore(name, score++);
        }
        setScore("§3§l» Top 3", score++);
        setScore("§0", score++);
    }

}
