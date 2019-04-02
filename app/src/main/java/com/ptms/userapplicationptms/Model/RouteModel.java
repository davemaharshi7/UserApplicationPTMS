package com.ptms.userapplicationptms.Model;

public class RouteModel {
    private String Path, SortPath;

    public RouteModel()
    {

    }

    public RouteModel(String path, String sortPath) {
        Path = path;
        SortPath = sortPath;
    }

    public String getPath() {
        return Path;
    }

    public void setPath(String path) {
        Path = path;
    }

    public String getSortPath() {
        return SortPath;
    }

    public void setSortPath(String sortPath) {
        SortPath = sortPath;
    }
}
