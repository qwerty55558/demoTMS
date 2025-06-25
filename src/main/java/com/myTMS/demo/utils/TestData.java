package com.myTMS.demo.utils;

import com.myTMS.demo.constant.RedisConst;
import com.myTMS.demo.dao.*;
import com.myTMS.demo.dao.delivery.Delivery;
import com.myTMS.demo.dao.typeconst.DeliveryStatus;
import com.myTMS.demo.dao.typeconst.MessageType;
import com.myTMS.demo.dao.typeconst.PaymentType;
import com.myTMS.demo.dao.users.Employee;
import com.myTMS.demo.dao.users.Users;
import com.myTMS.demo.dto.EmployeeProfileDTO;
import com.myTMS.demo.dto.user.UserCheckoutDTO;
import com.myTMS.demo.dto.user.UserSignUpDTO;
import com.myTMS.demo.repository.interfaces.JPADepartmentRepository;
import com.myTMS.demo.service.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.random.RandomGenerator;


@Component
@Slf4j
@RequiredArgsConstructor
public class TestData {

    private final UserService userService;
    private final CategoryService categoryService;
    private final ItemService itemService;
    private final CenterService centerService;
    private final PostService postService;
    private final DepartmentService departmentService;
    private final RedisService redisService;
    private final DeliveryService deliveryService;
    private final AlarmService alarmService;
    private final OrderService orderService;


    @PostConstruct
    public void init() throws Exception {
        departmentService.createDepartment("CustomerService");
        departmentService.createDepartment("Sales");
        departmentService.createDepartment("Logistics");
        departmentService.createDepartment("Marketing");
        departmentService.createDepartment("Finance");
        departmentService.createDepartment("IT");

        ArrayList<UserSignUpDTO> userSignUpDTOS = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            UserSignUpDTO signDTO = new UserSignUpDTO();
            signDTO.setEmail("test" + i + "@test.com");
            signDTO.setPw("testtest1!");
            userSignUpDTOS.add(signDTO);
        }

        List<String> departmentNames = departmentService.findDepartmentNames();

        for (UserSignUpDTO signUpDTO : userSignUpDTOS) {
            Optional<Department> customerService = departmentService.findDepartmentByName("CustomerService");
            customerService.ifPresent(department -> {
                userService.signUpEmployee(signUpDTO,department);
            });

            userService.findByEmail(signUpDTO.getEmail()).ifPresent(user -> {
                try {
                    Object unproxied = Hibernate.unproxy(user);
                    if (unproxied instanceof Employee empUser) {
                        Employee builtUser = (Employee) empUser.updateProfile(empUser.toBuilder()
                                .firstName(RandomAuthCodeGenerator.generate())
                                .lastName(RandomAuthCodeGenerator.generate()));
                        userService.setDepartment(builtUser,
                                departmentNames.get(new Random().nextInt(departmentNames.size())));
                    }
                } catch (Exception e) {
                    log.error("Error setting department for user", e);
                }
            });
        }


        UserSignUpDTO userSignUpDTO = new UserSignUpDTO();
        userSignUpDTO.setEmail("test10@test.com");
        userSignUpDTO.setPw("testtest1!");
        userService.signUpUser(userSignUpDTO);
        UserSignUpDTO userSignUpDTO2 = new UserSignUpDTO();
        userSignUpDTO2.setEmail("test11@test.com");
        userSignUpDTO2.setPw("testtest1!");
        userService.signUpUser(userSignUpDTO2);


        categoryService.createCategory("sundries");
        categoryService.createCategory("clothes");
        categoryService.createCategory("groceries");

        categoryService.setChild("sundries", "toiletries");
        categoryService.setChild("sundries", "stationery");
        categoryService.setChild("stationery", "sgrandchild");
        categoryService.setChild("toiletries", "tgrandchild");
        categoryService.setChild("clothes", "outerwear");
        categoryService.setChild("clothes", "underwear");
        categoryService.setChild("underwear", "ugrandchild");
        categoryService.setChild("outerwear", "ograndchild");
        categoryService.setChild("groceries", "fruits");
        categoryService.setChild("groceries", "vegetables");
        categoryService.setChild("vegetables", "vgrandchild");
        categoryService.setChild("fruits", "fgrandchild");

        categoryService.createCategory("items");

        categoryService.cacheAllCategories();

        itemService.createItem("test1", "test1", 1000);
        itemService.setCategory("test1", "stationery");
        itemService.setImg("test1", "/asset/Truck.jpg");
        itemService.createItem("test2", "test2", 2000);
        itemService.setCategory("test2", "sundries");
        itemService.setImg("test2", "/asset/Ship.jpg");
        itemService.createItem("test3", "test3", 3000);
        itemService.setCategory("test3", "sgrandchild");
        itemService.setImg("test3", "/asset/Airplane.webp");
        itemService.createItem("test4", "test4", 4000);
        itemService.setCategory("test4", "toiletries");
//        itemService.setImg("test4", "/asset/Truck.jpg");

        itemService.cacheAllItems();

        centerService.createCenter("Seoul", 126.97777990999413, 37.5694549491167, "Seoul center");
        centerService.createCenter("Incheon", 126.70360324551125, 37.462920077224126, "Incheon center");
        centerService.createCenter("Sejong", 127.29166195728632, 36.48020129107986, "Sejong center");
        centerService.createCenter("Daegu", 128.64467916794712, 35.882737240242726, "Daegu center");
        centerService.createCenter("Gwangju", 126.84214003294292, 35.19773959257022, "Gwangju center");

        postService.savePost(new Post("Test Title 1", "Writer 1", "Responser 1", "Test Content 1", "Test Answer 1", false, 2L));
        postService.savePost(new Post("Test Title 2", "Writer 1", "Responser 1", "Test Content 1", "Test Answer 1", true, 2L));
        postService.savePost(new Post("Test Title 3", "Writer 1", "Responser 1", "Test Content 1", "Test Answer 1", false, 1L));
        postService.savePost(new Post("Test Title 4", "Writer 1", "Responser 1", "Test Content 1", "Test Answer 1", true, 1L));
        postService.savePost(new Post("Test Title 5", "Writer 1", "Responser 1", "Test Content 1", "Test Answer 1", false, 1L));
        postService.savePost(new Post("Test Title 6", "Writer 1", "Responser 1", "Test Content 1", "Test Answer 1", false, 1L));
        postService.savePost(new Post("Test Title 7", "Writer 1", "Responser 1", "Test Content 1", "Test Answer 1", false, 1L));
        postService.savePost(new Post("Test Title 8", "Writer 1", "Responser 1", "Test Content 1", "Test Answer 1", false, 1L));
        postService.savePost(new Post("Test Title 9", "Writer 1", "Responser 1", "Test Content 1", "Test Answer 1", false, 1L));
        postService.savePost(new Post("Test Title 10", "Writer 1", "Responser 1", "Test Content 1", "Test Answer 1", false, 1L));


        Map<String, List<EmployeeProfileDTO>> stringListMap = departmentService.cacheDepartment();
        redisService.setObjectData(RedisConst.DEPARTMENT_CACHE_KEY.name(), stringListMap);

//        for (int i = 0; i < 20; i++) {
//            Cart cart = new Cart();
//            cart.setUserId(21L);
//            cart.plusItem(1L);
//        }

//        for (int i = 0; i < 1000; i++) {
//            alarmService.sendAlarmToAll("alarm.order.emergency", true, MessageType.emMessage);
//            alarmService.sendAlarm(21L,"alarm.order.emergency", false, MessageType.alarmMessage);
//        }
    }
}
