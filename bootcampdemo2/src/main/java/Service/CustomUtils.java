package Service;

import pojo.Order;

import java.util.ArrayList;
import java.util.List;

public class CustomUtils {

    public static List<Order> createDummyList(){
        List<Order> orders = new ArrayList<>();

        for (int i=0; i<7; i++){
            Order order = new Order("Order_"+i, i);
            orders.add(order);
        }

        return orders;
    }
}
