package orkunsoylu.premierleague;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private TableLayout leagueTable,leaguePredictionTable,weekResultsTable;
    private TextView weekResultsLabel,leaguePredictionsLabel;
    private int weekCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //League Initialization
        weekCounter = 0;

        final ArrayList<Team> teams = new ArrayList<Team>();
        teams.add(new Team("Arsenal",1.8,1.3));
        teams.add(new Team("Chelsea",1.8,0.7));
        teams.add(new Team("Tottenham Hotspur",2.0,0.9));
        teams.add(new Team("West Ham United",1.3,1.8));

        final Match[][] fixture = createFixture(teams);

        //UI Initialization
        leagueTable = findViewById(R.id.league_table);

        for (int i=0;i<teams.size();i++){
            populateLeagueTableRowWithTeam((TableRow) leagueTable.getChildAt(i+1),teams.get(i));
        }
        weekResultsTable = findViewById(R.id.week_results_table);
        leaguePredictionTable = findViewById(R.id.league_prediction_table);

        leaguePredictionsLabel = findViewById(R.id.league_prediction_label);
        weekResultsLabel = findViewById(R.id.week_results_label);

        Button playAllButton = findViewById(R.id.play_all_button);
        final Button nextWeekButton = findViewById(R.id.next_week_button);

        playAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                while(weekCounter < 6){
                    simulateOneWeek(fixture[weekCounter]);
                    updateWeekResultsTable(fixture[weekCounter]);
                    weekCounter++;
                }

                updateLeagueTable(teams);
            }
        });

        nextWeekButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (weekCounter < 6) {
                    simulateOneWeek(fixture[weekCounter]);
                    updateWeekResultsTable(fixture[weekCounter]);
                    updateLeagueTable(teams);
                    if (weekCounter > 2) {
                        leaguePredictionsLabel.setVisibility(View.VISIBLE);
                        leaguePredictionTable.setVisibility(View.VISIBLE);
                        updateLeaguePredictionTable(teams);
                    }
                    weekCounter++;
                }
            }
        });
    }

    private Match[][] createFixture(ArrayList<Team> teams) {
        Match[][] fixture = new Match[6][2];

        //All the possible fixtures.
        String[] possibleFixtures = new String[6];
        possibleFixtures[0] = "0123";
        possibleFixtures[1] = "0213";
        possibleFixtures[2] = "0312";
        possibleFixtures[3] = "1032";
        possibleFixtures[4] = "2031";
        possibleFixtures[5] = "3021";

        //In a for loop select a fixture randomly assign it to a week and flag as selected.

        fixture[0][0] = new Match(teams.get(0),teams.get(1));
        fixture[0][1] = new Match(teams.get(2),teams.get(3));

        fixture[1][0] = new Match(teams.get(0),teams.get(2));
        fixture[1][1] = new Match(teams.get(1),teams.get(3));

        fixture[2][0] = new Match(teams.get(0),teams.get(3));
        fixture[2][1] = new Match(teams.get(1),teams.get(2));

        fixture[3][0] = new Match(teams.get(1),teams.get(0));
        fixture[3][1] = new Match(teams.get(3),teams.get(2));

        fixture[4][0] = new Match(teams.get(2),teams.get(0));
        fixture[4][1] = new Match(teams.get(3),teams.get(1));

        fixture[5][0] = new Match(teams.get(3),teams.get(0));
        fixture[5][1] = new Match(teams.get(2),teams.get(1));

        return fixture;
    }

    private void updateLeaguePredictionTable(ArrayList<Team> teams) {
        calculatePredictions(teams);
        ArrayList<Team> prediction = (ArrayList<Team>) teams.clone();
        Collections.sort(prediction, new Comparator<Team>() {
            @Override
            public int compare(Team t1, Team t2) {
                if (t1.getPrediction() > t2.getPrediction()){
                    return -1;
                }
                if (t1.getPrediction() < t2.getPrediction()){
                    return 1;
                }
                return 0;

            }
        });

        for (int i=0;i<prediction.size();i++){
            populateLeaguePredictionTableRowWithTeam((TableRow) leaguePredictionTable.getChildAt(i),prediction.get(i));
        }


    }

    private void calculatePredictions(ArrayList<Team> teams) {
        double total = 100.0;
        int counter = 0;
        switch (weekCounter) {
            case 3:
                if (teams.get(0).getPoints() > teams.get(1).getPoints() + 6) {
                    teams.get(0).setPrediction(100.0);
                    total -= 100.0;
                }
                break;
            case 4:
                if (teams.get(0).getPoints() > teams.get(1).getPoints() + 3) {
                    teams.get(0).setPrediction(100.0);
                    total -= 100.0;
                }
                break;
            case 5:
                teams.get(0).setPrediction(100.0);
                total -= 100.0;
            default:
                break;
        }

        if (total > 0) {
            for (Team team : teams) {
                if (team.getPoints() + 3 * (6 - weekCounter + 1) < teams.get(0).getPoints()) {
                    team.setPrediction(0.0);
                } else {
                    double teamPrediction = team.getPoints() * 2.8;
                    if (total > teamPrediction){
                        team.setPrediction(teamPrediction);
                        total -= teamPrediction;
                    }
                }
                if (team.getPrediction() != 0.0){
                    counter++;
                }
            }
        }

        if (total == 0.0){
            return;
        }
        for (Team team: teams){
            if (team.getPrediction() != 0){
                team.setPrediction(team.getPrediction() + total / counter);
            }
        }

    }

    private void populateLeaguePredictionTableRowWithTeam(TableRow tableRow, Team team){
        ((TextView) tableRow.getChildAt(0)).setText(team.getTeamName());
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        ((TextView) tableRow.getChildAt(1)).setText("%"+decimalFormat.format(team.getPrediction()));
    }

    private void updateWeekResultsTable(Match[] fixture) {
        if (weekResultsTable.getVisibility() != View.VISIBLE){
            weekResultsTable.setVisibility(View.VISIBLE);
        }

        switch (weekCounter){
            case 0:
                weekResultsLabel.setText("1st Week Results");
                break;
            case 1:
                weekResultsLabel.setText("2nd Week Results");
                break;
            case 2:
                weekResultsLabel.setText("3rd Week Results");
                break;
            default:
                weekResultsLabel.setText((weekCounter+1)+"th Week Results");
                break;
        }

        for (int i=0;i<fixture.length;i++){
            TableRow weekResultsRow = (TableRow) weekResultsTable.getChildAt(i+1);
            ((TextView) weekResultsRow.getChildAt(0)).setText(fixture[i].getHomeTeam().getTeamName());
            ((TextView) weekResultsRow.getChildAt(1))
                    .setText(fixture[i].getHomeGoal() + " - " + fixture[i].getAwayGoal());
            ((TextView) weekResultsRow.getChildAt(2)).setText(fixture[i].getAwayTeam().getTeamName());
        }
    }

    private void simulateOneWeek(Match[] weeklyFixture) {
        for (int i=0;i<weeklyFixture.length;i++){
            Team homeTeam = weeklyFixture[i].getHomeTeam();
            Team awayTeam = weeklyFixture[i].getAwayTeam();

            int homeGoal = (int) Math.round((homeTeam.getGoalsForAvg() + awayTeam.getGoalsAgainstAvg()) / 2 + new Random().nextDouble());
            weeklyFixture[i].setHomeGoal(homeGoal);
            int awayGoal = (int) Math.round((awayTeam.getGoalsForAvg() + homeTeam.getGoalsAgainstAvg()) / 2 - new Random().nextDouble());
            weeklyFixture[i].setAwayGoal(awayGoal);

            homeTeam.setGoalDifference(homeTeam.getGoalDifference() + (homeGoal-awayGoal));
            awayTeam.setGoalDifference(awayTeam.getGoalDifference() + (awayGoal-homeGoal));

            if (homeGoal > awayGoal){
                homeTeam.setWin(homeTeam.getWin() + 1);
                homeTeam.setPoints(homeTeam.getPoints() + 3);
                awayTeam.setLose(awayTeam.getLose() + 1);
            } else if (homeGoal < awayGoal){
                homeTeam.setLose(homeTeam.getLose() + 1);
                awayTeam.setWin(awayTeam.getWin() + 1);
                awayTeam.setPoints(awayTeam.getPoints() + 3);
            } else if (homeGoal == awayGoal){
                homeTeam.setDraw(homeTeam.getDraw() + 1);
                homeTeam.setPoints(homeTeam.getPoints() + 1);
                awayTeam.setDraw(awayTeam.getDraw() + 1);
                awayTeam.setPoints(awayTeam.getPoints() + 1);
            }

        }
    }

    private void updateLeagueTable(ArrayList<Team> teams){
        Collections.sort(teams, new Comparator<Team>() {
            @Override
            public int compare(Team t1, Team t2) {
                if (t1.getPoints() > t2.getPoints()){
                    return -1;
                }
                if(t1.getPoints() < t2.getPoints()){
                    return 1;
                }

                    if (t1.getGoalDifference() > t2.getGoalDifference()){
                        return -1;
                    }
                    if(t1.getGoalDifference() < t2.getGoalDifference()) {
                        return 1;
                    }
                        return 0;
                }
        });

        for (int i=0;i<teams.size();i++){
            populateLeagueTableRowWithTeam((TableRow) leagueTable.getChildAt(i+1),teams.get(i));
        }

    }

    private void populateLeagueTableRowWithTeam(TableRow tableRow, Team team){
        ((TextView) tableRow.getChildAt(0)).setText(team.getTeamName());
        ((TextView) tableRow.getChildAt(1)).setText(Integer.toString(team.getPoints()));
        ((TextView) tableRow.getChildAt(2)).setText(Integer.toString(team.getWin()));
        ((TextView) tableRow.getChildAt(3)).setText(Integer.toString(team.getDraw()));
        ((TextView) tableRow.getChildAt(4)).setText(Integer.toString(team.getLose()));
        ((TextView) tableRow.getChildAt(5)).setText(Integer.toString(team.getGoalDifference()));
    }
}
