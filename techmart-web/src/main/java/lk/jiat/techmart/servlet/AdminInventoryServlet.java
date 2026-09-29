package lk.jiat.techmart.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lk.jiat.techmart.dto.ProductDTO;
import lk.jiat.techmart.remote.InventoryRemote;
import lk.jiat.techmart.remote.ProductRemote;
import lk.jiat.techmart.web.util.FlashMessageUtil;
import lk.jiat.techmart.web.util.LookupUtil;
import lk.jiat.techmart.web.util.ProductImageUtil;

import javax.naming.NamingException;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/inventory")
public class AdminInventoryServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            ProductRemote productRemote = LookupUtil.productRemote();
            InventoryRemote inventoryRemote = LookupUtil.inventoryRemote();
            if ("sync".equals(req.getParameter("action"))) {
                inventoryRemote.synchronizeInventory();
                FlashMessageUtil.success(req.getSession(), "Inventory synchronized successfully.");
                resp.sendRedirect(req.getContextPath() + "/admin/inventory");
                return;
            }
            List<ProductDTO> products = productRemote.getAllProducts();
            ProductImageUtil.applyLocalImages(products);
            req.setAttribute("products", products);
            req.setAttribute("lowStock", inventoryRemote.getLowStockProducts());
            req.setAttribute("warehouseStock", inventoryRemote.getWarehouseStock());
            req.getRequestDispatcher("/admin/inventory.jsp").forward(req, resp);
        } catch (NamingException e) {
            throw new ServletException(e);
        }
    }
}
