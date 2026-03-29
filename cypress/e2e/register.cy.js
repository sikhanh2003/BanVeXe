describe('BANVEXE - Register Test', () => {

  const baseUrl = 'http://localhost:8080'

  // ==============================
  // 1. REGISTER SUCCESS
  // ==============================
  it('Đăng ký thành công', () => {
    cy.visit(baseUrl + '/register')

    const random = Date.now()

    cy.get('[name="fullName"]').type('Nguyen Van A')
    cy.get('[name="username"]').type('user' + random)
    cy.get('[name="email"]').type('user' + random + '@gmail.com')
    cy.get('[name="phone"]').type('0901234567')
    cy.get('[name="password"]').type('123456')

    cy.get('button[type="submit"]').click()

    cy.url().should('not.include', '/register')
  })


  // ==============================
  // 2. USERNAME ĐÃ TỒN TẠI
  // ==============================
  it('Username đã tồn tại', () => {
    cy.visit(baseUrl + '/register')

    cy.get('[name="fullName"]').type('Test User')
    cy.get('[name="username"]').type('user14')
    cy.get('[name="email"]').type('test@gmail.com')
    cy.get('[name="phone"]').type('0901234567')
    cy.get('[name="password"]').type('123456')

    cy.get('button[type="submit"]').click()

    cy.contains('đã tồn tại')
  })


  // ==============================
  // 3. BỎ TRỐNG FORM
  // ==============================
  it('Không nhập dữ liệu', () => {
    cy.visit(baseUrl + '/register')

    cy.get('button[type="submit"]').click()

    cy.url().should('include', '/register')
  })


  // ==============================
  // 4. EMAIL SAI ĐỊNH DẠNG
  // ==============================
  it('Email không hợp lệ', () => {
    cy.visit(baseUrl + '/register')

    cy.get('[name="fullName"]').type('Test User')
    cy.get('[name="username"]').type('user999')
    cy.get('[name="email"]').type('abc') // sai
    cy.get('[name="phone"]').type('0901234567')
    cy.get('[name="password"]').type('123456')

    cy.get('button[type="submit"]').click()

    cy.url().should('include', '/register')
  })

})