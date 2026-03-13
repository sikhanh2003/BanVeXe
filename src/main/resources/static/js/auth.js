const API_URL = "/api/auth";

// Xử lý Đăng ký
async function handleRegister() {
    const data = {
        username: document.getElementById('reg-username').value,
        password: document.getElementById('reg-password').value,
        fullName: document.getElementById('reg-fullname').value,
        email: document.getElementById('reg-email').value,
        phone: document.getElementById('reg-phone').value
    };

    try {
        const response = await fetch(`${API_URL}/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });

        const result = await response.text();
        alert(result);
        if (result.includes("thành công")) {
            window.location.href = "login.html"; // Đăng ký xong tự qua trang login
        }
    } catch (error) {
        console.error("Lỗi đăng ký:", error);
    }
}

// Xử lý Đăng nhập
async function handleLogin() {
    const data = {
        username: document.getElementById('login-username').value,
        password: document.getElementById('login-password').value
    };

    try {
        const response = await fetch(`${API_URL}/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });

        const result = await response.text();
        const msgBox = document.getElementById('message');
        
        if (result.includes("thành công")) {
            alert(result);
            window.location.href = "/home.html"; // Đăng nhập xong vào trang chủ
        } else {
            msgBox.innerText = result;
        }
    } catch (error) {
        console.error("Lỗi đăng nhập:", error);
    }
}