export default defineNuxtConfig({
  ssr: false,
  nitro: {
    preset: 'static' // Cambia a 'static' para servir archivos estáticos
  },
  css: ['~/assets/css/main.css'],
  runtimeConfig: {
    public: {
      apiBase: process.env.API_BASE_URL || "http://localhost:3000",
    } as {
      apiBase: string;
    }
  },
  compatibilityDate: '2024-11-01',
  devtools: { enabled: true },
  postcss: {
    plugins: {
      tailwindcss: {},
      autoprefixer: {},
    },
  },
  modules: ['@nuxtjs/tailwindcss', '@pinia/nuxt']
});
