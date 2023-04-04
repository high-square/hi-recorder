package highsquare.hirecoder.dto;

public class DashWeekStats {
    private String dayOfWeek;
    private int numPosts;
    private int numViews;
    private int numMembers;

    public DashWeekStats(String dayOfWeek, int numPosts, int numViews, int numMembers) {
        this.dayOfWeek = dayOfWeek;
        this.numPosts = numPosts;
        this.numViews = numViews;
        this.numMembers = numMembers;
    }

}
