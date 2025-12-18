# Portfolio JS Example

A standalone JavaScript portfolio site built with Summon, Materia, and Sigil. This example demonstrates how to create a static single-page application that can be deployed to GitHub Pages or any static hosting.

## Features

- 🎨 **Aurora Background Effect** - WebGPU/WebGL animated shader background using Sigil
- 📱 **Responsive Design** - Mobile-first layout with fluid typography
- ⚡ **Client-Side Rendering** - Pure JavaScript, no server required
- 🎯 **Compose-Like API** - Familiar declarative UI patterns from Jetpack Compose
- 🌙 **Dark Theme** - Modern dark color scheme with accent colors

## Quick Start

```bash
# From the summon root directory:

# Run development server
./gradlew :examples:portfolio-js:jsBrowserDevelopmentRun

# Build for production
./gradlew :examples:portfolio-js:jsBrowserProductionWebpack
```

The app will be available at `http://localhost:8082`

## Deploying to GitHub Pages

1. Build the production bundle:
   ```bash
   ./gradlew :examples:portfolio-js:jsBrowserProductionWebpack
   ```

2. The output files will be in:
   ```
   examples/portfolio-js/build/dist/js/productionExecutable/
   ```

3. Copy the contents to your GitHub Pages repository or use GitHub Actions:
   ```yaml
   - name: Build
     run: ./gradlew :examples:portfolio-js:jsBrowserProductionWebpack
   
   - name: Deploy
     uses: peaceiris/actions-gh-pages@v3
     with:
       github_token: ${{ secrets.GITHUB_TOKEN }}
       publish_dir: ./examples/portfolio-js/build/dist/js/productionExecutable
   ```

## Project Structure

```
portfolio-js/
├── build.gradle.kts              # Build configuration
├── settings.gradle.kts           # Project settings
└── src/jsMain/
    ├── kotlin/portfolio/
    │   ├── Main.kt               # App entry point
    │   ├── data/
    │   │   └── PortfolioContent.kt   # Static content data
    │   ├── theme/
    │   │   └── PortfolioTheme.kt     # Colors, spacing, typography
    │   └── ui/
    │       ├── components/
    │       │   ├── Header.kt         # Navigation header
    │       │   ├── ProjectCard.kt    # Project display card
    │       │   └── SkillCard.kt      # Skill display card
    │       ├── effects/
    │       │   └── AuroraBackground.kt   # Sigil shader effect
    │       └── sections/
    │           ├── HeroSection.kt    # Landing hero
    │           ├── AboutSection.kt   # About/skills
    │           ├── ProjectsSection.kt    # Project grid
    │           ├── ExperienceSection.kt  # Work history
    │           ├── ContactSection.kt     # Contact info
    │           └── Footer.kt         # Page footer
    └── resources/
        └── index.html            # HTML entry point
```

## Customization

### Content

Edit `src/jsMain/kotlin/portfolio/data/PortfolioContent.kt` to update:
- Profile information (name, title, social links)
- Skills and technologies
- Projects
- Work experience

### Theme

Customize colors, spacing, and typography in `src/jsMain/kotlin/portfolio/theme/PortfolioTheme.kt`.

### Aurora Effect

Modify the shader in `src/jsMain/kotlin/portfolio/ui/effects/AuroraBackground.kt` to change:
- Color palette
- Animation speed
- Wave patterns
- Mouse interaction

## Technologies

- **[Summon](https://github.com/codeyousef/summon)** - Kotlin Multiplatform UI framework
- **[Materia](https://github.com/codeyousef/materia)** - WebGL/WebGPU rendering
- **[Sigil](https://github.com/codeyousef/sigil)** - Shader effect library

## Browser Support

- Chrome/Edge 113+ (WebGPU)
- Firefox 110+ (WebGL fallback)
- Safari 17+ (WebGPU with flag, WebGL fallback)

## License

MIT
