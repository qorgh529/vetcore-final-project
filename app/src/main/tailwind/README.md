# Stylesheet build

`src/main/resources/static/resources/css/vetcore.css` is generated from this folder and committed,
so the application build does not need Node.js.

Rebuild after changing templates or `input.css` (run in `app/`):

```
npx --yes tailwindcss@3.4.17 -c src/main/tailwind/tailwind.config.js \
  -i src/main/tailwind/input.css -o src/main/resources/static/resources/css/vetcore.css --minify
```

Tailwind only emits the classes it finds in `src/main/resources/templates`, so a class that is
new to the templates has no effect until the stylesheet is rebuilt.
