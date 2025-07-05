package com.firomsa.ecommerce.service;

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

import com.firomsa.ecommerce.dto.AddressRequestDTO;
import com.firomsa.ecommerce.dto.AddressResponseDTO;
import com.firomsa.ecommerce.dto.CartRequestDTO;
import com.firomsa.ecommerce.dto.CartResponseDTO;
import com.firomsa.ecommerce.dto.OrderResponseDTO;
import com.firomsa.ecommerce.dto.ReviewRequestDTO;
import com.firomsa.ecommerce.dto.ReviewResponseDTO;
import com.firomsa.ecommerce.dto.UserRequestDTO;
import com.firomsa.ecommerce.dto.UserResponseDTO;
import com.firomsa.ecommerce.exception.EmailAlreadyExistsException;
import com.firomsa.ecommerce.exception.LimitedProductStockException;
import com.firomsa.ecommerce.exception.ResourceNotFoundException;
import com.firomsa.ecommerce.exception.UserNameAlreadyExistsException;
import com.firomsa.ecommerce.mapper.AddressMapper;
import com.firomsa.ecommerce.mapper.CartMapper;
import com.firomsa.ecommerce.mapper.OrderMapper;
import com.firomsa.ecommerce.mapper.ReviewMapper;
import com.firomsa.ecommerce.mapper.UserMapper;
import com.firomsa.ecommerce.model.Address;
import com.firomsa.ecommerce.model.Cart;
import com.firomsa.ecommerce.model.Product;
import com.firomsa.ecommerce.model.Review;
import com.firomsa.ecommerce.model.Role;
import com.firomsa.ecommerce.model.User;
import com.firomsa.ecommerce.repository.AddressRepository;
import com.firomsa.ecommerce.repository.CartRepository;
import com.firomsa.ecommerce.repository.ProductRepository;
import com.firomsa.ecommerce.repository.ReviewRepository;
import com.firomsa.ecommerce.repository.RoleRepository;
import com.firomsa.ecommerce.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserService implements UserDetailsService{

    private final PasswordEncoder passwordEncoder;
    private final ReviewRepository reviewRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;

    public UserService(PasswordEncoder passwordEncoder, ReviewRepository reviewRepository,
            AddressRepository addressRepository, UserRepository userRepository, RoleRepository roleRepository,
            ProductRepository productRepository, CartRepository cartRepository) {
        this.passwordEncoder = passwordEncoder;
        this.reviewRepository = reviewRepository;
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponseDTO> getAll() {
        return userRepository.findAll().stream().map(UserMapper::toDTO).toList();
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
        user.setActive(true);
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
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User: " + id.toString()));

        if (userRepository.existsByEmailAndIdNot(userRequestDTO.getEmail(), id)) {
            throw new EmailAlreadyExistsException(userRequestDTO.getEmail());
        }

        if (userRepository.existsByUsername(userRequestDTO.getUsername())) {
            throw new UserNameAlreadyExistsException(userRequestDTO.getUsername());
        }

        user.setLastName(userRequestDTO.getLastName());
        user.setFirstName(userRequestDTO.getFirstName());
        user.setUsername(userRequestDTO.getUsername());
        user.setEmail(userRequestDTO.getEmail());
        user.setPassword(userRequestDTO.getPassword());
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
        if(cartRequestDTO.getQuantity() > product.getStock()){
            throw new LimitedProductStockException("Product Stock Limited");
        }
        if (existingCart.isPresent()) {
            cart = existingCart.get();
            int quantity = cart.getQuantity();
            cart.setQuantity(quantity + cartRequestDTO.getQuantity());
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
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product: " + productId.toString()));
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
        return userRepository.findByUsername(username).orElseThrow(() ->
            new UsernameNotFoundException("USER: "+username +" Not found")
        );
    }
}
