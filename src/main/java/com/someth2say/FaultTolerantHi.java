package com.someth2say;

import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;

import java.util.concurrent.atomic.AtomicInteger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/hi")
public class FaultTolerantHi {
    AtomicInteger counter = new AtomicInteger(1);
    int failChance = 0;
    int delayChance = 0;
    int delay = 0;

    @GET
    @Path("/fail/{chance}")
    @Produces(MediaType.TEXT_PLAIN)
    public String fail(final int chance){
        this.failChance=chance;
        return "Fail chance set to "+this.failChance;
    }

    @GET
    @Path("/delay/{chance}/{delay}")
    @Produces(MediaType.TEXT_PLAIN)
    public String delayChance(final int chance, final int delay){
        this.delayChance=chance;
        this.delay=delay;
        return "Delay chance set to "+this.delayChance+" with "+this.delay+" milis";
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Retry(maxRetries = 2)
    @Path("/retry")
    public String retry(){
        return hello();
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Fallback(fallbackMethod = "fallbackHello")
    @Path("/fallback")
    public String fallback(){
        return hello();
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Timeout(100)
    @Path("/timeout100")
    public String timeout100(){
        return hello();
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @CircuitBreaker(requestVolumeThreshold = 4)
    @Path("/circuitbreaker")
    public String circuitBreaker(){
        return hello();
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        var count = counter.getAndIncrement();

        if (failChance>0 && count%failChance == 0){
            throw new RuntimeException("Hello Failure.");
        }

        if (delayChance>0 && count%delayChance == 0){
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                throw new RuntimeException("Hello Interrupted!");
            }
        }
        return "Hello";
    }

    public String fallbackHello() {
        return "Hello Fallback";
    }


}