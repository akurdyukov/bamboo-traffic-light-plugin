package com.fvendor.bamboo.traflight;

import com.atlassian.bamboo.build.BuildLoggerManager;
import com.atlassian.bamboo.build.CustomPreBuildAction;
import com.atlassian.bamboo.v2.build.BaseConfigurableBuildPlugin;
import com.atlassian.bamboo.v2.build.BuildContext;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Pre-build action sends 'build started' command
 */
public class PreBuildAction extends BaseConfigurableBuildPlugin implements CustomPreBuildAction {
    private static final Logger log = Logger.getLogger(PreBuildAction.class);

    private static final String PRE_ENABLED_KEY = "custom.traflight.pre.program.enabled";
    private static final String PRE_PROGRAM_KEY = "custom.traflight.pre.program";

    private BuildContext buildContext;
    private BuildLoggerManager buildLoggerManager;
    private TrafficLightService trafficLightService;

    @Override
    public void init(@NotNull BuildContext buildContext) {
        this.buildContext = buildContext;
    }

    @NotNull
    @Override
    public BuildContext call() throws Exception {
        final Map customConfiguration = buildContext.getBuildDefinition().getCustomConfiguration();

        log.info("Pre traffic light enabled: " + customConfiguration.get(PRE_ENABLED_KEY));
        if (!"true".equals(customConfiguration.get(PRE_ENABLED_KEY))) {
            log.info("Pre traffic light disabled, skipping");
            return buildContext;
        }

        String program = (String) customConfiguration.get(PRE_PROGRAM_KEY);
        log.info("Pre traffic light enabled, setting program " + program);
        buildLoggerManager.getBuildLogger(buildContext.getPlanResultKey()).addBuildLogEntry(
                "setting pre traffic light program " + program);
        sendProgram(buildContext, program);
        return buildContext;
    }

    private void sendProgram(BuildContext context, String program) {
        if (context.getParentBuildContext() != null) {
            sendProgram(context.getParentBuildContext(), program);
        }
        trafficLightService.setProgram(context.getPlanKey(), program);
    }

    public void setBuildLoggerManager(BuildLoggerManager buildLoggerManager) {
        this.buildLoggerManager = buildLoggerManager;
    }

    public void setTrafficLightService(TrafficLightService trafficLightService) {
        this.trafficLightService = trafficLightService;
    }
}
