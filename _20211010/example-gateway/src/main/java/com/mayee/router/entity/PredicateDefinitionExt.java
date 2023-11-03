package com.mayee.router.entity;

import com.mayee.model.Constant;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.util.CollectionUtils;

import java.util.Set;

public class PredicateDefinitionExt extends PredicateDefinition {
    private Set<String> apply;
    private Set<String> notApply;

    public PredicateDefinitionExt(String text) {
        super(text);
    }

    public boolean isApply(String routeId) {
        if(CollectionUtils.isEmpty(apply)){
            return false;
        }
        if(apply.contains(Constant.FILTER_ALL)){
            return true;
        }
        return apply.contains(routeId);
    }
    public boolean isNotApply(String routeId) {
        if(CollectionUtils.isEmpty(notApply)){
            return false;
        }
        return notApply.contains(routeId);
    }

    public void setApply(Set<String> apply) {
        this.apply = apply;
    }


    public void setNotApply(Set<String> notApply) {
        this.notApply = notApply;
    }
}
