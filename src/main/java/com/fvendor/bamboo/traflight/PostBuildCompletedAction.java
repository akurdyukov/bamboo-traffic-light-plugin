package com.fvendor.bamboo.traflight;

import com.atlassian.bamboo.build.BuildLoggerManager;
import com.atlassian.bamboo.build.CustomPostBuildCompletedAction;
import com.atlassian.bamboo.builder.BuildState;
import com.atlassian.bamboo.v2.build.BaseConfigurablePlugin;
import com.atlassian.bamboo.v2.build.BuildContext;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Post build completed action sending program to traffic light
 */
public class PostBuildCompletedAction extends BaseConfigurablePlugin implements CustomPostBuildCompletedAction {
    private static final Logger log = Logger.getLogger(PostBuildCompletedAction.class);

    private static final String PRE_ENABLED_KEY = "custom.traflight.post.program.enabled";
    private static final String PRE_PROGRAM_SUCCESS_KEY = "custom.traflight.post.program.success";
    private static final String PRE_PROGRAM_FAIL_KEY = "custom.traflight.post.program.fail";

    private BuildContext buildContext;
    private BuildLoggerManager buildLoggerManager;
    private TrafficLightService trafficLightService;

    @Override
    public void init(@NotNull BuildContext buildContext) {
        this.buildContext = buildContext;
    }

    @NotNull
    @Override
    public BuildContext call() throws InterruptedException, Exception {
        final Map customConfiguration = buildContext.getBuildDefinition().getCustomConfiguration();

        if (!"true".equals(customConfiguration.get(PRE_ENABLED_KEY))) {
            log.info("Post traffic light disabled, skipping");
            return buildContext;
        }

        BuildState state = buildContext.getBuildResult().getBuildState();
        if (state.equals(BuildState.UNKNOWN)) {
            if (0 < buildContext.getBuildResult().getBuildReturnCode()) {
                state = BuildState.FAILED;
            } else {
                state = BuildState.SUCCESS;
            }
        }
        String program;
        if (BuildState.SUCCESS.equals(state)) {
            program = (String) customConfiguration.get(PRE_PROGRAM_SUCCESS_KEY);
        } else {
            program = (String) customConfiguration.get(PRE_PROGRAM_FAIL_KEY);
        }

        log.info("Post traffic light enabled, setting program " + program);
        buildLoggerManager.getBuildLogger(buildContext.getPlanResultKey()).addBuildLogEntry(
                "setting post traffic light program " + program);
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
