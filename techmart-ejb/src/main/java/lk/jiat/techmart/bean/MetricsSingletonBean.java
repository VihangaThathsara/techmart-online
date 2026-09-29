package lk.jiat.techmart.bean;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.interceptor.Interceptors;
import lk.jiat.techmart.interceptor.PerformanceInterceptor;
import lk.jiat.techmart.dto.MetricDTO;
import lk.jiat.techmart.remote.MetricsRemote;
import lk.jiat.techmart.store.TechMartStore;

import java.util.List;
import java.util.logging.Logger;

@Singleton
@Startup
@Interceptors(PerformanceInterceptor.class)
public class MetricsSingletonBean implements MetricsRemote {
    private static final Logger LOGGER = Logger.getLogger(MetricsSingletonBean.class.getName());


    @PostConstruct
    public void init() {
        LOGGER.info("Metrics singleton startup initialized");
        TechMartStore.incrementMetric("Application Starts");
    }

    @Override
    @Lock(LockType.WRITE)
    public void recordMetric(String name, String value, String unit) {
        TechMartStore.putMetric(name, value, unit);
    }

    @Override
    @Lock(LockType.WRITE)
    public void incrementCounter(String name) {
        TechMartStore.incrementMetric(name);
    }

    @Override
    @Lock(LockType.READ)
    public List<MetricDTO> getMetrics() {
        return TechMartStore.metrics();
    }

    @Override
    @Lock(LockType.READ)
    public String getSummary() {
        return "System Status: " + TechMartStore.metrics().size() + " active metrics available";
    }
}
