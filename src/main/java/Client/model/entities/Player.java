package Client.model.entities;

public class Player {
    private int id;
    private String username;
    private String password;
    private int totalScore;

    public Player(int id, String username, String password, int totalScore) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.totalScore = totalScore;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public int getTotalScore() { return totalScore; }
    public void setTotalScore(int totalScore) { this.totalScore = totalScore; }
}
