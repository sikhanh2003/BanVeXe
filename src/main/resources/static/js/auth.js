const API_URL = "/api/auth";

async function handleRegister() {
    const username = document.getElementById('reg-username').value;
    const password = document.getElementById('reg-password').value;
    const msgElement = document.getElementById('reg-message');

    // Kiểm tra nhanh các trường bắt buộc
    if (!username || !password) {
        msgElement.innerText = "Vui lòng nhập tên đăng nhập và mật khẩu!";
        return;
    }

    const data = {
        username: username,
        password: password,
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
        
        if (response.ok && result.toLowerCase().includes("thành công")) {
            alert("Đăng ký thành công! Quay lại trang đăng nhập.");
            window.location.href = "/login";
        } else {
            msgElement.innerText = result; // Hiển thị lỗi từ server trả về
        }
    } catch (error) {
        console.error("Lỗi đăng ký:", error);
        msgElement.innerText = "Không thể kết nối đến máy chủ!";
    }
}

async function handleLogin() {
    const userNode = document.getElementById('login-username');
    const passNode = document.getElementById('login-password');
    const msgElement = document.getElementById("message");

    // Tạo form data chuẩn
    const params = new URLSearchParams();
    params.append('username', userNode.value);
    params.append('password', passNode.value);

    try {
        const response = await fetch('/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: params
        });

        // Nếu đăng nhập đúng, Spring sẽ Redirect (từ SuccessHandler)
        if (response.redirected) {
            window.location.href = response.url;
        } else {
            msgElement.innerText = "Sai tài khoản hoặc mật khẩu!";
        }
    } catch (error) {
        msgElement.innerText = "Lỗi kết nối máy chủ!";
    }
}