package com.loyer.modules.monitor.config;

import com.loyer.common.apis.server.MessageServer;
import com.loyer.common.apis.server.SystemServer;
import com.loyer.common.dedicine.entity.WeChatAlarm;
import com.loyer.modules.monitor.entity.MessageParams;
import de.codecentric.boot.admin.server.domain.entities.Instance;
import de.codecentric.boot.admin.server.domain.entities.InstanceRepository;
import de.codecentric.boot.admin.server.domain.events.InstanceEvent;
import de.codecentric.boot.admin.server.domain.values.Registration;
import de.codecentric.boot.admin.server.notify.AbstractStatusChangeNotifier;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 报警通知
 *
 * @author kuangq
 * @date 2022-08-23 18:27
 */
@Configuration
public class AlarmConfig extends AbstractStatusChangeNotifier {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final SystemServer systemServer;

    private final MessageServer messageServer;

    public AlarmConfig(InstanceRepository repository, SystemServer systemServer, MessageServer messageServer) {
        super(repository);
        this.systemServer = systemServer;
        this.messageServer = messageServer;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected Mono<Void> doNotify(InstanceEvent event, Instance instance) {
        logger.error("【{}】服务异常：{}", instance.getRegistration().getName(), instance.getStatusInfo().getDetails().getOrDefault("message", ""));
        return Mono.fromRunnable(() -> sendAlarm(instance));
    }

    private void sendAlarm(Instance instance) {
        Date date = new Date();
        Registration registration = instance.getRegistration();
        List<String> messageList = new ArrayList<>();
        messageList.add(registration.getName());
        messageList.add(StringUtils.substringBetween(registration.getServiceUrl(), "//", ":"));
        messageList.add(StringUtils.substringAfterLast(registration.getServiceUrl(), ":"));
        messageList.add(new SimpleDateFormat("MM-dd HH:mm").format(date));
        MessageParams messageParams = MessageParams.of(messageList);
        messageServer.sendMessage(messageParams);
        sendWeChatAlarm(registration, date);
    }

    private void sendWeChatAlarm(Registration registration, Date date) {
        WeChatAlarm weChatAlarm = new WeChatAlarm();
        weChatAlarm.setTitle(String.format("%s服务异常", registration.getName()));
        weChatAlarm.setContent(registration.getServiceUrl());
        weChatAlarm.setDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
        systemServer.sendWeChatAlarm(weChatAlarm);
    }
}