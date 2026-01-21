package enums;
public enum Priority {

    LOW(1),
    MEDIUM(2),
    HIGH(3),
    CRITICAL(4);

    private final int score;

    Priority(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }
}