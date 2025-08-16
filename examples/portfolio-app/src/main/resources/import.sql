-- Initial data for development

-- Admin user (username: admin, password: admin123)
INSERT INTO users (id, username, password_hash, email, full_name, role, status, created_at, updated_at)
VALUES ('1', 'admin', '$2a$10$YTQSTEigXC3DBL8eODzr.evF5tRadJdZRk3527IAwjaSbvstPQwoq', 'admin@example.com', 'Administrator', 'ADMIN', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Regular user (username: user, password: user123)
INSERT INTO users (id, username, password_hash, email, full_name, role, status, created_at, updated_at)
VALUES ('2', 'user', '$2a$10$shY2YsTfs2BoTkWzmDiBh.mufTaL1jsirp/XLw485EY9pz/3Q6wNO', 'user@example.com', 'Regular User', 'USER', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Project categories
INSERT INTO categories (id, name, slug, description, type, color, sort, status, created_at, updated_at)
VALUES ('1', 'Web Development', 'web-development', 'Web applications and websites', 'PROJECT', '#3498db', 1, 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO categories (id, name, slug, description, type, color, sort, status, created_at, updated_at)
VALUES ('2', 'UI/UX Design', 'ui-ux-design', 'User interface and experience design', 'PROJECT', '#9b59b6', 2, 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO categories (id, name, slug, description, type, color, sort, status, created_at, updated_at)
VALUES ('3', 'Mobile Development', 'mobile-development', 'Mobile applications for iOS and Android', 'PROJECT', '#2ecc71', 3, 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Blog categories
INSERT INTO categories (id, name, slug, description, type, color, sort, status, created_at, updated_at)
VALUES ('4', 'Tutorials', 'tutorials', 'Step-by-step guides and tutorials', 'BLOG', '#e74c3c', 1, 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO categories (id, name, slug, description, type, color, sort, status, created_at, updated_at)
VALUES ('5', 'Tech News', 'tech-news', 'Latest news and updates in technology', 'BLOG', '#f39c12', 2, 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO categories (id, name, slug, description, type, color, sort, status, created_at, updated_at)
VALUES ('6', 'Thoughts & Opinions', 'thoughts-opinions', 'Personal thoughts and opinions on various topics', 'BLOG', '#1abc9c', 3, 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Portfolio files
INSERT INTO portfolio_files (id, title, filename, width, height, url, mime_type, status, created_at, updated_at)
VALUES ('1', 'Summon Framework Logo', 'summon-logo.png', 512, 512, '/images/summon-logo.png', 'image/png', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO portfolio_files (id, title, filename, width, height, url, mime_type, status, created_at, updated_at)
VALUES ('2', 'Liquid Glass Portal', 'aurora-portal.jpg', 1920, 1080, '/images/aurora-portal.jpg', 'image/jpeg', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO portfolio_files (id, title, filename, width, height, url, mime_type, status, created_at, updated_at)
VALUES ('3', 'Glass Shard Design', 'glass-shard.png', 800, 600, '/images/glass-shard.png', 'image/png', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Projects
INSERT INTO projects (id, title, slug, description, long_description_html, main_image_id, category_legacy, project_year, live_url, repo_url, sort, status, created_at, updated_at)
VALUES ('1', 'Summon UI Framework', 'summon-ui-framework', 'A Kotlin Multiplatform declarative UI framework bringing Jetpack Compose-style development to browser and JVM environments.', '<p>Summon is a comprehensive UI framework that enables developers to build sophisticated web applications using pure Kotlin.</p><p>Key features include:</p><ul><li>Type-safe CSS styling with modifier system</li><li>Component-based architecture</li><li>Reactive state management</li><li>Server-side rendering capabilities</li><li>Framework integrations (Quarkus, Ktor, Spring Boot)</li></ul>', '1', 'Open Source', '2024', 'https://github.com/codeyousef/summon', 'https://github.com/codeyousef/summon', 1, 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO projects (id, title, slug, description, long_description_html, main_image_id, category_legacy, project_year, live_url, repo_url, sort, status, created_at, updated_at)
VALUES ('2', 'Liquid Glass Aurora Portal', 'liquid-glass-aurora-portal', 'A sophisticated portfolio website implementing the ''Liquid Glass Aurora Portal'' design system with floating crystalline UI elements.', '<p>An advanced portfolio design that abandons traditional flat interfaces for a dimensional experience where UI elements exist as floating crystalline shards within an aurora energy field.</p><p>Design innovations include:</p><ul><li>Asymmetric glass fragments with beveled edges</li><li>Chromatic aberration and light refraction effects</li><li>Gravitational UI with orbital positioning</li><li>Aurora breathing and portal intensity variations</li><li>Prismatic navigation with faceted geometry</li></ul>', '2', 'Portfolio Design', '2024', '/portfolio', 'https://github.com/codeyousef/aethelframe-portfolio', 2, 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Project categories
INSERT INTO project_categories (project_id, category_id) VALUES ('1', '1');
INSERT INTO project_categories (project_id, category_id) VALUES ('2', '2');

-- Project tech stack
INSERT INTO project_tech_stack (project_id, tech) VALUES ('1', 'Kotlin');
INSERT INTO project_tech_stack (project_id, tech) VALUES ('1', 'Kotlin/JS');
INSERT INTO project_tech_stack (project_id, tech) VALUES ('1', 'Gradle');
INSERT INTO project_tech_stack (project_id, tech) VALUES ('1', 'CSS');

INSERT INTO project_tech_stack (project_id, tech) VALUES ('2', 'React');
INSERT INTO project_tech_stack (project_id, tech) VALUES ('2', 'TypeScript');
INSERT INTO project_tech_stack (project_id, tech) VALUES ('2', 'Three.js');
INSERT INTO project_tech_stack (project_id, tech) VALUES ('2', 'Django');
INSERT INTO project_tech_stack (project_id, tech) VALUES ('2', 'CSS3');

-- Project tags
INSERT INTO project_tags (project_id, tag) VALUES ('1', 'UI Framework');
INSERT INTO project_tags (project_id, tag) VALUES ('1', 'Kotlin Multiplatform');
INSERT INTO project_tags (project_id, tag) VALUES ('1', 'Web Development');

INSERT INTO project_tags (project_id, tag) VALUES ('2', 'Portfolio');
INSERT INTO project_tags (project_id, tag) VALUES ('2', '3D Design');
INSERT INTO project_tags (project_id, tag) VALUES ('2', 'Aurora Effects');
INSERT INTO project_tags (project_id, tag) VALUES ('2', 'Glass Morphism');

-- Project gallery images
INSERT INTO project_gallery_images (project_id, file_id) VALUES ('1', '1');
INSERT INTO project_gallery_images (project_id, file_id) VALUES ('1', '2');
INSERT INTO project_gallery_images (project_id, file_id) VALUES ('2', '2');
INSERT INTO project_gallery_images (project_id, file_id) VALUES ('2', '3');

-- Journal entries (blog posts)
INSERT INTO journal_entries (id, title, slug, excerpt, content_rich_text, language, publication_date, featured_image_id, sort, status, created_at, updated_at)
VALUES ('1', 'Building the Aurora Portal Design System', 'building-aurora-portal-design-system', 'Exploring the creation of a dimensional UI system that abandons flat glass surfaces for prismatic structures that capture and split portal light.', '<p>The journey of creating the Aurora Portal design system began with a simple question: What if UI elements existed as floating crystalline shards within an energy field instead of traditional flat interfaces?</p><h2>Design Philosophy</h2><p>The Aurora Portal system is built on several core principles:</p><ul><li><strong>Dimensional Interaction:</strong> Moving beyond 2D layouts to true 3D spatial relationships</li><li><strong>Gravitational Physics:</strong> UI elements drift toward the central portal when idle</li><li><strong>Light Refraction:</strong> Portal energy splits into brand colors (teal, maroon, navy)</li><li><strong>Asymmetric Beauty:</strong> Glass fragments with irregular, beveled geometries</li></ul><h2>Technical Implementation</h2><p>Implementing these effects required pushing CSS and WebGL to their limits...</p>', 'ENGLISH', CURRENT_TIMESTAMP - INTERVAL '7 days', '2', 1, 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO journal_entries (id, title, slug, excerpt, content_rich_text, language, publication_date, featured_image_id, sort, status, created_at, updated_at)
VALUES ('2', 'Kotlin Multiplatform UI: The Future of Cross-Platform Development', 'kotlin-multiplatform-ui-future', 'Exploring how Kotlin Multiplatform is revolutionizing UI development by enabling shared business logic while maintaining platform-specific UI excellence.', '<p>Kotlin Multiplatform (KMP) represents a paradigm shift in how we approach cross-platform development. Unlike other solutions that compromise on native capabilities, KMP allows developers to share business logic while maintaining platform-specific UI excellence.</p><h2>The Summon Approach</h2><p>Summon takes this philosophy and applies it to web development, providing:</p><ul><li>Type-safe CSS styling with compile-time validation</li><li>Component reusability across JVM and JS platforms</li><li>Server-side rendering without sacrificing interactivity</li><li>Framework-agnostic integration patterns</li></ul><p>This approach enables teams to build sophisticated web applications while maintaining code quality and developer productivity.</p>', 'ENGLISH', CURRENT_TIMESTAMP - INTERVAL '14 days', '1', 2, 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Journal entry categories
INSERT INTO journal_entry_categories (journal_entry_id, category_id) VALUES ('1', '4');
INSERT INTO journal_entry_categories (journal_entry_id, category_id) VALUES ('2', '5');

-- Journal entry tags
INSERT INTO journal_entry_tags (journal_entry_id, tag) VALUES ('1', 'Design System');
INSERT INTO journal_entry_tags (journal_entry_id, tag) VALUES ('1', 'UI/UX');
INSERT INTO journal_entry_tags (journal_entry_id, tag) VALUES ('1', 'CSS');
INSERT INTO journal_entry_tags (journal_entry_id, tag) VALUES ('1', 'WebGL');

INSERT INTO journal_entry_tags (journal_entry_id, tag) VALUES ('2', 'Kotlin');
INSERT INTO journal_entry_tags (journal_entry_id, tag) VALUES ('2', 'Multiplatform');
INSERT INTO journal_entry_tags (journal_entry_id, tag) VALUES ('2', 'Web Development');
INSERT INTO journal_entry_tags (journal_entry_id, tag) VALUES ('2', 'Architecture');

-- Services
INSERT INTO services (id, title, slug, description_rich_text, icon_svg, sort, status, created_at, updated_at)
VALUES ('1', 'UI Framework Development', 'ui-framework-development', '<p>Custom UI framework development and implementation for modern web applications.</p><ul><li>Component system architecture</li><li>Type-safe styling solutions</li><li>Framework integrations</li><li>Performance optimization</li></ul>', '<svg viewBox="0 0 24 24" fill="currentColor"><path d="M12 2L2 7V10C2 16 6 21.5 12 22C18 21.5 22 16 22 10V7L12 2Z"/></svg>', 1, 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO services (id, title, slug, description_rich_text, icon_svg, sort, status, created_at, updated_at)
VALUES ('2', 'Advanced Design Systems', 'advanced-design-systems', '<p>Cutting-edge design systems that push the boundaries of web interfaces.</p><ul><li>Dimensional UI design</li><li>Aurora portal effects</li><li>Glass morphism implementation</li><li>Interactive 3D components</li></ul>', '<svg viewBox="0 0 24 24" fill="currentColor"><path d="M9 11H7v3h2v-3zm4 0h-2v3h2v-3zm4 0h-2v3h2v-3zm2-7h-1V2h-2v2H8V2H6v2H5c-1.1 0-1.99.9-1.99 2L3 20c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 16H5V9h14v11z"/></svg>', 2, 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO services (id, title, slug, description_rich_text, icon_svg, sort, status, created_at, updated_at)
VALUES ('3', 'Kotlin Multiplatform Consulting', 'kotlin-multiplatform-consulting', '<p>Expert consulting on Kotlin Multiplatform architecture and implementation strategies.</p><ul><li>Architecture planning</li><li>Code sharing strategies</li><li>Platform integration</li><li>Team training and mentorship</li></ul>', '<svg viewBox="0 0 24 24" fill="currentColor"><path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/></svg>', 3, 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);