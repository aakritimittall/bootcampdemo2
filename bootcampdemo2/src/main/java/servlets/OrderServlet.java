package servlets;

import Service.CustomUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thoughtworks.xstream.XStream;
import pojo.Order;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/orders")
public class OrderServlet extends HttpServlet {
    private List<Order> orderList = new ArrayList<>();

    @Override
    public void init() {
        System.out.println("In INIT Method");
        orderList = CustomUtils.createDummyList();
//        ServletConfig config = getServletConfig();
//        System.out.println(config.getInitParameter("driver"));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter("id");
        Gson gson = new GsonBuilder().create();
        String accept = req.getHeader("accept");
        if (id != null && !id.equals("")){
            Order order = getOrderById(Integer.parseInt(id));
            if (order == null){
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }

            if (accept.equals("application/xml")){
                resp.setContentType("application/xml");
                XStream xStream = new XStream();
                xStream.alias("order", Order.class);
                String xml = xStream.toXML(order);
                OutputStream out = resp.getOutputStream();
                out.write(xml.getBytes());
                out.flush();
            } else {
                //resp.setContentType("application/json");
                resp.setHeader("ContentType", "application/json");
                PrintWriter out = resp.getWriter();
                out.write(gson.toJson(order));
                out.close();
            }
        }else {
            if (accept.equals("application/xml")){
                resp.setContentType("application/xml");
                OutputStream out = resp.getOutputStream();
                XStream xStream = new XStream();
                xStream.alias("order", Order.class);
                StringBuilder xml = new StringBuilder();
                for (Order order : orderList){
                    xml.append(xStream.toXML(order));
                }
                out.write(xml.toString().getBytes());
                out.flush();
            } else {
                resp.setContentType("application/json");
                PrintWriter out = resp.getWriter();
                out.println(gson.toJson(orderList));
                out.close();
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = req.getParameter("name");
        String id = req.getParameter("id");

        if (name != null && id != null){
            String status = addOrder(Integer.parseInt(id), name, resp);

            PrintWriter out = resp.getWriter();
            out.println(status);
            out.close();
        } else{
            resp.setContentType("text/plain");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            PrintWriter out = resp.getWriter();
            out.write("Please try again later!");
            out.close();
        }
    }

    private String addOrder(int id, String name, HttpServletResponse response){
        for (Order order : orderList){
            if (order.getId() == id){
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                return "Order with id " + id + " already exists.";
            }
        }
        this.orderList.add(new Order(name, id));
        response.setStatus(HttpServletResponse.SC_CREATED);
        return "Order successfully added";
    }

    private Order getOrderById(int id){
        for (Order order : orderList){
            if (order.getId() == id){
                return order;
            }
        }
        return null;
    }

}
