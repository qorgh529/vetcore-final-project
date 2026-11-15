/**
 * Design tokens from the "Clinical Warmth" design system (Stitch export).
 * Build: see src/main/tailwind/README.md
 */
const font = ['"Plus Jakarta Sans"', 'Pretendard', '"Apple SD Gothic Neo"', '"Malgun Gothic"', '"Noto Sans KR"', 'system-ui', 'sans-serif'];
module.exports = {
  content: ['./src/main/resources/templates/**/*.html'],
  theme: {
    extend: {
      colors: {
        'error': '#ba1a1a', 'on-error': '#ffffff', 'error-container': '#ffdad6', 'on-error-container': '#93000a',
        'primary': '#00685f', 'on-primary': '#ffffff', 'primary-container': '#008378', 'on-primary-container': '#f4fffc',
        'primary-fixed': '#89f5e7', 'primary-fixed-dim': '#6bd8cb',
        'secondary': '#006b5f', 'on-secondary': '#ffffff', 'secondary-container': '#6df5e1', 'on-secondary-container': '#006f64',
        'secondary-fixed': '#71f8e4',
        'tertiary': '#a33900', 'on-tertiary': '#ffffff', 'tertiary-container': '#cc4900', 'tertiary-fixed': '#ffdbce', 'on-tertiary-fixed': '#370e00',
        'surface': '#faf8ff', 'surface-container-lowest': '#ffffff', 'surface-container-low': '#f2f3ff',
        'surface-container': '#eaedff', 'surface-container-high': '#e2e7ff', 'surface-container-highest': '#dae2fd',
        'on-surface': '#131b2e', 'on-surface-variant': '#3d4947', 'inverse-surface': '#283044', 'inverse-on-surface': '#eef0ff',
        'outline': '#6d7a77', 'outline-variant': '#bcc9c6',
      },
      borderRadius: { DEFAULT: '0.25rem', lg: '0.5rem', xl: '0.75rem', '2xl': '1rem', full: '9999px' },
      fontFamily: { sans: font },
      fontSize: {
        'label-sm': ['11px', { lineHeight: '16px', letterSpacing: '0.04em', fontWeight: '700' }],
        'label-md': ['13px', { lineHeight: '18px', letterSpacing: '0.01em', fontWeight: '600' }],
        'body-md': ['14px', { lineHeight: '20px', fontWeight: '400' }],
        'body-lg': ['16px', { lineHeight: '24px', fontWeight: '400' }],
        'title-md': ['18px', { lineHeight: '26px', fontWeight: '600' }],
        'headline-sm': ['20px', { lineHeight: '28px', fontWeight: '600' }],
        'headline-md': ['24px', { lineHeight: '32px', letterSpacing: '-0.01em', fontWeight: '600' }],
        'headline-lg': ['32px', { lineHeight: '40px', letterSpacing: '-0.015em', fontWeight: '700' }],
        'display-lg': ['48px', { lineHeight: '56px', letterSpacing: '-0.02em', fontWeight: '700' }],
      },
    },
  },
};
