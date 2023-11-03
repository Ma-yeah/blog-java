package com.mayee.router;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.exception.NacosException;
import com.mayee.router.entity.FilterDefinitionExt;
import com.mayee.router.entity.PredicateDefinitionExt;
import com.mayee.router.entity.RouteEntity;
import com.mayee.router.entity.RouteGroupEntity;
import com.mayee.service.NacosRouterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ObjectUtils;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@ConditionalOnProperty(name = "dynamic.router.enable", havingValue = "true")
public class NacosRouteDefinitionRepository implements RouteDefinitionRepository, ApplicationEventPublisherAware {
    private ApplicationEventPublisher applicationEventPublisher;
    private static final Map<String, RouteDefinition> NACOS_ROUTE_DEFINITION = Collections.synchronizedMap(new LinkedHashMap<>());

    @Autowired
    private NacosRouterService nacosService;

    @Bean
    public Map<String, RouteDefinition> refreshNacosRouteDefinitionRepository() throws NacosException {
        nacosService.refreshRouter(this.assembleRouteDefinition);
        return NACOS_ROUTE_DEFINITION;
    }

    private final Consumer<RouteGroupEntity> assembleRouteDefinition = config -> {
        List<RouteEntity> routes = config.getRouteList();
        if (routes == null) {
            return;
        }
        routes.sort(Comparator.comparingInt(RouteEntity::getOrder));
        Set<String> tem = new HashSet<>(routes.size());
        List<FilterDefinitionExt> globalFilters = Optional.ofNullable(config.getGlobalFilters()).orElse(new ArrayList<>()).stream().map(m -> {
            FilterDefinitionExt filterDefinition = new FilterDefinitionExt();
            filterDefinition.setArgs(m.getArgs());
            filterDefinition.setName(m.getName());
            filterDefinition.setApply(m.getApply());
            filterDefinition.setNotApply(m.getNotApply());
            return filterDefinition;
        }).collect(Collectors.toList());
        List<PredicateDefinitionExt> globalPredicates = Optional.ofNullable(config.getGlobalPredicates()).orElse(new ArrayList<>()).stream().map(m -> {
            PredicateDefinitionExt predicateDefinition = new PredicateDefinitionExt(m.toString());
            predicateDefinition.setName(m.getName());
            return predicateDefinition;
        }).collect(Collectors.toList());

        int x = 1;
        for (FilterDefinition routFilterEntity : globalFilters) {
            log.info("Load GlobalFilter[{}/{}],name={}", x++, globalFilters.size(), routFilterEntity.getName());
        }
        x = 1;
        for (PredicateDefinitionExt routFilterEntity : globalPredicates) {
            log.info("Load GlobalPredicate[{}/{}],name={}", x++, globalFilters.size(), routFilterEntity.getName());
        }
        x = 1;
        for (RouteEntity eachRoute : routes) {
            RouteDefinition definition = new RouteDefinition();
            definition.setId(eachRoute.getId());
            definition.setOrder(eachRoute.getOrder());
            List<PredicateDefinition> predicateDefinitionList = Optional.ofNullable(eachRoute.getPredicates()).orElse(new ArrayList<>()).stream().map(m -> {
                PredicateDefinition predicateDefinition = new PredicateDefinition(m.toString());
                predicateDefinition.setName(m.getName());
                return predicateDefinition;
            }).collect(Collectors.toList());
            List<PredicateDefinition> predicateGlobal = globalPredicates.stream().filter(f -> {
                if (f.isNotApply(definition.getId())) {
                    return false;
                }
                return f.isApply(definition.getId());
            }).collect(Collectors.toList());
            predicateDefinitionList.addAll(predicateGlobal);
            predicateDefinitionList = predicateDefinitionList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(PredicateDefinition::getName))), ArrayList::new));
            definition.setPredicates(predicateDefinitionList);
            List<FilterDefinition> filterDefinitionList = Optional.ofNullable(eachRoute.getFilters()).orElse(new ArrayList<>()).stream().map(m -> {
                FilterDefinition filterDefinition = new FilterDefinition();
                filterDefinition.setArgs(m.getArgs());
                filterDefinition.setName(m.getName());
                return filterDefinition;
            }).collect(Collectors.toList());
            List<FilterDefinition> filterGlobal = globalFilters.stream().filter(f -> {
                if (f.isNotApply(definition.getId())) {
                    return false;
                }
                return f.isApply(definition.getId());
            }).collect(Collectors.toList());
            filterDefinitionList.addAll(filterGlobal);
            filterDefinitionList = filterDefinitionList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(FilterDefinition::getName))), ArrayList::new));
            definition.setFilters(filterDefinitionList);
            if (eachRoute.getMetadata() != null) {
                definition.setMetadata(eachRoute.getMetadata());
            }
            URI uri = UriComponentsBuilder.fromUriString(eachRoute.getUri()).build().toUri();
            definition.setUri(uri);
            NACOS_ROUTE_DEFINITION.put(definition.getId(), definition);
            tem.add(eachRoute.getId());
            log.info("Refresh[{}/{}] Order:{},RoutID:{},URL:{}, Value:{}", x++, tem.size(), eachRoute.getOrder(), eachRoute.getId(), eachRoute.getUri(), JSON.toJSONString(eachRoute.getPredicates()));
        }
        Set<String> exists = new HashSet<>(NACOS_ROUTE_DEFINITION.size());
        exists.addAll(NACOS_ROUTE_DEFINITION.keySet());
        x = 0;
        for (String k : exists) {
            if (tem.contains(k)) {
                continue;
            }
            NACOS_ROUTE_DEFINITION.remove(k);
            log.info("Remove[{}] RoutID:{}", x, k);
        }
        applicationEventPublisher.publishEvent(new RefreshRoutesEvent(this));
    };


    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        return Flux.fromIterable(NACOS_ROUTE_DEFINITION.values());
    }

    @Override
    public Mono<Void> save(Mono<RouteDefinition> route) {
        return route.flatMap(r -> {
            if (ObjectUtils.isEmpty(r.getId())) {
                return Mono.error(new IllegalArgumentException("id may not be empty"));
            }
            NACOS_ROUTE_DEFINITION.put(r.getId(), r);
            applicationEventPublisher.publishEvent(new RefreshRoutesEvent(this));
            return Mono.empty();
        });
    }

    @Override
    public Mono<Void> delete(Mono<String> routeId) {
        return routeId.flatMap(id -> {
            if (NACOS_ROUTE_DEFINITION.containsKey(id)) {
                NACOS_ROUTE_DEFINITION.remove(id);
                applicationEventPublisher.publishEvent(new RefreshRoutesEvent(this));
                return Mono.empty();
            }
            return Mono.defer(() -> Mono.error(new NotFoundException("RouteDefinition not found: " + routeId)));
        });
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
