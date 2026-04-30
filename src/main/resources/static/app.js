const API_BASE_URL = "http://localhost:8080";
const USE_MOCK_DATA = new URLSearchParams(window.location.search).get("mock") === "1";

const PAGE_CONFIG = {
    dashboard: {
        title: "대시보드",
        searchPlaceholder: "대시보드는 통합 검색을 사용하지 않습니다."
    },
    customers: {
        title: "고객관리",
        searchPlaceholder: "이름 또는 이메일 검색"
    },
    products: {
        title: "상품관리",
        searchPlaceholder: "상품명 검색"
    },
    orders: {
        title: "주문관리",
        searchPlaceholder: "고객명 또는 주문번호 검색"
    },
    adminApprovals: {
        title: "관리자 관리",
        searchPlaceholder: "관리자 이름 또는 이메일 검색"
    }
};

const statusLabels = {
    ACTIVE: "활성",
    INACTIVE: "비활성",
    SUSPENDED: "정지",
    ON_SALE: "판매중",
    OUT_OF_STOCK: "품절",
    DISCONTINUED: "단종",
    PREPARING: "준비중",
    SHIPPING: "배송중",
    DELIVERED: "배송완료",
    CANCELLED: "취소",
    PENDING_APPROVAL: "승인 대기",
    REJECTED: "거부"
};

const roleLabels = {
    SUPER_ADMIN: "슈퍼 관리자",
    OPERATIONS_ADMIN: "운영 관리자",
    CS_ADMIN: "CS 관리자"
};

const statusClasses = {
    ACTIVE: "status-active",
    ON_SALE: "status-active",
    DELIVERED: "status-active",
    INACTIVE: "status-inactive",
    DISCONTINUED: "status-inactive",
    SUSPENDED: "status-suspended",
    CANCELLED: "status-suspended",
    REJECTED: "status-suspended",
    OUT_OF_STOCK: "status-warning",
    PENDING_APPROVAL: "status-warning",
    PREPARING: "status-info",
    SHIPPING: "status-purple"
};

const statusColors = {
    ACTIVE: "#16a34a",
    INACTIVE: "#9ca3af",
    SUSPENDED: "#dc2626"
};

const categoryColors = ["#2563eb", "#16a34a", "#f97316", "#7c3aed", "#0891b2", "#db2777"];

const state = {
    page: "dashboard",
    currentAdmin: null,
    filters: {
        customers: { page: 1, size: 10, sortBy: "createdAt", sortOrder: "desc" },
        products: { page: 1, size: 10, sortBy: "createdAt", sortOrder: "desc" },
        orders: { page: 1, size: 10, sortBy: "createdAt", sortOrder: "desc" },
        admins: { page: 1, size: 10, sortBy: "createdAt", sortOrder: "desc" }
    },
    lastLoaded: {
        dashboard: null,
        customers: null,
        products: null,
        orders: null,
        admins: null
    }
};

const mockStore = {
    admins: [
        { id: 1, name: "정하윤", email: "admin@sparta.com", tele: "010-0000-0000", role: "SUPER_ADMIN", status: "ACTIVE", createdAt: "2026-04-23T15:00:00", approvedAt: "2026-04-23T15:10:00", updatedAt: "2026-04-23T15:10:00" },
        { id: 3, name: "김유진", email: "operation@sparta.com", tele: "010-3333-3333", role: "OPERATIONS_ADMIN", status: "ACTIVE", createdAt: "2026-04-24T09:00:00", approvedAt: "2026-04-24T10:00:00", updatedAt: "2026-04-24T10:00:00" },
        { id: 4, name: "배수진", email: "bae.sujin@sparta.com", tele: "010-4444-4444", role: "CS_ADMIN", status: "INACTIVE", createdAt: "2026-04-25T09:00:00", approvedAt: "2026-04-25T10:00:00", updatedAt: "2026-04-26T10:00:00" },
        { id: 10, name: "정유찬", email: "jung.yuchan@sparta.com", tele: "010-5555-5555", role: "OPERATIONS_ADMIN", status: "SUSPENDED", createdAt: "2026-04-26T09:00:00", approvedAt: "2026-04-26T10:00:00", updatedAt: "2026-04-27T10:00:00" },
        { id: 13, name: "윤태준", email: "yoon.taejun@sparta.com", tele: "010-9000-0001", role: "OPERATIONS_ADMIN", status: "PENDING_APPROVAL", createdAt: "2026-04-30T09:00:00", approvedAt: null, updatedAt: "2026-04-30T09:00:00" },
        { id: 14, name: "한서윤", email: "han.seoyun@sparta.com", tele: "010-9000-0002", role: "CS_ADMIN", status: "PENDING_APPROVAL", createdAt: "2026-04-30T10:00:00", approvedAt: null, updatedAt: "2026-04-30T10:00:00" },
        { id: 15, name: "문도현", email: "moon.dohyun@sparta.com", tele: "010-9000-0003", role: "OPERATIONS_ADMIN", status: "REJECTED", rejectReason: "승인 기준 미충족", createdAt: "2026-04-29T10:00:00", approvedAt: null, updatedAt: "2026-04-29T11:00:00" }
    ],
    customers: [
        { id: 1, name: "김민수", tele: "010-1000-0001", email: "customer1@example.com", status: "ACTIVE", createdAt: "2026-04-30T09:00:00", updatedAt: "2026-04-30T09:00:00", totalOrderCount: 4, totalOrderAmount: 83000 },
        { id: 2, name: "이서연", tele: "010-1000-0002", email: "customer2@example.com", status: "ACTIVE", createdAt: "2026-04-30T09:00:00", updatedAt: "2026-04-30T09:00:00", totalOrderCount: 1, totalOrderAmount: 39000 },
        { id: 3, name: "박지훈", tele: "010-1000-0003", email: "customer3@example.com", status: "INACTIVE", createdAt: "2026-04-30T09:00:00", updatedAt: "2026-04-30T09:00:00", totalOrderCount: 0, totalOrderAmount: 0 },
        { id: 4, name: "최하은", tele: "010-1000-0004", email: "customer4@example.com", status: "SUSPENDED", createdAt: "2026-04-30T09:00:00", updatedAt: "2026-04-30T09:00:00", totalOrderCount: 0, totalOrderAmount: 0 }
    ],
    products: [
        { id: 1, adminName: "admin", adminEmail: "admin@sparta.com", name: "무선 키보드", category: "전자기기", price: 59000, stock: 100, status: "ON_SALE", createdAt: "2026-04-30T09:00:00", updatedAt: "2026-04-30T09:00:00", reviewSummary: { averageRating: 4.5, totalCount: 2, ratingDistribution: { "1": 0, "2": 0, "3": 0, "4": 1, "5": 1 }, latestReviews: [{ id: 1, orderNumber: "260430100000001101", customerName: "김민수", productName: "무선 키보드", rating: 5, content: "키감이 좋아요", createdAt: "2026-04-30T14:00:00", updatedAt: "2026-04-30T14:00:00" }] } },
        { id: 2, adminName: "admin", adminEmail: "admin@sparta.com", name: "블루투스 마우스", category: "전자기기", price: 39000, stock: 80, status: "ON_SALE", createdAt: "2026-04-30T09:00:00", updatedAt: "2026-04-30T09:00:00", reviewSummary: { averageRating: 4, totalCount: 1, ratingDistribution: { "1": 0, "2": 0, "3": 0, "4": 1, "5": 0 }, latestReviews: [] } },
        { id: 3, adminName: "admin", adminEmail: "admin@sparta.com", name: "텀블러", category: "생활용품", price: 18000, stock: 50, status: "ON_SALE", createdAt: "2026-04-30T09:00:00", updatedAt: "2026-04-30T09:00:00", reviewSummary: { averageRating: 0, totalCount: 0, ratingDistribution: {}, latestReviews: [] } },
        { id: 4, adminName: "admin", adminEmail: "admin@sparta.com", name: "USB-C 케이블", category: "전자기기", price: 9000, stock: 5, status: "ON_SALE", createdAt: "2026-04-30T09:00:00", updatedAt: "2026-04-30T09:00:00", reviewSummary: { averageRating: 0, totalCount: 0, ratingDistribution: {}, latestReviews: [] } },
        { id: 6, adminName: "admin", adminEmail: "admin@sparta.com", name: "게이밍 마우스패드", category: "전자기기", price: 15000, stock: 0, status: "OUT_OF_STOCK", createdAt: "2026-04-30T09:00:00", updatedAt: "2026-04-30T09:00:00", reviewSummary: { averageRating: 0, totalCount: 0, ratingDistribution: {}, latestReviews: [] } }
    ],
    orders: [
        { id: 1, orderNumber: "260430100000001101", customerName: "김민수", customerEmail: "customer1@example.com", productName: "무선 키보드", quantity: 2, totalPrice: 20000, createdAt: "2026-04-30T10:00:00", status: "PREPARING", adminName: "admin", adminEmail: "admin@sparta.com", adminRole: "SUPER_ADMIN" },
        { id: 2, orderNumber: "260430110000002102", customerName: "김민수", customerEmail: "customer1@example.com", productName: "무선 키보드", quantity: 1, totalPrice: 15000, createdAt: "2026-04-30T11:00:00", status: "SHIPPING", adminName: "admin", adminEmail: "admin@sparta.com", adminRole: "SUPER_ADMIN" },
        { id: 3, orderNumber: "260430120000003103", customerName: "김민수", customerEmail: "customer1@example.com", productName: "무선 키보드", quantity: 3, totalPrice: 24000, createdAt: "2026-04-30T12:00:00", status: "DELIVERED", adminName: "admin", adminEmail: "admin@sparta.com", adminRole: "SUPER_ADMIN" },
        { id: 5, orderNumber: "260430130000005105", customerName: "김민수", customerEmail: "customer1@example.com", productName: "무선 키보드", quantity: 1, totalPrice: 10000, createdAt: "2026-04-30T13:00:00", status: "CANCELLED", adminName: "admin", adminEmail: "admin@sparta.com", adminRole: "SUPER_ADMIN" }
    ]
};

document.addEventListener("DOMContentLoaded", () => {
    bindLoginEvents();
    bindShellEvents();
    initializePage();
});

function getAuthHeaders() {
    const accessToken = localStorage.getItem("accessToken");
    const headers = { Accept: "application/json" };

    if (accessToken) {
        headers.Authorization = `Bearer ${accessToken}`;
    }

    return headers;
}

async function apiFetch(path, options = {}) {
    const headers = {
        ...getAuthHeaders(),
        ...(options.body ? { "Content-Type": "application/json" } : {}),
        ...(options.headers || {})
    };

    const response = await fetch(`${API_BASE_URL}${path}`, {
        method: options.method || "GET",
        headers,
        body: options.body ? JSON.stringify(options.body) : undefined
    });

    const body = await parseApiResponse(response);

    if (!response.ok) {
        throw new Error(body.message || `API request failed: ${response.status}`);
    }

    return body.data;
}

async function parseApiResponse(response) {
    const text = await response.text();
    if (!text) {
        return {};
    }

    return JSON.parse(text.replace(/("orderNumber"\s*:\s*)(\d{16,})/g, '$1"$2"'));
}

async function loginAdmin(email, password) {
    const response = await fetch(`${API_BASE_URL}/admins/login`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            Accept: "application/json"
        },
        body: JSON.stringify({ email, password })
    });

    const body = await response.json().catch(() => ({}));

    if (!response.ok || !body.data?.accessToken) {
        throw new Error(body.message || "로그인에 실패했습니다.");
    }

    localStorage.setItem("accessToken", body.data.accessToken);
    return body.data;
}

async function signupAdmin(values) {
    if (USE_MOCK_DATA) {
        mockStore.admins.push({
            id: nextId(mockStore.admins),
            name: values.name,
            email: values.email,
            tele: values.tele,
            role: values.role,
            status: "PENDING_APPROVAL",
            createdAt: new Date().toISOString(),
            approvedAt: null,
            updatedAt: new Date().toISOString()
        });
        return { message: "관리자 회원가입이 완료되었습니다." };
    }

    const response = await fetch(`${API_BASE_URL}/admins/signup`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            Accept: "application/json"
        },
        body: JSON.stringify(values)
    });

    const body = await response.json().catch(() => ({}));

    if (!response.ok) {
        throw new Error(body.message || "관리자 가입 요청에 실패했습니다.");
    }

    return body;
}

async function loadCurrentAdmin() {
    const admin = USE_MOCK_DATA ? mockStore.admins[0] : await apiFetch("/admins/me");
    state.currentAdmin = admin;

    if (admin?.email) {
        localStorage.setItem("adminEmail", admin.email);
    }

    return admin;
}

async function loadDashboard() {
    try {
        const data = USE_MOCK_DATA ? getMockDashboard() : await apiFetch("/admin/dashboard");
        state.lastLoaded.dashboard = data;
        renderSummaryCards(data);
        renderDashboardWidgets(data);
        renderRecentActivities(data);
        renderCustomerStatusChart(data);
        renderProductCategoryBars(data);
        renderReviewRatingBars(data);
        return data;
    } catch (error) {
        handleLoadError("대시보드 조회 실패", error);
        renderSummaryCards(null);
        renderDashboardWidgets(null);
        renderRecentActivities(null);
        renderCustomerStatusChart(null);
        renderProductCategoryBars(null);
        renderReviewRatingBars(null);
        return null;
    }
}

async function loadCustomers(params = {}) {
    try {
        state.filters.customers = { ...state.filters.customers, ...params };
        const data = USE_MOCK_DATA
            ? getMockPage(filterCustomers(mockStore.customers, state.filters.customers), state.filters.customers)
            : await apiFetch(`/admin/customers${createQueryString(state.filters.customers)}`);

        state.lastLoaded.customers = data;
        renderCustomers(data.content || []);
        renderPageMeta("customersPageMeta", data, "customers");
        updateSortIndicators("customers");
        return data;
    } catch (error) {
        handleLoadError("고객 목록 조회 실패", error);
        renderCustomers([]);
        return null;
    }
}

async function loadProducts(params = {}) {
    try {
        state.filters.products = { ...state.filters.products, ...params };
        const data = USE_MOCK_DATA
            ? getMockPage(filterProducts(mockStore.products, state.filters.products), state.filters.products)
            : await apiFetch(`/admin/products${createQueryString(state.filters.products)}`);

        state.lastLoaded.products = data;
        renderProducts(data.content || []);
        renderPageMeta("productsPageMeta", data, "products");
        updateSortIndicators("products");
        return data;
    } catch (error) {
        handleLoadError("상품 목록 조회 실패", error);
        renderProducts([]);
        return null;
    }
}

async function loadOrders(params = {}) {
    try {
        state.filters.orders = { ...state.filters.orders, ...params };
        const data = USE_MOCK_DATA
            ? getMockPage(filterOrders(mockStore.orders, state.filters.orders), state.filters.orders)
            : await apiFetch(`/admin/orders${createQueryString(state.filters.orders)}`);

        state.lastLoaded.orders = data;
        renderOrders(data.content || []);
        renderPageMeta("ordersPageMeta", data, "orders");
        updateSortIndicators("orders");
        return data;
    } catch (error) {
        handleLoadError("주문 목록 조회 실패", error);
        renderOrders([]);
        return null;
    }
}

async function loadAdmins(params = {}) {
    if (!isSuperAdmin()) {
        renderAdmins([]);
        showNotice("슈퍼 관리자만 관리자 관리 화면에 접근할 수 있습니다.", "error");
        return null;
    }

    try {
        state.filters.admins = { ...state.filters.admins, ...params };
        const data = USE_MOCK_DATA
            ? getMockPage(filterAdmins(mockStore.admins, state.filters.admins), state.filters.admins)
            : await apiFetch(`/admins${createQueryString(state.filters.admins)}`);

        state.lastLoaded.admins = data;
        renderAdmins(data.content || []);
        renderPageMeta("adminsPageMeta", data, "admins");
        renderAdminApprovalSummary(data);
        updateSortIndicators("admins");
        return data;
    } catch (error) {
        handleLoadError("관리자 목록 조회 실패", error);
        renderAdmins([]);
        renderAdminApprovalSummary(null);
        return null;
    }
}

function renderSummaryCards(data) {
    const container = document.getElementById("summaryCards");
    if (!container) {
        return;
    }

    const summary = data?.summary || {};
    const cards = [
        { title: "전체 관리자", value: summary.totalAdmins, icon: "A" },
        { title: "전체 고객", value: summary.totalCustomers, icon: "C" },
        { title: "등록 상품", value: summary.totalProducts, icon: "P" },
        { title: "전체 주문", value: summary.totalOrders, icon: "O" },
        { title: "오늘 주문", value: summary.todayOrders, icon: "T" },
        { title: "재고 부족", value: summary.lowStockProducts, icon: "S" },
        { title: "전체 리뷰", value: summary.totalReviews, icon: "R" },
        { title: "평균 평점", value: summary.averageRating, icon: "★", raw: true }
    ];

    container.replaceChildren(...cards.map((card) => createSummaryCard(
        card.title,
        card.raw ? formatDecimal(card.value) : formatNumber(card.value),
        card.icon
    )));
}

function renderCustomers(customers) {
    const tbody = document.getElementById("customersTableBody");
    if (!tbody) {
        return;
    }

    if (!customers || customers.length === 0) {
        tbody.replaceChildren(createEmptyRow("표시할 고객이 없습니다.", 9));
        return;
    }

    tbody.replaceChildren(...customers.map((customer) => {
        const tr = document.createElement("tr");
        tr.append(
            createCell(customer.id),
            createCell(customer.name),
            createCell(customer.tele),
            createCell(customer.email),
            createStatusCell(customer.status),
            createCell(formatDate(customer.createdAt)),
            createCell(formatNumber(customer.totalOrderCount)),
            createCell(formatPrice(customer.totalOrderAmount)),
            createActionCell([
                actionButton("상세", "customer-detail", customer.id),
                actionButton("수정", "customer-edit", customer.id),
                actionButton("상태", "customer-status", customer.id),
                actionButton("삭제", "customer-delete", customer.id, "danger")
            ])
        );
        return tr;
    }));
}

function renderProducts(products) {
    const tbody = document.getElementById("productsTableBody");
    if (!tbody) {
        return;
    }

    if (!products || products.length === 0) {
        tbody.replaceChildren(createEmptyRow("표시할 상품이 없습니다.", 9));
        return;
    }

    tbody.replaceChildren(...products.map((product) => {
        const tr = document.createElement("tr");
        tr.append(
            createCell(product.id),
            createCell(product.name),
            createCell(product.category),
            createCell(formatPrice(product.price)),
            createCell(formatNumber(product.stock)),
            createStatusCell(product.status),
            createCell(product.adminName),
            createCell(formatDate(product.createdAt)),
            createActionCell([
                actionButton("상세", "product-detail", product.id),
                actionButton("수정", "product-edit", product.id),
                actionButton("재고", "product-stock", product.id),
                actionButton("상태", "product-status", product.id),
                actionButton("리뷰", "product-reviews", product.id),
                actionButton("삭제", "product-delete", product.id, "danger")
            ])
        );
        return tr;
    }));
}

function renderOrders(orders) {
    const tbody = document.getElementById("ordersTableBody");
    if (!tbody) {
        return;
    }

    if (!orders || orders.length === 0) {
        tbody.replaceChildren(createEmptyRow("표시할 주문이 없습니다.", 9));
        return;
    }

    tbody.replaceChildren(...orders.map((order) => {
        const tr = document.createElement("tr");
        const actions = [
            actionButton("상세", "order-detail", order.id)
        ];

        if (order.status === "PREPARING") {
            actions.push(actionButton("배송중", "order-next", order.id));
            actions.push(actionButton("취소", "order-cancel", order.id, "danger"));
        } else if (order.status === "SHIPPING") {
            actions.push(actionButton("배송완료", "order-next", order.id));
        }

        tr.append(
            createCell(order.id),
            createCell(order.orderNumber),
            createCell(order.customerName),
            createCell(order.productName),
            createCell(formatNumber(order.quantity)),
            createCell(formatPrice(order.totalPrice)),
            createCell(formatDate(order.createdAt)),
            createStatusCell(order.status),
            createActionCell(actions)
        );
        return tr;
    }));
}

function renderAdmins(admins) {
    const tbody = document.getElementById("adminsTableBody");
    if (!tbody) {
        return;
    }

    if (!admins || admins.length === 0) {
        tbody.replaceChildren(createEmptyRow("표시할 관리자 가입 요청이 없습니다.", 9));
        return;
    }

    tbody.replaceChildren(...admins.map((admin) => {
        const tr = document.createElement("tr");
        const actions = [
            actionButton("상세", "admin-detail", admin.id),
            actionButton("수정", "admin-edit", admin.id),
            actionButton("역할", "admin-role", admin.id),
            actionButton("상태", "admin-status", admin.id)
        ];

        if (admin.status === "PENDING_APPROVAL") {
            actions.push(actionButton("승인", "admin-approve", admin.id));
            actions.push(actionButton("거부", "admin-reject", admin.id, "danger"));
        }

        if (admin.role !== "SUPER_ADMIN") {
            actions.push(actionButton("삭제", "admin-delete", admin.id, "danger"));
        }

        tr.append(
            createCell(admin.id),
            createCell(admin.name),
            createCell(admin.email),
            createCell(admin.tele),
            createCell(roleLabels[admin.role] || admin.role),
            createStatusCell(admin.status),
            createCell(formatDate(admin.createdAt)),
            createCell(formatDate(admin.approvedAt)),
            createActionCell(actions)
        );
        return tr;
    }));
}

function renderAdminApprovalSummary(data) {
    const container = document.getElementById("adminApprovalSummary");
    if (!container) {
        return;
    }

    const source = data?.content || [];
    const pending = source.filter((admin) => admin.status === "PENDING_APPROVAL").length;
    const active = source.filter((admin) => admin.status === "ACTIVE").length;
    const inactive = source.filter((admin) => ["INACTIVE", "SUSPENDED", "REJECTED"].includes(admin.status)).length;

    container.replaceChildren(
        createSummaryCard("검색 결과", formatNumber(data?.totalElements), "T"),
        createSummaryCard("현재 페이지 승인 대기", formatNumber(pending), "P"),
        createSummaryCard("현재 페이지 활성", formatNumber(active), "A"),
        createSummaryCard("현재 페이지 제한", formatNumber(inactive), "S")
    );
}

function renderRecentActivities(data) {
    const container = document.getElementById("recentActivities");
    if (!container) {
        return;
    }

    const orders = data?.recentOrders || [];
    if (orders.length === 0) {
        container.replaceChildren(createEmptyState("최근 주문이 없습니다."));
        return;
    }

    container.replaceChildren(...orders.slice(0, 5).map((order) => {
        const item = document.createElement("div");
        item.className = "activity-item";

        const top = document.createElement("div");
        top.className = "activity-top";

        const title = document.createElement("div");
        title.className = "activity-title";
        title.textContent = `주문 ${order.orderNumber ?? "-"}`;

        top.append(title, createStatusBadge(order.status));

        const meta = document.createElement("div");
        meta.className = "activity-meta";
        meta.textContent = `${order.customerName ?? "-"} · ${order.productName ?? "-"}`;

        const amount = document.createElement("div");
        amount.className = "activity-price";
        amount.textContent = formatPrice(order.amount);

        item.append(top, meta, amount);
        return item;
    }));
}

function renderCustomerStatusChart(data) {
    const container = document.getElementById("customerStatusChart");
    if (!container) {
        return;
    }

    const statuses = data?.charts?.customerStatuses || [];
    if (statuses.length === 0) {
        container.replaceChildren(createEmptyState("고객 상태 데이터가 없습니다."));
        return;
    }

    const total = statuses.reduce((sum, item) => sum + Number(item.count || 0), 0);
    const donut = document.createElement("div");
    donut.className = "donut";
    donut.dataset.total = formatNumber(total);
    donut.style.background = createDonutGradient(statuses, total);

    const legend = document.createElement("div");
    legend.className = "legend";

    statuses.forEach((item) => {
        const row = document.createElement("div");
        row.className = "legend-item";
        row.innerHTML = `
            <span class="legend-left">
                <span class="legend-dot" style="background:${getStatusChartColor(item.status)}"></span>
                <span class="legend-name">${escapeHtml(statusLabels[item.status] || item.status)}</span>
            </span>
            <strong class="legend-count">${formatNumber(item.count)}</strong>
        `;
        legend.append(row);
    });

    const wrap = document.createElement("div");
    wrap.className = "donut-wrap";
    wrap.append(donut, legend);
    container.replaceChildren(wrap);
}

function renderProductCategoryBars(data) {
    const container = document.getElementById("productCategoryBars");
    if (!container) {
        return;
    }

    const categories = data?.charts?.productCategories || [];
    renderBars(container, categories, "category", "상품 카테고리 데이터가 없습니다.");
}

function renderReviewRatingBars(data) {
    const container = document.getElementById("reviewRatingBars");
    if (!container) {
        return;
    }

    const ratings = (data?.charts?.reviewRatings || []).map((item) => ({
        rating: `${item.rating}점`,
        count: item.count
    }));
    renderBars(container, ratings, "rating", "리뷰 평점 데이터가 없습니다.");
}

function renderDashboardWidgets(data) {
    const container = document.getElementById("dashboardWidgets");
    if (!container) {
        return;
    }

    const widgets = data?.widgets || {};
    const cards = [
        ["총 매출", formatPrice(widgets.totalSales)],
        ["오늘 매출", formatPrice(widgets.todaySales)],
        ["준비중 주문", formatNumber(widgets.preparingOrders)],
        ["배송중 주문", formatNumber(widgets.shippingOrders)],
        ["배송완료 주문", formatNumber(widgets.deliveredOrders)],
        ["품절 상품", formatNumber(widgets.outOfStockProducts)]
    ];

    container.replaceChildren(...cards.map(([label, value]) => {
        const item = document.createElement("div");
        item.className = "metric-item";
        item.innerHTML = `<span>${label}</span><strong>${value}</strong>`;
        return item;
    }));
}

function formatDate(value) {
    return value ? String(value).slice(0, 10) : "-";
}

function formatPrice(value) {
    return `${Number(value || 0).toLocaleString("ko-KR")}원`;
}

function createStatusBadge(status) {
    const badge = document.createElement("span");
    badge.className = `status-badge ${statusClasses[status] || "status-inactive"}`;
    badge.textContent = statusLabels[status] || status || "-";
    return badge;
}

function bindLoginEvents() {
    document.getElementById("showLoginForm").addEventListener("click", () => setAuthMode("login"));
    document.getElementById("showSignupForm").addEventListener("click", () => setAuthMode("signup"));

    document.getElementById("loginForm").addEventListener("submit", async (event) => {
        event.preventDefault();

        const emailInput = document.getElementById("adminEmail");
        const passwordInput = document.getElementById("adminPassword");
        const loginButton = document.getElementById("loginButton");
        const loginError = document.getElementById("loginError");
        const authMessage = document.getElementById("authMessage");

        loginError.hidden = true;
        authMessage.hidden = true;
        loginButton.disabled = true;
        loginButton.textContent = "로그인 중...";

        try {
            const email = emailInput.value.trim();
            await loginAdmin(email, passwordInput.value);
            await loadCurrentAdmin();
            localStorage.setItem("adminEmail", email);
            window.history.replaceState(null, "", "/");
            showDashboardView();
            await setPage("dashboard");
        } catch (error) {
            console.error("로그인 실패", error);
            localStorage.removeItem("accessToken");
            localStorage.removeItem("adminEmail");
            state.currentAdmin = null;
            loginError.textContent = error.message || "이메일 또는 비밀번호를 확인해주세요.";
            loginError.hidden = false;
        } finally {
            loginButton.disabled = false;
            loginButton.textContent = "로그인";
        }
    });

    document.getElementById("signupForm").addEventListener("submit", async (event) => {
        event.preventDefault();

        const form = event.currentTarget;
        const signupButton = document.getElementById("signupButton");
        const signupError = document.getElementById("signupError");
        const authMessage = document.getElementById("authMessage");

        signupError.hidden = true;
        authMessage.hidden = true;
        signupButton.disabled = true;
        signupButton.textContent = "요청 중...";

        try {
            await signupAdmin(formValues(form));
            form.reset();
            setAuthMode("login");
            authMessage.textContent = "관리자 가입 요청이 접수되었습니다. 슈퍼 관리자 승인 후 로그인할 수 있습니다.";
            authMessage.hidden = false;
        } catch (error) {
            console.error("관리자 가입 실패", error);
            signupError.textContent = error.message || "가입 요청 정보를 확인해주세요.";
            signupError.hidden = false;
        } finally {
            signupButton.disabled = false;
            signupButton.textContent = "가입 요청";
        }
    });

    document.getElementById("logoutButton").addEventListener("click", () => {
        localStorage.removeItem("accessToken");
        localStorage.removeItem("adminEmail");
        state.currentAdmin = null;
        setAuthMode("login");
        showLoginView();
    });
}

function bindShellEvents() {
    document.querySelectorAll(".nav-item[data-page]").forEach((item) => {
        item.addEventListener("click", (event) => {
            event.preventDefault();
            setPage(item.dataset.page);
        });
    });

    document.getElementById("globalSearch").addEventListener("keydown", (event) => {
        if (event.key !== "Enter" || state.page === "dashboard") {
            return;
        }

        const keyword = event.currentTarget.value.trim();
        if (state.page === "customers") {
            document.getElementById("customerKeyword").value = keyword;
            loadCustomers({ keyword, page: 1 });
        } else if (state.page === "products") {
            document.getElementById("productKeyword").value = keyword;
            loadProducts({ keyword, page: 1 });
        } else if (state.page === "orders") {
            document.getElementById("orderKeyword").value = keyword;
            loadOrders({ keyword, page: 1 });
        } else if (state.page === "adminApprovals") {
            document.getElementById("adminKeyword").value = keyword;
            loadAdmins({ keyword, page: 1 });
        }
    });

    document.getElementById("pageContent").addEventListener("click", handlePageClick);
    document.getElementById("pageContent").addEventListener("submit", handlePageSubmit);
    document.getElementById("modalRoot").addEventListener("click", (event) => {
        const reviewDeleteButton = event.target.closest("[data-review-delete]");
        if (reviewDeleteButton) {
            deleteReview(Number(reviewDeleteButton.dataset.productId), Number(reviewDeleteButton.dataset.reviewId));
            return;
        }

        if (event.target.matches("[data-modal-close], .modal-backdrop")) {
            closeModal();
        }
    });
    document.getElementById("modalRoot").addEventListener("submit", handleModalSubmit);
}

async function initializePage() {
    const accessToken = localStorage.getItem("accessToken");

    if (USE_MOCK_DATA) {
        await loadCurrentAdmin();
        showDashboardView();
        await setPage("dashboard");
        return;
    }

    if (accessToken) {
        try {
            await loadCurrentAdmin();
            showDashboardView();
            await setPage("dashboard");
        } catch (error) {
            console.error("관리자 프로필 조회 실패", error);
            localStorage.removeItem("accessToken");
            localStorage.removeItem("adminEmail");
            showLoginView();
        }
        return;
    }

    showLoginView();
}

async function setPage(page) {
    if (page === "adminApprovals" && !isSuperAdmin()) {
        showNotice("슈퍼 관리자만 관리자 관리 화면에 접근할 수 있습니다.", "error");
        page = "dashboard";
    }

    state.page = PAGE_CONFIG[page] ? page : "dashboard";
    const config = PAGE_CONFIG[state.page];

    document.getElementById("pageTitle").textContent = config.title;
    document.getElementById("globalSearch").value = "";
    document.getElementById("globalSearch").placeholder = config.searchPlaceholder;
    document.getElementById("globalSearch").disabled = state.page === "dashboard";

    document.querySelectorAll(".nav-item[data-page]").forEach((item) => {
        const active = item.dataset.page === state.page;
        item.classList.toggle("active", active);
        item.setAttribute("aria-current", active ? "page" : "false");
    });

    if (state.page === "dashboard") {
        renderDashboardPage();
        await loadDashboard();
    } else if (state.page === "customers") {
        renderCustomersPage();
        await loadCustomers();
    } else if (state.page === "products") {
        renderProductsPage();
        await loadProducts();
    } else if (state.page === "orders") {
        renderOrdersPage();
        await loadOrders();
    } else if (state.page === "adminApprovals") {
        renderAdminApprovalsPage();
        await loadAdmins();
    }
}

function showLoginView() {
    document.getElementById("loginView").hidden = false;
    document.getElementById("dashboardView").hidden = true;
    document.getElementById("authBanner").hidden = false;
    document.getElementById("adminEmail").focus();
}

function showDashboardView() {
    updateProfile();
    updateAdminNavVisibility();
    document.getElementById("loginView").hidden = true;
    document.getElementById("dashboardView").hidden = false;
    document.getElementById("authBanner").hidden = true;
}

function updateProfile() {
    document.getElementById("profileName").textContent = state.currentAdmin?.name || "관리자";
    document.getElementById("profileEmail").textContent = state.currentAdmin?.email || localStorage.getItem("adminEmail") || "Admin";
}

function updateAdminNavVisibility() {
    document.getElementById("adminApprovalNav").hidden = !isSuperAdmin();
}

function isSuperAdmin() {
    return state.currentAdmin?.role === "SUPER_ADMIN";
}

function setAuthMode(mode) {
    const isSignup = mode === "signup";

    document.getElementById("loginForm").hidden = isSignup;
    document.getElementById("signupForm").hidden = !isSignup;
    document.getElementById("showLoginForm").classList.toggle("active", !isSignup);
    document.getElementById("showSignupForm").classList.toggle("active", isSignup);
    document.getElementById("authTitle").textContent = isSignup ? "관리자 가입 요청" : "관리자 로그인";
    document.getElementById("authDescription").textContent = isSignup
        ? "가입 요청은 승인 대기 상태로 등록되며, 슈퍼 관리자 승인 후 로그인할 수 있습니다."
        : "관리자 이메일과 비밀번호를 입력하면 운영 대시보드로 이동합니다.";
    document.getElementById("loginError").hidden = true;
    document.getElementById("signupError").hidden = true;
}

function renderDashboardPage() {
    document.getElementById("pageContent").innerHTML = `
        <section id="summaryCards" class="summary-grid dashboard-summary" aria-label="대시보드 요약"></section>
        <div class="content-grid dashboard-layout">
            <section class="main-column">
                <article class="panel">
                    <div class="panel-header">
                        <div>
                            <h2>운영 지표</h2>
                            <p>매출, 주문 상태, 재고 상황을 한 번에 확인합니다.</p>
                        </div>
                        <button class="secondary-button" type="button" data-action="dashboard-refresh">새로고침</button>
                    </div>
                    <div id="dashboardWidgets" class="metric-grid"></div>
                </article>
                <article class="panel compact-panel">
                    <div class="panel-title-row">
                        <h2>최근 주문</h2>
                        <span>대시보드 API</span>
                    </div>
                    <div id="recentActivities" class="activity-list"></div>
                </article>
            </section>
            <aside class="right-column">
                <article class="panel compact-panel">
                    <div class="panel-title-row"><h2>고객 상태 분포</h2><span>상태별</span></div>
                    <div id="customerStatusChart" class="chart-card"></div>
                </article>
                <article class="panel compact-panel">
                    <div class="panel-title-row"><h2>상품 카테고리 비율</h2><span>카테고리</span></div>
                    <div id="productCategoryBars" class="bar-list"></div>
                </article>
                <article class="panel compact-panel">
                    <div class="panel-title-row"><h2>리뷰 평점 분포</h2><span>평점</span></div>
                    <div id="reviewRatingBars" class="bar-list"></div>
                </article>
            </aside>
        </div>
    `;
}

function renderCustomersPage() {
    document.getElementById("pageContent").innerHTML = `
        <article class="panel">
            <div class="panel-header page-panel-header">
                <div>
                    <h2>고객 목록</h2>
                    <p>고객 조회, 상세 확인, 정보 수정, 상태 변경, 삭제를 처리합니다.</p>
                </div>
                <form class="toolbar-form" data-form="customer-filter">
                    <input id="customerKeyword" name="keyword" type="search" placeholder="이름 또는 이메일">
                    <select id="customerStatus" name="status">
                        <option value="">전체 상태</option>
                        ${customerStatusOptions()}
                    </select>
                    <button type="submit">검색</button>
                    <button type="button" class="secondary-button" data-action="customer-reset">초기화</button>
                </form>
            </div>
            <div class="table-wrap">
                <table class="data-table customers-table">
                    ${tableColGroup([90, 150, 150, 260, 110, 130, 110, 140, 260])}
                    <thead>
                        <tr>
                            <th>고객번호</th>${sortableHeader("customers", "name", "이름")}<th>연락처</th>${sortableHeader("customers", "email", "이메일")}<th>상태</th>
                            ${sortableHeader("customers", "createdAt", "가입일")}<th>총 주문 수</th><th>총 구매 금액</th><th>관리</th>
                        </tr>
                    </thead>
                    <tbody id="customersTableBody"></tbody>
                </table>
            </div>
            <div id="customersPageMeta" class="page-meta"></div>
        </article>
    `;
}

function renderProductsPage() {
    document.getElementById("pageContent").innerHTML = `
        <section class="management-grid">
            <article class="panel">
                <div class="panel-header">
                    <div>
                        <h2>상품 등록</h2>
                        <p>상품명, 카테고리, 가격, 재고, 판매상태를 입력합니다.</p>
                    </div>
                </div>
                <form class="entity-form" data-form="product-create">
                    <label>상품명<input name="name" required maxlength="30"></label>
                    <label>카테고리<input name="category" required maxlength="30"></label>
                    <label>가격<input name="price" type="number" min="0" required></label>
                    <label>재고<input name="stock" type="number" min="0" required></label>
                    <label>상태<select name="status" required>${productStatusOptions("ON_SALE")}</select></label>
                    <button type="submit">상품 등록</button>
                </form>
            </article>
            <article class="panel">
                <div class="panel-header page-panel-header">
                    <div>
                        <h2>상품 목록</h2>
                        <p>상품 조회, 상세, 정보 수정, 재고/상태 변경, 삭제를 처리합니다.</p>
                    </div>
                    <form class="toolbar-form" data-form="product-filter">
                        <input id="productKeyword" name="keyword" type="search" placeholder="상품명">
                        <input id="productCategory" name="category" placeholder="카테고리">
                        <select id="productStatus" name="status">
                            <option value="">전체 상태</option>
                            ${productStatusOptions()}
                        </select>
                        <button type="submit">검색</button>
                        <button type="button" class="secondary-button" data-action="product-reset">초기화</button>
                    </form>
                </div>
                <div class="table-wrap">
                    <table class="data-table products-table">
                        ${tableColGroup([90, 230, 140, 120, 90, 120, 130, 130, 300])}
                        <thead>
                            <tr>
                                <th>상품코드</th><th>상품명</th><th>카테고리</th>${sortableHeader("products", "price", "가격")}${sortableHeader("products", "stock", "재고")}
                                <th>판매상태</th><th>등록 관리자</th>${sortableHeader("products", "createdAt", "등록일")}<th>관리</th>
                            </tr>
                        </thead>
                        <tbody id="productsTableBody"></tbody>
                    </table>
                </div>
                <div id="productsPageMeta" class="page-meta"></div>
            </article>
        </section>
    `;
}

function renderOrdersPage() {
    document.getElementById("pageContent").innerHTML = `
        <section class="management-grid">
            <article class="panel">
                <div class="panel-header">
                    <div>
                        <h2>주문 생성</h2>
                        <p>고객 ID, 상품 ID, 수량을 지정해 CS 주문을 생성합니다.</p>
                    </div>
                </div>
                <form class="entity-form" data-form="order-create">
                    <label>고객 ID<input name="customerId" type="number" min="1" required></label>
                    <label>상품 ID<input name="productId" type="number" min="1" required></label>
                    <label>수량<input name="quantity" type="number" min="1" required></label>
                    <button type="submit">주문 생성</button>
                </form>
            </article>
            <article class="panel">
                <div class="panel-header page-panel-header">
                    <div>
                        <h2>주문 목록</h2>
                        <p>주문 조회, 상세, 배송 상태 변경, 취소를 처리합니다.</p>
                    </div>
                    <form class="toolbar-form" data-form="order-filter">
                        <input id="orderKeyword" name="keyword" type="search" placeholder="고객명 또는 주문번호">
                        <select id="orderStatus" name="status">
                            <option value="">전체 상태</option>
                            ${orderStatusOptions()}
                        </select>
                        <button type="submit">검색</button>
                        <button type="button" class="secondary-button" data-action="order-reset">초기화</button>
                    </form>
                </div>
                <div class="table-wrap">
                    <table class="data-table orders-table">
                        ${tableColGroup([60, 230, 100, 220, 80, 130, 120, 110, 280])}
                        <thead>
                            <tr>
                                <th>ID</th><th>주문번호</th><th>고객명</th><th>상품명</th>${sortableHeader("orders", "quantity", "수량")}
                                ${sortableHeader("orders", "totalPrice", "총액")}${sortableHeader("orders", "createdAt", "주문일")}<th>상태</th><th>관리</th>
                            </tr>
                        </thead>
                        <tbody id="ordersTableBody"></tbody>
                    </table>
                </div>
                <div id="ordersPageMeta" class="page-meta"></div>
            </article>
        </section>
    `;
}

function renderAdminApprovalsPage() {
    document.getElementById("pageContent").innerHTML = `
        <section id="adminApprovalSummary" class="summary-grid approval-summary" aria-label="관리자 관리 요약"></section>
        <article class="panel">
            <div class="panel-header page-panel-header">
                <div>
                    <h2>관리자 관리</h2>
                    <p>관리자 계정 조회, 상세, 수정, 역할/상태 변경, 삭제, 가입 승인/거부를 처리합니다.</p>
                </div>
                <form class="toolbar-form" data-form="admin-filter">
                    <input id="adminKeyword" name="keyword" type="search" placeholder="이름 또는 이메일">
                    <select id="adminRole" name="role">
                        <option value="">전체 권한</option>
                        ${adminRoleOptions()}
                    </select>
                    <select id="adminStatus" name="status">
                        <option value="">전체 상태</option>
                        ${adminStatusOptions()}
                    </select>
                    <button type="submit">검색</button>
                    <button type="button" class="secondary-button" data-action="admin-reset">초기화</button>
                </form>
            </div>
            <div class="table-wrap">
                <table class="data-table admins-table">
                    ${tableColGroup([100, 140, 250, 150, 150, 130, 130, 130, 360])}
                    <thead>
                        <tr>
                            <th>관리자번호</th>${sortableHeader("admins", "name", "이름")}${sortableHeader("admins", "email", "이메일")}<th>연락처</th>${sortableHeader("admins", "role", "권한")}
                            ${sortableHeader("admins", "status", "상태")}${sortableHeader("admins", "createdAt", "생성일")}${sortableHeader("admins", "approvedAt", "승인일")}<th>관리</th>
                        </tr>
                    </thead>
                    <tbody id="adminsTableBody"></tbody>
                </table>
            </div>
            <div id="adminsPageMeta" class="page-meta"></div>
        </article>
    `;
}

async function handlePageClick(event) {
    const button = event.target.closest("[data-action]");
    if (!button) {
        return;
    }

    const action = button.dataset.action;
    const id = Number(button.dataset.id);

    if (action === "dashboard-refresh") await loadDashboard();
    if (action === "customer-reset") resetFilters("customers", loadCustomers);
    if (action === "product-reset") resetFilters("products", loadProducts);
    if (action === "order-reset") resetFilters("orders", loadOrders);
    if (action === "admin-reset") resetFilters("admins", loadAdmins);
    if (action === "page-change") await changeListPage(button.dataset.pageKey, Number(button.dataset.pageNumber));
    if (action === "sort-list") await changeSort(button.dataset.sortKey, button.dataset.sortBy);
    if (action === "customer-detail") openCustomerDetail(id);
    if (action === "customer-edit") openCustomerEdit(id);
    if (action === "customer-status") openCustomerStatus(id);
    if (action === "customer-delete") deleteCustomer(id);
    if (action === "product-detail") openProductDetail(id);
    if (action === "product-edit") openProductEdit(id);
    if (action === "product-stock") openProductStock(id);
    if (action === "product-status") openProductStatus(id);
    if (action === "product-reviews") openProductReviews(id);
    if (action === "product-delete") deleteProduct(id);
    if (action === "order-detail") openOrderDetail(id);
    if (action === "order-next") updateOrderToNextStatus(id);
    if (action === "order-cancel") openOrderCancel(id);
    if (action === "admin-detail") openAdminDetail(id);
    if (action === "admin-edit") openAdminEdit(id);
    if (action === "admin-role") openAdminRole(id);
    if (action === "admin-status") openAdminStatus(id);
    if (action === "admin-approve") approveAdmin(id);
    if (action === "admin-reject") openAdminReject(id);
    if (action === "admin-delete") deleteAdmin(id);
}

async function handlePageSubmit(event) {
    const form = event.target;
    const formType = form.dataset.form;
    if (!formType) {
        return;
    }

    event.preventDefault();
    const values = formValues(form);

    if (formType === "customer-filter") {
        await loadCustomers({ keyword: values.keyword, status: values.status, page: 1 });
    }
    if (formType === "product-filter") {
        await loadProducts({ keyword: values.keyword, category: values.category, status: values.status, page: 1 });
    }
    if (formType === "product-create") {
        await createProduct(values, form);
    }
    if (formType === "order-filter") {
        await loadOrders({ keyword: values.keyword, status: values.status, page: 1 });
    }
    if (formType === "order-create") {
        await createOrder(values, form);
    }
    if (formType === "admin-filter") {
        await loadAdmins({ keyword: values.keyword, role: values.role, status: values.status, page: 1 });
    }
}

async function handleModalSubmit(event) {
    const form = event.target;
    const formType = form.dataset.form;
    if (!formType) {
        return;
    }

    event.preventDefault();
    const id = Number(form.dataset.id);
    const values = formValues(form);

    if (formType === "customer-edit") await updateCustomer(id, values);
    if (formType === "customer-status") await updateCustomerStatus(id, values.status);
    if (formType === "product-edit") await updateProduct(id, values);
    if (formType === "product-stock") await updateProductStock(id, values.stock);
    if (formType === "product-status") await updateProductStatus(id, values.status);
    if (formType === "order-cancel") await cancelOrder(id, values.cancelReason);
    if (formType === "admin-edit") await updateAdmin(id, values);
    if (formType === "admin-role") await updateAdminRole(id, values.role);
    if (formType === "admin-status") await updateAdminStatus(id, values.status);
    if (formType === "admin-reject") await rejectAdmin(id, values.rejectReason);
}

async function openCustomerDetail(id) {
    const customer = await getCustomer(id);
    openModal("고객 상세", detailList([
        ["고객번호", customer.id],
        ["이름", customer.name],
        ["이메일", customer.email],
        ["연락처", customer.tele],
        ["상태", statusLabels[customer.status] || customer.status],
        ["가입일", formatDate(customer.createdAt)],
        ["총 주문 수", formatNumber(customer.totalOrderCount)],
        ["총 구매 금액", formatPrice(customer.totalOrderAmount)]
    ]));
}

async function openCustomerEdit(id) {
    const customer = await getCustomer(id);
    openModal("고객 정보 수정", `
        <form class="entity-form modal-form" data-form="customer-edit" data-id="${customer.id}">
            <label>이름<input name="name" value="${escapeAttr(customer.name)}" maxlength="30"></label>
            <label>이메일<input name="email" type="email" value="${escapeAttr(customer.email)}" maxlength="50"></label>
            <label>연락처<input name="tele" value="${escapeAttr(customer.tele)}" maxlength="30"></label>
            <button type="submit">수정 저장</button>
        </form>
    `);
}

async function openCustomerStatus(id) {
    const customer = await getCustomer(id);
    openModal("고객 상태 변경", `
        <form class="entity-form modal-form" data-form="customer-status" data-id="${customer.id}">
            <label>상태<select name="status" required>${customerStatusOptions(customer.status)}</select></label>
            <button type="submit">상태 변경</button>
        </form>
    `);
}

async function deleteCustomer(id) {
    if (!confirm("이 고객을 삭제 처리할까요?")) {
        return;
    }
    await runMutation(() => mockOrApiDelete("customers", id, `/admin/customers/${id}`), "고객을 삭제했습니다.");
    await loadCustomers();
}

async function openProductDetail(id) {
    const product = await getProduct(id);
    const reviewSummary = product.reviewSummary || {};
    const latestReviews = reviewSummary.latestReviews || [];
    openModal("상품 상세", `
        ${detailList([
            ["상품코드", product.id],
            ["상품명", product.name],
            ["카테고리", product.category],
            ["가격", formatPrice(product.price)],
            ["재고", formatNumber(product.stock)],
            ["상태", statusLabels[product.status] || product.status],
            ["등록 관리자", product.adminName || "-"],
            ["관리자 이메일", product.adminEmail || "-"],
            ["평균 평점", formatDecimal(reviewSummary.averageRating)],
            ["리뷰 수", formatNumber(reviewSummary.totalCount)]
        ])}
        <div class="modal-section-title">최신 리뷰</div>
        ${latestReviews.length ? latestReviews.map((review) => `
            <div class="review-item">
                <strong>${escapeHtml(review.customerName)} · ${review.rating}점</strong>
                <span>${escapeHtml(review.content)}</span>
                <small>${formatDate(review.createdAt)}</small>
            </div>
        `).join("") : `<div class="empty-state">리뷰가 없습니다.</div>`}
    `);
}

async function openProductEdit(id) {
    const product = await getProduct(id);
    openModal("상품 정보 수정", `
        <form class="entity-form modal-form" data-form="product-edit" data-id="${product.id}">
            <label>상품명<input name="name" value="${escapeAttr(product.name)}" maxlength="30"></label>
            <label>카테고리<input name="category" value="${escapeAttr(product.category)}" maxlength="30"></label>
            <label>가격<input name="price" type="number" min="0" value="${product.price}"></label>
            <button type="submit">수정 저장</button>
        </form>
    `);
}

async function openProductStock(id) {
    const product = await getProduct(id);
    openModal("상품 재고 변경", `
        <form class="entity-form modal-form" data-form="product-stock" data-id="${product.id}">
            <label>재고<input name="stock" type="number" min="0" value="${product.stock}" required></label>
            <button type="submit">재고 변경</button>
        </form>
    `);
}

async function openProductStatus(id) {
    const product = await getProduct(id);
    openModal("상품 상태 변경", `
        <form class="entity-form modal-form" data-form="product-status" data-id="${product.id}">
            <label>상태<select name="status" required>${productStatusOptions(product.status)}</select></label>
            <button type="submit">상태 변경</button>
        </form>
    `);
}

async function openProductReviews(id) {
    const reviews = await getProductReviews(id);
    openModal("상품 리뷰 관리", `
        <div class="table-wrap modal-table-wrap">
            <table>
                <thead>
                    <tr><th>ID</th><th>주문번호</th><th>고객</th><th>평점</th><th>내용</th><th>작성일</th><th>관리</th></tr>
                </thead>
                <tbody>
                    ${reviews.length ? reviews.map((review) => `
                        <tr>
                            <td>${escapeHtml(review.id)}</td>
                            <td>${escapeHtml(review.orderNumber || "-")}</td>
                            <td>${escapeHtml(review.customerName || "-")}</td>
                            <td>${escapeHtml(review.rating || "-")}</td>
                            <td>${escapeHtml(review.content || "-")}</td>
                            <td>${formatDate(review.createdAt)}</td>
                            <td><button class="table-action danger" type="button" data-review-delete data-product-id="${id}" data-review-id="${review.id}">삭제</button></td>
                        </tr>
                    `).join("") : `<tr><td class="empty-cell" colspan="7">표시할 리뷰가 없습니다.</td></tr>`}
                </tbody>
            </table>
        </div>
    `);
}

async function deleteProduct(id) {
    if (!confirm("이 상품을 삭제 처리할까요?")) {
        return;
    }
    await runMutation(() => mockOrApiDelete("products", id, `/admin/products/${id}`), "상품을 삭제했습니다.");
    await loadProducts();
}

async function openOrderDetail(id) {
    const order = await getOrder(id);
    openModal("주문 상세", detailList([
        ["ID", order.id],
        ["주문번호", order.orderNumber],
        ["고객명", order.customerName],
        ["고객 이메일", order.customerEmail],
        ["상품명", order.productName],
        ["수량", formatNumber(order.quantity)],
        ["총액", formatPrice(order.totalPrice)],
        ["주문일", formatDate(order.createdAt)],
        ["상태", statusLabels[order.status] || order.status],
        ["담당 관리자", order.adminName || "-"],
        ["관리자 이메일", order.adminEmail || "-"],
        ["관리자 권한", order.adminRole || "-"]
    ]));
}

async function updateOrderToNextStatus(id) {
    const order = await getOrder(id);
    const nextStatus = order.status === "PREPARING" ? "SHIPPING" : order.status === "SHIPPING" ? "DELIVERED" : null;
    if (!nextStatus) {
        showNotice("변경 가능한 다음 상태가 없습니다.", "error");
        return;
    }
    const success = await runMutation(() => updateOrderStatus(id, nextStatus), `주문 상태를 ${statusLabels[nextStatus]} 상태로 변경했습니다.`);
    if (success) {
        await loadOrders();
    }
}

function openOrderCancel(id) {
    openModal("주문 취소", `
        <form class="entity-form modal-form" data-form="order-cancel" data-id="${id}">
            <label>취소 사유<textarea name="cancelReason" maxlength="255" required placeholder="고객 변심"></textarea></label>
            <button type="submit" class="danger-submit">주문 취소</button>
        </form>
    `);
}

async function getCustomer(id) {
    if (USE_MOCK_DATA) {
        return findById(mockStore.customers, id);
    }
    return apiFetch(`/admin/customers/${id}`);
}

async function updateCustomer(id, values) {
    const success = await runMutation(async () => {
        if (USE_MOCK_DATA) {
            Object.assign(findById(mockStore.customers, id), compact(values));
            return;
        }
        await apiFetch(`/admin/customers/${id}`, { method: "PUT", body: compact(values) });
    }, "고객 정보를 수정했습니다.");
    if (success) {
        closeModal();
        await loadCustomers();
    }
}

async function updateCustomerStatus(id, status) {
    const success = await runMutation(async () => {
        if (USE_MOCK_DATA) {
            findById(mockStore.customers, id).status = status;
            return;
        }
        await apiFetch(`/admin/customers/${id}/status`, { method: "PUT", body: { status } });
    }, "고객 상태를 변경했습니다.");
    if (success) {
        closeModal();
        await loadCustomers();
    }
}

async function getProduct(id) {
    if (USE_MOCK_DATA) {
        return findById(mockStore.products, id);
    }
    return apiFetch(`/admin/products/${id}`);
}

async function createProduct(values, form) {
    const success = await runMutation(async () => {
        const payload = {
            name: values.name,
            category: values.category,
            price: Number(values.price),
            stock: Number(values.stock),
            status: values.status
        };
        if (USE_MOCK_DATA) {
            mockStore.products.unshift({ id: nextId(mockStore.products), adminName: "admin", createdAt: new Date().toISOString(), updatedAt: new Date().toISOString(), ...payload });
            return;
        }
        await apiFetch("/admin/products", { method: "POST", body: payload });
    }, "상품을 등록했습니다.");
    if (success) {
        form.reset();
        await loadProducts();
    }
}

async function updateProduct(id, values) {
    const success = await runMutation(async () => {
        const payload = compact({
            name: values.name,
            category: values.category,
            price: values.price === "" ? "" : Number(values.price)
        });
        if (USE_MOCK_DATA) {
            Object.assign(findById(mockStore.products, id), payload);
            return;
        }
        await apiFetch(`/admin/products/${id}`, { method: "PUT", body: payload });
    }, "상품 정보를 수정했습니다.");
    if (success) {
        closeModal();
        await loadProducts();
    }
}

async function updateProductStock(id, stock) {
    const success = await runMutation(async () => {
        const nextStock = Number(stock);
        if (USE_MOCK_DATA) {
            const product = findById(mockStore.products, id);
            product.stock = nextStock;
            if (product.status !== "DISCONTINUED") {
                product.status = nextStock === 0 ? "OUT_OF_STOCK" : "ON_SALE";
            }
            return;
        }
        await apiFetch(`/admin/products/${id}/stock`, { method: "PUT", body: { stock: nextStock } });
    }, "상품 재고를 변경했습니다.");
    if (success) {
        closeModal();
        await loadProducts();
    }
}

async function updateProductStatus(id, status) {
    const success = await runMutation(async () => {
        if (USE_MOCK_DATA) {
            findById(mockStore.products, id).status = status;
            return;
        }
        await apiFetch(`/admin/products/${id}/status`, { method: "PUT", body: { status } });
    }, "상품 상태를 변경했습니다.");
    if (success) {
        closeModal();
        await loadProducts();
    }
}

async function createOrder(values, form) {
    const success = await runMutation(async () => {
        const payload = {
            customerId: Number(values.customerId),
            productId: Number(values.productId),
            quantity: Number(values.quantity)
        };
        if (USE_MOCK_DATA) {
            const customer = findById(mockStore.customers, payload.customerId);
            const product = findById(mockStore.products, payload.productId);
            const totalPrice = product.price * payload.quantity;
            mockStore.orders.unshift({
                id: nextId(mockStore.orders),
                orderNumber: generateMockOrderNumber(),
                customerName: customer.name,
                customerEmail: customer.email,
                productName: product.name,
                quantity: payload.quantity,
                totalPrice,
                createdAt: new Date().toISOString(),
                status: "PREPARING",
                adminName: "admin",
                adminEmail: "admin@sparta.com",
                adminRole: "SUPER_ADMIN"
            });
            customer.totalOrderCount += 1;
            customer.totalOrderAmount += totalPrice;
            product.stock = Math.max(product.stock - payload.quantity, 0);
            return;
        }
        await apiFetch("/admin/orders", { method: "POST", body: payload });
    }, "주문을 생성했습니다.");
    if (success) {
        form.reset();
        await loadOrders();
    }
}

async function getOrder(id) {
    if (USE_MOCK_DATA) {
        return findById(mockStore.orders, id);
    }
    return apiFetch(`/admin/orders/${id}`);
}

async function updateOrderStatus(id, status) {
    if (USE_MOCK_DATA) {
        findById(mockStore.orders, id).status = status;
        return;
    }
    await apiFetch(`/admin/orders/${id}/status`, { method: "PUT", body: { status } });
}

async function cancelOrder(id, cancelReason) {
    const success = await runMutation(async () => {
        if (USE_MOCK_DATA) {
            const order = findById(mockStore.orders, id);
            order.status = "CANCELLED";
            order.cancelReason = cancelReason;
            return;
        }
        await apiFetch(`/admin/orders/${id}/cancel`, { method: "PATCH", body: { cancelReason } });
    }, "주문을 취소했습니다.");
    if (success) {
        closeModal();
        await loadOrders();
    }
}

async function getAdmin(id) {
    if (USE_MOCK_DATA) {
        return findById(mockStore.admins, id);
    }
    return apiFetch(`/admins/${id}`);
}

async function openAdminDetail(id) {
    const admin = await getAdmin(id);
    openModal("관리자 상세", detailList([
        ["관리자번호", admin.id],
        ["이름", admin.name],
        ["이메일", admin.email],
        ["연락처", admin.tele],
        ["권한", roleLabels[admin.role] || admin.role],
        ["상태", statusLabels[admin.status] || admin.status],
        ["생성일", formatDate(admin.createdAt)],
        ["승인일", formatDate(admin.approvedAt)],
        ["거부일", formatDate(admin.rejectedAt)],
        ["거부 사유", admin.rejectReason || "-"],
        ["수정일", formatDate(admin.updatedAt)]
    ]));
}

async function openAdminEdit(id) {
    const admin = await getAdmin(id);
    openModal("관리자 정보 수정", `
        <form class="entity-form modal-form" data-form="admin-edit" data-id="${admin.id}">
            <label>이름<input name="name" value="${escapeAttr(admin.name)}" maxlength="30"></label>
            <label>이메일<input name="email" type="email" value="${escapeAttr(admin.email)}" maxlength="50"></label>
            <label>연락처<input name="tele" value="${escapeAttr(admin.tele)}" maxlength="20" pattern="010-[0-9]{4}-[0-9]{4}"></label>
            <button type="submit">수정 저장</button>
        </form>
    `);
}

async function updateAdmin(id, values) {
    const success = await runMutation(async () => {
        const payload = compact(values);
        if (USE_MOCK_DATA) {
            Object.assign(findById(mockStore.admins, id), payload, { updatedAt: new Date().toISOString() });
            return;
        }
        await apiFetch(`/admins/${id}`, { method: "PUT", body: payload });
    }, "관리자 정보를 수정했습니다.");

    if (success) {
        closeModal();
        await loadAdmins();
    }
}

async function openAdminRole(id) {
    const admin = await getAdmin(id);
    openModal("관리자 역할 변경", `
        <form class="entity-form modal-form" data-form="admin-role" data-id="${admin.id}">
            <label>역할<select name="role" required>${adminRoleOptions(admin.role)}</select></label>
            <button type="submit">역할 변경</button>
        </form>
    `);
}

async function updateAdminRole(id, role) {
    const success = await runMutation(async () => {
        if (USE_MOCK_DATA) {
            Object.assign(findById(mockStore.admins, id), { role, updatedAt: new Date().toISOString() });
            return;
        }
        await apiFetch(`/admins/${id}/role`, { method: "PUT", body: { role } });
    }, "관리자 역할을 변경했습니다.");

    if (success) {
        closeModal();
        await loadAdmins();
    }
}

async function openAdminStatus(id) {
    const admin = await getAdmin(id);
    openModal("관리자 상태 변경", `
        <form class="entity-form modal-form" data-form="admin-status" data-id="${admin.id}">
            <label>상태<select name="status" required>${adminStatusOptions(admin.status)}</select></label>
            <button type="submit">상태 변경</button>
        </form>
    `);
}

async function updateAdminStatus(id, status) {
    const success = await runMutation(async () => {
        if (USE_MOCK_DATA) {
            Object.assign(findById(mockStore.admins, id), { status, updatedAt: new Date().toISOString() });
            return;
        }
        await apiFetch(`/admins/${id}/status`, { method: "PUT", body: { status } });
    }, "관리자 상태를 변경했습니다.");

    if (success) {
        closeModal();
        await loadAdmins();
    }
}

async function deleteAdmin(id) {
    if (!confirm("이 관리자 계정을 삭제 처리할까요? 삭제 제한 상태는 서버 정책에 따라 거부될 수 있습니다.")) {
        return;
    }

    const success = await runMutation(async () => {
        if (USE_MOCK_DATA) {
            mockStore.admins = mockStore.admins.filter((admin) => Number(admin.id) !== Number(id));
            return;
        }
        await apiFetch(`/admins/${id}`, { method: "DELETE" });
    }, "관리자를 삭제했습니다.");

    if (success) {
        await loadAdmins();
    }
}

async function approveAdmin(id) {
    if (!confirm("이 관리자 가입 요청을 승인할까요? 승인 후 해당 계정은 로그인할 수 있습니다.")) {
        return;
    }

    const success = await runMutation(async () => {
        await submitAdminApproval(id, true, null);
    }, "관리자 가입 요청을 승인했습니다.");

    if (success) {
        await loadAdmins();
    }
}

function openAdminReject(id) {
    openModal("관리자 가입 거부", `
        <form class="entity-form modal-form" data-form="admin-reject" data-id="${id}">
            <label>거부 사유<textarea name="rejectReason" maxlength="100" required placeholder="승인 기준 미충족"></textarea></label>
            <button type="submit" class="danger-submit">가입 거부</button>
        </form>
    `);
}

async function rejectAdmin(id, rejectReason) {
    const success = await runMutation(async () => {
        await submitAdminApproval(id, false, rejectReason);
    }, "관리자 가입 요청을 거부했습니다.");

    if (success) {
        closeModal();
        await loadAdmins();
    }
}

async function submitAdminApproval(id, isApproved, rejectReason) {
    if (USE_MOCK_DATA) {
        const admin = findById(mockStore.admins, id);
        admin.status = isApproved ? "ACTIVE" : "REJECTED";
        admin.updatedAt = new Date().toISOString();

        if (isApproved) {
            admin.approvedAt = new Date().toISOString();
            admin.rejectReason = null;
        } else {
            admin.rejectedAt = new Date().toISOString();
            admin.rejectReason = rejectReason;
        }
        return;
    }

    await apiFetch(`/admins/${id}/approval`, {
        method: "PATCH",
        body: {
            isApproved,
            rejectReason: isApproved ? null : rejectReason
        }
    });
}

async function getProductReviews(productId) {
    if (USE_MOCK_DATA) {
        return findById(mockStore.products, productId).reviewSummary?.latestReviews || [];
    }

    const data = await apiFetch(`/admin/products/${productId}/reviews?page=1&size=10&sortBy=createdAt&sortOrder=desc`);
    return data.content || [];
}

async function deleteReview(productId, reviewId) {
    if (!confirm("이 리뷰를 삭제 처리할까요?")) {
        return;
    }

    const success = await runMutation(async () => {
        if (USE_MOCK_DATA) {
            const product = findById(mockStore.products, productId);
            product.reviewSummary.latestReviews = (product.reviewSummary.latestReviews || []).filter((review) => Number(review.id) !== Number(reviewId));
            product.reviewSummary.totalCount = product.reviewSummary.latestReviews.length;
            return;
        }
        await apiFetch(`/admin/products/${productId}/reviews/${reviewId}`, { method: "DELETE" });
    }, "리뷰를 삭제했습니다.");

    if (success) {
        await openProductReviews(productId);
    }
}

function getMockDashboard() {
    const activeCustomers = mockStore.customers.filter((item) => item.status === "ACTIVE").length;
    const lowStockProducts = mockStore.products.filter((item) => item.stock > 0 && item.stock <= 5).length;
    const outOfStockProducts = mockStore.products.filter((item) => item.status === "OUT_OF_STOCK").length;
    const today = new Date().toISOString().slice(0, 10);
    const todayOrders = mockStore.orders.filter((item) => formatDate(item.createdAt) === today);
    const totalSales = mockStore.orders.filter((item) => item.status !== "CANCELLED").reduce((sum, item) => sum + item.totalPrice, 0);

    return {
        summary: {
            totalAdmins: 11,
            activeAdmins: 8,
            totalCustomers: mockStore.customers.length,
            activeCustomers,
            totalProducts: mockStore.products.length,
            lowStockProducts,
            totalOrders: mockStore.orders.length,
            todayOrders: todayOrders.length,
            totalReviews: 3,
            averageRating: 4.2
        },
        widgets: {
            totalSales,
            todaySales: todayOrders.reduce((sum, item) => sum + item.totalPrice, 0),
            preparingOrders: mockStore.orders.filter((item) => item.status === "PREPARING").length,
            shippingOrders: mockStore.orders.filter((item) => item.status === "SHIPPING").length,
            deliveredOrders: mockStore.orders.filter((item) => item.status === "DELIVERED").length,
            lowStockProducts,
            outOfStockProducts
        },
        charts: {
            reviewRatings: [
                { rating: 5, count: 1 },
                { rating: 4, count: 2 }
            ],
            customerStatuses: countBy(mockStore.customers, "status").map(([status, count]) => ({ status, count })),
            productCategories: countBy(mockStore.products, "category").map(([category, count]) => ({ category, count }))
        },
        recentOrders: mockStore.orders.slice(0, 5).map((order) => ({
            orderNumber: order.orderNumber,
            customerName: order.customerName,
            productName: order.productName,
            amount: order.totalPrice,
            status: order.status
        }))
    };
}

function filterCustomers(customers, filters) {
    return customers.filter((item) => {
        const keywordMatch = !filters.keyword || `${item.name} ${item.email}`.toLowerCase().includes(filters.keyword.toLowerCase());
        const statusMatch = !filters.status || item.status === filters.status;
        return keywordMatch && statusMatch;
    });
}

function filterProducts(products, filters) {
    return products.filter((item) => {
        const keywordMatch = !filters.keyword || item.name.toLowerCase().includes(filters.keyword.toLowerCase());
        const categoryMatch = !filters.category || item.category.toLowerCase().includes(filters.category.toLowerCase());
        const statusMatch = !filters.status || item.status === filters.status;
        return keywordMatch && categoryMatch && statusMatch;
    });
}

function filterOrders(orders, filters) {
    return orders.filter((item) => {
        const keywordMatch = !filters.keyword || `${item.customerName} ${item.orderNumber}`.toLowerCase().includes(filters.keyword.toLowerCase());
        const statusMatch = !filters.status || item.status === filters.status;
        return keywordMatch && statusMatch;
    });
}

function filterAdmins(admins, filters) {
    return admins.filter((item) => {
        const keywordMatch = !filters.keyword || `${item.name} ${item.email}`.toLowerCase().includes(filters.keyword.toLowerCase());
        const roleMatch = !filters.role || item.role === filters.role;
        const statusMatch = !filters.status || item.status === filters.status;
        return keywordMatch && roleMatch && statusMatch;
    });
}

function getMockPage(items, filters) {
    const page = Number(filters.page || 1);
    const size = Number(filters.size || 10);
    const start = (page - 1) * size;
    const sortedItems = sortItems(items, filters.sortBy, filters.sortOrder);
    return {
        content: sortedItems.slice(start, start + size),
        page,
        size,
        totalElements: sortedItems.length,
        totalPages: Math.max(Math.ceil(sortedItems.length / size), 1)
    };
}

function sortItems(items, sortBy, sortOrder) {
    if (!sortBy) {
        return [...items];
    }

    const direction = sortOrder === "asc" ? 1 : -1;
    return [...items].sort((a, b) => compareValues(a[sortBy], b[sortBy]) * direction);
}

function compareValues(a, b) {
    if (a === undefined || a === null) return b === undefined || b === null ? 0 : -1;
    if (b === undefined || b === null) return 1;

    const aString = String(a);
    const bString = String(b);
    if (/^\d+$/.test(aString) && /^\d+$/.test(bString) && (aString.length > 15 || bString.length > 15)) {
        return aString.localeCompare(bString);
    }

    const aNumber = Number(a);
    const bNumber = Number(b);
    if (!Number.isNaN(aNumber) && !Number.isNaN(bNumber)) {
        return aNumber - bNumber;
    }

    const aTime = Date.parse(a);
    const bTime = Date.parse(b);
    if (!Number.isNaN(aTime) && !Number.isNaN(bTime)) {
        return aTime - bTime;
    }

    return String(a).localeCompare(String(b), "ko-KR");
}

function renderBars(container, items, labelKey, emptyMessage) {
    if (items.length === 0) {
        container.replaceChildren(createEmptyState(emptyMessage));
        return;
    }

    const maxCount = Math.max(...items.map((item) => Number(item.count || 0)), 1);
    container.replaceChildren(...items.slice(0, 6).map((item, index) => {
        const row = document.createElement("div");
        row.className = "bar-row";
        row.innerHTML = `
            <div class="bar-meta">
                <span class="bar-label">${escapeHtml(item[labelKey] || "-")}</span>
                <span class="bar-count">${formatNumber(item.count)}개</span>
            </div>
            <div class="bar-track">
                <div class="bar-fill" style="width:${Math.max((Number(item.count || 0) / maxCount) * 100, 4)}%; background:${categoryColors[index % categoryColors.length]}"></div>
            </div>
        `;
        return row;
    }));
}

function createSummaryCard(title, value, icon) {
    const article = document.createElement("article");
    article.className = "summary-card";
    article.innerHTML = `
        <div>
            <p>${escapeHtml(title)}</p>
            <strong>${value ?? "-"}</strong>
        </div>
        <span class="summary-icon">${escapeHtml(icon)}</span>
    `;
    return article;
}

function createCell(value) {
    const td = document.createElement("td");
    td.textContent = value === undefined || value === null || value === "" ? "-" : value;
    return td;
}

function createStatusCell(status) {
    const td = document.createElement("td");
    td.append(createStatusBadge(status));
    return td;
}

function createActionCell(buttons) {
    const td = document.createElement("td");
    const wrap = document.createElement("div");
    wrap.className = "action-buttons";
    buttons.forEach((button) => wrap.append(button));
    td.append(wrap);
    return td;
}

function actionButton(label, action, id, variant = "") {
    const button = document.createElement("button");
    button.type = "button";
    button.className = `table-action ${variant}`;
    button.dataset.action = action;
    button.dataset.id = id;
    button.textContent = label;
    return button;
}

function sortableHeader(key, sortBy, label) {
    const active = state.filters[key]?.sortBy === sortBy;
    return `
        <th>
            <button class="sort-button ${active ? "active" : ""}" type="button" data-action="sort-list" data-sort-key="${key}" data-sort-by="${sortBy}" aria-label="${label} 정렬">
                <span>${escapeHtml(label)}</span>
                <span class="sort-indicator">${getSortIndicator(key, sortBy)}</span>
            </button>
        </th>
    `;
}

function tableColGroup(widths) {
    return `<colgroup>${widths.map((width) => `<col style="width:${width}px">`).join("")}</colgroup>`;
}

function getSortIndicator(key, sortBy) {
    const filters = state.filters[key] || {};
    if (filters.sortBy !== sortBy) {
        return "↕";
    }
    return filters.sortOrder === "asc" ? "↑" : "↓";
}

function updateSortIndicators(key) {
    document.querySelectorAll(`[data-sort-key="${key}"]`).forEach((button) => {
        const sortBy = button.dataset.sortBy;
        const active = state.filters[key]?.sortBy === sortBy;
        button.classList.toggle("active", active);
        const indicator = button.querySelector(".sort-indicator");
        if (indicator) {
            indicator.textContent = getSortIndicator(key, sortBy);
        }
    });
}

function disabledActionButton(label) {
    const button = document.createElement("button");
    button.type = "button";
    button.className = "table-action muted";
    button.disabled = true;
    button.textContent = label;
    return button;
}

function createEmptyRow(message, colspan) {
    const tr = document.createElement("tr");
    const td = document.createElement("td");
    td.className = "empty-cell";
    td.colSpan = colspan;
    td.textContent = message;
    tr.append(td);
    return tr;
}

function createEmptyState(message) {
    const div = document.createElement("div");
    div.className = "empty-state";
    div.textContent = message;
    return div;
}

function createQueryString(params = {}) {
    const searchParams = new URLSearchParams();
    Object.entries(params).forEach(([key, value]) => {
        if (value !== undefined && value !== null && String(value).trim() !== "") {
            searchParams.set(key, String(value).trim());
        }
    });
    const query = searchParams.toString();
    return query ? `?${query}` : "";
}

function formValues(form) {
    return Object.fromEntries(new FormData(form).entries());
}

function compact(values) {
    return Object.fromEntries(Object.entries(values).filter(([, value]) => value !== undefined && value !== null && String(value).trim() !== ""));
}

function renderPageMeta(id, data, key) {
    const node = document.getElementById(id);
    if (!node || !data) {
        return;
    }

    const currentPage = Number(data.page || state.filters[key]?.page || 1);
    const totalPages = Math.max(Number(data.totalPages || 1), 1);
    const previousPage = Math.max(currentPage - 1, 1);
    const nextPage = Math.min(currentPage + 1, totalPages);

    node.innerHTML = `
        <span>총 ${formatNumber(data.totalElements)}건 · ${formatNumber(currentPage)} / ${formatNumber(totalPages)} 페이지</span>
        <div class="pagination-controls" aria-label="페이지 이동">
            <button class="pagination-button" type="button" data-action="page-change" data-page-key="${key}" data-page-number="${previousPage}" ${currentPage <= 1 ? "disabled" : ""}>이전</button>
            <span class="pagination-current">${formatNumber(currentPage)} / ${formatNumber(totalPages)}</span>
            <button class="pagination-button" type="button" data-action="page-change" data-page-key="${key}" data-page-number="${nextPage}" ${currentPage >= totalPages ? "disabled" : ""}>다음</button>
        </div>
    `;
}

function customerStatusOptions(selected = "") {
    return ["ACTIVE", "INACTIVE", "SUSPENDED"].map((status) => optionHtml(status, statusLabels[status], selected)).join("");
}

function productStatusOptions(selected = "") {
    return ["ON_SALE", "OUT_OF_STOCK", "DISCONTINUED"].map((status) => optionHtml(status, statusLabels[status], selected)).join("");
}

function orderStatusOptions(selected = "") {
    return ["PREPARING", "SHIPPING", "DELIVERED", "CANCELLED"].map((status) => optionHtml(status, statusLabels[status], selected)).join("");
}

function adminStatusOptions(selected = "") {
    return ["PENDING_APPROVAL", "ACTIVE", "INACTIVE", "SUSPENDED", "REJECTED"].map((status) => optionHtml(status, statusLabels[status], selected)).join("");
}

function adminRoleOptions(selected = "") {
    return ["SUPER_ADMIN", "OPERATIONS_ADMIN", "CS_ADMIN"].map((role) => optionHtml(role, roleLabels[role], selected)).join("");
}

function optionHtml(value, label, selected) {
    return `<option value="${value}" ${value === selected ? "selected" : ""}>${label}</option>`;
}

function detailList(rows) {
    return `<dl class="detail-list">${rows.map(([label, value]) => `
        <div><dt>${escapeHtml(label)}</dt><dd>${escapeHtml(value ?? "-")}</dd></div>
    `).join("")}</dl>`;
}

function openModal(title, content) {
    document.getElementById("modalRoot").innerHTML = `
        <div class="modal-backdrop">
            <section class="modal-panel" role="dialog" aria-modal="true" aria-label="${escapeAttr(title)}">
                <header class="modal-header">
                    <h2>${escapeHtml(title)}</h2>
                    <button type="button" data-modal-close aria-label="닫기">×</button>
                </header>
                <div class="modal-body">${content}</div>
            </section>
        </div>
    `;
}

function closeModal() {
    document.getElementById("modalRoot").replaceChildren();
}

function resetFilters(key, loader) {
    state.filters[key] = defaultFilters(key);
    if (key === "customers") renderCustomersPage();
    if (key === "products") renderProductsPage();
    if (key === "orders") renderOrdersPage();
    if (key === "admins") renderAdminApprovalsPage();
    loader();
}

async function changeListPage(key, page) {
    const loader = getListLoader(key);
    if (!loader || !Number.isFinite(page)) {
        return;
    }

    const totalPages = Math.max(Number(state.lastLoaded[key]?.totalPages || 1), 1);
    const targetPage = Math.min(Math.max(page, 1), totalPages);
    await loader({ page: targetPage });
}

async function changeSort(key, sortBy) {
    const loader = getListLoader(key);
    if (!loader || !sortBy) {
        return;
    }

    const current = state.filters[key] || {};
    const nextOrder = current.sortBy === sortBy && current.sortOrder === "asc" ? "desc" : "asc";
    await loader({ sortBy, sortOrder: nextOrder, page: 1 });
}

function getListLoader(key) {
    return {
        customers: loadCustomers,
        products: loadProducts,
        orders: loadOrders,
        admins: loadAdmins
    }[key];
}

function defaultFilters(key) {
    const common = { page: 1, size: 10, sortBy: "createdAt", sortOrder: "desc" };
    return common;
}

async function runMutation(action, successMessage) {
    try {
        await action();
        showNotice(successMessage, "success");
        return true;
    } catch (error) {
        console.error(error);
        showNotice(error.message || "요청 처리에 실패했습니다.", "error");
        return false;
    }
}

async function mockOrApiDelete(storeName, id, path) {
    if (USE_MOCK_DATA) {
        mockStore[storeName] = mockStore[storeName].filter((item) => item.id !== id);
        return;
    }
    await apiFetch(path, { method: "DELETE" });
}

function handleLoadError(message, error) {
    console.error(message, error);
    showNotice(`${message}: ${error.message}`, "error");
}

function showNotice(message, type = "success") {
    const notice = document.createElement("div");
    notice.className = `notice ${type}`;
    notice.textContent = message;
    document.body.append(notice);
    setTimeout(() => notice.remove(), 2600);
}

function findById(items, id) {
    const item = items.find((candidate) => Number(candidate.id) === Number(id));
    if (!item) {
        throw new Error("대상을 찾을 수 없습니다.");
    }
    return item;
}

function nextId(items) {
    return Math.max(0, ...items.map((item) => Number(item.id))) + 1;
}

function generateMockOrderNumber(date = new Date()) {
    const pad = (value, size) => String(value).padStart(size, "0");
    const stamp = [
        String(date.getFullYear()).slice(-2),
        pad(date.getMonth() + 1, 2),
        pad(date.getDate(), 2),
        pad(date.getHours(), 2),
        pad(date.getMinutes(), 2),
        pad(date.getSeconds(), 2),
        pad(date.getMilliseconds(), 3)
    ].join("");
    const randomNumber = Math.floor(Math.random() * 900) + 100;

    return `${stamp}${randomNumber}`;
}

function countBy(items, key) {
    const map = new Map();
    items.forEach((item) => map.set(item[key], (map.get(item[key]) || 0) + 1));
    return Array.from(map.entries());
}

function formatNumber(value) {
    return Number(value || 0).toLocaleString("ko-KR");
}

function formatDecimal(value) {
    if (value === undefined || value === null) {
        return "-";
    }
    return Number(value).toLocaleString("ko-KR", { maximumFractionDigits: 1 });
}

function getStatusChartColor(status) {
    return statusColors[status] || "#9ca3af";
}

function createDonutGradient(items, total) {
    if (!total) {
        return "conic-gradient(#d1d5db 0 100%)";
    }

    let cursor = 0;
    const segments = items.map((item) => {
        const count = Number(item.count || 0);
        const start = cursor;
        const end = cursor + (count / total) * 100;
        cursor = end;
        return `${getStatusChartColor(item.status)} ${start}% ${end}%`;
    });

    return `conic-gradient(${segments.join(", ")})`;
}

function escapeHtml(value) {
    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}

function escapeAttr(value) {
    return escapeHtml(value ?? "");
}
