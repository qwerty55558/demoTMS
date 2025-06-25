## 🎯목표
이번 프로젝트는 물류 센터에서의 작업을 전산화할 수 있는 웹 사이트를 구현해보았다.
TMS 는 Transport Management System 준말로 (이하 TMS) 실제 물류 작업이 일어나는 현실 세계의 정보를
객체화 시켜 관리할 수 있도록 만드는 것을 의미한다. 지금까지의 프로젝트 구현과는 다르게
먼저 TMS 라고 할만 한 프로그램이라면 어떤 기능을 해야할까를 생각해보고 엔티티 구현부터
DB 에 작성되는 내용, MVC 에 충실한 프로그래밍, API 통신, 보안 등등 여러가지 Know-How 를 총합한
상대적으로 대규모인 프로젝트이다.

## ⚙️Tech Stack
### Backend
* **Java 23 (Temurin)**
* **SpringBoot 3.4.2**
* **Spring Security 6** - Authentication
* **Spring Websocket** - RealTime Chatting
* **Spring Mail** - Validation to Email
* **Spring Validation** - Validate & CustomValidate
* **PostgreSQL** - DataBase
* **Redis** - Caching
* **JPA** - DataBinding (Entity Manager & JPARepository<> 2 Way)

### Frontend
* **ThymeLeaf** - TemplateEngine
* **Javascript** - Script
* **Jquery** - ScriptLibrary
### VCS
* **Git**
### Server
* **Raspberry Pi OS (Raspberry Pi 5)**
* **Ubuntu 22.0.4 (OracleCloudInstance)**
* **Nginx** - ProxyPass

## ✅주요 기능
- 유저
  - 인증 후 가입
  - 로그인
  - 프로필 수정
  - 주문 관리
  - 배송 추적
  - 배송 대행
  - 사업자 계정, 개인 계정 구별
  - 카테고리별 아이템 확인
  - 고객센터 문의
  - 실시간 고객 문의
  - 장바구니 관리
  - 알람
- 관리자
  - 대시보드를 통한 통합적인 관리 (유저 관리, 주문 관리 등등)
  - 부서 인원 확인
  - 긴급 처리건 확인
  - 직원 유저 등록
- 직원
  - 실시간 고객 응대
  - 부서별 연락처 확인
  - 긴급 처리건 확인
  - 고객 문의 답변
  - 알람
  - 프로필 수정

## 🗄️ERD 구조

![](https://velog.velcdn.com/images/qwerty55558/post/45a5e9b2-21a8-4e8c-9fcc-62c0fe1c8cb8/image.png)


> [ERD Cloud 링크](https://www.erdcloud.com/d/8qdoQsJNvWmfRT8tf)


## 🫙엔티티
- User
  - id
  - name
  - password
  - address
  - userType
    - 사업자(Business)
      - BRN(Business Registration Number)
    - 배송 대행(Package Forwarding Service, PFS)
      - PCC(Personal Clearance Code)
    - 직원(Employee)
      - 부서(Department)
    - 관리자(Admin)
    - 일반(General)
  - orders(object)
  - cart(object)
  - created_at
  - modified_at
- Order
  - id
  - user(object)
  - items(object)
  - totalPrice
  - expire_time
  - created_at
- Item
  - id
  - description
  - price
  - created_at
  - modified_at
- OrderItem
  - id
  - item(object)
  - order(object)
  - orderPrice
  - amount
  - created_at
- Category
  - id
  - name
  - depth
  - parent (Category Object)
  - children (Category Object)
- CategoryItem
  - id
  - item(object)
  - category(object)
- Delivery
  - id
  - order(object)
  - distance
  - destination
  - departureId
  - deliveryType
    - 일반 주문건
    - 배달 대행건
  - price
  - status
  - expected_at
  - created_at
- Post
  - id
  - content
  - media
  - created_at
  - modified_at
- Alarm
  - id
  - userId
  - type
  - content
  - created_at
- Department
  - id
  - name
  - employees(object)
  - created_at
- Message
  - id
  - sender(object)
  - receiver(object)
  - content
  - created_at
- Cart
  - id
  - user(Object)
  - cartItem
  - price
- Center
  - id
  - name
  - location
  - description
- Payment
  - userid
  - type
  - number
  - CVV
  - excepted_at
  - created_at



## 🔚엔드포인트 명세
<table>
    <tr>
        <td><b>Domain</b></td>
        <td><b>URL</b></td>
        <td><b>Method</b></td>
        <td><b>Description</b></td>
    </tr>
    <tr>
        <td>OrderController</td>
        <td>/checkout</td>
        <td>GET</td>
        <td>장바구니 리스팅 + 체크아웃 페이지</td>
    </tr>
    <tr>
        <td></td>
        <td>/order</td>
        <td>GET</td>
        <td>주문 리스트 출력</td>
    </tr>
    <tr>
        <td></td>
        <td>/order</td>
        <td>POST</td>
        <td>주문 생성</td>
    </tr>
    <tr>
        <td></td>
        <td>/order/overseas</td>
        <td>GET</td>
        <td>해외 직구 체크아웃 페이지</td>
    </tr>
    <tr>
        <td></td>
        <td>/order/overseas</td>
        <td>POST</td>
        <td>해외 직구 주문 생성</td>
    </tr>
    <tr>
        <td>OrderRestController</td>
        <td>/api/employee/order/detail</td>
        <td>GET</td>
        <td>리스팅을 위한 API 호출</td>
    </tr>
    <tr>
        <td></td>
        <td>/api/order/detail</td>
        <td>GET</td>
        <td>리스팅을 위한 API 호출</td>
    </tr>
    <tr>
        <td>CartController</td>
        <td>/cart</td>
        <td>GET</td>
        <td>장바구니 목록 리스팅</td>
    </tr>
    <tr>
        <td>CartRestController</td>
        <td>/api/cart/additem</td>
        <td>POST</td>
        <td>장바구니 아이템 추가 API</td>
    </tr>
    <tr>
        <td></td>
        <td>/api/cart/clear</td>
        <td>GET</td>
        <td>장바구니 초기화 API</td>
    </tr>
    <tr>
        <td></td>
        <td>/api/cart/create</td>
        <td>GET</td>
        <td>장바구니 생성 API</td>
    </tr>
    <tr>
        <td></td>
        <td>/api/cart/fetch</td>
        <td>POST</td>
        <td>장바구니 갱신 API</td>
    </tr>
    <tr>
        <td></td>
        <td>/api/cart/list</td>
        <td>GET</td>
        <td>장바구니 리스팅 API</td>
    </tr>
    <tr>
        <td></td>
        <td>/api/cart/minusItem</td>
        <td>POST</td>
        <td>장바구니 아이템 감소/삭제 API</td>
    </tr>
    <tr>
        <td>CustomErrorController</td>
        <td>/error</td>
        <td>GET</td>
        <td>세션에 저장된 에러를 alert 시켜주는 통합 에러 처리 컨트롤러</td>
    </tr>
    <tr>
        <td>LoginController</td>
        <td>/findpw</td>
        <td>POST</td>
        <td>사용자 비밀번호 찾기 요청 처리</td>
    </tr>
    <tr>
        <td></td>
        <td>/logout</td>
        <td>POST</td>
        <td>폼 로그아웃이 아닌 POST 방식 로그아웃</td>
    </tr>
    <tr>
        <td></td>
        <td>/signin</td>
        <td>POST</td>
        <td>커스텀 로그인 로직을 사용한 로그인 처리</td>
    </tr>
    <tr>
        <td></td>
        <td>/signup</td>
        <td>POST</td>
        <td>사용자 가입 요청에 사용</td>
    </tr>
    <tr>
        <td></td>
        <td>/valid/home</td>
        <td>GET</td>
        <td>인증 유저 홈 화면</td>
    </tr>
    <tr>
        <td>HomeController</td>
        <td>/, /home</td>
        <td>GET</td>
        <td>유저 타입에 따른 홈 화면 Redirect</td>
    </tr>
    <tr>
        <td>ProfileController</td>
        <td>/profile</td>
        <td>GET</td>
        <td>프로필 페이지 요청</td>
    </tr>
    <tr>
        <td></td>
        <td>/profile/edit</td>
        <td>GET</td>
        <td>프로필 수정 페이지 요청</td>
    </tr>
    <tr>
        <td></td>
        <td>/profile/edit</td>
        <td>POST</td>
        <td>프로필 수정 form 데이터 전송</td>
    </tr>
    <tr>
        <td>CustomerServiceController</td>
        <td>/cs/post</td>
        <td>GET</td>
        <td>CS 페이지 요청</td>
    </tr>
    <tr>
        <td></td>
        <td>/cs/post/delete</td>
        <td>GET</td>
        <td>권한 확인 후 포스트 삭제</td>
    </tr>
    <tr>
        <td></td>
        <td>/cs/post/edit</td>
        <td>GET</td>
        <td>포스트 수정 페이지 요청</td>
    </tr>
    <tr>
        <td></td>
        <td>/cs/post/edit</td>
        <td>POST</td>
        <td>포스트 수정 데이터 폼 전송</td>
    </tr>
    <tr>
        <td></td>
        <td>/cs/post/response/edit</td>
        <td>POST</td>
        <td>CS 답변 폼 전송</td>
    </tr>
    <tr>
        <td></td>
        <td>/cs/post/response/{id}</td>
        <td>GET</td>
        <td>해당 id 의 포스트에 답변 작성 가능한 페이지 요청</td>
    </tr>
    <tr>
        <td></td>
        <td>/cs/post/{id}</td>
        <td>GET</td>
        <td>해당 페이지의 포스트를 요청</td>
    </tr>
    <tr>
        <td>CustomerServiceRestController</td>
        <td>/api/cs/disconnect</td>
        <td>GET</td>
        <td>고객 센터 Queue 에서 drop + chatroom 삭제 요청</td>
    </tr>
    <tr>
        <td>InformationController</td>
        <td>/about/pfs</td>
        <td>GET</td>
        <td>해외 배송 Info 페이지 요청</td>
    </tr>
    <tr>
        <td>EmployeeController</td>
        <td>/valid/employee/department</td>
        <td>GET</td>
        <td>조직도 페이지 요청</td>
    </tr>
    <tr>
        <td></td>
        <td>/valid/employee/emergency/order</td>
        <td>GET</td>
        <td>긴급 배송건 페이지 요청</td>
    </tr>
    <tr>
        <td></td>
        <td>/valid/employee/home</td>
        <td>GET</td>
        <td>직원 전용 홈페이지 요청</td>
    </tr>
    <tr>
        <td></td>
        <td>/valid/employee/manage/order</td>
        <td>GET</td>
        <td>해당 오더의 상세 페이지를 로딩</td>
    </tr>
    <tr>
        <td></td>
        <td>/valid/employee/manage/order/list</td>
        <td>GET</td>
        <td>리스팅 + 페이징 + 검색 기능이 들어간 주문 리스트 페이지 요청</td>
    </tr>
    <tr>
        <td></td>
        <td>/valid/employee/manage/order/list</td>
        <td>POST</td>
        <td>파라미터로 요청 받은 데이터를 넘겨주는 엔드포인트</td>
    </tr>
    <tr>
        <td>EmployeeRestController</td>
        <td>/api/employee/department</td>
        <td>GET</td>
        <td>모델에 보내는 방식이 아니라 rest 요청으로 조직도 구성</td>
    </tr>
    <tr>
        <td></td>
        <td>/api/employee/order/list</td>
        <td>POST</td>
        <td>인피니티 스크롤링이 적용된 페이지에 추가 Page<> 를 전송</td>
    </tr>
    <tr>
        <td>EmitterController</td>
        <td>/sse/disconnect</td>
        <td>POST</td>
        <td>emitter 삭제 로직</td>
    </tr>
    <tr>
        <td></td>
        <td>/sse/employee</td>
        <td>GET</td>
        <td>직원용 emitter 발급</td>
    </tr>
    <tr>
        <td></td>
        <td>/sse/general</td>
        <td>GET</td>
        <td>일반 유저용 emitter 발급</td>
    </tr>
    <tr>
        <td>AlarmRestController</td>
        <td>/api/alarm/employee</td>
        <td>POST</td>
        <td>직원용 페이징 알람 요청</td>
    </tr>
    <tr>
        <td></td>
        <td>/api/alarm/general</td>
        <td>POST</td>
        <td>일반 유저용 페이징 알람 요청</td>
    </tr>
    <tr>
        <td></td>
        <td>/api/alarm/remove</td>
        <td>GET</td>
        <td>일반 유저용 알람 비우기 요청</td>
    </tr>
    <tr>
        <td>AdminController</td>
        <td>/valid/admin, /valid/admin/</td>
        <td>GET</td>
        <td>운영자 전용 홈페이지</td>
    </tr>
    <tr>
        <td></td>
        <td>/valid/admin/department</td>
        <td>GET</td>
        <td>직원 조직도 페이지 요청</td>
    </tr>
    <tr>
        <td></td>
        <td>/valid/admin/emergency/order</td>
        <td>GET</td>
        <td>응급 주문 확인 가능</td>
    </tr>
    <tr>
        <td></td>
        <td>/valid/admin/enroll/employee</td>
        <td>GET</td>
        <td>직원 생성 페이지 요청</td>
    </tr>
    <tr>
        <td></td>
        <td>/valid/admin/enroll/employee</td>
        <td>POST</td>
        <td>직원 생성 폼 전송</td>
    </tr>
    <tr>
        <td>EmailRestController</td>
        <td>/api/email/check</td>
        <td>POST</td>
        <td>RequestBody 로 받은 데이터를 cache 데이터와 비교</td>
    </tr>
    <tr>
        <td></td>
        <td>/api/email/valid</td>
        <td>POST</td>
        <td>타입별 인증 메일 전송 요청</td>
    </tr>
    <tr>
        <td>ItemRestController</td>
        <td>/api/item</td>
        <td>GET</td>
        <td>파라미터로 받은 offset 값을 받아 아이템 리스팅</td>
    </tr>
    <tr>
        <td>DeliveryProceedRestController</td>
        <td>/api/delivery/proceed</td>
        <td>POST</td>
        <td>배송 진행 요청</td>
    </tr>
</table>

## 🗒️개발 과정과 기록
#### 모든 개발 과정은 테스트 클래스를 통한 테스팅, 실제 구동에서의 구현 확인, 구현에서 발생하는 에러 수정을 통한 과정을 포함
- `dataSource(postGreSQL)` 설정
- `securityConfig` 작성 (FilterChain)
- `DAO` 생성
  - 사전에 계획한 구도에 따라 테이블을 생성
  - 조인 테이블을 이용한 상속 구현
  - 다양한 연관관계 매핑
- `AutoGeneratedDDL` 사용
  - dev 과정에서 사용하도록 매 테스트마다 따로 db 관리를 하지 않아도 계속해서 초기화 되도록 설정
- `국제화` 설정
  - Message Source 설정
  - message.properties 작성
- `home.html` 작성
  - 인증 전 메인페이지가 될 홈페이지 작성
  - `home` 탭은 비밀번호 찾기, 가입, 로그인 등을 `carousel` 로 구현
  - `feature` 탭은 회사에서 제공 가능한 내용을 알림
  - 마지막으로 `contact` 탭을 추가하여 연락할 수 있는 수단 사용자에게 알림
- `HomeController` 구현
  - 단순히 home 으로 view 를 전송하고 model 에 비어있는 객체를 넣어 thymeleaf 구현이 쉽도록 만듦.
  - 한 페이지에서 기능들을 구현해야 하므로 `emptyObjectInclude static 메서드` 구현
- `LoginController` 구현
  - javascript 단에서 구별하여 보내주는 form submit 데이터를 파싱한 뒤 인증 과정을 거침
  - 사전 세팅된 form login 으로 구현하지 않고 `AuthenticationManager` 를 커스텀하여 구현했음 (DAO 로 인증하기 위함)
- `Users` 추상 클래스를 자식으로 갖는 DAO 생성, `제너럴 빌더 패턴` 구현
  - UserType 에 따라 필드를 나눠야 하는 상황이라 `Join 패턴`으로 상속을 구현
  - 제너럴 빌더 패턴을 구현하여 추상 클래스의 데이터들은 기본으로 구현하고 필요한 데이터는 추가적으로 입력하여 각 테이블에 Join 될 수 있도록 구현함
- DTO 를 통한 `LoginController` 접근
  - 각 데이터의 DTO 를 구별하여 매핑하여 `유지보수`가 용이하도록 만듦
    -`Repository Interface`, `JPA Implement` 생성 및 구현
  - Repository 의 Interface 를 만들어 `다형성`을 보장
  - `JPA` 로 UserRepository 를 구현하였음
- `UserService` 생성
  - 중복 유저 확인, 유저 생성, findByEmail 등등 간단한 서비스 구현
  - 사용자 요구사항에 따라서 비즈니스 로직들을 구현할 것
- `검증`, `가입` 구현 (carousel 초깃값, 필드 에러 표시, @Valid 사용, 오류 메시지 커스텀 후 Toast 로 구현)
  - 필드 에러가 발생했을 시에 에러가 발생한 필드로 `carousel` 의 초깃값을 설정할 수 있게 만들어 사용자 경험에 Plus 요소
  - 필드에 `error` 표시를 하여 오류가 난 부분을 확인할 수 있도록 만듦
  - lifeTime 이 10초인 `Toast` 를 출력하여 어떤 부분이 잘못되었는지 알 수 있게 각 메시지를 띄워줌
- `API 이메일` 인증 기능 추가 (`@Async` 어노테이션 적용, `Redis Hash` + 상수 를 통해 하나의 email 당 여러 데이터를 저장할 수 있음)
  - `Thymeleaf Template` 설정을 사용해 템플릿에 따라 메일링이 가능함
  - `MailTemplate` DTO 를 만들어 Build 할 수 있도록 구현
- 쿠키를 이용한 `rememberMe` 기능 추가 (Vanilla Js)
- `Spring Security` 를 통한 접근 제어 및 에러 처리
  - `Exception` 타입에 따른 `AuthenticationEntryPoint` 분기 처리로 여러 오류 값에 따라 에러 메시지를 다르게 출력하였으며 `정보 유추`를 어렵게 만듦 (id 존재 유무 확인 등)
  - 해당 권한을 갖지 않은 유저를 Deny 하기 위한 `AccessDeniedExceptionHandler` 도 구현하였음
  - `UserDetailsService` 를 커스텀하여 DAO 를 통해 실제 값을 repository 에서 조회하여 객체지향적으로 검증할 수 있도록 만듦
- `ErrorController` 를 구현하여 메시징 된 내용을 alert 하고 페이지를 초기화면으로 보낼 수 있도록 설정
  - 다른 엔드포인트를 지정하여 `다양한 에러 처리` 가능
- `Valid/Home` 엔드포인트로 로그인 완료 된 유저만 접근할 수 있는 홈페이지을 작성
  - navbar 를 통해 리다이렉트 없이 여러 탭에 접근할 수 있음
    - home
    - dashboard
    - order
    - transport
    - alarm
  - 적절한 아이콘과 플로팅 메시지를 통해 직관성을 높힘
  - `Intersecting Observer` 를 통한 `css transaction` 으로 트렌디한 웹 브라우징 가능
  - 반응형 웹 구현으로 모바일 디바이스에서도 UI 가 깨지지 않게 구성
- Users 의 자식 클래스로 `Employee` 클래스 추가
- DAO 패키지에 `Alarm, Department, Message, Post` 클래스 추가
  - 사용자 알람
  - 직원 부서
  - 고객 센터 메시지
  - 소식
- Users 의 자식 클래스인 `Employee, Business, PFS` 에 `toBuilder` 메서드 작성
  - 일반 회원이 다른 회원으로 전환할 경우에 `정보를 그대로 사용해 재가입` 할 수 있도록 구현
  - `회원 정보 수정`을 위해서 toBuilder 메서드 구현
- DAO 별 `Repository, Service` 구현
- 계층형 카테고리인 `Category DAO` 의 재귀적 구현과 실질적으로 사용할 요소인 `DTO` 구현
- 전체 `카테고리를 캐싱`하여 (Redis 사용) DB 부담을 줄임
  - DB 조회는 캐싱할 때 한 번만 진행하고 parentId 와 name, id 만 가진 DTO 요소는 단순하게 카테고리 목록만 출력함
- `상품 리스트`도 마찬가지로 리스트 자체는 캐싱된 데이터를 사용
- 카테고리별로 상품을 조회할 수 있도록 구현
  - 해당 카테고리를 클릭하고 정렬을 선택하면 API 와 통신하여 선택한 카테고리의 상품만 visible 하게 설정
  - 중복적인 api 호출을 줄이기 위해 단순하게 사용자에게 보이지 않도록 설정하고 다른 카테고리 선택 시에 재활용 가능하도록 구현
  - css transaction 을 사용해 사용자 경험을 높힘
- 아이템 리스트는 `Intersection Observer` 를 통한 `Infinite Scroll` 로 구현
  - 옵저버가 `관측`  되었을 때 API 를 호출하여 오프셋 값을 전송하고 페이징을 통한 `상품 리스트` 를 출력함
- `장바구니` 구현
  - Cart, CartItem, CartRepository 등등 장바구니 관련 클래스 생성 및 구현
  - Cart 에 관련된 로직은 userId 로 구현할 것이기 때문에 SpringSecurity 에서 인증객체로 사용하는 UserDetails 의 구현체인 `User` 를 상속받은 `CustomUserDetails` 로 구현함
  - 간단하게 userId 만 포함하도록 만듦
  - 이렇게 구현함으로 join 문이 포함된 select 문을 남용하지 않을 수 있음
  - 고정된 장바구니 아이콘을 추가하고 아이템이 추가될 때마다 css 트랜지션으로 사용자가 확인할 수 있기 편하게 만듦
  - 뱃지를 통해 장바구니에 담긴 아이템의 수를 알 수 있도록 만듦
  - Cart html 구현
    - 장바구니 내부의 아이템 삭제, 수량 변경, 주문 기능 구현
    - 페이지를 떠났을 때 변경사항을 저장 할 수 있도록 BulkUpdate 구현 (navigator.sendBeacon 사용)
- `체크아웃` 구현
  - Cart 에서 주문하기 버튼을 클릭했을 때 주문을 위한 정보를 작성할 수 있는 페이지를 구현
  - 카카오 API 를 사용해 주소를 검색할 수 있도록 구현
    - Modal 을 띄우고 검색한 내용이 리스팅 됐을 때 해당 주소를 클릭하면 자동적으로 주소가 입력됨
    - 직접 입력은 하지 못하게 readOnly 로 설정
    - 사용자가 카드 정보와 배송지 정보를 입력하여 submit 하면 Order 로 넘어가서 주문을 처리함
- `주문` 구현
  - checkOut 페이지에서 작성한 정보를 토대로 주문을 생성함
  - 카카오 길찾기 REST API 를 사용해서 배송지와 출발지의 거리를 계산하여 시간을 예측
  - 각 center 중에서 가장 가까운 거리의 센터를 배정
  - Cart 를 통해 Order 를 구현하고 Delivery 에 Order 를 매핑함
  - POST 요청을 통해 주문을 완료하면 주문할 때마다 캐싱 데이터를 갱신
- `프로필` 구현
  - 이메일, 계정상태, 프로필 수정, 주문 내역 확인, 로그아웃할 수 있는 페이지 구현
  - 주문 내역은 `Collapse` 로 구성하였고 주문이 존재할 때는 데이터를 가져와서 보여주고 없을 경우에는 없다고 알림
  - 주문 데이터를 클릭하면 주문 리스트 페이지로 이동함
- `주문 리스트` 구현
  - 주문을 하면 주문 리스트 페이지로 이동
  - 주문 리스트는 profile 탭에서 주문한 내역을 같이 확인할 수 있도록 구현
  - redis 캐싱을 사용하여 User 조회를 한 번만 해도 주문 리스트를 확인할 수 있도록 구현 (유저별 주문 리스트)
- `주문 상세` 구현
  - 주문 리스트에서 주문을 클릭하면 주문 상세 페이지로 이동
  - 주문 상세 페이지에서 주문한 아이템과 배송지, 결제 정보, 배송 상태를 확인할 수 있도록 구현
  - JPQL 을 활용한 DTO 매칭 방식으로 OrderDetailsDTO 구현 (Select 문을 최대한 적게 사용하기 위해)
- **수정 내역**
  - 세션이 없는 상태에서 Error 페이지에 접근할 시에 세션 만료에 대한 안내
  - Payment 를 Entity 로 승격 (Repository 구현)
- `프로필 수정` 구현
  - 프로필을 수정할 수 있는 페이지 구현
  - 이름, 비밀번호 만 변경 가능 (비밀번호는 지정한 정규 표현식을 준수하도록 구현)
  - 커스텀 어노테이션을 구현하여서 프로필 변경 시 변경확인이 필요한 패스워드가 미입력 되더라도 `미변경`으로 인식하여 `@Pattern` 검증이 통과되도록 만듦
  - 유저 타입에 따라 attribute 가 존재하는데 이를 구별하여 수정할 수 있도록 만듦
  - 변환하고 싶은 유저 타입을 선택하고 해당 유저 타입에 기입해야하는 정보를 입력하면 변경할 수 있게 구현 (주문이 존재하면 안 되도록 설정)
- `CustomerService` 페이지 구현
  - 사용자가 고객 문의를 남기면 Employee 가 답변을 남길 수 있는 구조
  - 페이지네이션을 구현하여 properties 에 설정된 pageSize 만큼 보여줌
  - 검색을 구현하여 사용자가 원하는 Title 로 검색할 수 있도록 구현 (페이징 가능)
  - @Valid (NotEmpty, Size) 된 Post 객체를 사용해 제목과 내용을 작성하면 새로운 게시글을 작성할 수 있도록 구현
- **수정 내역**
  - 세션 데이터(CustomUserDetails)에 UserType 을 추가함으로 Employee 와 일반 유저간의 구별을 만듦(매번 DB 조회를 하지 않도록)
  - Home 에 SessionData 속 userType 을 Model 에 추가하여 각 탭에 맞는 권한을 부여 (thymeleaf 의 if, unless 사용)
- `해외 배송` 페이지 구현
  - 해외 배송을 필요로 하는 `Delivery` 유저들의 전용 배송 페이지
  - PCC (Personal customs code Check), 즉, 개인통관고유부호 가 존재해야하는 PFS (Port Facility Security Fee) 납부 의무가 있는 유저를 대상으로 해외 배송이 가능하게 만듦
  - 배송 대행지 (Center) 가 존재하며 이 센터에서 배송지 까지의 거리를 구하고 기본 배송일을 영업일 기준 5일로 설정
  - OverseasDelivery Entity 를 Joined 전략으로 생성하여 Delivery 의 자식객체로 생성 (해외 배송을 위한 필드를 추가함)
  - 해외 배송을 위한 Checkout 페이지를 구현하여 PFS 유저만의 배송 서비스를 제공
  - Order 없이 순수 Delivery 만을 생성하기 때문에 따로 Delivery 만을 관리하는 페이지를 생성해야함
  - radio 를 추가하는 과정에 클래스 네임을 노출하지 않기 위해 model 에 Enum 의 values 를 추가하여 사용
  - SSTI 공격이 문제되는 표현식 `__${}__` 를 사용하지만 Spring 과 Thymeleaf 고버전의 경우에는 `__${}__` 를 사용해도 문제가 발생하지 않음
  - TrackingNumber, weight 등등을 추가하여 해외 배송 전용 페이지를 구현함 (검증 O)
- **수정 내역**
  - Order 없이 순수 Delivery 구현을 통해 따로 해외배송을 구현하려고 했지만 Order 에 더미 Item 을 추가하여 Order 에서 확인이 가능하도록 구현
  - OrderControllerTest 에서 mockMvc 를 사용해 OrderController 의 기능을 테스트
  - OrderList 출력시에는 캐싱된 데이터를 사용하는데 DB 에 더미 데이터를 남기고 싶지 않아서 캐싱 데이터에만 더미 아이템을 넣고 PFS 주문을 구현함
  - Delivery 를 OverseasDelivery 로 DownCasting 하기 위해서는 UnProxy 를 사용해야함 (코드 추가)
  - CustomerService 의 게시글 정렬 기준을 postId 기준으로 변경
- `CustomerService` 상세 페이지 구현
  - 고객 센터에 작성한 글을 클릭하면 상세 페이지로 이동
  - 상세 페이지에서 해당 글의 작성자는 수정과 삭제를 할 수 있도록 구현
  - `Employee` 는 답변 작성, 작성글 임의 삭제 기능을 추가 (게시판 관리 부서)
- `findPw` 로직 구현
  - 이메일 인증까지만 구현했던 findPw 로직을 구현
  - 메일 인증을 완료하고 findPw 엔드포인트로 post 요청을 하면 임시 비밀번호를 발급할 수 있도록 구현
  - 같은 이메일 인증 로직을 사용하지만 redis 에 임시 데이터를 저장할 때 정형화된 분기를 구현하여 여러 갈래로 나뉠 수 있게 만듦 (RedisConst.get() 에 파라미터 추가)
  - 이메일 인증을 통해 이메일의 유효성을 검증하고 임시 비밀번호를 메일로 발급함
  - 같은 템플릿이지만 분기가 다르기 때문에 전송되는 메일의 내용이 다름
  - 임시 비밀번호는 일회용이며 해당 비밀번호로 로그인 한 이후로는 변경이 필요함
  - 로그인 검증 로직에 대한 bypass 를 추가하여 임시 비밀번호를 발급받은 유저는 1회에 한해 로그인 할 수 있도록 구현
  - 임시 비밀번호 이메일 내용을 별도로 구현 (MailService 에서 MailTemplate 를 추가)
  - 수동 검증을 사용하기 때문에 AuthenticationManager 에서 에러가 발생하면 Servlet 단으로 에러를 재전송하여 공용 에러 처리 (EntryPoint) 에서 처리하도록 만듦
  - Redis 에 저장된 데이터는 로직 진행 순서에 따라 삭제되도록 구현 (HashKey 는 Duration 을 지정할 수가 없음)
- **수정 내역**
  - `homeController` 에서 각 유저타입에 따라서 redirect 되는 페이지를 각 유저타입에 맞는 홈페이지로 이동시킴 (Employee, General 만 구현)
  - `SecurityConfig` 에서 UserType 에 따라 접근할 수 있는 페이지를 제한
- `EmployeeController` 생성
  - Employee UserType 을 가진 직원들만 접근할 수 있는 페이지를 구현
  - `valid/employee/home` 페이지를 생성
    - `PostId` 순서대로 정렬하여 응답되지 않은 게시글을 확인하여 답변할 수 있도록 구현
    - 실시간 고객센터 대기열을 확인하고 매칭할 수 있도록 만듦
    - 부서, 프로필, 응급처리, 알람 확인 등의 페이지 링크를 추가
    - 실시간 고객 응대 즉, 채팅 중에는 스크롤을 내려서 작업해야 하므로 응대 진행 중일 때 시각적 피드백을 통해 즉시 확인할 수 있도록 만듦
- `EmitterService` 생성
  - 알람을 실시간으로 전송하기 위한 Emitter 서비스를 생성
  - 구독, 발행 구조로 구현되어있어 실시간 알람 서비스를 제공하기에 적절하다고 판단
  - Emitter 는 SseEmitter 를 사용했으며 LifeTime 은 30초로 설정함
  - Emitter 는 각각 두 개의 로컬 저장소, 두 개의 캐시 저장소를 사용하여 구현함
  - 로컬 저장소는 `ConcurrentHashMap` 을 사용해서 각 유저의 Emitter 를 저장할 수 있도록 구현 (flag 값에 따라 emitter 를 구별해서 저장)
    - 그리고 `EmitterWrapper` 클래스를 만들어 locale 도 저장할 수 있도록 만듦
  - 캐시 저장소는 `LinkedHashMap` 을 상속받은 `ExpiringMap` 클래스를 구현함
    - Key 값은 비교가 가능해야하기 때문에 제네릭 함수의 K 는 Comparable 을 상속받음
    - SuperClass 인 LinkedHashMap 의 파라미터는 capacity 값 = 100(MAX_CACHE_SIZE), loadFactor = 1.0(할당 최적화 X), accessOrder = false(입력 순서 유지) 로 설정
    - properties 에서 MAX_SIZE 를 지정하면 @Postconstruct 에서 @Value 로 받아온 값을 사용해 메모리에 크기를 할당 (Bean 생성 시점보다 @Value 가 늦게 주입되기 때문에 Bean 생성 이후에 상수값을 지정)
    - MAX_SIZE 를 초과하면 가장 먼저 입력된 Emitter 를 삭제하고 값을 put
    - getEntryAfter() 메서드를 생성했는데 이는 입력된 정렬된 데이터 내부에서 Key 값을 발견하면 flag 를 true 로 놓아 이후 데이터는 모두 add 하여 list 형태로 리턴해줌
    - 이로써 last-event-id 이후에 입력된 데이터 (key 값이 currentMillis 기 때문) 를 확인할 수 있도록 캐싱시스템을 구현
  - 캐시 시스템의 한계는 last-event-id 가 지정되지 않는 시간 (재발급 시간) 에 알람이 생성되는 조건이 무한히 갖춰지면 계속해서 캐시 데이터를 받아올 수 없음
  - 하지만 30초의 lifetime 사이에 한 번이라도 알람이 발생해서 last-event-id 가 지정되면 이후에는 정상적으로 작동함
- `AlarmService` 생성
  - 알람을 전송하기 위한 서비스 생성
  - `Alarm` Entity 를 사용해 알림을 저장
  - 알람의 메시지에는 messageSource 에 들어갈 파라미터 값을 주어서 국제화할 수 있도록 구현
  - 실제 알람을 사용자가 확인할 때 emitter 에서 messageSource 에 전달될 파라미터를 찾아와 사용자의 브라우저의 언어로 변환하여 전송
  - DB 에 저장되는 알람은 Locale.KOR 로 저장
- **수정 내역**
  - Alarm DAO 에 `sourceMsg` 필드를 추가하여 알람 확인자의 언어에 맞는 메시지를 전송할 수 있도록 구현
- `CustomerService` 응답 기능 구현
  - Employee Home 에서 답변을 기다리는 질문을 id 순서대로 확인할 수 있도록 구현
  - 작성된 순서로 sorted 되어있어 FIFO 방식으로 답변을 작성할 수 있음
  - 답변은 운영자와 직원만 작성할 수 있음
  - 답변을 작성할 때는 `Post` Entity 를 사용하여 데이터를 주고받는데 AnswerContent 필드에 ValidationGroup 을 지정하였음
  - 마커인터페이스가 적용된 `AnswerContent` 는 `cs/post/response/edit` 페이지에서만 사용되며 NotEmpty, Size 검증을 적용함
  - 다른 Post 작업에서는 검증하면 안 되기 때문에 마커 인터페이스를 활용해 특정 로직에서만 검증이 적용되도록 그룹을 부여
  - 어노테이션을 사용하지 않고 `LocalBeanValidator` 를 사용해 수동 검증
  - 각 요소에 대한 버튼이 존재함
    - 부서 정보
      - 각 부서에 할당된 직원을 확인할 수 있음 (메일, 이름 확인 가능)
    - 프로필
      - 기존에 존재하는 `/profile` 패스로 href 처리
    - 응급처리
      - 아직 배송처리가 되지 않았고 배송일이 가까워져오는 (`exceptedAt` field) order 을 통합적으로 관리할 수 있는 페이지
    - 알람
      - `CS` 가 가능한 작업들에 대한 알람을 확인할 수 있는 페이지
- `Department` 페이지 구현
  - 타부서 직원들의 조직을 확인할 수 있는 페이지 구현
  - fetch api 를 사용하여 script 자체에서는 데이터를 확인할 수 없도록 만듦
  - 캐싱을 사용해 redis 에 저장된 데이터를 조회할 수 있도록함
  - 부서 데이터를 list 로 출력하여 collapse 로 구현
- **수정 내역**
  - 메인 클래스에 `@EnableScheduling` 어노테이션 추가 (긴급건 체크를 위한 스케쥴링을 사용함을 명시)
  - `OrderScheduler` 클래스를 추가하여 설정 시간마다 긴급건 체크를 진행하며 체크된 내용은 직원에게 알람을 보내도록 설정
- `긴급 처리` 로직 구현
  - `DeliveryService` 에서 긴급 처리 건을 확인할 수 있는 로직 구현
  - application.properties 에서 설정해놓은 긴급 처리 기준 시간 이내의, `배송완료` 상태가 아닌 주문을 찾아 긴급 처리건으로 설정
  - `DeliveryProceedRestController` 에서 `DeliveryStatus` 의 다음 상태로 진행하는 엔드포인트를 작성
  - `/api/delivery/proceed` 엔드포인트는 해당 유저의 요청이 아니거나 Employee, Admin 의 유저타입을 갖고있지 않으면 배송 상태를 변경할 수 없음
  - 긴급 처리 스케쥴링 시에 캐싱 저장소(redis) 에 DTO를 캐싱하여 등록함, 이 데이터는 직원들의 긴급 처리 건에 대한 조회를 담당
  - 일정 스케쥴 마다 한 번씩 실행되는 것이기 때문에 JPQL 을 통한 성능 최적화 보다는 객체의 유연성을 보장하기 위해서 트랜잭션 내에서 엔티티를 사용한 초기화 방법을 사용함 (수정사항이 생길 수 있기 때문에)
  - 조회 버튼을 클릭하면 Orders 상세 보기로 이동
  - 그렇게 이동한 OrdersDetail 페이지에는 각 센터의 직원들에게 다음 배송 세션으로 진행시킬 수 있는 버튼을 추가
  - 배송이 완료된 주문은 em 건에서 자동으로 제외됨
- **수정 내역**
  - `EmitterController` 의 로직에서 Emitter 를 직접 리턴받아서 Map 을 찾는 수고를 한단계 덜도록 리팩토링
  - `Emitter` 발급 시에 id 를 추가해서 cache 메시지를 확실히 받도록 설정
- `알람` 기능 구현
  - 페이지를 로딩할 때마다 application.properties 에 설정된 pageSize 만큼의 알람을 가져와서 화면에 출력
  - 알람 버튼을 클릭하면 `offcanvs` 를 통해 알림을 같은 페이지에서 확인할 수 있도록 만듦
  - jpql 을 통해서 createAt 기준으로 내림차순 정렬하여 가장 최신 알림을 확인할 수 있도록 구현
  - 이후 스크롤을 내릴 때마다 `Intersection Observer` 를 사용해 다음 pageSize 만큼의 알람을 가져옴
  - 그렇게 가져온 알람은 과거의 데이터이며 리스트의 아래에 추가됨
  - 현재 알람은 `Emitter` 를 사용해 실시간으로 prepend 방식으로 리스트의 앞에 추가해줌
  - 알람이 발생하면 badge 를 통해 알람이 생김을 유저가 알 수있도록 구현
  - 긴급 처리건은 해당 버튼의 링크 위에 badge 를 통해 구별하여 표시
  - 스크롤이 최상단이 아닐 경우에는 화면에 static 하게 fading 처리된 화살표를 띄워 유저가 인지할 수 있도록 구현
    - 고객 응대중인 직원은 알람을 확인할 수 없으니 화살표로 알릴 수 있도록 하기 위함
    - 스크롤이 최상단인 경우에는 알람 badge 를 통해 알림을 확인할 수 있음
    - 하지만 고객 응대중인 직원의 경우에는 이를 알 수없으니 화살표를 통해 알림이 생겼음을 알려줌
- **수정 내역**
  - createAt 의 JSON format 을 설정
  - 테스팅을 통해 알람 캐시데이터의 max size 와 페이징 처리 기능의 유효성을 확인
  - Message DAO 를 수정하여 JSON 직렬화 시에 ignore 할 필드 추가, DB 에 저장하지 않을 필드 추가
- `고객센터` 기능 구현
  - 유저와 직원의 매칭 시스템을 통해서 실시간 고객 응대를 할 수 있도록 구현
  - `webSocket` 을 사용
  - `WebSocketConfig` 클래스를 생성하여 Websocket 에 대한 설정을 해준다
    - `@EnableWebSocket` 어노테이션을 사용하여 Bean 등록, WebSocketConfigurer 를 구현함
    - ws 통신을 위한 빈 등록은 이 클래스에서 진행함
    - **핸들러**
      - `MyWebSocketHandler` 클래스를 생성하여 `TextWebSocketHandler` 를 상속받고 로직을 구현함
        - connect: queue 에 유저를 등록, 인터셉터를 통해 HttpSession 에서 유저 정보를 가져와 queue 에 `SessionWrapper` 라는 객체를 만들어 저장
        - handleTextMessage: objectMapper 를 통해 JSON 문자열을 역직렬화 한 뒤 메시지 길이 검사, 타입별 처리
          - csMessage: 실질적인 메시지 전송
          - dropMessage: 유저가 채팅을 종료했을 시 (onClose), 매칭 timeOut 됐을 시에 dropMessage 를 보내게 되는데 이는 dropQueue 메서드를 실행시킴
        - close: 세션이 종료되면 Chatroom 을 제거, queue 에서 해당 유저를 제거
    - **인터셉터**
      - `HttpSessionHandShakeInterCeptor` 를 체이닝하여 HttpSession 에서 유저 정보를 가져와서 WebSocketSession 에 저장
        - 가져온 데이터를 활용할 수 있도록 Handler 에 static 메서드를 구현하여 webSocketSession 에서 HttpSession 을 가져올 수 있도록 함
      - `WebSocketThrottleInterceptor` 를 체이닝하여 해당 path 에 반복된 요청을 제한함
        - employee 의 경우에는 제한을 걸지 않고 일반 유저의 경우에는 20초에 한 번만 요청을 보낼 수 있도록 설정
    - **서블렛**
      - `ServletServerContainerFactoryBean` 을 사용하여 서블렛 단에서 WebSocket 에 대한 설정을 프로젝트 내에서 할 수 있게 만듦
        - TextMessage 의 사이즈는 64Kb, BinaryMessage 의 사이즈는 512kb 로 설정 (추후 사용 가능성)
  - `ChatRoom` DTO 클래스 생성
    - 유저의 세션을 저장하고 메시지를 전송할 수 있는 클래스
    - roomId, ObjectMapper, ConcurrentHashMap.newKeySet() 을 필드로 가짐
    - 생성자로 session 을 add 하는 메서드, remove 하는 메서드, 각 세션에 메시지를 send 하는 메서드
    - HttpSession 을 전달받는 WebSocketSession 을 사용하여 각 socket 의 개별화가 되어있는 상태에서 메시지의 isMine 값을 설정해주어 자신이 보낸 메시지인지 아닌지 구별 가능하게 함
    - 이렇게 함으로써 메시지의 isMine 값에 따라 css 를 다르게 적용할 수 있도록 함
    - 마지막으로 각 객체별로 선언된 Jackson2ObjectMapper 를 사용해 메시지를 직렬화 하여 전송
  - `CustomerServiceRestController` 생성
    - fetch API 를 사용하여 세션 인증 정보를 전달받아 유저의 세션을 dropQueue 하고 존재하는 chatRoom 을 제거하는 엔드포인트를 구현
  - `MessageService` 생성
    - MessageType 이 csMessage 인 경우에 메시지를 저장하도록 구현
  - `ChatService` 생성
    - 유저 타입에 따라 구분된 (Employee, General) queue 를 관리
    - 매칭이 완료되면 생성되는 `ChatRoom` 을 관리
    - 유저 ID 에 따라 roomId 를 저장할 수 있는 Map 을 관리
    - 모든 유저의 세션 생성과 소멸 단계에서 리소스를 정리할 수 있도록 구현
- **수정 내역**
  - EmitterService
    - UserQueue 의 사이즈를 알려주어 대기하고 있는 유저의 수를 실시간으로 확인 가능
    - 파라미터를 포함한 메시지를 전송할 수 있도록 wrapper 클래스를 생성하고 전체적인 로직을 수정함
    - Cache 로직을 수정하여 일반 유저의 캐시와 직원의 캐시를 구별함
    - 로직 중에 millis 단위로 캐시를 갱신하도록 구현했는데 이 과정에서 캐시가 중복될 수 있어 동기화 시킨 (synchronized) 로직으로 millis 가 중복될 경우 while + contains 여부에 따라 계속해서 값을 ++ 하여 빈자리를 찾게 만들어 중복을 방지함
  - `deliveryProceed` 시에 캐시를 갱신하여 로직 진행 시에 갱신된 캐싱 데이터를 확인할 수 있도록 만듦
  - 확실한 emitter 관리를 위해서 sendBeacon 를 트리거로 emitter remove 를 명확히 하도록 구현
- `유저 알람 기능` 구현
  - 작성 문의사항에 답변이 달리거나 배송이 완료됐을 시에 알람을 받을 수 있도록 구현
  - 마찬가지로 인피니티 스크롤을 이용, 스크롤링하여 알람을 불러올 수 있도록 함
  - emitter 를 사용해 실시간 알람이 가능
- `주문 리스트` 구현
  - `/valid/emplyee/home` 에서 order list 에 접근할 수 있음
  - 이를 통해서 현재 존재하는 주문들을 리스팅
  - 검색 시 조건 (Keyword class) 에 따라서 select form 으로 조건들을 나열하고 해당 조건 enum 이 갖는 클래스명을 라벨링 하여 switch 문을 통해 클래스 타입 검사를 하게됨
  - 결과적으로 제네릭 형태와 같은 기능을 가진 keyword 검색 로직 구현
  - `OrderSearchParamDTO` 검색 DTO + `@ModelAttribute` 를 통해 페이징 이동과 검색 시에 이전 페이지의 검색 결과등을 유지하게 생산성 확대
  - 각 조건에 부합하는 데이터를 띄워주고 클릭 시에 `/manage/order` 페이지로 이동함
  - 각 주문의 배송 상태를 관리할 수 있게됨
- `운영 관리 페이지` 구현
  - 실시간 데이터를 확인할 수 있는 대시보드 페이지를 구현
  - `ChartJs` 를 사용하여 API 요청을 통한 차트를 구성
  - `/valid/admin/**` 패스로 admin 권한이 있는 유저만 접속 가능
  - 확인할 수 있는 차트 데이터
    - 막대 그래프
      - 응급 주문 수
      - 유저 수
      - 미응답 문의 수
      - 미완료 주문 수
    - 선 그래프
      - 가입자 추이
      - 일별 주문
  - 직원 등록 가능
    - 부서 즉, Department 는 `CustomerService` 로 생성됨
    - `Valid` 된 이메일로 임시 비밀번호 메일을 전송
    - 그럼 해당 이메일로 아이디가 생성되며 1회에 한하여 임시 비밀번호로 접속 가능
  - 긴급 주문의 상세 내역을 확인할 수 있음
  - 부서 인원의 연락처를 열람 가능
- 국제화 마무리
- 클래스 별로 기능을 주석으로 설명
- RESTAPI 통신 SpringSecurity 권한 체크

