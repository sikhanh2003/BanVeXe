describe("BANVEXE - Login Test Suite", () => {
  const baseUrl = "http://localhost:8080";

  const user = {
    username: "user14",
    password: "user14@gmail.com",
  };

  // ===== Helper login =====
  const login = () => {
    cy.visit(baseUrl + "/login");

    cy.get('input[name="username"]').clear().type(user.username);
    cy.get('input[name="password"]').clear().type(user.password);

    cy.get('button[type="submit"]').click();
  };

  // =========================================
  // 1. LOGIN SUCCESS
  // =========================================
  it("Login thành công", () => {
    login();

    cy.url().should("include", "/");
    cy.contains("Chào, user14");
  });

  // =========================================
  // 2. LOGIN FAIL - SAI PASSWORD
  // =========================================
  it("Login sai mật khẩu", () => {
    cy.visit(baseUrl + "/login");

    cy.get('input[name="username"]').type(user.username);
    cy.get('input[name="password"]').type("sai123");

    cy.get('button[type="submit"]').click();

    cy.url().should("include", "/login");
    cy.contains("Tên đăng nhập hoặc mật khẩu không đúng!");
  });

  // =========================================
  // 3. LOGIN EMPTY
  // =========================================
  it("Không nhập dữ liệu", () => {
    cy.visit(baseUrl + "/login");

    cy.get('button[type="submit"]').click();

    // vẫn ở login
    cy.url().should("include", "/login");
  });

  // =========================================
  // 4. PROTECTED ROUTE (chưa login)
  // =========================================
  it("Chưa login không vào được history", () => {
    cy.visit(baseUrl + "/history");

    cy.url().should("include", "/login");
  });

  // =========================================
  // 5. LOGIN + HISTORY
  // =========================================
  it("Login và xem lịch sử vé", () => {
    login();

    cy.visit(baseUrl + "/history");

    cy.contains("Lịch sử đặt vé");

    // nếu có vé
    cy.get("body").then(($body) => {
      if ($body.text().includes("Bạn chưa có chuyến hành trình nào")) {
        cy.log("User chưa có vé");
      } else {
        cy.contains("đ"); // có hiển thị tiền
      }
    });
  });

  // =========================================
  // 6. LOGOUT
  // =========================================
  it("Logout", () => {
    login();

    cy.visit(baseUrl + "/");

    cy.get(".dropdown-toggle").click();

    // Đợi menu hiển thị rồi mới click
    cy.get(".dropdown-menu").should("be.visible");

    cy.contains("Đăng xuất").click();

    cy.url().should("include", "/");
  });
});
