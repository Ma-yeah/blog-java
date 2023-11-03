package com.mayee.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.util.concurrent.atomic.AtomicInteger;

@Setter
@Getter
public class RefreshClientEvent extends ApplicationEvent {

    private static volatile AtomicInteger counter = new AtomicInteger();

    private Integer index;

    public RefreshClientEvent(Object source,Integer index) {
        super(source);
        this.index = index;
    }

    public static Integer increment(){
       return counter.incrementAndGet();
    }

    public static Integer get(){
        return counter.get();
    }
}
