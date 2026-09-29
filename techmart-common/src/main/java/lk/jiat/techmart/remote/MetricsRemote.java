package lk.jiat.techmart.remote;

import jakarta.ejb.Remote;
import lk.jiat.techmart.dto.MetricDTO;

import java.util.List;

@Remote
public interface MetricsRemote {
    void recordMetric(String name, String value, String unit);
    void incrementCounter(String name);
    List<MetricDTO> getMetrics();
    String getSummary();
}
