package fatima.tech.espnetsmart;

/* JADX INFO: loaded from: classes.dex */
public class Data {
    private static Data data = new Data();
    private boolean bottomViewVisible;
    private boolean snackVisible;

    private Data() {
    }

    public static Data getInstance() {
        return data;
    }

    public boolean getSnackVisible() {
        return this.snackVisible;
    }

    public void setSnackVisible(boolean snackVisible) {
        this.snackVisible = snackVisible;
    }

    public boolean getBottomViewVisible() {
        return this.bottomViewVisible;
    }

    public void setBottomViewVisible(boolean bottomViewVisible) {
        this.snackVisible = bottomViewVisible;
    }
}
