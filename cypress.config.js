const { defineConfig } = require('cypress')

module.exports = defineConfig({
  projectId: "5g3fgo", // 👈 dán cái bạn lấy từ Cypress Cloud

  e2e: {
    baseUrl: "http://localhost:8080",
    video: true
  }
})