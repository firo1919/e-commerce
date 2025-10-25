package com.firomsa.ecommerce.v1.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.firomsa.ecommerce.exception.EmailAlreadyExistsException;
import com.firomsa.ecommerce.exception.LimitedProductStockException;
import com.firomsa.ecommerce.exception.OrderProcessException;
import com.firomsa.ecommerce.exception.ResourceNotFoundException;
import com.firomsa.ecommerce.exception.UserNameAlreadyExistsException;
import com.firomsa.ecommerce.model.Address;
import com.firomsa.ecommerce.model.Cart;
import com.firomsa.ecommerce.model.Order;
import com.firomsa.ecommerce.model.OrderItem;
import com.firomsa.ecommerce.model.Product;
import com.firomsa.ecommerce.model.Review;
import com.firomsa.ecommerce.model.Role;
import com.firomsa.ecommerce.model.User;
import com.firomsa.ecommerce.repository.AddressRepository;
import com.firomsa.ecommerce.repository.CartRepository;
import com.firomsa.ecommerce.repository.OrderItemRepository;
import com.firomsa.ecommerce.repository.OrderRepository;
import com.firomsa.ecommerce.repository.ProductRepository;
import com.firomsa.ecommerce.repository.ReviewRepository;
import com.firomsa.ecommerce.repository.RoleRepository;
import com.firomsa.ecommerce.repository.UserRepository;
import com.firomsa.ecommerce.v1.dto.AddressRequestDTO;
import com.firomsa.ecommerce.v1.dto.AddressResponseDTO;
import com.firomsa.ecommerce.v1.dto.CartRequestDTO;
import com.firomsa.ecommerce.v1.dto.CartResponseDTO;
import com.firomsa.ecommerce.v1.dto.OrderDetailDTO;
import com.firomsa.ecommerce.v1.dto.OrderResponseDTO;
import com.firomsa.ecommerce.v1.dto.ReviewRequestDTO;
import com.firomsa.ecommerce.v1.dto.ReviewResponseDTO;
import com.firomsa.ecommerce.v1.dto.UserRequestDTO;
import com.firomsa.ecommerce.v1.dto.UserResponseDTO;
import com.firomsa.ecommerce.v1.mapper.AddressMapper;
import com.firomsa.ecommerce.v1.mapper.CartMapper;
import com.firomsa.ecommerce.v1.mapper.OrderMapper;
import com.firomsa.ecommerce.v1.mapper.ReviewMapper;
import com.firomsa.ecommerce.v1.mapper.UserMapper;
import com.yaphet.chapa.model.InitializeResponseData;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final ReviewRepository reviewRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final PaymentService paymentService;

    public UserService(PasswordEncoder passwordEncoder, ReviewRepository reviewRepository,
            AddressRepository addressRepository, UserRepository userRepository, RoleRepository roleRepository,
            ProductRepository productRepository, CartRepository cartRepository,
            OrderItemRepository orderItemRepository, OrderRepository orderRepository, PaymentService paymentService) {
        this.passwordEncoder = passwordEncoder;
        this.reviewRepository = reviewRepository;
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.paymentService = paymentService;

    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponseDTO> getAll() {
        return userRepository.findAll().stream()
                .filter(User::isActive)
                .map(UserMapper::toDTO).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponseDTO> getAllInActiveUsers() {
        return userRepository.findAll().stream()
                .filter(e -> (!e.isActive()))
                .map(UserMapper::toDTO).toList();
    }

    @PreAuthorize("hasRole('ADMIN') or authentication.principal.id.equals(#id)")
    public UserResponseDTO get(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User: " + id.toString()));
        return UserMapper.toDTO(user);
    }

    public UserResponseDTO create(UserRequestDTO userRequestDTO) {
        if (userRepository.existsByEmail(userRequestDTO.getEmail())) {
            throw new EmailAlreadyExistsException(userRequestDTO.getEmail());
        }

        if (userRepository.existsByUsername(userRequestDTO.getUsername())) {
            throw new UserNameAlreadyExistsException(userRequestDTO.getUsername());
        }
        Role role = roleRepository.findByName("USER")
                .orElseThrow(() -> new ResourceNotFoundException("Role: USER"));
        User user = UserMapper.toModel(userRequestDTO);
        user.setRole(role);
        user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
        return UserMapper.toDTO(userRepository.save(user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public UserResponseDTO createAdmin(UserRequestDTO userRequestDTO) {
        if (userRepository.existsByEmail(userRequestDTO.getEmail())) {
            throw new EmailAlreadyExistsException(userRequestDTO.getEmail());
        }

        if (userRepository.existsByUsername(userRequestDTO.getUsername())) {
            throw new UserNameAlreadyExistsException(userRequestDTO.getUsername());
        }
        Role role = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new ResourceNotFoundException("Role: ADMIN"));
        User admin = UserMapper.toModel(userRequestDTO);
        admin.setRole(role);
        admin.setActive(true);
        admin.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
        return UserMapper.toDTO(userRepository.save(admin));
    }

    @PreAuthorize("hasRole('USER') and authentication.principal.id.equals(#id)")
    public UserResponseDTO update(UserRequestDTO userRequestDTO, UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User: " + id.toString()));

        if (userRepository.existsByEmailAndIdNot(userRequestDTO.getEmail(), id)) {
            throw new EmailAlreadyExistsException(userRequestDTO.getEmail());
        }

        if (userRepository.existsByUsernameAndIdNot(userRequestDTO.getUsername(), id)) {
            throw new UserNameAlreadyExistsException(userRequestDTO.getUsername());
        }

        user.setLastName(userRequestDTO.getLastName());
        user.setFirstName(userRequestDTO.getFirstName());
        user.setUsername(userRequestDTO.getUsername());
        user.setEmail(userRequestDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
        user.setUpdatedAt(LocalDateTime.now());

        return UserMapper.toDTO(userRepository.save(user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void remove(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User: " + id.toString()));
        userRepository.delete(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void softDelete(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User: " + id.toString()));
        user.setActive(false);
        userRepository.save(user);
    }

    @PreAuthorize("hasRole('ADMIN') or authentication.principal.id.equals(#id)")
    public List<CartResponseDTO> getCarts(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User: " + id.toString()));
        return user.getCarts().stream().map(CartMapper::toDTO).toList();
    }

    @PreAuthorize("hasRole('USER') and authentication.principal.id.equals(#id)")
    public CartResponseDTO addItemToCart(UUID id, CartRequestDTO cartRequestDTO, UUID productId) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User: " + id.toString()));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product: " + productId.toString()));

        LocalDateTime now = LocalDateTime.now();
        Cart cart;
        Optional<Cart> existingCart = cartRepository.findByUserAndProduct(user, product);
        if (cartRequestDTO.getQuantity() > product.getStock()) {
            throw new LimitedProductStockException("Product Stock Limited");
        }
        if (existingCart.isPresent()) {
            cart = existingCart.get();
            cart.setQuantity(cartRequestDTO.getQuantity());
            cart.setUpdatedAt(now);
        } else {
            cart = CartMapper.toModel(cartRequestDTO);
            cart.setCreatedAt(now);
            cart.setUpdatedAt(now);
            cart.setProduct(product);
            cart.setUser(user);
        }

        return CartMapper.toDTO(cartRepository.save(cart));
    }

    @PreAuthorize("hasRole('ADMIN') or authentication.principal.id.equals(#id)")
    public List<OrderResponseDTO> getOrders(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User: " + id.toString()));
        return user.getOrders().stream().map(OrderMapper::toDTO).toList();
    }

    @PreAuthorize("hasRole('USER') and authentication.principal.id.equals(#id)")
    @Transactional
    public OrderDetailDTO addOrder(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User: " + id.toString()));
        Address defAddress = addressRepository.findByUserAndActive(user, true)
                .orElseThrow(() -> new OrderProcessException("User should have address to create an order"));
        LocalDateTime now = LocalDateTime.now();
        List<Cart> cartItems = cartRepository.findAllByUser(user);
        if (cartItems.isEmpty()) {
            throw new OrderProcessException("The cart doesn't contain any items");
        }

        for (Cart cart : cartItems) {
            if (cart.getQuantity() > cart.getProduct().getStock()) {
                throw new LimitedProductStockException("Product Stock Limited");
            }
        }
        Double total = cartItems.stream().map(item -> item.getProduct().getPrice() * item.getQuantity())
                .reduce(Double::sum).orElse(0.0);
        Order order = Order.builder()
                .createdAt(now)
                .updatedAt(now)
                .user(user)
                .totalPrice(total)
                .build();
        Order savedOrder = orderRepository.save(order);
        List<OrderItem> orderItems = cartItems.stream()
                .map(item -> OrderItem.builder()
                        .priceAtPurchase(item.getProduct().getPrice())
                        .product(item.getProduct())
                        .order(savedOrder)
                        .quantity(item.getQuantity()).build())
                .toList();

        List<OrderItem> savedOrderItems = orderItemRepository.saveAll(orderItems);
        cartRepository.deleteAllByUser(user);
        savedOrder.setOrderItems(savedOrderItems);
        InitializeResponseData response = paymentService.startTransaction(savedOrder);
        OrderDetailDTO orderDetailDTO = OrderDetailDTO.builder()
                .response(response)
                .order(OrderMapper.toDTO(savedOrder))
                .address(AddressMapper.toDTO(defAddress))
                .build();
        return orderDetailDTO;
    }

    @PreAuthorize("hasRole('ADMIN') or authentication.principal.id.equals(#id)")
    public List<AddressResponseDTO> getAddresses(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User: " + id.toString()));
        return user.getAddresses().stream().map(AddressMapper::toDTO).toList();
    }

    @Transactional
    @PreAuthorize("hasRole('USER') and authentication.principal.id.equals(#id)")
    public AddressResponseDTO addAddressToAddresses(UUID id, AddressRequestDTO addressRequestDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User: " + id.toString()));
        Address address = AddressMapper.toModel(addressRequestDTO);
        if (Boolean.TRUE.equals(address.getActive())) {
            Optional<Address> defaultAddress = addressRepository.findByUserAndActive(user, true);
            if (defaultAddress.isPresent()) {
                Address defAddress = defaultAddress.get();
                defAddress.setActive(false);
                addressRepository.save(defAddress);
            }
        }
        LocalDateTime now = LocalDateTime.now();
        address.setUser(user);
        address.setCreatedAt(now);
        address.setUpdatedAt(now);
        return AddressMapper.toDTO(addressRepository.save(address));
    }

    @PreAuthorize("hasRole('USER') and authentication.principal.id.equals(#userId)")
    public void removeAddress(int addressId, UUID userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User: " + userId.toString()));
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address: " +
                        addressId));
        addressRepository.delete(address);
    }

    @Transactional
    @PreAuthorize("hasRole('USER') and authentication.principal.id.equals(#userId)")
    public AddressResponseDTO updateAddress(AddressRequestDTO addressRequestDTO, int addressId, UUID userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User: " + userId.toString()));
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address: " + addressId));
        address.setFirstName(addressRequestDTO.getFirstName());
        address.setLastName(addressRequestDTO.getLastName());
        address.setState(addressRequestDTO.getState());
        address.setCity(addressRequestDTO.getCity());
        address.setStreet(addressRequestDTO.getStreet());
        address.setZipCode(addressRequestDTO.getZipCode());
        address.setCountry(addressRequestDTO.getCountry());
        address.setPhone(addressRequestDTO.getPhone());
        address.setActive(addressRequestDTO.isActive());
        address.setUpdatedAt(LocalDateTime.now());

        if (Boolean.TRUE.equals(address.getActive())) {
            Optional<Address> defaultAddress = addressRepository.findByUserAndActive(address.getUser(), true);
            if (defaultAddress.isPresent() && !defaultAddress.get().getId().equals(address.getId())) {
                Address defAddress = defaultAddress.get();
                defAddress.setActive(false);
                addressRepository.save(defAddress);
            }
        }

        return AddressMapper.toDTO(addressRepository.save(address));
    }

    @PreAuthorize("hasRole('ADMIN') or authentication.principal.id.equals(#id)")
    public List<ReviewResponseDTO> getReviews(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User: " + id.toString()));
        return user.getReviews().stream().map(ReviewMapper::toDTO).toList();
    }

    @Transactional
    @PreAuthorize("hasRole('USER') and authentication.principal.id.equals(#id)")
    public ReviewResponseDTO addReviewToReviews(UUID id, ReviewRequestDTO reviewRequestDTO, UUID productId) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User: " + id.toString()));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product: " + productId.toString()));
        Review review = ReviewMapper.toModel(reviewRequestDTO);
        LocalDateTime now = LocalDateTime.now();
        review.setUser(user);
        review.setProduct(product);
        review.setCreatedAt(now);
        review.setUpdatedAt(now);
        return ReviewMapper.toDTO(reviewRepository.save(review));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("USER: " + username + " Not found"));
    }
}
