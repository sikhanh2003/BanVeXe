describe("Kiểm tra chức năng Đăng nhập", () => {
  const baseUrl = "http://localhost:8080";

  Cypress.on("uncaught:exception", (err, runnable) => {
    // Trả về false để Cypress không làm hỏng bài test khi gặp lỗi JS của ứng dụng
    return false;
  });

  beforeEach(() => {
    // Truy cập trang login trước mỗi bài test
    cy.visit(`${baseUrl}/login`);
  });

  it("Nên đăng nhập thành công với quyền USER và chuyển hướng về trang chủ", () => {
    // 1. Nhập liệu dựa trên ID trong code HTML của bạn
    cy.get("#login-username").type("user8@gmail");
    cy.get("#login-password").type("user8@gmail");

    // 2. Click nút đăng nhập (Dùng đúng selector để tránh click nhầm)
    cy.get("button").contains("Đăng nhập").click();
    cy.url({ timeout: 10000 }).should("eq", `http://localhost:8080/`);

    // 3. Kiểm tra xem localStorage có lưu dữ liệu không (như trong handleLogin xử lý)
    // Lưu ý: Cypress đôi khi chạy nhanh hơn login, nên ta dùng should() để chờ
    cy.window().then((win) => {
      cy.url().should("eq", `${baseUrl}/`);
      // Đợi cho đến khi localStorage có dữ liệu
      cy.wrap(win.localStorage).its("username").should("exist");
    });

    // 4. Kiểm tra giao diện trang chủ đã hiển thị chưa
    cy.contains("Tuyến đường phổ biến", { timeout: 10000 }).should(
      "be.visible",
    );
  });

  it("Nên hiển thị lỗi khi bỏ trống thông tin", () => {
    cy.get("button").contains("Đăng nhập").click();

    // Kiểm tra thông báo lỗi hiển thị trong thẻ <p id="message">
    cy.get("#message").should("have.text", "Vui lòng điền đầy đủ thông tin!");
  });

  it("Nên hiển thị lỗi khi sai tài khoản hoặc mật khẩu", () => {
    // Giả lập API trả về lỗi hoặc nhập sai
    cy.get("#login-username").type("wrong_user");
    cy.get("#login-password").type("wrong_pass");
    cy.get("button").contains("Đăng nhập").click();

    // Đợi phản hồi từ server và check thông báo
    cy.get("#message").should("be.visible");
    // Vì trong code js bạn dùng: msgElement.innerText = "Tài khoản hoặc mật khẩu không chính xác!"
    cy.get("#message").contains("Sai tài khoản hoặc mật khẩu");
  });

  it("Nên chuyển hướng đến Dashboard nếu là ADMIN", () => {
    // Trường hợp này cần tài khoản admin thật hoặc dùng cy.intercept để giả lập
    cy.get("#login-username").type("Phanthuky12@gmail.com");
    cy.get("#login-password").type("Phanthuky12@gmail.com");
    cy.get("button").contains("Đăng nhập").click();

    // Kiểm tra URL dành cho admin
    cy.url().should("include", "/admin/dashboard");
  });
});
