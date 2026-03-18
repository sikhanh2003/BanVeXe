describe('Kiểm tra chức năng Đăng ký', () => {
  
  beforeEach(() => {
    // Truy cập trang đăng ký
    cy.visit('http://localhost:8080/register'); 
  });

  it('Kiểm tra giao diện trang đăng ký', () => {
    cy.get('h2').should('contain', 'Đăng ký');
    cy.get('#reg-username').should('be.visible');
    cy.get('#reg-email').should('be.visible');
    cy.get('button').should('contain', 'Tạo tài khoản');
  });

  it('Hiển thị lỗi khi để trống tất cả các trường', () => {
    cy.get('button').click();
    // Kiểm tra thông báo lỗi hiển thị ở thẻ #reg-message
    cy.get('#reg-message').should('be.visible').and('not.be.empty');
  });

  it('Kiểm tra nhập liệu các trường thông tin', () => {
    const randomUser = `user_${Math.floor(Math.random() * 1000)}`;
    
    cy.get('#reg-username').type(randomUser);
    cy.get('#reg-password').type('Pass123!');
    cy.get('#reg-fullname').type('Nguyen Van A');
    cy.get('#reg-email').type(`${randomUser}@gmail.com`);
    cy.get('#reg-phone').type('0901234567');
    
    cy.get('button').click();

    // Tùy vào logic auth.js, nếu thành công thường sẽ redirect sang login
    // hoặc hiển thị thông báo thành công
    cy.url().should('include', '/login');
  });

  it('Kiểm tra định dạng email không hợp lệ', () => {
    cy.get('#reg-email').type('email_sai_dinh_dang');
    cy.get('button').click();
    
    // Nếu bạn có validation phía client, thông báo lỗi sẽ hiện ra
    cy.get('#reg-message').should('be.visible');
  });
});