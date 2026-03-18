// Cấu hình hằng số
const CONFIG = {
    api_url: '/api/routes',
    rows_per_page: 6, // Số lượng Card trên mỗi trang
    default_img: "https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?auto=format&fit=crop&w=600&q=80"
};

const CITY_IMAGES = {
    "hà nội": "https://images.unsplash.com/photo-1555944198-4395d330544c?auto=format&fit=crop&w=600&q=80",
    "đà lạt": "https://images.unsplash.com/photo-1599708153386-62e240404f5e?auto=format&fit=crop&w=600&q=80",
    "hồ chí minh": "https://images.unsplash.com/photo-1553913861-c0fddf2619ee?auto=format&fit=crop&w=600&q=80",
    "sài gòn": "https://images.unsplash.com/photo-1553913861-c0fddf2619ee?auto=format&fit=crop&w=600&q=80",
    "đà nẵng": "https://images.unsplash.com/photo-1559592443-7f87a79f6527?auto=format&fit=crop&w=600&q=80",
    "nha trang": "https://images.unsplash.com/photo-1610450949065-1f280f33950d?auto=format&fit=crop&w=600&q=80"
};

let groupedData = []; // Danh sách các tỉnh đã gom nhóm
let currentPage = 1;

// 1. Hàm khởi tạo chính
async function initBusRoutes() {
    try {
        const response = await fetch(CONFIG.api_url);
        const rawRoutes = await response.json();

        // Gom nhóm Tuyến theo Điểm đi (Departure)
        const groups = rawRoutes.reduce((acc, route) => {
            const city = route.departureLocation || "Khác";
            if (!acc[city]) acc[city] = [];
            acc[city].push(route);
            return acc;
        }, {});

        // Chuyển object thành mảng để dễ phân trang
        groupedData = Object.keys(groups).map(cityName => ({
            name: cityName,
            routes: groups[cityName]
        }));

        renderPage(currentPage);
    } catch (error) {
        console.error("Không thể load dữ liệu route:", error);
    }
}

// 2. Hàm hiển thị dữ liệu theo trang
function renderPage(page) {
    const container = document.getElementById('route-container');
    const pagination = document.getElementById('pagination');
    if(!container) return;

    container.innerHTML = "";
    const start = (page - 1) * CONFIG.rows_per_page;
    const end = start + CONFIG.rows_per_page;
    const itemsToShow = groupedData.slice(start, end);

    itemsToShow.forEach(group => {
        const imgUrl = getCityImg(group.name);
        
        const cardHtml = `
            <div class="route-card-main animate__animated animate__fadeIn">
                <div class="card-header-img">
                    <img src="${imgUrl}" onerror="this.src='${CITY_IMAGES.default}'">
                    <div class="img-overlay">
                        <small>Tuyến xe từ</small>
                        <h5 class="m-0 fw-bold">${group.name}</h5>
                    </div>
                </div>
                <div class="route-list">
                    ${group.routes.map(r => `
                        <div class="sub-route-item" onclick="selectRoute('${r.departureLocation}', '${r.arrivalLocation}')">
                            <div class="info">
                                <p class="dest-name">${r.arrivalLocation}</p>
                                <span class="meta-info">${r.distanceKm}km - ${Math.round(r.distanceKm/40)} giờ</span>
                            </div>
                            <div class="price-tag">${new Intl.NumberFormat('vi-VN').format(200000)}đ</div>
                        </div>
                    `).join('')}
                </div>
            </div>
        `;
        container.innerHTML += cardHtml;
    });

    setupPaginationControls(pagination);
}

// 3. Hàm tạo nút phân trang
function setupPaginationControls(wrapper) {
    if(!wrapper) return;
    wrapper.innerHTML = "";
    const pageCount = Math.ceil(groupedData.length / CONFIG.rows_per_page);

    for (let i = 1; i <= pageCount; i++) {
        const li = document.createElement('li');
        li.className = `page-item ${currentPage === i ? 'active' : ''}`;
        li.innerHTML = `<a class="page-link" href="javascript:void(0)">${i}</a>`;
        li.addEventListener('click', () => {
            currentPage = i;
            renderPage(i);
            window.scrollTo({ top: 500, behavior: 'smooth' });
        });
        wrapper.appendChild(li);
    }
}

// 4. Hàm bổ trợ: Lấy ảnh và xử lý click
function getCityImg(name) {
    const n = name.toLowerCase();
    for (let key in CITY_IMAGES) {
        if (n.includes(key)) return CITY_IMAGES[key];
    }
    return CONFIG.default_img;
}

function selectRoute(dep, arr) {
    const depInput = document.getElementById('departureInput');
    const arrInput = document.getElementById('arrivalInput');
    if(depInput) depInput.value = dep;
    if(arrInput) arrInput.value = arr;
}

// Chạy khi trang sẵn sàng
document.addEventListener('DOMContentLoaded', initBusRoutes);