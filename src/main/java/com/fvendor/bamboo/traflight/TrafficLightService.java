package com.fvendor.bamboo.traflight;

/**
 * Public interface for traffic light interaction service
 */
public interface TrafficLightService {
    /**
     * Set program for traffic light
     *
     * @param program program source
     */
    void setProgram(String buildPlan, String program);
}
