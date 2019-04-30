package com.pingpangus.escheduleclient.core;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.pingpangus.escheduleclient.api.EScheduleTask;
import com.pingpangus.escheduleclient.config.EScheduleProperties;
import com.pingpangus.escheduleclient.common.ClientAction;
import com.pingpangus.escheduleclient.common.EClientConstants;
import com.pingpangus.escheduleclient.utils.EscheduleHttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.ScheduledMethodRunnable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

/**
 * @author qunar-qw
 * @date 18-7-13
 */
@Service
public class JobRegisterService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobRegisterService.class);

    @Autowired
    private EScheduleProperties scheduleProperties;

    public boolean registerJob(Method method, Object bean) throws UnknownHostException {

        EScheduleTask eScheduleTask = method.getAnnotation(EScheduleTask.class);

        Map<String, String> jobParamMap = Maps.newHashMap();

        String ipAddress = InetAddress.getLocalHost().getHostAddress();

        jobParamMap.put("jobName", eScheduleTask.jobName());
        jobParamMap.put("ip", "http://" + ipAddress + ":8081" + EClientConstants.CLIENT_URL);
        jobParamMap.put("cron", eScheduleTask.cron());
        jobParamMap.put("group", eScheduleTask.group());
        Joiner joiner  = Joiner.on("/");

        try {
            String url = joiner.join(scheduleProperties.getServerurl(), ClientAction.REGISTER.action);
            /**
             * register to server
             */
            EscheduleHttpUtils.get(url, jobParamMap);

            /**
             * Runner wrapper
             */
            Runnable job = new ScheduledMethodRunnable(bean, method);
            /**
             * add to jobManagement
             */
            JobManager.addJob(eScheduleTask.jobName(), job);

        } catch (IOException e) {
            LOGGER.error("注册定时任务失败", e);
        }

        return true;
    }

}