let allRoutes = []; // Biến lưu toàn bộ dữ liệu từ server
let currentPage = 1;
const rowsPerPage = 10; // Số dòng hiển thị mỗi trang

document.addEventListener('DOMContentLoaded', function() {
    // 1. Tải dữ liệu ban đầu
    fetchRoutes();

    // 2. Xử lý sự kiện nhấn nút Lưu
    const btnSave = document.getElementById('btnSaveRoute');
    if (btnSave) {
        btnSave.addEventListener('click', saveRoute);
    }
});

// Hàm lấy danh sách từ API
async function fetchRoutes() {
    try {
        const response = await fetch('/api/routes');
        allRoutes = await response.json();
        
        // Sau khi lấy dữ liệu, gọi hàm render để hiển thị trang 1
        renderTable(currentPage);
    } catch (error) {
        console.error('Lỗi khi tải danh sách tuyến:', error);
        alert('Không thể kết nối đến server!');
    }
}

// Hàm render bảng dựa trên trang hiện tại
function renderTable(page) {
    const tbody = document.getElementById('tbody-routes');
    const pagination = document.getElementById('pagination'); // ID của <ul> phân trang trong HTML
    if (!tbody) return;

    tbody.innerHTML = ''; 

    // Tính toán vị trí cắt mảng
    const start = (page - 1) * rowsPerPage;
    const end = start + rowsPerPage;
    const paginatedItems = allRoutes.slice(start, end);

    if (allRoutes.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" class="text-center p-4 text-muted">Chưa có tuyến đường nào.</td></tr>';
        if (pagination) pagination.innerHTML = '';
        return;
    }

    // Hiển thị các dòng dữ liệu
    paginatedItems.forEach(route => {
        const row = `
            <tr>
                <td class="ps-4 text-muted">${route.id}</td>
                <td class="fw-bold text-primary">${route.departure || 'Không xác định'}</td>
                <td class="fw-bold text-success">${route.destination || 'Không xác định'}</td>
                <td>${new Intl.NumberFormat('vi-VN').format(route.price)} đ</td>
                <td class="text-center">
                    <button class="btn btn-outline-danger btn-sm" onclick="deleteRoute(${route.id})">
                        <i class="bi bi-trash"></i> Xóa
                    </button>
                </td>
            </tr>
        `;
        tbody.innerHTML += row;
    });

    // Vẽ các nút phân trang
    renderPaginationControls();
}

// Hàm tạo các nút bấm số trang
function renderPaginationControls() {
    const pagination = document.getElementById('pagination');
    if (!pagination) return;

    pagination.innerHTML = "";
    const pageCount = Math.ceil(allRoutes.length / rowsPerPage);

    if (pageCount <= 1) return; // Không hiện nếu chỉ có 1 trang

    for (let i = 1; i <= pageCount; i++) {
        const li = document.createElement('li');
        li.className = `page-item ${currentPage === i ? 'active' : ''}`;
        li.innerHTML = `<a class="page-link" href="javascript:void(0)">${i}</a>`;
        li.onclick = (e) => {
            e.preventDefault();
            currentPage = i;
            renderTable(i);
        };
        pagination.appendChild(li);
    }
}

// Hàm thêm mới tuyến xe
async function saveRoute() {
    const departure = document.getElementById('route-departure').value;
    const destination = document.getElementById('route-destination').value;
    const price = document.getElementById('route-price').value;

    if (!departure || !destination || !price) {
        alert('Vui lòng nhập đầy đủ thông tin!');
        return;
    }

    const newRoute = {
        departure: departure,
        destination: destination,
        price: parseFloat(price)
    };

    try {
        const response = await fetch('/api/routes', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(newRoute)
        });

        if (response.ok) {
            const modalElement = document.getElementById('modalRoute');
            const modal = bootstrap.Modal.getInstance(modalElement) || new bootstrap.Modal(modalElement);
            modal.hide();

            document.getElementById('formRoute').reset();
            fetchRoutes(); // Tải lại toàn bộ và render lại
        } else {
            const errMsg = await response.text();
            alert('Lỗi: ' + errMsg);
        }
    } catch (error) {
        console.error('Lỗi khi lưu tuyến:', error);
    }
}

// Hàm xóa tuyến xe
async function deleteRoute(id) {
    if (confirm('Bạn có chắc chắn muốn xóa tuyến này? Thao tác này không thể hoàn tác.')) {
        try {
            const response = await fetch(`/api/routes/${id}`, { method: 'DELETE' });
            if (response.ok) {
                fetchRoutes(); 
            } else {
                alert('Không thể xóa! Tuyến này có thể đang được sử dụng trong một Chuyến xe.');
            }
        } catch (error) {
            console.error('Lỗi khi xóa:', error);
        }
    }
}