package com.hypo.appstoreprice.runner;

import com.hypo.appstoreprice.common.BizException;
import com.hypo.appstoreprice.task.PriceSnapshotTask;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @author 53546
 * @date 2026/5/7 9:56
 * @description:
 */
@Component
@RequiredArgsConstructor
public class priceSnapshotRunner implements ApplicationRunner {

    private final PriceSnapshotTask priceSnapshotTask;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        CompletableFuture.runAsync(priceSnapshotTask::executeSnapshot);
    }
}
