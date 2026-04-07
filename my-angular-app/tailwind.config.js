/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{html,ts}"
  ],
  theme: {
    extend: {
      colors: {
        primary: "#A2D45E",
        "portal-cyan": "#00e3fd",
        "void-purple": "#190033",
      },
      fontFamily: {
        headline: ["Space Grotesk", "sans-serif"],
        body: ["Public Sans", "sans-serif"],
        label: ["Space Grotesk", "sans-serif"],
      },
    },
  },
  plugins: [],
};