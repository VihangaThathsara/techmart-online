<%@ page import="java.util.List" %>
<%@ page import="lk.jiat.techmart.dto.ProductDTO" %>
<%@ page import="lk.jiat.techmart.dto.OrderDTO" %>
<%@ page import="lk.jiat.techmart.dto.MetricDTO" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%!
    private String metric(List<MetricDTO> metrics, String name) {
        if (metrics == null) {
            return "0";
        }
        for (MetricDTO metric : metrics) {
            if (metric != null && name.equals(metric.getName())) {
                String value = metric.getValue();
                return value == null || value.trim().isEmpty() ? "0" : value.trim();
            }
        }
        return "0";
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
    List<ProductDTO> products = (List<ProductDTO>) request.getAttribute("products");
    List<ProductDTO> lowStock = (List<ProductDTO>) request.getAttribute("lowStock");
    List<OrderDTO> orders = (List<OrderDTO>) request.getAttribute("orders");
    List<MetricDTO> metrics = (List<MetricDTO>) request.getAttribute("metrics");
    String dbStatus = cleanDatabaseStatus(metric(metrics, "Database Status"));
    String flashMessage = (String) session.getAttribute("flashMessage");
    String flashType = (String) session.getAttribute("flashType");
    if (flashMessage != null) {
        session.removeAttribute("flashMessage");
        session.removeAttribute("flashType");
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Dashboard | TechMart Admin</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/favicon-techmart.png">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css">
</head>
<body class="admin-bg">
<div class="admin-layout">
    <aside class="admin-sidebar">
        <div class="admin-brand-block">
            <span class="admin-brand-shell"><img class="admin-brand-logo"
                                                 src="${pageContext.request.contextPath}/assets/logo-techmart-transparent-light.png"
                                                 alt="TechMart Online"></span>
            <span class="admin-panel-label">Admin Panel</span>
        </div>
        <nav class="admin-menu">
            <a class="active" href="dashboard">Dashboard</a>
            <a href="inventory">Inventory</a>
            <a href="orders">Orders</a>
            <a href="metrics">Reports</a>
            <a href="${pageContext.request.contextPath}/logout">Logout</a>
        </nav>
    </aside>

    <main class="admin-main">
        <% if (flashMessage != null) { %>
        <div class="toast-stack toast-stack-admin">
            <div class="toast <%= flashType == null ? "info" : flashType %>"><span><%= flashMessage %></span>
                <button type="button" onclick="this.parentElement.parentElement.remove()">×</button>
            </div>
        </div>
        <% } %>
        <div class="admin-top">
            <div>
                <span class="kicker">Command center</span>
                <h1>Dashboard</h1>
                <p>Modern overview for store activity, catalog status and enterprise performance monitoring.</p>
            </div>
            <div class="hero-actions">
                <a class="btn ghost" href="${pageContext.request.contextPath}/products">Open store</a>
                <a class="btn" href="metrics">View metrics</a>
            </div>
        </div>

        <div class="admin-grid">
            <div class="admin-card"><span class="card-label">Products</span>
                <div class="metric-value"><%= products == null ? 0 : products.size() %>
                </div>
                <span class="stat-note">Catalog items</span></div>
            <div class="admin-card"><span class="card-label">Low stock</span>
                <div class="metric-value"><%= lowStock == null ? 0 : lowStock.size() %>
                </div>
                <span class="stat-note">Needs attention</span></div>
            <div class="admin-card"><span class="card-label">Orders</span>
                <div class="metric-value"><%= orders == null ? 0 : orders.size() %>
                </div>
                <span class="stat-note">Order desk</span></div>
            <div class="admin-card"><span class="card-label">Database</span>
                <div class="metric-value"><span class="status <%= statusClass(dbStatus) %>"><%= dbStatus %></span></div>
                <span class="stat-note">Secure connection details</span></div>
        </div>

        <section class="admin-section admin-two">
            <div class="admin-card">
                <div class="admin-section-head">
                    <div><span class="kicker">Sales</span>
                        <h2>Order activity</h2></div>
                    <a class="btn small" href="orders">View orders</a>
                </div>
                <div class="health-list">
                    <div class="health-item"><span>Orders received</span><b><%= metric(metrics, "Orders Received") %>
                    </b></div>
                    <div class="health-item"><span>Orders completed</span><b><%= metric(metrics, "Orders Completed") %>
                    </b></div>
                    <div class="health-item"><span>Orders failed</span><b><%= metric(metrics, "Orders Failed") %>
                    </b></div>
                </div>
                <div class="mini-chart"><span></span><span></span><span></span><span></span><span></span><span></span>
                </div>
            </div>

            <div class="admin-card">
                <div class="admin-section-head">
                    <div><span class="kicker">Performance</span>
                        <h2>Runtime metrics</h2></div>
                    <a class="btn small" href="metrics">Reports</a>
                </div>
                <div class="health-list">
                    <div class="health-item"><span>HTTP requests</span><b><%= metric(metrics, "HTTP Requests") %>
                    </b></div>
                    <div class="health-item"><span>JMS messages</span><b><%= metric(metrics, "JMS Messages Sent") %>
                    </b></div>
                    <div class="health-item"><span>MDB consumed</span><b><%= metric(metrics, "MDB Messages Consumed") %>
                    </b></div>
                    <div class="health-item">
                        <span>Avg processing</span><b><%= metric(metrics, "Average Order Processing Time") %> ms</b>
                    </div>
                </div>
            </div>
        </section>
    </main>
</div>
<script>setTimeout(function () {
    var stack = document.querySelector('.toast-stack');
    if (stack) stack.remove();
}, 4000);</script>
</body>
</html>
