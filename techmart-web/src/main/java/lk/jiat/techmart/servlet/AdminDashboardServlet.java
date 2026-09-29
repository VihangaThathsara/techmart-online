package lk.jiat.techmart.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lk.jiat.techmart.remote.InventoryRemote;
import lk.jiat.techmart.remote.MetricsRemote;
import lk.jiat.techmart.remote.OrderRemote;
import lk.jiat.techmart.remote.ProductRemote;
import lk.jiat.techmart.web.util.LookupUtil;

import javax.naming.NamingException;
import java.io.IOException;

@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            ProductRemote productRemote = LookupUtil.productRemote();
            OrderRemote orderRemote = LookupUtil.orderRemote();
            InventoryRemote inventoryRemote = LookupUtil.inventoryRemote();
            MetricsRemote metricsRemote = LookupUtil.metricsRemote();
            req.setAttribute("products", productRemote.getAllProducts());
            req.setAttribute("orders", orderRemote.getAllOrders());
            req.setAttribute("lowStock", inventoryRemote.getLowStockProducts());
            req.setAttribute("metrics", metricsRemote.getMetrics());
            req.getRequestDispatcher("/admin/dashboard.jsp").forward(req, resp);
        } catch (NamingException e) {
            throw new ServletException(e);
        }
    }
}
