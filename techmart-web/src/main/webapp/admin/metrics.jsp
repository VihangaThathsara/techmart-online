<%@ page import="java.util.Map" %>
<%@ page import="lk.jiat.techmart.dto.MetricDTO" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%!
    private String val(Map<String, MetricDTO> map, String name) {
        MetricDTO metric = map == null ? null : map.get(name);
        String value = metric == null ? "0" : metric.getValue();
        return value == null || value.trim().isEmpty() ? "0" : value.trim();
    }

    private int asNumber(String value) {
        if (value == null) {
            return 0;
        }
        try {
            String cleaned = value.replaceAll("[^0-9]", "");
            return cleaned.isEmpty() ? 0 : Integer.parseInt(cleaned);
        } catch (Exception e) {
            return 0;
        }
    }

    private int percent(String value, int target) {
        if (target <= 0) {
            return 0;
        }
        int number = asNumber(value);
        int pct = (int) Math.round((number * 100.0) / target);
        return Math.max(4, Math.min(100, pct));
    }

    private String cleanDatabaseStatus(String value) {
        if (value == null || value.trim().isEmpty() || "0".equals(value.trim())) {
            return "Not checked";
        }
        String lower = value.toLowerCase();
        if (lower.contains("disconnected")) {
            return "Disconnected";
        }
        if (lower.contains("connected")) {
            return value.replaceAll("jdbc:[^\\s]+", "database");
        }
        if (lower.contains("ready")) {
            return "Connected";
        }
        return "Available";
    }

    private String statusClass(String value) {
        return value != null && value.toLowerCase().contains("disconnected") ? "disconnected" : "connected";
    }
%>
<%
    Map<String, MetricDTO> metricMap = (Map<String, MetricDTO>) request.getAttribute("metricMap");
    String dbStatus = cleanDatabaseStatus(val(metricMap, "Database Status"));
    String summary = (String) request.getAttribute("summary");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Reports | TechMart Admin</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/favicon-techmart.png">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css">
</head>
<body class="admin-bg">
<div class="admin-layout">
    <aside class="admin-sidebar">
        <div class="admin-brand-block"><span class="admin-brand-shell"><img class="admin-brand-logo"
                                                                            src="${pageContext.request.contextPath}/assets/logo-techmart-transparent-light.png"
                                                                            alt="TechMart Online"></span><span
                class="admin-panel-label">Admin Panel</span></div>
        <nav class="admin-menu"><a href="dashboard">Dashboard</a><a href="inventory">Inventory</a><a href="orders">Orders</a><a
                class="active" href="metrics">Reports</a><a href="${pageContext.request.contextPath}/logout">Logout</a>
        </nav>
    </aside>

    <main class="admin-main">
        <div class="admin-top">
            <div>
                <span class="kicker">Performance center</span>
                <h1>Metrics &amp; health</h1>
                <p>Clean monitoring view for orders, requests, JMS activity, MDB background work and database
                    health.</p>
                <% if (summary != null) { %><span class="stat-note"><%= summary %></span><% } %>
            </div>
            <a class="btn ghost" href="dashboard">Dashboard</a>
        </div>

        <div class="metric-clean-grid">
            <div class="metric-clean-card"><span>Orders received</span><strong><%= val(metricMap, "Orders Received") %>
            </strong><small>Total order requests accepted by the store</small></div>
            <div class="metric-clean-card">
                <span>Completed orders</span><strong><%= val(metricMap, "Orders Completed") %>
            </strong><small>Successfully processed customer orders</small></div>
            <div class="metric-clean-card">
                <span>Average processing</span><strong><%= val(metricMap, "Average Order Processing Time") %>
                ms</strong><small>Recent asynchronous processing average</small></div>
            <div class="metric-clean-card"><span>HTTP requests</span><strong><%= val(metricMap, "HTTP Requests") %>
            </strong><small>Web interface request counter</small></div>
            <div class="metric-clean-card"><span>JMS messages</span><strong><%= val(metricMap, "JMS Messages Sent") %>
            </strong><small>Order and notification messages sent</small></div>
            <div class="metric-clean-card">
                <span>MDB consumed</span><strong><%= val(metricMap, "MDB Messages Consumed") %>
            </strong><small>Background jobs completed by MDBs</small></div>
            <div class="metric-clean-card"><span>Topic events</span><strong><%= val(metricMap, "JMS Topic Events") %>
            </strong><small>Publish/subscribe inventory and order events</small></div>
            <div class="metric-clean-card">
                <span>Inventory reserves</span><strong><%= val(metricMap, "Inventory Reservations") %>
            </strong><small>Stock reservations across warehouses</small></div>
        </div>

        <section class="admin-section admin-two">
            <div class="admin-card">
                <div class="admin-section-head">
                    <div><span class="kicker">Health</span>
                        <h2>System status</h2></div>
                    <span class="status <%= statusClass(dbStatus) %>"><%= dbStatus %></span></div>
                <div class="health-list">
                    <div class="health-item"><span>Database</span><b><%= dbStatus %>
                    </b></div>
                    <div class="health-item"><span>Failed orders</span><b><%= val(metricMap, "Orders Failed") %>
                    </b></div>
                    <div class="health-item"><span>MDB failures</span><b><%= val(metricMap, "MDB Failures") %>
                    </b></div>
                    <div class="health-item"><span>JMS failures</span><b><%= val(metricMap, "JMS Send Failures") %>
                    </b></div>
                    <div class="health-item"><span>Async started</span><b><%= val(metricMap, "Async Tasks Started") %>
                    </b></div>
                    <div class="health-item">
                        <span>Async completed</span><b><%= val(metricMap, "Async Tasks Completed") %>
                    </b></div>
                </div>
            </div>

            <div class="admin-card">
                <div class="admin-section-head">
                    <div><span class="kicker">Throughput</span>
                        <h2>Activity snapshot</h2></div>
                </div>
                <div class="progress-list">
                    <div class="progress-item">
                        <div class="progress-label"><span>Order intake</span><b><%= val(metricMap, "Orders Received") %>
                        </b></div>
                        <div class="progress-track">
                            <div class="progress-fill"
                                 style="width:<%= percent(val(metricMap, "Orders Received"), 20) %>%"></div>
                        </div>
                    </div>
                    <div class="progress-item">
                        <div class="progress-label">
                            <span>Completed orders</span><b><%= val(metricMap, "Orders Completed") %>
                        </b></div>
                        <div class="progress-track">
                            <div class="progress-fill"
                                 style="width:<%= percent(val(metricMap, "Orders Completed"), 20) %>%"></div>
                        </div>
                    </div>
                    <div class="progress-item">
                        <div class="progress-label"><span>Web requests</span><b><%= val(metricMap, "HTTP Requests") %>
                        </b></div>
                        <div class="progress-track">
                            <div class="progress-fill"
                                 style="width:<%= percent(val(metricMap, "HTTP Requests"), 80) %>%"></div>
                        </div>
                    </div>
                    <div class="progress-item">
                        <div class="progress-label">
                            <span>Message work</span><b><%= val(metricMap, "MDB Messages Consumed") %>
                        </b></div>
                        <div class="progress-track">
                            <div class="progress-fill"
                                 style="width:<%= percent(val(metricMap, "MDB Messages Consumed"), 30) %>%"></div>
                        </div>
                    </div>
                </div>
            </div>
        </section>

        <section class="admin-section admin-two">
            <div class="admin-card">
                <div class="admin-section-head">
                    <div><span class="kicker">Timing</span>
                        <h2>Processing speed</h2></div>
                </div>
                <div class="health-list">
                    <div class="health-item">
                        <span>Avg order placement</span><b><%= val(metricMap, "Average Order Placement Time") %> ms</b>
                    </div>
                    <div class="health-item">
                        <span>Avg order processing</span><b><%= val(metricMap, "Average Order Processing Time") %>
                        ms</b></div>
                    <div class="health-item">
                        <span>Avg product lookup</span><b><%= val(metricMap, "Average Product Catalogue Lookup Time") %>
                        ms</b></div>
                    <div class="health-item">
                        <span>Avg reservation</span><b><%= val(metricMap, "Average Inventory Reservation Time") %>
                        ms</b></div>
                    <div class="health-item"><span>Last HTTP time</span><b><%= val(metricMap, "Last HTTP Time") %>
                        ms</b></div>
                    <div class="health-item"><span>EJB invocations</span><b><%= val(metricMap, "EJB Invocations") %>
                    </b></div>
                </div>
            </div>
            <div class="admin-card">
                <div class="admin-section-head">
                    <div><span class="kicker">Events</span>
                        <h2>Runtime activity</h2></div>
                </div>
                <div class="health-list">
                    <div class="health-item">
                        <span>Latest store event</span><b><%= val(metricMap, "Latest Store Event") %>
                    </b></div>
                    <div class="health-item">
                        <span>Latest stock alert</span><b><%= val(metricMap, "Latest Stock Alert") %>
                    </b></div>
                    <div class="health-item">
                        <span>Notifications queued</span><b><%= val(metricMap, "Notifications Queued") %>
                    </b></div>
                    <div class="health-item">
                        <span>Notifications sent</span><b><%= val(metricMap, "Notifications Sent") %>
                    </b></div>
                </div>
            </div>
        </section>
    </main>
</div>
</body>
</html>
