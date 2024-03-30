package Hse.CourseProject.ExploreMoscow.Location;

public class Location {
    private final String _nameLocation;
    private final String _loadPictureLocation;

    public Location(String nameLocation, String loadPictureLocation) {
        _nameLocation = nameLocation;
        _loadPictureLocation = loadPictureLocation;
    }

    public String getLoadPictureLocation() {
        return _loadPictureLocation;
    }

    public String getNameLocation() {
        return _nameLocation;
    }
}
