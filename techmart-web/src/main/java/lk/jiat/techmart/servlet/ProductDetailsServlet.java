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

@WebServlet("/product-details")
public class ProductDetailsServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Long id = Long.parseLong(req.getParameter("id"));
            ProductRemote productRemote = LookupUtil.productRemote();
            ProductDTO product = productRemote.getProduct(id);
            ProductImageUtil.applyLocalImage(product);
            req.setAttribute("product", product);
            req.getRequestDispatcher("/product-details.jsp").forward(req, resp);
        } catch (NamingException e) {
            throw new ServletException(e);
        }
    }
}
