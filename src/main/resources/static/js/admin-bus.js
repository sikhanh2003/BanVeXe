let currentPage = 0; // Spring Boot bắt đầu từ trang 0

// 1. Tải danh sách xe từ API (Server-side Pagination)
async function loadBuses(page = 0) {
    try {
        currentPage = page;
        const res = await fetch(`/api/buses?page=${page}&size=10`);
        const data = await res.json();
        
        // Spring Data JPA trả về đối tượng có thuộc tính 'content' và 'totalPages'
        const buses = data.content || []; 
        const totalPages = data.totalPages || 0;

        renderTable(buses);
        renderPagination(totalPages, currentPage);
    } catch (e) {
        console.error("Lỗi tải dữ liệu:", e);
        document.getElementById("list-buses").innerHTML = '<tr><td colspan="5" class="text-center text-danger p-4">Không thể kết nối máy chủ</td></tr>';
    }
}

// 2. Hàm hiển thị bảng
function renderTable(items) {
    const listBody = document.getElementById('list-buses');
    if (!listBody) return;

    if (items.length === 0) {
        listBody.innerHTML = '<tr><td colspan="5" class="text-center p-4">Chưa có dữ liệu xe</td></tr>';
        return;
    }

    listBody.innerHTML = items.map(b => `
        <tr>
            <td class="ps-4 text-secondary">#${b.id}</td>
            <td><span class="badge bg-light text-dark border">${b.busNumber || "N/A"}</span></td>
            <td>${b.busType || "Chưa rõ"}</td>
            <td><span class="badge bg-info-subtle text-info-emphasis px-3">${b.capacity || 0} ghế</span></td>
            <td class="text-center">
                <button class="btn btn-sm btn-outline-primary me-1" 
                        onclick="openEditModal(${b.id}, '${b.busNumber}', '${b.busType}', ${b.capacity})">
                    <i class="bi bi-pencil"></i>
                </button>
                <button class="btn btn-sm btn-outline-danger" onclick="deleteBus(${b.id})">
                    <i class="bi bi-trash"></i>
                </button>
            </td>
        </tr>`).join("");
}

// 3. Hàm vẽ nút phân trang (Đồng bộ logic với trang Trips)
function renderPagination(totalPages, current) {
    const pagination = document.getElementById('pagination');
    if (!pagination || totalPages <= 1) {
        pagination.innerHTML = "";
        return;
    }

    let html = "";
    // Nút Trước
    html += `
        <li class="page-item ${current === 0 ? 'disabled' : ''}">
            <a class="page-link" href="javascript:void(0)" onclick="loadBuses(${current - 1})">Trước</a>
        </li>`;

    // Các nút số
    for (let i = 0; i < totalPages; i++) {
        html += `
            <li class="page-item ${current === i ? 'active' : ''}">
                <a class="page-link" href="javascript:void(0)" onclick="loadBuses(${i})">${i + 1}</a>
            </li>`;
    }

    // Nút Sau
    html += `
        <li class="page-item ${current + 1 >= totalPages ? 'disabled' : ''}">
            <a class="page-link" href="javascript:void(0)" onclick="loadBuses(${current + 1})">Sau</a>
        </li>`;

    pagination.innerHTML = html;
}

// --- Các hàm Modal (Giữ nguyên logic của bạn nhưng tối ưu đóng modal) ---

function prepareAdd() {
    document.getElementById('modalTitle').innerText = "Thêm phương tiện mới";
    document.getElementById('edit-id').value = "";
    document.getElementById('inp-busNumber').value = "";
    document.getElementById('inp-type').value = "Ghế ngồi";
    document.getElementById('inp-capacity').value = "40";
}

function openEditModal(id, number, type, cap) {
    document.getElementById('modalTitle').innerText = "Cập nhật thông tin xe";
    document.getElementById('edit-id').value = id;
    document.getElementById('inp-busNumber').value = number; 
    document.getElementById('inp-type').value = type;
    document.getElementById('inp-capacity').value = cap;
    
    new bootstrap.Modal(document.getElementById('modalBus')).show();
}

async function saveBus() {
    const id = document.getElementById('edit-id').value;
    const data = {
        busNumber: document.getElementById("inp-busNumber").value,
        busType: document.getElementById("inp-type").value,
        capacity: parseInt(document.getElementById("inp-capacity").value)
    };

    if (!data.busNumber) return alert("Vui lòng nhập biển số xe!");

    const method = id ? 'PUT' : 'POST';
    const url = id ? `/api/buses/${id}` : '/api/buses';

    const res = await fetch(url, {
        method: method,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data)
    });

    if (res.ok) {
        const modalEl = document.getElementById('modalBus');
        bootstrap.Modal.getInstance(modalEl).hide();
        loadBuses(currentPage); // Tải lại trang hiện tại
    }
}

async function deleteBus(id) {
    if (confirm("Xác nhận xóa xe này?")) {
        const res = await fetch(`/api/buses/${id}`, { method: "DELETE" });
        if (res.ok) loadBuses(currentPage);
    }
}

document.addEventListener("DOMContentLoaded", () => loadBuses(0));