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

    if (!userNode.value || !passNode.value) {
        msgElement.innerText = "Vui lòng điền đầy đủ thông tin!";
        return;
    }

    const data = {
        username: userNode.value,
        password: passNode.value
    };

    try {
        const response = await fetch(`${API_URL}/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });

        if (!response.ok) {
            msgElement.innerText = "Tài khoản hoặc mật khẩu không chính xác!";
            return;
        }

        const result = await response.json();

        if (result.message && result.message.toLowerCase().includes("thành công")) {
            localStorage.setItem("role", result.role);
            localStorage.setItem("username", result.username);

            if (result.role === "ADMIN") {
                window.location.href = "/admin/dashboard";
            } else {
                localStorage.setItem("currentUser", JSON.stringify(result.user));
                alert("Đăng nhập thành công!");
                window.location.href = "/";
            }
        } else {
            msgElement.innerText = result.message;
        }
    } catch (error) {
        console.error("Lỗi đăng nhập:", error);
        msgElement.innerText = "Có lỗi xảy ra khi đăng nhập!";
    }
}