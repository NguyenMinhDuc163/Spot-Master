package server.model;

import java.io.Serial;
import java.io.Serializable;

public class UserModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
	
    private String userName;
    private String password;
    private String status;
    private float score;
    private int win;
    private int draw;
    private int lose;
    private float avgCompetitor;
    private float avgTime;
    
    public UserModel() { }
    
    public UserModel(String userName, String password, String status) {
    	super();
        this.userName = userName;
        this.password = password;
        this.status = status;
    }
    
    public UserModel(String userName, String password, float score) {
    	super();
        this.userName = userName;
        this.password = password;
        this.score = score;
    }
    
    public UserModel(String userName, String password, String status, float score) {
    	super();
        this.userName = userName;
        this.password = password;
        this.status = status;
        this.score = score;
    }
    
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getStatus() {
        return status;
    }

    public void setStatus(String password) {
        this.status = password;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public int getWin() {
        return win;
    }

    public void setWin(int win) {
        this.win = win;
    }

    public int getDraw() {
        return draw;
    }

    public void setDraw(int draw) {
        this.draw = draw;
    }

    public int getLose() {
        return lose;
    }

    public void setLose(int lose) {
        this.lose = lose;
    }

    public float getAvgCompetitor() {
        return avgCompetitor;
    }

    public void setAvgCompetitor(float avgCompetitor) {
        this.avgCompetitor = avgCompetitor;
    }

    public float getAvgTime() {
        return avgTime;
    }

    public void setAvgTime(float avgTime) {
        this.avgTime = avgTime;
    }

    
}
