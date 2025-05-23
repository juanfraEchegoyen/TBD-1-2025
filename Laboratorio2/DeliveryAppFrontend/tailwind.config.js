/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./components/**/*.{js,vue,ts}",
    "./layouts/**/*.vue",
    "./pages/**/*.vue",
    "./plugins/**/*.{js,ts}",
    "./app.vue",
    "./error.vue",
  ],
  theme: {
    extend: {
      colors: {
        primary: '#c83e4d',
        secondary: '#f5efed',
        tertiary: '#0f0a0a' 
        
      },
      boxShadow: {
        'hard':  '0 4px 9px 6px rgba(57, 64, 73, 0.2)',
        'soft-r': '4px 2px 21px 0px rgba(57, 64, 73, 0.1);'
      }
    },
  },
  plugins: [],
}

