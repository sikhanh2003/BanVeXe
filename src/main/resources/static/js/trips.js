let allTrips = [];
let currentPage = 1;
const rowsPerPage = 5; // Số chuyến xe hiển thị trên mỗi trang

// 1. Tải danh sách chuyến xe từ API
async function loadTrips() {
    try {
        const res = await fetch('/api/trips');
        allTrips = await res.json();
        
        // Gọi hàm hiển thị trang đầu tiên
        renderTripTable(currentPage);
    } catch (error) {
        console.error("Lỗi khi tải chuyến xe:", error);
        document.getElementById('trip-list').innerHTML = `
            <tr><td colspan="5" class="text-center text-danger p-4">Không thể kết nối dữ liệu chuyến xe</td></tr>
        `;
    }
}

// 2. Hàm hiển thị bảng dữ liệu theo trang
function renderTripTable(page) {
    const list = document.getElementById('trip-list');
    const pagination = document.getElementById('pagination'); // Đảm bảo trong HTML có ID này
    if (!list) return;

    list.innerHTML = "";
    
    // Tính toán cắt mảng dữ liệu
    const start = (page - 1) * rowsPerPage;
    const end = start + rowsPerPage;
    const paginatedItems = allTrips.slice(start, end);

    if (paginatedItems.length === 0) {
        list.innerHTML = '<tr><td colspan="5" class="text-center p-4">Chưa có chuyến xe nào được tạo</td></tr>';
        if (pagination) pagination.innerHTML = "";
        return;
    }

    // Hiển thị dữ liệu
    paginatedItems.forEach(t => {
        // Xử lý hiển thị thời gian đẹp hơn
        const startTime = t.departureTime ? t.departureTime.replace('T', ' ').substring(0, 16) : "Chưa rõ";
        
        list.innerHTML += `
            <tr>
                <td class="fw-bold text-dark">
                    ${t.route?.departureLocation || '???'} 
                    <i class="bi bi-arrow-right text-muted mx-1"></i> 
                    ${t.route?.arrivalLocation || '???'}
                </td>
                <td>
                    <span class="badge bg-light text-dark border">
                        <i class="bi bi-bus-front me-1"></i>${t.bus?.busNumber || 'N/A'}
                    </span>
                    <small class="text-muted d-block">${t.bus?.busType || ''}</small>
                </td>
                <td><i class="bi bi-clock me-1"></i>${startTime}</td>
                <td class="text-danger fw-bold">${(t.pricePerTicket || 0).toLocaleString()}đ</td>
                <td class="text-center">
                    <button class="btn btn-outline-danger btn-sm" onclick="deleteTrip(${t.id})">
                        <i class="bi bi-trash"></i>
                    </button>
                </td>
            </tr>
        `;
    });

    // Vẽ các nút chuyển trang
    renderPaginationControls();
}

// 3. Hàm tạo các nút bấm phân trang
function renderPaginationControls() {
    const pagination = document.getElementById('pagination');
    if (!pagination) return;

    pagination.innerHTML = "";
    const pageCount = Math.ceil(allTrips.length / rowsPerPage);

    if (pageCount <= 1) return; 

    for (let i = 1; i <= pageCount; i++) {
        const li = document.createElement('li');
        li.className = `page-item ${currentPage === i ? 'active' : ''}`;
        li.innerHTML = `<a class="page-link" href="javascript:void(0)">${i}</a>`;
        li.onclick = (e) => {
            currentPage = i;
            renderTripTable(i);
            // Cuộn lên đầu bảng cho dễ nhìn
            window.scrollTo({ top: 0, behavior: 'smooth' });
        };
        pagination.appendChild(li);
    }
}

// 4. Hàm xóa chuyến xe (bổ sung thêm để hoàn thiện)
async function deleteTrip(id) {
    if (confirm("Bạn có chắc chắn muốn xóa chuyến xe này?")) {
        try {
            const res = await fetch(`/api/trips/${id}`, { method: 'DELETE' });
            if (res.ok) {
                loadTrips(); // Tải lại danh sách
            } else {
                alert("Không thể xóa chuyến xe đã có người đặt vé!");
            }
        } catch (error) {
            alert("Lỗi kết nối khi xóa!");
        }
    }
}

// Khởi chạy khi trang sẵn sàng
document.addEventListener('DOMContentLoaded', loadTrips);