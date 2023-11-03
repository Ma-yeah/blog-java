package com.mayee.router.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class RouteGroupEntity implements Serializable {
    private static final long serialVersionUID = -1847899804045021045L;
    private List<RouteEntity> routeList;
    private List<GlobalRoutFilterEntity> globalFilters;
    private List<GlobalRoutePredicateEntity> globalPredicates;
}
