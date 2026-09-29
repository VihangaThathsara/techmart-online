package lk.jiat.techmart.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public class MetricDTO implements Serializable {
    private String name;
    private String value;
    private String unit;
    private LocalDateTime updatedAt;

    public MetricDTO() {
    }

    public MetricDTO(String name, String value, String unit, LocalDateTime updatedAt) {
        this.name = name;
        this.value = value;
        this.unit = unit;
        this.updatedAt = updatedAt;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
