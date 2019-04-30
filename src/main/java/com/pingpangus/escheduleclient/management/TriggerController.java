package com.pingpangus.escheduleclient.management;

import com.pingpangus.escheduleclient.api.EScheduleTask;
import com.pingpangus.escheduleclient.core.JobManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * trigger project task
 * @see EScheduleTask
 * @author qunar-qw
 */
@RestController
@RequestMapping(value = "eclient")
public class TriggerController {

    private static final Logger logger = LoggerFactory.getLogger(TriggerController.class);

    @RequestMapping("start")
    public ResponseVo start(String jobName) {
        logger.info("receive job name:{}", jobName);
        try {
            JobManager.runJob(jobName);
        } catch (Exception e) {
            logger.error("start job: {} exception", jobName, e);
            return ResponseVo.createTrue("false");
        }
        return ResponseVo.createTrue("success");
    }



}