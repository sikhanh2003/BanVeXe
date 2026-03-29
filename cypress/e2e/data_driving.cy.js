describe('Login Data Driven Test', () => {

  const baseUrl = 'http://localhost:8080/login'

  const testData = [
    { username: 'user14', password: 'user14@gmail.com', success: true },
    { username: 'user14', password: 'sai123', success: false },
    { username: 'saiuser', password: 'user14@gmail.com', success: false },
    { username: '', password: 'user14@gmail.com', success: false },
    { username: 'user14', password: '', success: false },
    { username: '', password: '', success: false },
    { username: 'user14', password: ' USER14@GMAIL.COM ', success: false },
    { username: 'test123', password: '123456', success: false },
  ]

  testData.forEach((data, index) => {
    it(`Test login case #${index + 1}`, () => {

      cy.visit(baseUrl)

      if (data.username !== '') {
        cy.get('[name="username"]').type(data.username)
      }

      if (data.password !== '') {
        cy.get('[name="password"]').type(data.password)
      }

      cy.get('button[type="submit"]').click()

      if (data.success) {
        cy.url().should('include', '/')
        cy.contains('Chào')
      } else {
        cy.url().should('include', '/login')
      }

    })
  })

})