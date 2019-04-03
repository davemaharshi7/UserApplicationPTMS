package com.ptms.userapplicationptms.Model;

public class SingleBusClass {
    private String routeSource, routeDestination, departTime, bus_id, route_id;

    public SingleBusClass(String routeSource, String routeDestination, String departTime, String bus_id, String route_id) {
        this.routeSource = routeSource;
        this.routeDestination = routeDestination;
        this.departTime = departTime;
        this.bus_id = bus_id;
        this.route_id = route_id;
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

    public String getBus_id() {
        return bus_id;
    }

    public void setBus_id(String bus_id) {
        this.bus_id = bus_id;
    }

    public String getRoute_id() {
        return route_id;
    }

    public void setRoute_id(String route_id) {
        this.route_id = route_id;
    }
}
