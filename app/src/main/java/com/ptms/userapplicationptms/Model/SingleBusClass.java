package com.ptms.userapplicationptms.Model;

public class SingleBusClass {
    private String routeSource,routeDestination,departTime;

    public SingleBusClass(String routeSource, String routeDestination, String departTime) {
        this.routeSource = routeSource;
        this.routeDestination = routeDestination;
        this.departTime = departTime;
    }

    public SingleBusClass() {
    }

    public String getRouteSource() {
        return routeSource;
    }

    public void setRouteSource(String routeSource) {
        this.routeSource = routeSource;
    }

    public String getRouteDestination() {
        return routeDestination;
    }

    public void setRouteDestination(String routeDestination) {
        this.routeDestination = routeDestination;
    }

    public String getDepartTime() {
        return departTime;
    }

    public void setDepartTime(String departTime) {
        this.departTime = departTime;
    }
}
