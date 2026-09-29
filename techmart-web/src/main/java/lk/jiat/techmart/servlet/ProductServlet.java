package lk.jiat.techmart.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lk.jiat.techmart.dto.ProductDTO;
import lk.jiat.techmart.remote.ProductRemote;
import lk.jiat.techmart.web.util.LookupUtil;
import lk.jiat.techmart.web.util.ProductImageUtil;

import javax.naming.NamingException;
import java.io.IOException;
import java.util.List;

@WebServlet("/products")
public class ProductServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            ProductRemote productRemote = LookupUtil.productRemote();
            String keyword = req.getParameter("q");
            List<ProductDTO> products = productRemote.searchProducts(keyword);
            ProductImageUtil.applyLocalImages(products);
            req.setAttribute("products", products);
            req.getRequestDispatcher("/products.jsp").forward(req, resp);
        } catch (NamingException e) {
            throw new ServletException(e);
        }
    }
}
