package tv.logisch.game.team;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Accessors(fluent = true)
public class TeamManager {

    private final List<Team> teams;
    private List<Team> cachedTopTeams = new ArrayList<>();

    public TeamManager() {
        this.teams = new ArrayList<>();
    }

    public void teleportAll() {
        for (Team team : teams) {
            if (team.getIsland() != null) {
                for (Player player : team.players()) {
                    player.teleport(team.getIsland());
                }
            }
        }
    }
    public void addTeam(Team team) {
        if (!teams.contains(team)) {
            teams.add(team);
        }
    }
    public void removeTeam(Team team) {
        teams.remove(team);
    }

    public List<Team> getTop() {
        List<Team> topTeams = new ArrayList<>();
        for (Team team : teams) {
            if (!team.players().isEmpty()) {
                topTeams.add(team);
            }
        }
        topTeams.sort((t1, t2) -> Integer.compare(t2.getScore(), t1.getScore()));
        return topTeams;
    }
    public List<Team> getTop(int limit) {
        List<Team> topTeams = getTop();
        return topTeams.size() > limit ? topTeams.subList(0, limit) : topTeams;
    }

    public void recalcTop() {
        this.cachedTopTeams = getTop();
    }

    public List<Team> getCachedTop(int limit) {
        if (cachedTopTeams == null || cachedTopTeams.isEmpty()) {
            recalcTop();
        }
        return cachedTopTeams.size() > limit ? cachedTopTeams.subList(0, limit) : new ArrayList<>(cachedTopTeams);
    }

    public List<Team> getCachedTopAll() {
        if (cachedTopTeams == null || cachedTopTeams.isEmpty()) {
            recalcTop();
        }
        return new ArrayList<>(cachedTopTeams);
    }

    public Team getTeam(Player player) {
        for (Team team : teams) {
            if (team.isPlayerInTeam(player)) {
                return team;
            }
        }
        return null;
    }

    public int getPlace(Team team) {
        List<Team> topTeams = getCachedTopAll();
        for (int i = 0; i < topTeams.size(); i++) {
            if (topTeams.get(i).equals(team)) {
                return i + 1; // Place starts at 1
            }
        }
        return -1;
    }

    public List<Team> getAround(Team team) {
        List<Team> topTeams = getCachedTopAll();
        List<Team> aroundTeams = new ArrayList<>();
        int index = topTeams.indexOf(team);
        if (index > 0) {
            aroundTeams.add(topTeams.get(index - 1));
        } else {
            aroundTeams.add(null);
        }
        aroundTeams.add(team);
        if (index < topTeams.size() - 1) {
            aroundTeams.add(topTeams.get(index + 1));
        } else {
            aroundTeams.add(null);
        }
        return aroundTeams;
    }

    public void setupCoins() {
        for(Team team : this.teams) {
            team.coins(team.getScore());
        }
    }

}
