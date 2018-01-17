package orkunsoylu.premierleague;

import java.text.DecimalFormat;

/**
 * Created by orkunsoylu on 16/01/2018.
 */

public class Team {
    private String teamName;
    private int points,goalDifference;
    private int[] wdlArray = {0,0,0};
    private double goalsForAvg,goalsAgainstAvg;
    private double prediction;

    public Team(String teamName, double goalsForAvg, double goalsAgainstAvg){
        this.teamName = teamName;
        points = 0;
        goalDifference = 0;
        this.goalsForAvg = goalsForAvg;
        this.goalsAgainstAvg = goalsAgainstAvg;
        prediction = 0;

    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getGoalDifference() {
        return goalDifference;
    }

    public void setGoalDifference(int goalDifference) {
        this.goalDifference = goalDifference;
    }

    public double getGoalsForAvg() {
        return goalsForAvg;
    }

    public void setGoalsForAvg(double goalsForAvg) {
        this.goalsForAvg = goalsForAvg;
    }

    public double getGoalsAgainstAvg() {
        return goalsAgainstAvg;
    }

    public void setGoalsAgainstAvg(double goalsAgainstAvg) {
        this.goalsAgainstAvg = goalsAgainstAvg;
    }

    public double getPrediction() {

        return prediction;
    }

    public void setPrediction(double prediction) {
        this.prediction = prediction;
    }

    //Get wdlArray values.

    public int getWin(){
        return wdlArray[0];
    }
    public void setWin(int win){
        this.wdlArray[0] = win;
    }

    public int getDraw(){
        return wdlArray[1];
    }
    public void setDraw(int draw){
        this.wdlArray[1] = draw;
    }

    public int getLose(){
        return wdlArray[2];
    }
    public void setLose(int lose){
        this.wdlArray[2] = lose;
    }
}
