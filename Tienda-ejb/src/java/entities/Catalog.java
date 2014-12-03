package entities;

import interfaces.ICatalog;
import interfaces.IShoppingCart;
import discount.cartdiscount.CartDiscount;
import discount.Discount;
import loader.DiscountLoader;
import discount.productdiscount.ProductDiscount;
import java.util.ArrayList;
import javax.ejb.Stateless;
import loader.ProductLoader;

@Stateless
public class Catalog implements ICatalog {

    public ArrayList<Product> listProduct = new ArrayList<>();
    
    public Catalog () {
        new ProductLoader().load(listProduct);
        new DiscountLoader().load(listProduct);
        applyProductDiscounts();
    }

    @Override
    public ArrayList<Product> getCatalog() {
        return listProduct;
    }

    @Override
    public void applyProductDiscounts() {
        for (Product product : listProduct) {
            try {
                Discount discount = product.getDiscount();
                if (discount != null)   ((ProductDiscount) discount).apply(product);
            } catch (ClassCastException ex) {
                product.setPrice(product.getPricePerUnit());
            }
        }
    }

    @Override
    public void applyCartDiscounts(IShoppingCart cart) {
        if (!cart.isUpdate()) {
            for (Product product : cart.getCart().keySet()) {
                try {
                    Discount discount = product.getDiscount();
                    if (discount != null)   {
                        ((CartDiscount) discount).apply(cart, product);
                    }
                } catch (ClassCastException ex) {
                }
            }
            cart.setUpdate(true);
        }
    }
  
}