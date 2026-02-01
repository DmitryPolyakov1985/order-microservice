package com.ecommerce.order.service;

import com.ecommerce.order.clients.ProductServiceClient;
import com.ecommerce.order.dto.CartItemRequest;
import com.ecommerce.order.dto.ProductResponse;
import com.ecommerce.order.model.CartItem;
//import com.ecommerce.order.model.Product;
//import com.ecommerce.order.model.User;
import com.ecommerce.order.repository.CartItemRepository;
//import com.ecommerce.order.repository.ProductRepository;
//import com.ecommerce.order.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CartService {
    private final CartItemRepository cartItemRepository;
    private final ProductServiceClient productServiceClient;
//    private ProductRepository productRepository;
//    private UserRepository userRepository;

    public CartService(CartItemRepository cartItemRepository, ProductServiceClient productServiceClient) {
        this.cartItemRepository = cartItemRepository;
//        this.productRepository = productRepository;
//        this.userRepository =  userRepository;
        this.productServiceClient = productServiceClient;
    }

    public boolean addToCart(String userId, CartItemRequest request) {
        ProductResponse productResponse = productServiceClient.getProductDetails(String.valueOf(request.getProductId()));
        if (productResponse == null || productResponse.getStockQuantity() < request.getQuantity()) {
            return false;
        }

//        Optional<User> userOpt = userRepository.findById(Long.valueOf(userId));
//        if (userOpt.isEmpty()) {
//            return false;
//        }
//
//        User user = userOpt.get();

        CartItem existingCartItem = cartItemRepository.findByUserIdAndProductId(Long.valueOf(userId), request.getProductId());
        if (existingCartItem != null) {
            // update quantity
            existingCartItem.setQuantity(existingCartItem.getQuantity() + request.getQuantity());
            existingCartItem.setPrice(BigDecimal.valueOf(1000.00));
            cartItemRepository.save(existingCartItem);
        } else {
            // create new cart item
            CartItem cartItem = new CartItem();
            cartItem.setUserId(Long.valueOf(userId));
            cartItem.setProductId(request.getProductId());
            cartItem.setQuantity(request.getQuantity());
            cartItem.setPrice(BigDecimal.valueOf(1000.00));
            cartItemRepository.save(cartItem);
        }
        return true;
    }

    public boolean deleteItemFromCart(String userId, Long productId) {
       CartItem cartItem = cartItemRepository.findByUserIdAndProductId(Long.valueOf(userId), productId);

        if (cartItem != null) {
            cartItemRepository.delete(cartItem);
            return true;
        }

        return false;
    }

    public List<CartItem> getCart(String userId) {
        return cartItemRepository.findByUserId(Long.valueOf(userId));
    }

    public void clearCart(String userId) {
        cartItemRepository.deleteByUserId(Long.valueOf(userId));
    }
}
