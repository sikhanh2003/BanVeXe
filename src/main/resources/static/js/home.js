document.addEventListener("DOMContentLoaded", function () {
  updateUserHeader();
  fetchPopularRoutes();
});

// Hiển thị tên người dùng sau khi đăng nhập
function updateUserHeader() {
  const username = localStorage.getItem("username");
  // Giả sử bạn có một thẻ <span id="username-display"></span> trong HTML
  const userElement = document.getElementById("username-display"); 

  if (userElement && username) { 
    userElement.innerHTML = username;
  }
}

function logout() {
  localStorage.removeItem("username");
  location.reload();
}

// Lấy danh sách tuyến đường từ API
async function fetchPopularRoutes() {
  try {
    const response = await fetch("/api/routes/popular");
    const data = await response.json();
    const container = document.getElementById("route-container");

    container.innerHTML = ""; // Xóa dữ liệu cũ

    data.forEach((route) => {
      container.innerHTML += `
                <div class="card" style="border: 1px solid #ddd; border-radius: 8px; width: 250px; overflow: hidden; cursor: pointer;" onclick="fillSearch('${route.departureLocation}', '${route.arrivalLocation}')">
                    <img src="https://images.unsplash.com/photo-1544620347-c4fd4a3d5957" style="width: 100%; height: 150px; object-fit: cover;">
                    <div style="padding: 15px;">
                        <p style="margin: 0; font-weight: bold;">${route.departureLocation} - ${route.arrivalLocation}</p>
                        <p style="margin: 5px 0 0; color: #666; font-size: 14px;">Khoảng cách: ${route.distanceKm} km</p>
                    </div>
                </div>
            `;
    });
  } catch (error) {
    console.error("Lỗi khi tải tuyến đường:", error);
  }
}

function fillSearch(dep, arr) {
  document.getElementById("departureInput").value = dep;
  document.getElementById("arrivalInput").value = arr;
  window.scrollTo({ top: 0, behavior: "smooth" });
}
