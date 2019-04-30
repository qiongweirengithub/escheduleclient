package com.pingpangus.escheduleclient.core;

import com.pingpangus.escheduleclient.api.EScheduleTask;
import com.pingpangus.escheduleclient.config.EScheduleProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.net.UnknownHostException;
import java.util.Map;

/**
 * @see EScheduleTask
 * @author qunar-qw
 * @date 18-7-13
 */
@Service
public class AnnotationParser implements BeanPostProcessor {

    private static final Logger logger = LoggerFactory.getLogger(AnnotationParser.class);

    @Autowired
    private EScheduleProperties scheduleProperties;

    @Autowired
    private JobRegisterService jobRegisterService;

    @Nullable
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        logger.info("start scanning beans for escheduleclient task : {}", beanName);

        /**
         * bean 获取  注释了 EScheduleTask 的方法
         */
        Map<Method, EScheduleTask> annotatedMethods = MethodIntrospector.selectMethods(bean.getClass(),
                (MethodIntrospector.MetadataLookup<EScheduleTask>) method -> {
                    EScheduleTask scheduledMethods = AnnotatedElementUtils.getMergedAnnotation(
                            method, EScheduleTask.class);
                    return (!(scheduledMethods == null) ? scheduledMethods : null);
                });
        if(CollectionUtils.isEmpty(annotatedMethods)) {
            return bean;
        }

        annotatedMethods.forEach((method, eScheduleTasks) -> {
            try {
                jobRegisterService.registerJob(method, bean);
            } catch (UnknownHostException e) {
                logger.error("register for job error  -> job:{}", eScheduleTasks.toString(), e);
            }
        });

        logger.info("=========Post process beans:{}", beanName);
        return bean;
    }

    @Nullable @Override public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {
        return bean;
    }
}