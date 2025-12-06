# 课程设计报告

**课程名称**： 开源软件生态与开发技术
**设计题目**： **基于Spring Boot与Vue的宠物领养管理系统的设计与实现**
**学院**： 计算机学院
**专业**： 软件工程
**年级**： 2023级
**学号**： [填写学号]
**姓名**： [填写姓名]
**指导教师**： [填写教师姓名]

---

## 摘要

在当今数字化转型的浪潮中，开源软件生态已成为推动软件技术创新的核心动力。随着互联网技术的飞速发展，利用成熟的开源技术栈构建高性能、高可用的企业级应用已成为软件行业的标准实践。本课程设计《开源软件生态与开发技术》旨在通过一个完整的项目实战，引导学生深入理解开源文化的精髓，掌握JavaEE领域主流开源技术与框架的深度应用。针对当前社会中日益严峻的流浪动物救助与领养这一实际社会痛点，本实验经过深入的需求调研与分析，设计并实现了一套功能完善、交互友好的基于B/S架构的宠物领养管理系统。

本系统严格遵循JavaEE企业级开发规范，采用前后端分离的现代化开发模式，确保了系统的高内聚低耦合。后端基于Java语言，深度应用Spring Boot框架（作为SSM框架的现代演进版本），并整合了MyBatis-Plus、Spring Security、Hutool等一系列优秀的开源技术，实现了包括用户鉴权、复杂业务逻辑处理、数据持久化在内的核心功能；前端则紧跟大前端技术趋势，采用Vue 3框架结合Vite构建工具和Element Plus组件库，构建了响应式、组件化的用户交互界面。系统核心功能涵盖了宠物信息全生命周期管理、基于状态机的双重审核机制领养申请、活跃的宠友交流论坛、系统公告发布及基于ECharts的数据可视化统计等。

实验结果表明，该系统运行稳定，性能优良，功能完备，能够有效解决传统线下宠物领养过程中存在的信息不对称、流程不透明、管理效率低下等问题。通过本项目的全周期开发，不仅深入理解了开源社区的协作文化，掌握了利用开源解决方案解决实际工程问题的能力，更极大地巩固了面向对象程序设计、数据库设计及软件工程等基础理论知识，为未来从事复杂的企业级软件开发奠定了坚实基础。

**关键词**： 开源软件生态；Spring Boot；SSM框架；Vue.js；B/S架构；前后端分离

---

## 目录

1. 实验目的与意义
2. 系统需求分析与总体设计
    2.1 需求分析
    2.2 系统架构设计
    2.3 数据库设计
3. 系统详细设计与实现
    3.1 后端核心模块实现
    3.2 前端交互设计与实现
    3.3 关键功能实现细节
4. 系统测试与问题分析
    4.1 测试环境与测试用例
    4.2 开发过程中的问题与解决方案
    4.3 实验内容对课程目标的支撑
5. 结论
参考文献
附录

---

## 正文

### 1. 实验目的与意义

本课程设计的核心教学目标在于打破理论学习与工程实践之间的壁垒。通过从零开始构建一个完整的Web应用，旨在让学生在实践中深入理解开源软件生态体系的运作机制，熟练掌握包括Spring Boot、Vue.js在内的主流开源技术栈。这不仅是对面向对象程序设计、数据库系统原理、软件工程等基础课程知识的综合运用与巩固，更是为了培养学生利用计算思维和编程技能分析复杂问题、设计系统架构以及解决实际工程问题的核心能力。

**实验意义**：
1.  **深度掌握开源技术栈**：通过全栈开发实践，深入掌握JavaEE相关主流技术、框架和规范。特别是Spring Boot（SSM），作为Spring生态的核心，它通过“约定优于配置”的理念极大地简化了企业级应用的配置与部署，是当前业界最主流的Java开发框架。同时，Vue 3作为前端领域的佼佼者，其组合式API和响应式系统代表了现代前端开发的方向。掌握这些技术栈，意味着具备了与行业接轨的实战能力。
2.  **深刻理解开源生态文化**：在开发过程中，通过使用Maven进行依赖管理，引入第三方开源库（如Hutool工具包、Lombok注解库、PageHelper分页插件），学生能够深入了解开源社区的协作模式，体会开源软件“共享、协作、共赢”的精神内核。学会如何阅读开源文档、如何参与社区讨论、如何利用现有的开源轮子来提高开发效率，是成为一名优秀软件工程师的必经之路。
3.  **解决实际社会问题**：选择宠物领养这一B/S架构平台的应用场景，具有显著的社会公益价值。通过完整的需求分析、概要设计、详细设计与实现，构建一个透明、高效的宠物领养平台，能够有效解决流浪动物救助信息不对称的问题，推广“领养代替购买”的理念，用技术手段传递社会温情，体现了技术人员的社会责任感。

### 2. 系统需求分析与总体设计

#### 2.1 需求分析
经过对现有宠物领养流程的调研，本系统主要包含两类用户角色：普通用户和系统管理员，各类角色的功能需求如下：
1.  **普通用户**：
    *   **浏览与搜索**：用户可以浏览系统中所有待领养的宠物信息，查看宠物的详细资料（包括高清图片、年龄、性别、健康状况、疫苗接种情况等）。系统支持多维度的筛选功能，用户可按品种、性别、年龄段等条件快速定位心仪的宠物。
    *   **业务办理**：用户在登录后，可以对心仪的宠物提交领养申请。申请表单需要填写详细的领养理由、个人居住条件、经济状况等信息，以便管理员审核。用户还可以实时查看自己提交的申请进度（待审核、已通过、已驳回），并在被驳回时查看具体的驳回理由。
    *   **社区交互**：为了增加用户粘性，系统设有“宠友交流”版块。用户可以在此发布帖子分享养宠经验、晒出宠物萌照，同时也支持对他人帖子进行评论互动和点赞，形成良好的社区氛围。
    *   **个人中心**：用户可以管理自己的个人信息，包括修改昵称、联系方式、上传个性化头像以及修改登录密码，确保账户安全。
2.  **管理员**：
    *   **基础管理**：管理员拥有最高权限，可以对注册用户进行管理（如禁用发布违规内容的账号、启用误封账号），同时负责维护宠物分类字典（如猫、狗、兔子等），确保系统数据的规范性。
    *   **核心业务**：管理员负责宠物信息的全生命周期管理，包括发布新宠物（上传图片、填写详细资料）、编辑现有信息以及将已领养的宠物下架。最核心的功能是领养申请审核，管理员需要仔细查阅申请人的资料，决定批准申请（此时系统自动锁定宠物状态）或驳回申请（需填写驳回理由反馈给用户）。
    *   **运营管理**：管理员可以发布系统公告（如线下领养活动通知、系统维护通知），并对论坛帖子进行监管，及时删除涉及敏感信息或广告的违规内容，维护社区环境。
    *   **数据统计**：系统提供可视化的数据看板，管理员可以查看领养趋势图表（基于ECharts实现），统计热门品种、领养成功率、用户活跃度等关键指标，为运营决策提供数据支持。

#### 2.2 系统架构设计
为了确保系统的高可用性、可扩展性和易维护性，本系统采用经典的前后端分离B/S架构，技术选型紧跟开源社区主流：
*   **表现层（Frontend）**：基于 **Vue 3** + **Vite** 构建。Vue 3 的组合式API（Composition API）提供了更好的逻辑复用能力，使得代码组织更加清晰；Vite 作为新一代前端构建工具，利用ES Modules特性提供了极速的冷启动和热更新体验，大幅提升了开发效率；**Element Plus** 作为一套基于Vue 3的桌面端组件库，提供了丰富的企业级组件，保证了系统界面的统一性和美观度；**Axios** 负责与后端进行异步HTTP通信；**Pinia** 作为新一代状态管理库，替代了Vuex，提供了更简洁的API和更好的TypeScript支持。
*   **业务逻辑层（Backend）**：采用 **Spring Boot 2.7** 框架。它是SSM（Spring + Spring MVC + MyBatis）框架的现代封装与演进，通过自动配置（Auto-configuration）和起步依赖（Starter）极大简化了繁琐的XML配置，使得开发者可以专注于业务逻辑的实现。安全控制方面，采用 **Spring Security** + **JWT** 实现无状态认证，非常适合前后端分离的架构场景，能够有效防止CSRF攻击并实现细粒度的权限控制。
*   **数据持久层（Database）**：**MySQL 8.0** 作为关系型数据库存储系统核心数据。ORM框架选用 **MyBatis-Plus**，它在MyBatis基础上只做增强不做改变，提供了强大的CRUD接口和条件构造器（Wrapper），极大简化了SQL操作，同时支持主键自动生成、逻辑删除、乐观锁等高级特性。

#### 2.3 数据库设计
系统核心数据表经过规范化设计，确保数据的一致性和完整性：
1.  **用户表 (`sys_user`)**：存储系统的所有用户信息。
    *   `id`: 主键ID，采用自增策略。
    *   `username`: 用户名，设置唯一索引，不可重复。
    *   `password`: 密码，经过BCrypt强哈希算法加密存储，保障安全性。
    *   `role`: 角色标识（0-管理员, 1-普通用户），用于权限控制。
    *   `avatar`: 头像URL，存储图片服务器的访问路径。
2.  **宠物信息表 (`pet_info`)**：存储待领养宠物的详细档案。
    *   `id`: 主键ID。
    *   `name`: 宠物昵称。
    *   `category_id`: 外键，关联品种字典表。
    *   `status`: 状态机字段（0-待领养, 1-申请中, 2-已领养），用于控制业务流程。
    *   `image_url`: 宠物照片地址。
3.  **领养申请表 (`adoption_application`)**：存储用户的领养请求记录。
    *   `id`: 主键ID。
    *   `pet_id`: 外键，关联宠物ID。
    *   `applicant_id`: 外键，关联申请人ID。
    *   `reason`: 申请人填写的领养理由。
    *   `status`: 审核状态（0-待审核, 1-通过, 2-驳回）。
    *   `reject_reason`: 若被驳回，存储管理员填写的驳回原因。
4.  **论坛帖子表 (`forum_post`)**：存储社区交流内容。
    *   `id`: 主键ID。
    *   `title`: 帖子标题。
    *   `content`: 帖子内容，支持富文本。
    *   `user_id`: 外键，发布人ID。
    *   `view_count`: 浏览量，用于统计热度。

### 3. 系统详细设计与实现

#### 3.1 后端核心模块实现
后端采用标准的分层架构（Controller-Service-Mapper），严格遵循JavaEE开发规范，确保代码的清晰度和可维护性。

**（1）安全认证模块**
**代码位置**：
*   `JwtAuthenticationFilter`: `pet-adoption-backend/src/main/java/com/whu/pet/security/JwtAuthenticationFilter.java`
*   `SecurityConfig`: `pet-adoption-backend/src/main/java/com/whu/pet/config/SecurityConfig.java`

系统基于Spring Security实现了一套完善的安全控制机制。核心逻辑在于自定义的 `JwtAuthenticationFilter` 过滤器。该过滤器会拦截所有进入系统的HTTP请求，从Header中提取 `Authorization` 字段（Bearer Token）。如果Token存在且有效（通过JWT工具类验证签名和有效期），则解析出用户信息（用户名、角色），并构建 `UsernamePasswordAuthenticationToken` 对象存入 `SecurityContextHolder`，完成用户的身份注入。同时，在 `SecurityConfig` 配置类中定义了精细的URL权限规则，例如 `/api/admin/**` 路径仅允许拥有管理员角色的用户访问，而 `/api/auth/**` 和公共GET请求则允许匿名访问。

**（2）全局异常处理**
**代码位置**：
*   `GlobalExceptionHandler`: `pet-adoption-backend/src/main/java/com/whu/pet/exception/GlobalExceptionHandler.java`
*   `BusinessException`: `pet-adoption-backend/src/main/java/com/whu/pet/exception/BusinessException.java`

为了规范API接口的返回格式，提升前后端交互的稳定性，系统实现了 `GlobalExceptionHandler`。利用 `@RestControllerAdvice` 注解捕获Controller层抛出的所有异常。针对 `MethodArgumentNotValidException`（参数校验失败），提取具体的错误字段信息；针对 `AccessDeniedException`（权限不足），返回403状态码；针对自定义的 `BusinessException`，返回特定的业务错误码。所有异常最终都被封装为统一的 `Result<T>` 格式（包含code, message, data）返回给前端，避免了直接暴露堆栈信息，提升了系统的安全性。

**（3）MyBatis-Plus应用**
**代码位置**：
*   `PetService`: `pet-adoption-backend/src/main/java/com/whu/pet/service/PetService.java`
*   `PetMapper`: `pet-adoption-backend/src/main/java/com/whu/pet/mapper/PetMapper.java`

在Service层，通过继承 `ServiceImpl<Mapper, Entity>`，直接获得了通用的CRUD能力，无需手写基础SQL。例如，在查询宠物列表时，使用 `LambdaQueryWrapper` 构建动态查询条件，可以优雅地处理“如果名称不为空则模糊查询”、“如果状态不为空则精确匹配”等逻辑，代码简洁且类型安全。此外，还配置了 `PaginationInnerInterceptor` 分页插件，实现了物理分页功能，有效提升了大数据量下的查询性能。

#### 3.2 前端交互设计与实现
**（1）网络请求封装**
**代码位置**：`pet-adoption-frontend/src/utils/request.js`

为了统一处理HTTP请求，系统对Axios进行了二次封装（`request.js`）。
*   **请求拦截器**：自动在每个请求的Header中添加 `Authorization: Bearer token`，确保后端能识别用户身份，避免了在每个API调用处手动添加Token的繁琐。
*   **响应拦截器**：统一处理后端返回的状态码。如果遇到 `401 Unauthorized`，说明Token过期或无效，自动清除本地缓存的用户信息并跳转至登录页；如果遇到业务错误（code != 200），统一调用Element Plus的 `ElMessage` 组件弹出错误提示框，简化了组件内的错误处理逻辑。

**（2）状态管理**
**代码位置**：`pet-adoption-frontend/src/stores/user.js`

使用 Pinia 定义了 `userStore`，用于集中存储当前登录用户的 Token、UserInfo 和 Role。利用 Pinia 的持久化插件（pinia-plugin-persistedstate），将关键状态自动同步到 `localStorage` 中，确保页面刷新后用户登录状态不丢失，提供了流畅的用户体验。

#### 3.3 关键功能实现细节
**（1）双重审核机制**
**代码位置**：
*   `AdoptionApplicationController`: `pet-adoption-backend/src/main/java/com/whu/pet/controller/AdoptionApplicationController.java`
*   `AdoptionApplicationService`: `pet-adoption-backend/src/main/java/com/whu/pet/service/AdoptionApplicationService.java`

领养申请流程设计了严谨的状态机，确保业务逻辑的闭环。
1.  **提交申请**：用户提交申请时，系统首先检查该宠物是否处于“待领养”状态。如果是，则插入 `adoption_application` 记录，状态为 `0`（待审核），同时在一个事务中将对应 `pet_info` 的状态更新为 `1`（申请中），防止其他人重复申请同一只宠物。
2.  **审核通过**：管理员点击“通过”后，调用 `approve` 接口。后端开启事务，执行两步操作：将申请记录状态置为 `1`（通过），将宠物信息状态置为 `2`（已领养）。
3.  **审核驳回**：管理员点击“驳回”并填写理由后，调用 `reject` 接口。后端开启事务，将申请记录状态置为 `2`（驳回），并将宠物信息状态回滚为 `0`（待领养），使其重新回到可被申请的状态，释放资源。

**（2）论坛互动功能**
**代码位置**：
*   `ForumController`: `pet-adoption-backend/src/main/java/com/whu/pet/controller/ForumController.java`
*   `ForumService`: `pet-adoption-backend/src/main/java/com/whu/pet/service/ForumService.java`
*   前端页面: `pet-adoption-frontend/src/views/forum/ForumList.vue`, `ForumDetail.vue`

实现了完整的社区功能。帖子列表支持分页查询（PageHelper），支持按标题或内容关键词搜索。详情页展示帖子内容及评论树。点赞功能设计为幂等操作，后端通过Redis或数据库表记录“用户-帖子”的点赞关系，防止同一用户对同一帖子重复点赞，保证了数据的准确性。

### 4. 系统测试与问题分析

#### 4.1 测试环境与测试用例
为了验证系统的稳定性和功能的正确性，我们在以下环境中进行了严格的测试：
*   **测试环境**：操作系统 Windows 11 专业版, JDK 11.0.19, MySQL 8.0.33, 浏览器 Chrome 120.0.6099.130。
*   **测试用例**：
    | 模块 | 测试场景 | 预期结果 | 实际结果 |
    | :--- | :--- | :--- | :--- |
    | 用户认证 | 输入正确账号密码登录 | 跳转至首页，LocalStorage中存储Token | 通过 |
    | 用户认证 | 输入错误密码 | 界面提示“用户名或密码错误”，不跳转 | 通过 |
    | 宠物管理 | 管理员上传宠物图片 | 图片回显成功，URL正确存入数据库 | 通过 |
    | 领养申请 | 用户申请已锁定的宠物 | 系统拦截请求，提示“该宠物正在申请中” | 通过 |
    | 论坛互动 | 未登录用户尝试发帖 | 路由守卫拦截，自动跳转至登录页面 | 通过 |

#### 4.2 开发过程中的问题与解决方案
在项目开发及集成阶段，我们遇到并解决了以下关键技术问题，这些问题的解决过程极大地提升了我们的工程实践能力：

**问题一：Lombok版本与JDK编译器不兼容导致编译失败**
*   **现象**：在运行 `mvn spring-boot:run` 时，控制台爆出大量 `java.lang.NoClassDefFoundError: lombok.javac.Javac` 错误，提示找不到符号（如 `setCode`, `setData` 等Getter/Setter方法），导致项目无法启动。
*   **分析**：经过排查，发现项目使用的JDK版本较新（JDK 11+），而Spring Boot 2.7.x 默认管理的Lombok版本较低（1.18.22及以下），旧版Lombok使用了JDK内部的非公开API，这些API在JDK高版本中被移除或重构，导致注解处理器无法在编译期正常工作。
*   **解决方案**：在 `pom.xml` 中显式指定 `maven-compiler-plugin` 的配置，并将 Lombok 版本升级至 **1.18.30**，该版本修复了对高版本JDK的兼容性问题。这一过程加深了我们对Maven依赖仲裁机制和Java编译器工作原理的理解。

**问题二：前后端接口路径不一致导致404错误**
*   **现象**：前端开发完成后，访问“宠友交流”页面时，控制台报错 `404 Not Found`，请求路径为 `/forum/post/list`，数据无法加载。
*   **分析**：通过对比后端Swagger接口文档和前端代码，发现后端 `ForumController` 定义的接口路径为 `/api/forum/posts`，而前端 `api/forum.js` 中封装的路径与后端不匹配。这是前后端分离开发中常见的协作问题，通常由于沟通不及时或文档更新滞后导致。
*   **解决方案**：以后端Swagger文档为准，修正前端 `api/forum.js` 中的请求URL，使其与后端严格一致。同时，在团队协作中引入了接口定义规范，要求后端变更接口必须同步通知前端，减少此类问题的发生。

**问题三：SCSS预编译变量未定义错误**
*   **现象**：前端启动时报错 `Undefined variable: $color-danger`，导致页面样式编译失败。
*   **分析**：在 `MyApplications.vue` 等组件中使用了 `$color-danger` 变量，但该变量在全局变量文件 `variables.scss` 中未定义（实际定义为 `$color-error`）。这反映了代码规范执行不到位的问题。
*   **解决方案**：全局搜索并替换变量名，将 `$color-danger` 统一修正为 `$color-error`，并确保在 `vite.config.js` 中正确配置了SCSS的全局引入（`preprocessorOptions`），使得全局变量在每个组件中自动生效，无需手动导入。

#### 4.3 实验内容对课程目标的支撑
1.  **开源软件生态理解**：通过引入Spring Boot、MyBatis-Plus、Hutool等开源组件，并解决版本兼容性问题（如Lombok），深入理解了开源生态中的依赖管理、版本迭代机制以及开源协议的重要性。
2.  **主流开源技术掌握**：熟练掌握了SSM框架的现代实现（Spring Boot）及Vue 3前端框架，具备了全栈开发B/S架构企业级应用的能力，能够独立完成从数据库设计到前后端代码编写的完整流程。
3.  **面向对象程序设计巩固**：在后端开发中，通过设计Entity、DTO、VO等类，以及Service接口与实现类的分离，深入实践了面向对象的设计原则（如单一职责、接口隔离、开闭原则），提升了代码的可读性和可扩展性。
4.  **问题分析与解决能力**：通过排查编译错误、接口404、样式编译失败等问题，锻炼了利用日志分析问题、查阅开源文档、使用调试工具（Debugger）解决问题的能力，这是成为一名合格工程师的关键素质。

### 5. 结论

本次课程设计成功实现了一个功能完善、架构合理的宠物领养管理系统，圆满完成了《开源软件生态与开发技术》课程的各项要求。系统界面友好，逻辑清晰，代码规范，达到了预期的设计目标。
在技术层面，成功整合了Spring Boot与Vue 3两大主流开源技术栈，解决了跨域资源共享（CORS）、JWT无状态鉴权、文件上传与回显等常见Web开发难题。在工程层面，通过解决Lombok兼容性、接口对齐等实际问题，积累了宝贵的调试经验和团队协作经验。
未来，该系统还有广阔的优化空间。例如，可以引入WebSocket技术实现用户与管理员之间的实时在线聊天功能，提升沟通效率；或者引入基于协同过滤的推荐算法，根据用户的浏览记录和点赞行为推荐感兴趣的宠物，进一步提升用户体验和领养成功率。

---

## 参考文献

[1] 沃尔斯 (Craig Walls). Spring实战(第5版)[M]. 人民邮电出版社, 2020.
[2] 尤雨溪. Vue.js设计与实现[M]. 人民邮电出版社, 2022.
[3] 阿里巴巴Java开发手册(黄山版)[S]. 2022.
[4] GB/T 7714-2015. 信息与文献 参考文献著录规则[S]. 北京: 中国标准出版社, 2015.

---

## 附录

**附录A：关键代码实现**

**1. Spring Security 配置类 (SecurityConfig.java)**
**代码位置**：`pet-adoption-backend/src/main/java/com/whu/pet/config/SecurityConfig.java`
```java
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable() // 禁用CSRF，因为使用JWT
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 禁用Session
            .and()
            .authorizeRequests()
            .antMatchers("/api/auth/**").permitAll() // 放行认证接口
            .antMatchers(HttpMethod.GET, "/api/pets/**", "/api/announcements/**").permitAll() // 放行公共读接口
            .anyRequest().authenticated() // 其他接口需认证
            .and()
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // 添加JWT过滤器
        return http.build();
    }
}
```

**2. MyBatis-Plus 分页配置 (MybatisPlusConfig.java)**
**代码位置**：`pet-adoption-backend/src/main/java/com/whu/pet/config/MybatisPlusConfig.java`
```java
@Configuration
@MapperScan("com.whu.pet.mapper")
public class MybatisPlusConfig {
    /**
     * 添加分页插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
```

**3. 全局异常处理器 (GlobalExceptionHandler.java)**
**代码位置**：`pet-adoption-backend/src/main/java/com/whu/pet/exception/GlobalExceptionHandler.java`
```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 处理自定义业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        log.error("业务异常：{}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return Result.error(400, message);
    }
}
```

**4. 前端 Axios 请求封装 (request.js)**
**代码位置**：`pet-adoption-frontend/src/utils/request.js`
```javascript
import axios from 'axios'
import { useUserStore } from '@/stores/user'

const service = axios.create({
  baseURL: '/api',
  timeout: 5000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers['Authorization'] = 'Bearer ' + userStore.token
    }
    return config
  },
  error => Promise.reject(error)
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.message || 'Error')
      return Promise.reject(new Error(res.message || 'Error'))
    } else {
      return res.data
    }
  },
  error => {
    ElMessage.error(error.message)
    return Promise.reject(error)
  }
)

export default service
```
