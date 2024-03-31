package Hse.CourseProject.ExploreMoscow.Location;

public class Location {
    private final String nameLocation;
    private final String loadPictureLocation;
    private final String mainInfo;
    private final String history;

    public Location(String nameLocation, String loadPictureLocation, String history, String mainInfo) {
        this.nameLocation = nameLocation;
        this.loadPictureLocation = loadPictureLocation;
        this.history = history;
        this.mainInfo = mainInfo;
    }

    public String getLoadPictureLocation() {
        return loadPictureLocation;
    }

    public String getNameLocation() {
        return nameLocation;
    }

    public String getMainInfo() {
        return mainInfo;
    }

    public String getHistory() {
        return history;
    }
}
