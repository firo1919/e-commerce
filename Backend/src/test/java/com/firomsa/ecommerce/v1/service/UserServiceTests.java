package com.firomsa.ecommerce.v1.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import com.firomsa.ecommerce.exception.EmailAlreadyExistsException;
import com.firomsa.ecommerce.exception.LimitedProductStockException;
import com.firomsa.ecommerce.exception.OrderProcessException;
import com.firomsa.ecommerce.exception.ResourceNotFoundException;
import com.firomsa.ecommerce.exception.UserNameAlreadyExistsException;
import com.firomsa.ecommerce.model.Address;
import com.firomsa.ecommerce.model.Cart;
import com.firomsa.ecommerce.model.Category;
import com.firomsa.ecommerce.model.Order;
import com.firomsa.ecommerce.model.Product;
import com.firomsa.ecommerce.model.Review;
import com.firomsa.ecommerce.model.Role;
import com.firomsa.ecommerce.model.User;
import com.firomsa.ecommerce.repository.AddressRepository;
import com.firomsa.ecommerce.repository.CartRepository;
import com.firomsa.ecommerce.repository.CategoryRepository;
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
import com.firomsa.ecommerce.v1.mapper.UserMapper;
import com.yaphet.chapa.model.InitializeResponseData;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private PaymentService paymentService;

    private Role role;
    private User firstUser;
    private User secondUser;
    private Product testProduct;
    private Cart testCart;
    private Address testAddress;
    private Order testOrder;
    private Review testReview;
    private Category testCategory;
    private User inactiveUser;
    private ReviewRequestDTO reviewRequestDTO;
    private AddressRequestDTO addressRequestDTO;
    private UserRequestDTO userRequestDTO;
    private CartRequestDTO cartRequestDTO;

    @BeforeEach
    void setup() {
        role = Role.builder().name("USER").id(1).build();
        testCategory = Category.builder().name("Electronics").build();
        firstUser = User.builder()
                .username("firo")
                .id(UUID.randomUUID())
                .email("example@gmail.com")
                .firstName("Firomsa")
                .lastName("Assefa")
                .password("123")
                .role(role)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now()).build();
        secondUser = User.builder()
                .username("firo")
                .id(UUID.randomUUID())
                .email("example@gmail.com")
                .firstName("Firomsa")
                .lastName("Assefa")
                .password("123")
                .role(role)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now()).build();
        testProduct = Product.builder()
                .name("Test Product")
                .id(UUID.randomUUID())
                .description("A test product")
                .price(100.0).stock(10)
                .categories(new ArrayList<Category>(List.of(testCategory)))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        testCart = Cart.builder()
                .quantity(2)
                .id(1)
                .user(firstUser)
                .product(testProduct)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        testAddress = Address.builder()
                .firstName("Firomsa")
                .id(1)
                .lastName("Assefa")
                .street("123 Main St")
                .city("Addis Ababa")
                .state("Addis Ababa")
                .zipCode("1000")
                .country("Ethiopia")
                .phone("+251911234567")
                .active(true)
                .user(firstUser)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now()).build();
        testOrder = Order.builder()
                .user(firstUser)
                .id(1)
                .totalPrice(200.0)
                .txRef("TXN123456")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        testReview = Review.builder()
                .rating(5)
                .id(1)
                .comment("Great product!")
                .user(firstUser)
                .product(testProduct)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        inactiveUser = User.builder()
                .username("firo")
                .id(UUID.randomUUID())
                .email("example@gmail.com")
                .firstName("Firomsa")
                .lastName("Assefa")
                .password("123")
                .role(role)
                .active(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        reviewRequestDTO = ReviewRequestDTO.builder()
                .rating(5)
                .comment("Great product!")
                .build();
        addressRequestDTO = AddressRequestDTO.builder()
                .firstName("Firomsa")
                .lastName("Assefa")
                .street("123 Main St")
                .city("Addis Ababa")
                .state("Addis Ababa")
                .zipCode("1000")
                .country("Ethiopia")
                .phone("+251911234567")
                .active(true)
                .build();
        userRequestDTO = UserRequestDTO.builder()
                .username("firo")
                .email("example@gmail.com")
                .firstName("Firomsa")
                .lastName("Assefa")
                .password("123")
                .build();
        cartRequestDTO = CartRequestDTO.builder().quantity(2).build();
    }

    @InjectMocks
    private UserService userService;

    @Test
    public void UserService_GetAllUsers_ReturnAllUsers() {
        // Arrange
        List<User> users = List.of(firstUser, secondUser);
        given(userRepository.findAll()).willReturn(users);

        // Act
        List<UserResponseDTO> result = userService.getAll();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void UserService_GetAUser_ReturnAUser() {
        // Arrange
        given(userRepository.findById(firstUser.getId())).willReturn(Optional.of(firstUser));

        // Act
        UserResponseDTO responseDTO = userService.get(firstUser.getId());

        // Assert
        assertThat(responseDTO).isNotNull();
        assertThat(responseDTO.getId()).isEqualTo(firstUser.getId().toString());
        assertThat(responseDTO.getEmail()).isEqualTo(firstUser.getEmail());
        assertThat(responseDTO.getFirstName()).isEqualTo(firstUser.getFirstName());
        assertThat(responseDTO.getLastName()).isEqualTo(firstUser.getLastName());
        assertThat(responseDTO.getRole()).isEqualTo(firstUser.getRole().getName());
        assertThat(responseDTO.isActive()).isEqualTo(firstUser.isActive());
        assertThat(responseDTO.getUsername()).isEqualTo(firstUser.getUsername());
        verify(userRepository, times(1)).findById(firstUser.getId());
    }

    @Test
    public void UserService_GetAUser_ShouldThrowException_WhenUserDoesNotExist() {
        // Arrange
        given(userRepository.findById(firstUser.getId())).willReturn(Optional.empty());

        // Act and Assert
        assertThatThrownBy(() -> userService.get(firstUser.getId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User: " + firstUser.getId().toString());
    }

    @Test
    public void UserService_CreateUser_ReturnSavedUserDTO() {
        // Arrange
        UserResponseDTO userResponseDTO = UserMapper.toDTO(firstUser);

        given(userRepository.save(Mockito.any(User.class))).willReturn(firstUser);
        given(userRepository.existsByUsername(Mockito.anyString())).willReturn(false);
        given(userRepository.existsByEmail(Mockito.anyString())).willReturn(false);
        given(roleRepository.findByName("USER")).willReturn(Optional.of(role));

        // Act
        UserResponseDTO responseDTO = userService.create(userRequestDTO);

        // Assert
        assertThat(responseDTO).isNotNull();
        assertThat(responseDTO).usingRecursiveComparison().isEqualTo(userResponseDTO);

        verify(userRepository).save(any(User.class));
        verify(userRepository).existsByUsername(anyString());
        verify(userRepository).existsByEmail(anyString());
        verify(roleRepository).findByName("USER");

    }

    @Test
    public void UserService_CreateAdmin_ReturnSavedUserDTO() {
        // Arrange
        UserResponseDTO userResponseDTO = UserMapper.toDTO(firstUser);

        given(userRepository.save(Mockito.any(User.class))).willReturn(firstUser);
        given(userRepository.existsByUsername(Mockito.anyString())).willReturn(false);
        given(userRepository.existsByEmail(Mockito.anyString())).willReturn(false);
        given(roleRepository.findByName("ADMIN")).willReturn(Optional.of(Role.builder().name("ADMIN").id(2).build()));

        // Act
        UserResponseDTO responseDTO = userService.createAdmin(userRequestDTO);

        // Assert
        assertThat(responseDTO).isNotNull();
        assertThat(responseDTO).usingRecursiveComparison().isEqualTo(userResponseDTO);

        verify(userRepository).save(any(User.class));
        verify(userRepository).existsByUsername(anyString());
        verify(userRepository).existsByEmail(anyString());
        verify(roleRepository).findByName("ADMIN");
    }

    @Test
    public void UserService_CreateUser_ShouldThrowException_WhenEmailAlreadyExists() {
        // Arrange
        UserResponseDTO userResponseDTO = UserMapper.toDTO(firstUser);

        given(userRepository.existsByEmail(Mockito.anyString())).willReturn(true);

        // Act and Assert
        assertThatThrownBy(() -> userService.create(userRequestDTO))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessage(userResponseDTO.getEmail());

        verify(userRepository, never()).save(any(User.class));

    }

    @Test
    public void UserService_CreateUser_ShouldThrowException_WhenUserNameAlreadyExists() {
        // Arrange
        UserResponseDTO userResponseDTO = UserMapper.toDTO(firstUser);

        given(userRepository.existsByUsername(Mockito.anyString())).willReturn(true);

        // Act and Assert
        assertThatThrownBy(() -> userService.create(userRequestDTO))
                .isInstanceOf(UserNameAlreadyExistsException.class)
                .hasMessage(userResponseDTO.getUsername());

        verify(userRepository, never()).save(any(User.class));

    }

    @Test
    public void UserService_CreateUser_ShouldThrowException_WhenRoleDoesntExist() {
        // Arrange

        given(roleRepository.findByName(Mockito.anyString())).willReturn(Optional.empty());

        // Act and Assert
        assertThatThrownBy(() -> userService.create(userRequestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Role: USER");

        verify(userRepository, never()).save(any(User.class));

    }

    @Test
    public void UserService_UpdateUser_ReturnUpdatedUserDTO() {
        // Arrange
        UserResponseDTO userResponseDTO = UserMapper.toDTO(firstUser);

        given(userRepository.findById(firstUser.getId())).willReturn(Optional.of(firstUser));
        given(userRepository.save(Mockito.any(User.class))).willReturn(firstUser);
        given(userRepository.existsByUsernameAndIdNot(firstUser.getUsername(), firstUser.getId())).willReturn(false);
        given(userRepository.existsByEmailAndIdNot(firstUser.getEmail(), firstUser.getId())).willReturn(false);

        // Act
        UserResponseDTO responseDTO = userService.update(userRequestDTO, firstUser.getId());

        // Assert
        assertThat(responseDTO).isNotNull();
        assertThat(responseDTO).usingRecursiveComparison().isEqualTo(userResponseDTO);

        verify(userRepository, times(1)).findById(firstUser.getId());
        verify(userRepository, times(1)).save(Mockito.any(User.class));
        verify(userRepository, times(1)).existsByUsernameAndIdNot(firstUser.getUsername(), firstUser.getId());
        verify(userRepository, times(1)).existsByEmailAndIdNot(firstUser.getEmail(), firstUser.getId());
    }

    @Test
    public void UserService_UpdateUser_ShouldThrowException_WhenEmailAlreadyExists() {
        // Arrange
        UserResponseDTO userResponseDTO = UserMapper.toDTO(firstUser);

        given(userRepository.existsByEmailAndIdNot(firstUser.getEmail(), firstUser.getId())).willReturn(true);
        given(userRepository.findById(firstUser.getId())).willReturn(Optional.of(firstUser));

        // Act and Assert
        assertThatThrownBy(() -> userService.update(userRequestDTO, firstUser.getId()))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessage(userResponseDTO.getEmail());

        verify(userRepository, never()).save(any(User.class));

    }

    @Test
    public void UserService_UpdateUser_ShouldThrowException_WhenUserNameAlreadyExists() {
        // Arrange
        UserResponseDTO userResponseDTO = UserMapper.toDTO(firstUser);

        given(userRepository.existsByUsernameAndIdNot(firstUser.getUsername(), firstUser.getId())).willReturn(true);
        given(userRepository.findById(firstUser.getId())).willReturn(Optional.of(firstUser));

        // Act and Assert
        assertThatThrownBy(() -> userService.update(userRequestDTO, firstUser.getId()))
                .isInstanceOf(UserNameAlreadyExistsException.class)
                .hasMessage(userResponseDTO.getUsername());

        verify(userRepository, never()).save(any(User.class));

    }

    @Test
    public void UserService_RemoveUser_CallsDeleteById() {
        // Arrange
        given(userRepository.findById(firstUser.getId())).willReturn(Optional.of(firstUser));

        // Act
        userService.remove(firstUser.getId());

        // Assert
        verify(userRepository).findById(firstUser.getId());
        verify(userRepository).delete(firstUser);
    }

    @Test
    public void UserService_RemoveUser_ShouldThrowException_WhenUserDoesntExist() {
        // Arrange
        given(userRepository.findById(firstUser.getId())).willReturn(Optional.empty());

        // Act and Assert
        assertThatThrownBy(() -> userService.remove(firstUser.getId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User: " + firstUser.getId().toString());

        verify(userRepository, never()).delete(any(User.class));

    }

    @Test
    public void UserService_SoftRemoveUser_CallsSave() {
        // Arrange
        given(userRepository.findById(firstUser.getId())).willReturn(Optional.of(firstUser));

        // Act
        userService.softDelete(firstUser.getId());

        // Assert
        verify(userRepository).findById(firstUser.getId());
        verify(userRepository).save(firstUser);
    }

    @Test
    public void UserService_SoftRemoveUser_ShouldThrowException_WhenUserDoesntExist() {
        // Arrange
        given(userRepository.findById(firstUser.getId())).willReturn(Optional.empty());

        // Act and Assert
        assertThatThrownBy(() -> userService.remove(firstUser.getId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User: " + firstUser.getId().toString());

        verify(userRepository, never()).delete(any(User.class));

    }

    @Test
    public void UserService_GetAllInActiveUsers_ReturnInActiveUsers() {
        // Arrange
        List<User> users = List.of(firstUser, inactiveUser);
        given(userRepository.findAll()).willReturn(users);

        // Act
        List<UserResponseDTO> result = userService.getAllInActiveUsers();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).isActive()).isFalse();
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void UserService_GetCarts_ReturnUserCarts() {
        // Arrange
        firstUser.setCarts(List.of(testCart));
        given(userRepository.findById(firstUser.getId())).willReturn(Optional.of(firstUser));

        // Act
        List<CartResponseDTO> result = userService.getCarts(firstUser.getId());

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getQuantity()).isEqualTo(testCart.getQuantity());
        verify(userRepository, times(1)).findById(firstUser.getId());
    }

    @Test
    public void UserService_GetCarts_ShouldThrowException_WhenUserDoesNotExist() {
        // Arrange
        given(userRepository.findById(firstUser.getId())).willReturn(Optional.empty());

        // Act and Assert
        assertThatThrownBy(() -> userService.getCarts(firstUser.getId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User: " + firstUser.getId().toString());
    }

    @Test
    public void UserService_AddItemToCart_ReturnSavedCartDTO() {
        // Arrange
        given(userRepository.findById(firstUser.getId())).willReturn(Optional.of(firstUser));
        given(productRepository.findById(testProduct.getId())).willReturn(Optional.of(testProduct));
        given(cartRepository.findByUserAndProduct(firstUser, testProduct)).willReturn(Optional.empty());
        given(cartRepository.save(any(Cart.class))).willReturn(testCart);

        // Act
        CartResponseDTO result = userService.addItemToCart(firstUser.getId(), cartRequestDTO, testProduct.getId());

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getQuantity()).isEqualTo(testCart.getQuantity());
        verify(userRepository, times(1)).findById(firstUser.getId());
        verify(productRepository, times(1)).findById(testProduct.getId());
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    public void UserService_AddItemToCart_ShouldThrowException_WhenUserDoesNotExist() {
        // Arrange
        given(userRepository.findById(firstUser.getId())).willReturn(Optional.empty());

        // Act and Assert
        assertThatThrownBy(() -> userService.addItemToCart(firstUser.getId(), cartRequestDTO, testProduct.getId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User: " + firstUser.getId().toString());
    }

    @Test
    public void UserService_AddItemToCart_ShouldThrowException_WhenProductDoesNotExist() {
        // Arrange
        given(userRepository.findById(firstUser.getId())).willReturn(Optional.of(firstUser));
        given(productRepository.findById(testProduct.getId())).willReturn(Optional.empty());

        // Act and Assert
        assertThatThrownBy(() -> userService.addItemToCart(firstUser.getId(), cartRequestDTO, testProduct.getId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Product: " + testProduct.getId().toString());
    }

    @Test
    public void UserService_AddItemToCart_ShouldThrowException_WhenStockLimited() {
        // Arrange
        CartRequestDTO highQuantityRequest = CartRequestDTO.builder().quantity(15).build();
        given(userRepository.findById(firstUser.getId())).willReturn(Optional.of(firstUser));
        given(productRepository.findById(testProduct.getId())).willReturn(Optional.of(testProduct));

        // Act and Assert
        assertThatThrownBy(() -> userService.addItemToCart(firstUser.getId(), highQuantityRequest, testProduct.getId()))
                .isInstanceOf(LimitedProductStockException.class)
                .hasMessage("Product Stock Limited");
    }

    @Test
    public void UserService_GetOrders_ReturnUserOrders() {
        // Arrange
        firstUser.setOrders(List.of(testOrder));
        given(userRepository.findById(firstUser.getId())).willReturn(Optional.of(firstUser));

        // Act
        List<OrderResponseDTO> result = userService.getOrders(firstUser.getId());

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTotalPrice()).isEqualTo(testOrder.getTotalPrice());
        verify(userRepository, times(1)).findById(firstUser.getId());
    }

    @Test
    public void UserService_GetOrders_ShouldThrowException_WhenUserDoesNotExist() {
        // Arrange
        given(userRepository.findById(firstUser.getId())).willReturn(Optional.empty());

        // Act and Assert
        assertThatThrownBy(() -> userService.getOrders(firstUser.getId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User: " + firstUser.getId().toString());
    }

    @Test
    public void UserService_AddOrder_ReturnOrderDetailDTO() {
        // Arrange
        firstUser.setCarts(List.of(testCart));
        given(userRepository.findById(firstUser.getId())).willReturn(Optional.of(firstUser));
        given(cartRepository.findAllByUser(firstUser)).willReturn(List.of(testCart));
        given(addressRepository.findByUserAndActive(firstUser, true)).willReturn(Optional.of(testAddress));
        given(orderRepository.save(any(Order.class))).willReturn(testOrder);
        given(orderItemRepository.saveAll(any())).willReturn(List.of());
        given(paymentService.startTransaction(any(Order.class))).willReturn(new InitializeResponseData());

        // Act
        OrderDetailDTO result = userService.addOrder(firstUser.getId());

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getOrder()).isNotNull();
        assertThat(result.getAddress()).isNotNull();
        verify(userRepository, times(1)).findById(firstUser.getId());
        verify(addressRepository, times(1)).findByUserAndActive(firstUser, true);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    public void UserService_AddOrder_ShouldThrowException_WhenUserDoesNotExist() {
        // Arrange
        given(userRepository.findById(firstUser.getId())).willReturn(Optional.empty());

        // Act and Assert
        assertThatThrownBy(() -> userService.addOrder(firstUser.getId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User: " + firstUser.getId().toString());
    }

    @Test
    public void UserService_AddOrder_ShouldThrowException_WhenNoAddress() {
        // Arrange
        firstUser.setCarts(List.of(testCart));
        given(userRepository.findById(firstUser.getId())).willReturn(Optional.of(firstUser));
        given(addressRepository.findByUserAndActive(firstUser, true)).willReturn(Optional.empty());

        // Act and Assert
        assertThatThrownBy(() -> userService.addOrder(firstUser.getId()))
                .isInstanceOf(OrderProcessException.class)
                .hasMessage("User should have address to create an order");
    }

    @Test
    public void UserService_AddOrder_ShouldThrowException_WhenCartEmpty() {
        // Arrange
        firstUser.setCarts(List.of());
        given(userRepository.findById(firstUser.getId())).willReturn(Optional.of(firstUser));
        given(addressRepository.findByUserAndActive(firstUser, true)).willReturn(Optional.of(testAddress));

        // Act and Assert
        assertThatThrownBy(() -> userService.addOrder(firstUser.getId()))
                .isInstanceOf(OrderProcessException.class)
                .hasMessage("The cart doesn't contain any items");
    }

    @Test
    public void UserService_AddOrder_ShouldThrowException_WhenProductStockLimited() {
        // Arrange
        Product limitedStockProduct = Product.builder()
                .id(UUID.randomUUID())
                .name("Limited Stock Product")
                .price(100.0)
                .stock(1)
                .build();

        Cart limitedStockCart = Cart.builder()
                .id(2)
                .quantity(2)
                .user(firstUser)
                .product(limitedStockProduct)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        firstUser.setCarts(List.of(limitedStockCart));
        given(userRepository.findById(firstUser.getId())).willReturn(Optional.of(firstUser));
        given(cartRepository.findAllByUser(firstUser)).willReturn(List.of(limitedStockCart));
        given(addressRepository.findByUserAndActive(firstUser, true)).willReturn(Optional.of(testAddress));

        // Act and Assert
        assertThatThrownBy(() -> userService.addOrder(firstUser.getId()))
                .isInstanceOf(LimitedProductStockException.class)
                .hasMessage("Product Stock Limited");
    }

    @Test
    public void UserService_GetAddresses_ReturnUserAddresses() {
        // Arrange
        firstUser.setAddresses(List.of(testAddress));
        given(userRepository.findById(firstUser.getId())).willReturn(Optional.of(firstUser));

        // Act
        List<AddressResponseDTO> result = userService.getAddresses(firstUser.getId());

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStreet()).isEqualTo(testAddress.getStreet());
        verify(userRepository, times(1)).findById(firstUser.getId());
    }

    @Test
    public void UserService_GetAddresses_ShouldThrowException_WhenUserDoesNotExist() {
        // Arrange
        given(userRepository.findById(firstUser.getId())).willReturn(Optional.empty());

        // Act and Assert
        assertThatThrownBy(() -> userService.getAddresses(firstUser.getId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User: " + firstUser.getId().toString());
    }

    @Test
    public void UserService_AddAddressToAddresses_ReturnSavedAddressDTO() {
        // Arrange
        given(userRepository.findById(firstUser.getId())).willReturn(Optional.of(firstUser));
        given(addressRepository.findByUserAndActive(firstUser, true)).willReturn(Optional.empty());
        given(addressRepository.save(any(Address.class))).willReturn(testAddress);

        // Act
        AddressResponseDTO result = userService.addAddressToAddresses(firstUser.getId(), addressRequestDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getStreet()).isEqualTo(testAddress.getStreet());
        verify(userRepository, times(1)).findById(firstUser.getId());
        verify(addressRepository, times(1)).save(any(Address.class));
    }

    @Test
    public void UserService_AddAddressToAddresses_ShouldThrowException_WhenUserDoesNotExist() {
        // Arrange
        given(userRepository.findById(firstUser.getId())).willReturn(Optional.empty());

        // Act and Assert
        assertThatThrownBy(() -> userService.addAddressToAddresses(firstUser.getId(), addressRequestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User: " + firstUser.getId().toString());
    }

    @Test
    public void UserService_RemoveAddress_CallsDeleteById() {
        // Arrange
        given(userRepository.findById(firstUser.getId())).willReturn(Optional.of(firstUser));
        given(addressRepository.findById(testAddress.getId().intValue())).willReturn(Optional.of(testAddress));

        // Act
        userService.removeAddress(testAddress.getId().intValue(), firstUser.getId());

        // Assert
        verify(userRepository).findById(firstUser.getId());
        verify(addressRepository).findById(testAddress.getId().intValue());
        verify(addressRepository).delete(testAddress);
    }

    @Test
    public void UserService_RemoveAddress_ShouldThrowException_WhenUserDoesNotExist() {
        // Arrange
        given(userRepository.findById(firstUser.getId())).willReturn(Optional.empty());

        // Act and Assert
        assertThatThrownBy(() -> userService.removeAddress(testAddress.getId().intValue(), firstUser.getId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User: " + firstUser.getId().toString());
    }

    @Test
    public void UserService_RemoveAddress_ShouldThrowException_WhenAddressDoesNotExist() {
        // Arrange
        given(userRepository.findById(firstUser.getId())).willReturn(Optional.of(firstUser));
        given(addressRepository.findById(testAddress.getId())).willReturn(Optional.empty());

        // Act and Assert
        assertThatThrownBy(() -> userService.removeAddress(testAddress.getId().intValue(), firstUser.getId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Address: " + testAddress.getId());
    }

    @Test
    public void UserService_UpdateAddress_ReturnUpdatedAddressDTO() {
        // Arrange
        given(userRepository.findById(firstUser.getId())).willReturn(Optional.of(firstUser));
        given(addressRepository.findById(testAddress.getId().intValue())).willReturn(Optional.of(testAddress));
        given(addressRepository.findByUserAndActive(firstUser, true)).willReturn(Optional.empty());
        given(addressRepository.save(any(Address.class))).willReturn(testAddress);

        // Act
        AddressResponseDTO result = userService.updateAddress(addressRequestDTO, testAddress.getId(),
                firstUser.getId());

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getStreet()).isEqualTo(testAddress.getStreet());
        verify(userRepository, times(1)).findById(firstUser.getId());
        verify(addressRepository, times(1)).findById(testAddress.getId());
        verify(addressRepository, times(1)).save(any(Address.class));
    }

    @Test
    public void UserService_UpdateAddress_ShouldThrowException_WhenUserDoesNotExist() {
        // Arrange
        given(userRepository.findById(firstUser.getId())).willReturn(Optional.empty());

        // Act and Assert
        assertThatThrownBy(() -> userService.updateAddress(addressRequestDTO, testAddress.getId(), firstUser.getId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User: " + firstUser.getId().toString());
    }

    @Test
    public void UserService_UpdateAddress_ShouldThrowException_WhenAddressDoesNotExist() {
        // Arrange
        given(userRepository.findById(firstUser.getId())).willReturn(Optional.of(firstUser));
        given(addressRepository.findById(testAddress.getId())).willReturn(Optional.empty());

        // Act and Assert
        assertThatThrownBy(() -> userService.updateAddress(addressRequestDTO, testAddress.getId(), firstUser.getId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Address: " + testAddress.getId());
    }

    @Test
    public void UserService_GetReviews_ReturnUserReviews() {
        // Arrange
        firstUser.setReviews(List.of(testReview));
        given(userRepository.findById(firstUser.getId())).willReturn(Optional.of(firstUser));

        // Act
        List<ReviewResponseDTO> result = userService.getReviews(firstUser.getId());

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRating()).isEqualTo(testReview.getRating());
        verify(userRepository, times(1)).findById(firstUser.getId());
    }

    @Test
    public void UserService_GetReviews_ShouldThrowException_WhenUserDoesNotExist() {
        // Arrange
        given(userRepository.findById(firstUser.getId())).willReturn(Optional.empty());

        // Act and Assert
        assertThatThrownBy(() -> userService.getReviews(firstUser.getId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User: " + firstUser.getId().toString());
    }

    @Test
    public void UserService_AddReviewToReviews_ReturnSavedReviewDTO() {
        // Arrange
        given(userRepository.findById(firstUser.getId())).willReturn(Optional.of(firstUser));
        given(productRepository.findById(testProduct.getId())).willReturn(Optional.of(testProduct));
        given(reviewRepository.save(any(Review.class))).willReturn(testReview);

        // Act
        ReviewResponseDTO result = userService.addReviewToReviews(firstUser.getId(), reviewRequestDTO,
                testProduct.getId());

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getRating()).isEqualTo(testReview.getRating());
        verify(userRepository, times(1)).findById(firstUser.getId());
        verify(productRepository, times(1)).findById(testProduct.getId());
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    public void UserService_AddReviewToReviews_ShouldThrowException_WhenUserDoesNotExist() {
        // Arrange
        given(userRepository.findById(firstUser.getId())).willReturn(Optional.empty());

        // Act and Assert
        assertThatThrownBy(
                () -> userService.addReviewToReviews(firstUser.getId(), reviewRequestDTO, testProduct.getId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User: " + firstUser.getId().toString());
    }

    @Test
    public void UserService_AddReviewToReviews_ShouldThrowException_WhenProductDoesNotExist() {
        // Arrange
        given(userRepository.findById(firstUser.getId())).willReturn(Optional.of(firstUser));
        given(productRepository.findById(testProduct.getId())).willReturn(Optional.empty());

        // Act and Assert
        assertThatThrownBy(
                () -> userService.addReviewToReviews(firstUser.getId(), reviewRequestDTO, testProduct.getId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Product: " + testProduct.getId().toString());
    }

    @Test
    public void UserService_LoadUserByUsername_ReturnUserDetails() {
        // Arrange
        given(userRepository.findByUsername(firstUser.getUsername())).willReturn(Optional.of(firstUser));

        // Act
        UserDetails result = userService.loadUserByUsername(firstUser.getUsername());

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo(firstUser.getUsername());
        verify(userRepository, times(1)).findByUsername(firstUser.getUsername());
    }

    @Test
    public void UserService_LoadUserByUsername_ShouldThrowException_WhenUserDoesNotExist() {
        // Arrange
        given(userRepository.findByUsername(firstUser.getUsername())).willReturn(Optional.empty());

        // Act and Assert
        assertThatThrownBy(() -> userService.loadUserByUsername(firstUser.getUsername()))
                .isInstanceOf(org.springframework.security.core.userdetails.UsernameNotFoundException.class)
                .hasMessage("USER: " + firstUser.getUsername() + " Not found");
    }

    @Test
    public void UserService_CreateAdmin_ShouldThrowException_WhenEmailAlreadyExists() {
        // Arrange
        UserResponseDTO userResponseDTO = UserMapper.toDTO(firstUser);

        given(userRepository.existsByEmail(Mockito.anyString())).willReturn(true);

        // Act and Assert
        assertThatThrownBy(() -> userService.createAdmin(userRequestDTO))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessage(userResponseDTO.getEmail());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void UserService_CreateAdmin_ShouldThrowException_WhenUserNameAlreadyExists() {
        // Arrange
        UserResponseDTO userResponseDTO = UserMapper.toDTO(firstUser);

        given(userRepository.existsByUsername(Mockito.anyString())).willReturn(true);

        // Act and Assert
        assertThatThrownBy(() -> userService.createAdmin(userRequestDTO))
                .isInstanceOf(UserNameAlreadyExistsException.class)
                .hasMessage(userResponseDTO.getUsername());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void UserService_CreateAdmin_ShouldThrowException_WhenRoleDoesntExist() {
        // Arrange
        given(roleRepository.findByName(Mockito.anyString())).willReturn(Optional.empty());

        // Act and Assert
        assertThatThrownBy(() -> userService.createAdmin(userRequestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Role: ADMIN");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void UserService_AddItemToCart_ReturnUpdatedCartDTO_WhenCartItemExists() {
        // Arrange
        given(userRepository.findById(firstUser.getId())).willReturn(Optional.of(firstUser));
        given(productRepository.findById(testProduct.getId())).willReturn(Optional.of(testProduct));
        given(cartRepository.findByUserAndProduct(firstUser, testProduct)).willReturn(Optional.of(testCart));
        given(cartRepository.save(any(Cart.class))).willReturn(testCart);

        // Act
        CartResponseDTO result = userService.addItemToCart(firstUser.getId(), cartRequestDTO, testProduct.getId());

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getQuantity()).isEqualTo(testCart.getQuantity());
        verify(userRepository, times(1)).findById(firstUser.getId());
        verify(productRepository, times(1)).findById(testProduct.getId());
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    public void UserService_AddAddressToAddresses_ReturnSavedAddressDTO_WhenSettingAsDefault() {
        // Arrange
        Address existingDefaultAddress = Address.builder()
                .id(2)
                .firstName("Existing")
                .lastName("Default")
                .street("456 Old St")
                .city("Old City")
                .state("Old State")
                .zipCode("2000")
                .country("Old Country")
                .phone("+251911234568")
                .active(true)
                .user(firstUser)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        given(userRepository.findById(firstUser.getId())).willReturn(Optional.of(firstUser));
        given(addressRepository.findByUserAndActive(firstUser, true)).willReturn(Optional.of(existingDefaultAddress));
        given(addressRepository.save(any(Address.class))).willReturn(testAddress);

        // Act
        AddressResponseDTO result = userService.addAddressToAddresses(firstUser.getId(), addressRequestDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getStreet()).isEqualTo(testAddress.getStreet());
        verify(userRepository, times(1)).findById(firstUser.getId());
        verify(addressRepository, times(2)).save(any(Address.class)); // Once for deactivating existing, once for saving
                                                                      // new
    }

    @Test
    public void UserService_UpdateAddress_ReturnUpdatedAddressDTO_WhenSettingAsDefault() {
        // Arrange
        Address existingDefaultAddress = Address.builder()
                .id(3)
                .firstName("Existing")
                .lastName("Default")
                .street("789 Another St")
                .city("Another City")
                .state("Another State")
                .zipCode("3000")
                .country("Another Country")
                .phone("+251911234569")
                .active(true)
                .user(firstUser)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        given(userRepository.findById(firstUser.getId())).willReturn(Optional.of(firstUser));
        given(addressRepository.findById(testAddress.getId().intValue())).willReturn(Optional.of(testAddress));
        given(addressRepository.findByUserAndActive(firstUser, true)).willReturn(Optional.of(existingDefaultAddress));
        given(addressRepository.save(any(Address.class))).willReturn(testAddress);

        // Act
        AddressResponseDTO result = userService.updateAddress(addressRequestDTO, testAddress.getId(),
                firstUser.getId());

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getStreet()).isEqualTo(testAddress.getStreet());
        verify(userRepository, times(1)).findById(firstUser.getId());
        verify(addressRepository, times(1)).findById(testAddress.getId());
        verify(addressRepository, times(2)).save(any(Address.class)); // Once for deactivating existing, once for saving
                                                                      // updated
    }
}
