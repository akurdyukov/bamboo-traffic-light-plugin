package com.fvendor.bamboo.traflight;

import com.atlassian.bamboo.plan.PlanManager;
import com.atlassian.bamboo.plan.TopLevelPlan;
import com.atlassian.bamboo.security.BambooPermissionManager;
import com.atlassian.spring.container.ContainerManager;
import org.acegisecurity.context.SecurityContextHolder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.xsocket.MaxReadSizeExceededException;
import org.xsocket.connection.*;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.channels.ClosedChannelException;
import java.util.HashMap;
import java.util.Map;

/**
 * xSocket TCP server implementing TrafficLightSource
 */
public class TcpTrafficLightService implements TrafficLightService, InitializingBean, DisposableBean, IDataHandler {
    private static final Logger log = Logger.getLogger(TcpTrafficLightService.class);

    private int serverPort = 9090;
    private Map<String, String> programs = new HashMap<String, String>();
    private Server server;

    @Override
    public synchronized void setProgram(String buildPlan, String program) {
        programs.put(buildPlan, program);
        int found = 0;
        for(INonBlockingConnection conn: server.getOpenConnections()) {
            String login = (String) conn.getAttachment();
            if (!buildPlan.equals(login)) {
                continue;
            }
            try {
                conn.write(program);
                conn.write('\n');
                conn.flush();
                found++;
            } catch (IOException e) {
                log.error("Error sending program to client", e);
            }
        }
        log.info("Program " + program + " set to " + found + " connected lights for plan " + buildPlan);
    }

    @Override
    public void destroy() throws Exception {
        server.close();
        log.info("Traffic light server stopped");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(BambooPermissionManager.SYSTEM_AUTHORITY);
        PlanManager planManager = (PlanManager) ContainerManager.getComponent("planManager");

        for (TopLevelPlan plan : planManager.getAllPlans()) {
            programs.put(plan.getPlanKey().getKey(), "");
            log.info("Default program added for plan " + plan.getPlanKey().getKey());
        }

        server = new Server(serverPort, this);
        server.start();
        log.info("Started traffic light server on port " + serverPort);
    }

    @Override
    public boolean onData(INonBlockingConnection conn) throws IOException, BufferUnderflowException, ClosedChannelException, MaxReadSizeExceededException {
        // handle simplistic auth
        String login = conn.readStringByDelimiter("\n").trim();
        log.info("Got login " + login);
        if (!programs.containsKey(login)) {
            log.warn("Login is invalid, closing connection");
            conn.close(); // close invalid connection
        }
        conn.setAttachment(login);
        String program = programs.get(login);
        if (program != null && !program.trim().equals("")) {
            conn.write(program);
            conn.write('\n');
            conn.flush();
        }
        return true;
    }
}
