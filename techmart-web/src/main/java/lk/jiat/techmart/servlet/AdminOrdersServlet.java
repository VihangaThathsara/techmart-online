package lk.jiat.techmart.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lk.jiat.techmart.remote.OrderRemote;
import lk.jiat.techmart.web.util.LookupUtil;

import javax.naming.NamingException;
import java.io.IOException;

@WebServlet("/admin/orders")
public class AdminOrdersServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            OrderRemote orderRemote = LookupUtil.orderRemote();
            req.setAttribute("orders", orderRemote.getAllOrders());
            req.getRequestDispatcher("/admin/orders.jsp").forward(req, resp);
        } catch (NamingException e) {
            throw new ServletException(e);
        }
    }
}
