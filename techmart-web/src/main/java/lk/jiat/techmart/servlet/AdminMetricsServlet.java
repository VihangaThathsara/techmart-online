package lk.jiat.techmart.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lk.jiat.techmart.dto.MetricDTO;
import lk.jiat.techmart.remote.MetricsRemote;
import lk.jiat.techmart.web.util.LookupUtil;

import javax.naming.NamingException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/admin/metrics")
public class AdminMetricsServlet extends HttpServlet {
    private static final String[] IMPORTANT_METRICS = {
            "Database Status",
            "Orders Received",
            "Orders Completed",
            "Orders Failed",
            "Average Order Placement Time",
            "Average Order Processing Time",
            "Last Order Time",
            "Last Order Processing Time",
            "HTTP Requests",
            "Last HTTP Time",
            "EJB Invocations",
            "JMS Messages Sent",
            "JMS Send Failures",
            "JMS Topic Events",
            "JMS Topic Failures",
            "MDB Messages Consumed",
            "MDB Failures",
            "Topic Events Consumed",
            "Async Tasks Started",
            "Async Tasks Completed",
            "Async Timeouts",
            "Inventory Reservations",
            "Inventory Reservation Failures",
            "Inventory Releases",
            "Inventory Sync Runs",
            "Notifications Queued",
            "Notifications Sent",
            "Average Product Catalogue Lookup Time",
            "Average Inventory Reservation Time",
            "Latest Store Event",
            "Latest Stock Alert"
    };

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            MetricsRemote metricsRemote = LookupUtil.metricsRemote();
            List<MetricDTO> allMetrics = metricsRemote.getMetrics();
            Map<String, MetricDTO> byName = new LinkedHashMap<>();
            if (allMetrics != null) {
                for (MetricDTO metric : allMetrics) {
                    byName.put(metric.getName(), metric);
                }
            }
            List<MetricDTO> selected = new ArrayList<>();
            for (String name : IMPORTANT_METRICS) {
                MetricDTO metric = byName.get(name);
                if (metric != null) {
                    selected.add(metric);
                }
            }
            req.setAttribute("metrics", selected);
            req.setAttribute("metricMap", byName);
            req.setAttribute("summary", metricsRemote.getSummary());
            req.getRequestDispatcher("/admin/metrics.jsp").forward(req, resp);
        } catch (NamingException e) {
            throw new ServletException(e);
        }
    }
}
