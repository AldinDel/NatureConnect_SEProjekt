package at.fhv.Event.presentation.ui.dto;

public class FeedbackStats {

    private double averageRating;
    private long lowRatingCount;
    private long commentCount;

    public double getAverageRating() {
        return averageRating;
    }

    public long getLowRatingCount() {
        return lowRatingCount;
    }

    public long getCommentCount() {
        return commentCount;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public void setLowRatingCount(long lowRatingCount) {
        this.lowRatingCount = lowRatingCount;
    }

    public void setCommentCount(long commentCount) {
        this.commentCount = commentCount;
    }
}

