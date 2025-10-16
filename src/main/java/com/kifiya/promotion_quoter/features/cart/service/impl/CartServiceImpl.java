package com.kifiya.promotion_quoter.features.cart.service.impl;

import com.kifiya.promotion_quoter.features.cart.dto.request.CartItem;
import com.kifiya.promotion_quoter.features.cart.dto.request.CartRequestDto;
import com.kifiya.promotion_quoter.features.cart.dto.response.CartConfirmResponse;
import com.kifiya.promotion_quoter.features.cart.dto.response.CartQuoteResponse;
import com.kifiya.promotion_quoter.features.cart.service.CartService;
import com.kifiya.promotion_quoter.features.order.model.OrderReservation;
import com.kifiya.promotion_quoter.features.order.repository.OrderRepository;
import com.kifiya.promotion_quoter.features.product.exception.InsufficientStockException;
import com.kifiya.promotion_quoter.features.product.exception.ProductNotFoundException;
import com.kifiya.promotion_quoter.features.product.model.Product;
import com.kifiya.promotion_quoter.features.product.repository.ProductRepository;
import com.kifiya.promotion_quoter.features.promotion.model.Promotion;
import com.kifiya.promotion_quoter.features.promotion.repository.PromotionRepository;
import com.kifiya.promotion_quoter.shared.exceptions.base.BaseException;
import com.kifiya.promotion_quoter.shared.rules.context.CartCalculationContext;
import com.kifiya.promotion_quoter.shared.rules.promo_rules.PromotionRule;
import com.kifiya.promotion_quoter.shared.rules.promo_rules.strategy.factory.PromotionRuleFactory;
import com.kifiya.promotion_quoter.shared.utils.price.PriceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final ProductRepository productRepository;
    private final PromotionRepository promotionRepository;
    private final OrderRepository orderRepository;
    private final PromotionRuleFactory ruleFactory;

    @Override
    public CartQuoteResponse quote(CartRequestDto request) {
        CartCalculationContext context = buildContext(request);
        applyPromotionChain(context);
        BigDecimal total = context.getCurrentPrices().values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        return new CartQuoteResponse(context.getCurrentPrices(), total, context.getAuditTrail());
    }

    @Transactional
    public CartConfirmResponse confirm(CartRequestDto request, String idempotencyKey) {
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            Optional<OrderReservation> existing = orderRepository.findByIdempotencyKey(idempotencyKey);
            if (existing.isPresent()) {
                log.info("Idempotent request detected, returning existing order: {}", existing.get().getId());
                return new CartConfirmResponse(existing.get().getFinalPrice(), existing.get().getId());
            }
        }

        CartCalculationContext context = buildContext(request);
        lockAndCheckStock(context);

        applyPromotionChain(context);
        BigDecimal total = context.getCurrentPrices().values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        reserveStock(context);

        OrderReservation order = new OrderReservation();
        order.setIdempotencyKey(idempotencyKey);
        order.setFinalPrice(total);
        orderRepository.save(order);
        log.info("Order confirmed: {}", order.getId());

        return new CartConfirmResponse(total, order.getId());
    }

    private CartCalculationContext buildContext(CartRequestDto request) {

        List<String> productIds = request.items().stream().map(CartItem::productId).distinct().toList();

        Map<String, Product> products = productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        if (products.size() != productIds.size()) {
            throw new BaseException("One or more products not found");
        }

        CartCalculationContext context = new CartCalculationContext();

        context.setItems(request.items());
        context.setProducts(products);

        for (CartItem item : request.items()) {
            Product p = products.get(item.productId());
            BigDecimal linePrice = PriceUtil.toCents(p.getPrice().multiply(BigDecimal.valueOf(item.quantity())));
            context.getCurrentPrices().put(item.productId(), linePrice);
        }
        return context;
    }

    private void applyPromotionChain(CartCalculationContext context) {

        List<Promotion> promotions = promotionRepository.findAllByActiveTrue().stream()
                .sorted(Comparator.comparingInt(Promotion::getOrderPriority))
                .toList();

        // pipeline
        for (Promotion promo : promotions) {
            PromotionRule rule = ruleFactory.getRule(promo);
            rule.apply(context, promo);
        }
    }

    private void lockAndCheckStock(CartCalculationContext context) {
        for (CartItem item : context.getItems()) {
            Product product = productRepository.findById(item.productId())
                    .orElseThrow(ProductNotFoundException::new);

            if (product.getStock() < item.quantity()) {
                throw new InsufficientStockException("Insufficient stock for product " + item.productId() + ". Available: " + product.getStock() + ", requested: " + item.quantity());
            }

            context.getProducts().put(item.productId(), product);
        }
    }

    private void reserveStock(CartCalculationContext context) {
        for (CartItem item : context.getItems()) {
            Product product = context.getProducts().get(item.productId());
            product.setStock(product.getStock() - item.quantity());
            productRepository.save(product);
        }
    }
}
