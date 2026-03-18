let allBuses = [];
let currentPage = 1;
const rowsPerPage = 10; // Số hàng mỗi trang

// 1. Tải danh sách xe từ API
async function loadBuses() {
    try {
        const res = await fetch("/api/buses");
        allBuses = await res.json();
        renderTable(currentPage); // Gọi hàm hiển thị có phân trang
    } catch (e) {
        console.error("Lỗi tải dữ liệu:", e);
        document.getElementById("list-buses").innerHTML = '<tr><td colspan="5" class="text-center text-danger p-4">Không thể kết nối máy chủ</td></tr>';
    }
}

// 2. Hàm hiển thị bảng kèm Phân trang
function renderTable(page) {
    const listBody = document.getElementById('list-buses');
    const pagination = document.getElementById('pagination');
    if (!listBody || !pagination) return;

    listBody.innerHTML = "";
    
    // Tính toán dữ liệu cho trang hiện tại
    const start = (page - 1) * rowsPerPage;
    const end = start + rowsPerPage;
    const items = allBuses.slice(start, end);

    if (items.length === 0) {
        listBody.innerHTML = '<tr><td colspan="5" class="text-center p-4">Chưa có dữ liệu xe</td></tr>';
    } else {
        items.forEach(b => {
            listBody.innerHTML += `
                <tr>
                    <td class="ps-4 text-secondary">#${b.id}</td>
                    <td><span class="badge bg-light text-dark border">${b.busNumber || "N/A"}</span></td>
                    <td>${b.busType || "Chưa rõ"}</td>
                    <td>${b.capacity || 0} ghế</td>
                    <td class="text-center">
                        <button class="btn btn-sm btn-outline-primary me-1" 
                                onclick="openEditModal(${b.id}, '${b.busNumber}', '${b.busType}', ${b.capacity})">
                            <i class="bi bi-pencil"></i> Sửa
                        </button>
                        <button class="btn btn-sm btn-outline-danger" onclick="deleteBus(${b.id})">
                            <i class="bi bi-trash"></i> Xóa
                        </button>
                    </td>
                </tr>`;
        });
    }

    // Vẽ các nút số trang
    renderPagination();
}

// 3. Hàm vẽ nút phân trang
function renderPagination() {
    const pagination = document.getElementById('pagination');
    pagination.innerHTML = "";
    const pageCount = Math.ceil(allBuses.length / rowsPerPage);

    if (pageCount <= 1) return; // Không hiện phân trang nếu chỉ có 1 trang

    for (let i = 1; i <= pageCount; i++) {
        const li = document.createElement('li');
        li.className = `page-item ${currentPage === i ? 'active' : ''}`;
        li.innerHTML = `<a class="page-link" href="javascript:void(0)">${i}</a>`;
        li.onclick = (e) => {
            currentPage = i;
            renderTable(i);
        };
        pagination.appendChild(li);
    }
}

// 4. Modal: Chuẩn bị Thêm mới
function prepareAdd() {
    document.getElementById('modalTitle').innerText = "Thêm phương tiện mới";
    document.getElementById('edit-id').value = "";
    document.getElementById('inp-busNumber').value = "";
    document.getElementById('inp-type').value = "Ghế ngồi";
    document.getElementById('inp-capacity').value = "40";
}

// 5. Modal: Mở để Sửa
function openEditModal(id, number, type, cap) {
    document.getElementById('modalTitle').innerText = "Cập nhật thông tin xe";
    document.getElementById('edit-id').value = id;
    document.getElementById('inp-busNumber').value = number; 
    document.getElementById('inp-type').value = type;
    document.getElementById('inp-capacity').value = cap;
    
    const myModal = new bootstrap.Modal(document.getElementById('modalBus'));
    myModal.show();
}

// 6. Lưu thông tin (Thêm/Sửa)
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

    try {
        const res = await fetch(url, {
            method: method,
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(data)
        });

        if (res.ok) {
            // Đóng modal bằng cách tìm instance hiện tại
            const modalElement = document.getElementById('modalBus');
            const modalInstance = bootstrap.Modal.getInstance(modalElement) || new bootstrap.Modal(modalElement);
            modalInstance.hide();
            loadBuses();
        } else {
            alert("Lỗi khi lưu thông tin!");
        }
    } catch (e) {
        alert("Lỗi kết nối API!");
    }
}

// 7. Xóa xe
async function deleteBus(id) {
    if (confirm("Bạn có chắc chắn muốn xóa xe này?")) {
        try {
            const res = await fetch(`/api/buses/${id}`, { method: "DELETE" });
            if (res.ok) loadBuses();
        } catch (e) {
            alert("Lỗi khi xóa!");
        }
    }
}

// Khởi chạy
document.addEventListener("DOMContentLoaded", loadBuses);